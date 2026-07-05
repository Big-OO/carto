package com.shopify.carto.feature.favorite.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shopify.carto.feature.favorite.domain.usecase.ObserveFavoriteIdsUseCase
import com.shopify.carto.feature.favorite.domain.usecase.ToggleFavoriteUseCase
import com.shopify.carto.feature.favorite.presentation.feedback.FavoriteFeedbackBus
import com.shopify.carto.feature.favorite.presentation.feedback.FavoriteFeedbackEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    observeFavoriteIdsUseCase: ObserveFavoriteIdsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val feedbackBus: FavoriteFeedbackBus,
) : ViewModel() {

    val favoriteIds: StateFlow<Set<Long>> = observeFavoriteIdsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptySet(),
        )

    fun toggleFavorite(
        productId: Long,
        name: String,
        imageUrl: String?,
        price: Double,
    ) {
        viewModelScope.launch {
            val added = toggleFavoriteUseCase(productId, name, imageUrl, price)
            feedbackBus.emit(
                FavoriteFeedbackEvent(
                    productId = productId,
                    productName = name,
                    imageUrl = imageUrl,
                    price = price,
                    added = added,
                )
            )
        }
    }
}