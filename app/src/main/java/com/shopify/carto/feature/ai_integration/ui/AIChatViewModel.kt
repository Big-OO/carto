package com.shopify.carto.feature.ai_integration.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shopify.carto.feature.ai_integration.ai.AIShoppingAgent
import com.shopify.carto.feature.product_details.domain.usecase.GetProductDetailsUseCase
import com.shopify.carto.feature.search.domain.model.SearchProduct
import com.shopify.carto.feature.shopping_cart.domain.usecase.AddToCartUseCase
import com.shopify.carto.feature.favorite.domain.usecase.ToggleFavoriteUseCase
import com.shopify.carto.feature.product_details.domain.model.merchandiseId
import com.shopify.carto.feature.settings.domain.model.Currency as AppCurrency
import com.shopify.carto.feature.settings.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
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
    val timestamp: Long = System.currentTimeMillis()
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
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AIChatUiState())
    val uiState: StateFlow<AIChatUiState> = _uiState.asStateFlow()

    val selectedCurrency: StateFlow<AppCurrency> = settingsRepository.currency
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppCurrency.EGP)

    init {
        _uiState.update {
            it.copy(
                messages = listOf(
                    ChatMessage(
                        text = "Hi! I'm your AI Shopping Assistant. Ask me anything about searching products, comparing prices, managing your cart/wishlist, or generating outfit ideas!",
                        isUser = false
                    )
                )
            )
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank() || _uiState.value.isProcessing) return

        val userMessage = ChatMessage(text = text, isUser = true)
        _uiState.update {
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

        _uiState.update {
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
                val agentResponse = aiShoppingAgent.sendMessage(text) { step ->
                    _uiState.update { it.copy(statusMessage = step) }
                }

                val productIds = extractProductIds(agentResponse)
                val recommendedProducts = mutableListOf<SearchProduct>()
                productIds.forEach { id ->
                    val detailsResult = getProductDetailsUseCase(id)
                    detailsResult.onSuccess { product ->
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

                val cleanedText = agentResponse
                    .replace(Regex("""\(\s*Product ID:\s*\d+\s*\)""", RegexOption.IGNORE_CASE), "")
                    .replace(Regex("""\s*Product ID:\s*\d+""", RegexOption.IGNORE_CASE), "")
                    .trim()

                val targetText = cleanedText.ifBlank { "I found these items for you:" }
                val aiMessageId = UUID.randomUUID().toString()

                val aiMessage = ChatMessage(
                    id = aiMessageId,
                    text = "",
                    isUser = false,
                    type = if (recommendedProducts.isNotEmpty()) MessageType.PRODUCT_LIST else MessageType.TEXT,
                    products = recommendedProducts
                )

                // Add empty response bubble and clear the progress status message loader,
                // but keep isProcessing = true to keep the typing area locked during streaming.
                _uiState.update {
                    it.copy(
                        messages = it.messages + aiMessage,
                        statusMessage = null
                    )
                }

                // Typewriter/streaming animation word-by-word
                val words = targetText.split(" ")
                var currentText = ""
                for (i in words.indices) {
                    currentText += (if (i == 0) "" else " ") + words[i]
                    _uiState.update { state ->
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
                    kotlinx.coroutines.delay(35) // 35ms delay per word
                }

                _uiState.update {
                    it.copy(isProcessing = false)
                }
            } catch (e: Exception) {
                val errorMessage = ChatMessage(
                    text = "Sorry, I encountered an error: ${e.localizedMessage}",
                    isUser = false,
                    type = MessageType.ERROR
                )
                _uiState.update {
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

    private fun extractProductIds(text: String): List<Long> {
        val regex = Regex("""Product ID:\s*(\d+)""", RegexOption.IGNORE_CASE)
        return regex.findAll(text)
            .map { it.groupValues[1].toLong() }
            .distinct()
            .toList()
    }
}
