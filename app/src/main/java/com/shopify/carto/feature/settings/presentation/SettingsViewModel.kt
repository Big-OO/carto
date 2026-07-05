package com.shopify.carto.feature.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shopify.carto.feature.settings.domain.model.AppLanguage
import com.shopify.carto.feature.settings.domain.model.AppTheme
import com.shopify.carto.feature.settings.domain.model.Currency
import com.shopify.carto.feature.settings.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val theme: StateFlow<AppTheme> = settingsRepository.theme
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppTheme.LIGHT)

    val language: StateFlow<AppLanguage> = settingsRepository.language
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppLanguage.ENGLISH)

    val currency: StateFlow<Currency> = settingsRepository.currency
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Currency.USD)

    fun setTheme(theme: AppTheme) {
        viewModelScope.launch {
            settingsRepository.setTheme(theme)
        }
    }

    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch {
            settingsRepository.setLanguage(language)
        }
    }

    fun setCurrency(currency: Currency) {
        viewModelScope.launch {
            settingsRepository.setCurrency(currency)
        }
    }
}
