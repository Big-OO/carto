package com.shopify.carto.feature.product_reviews.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shopify.carto.feature.product_reviews.domain.usecase.GetProductReviewsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ProductReviewsViewModel @Inject constructor(
    private val getProductReviewsUseCase: GetProductReviewsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductReviewsUiState())
    val uiState: StateFlow<ProductReviewsUiState> = _uiState.asStateFlow()

    fun loadProductReviews(productId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            getProductReviewsUseCase(productId.toLong())
                .onSuccess { data ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            reviews = data
                        )
                    }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = throwable.message
                        )
                    }
                }
        }
    }


}