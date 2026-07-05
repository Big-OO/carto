package com.shopify.carto.feature.brand.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shopify.carto.feature.brand.domain.usecase.GetBrandDetailsUseCase
import com.shopify.carto.feature.brand.domain.usecase.GetBrandProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BrandViewModel @Inject constructor(
    private val getBrandDetailsUseCase: GetBrandDetailsUseCase,
    private val getBrandProductsUseCase: GetBrandProductsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<BrandUiState>(BrandUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _effect = Channel<BrandEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun onEvent(event: BrandEvent) {
        when (event) {
            is BrandEvent.LoadBrand -> loadBrand(event.brandName)
            is BrandEvent.FilterProductType -> filterProducts(event.productType)
            BrandEvent.ClickBack -> {
                viewModelScope.launch {
                    _effect.send(BrandEffect.NavigateBack)
                }
            }
            is BrandEvent.ClickProduct -> {
                viewModelScope.launch {
                    event.productId.toLongOrNull()?.let { id ->
                        _effect.send(BrandEffect.NavigateToProductDetail(id))
                    }
                }
            }
        }
    }

    private fun loadBrand(brandName: String) {
        viewModelScope.launch {
            _uiState.value = BrandUiState.Loading
            
            val detailsResult = getBrandDetailsUseCase(brandName)
            val productsResult = getBrandProductsUseCase(brandName)
            
            if (detailsResult.isSuccess && productsResult.isSuccess) {
                val domainBrand = detailsResult.getOrNull()!!
                val domainProducts = productsResult.getOrNull()!!
                
                val brand = Brand(
                    name = domainBrand.title,
                    imageUrl = domainBrand.imageUrl
                )
                
                val allProducts = domainProducts.map { domainProd ->
                    Product(
                        id = domainProd.id.toString(),
                        name = domainProd.title,
                        type = domainProd.productType,
                        price = if (domainProd.price.startsWith("$")) domainProd.price else "$${domainProd.price}",
                        imageUrl = domainProd.imageUrl
                    )
                }
                
                val productTypes = listOf("All") + allProducts.map { it.type }.filter { it.isNotBlank() }.distinct()
                
                _uiState.value = BrandUiState.Success(
                    brand = brand,
                    allProducts = allProducts,
                    filteredProducts = allProducts,
                    filterChips = productTypes,
                    selectedChip = "All"
                )
            } else {
                val errorMsg = productsResult.exceptionOrNull()?.message 
                    ?: detailsResult.exceptionOrNull()?.message 
                    ?: "Failed to load brand data"
                _uiState.value = BrandUiState.Error(errorMsg)
            }
        }
    }

    private fun filterProducts(productType: String) {
        val currentState = _uiState.value
        if (currentState is BrandUiState.Success) {
            val filtered = if (productType == "All") {
                currentState.allProducts
            } else {
                currentState.allProducts.filter { it.type == productType }
            }
            _uiState.value = currentState.copy(
                filteredProducts = filtered,
                selectedChip = productType
            )
        }
    }
}
