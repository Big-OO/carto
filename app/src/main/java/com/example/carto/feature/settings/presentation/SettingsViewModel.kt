package com.example.carto.feature.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carto.feature.settings.data.local.SettingsLocalDataSource
import com.example.carto.feature.settings.domain.model.AppLanguage
import com.example.carto.feature.settings.domain.model.AppTheme
import com.example.carto.feature.settings.domain.model.Currency
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsLocalDataSource: SettingsLocalDataSource
) : ViewModel() {

    val theme: StateFlow<AppTheme> = settingsLocalDataSource.theme
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppTheme.LIGHT)

    val language: StateFlow<AppLanguage> = settingsLocalDataSource.language
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppLanguage.ENGLISH)

    val currency: StateFlow<Currency> = settingsLocalDataSource.currency
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Currency.USD)

    fun setTheme(theme: AppTheme) {
        viewModelScope.launch {
            settingsLocalDataSource.setTheme(theme)
        }
    }

    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch {
            settingsLocalDataSource.setLanguage(language)
        }
    }

    fun setCurrency(currency: Currency) {
        viewModelScope.launch {
            settingsLocalDataSource.setCurrency(currency)
        }
    }
}
