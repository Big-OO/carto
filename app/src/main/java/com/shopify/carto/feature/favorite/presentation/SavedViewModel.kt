package com.shopify.carto.feature.favorite.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shopify.carto.feature.favorite.domain.model.FavoriteProduct
import com.shopify.carto.feature.favorite.domain.usecase.ObserveFavoritesUseCase
import com.shopify.carto.feature.favorite.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart


@HiltViewModel
class SavedViewModel @Inject constructor(
    observeFavoritesUseCase: ObserveFavoritesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
) : ViewModel() {

    private val _isInitialLoading = MutableStateFlow(true)
    val isInitialLoading: StateFlow<Boolean> = _isInitialLoading.asStateFlow()

    val favorites: StateFlow<List<FavoriteProduct>> = observeFavoritesUseCase()
        .onStart {
            _isInitialLoading.value = false
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    fun removeFavorite(product: FavoriteProduct) {
        viewModelScope.launch {
            toggleFavoriteUseCase(
                productId = product.productId,
                name = product.name,
                imageUrl = product.imageUrl,
                price = product.price
            )
        }
    }
}