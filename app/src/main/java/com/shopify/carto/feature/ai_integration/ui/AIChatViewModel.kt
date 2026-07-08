package com.shopify.carto.feature.ai_integration.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shopify.carto.feature.ai_integration.ai.AIShoppingAgent
import com.shopify.carto.feature.product_details.domain.usecase.GetProductDetailsUseCase
import com.shopify.carto.feature.search.domain.model.SearchProduct
import com.shopify.carto.feature.shopping_cart.domain.usecase.AddToCartUseCase
import com.shopify.carto.feature.favorite.domain.usecase.ToggleFavoriteUseCase
import com.shopify.carto.feature.favorite.domain.usecase.ObserveFavoriteIdsUseCase
import com.shopify.carto.feature.product_details.domain.model.merchandiseId
import com.shopify.carto.feature.currency.domain.model.Currency as AppCurrency
import com.shopify.carto.feature.settings.domain.repository.SettingsRepository
import com.shopify.carto.feature.currency.domain.repository.CurrencyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

enum class MessageType {
    TEXT, PRODUCT_LIST, COMPARISON, OUTFIT, ERROR
}

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val type: MessageType = MessageType.TEXT,
    val products: List<SearchProduct> = emptyList(),
    val isVoiceMessage: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val options: List<String> = emptyList(),
    val isTypingFinished: Boolean = true
)

data class AIChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isProcessing: Boolean = false,
    val statusMessage: String? = null
)

