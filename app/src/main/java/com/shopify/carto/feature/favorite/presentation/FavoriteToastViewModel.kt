package com.shopify.carto.feature.favorite.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shopify.carto.feature.favorite.presentation.feedback.FavoriteFeedbackBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.shopify.carto.feature.favorite.domain.usecase.ToggleFavoriteUseCase
import kotlin.time.Duration.Companion.milliseconds


data class FavoriteToastUiModel(
    val productId: Long,
    val productName: String,
    val imageUrl: String?,
    val price: Double,
    val added: Boolean,
)

@HiltViewModel
class FavoriteToastViewModel @Inject constructor(
    private val feedbackBus: FavoriteFeedbackBus,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
) : ViewModel() {

    private val _toast = MutableStateFlow<FavoriteToastUiModel?>(null)
    val toast: StateFlow<FavoriteToastUiModel?> = _toast.asStateFlow()

    private var dismissJob: Job? = null

    init {
        viewModelScope.launch {
            feedbackBus.events.collect { event ->
                dismissJob?.cancel()
                _toast.value = FavoriteToastUiModel(
                    productId = event.productId,
                    productName = event.productName,
                    imageUrl = event.imageUrl,
                    price = event.price,
                    added = event.added,
                )
                dismissJob = launch {
                    delay(3_000.milliseconds)
                    _toast.value = null
                }
            }
        }
    }

    fun dismiss() {
        dismissJob?.cancel()
        _toast.value = null
    }

    fun undo() {
        val current = _toast.value ?: return
        dismissJob?.cancel()
        _toast.value = null
        viewModelScope.launch {
            toggleFavoriteUseCase(
                productId = current.productId,
                name = current.productName,
                imageUrl = current.imageUrl,
                price = current.price,
            )
        }
    }
}