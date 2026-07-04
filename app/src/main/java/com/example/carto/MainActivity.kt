package com.example.carto

import android.content.Context
import android.content.ContextWrapper
import android.content.res.AssetManager
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import com.example.carto.feature.settings.domain.model.AppLanguage
import com.example.carto.feature.settings.domain.model.AppTheme
import com.example.carto.feature.settings.domain.repository.SettingsRepository
import com.example.carto.navigation.AppNavHost
import com.example.carto.ui.theme.CartoTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val appTheme by settingsRepository.theme.collectAsState(initial = AppTheme.LIGHT)
            val appLanguage by settingsRepository.language.collectAsState(initial = AppLanguage.ENGLISH)
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

            CompositionLocalProvider(
                LocalContext provides localizedContext,
                LocalConfiguration provides configuration
            ) {
                CartoTheme(appTheme = appTheme) {
                    AppNavHost()
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
