package com.example.carto.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.carto.home.data.repository.HomeRepositoryImp
import com.example.carto.home.domain.mappers.Product
import com.example.carto.home.domain.mappers.VendorUi
import com.example.carto.home.domain.mappers.toVendorUiList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(val products: List<Product>, val vendors: List<VendorUi>) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

class HomeViewModel(
    private val repository: HomeRepositoryImp
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        fetchHomeData()
    }

    fun fetchHomeData() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            repository.getProducts()
                .onSuccess { products ->
                    _uiState.value = HomeUiState.Success(
                        products = products,
                        vendors = products.toVendorUiList()
                    )
                }
                .onFailure {
                    _uiState.value = HomeUiState.Error(it.message ?: "Unknown error")
                }
        }
    }
}

class HomeViewModelFactory(
    private val repository: HomeRepositoryImp
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(repository) as T
    }
}