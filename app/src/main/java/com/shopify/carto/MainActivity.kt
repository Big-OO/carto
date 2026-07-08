package com.shopify.carto

import android.content.Context
import android.content.ContextWrapper
import android.content.res.AssetManager
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import kotlinx.coroutines.launch
import com.shopify.carto.feature.settings.domain.model.AppLanguage
import com.shopify.carto.feature.settings.domain.model.AppTheme
import com.shopify.carto.feature.settings.domain.repository.SettingsRepository
import com.shopify.carto.navigation.AppNavHost
import com.shopify.carto.ui.theme.CartoTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject

import com.shopify.carto.feature.currency.domain.repository.CurrencyRepository
import com.shopify.carto.feature.currency.presentation.format.CurrencyFormatter
import com.shopify.carto.feature.currency.presentation.format.LocalCurrencyFormatter
import com.shopify.carto.feature.currency.domain.model.Currency

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    @Inject
    lateinit var currencyRepository: CurrencyRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val appTheme by settingsRepository.theme.collectAsState(initial = AppTheme.LIGHT)
            val appLanguage by settingsRepository.language.collectAsState(initial = AppLanguage.ENGLISH)
            val selectedCurrency by currencyRepository.observeSelectedCurrency().collectAsState(initial = Currency.USD)
            val exchangeRates by currencyRepository.observeRates().collectAsState(initial = null)
            
            val context = LocalContext.current
            val currentConfig = LocalConfiguration.current

            val localeCode = when (appLanguage) {
                AppLanguage.ENGLISH -> "en"
                AppLanguage.ARABIC -> "ar"
            }

            val locale = Locale(localeCode)
            Locale.setDefault(locale)


            val configuration = Configuration(currentConfig).apply {
                setLocale(locale)
                setLayoutDirection(locale)
            }

            val configContext = context.createConfigurationContext(configuration)

            val localizedContext = remember(appLanguage) {
                LocalizedContext(context, configContext)
            }

            val layoutDirection = if (appLanguage == AppLanguage.ARABIC) {
                LayoutDirection.Rtl
            } else {
                LayoutDirection.Ltr
            }

            val contentAlpha = remember { Animatable(1f) }
            val contentScale = remember { Animatable(1f) }
            val isFirstLaunch = remember { mutableStateOf(true) }

            LaunchedEffect(appLanguage) {
                if (isFirstLaunch.value) {
                    isFirstLaunch.value = false
                } else {
                    contentAlpha.snapTo(0f)
                    contentScale.snapTo(0.96f)
                    launch { contentAlpha.animateTo(1f, tween(350, easing = FastOutSlowInEasing)) }
                    launch { contentScale.animateTo(1f, tween(350, easing = FastOutSlowInEasing)) }
                }
            }

            CompositionLocalProvider(
                LocalContext provides localizedContext,
                LocalConfiguration provides configuration,
                LocalLayoutDirection provides layoutDirection,
                LocalCurrencyFormatter provides CurrencyFormatter(selectedCurrency, exchangeRates)
            ) {
                CartoTheme(appTheme = appTheme) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                alpha = contentAlpha.value
                                scaleX = contentScale.value
                                scaleY = contentScale.value
                            }
                    ) {
                        AppNavHost()
                    }
                }
            }
        }
    }
}


private class LocalizedContext(
    base: Context,
    private val localizedContext: Context
) : ContextWrapper(base) {
    override fun getResources(): Resources = localizedContext.resources
    override fun getAssets(): AssetManager = localizedContext.assets
}