@HiltViewModel
class AIChatViewModel @Inject constructor(
    private val aiShoppingAgent: AIShoppingAgent,
    private val getProductDetailsUseCase: GetProductDetailsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val addToCartUseCase: AddToCartUseCase,
    private val settingsRepository: SettingsRepository,
    private val currencyRepository: CurrencyRepository,
    observeFavoriteIdsUseCase: ObserveFavoriteIdsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AIChatUiState())
    val uiState: StateFlow<AIChatUiState> = _uiState.asStateFlow()

    val selectedCurrency: StateFlow<AppCurrency> = settingsRepository.currency
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppCurrency.EGP)

    val favoriteIds: StateFlow<Set<Long>> = observeFavoriteIdsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptySet()
        )

    init {
        val initialMessages = savedMessages ?: listOf(
            ChatMessage(
                text = "WELCOME_PLACEHOLDER",
                isUser = false
            )
        )
        _uiState.update {
            it.copy(messages = initialMessages)
        }
    }

    fun sendMessage(text: String, isVoice: Boolean = false) {
        if (text.isBlank() || _uiState.value.isProcessing) return

        val userMessage = ChatMessage(text = text, isUser = true, isVoiceMessage = isVoice)
        updateUiState {
            it.copy(
                messages = it.messages + userMessage,
                isProcessing = true,
                statusMessage = "Thinking..."
            )
        }

        executeMessageQuery(text)
    }

    fun regenerateLastResponse() {
        if (_uiState.value.isProcessing) return

        val lastUserMessage = _uiState.value.messages.lastOrNull { it.isUser } ?: return
        val indexOfLastUser = _uiState.value.messages.lastIndexOf(lastUserMessage)

        updateUiState {
            it.copy(
                messages = it.messages.subList(0, indexOfLastUser + 1),
                isProcessing = true,
                statusMessage = "Thinking..."
            )
        }

        executeMessageQuery(lastUserMessage.text)
    }

    private fun executeMessageQuery(text: String) {
        viewModelScope.launch {
            try {
                val rates = currencyRepository.observeRates().first()
                val activeCurrency = selectedCurrency.value
                val rate = rates?.rates?.get(activeCurrency) ?: 1.0
                val agentResult = aiShoppingAgent.sendMessage(text, activeCurrency.name, rate) { step ->
                    _uiState.update { it.copy(statusMessage = step) }
                }

                val agentResponse = agentResult.responseText
                val productIds = agentResult.productIds
                val recommendedProducts = mutableListOf<SearchProduct>()
                productIds.forEach { id ->
                    val detailsResult = getProductDetailsUseCase(id)
                    detailsResult.onSuccess { product ->

                        Log.d(
                            "AI_CHAT",
                            """Product Loaded: id=${product.id}, title=${product.title}, vendor=${product.vendor}, image=${product.images.firstOrNull()}""".trimIndent()
                        )

                        recommendedProducts.add(
                            SearchProduct(
                                id = product.id,
                                title = product.title,
                                price = product.price,
                                compareAtPrice = product.compareAtPrice,
                                imageUrl = product.images.firstOrNull(),
                                vendor = product.vendor,
                                productType = product.productType
                            )
                        )
                    }
                }

                val optionsRegex = Regex("""\[Options:\s*([^\]]+)\]""", RegexOption.IGNORE_CASE)
                val optionsMatch = optionsRegex.find(agentResponse)
                val extractedOptions = if (optionsMatch != null) {
                    optionsMatch.groupValues[1].split("|").map { it.trim() }.filter { it.isNotEmpty() }
                } else {
                    emptyList()
                }

                val responseWithoutOptions = agentResponse.replace(optionsRegex, "").trim()

                val cleanedText = responseWithoutOptions
                    .replace(Regex("""\(\s*Product ID:\s*\d+\s*\)""", RegexOption.IGNORE_CASE), "")
                    .replace(Regex("""\s*Product ID:\s*\d+""", RegexOption.IGNORE_CASE), "")
                    .trim()

                val targetText = if (recommendedProducts.isNotEmpty()) {
                    extractIntroText(cleanedText)
                } else {
                    cleanedText.ifBlank { "Here you go!" }
                }

                val aiMessageId = UUID.randomUUID().toString()

                val aiMessage = ChatMessage(
                    id = aiMessageId,
                    text = "",
                    isUser = false,
                    type = if (recommendedProducts.isNotEmpty()) MessageType.PRODUCT_LIST else MessageType.TEXT,
                    products = recommendedProducts,
                    options = extractedOptions,
                    isTypingFinished = false
                )

                updateUiState {
                    it.copy(
                        messages = it.messages + aiMessage,
                        statusMessage = null
                    )
                }

                val words = targetText.split(" ")
                var currentText = ""
                for (i in words.indices) {
                    currentText += (if (i == 0) "" else " ") + words[i]
                    updateUiState { state ->
                        state.copy(
                            messages = state.messages.map { msg ->
                                if (msg.id == aiMessageId) {
                                    msg.copy(text = currentText)
                                } else {
                                    msg
                                }
                            }
                        )
                    }
                    kotlinx.coroutines.delay(55)
                }

                updateUiState { state ->
                    state.copy(
                        messages = state.messages.map { msg ->
                            if (msg.id == aiMessageId) {
                                msg.copy(isTypingFinished = true)
                            } else {
                                msg
                            }
                        },
                        isProcessing = false
                    )
                }
            } catch (e: Exception) {
                val errorMessage = ChatMessage(
                    text = "Sorry, I encountered an error: ${e.localizedMessage}",
                    isUser = false,
                    type = MessageType.ERROR
                )
                updateUiState {
                    it.copy(
                        messages = it.messages + errorMessage,
                        isProcessing = false,
                        statusMessage = null
                    )
                }
            }
        }
    }

    fun toggleProductFavorite(product: SearchProduct) {
        viewModelScope.launch {
            toggleFavoriteUseCase(
                productId = product.id,
                name = product.title,
                imageUrl = product.imageUrl,
                price = product.price
            )
        }
    }

    fun addProductToCart(productId: Long) {
        viewModelScope.launch {
            val detailsResult = getProductDetailsUseCase(productId)
            detailsResult.onSuccess { product ->
                val variant = product.variants.firstOrNull()
                if (variant != null) {
                    addToCartUseCase(variant.merchandiseId, 1)
                }
            }
        }
    }


    /**
     * Extract a short intro sentence from AI response when products will be shown in cards.
     * Strips everything after product lists start (numbered lines, bullet items, headings
     * that describe individual products). Keeps the first human-friendly sentence only.
     */
    private fun extractIntroText(fullText: String): String {
        val lines = fullText.lines()
        val introLines = mutableListOf<String>()

        val productDetailPatterns = listOf(
            Regex("""^\d+[\.\)]\s+.{3,}"""),           // 1. Product name ...
            Regex("""^[-*•]\s+\*\*.+\*\*"""),           // - **Product Name**
            Regex("""^[-*•]\s+.+(price|EGP|USD|\$|£)""", RegexOption.IGNORE_CASE),
            Regex("""^\*\*.+\*\*\s*[-–:]\s*"""),        // **Product**: ...
            Regex("""^#+\s+(here|product|item|result|option|suggestion)""", RegexOption.IGNORE_CASE)
        )

        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.isBlank()) {
                if (introLines.isNotEmpty()) break
                continue
            }
            if (productDetailPatterns.any { it.containsMatchIn(trimmed) }) break
            introLines.add(trimmed)
            if (introLines.size >= 2) break
        }

        return introLines.joinToString(" ").trim()
            .ifBlank { "Here are some items I found for you:" }
    }

    private fun updateUiState(update: (AIChatUiState) -> AIChatUiState) {
        _uiState.update {
            val newState = update(it)
            savedMessages = newState.messages
            newState
        }
    }

    companion object {
        private var savedMessages: List<ChatMessage>? = null
    }
}
