package com.example.carto.feature.home.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carto.feature.home.domain.repository.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    private val repository: HomeRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val productId: Long = checkNotNull(savedStateHandle["productId"])

    private val _uiState = MutableStateFlow<ProductDetailsUiState>(ProductDetailsUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadProduct()
    }

    fun loadProduct() {
        viewModelScope.launch {
            _uiState.value = ProductDetailsUiState.Loading
            repository.getProductById(productId)
                .onSuccess { product ->
                    _uiState.value = ProductDetailsUiState.Success(product)
                }
                .onFailure { error ->
                    _uiState.value = ProductDetailsUiState.Error(
                        error.message ?: "We couldn't load this product. Try again later."
                    )
                }
        }
    }
}
