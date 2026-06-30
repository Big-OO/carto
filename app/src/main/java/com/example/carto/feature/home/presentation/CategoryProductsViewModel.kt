package com.example.carto.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carto.feature.home.domain.model.Product
import com.example.carto.feature.home.domain.repository.HomeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CategoryProductsViewModel @Inject constructor(
    private val repository: HomeRepository
) : ViewModel() {

    private var allProducts = emptyList<Product>()

    private val _uiState =
        MutableStateFlow<CategoryProductsUiState>(
            CategoryProductsUiState.Loading
        )

    val uiState = _uiState.asStateFlow()

    fun loadCategory(collectionId: Long) {

        viewModelScope.launch {

            _uiState.value = CategoryProductsUiState.Loading

            repository
                .getProductsByCategory(collectionId)
                .onSuccess {

                    allProducts = it

                    updateUi("All")

                }
                .onFailure {

                    _uiState.value =
                        CategoryProductsUiState.Error(
                            it.message ?: "Unknown error"
                        )

                }

        }

    }
    private fun updateUi(
        selectedChip: String
    ) {

        val filteredProducts =
            if (selectedChip == "All")
                allProducts
            else
                allProducts.filter {
                    it.productType.equals(selectedChip, true)
                }

        _uiState.value =
            CategoryProductsUiState.Success(
                products = filteredProducts,
                chips = listOf("All") +
                        allProducts
                            .map { it.productType }
                            .distinct(),
                selectedChip = selectedChip
            )
    }


    fun selectChip(type: String) {

        updateUi(type)

    }


}
