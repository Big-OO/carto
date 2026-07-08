package com.shopify.carto.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import com.shopify.carto.core.session.domain.model.AppSession
import com.shopify.carto.core.session.domain.usecase.ObserveAppSessionUseCase
import com.shopify.carto.feature.home.domain.model.Brand
import com.shopify.carto.feature.home.domain.model.Category
import com.shopify.carto.feature.home.domain.model.Coupon
import com.shopify.carto.feature.home.domain.model.Product
import com.shopify.carto.feature.home.domain.repository.HomeRepository
import com.shopify.carto.feature.home.domain.usecase.GetActiveCouponsUseCase
import com.shopify.carto.feature.shopping_cart.domain.usecase.RefreshCartUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class HomeContent(
    val products: List<Product> = emptyList(),
    val categories: List<Category> = emptyList(),
    val brands: List<Brand> = emptyList(),
    val coupons: List<Coupon> = emptyList(),
)

sealed interface HomeUiState {

    data object Loading : HomeUiState

    data class Success(
        val content: HomeContent
    ) : HomeUiState

    data class Error(
        val error: Throwable
    ) : HomeUiState
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository,
    private val getActiveCouponsUseCase: GetActiveCouponsUseCase,
    observeAppSessionUseCase: ObserveAppSessionUseCase,
    private val refreshCartUseCase: RefreshCartUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    val session: StateFlow<AppSession> = observeAppSessionUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AppSession(isGuest = true),
    )

    init {
        fetchHomeData()
        viewModelScope.launch {
            session.collect {
                refreshCartUseCase()
            }
        }
    }

    fun fetchHomeData() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading

            val productsDeferred = async {
                repository.getProducts()
            }

            val categoriesDeferred = async {
                repository.getCategories()
            }
            val brandsDeferred = async {
                repository.getBrands()
            }
            val couponsDeferred = async {
                getActiveCouponsUseCase()
            }

            val productsResult = productsDeferred.await()
            val categoriesResult = categoriesDeferred.await()
            val brandsResult = brandsDeferred.await()
            val couponsResult = couponsDeferred.await()

            productsResult.onFailure {
                _uiState.value = HomeUiState.Error(it)
                return@launch
            }

            categoriesResult.onFailure {
                _uiState.value = HomeUiState.Error(it)
                return@launch
            }

            brandsResult.onFailure {
                _uiState.value = HomeUiState.Error(it)
                return@launch
            }

            val products = productsResult.getOrThrow()
            val categories = categoriesResult.getOrThrow()

            _uiState.value = HomeUiState.Success(
                content = HomeContent(
                    products = products,
                    categories = categories,
                    brands = brandsResult.getOrThrow(),
                    coupons = couponsResult.getOrElse { emptyList() },
                )
            )
        }
    }
}
