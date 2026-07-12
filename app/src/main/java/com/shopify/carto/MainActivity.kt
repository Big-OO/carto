package com.shopify.carto

import android.content.Context
import android.content.ContextWrapper
import android.content.res.AssetManager
import android.content.res.Configuration
import android.content.res.Resources
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.shopify.carto.feature.currency.domain.model.Currency
import com.shopify.carto.feature.currency.domain.repository.CurrencyRepository
import com.shopify.carto.feature.currency.presentation.format.CurrencyFormatter
import com.shopify.carto.feature.currency.presentation.format.LocalCurrencyFormatter
import com.shopify.carto.feature.settings.domain.model.AppLanguage
import com.shopify.carto.feature.settings.domain.model.AppTheme
import com.shopify.carto.feature.settings.domain.repository.SettingsRepository
import com.shopify.carto.navigation.AppNavHost
import com.shopify.carto.ui.theme.CartoTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    @Inject
    lateinit var currencyRepository: CurrencyRepository

    private val widgetLaunchState = mutableStateOf(WidgetLaunchAction.None)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        widgetLaunchState.value = extractWidgetLaunchAction(intent)
        setContent {
            val appTheme by settingsRepository.theme.collectAsState(initial = AppTheme.LIGHT)
            val widgetLaunchAction by remember { widgetLaunchState }
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
                    val isOnline by rememberConnectivityState()
                    var showOfflineBanner by remember { mutableStateOf(false) }
                    var showOnlineNotification by remember { mutableStateOf(false) }
                    var previousState by remember { mutableStateOf<Boolean?>(null) }

                    LaunchedEffect(isOnline) {
                        val prev = previousState
                        previousState = isOnline
                        if (prev != null) {
                            if (!isOnline) {
                                showOfflineBanner = true
                                showOnlineNotification = false
                            } else {
                                showOfflineBanner = false
                                showOnlineNotification = true
                                delay(3000)
                                showOnlineNotification = false
                            }
                        } else {
                            if (!isOnline) {
                                showOfflineBanner = true
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                alpha = contentAlpha.value
                                scaleX = contentScale.value
                                scaleY = contentScale.value
                            }
                    ) {
                        AppNavHost(
                            openAiAssistant = widgetLaunchAction.openAiAssistant,
                            autoStartAiVoice = widgetLaunchAction.startAiVoice,
                        )

                        ConnectivityStatusOverlay(
                            showOffline = showOfflineBanner,
                            showOnline = showOnlineNotification,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 80.dp) // Offset to float nicely above bottom bar
                        )
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        widgetLaunchState.value = extractWidgetLaunchAction(intent)
    }

    private fun extractWidgetLaunchAction(intent: android.content.Intent?): WidgetLaunchAction {
        if (intent == null) return WidgetLaunchAction.None
        val openAi = intent.getBooleanExtra(EXTRA_OPEN_AI_ASSISTANT, false)
        val startVoice = intent.getBooleanExtra(EXTRA_START_AI_VOICE, false)
        return WidgetLaunchAction(
            openAiAssistant = openAi,
            startAiVoice = openAi && startVoice,
        )
    }

    companion object {
        const val EXTRA_OPEN_AI_ASSISTANT = "extra_open_ai_assistant"
        const val EXTRA_START_AI_VOICE = "extra_start_ai_voice"
    }
}

@Composable
private fun rememberConnectivityState(): State<Boolean> {
    val context = LocalContext.current
    val connectivityManager = remember {
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    val initialState = remember {
        val activeNetwork = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

    val connectionState = remember { mutableStateOf(initialState) }

    DisposableEffect(connectivityManager) {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                connectionState.value = true
            }
            override fun onLost(network: Network) {
                connectionState.value = false
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(request, callback)

        onDispose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }

    return connectionState
}

@Composable
private fun ConnectivityStatusOverlay(
    showOffline: Boolean,
    showOnline: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "wifi_pulse")
        val pulseScale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.4f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulse_scale"
        )
        val pulseAlpha by infiniteTransition.animateFloat(
            initialValue = 0.8f,
            targetValue = 0.2f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulse_alpha"
        )

        AnimatedVisibility(
            visible = showOffline,
            enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it / 2 }) + fadeOut()
        ) {
            Row(
                modifier = Modifier
                    .shadow(12.dp, RoundedCornerShape(24.dp))
                    .background(Color(0xFF212121), RoundedCornerShape(24.dp))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.WifiOff,
                    contentDescription = "Offline",
                    tint = Color(0xFFFF5252),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "You are offline",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(10.dp))
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .graphicsLayer {
                                scaleX = pulseScale
                                scaleY = pulseScale
                                alpha = pulseAlpha
                            }
                            .background(Color(0xFFFF5252), CircleShape)
                    )
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(Color(0xFFFF5252), CircleShape)
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = showOnline,
            enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it / 2 }) + fadeOut()
        ) {
            Row(
                modifier = Modifier
                    .shadow(12.dp, RoundedCornerShape(24.dp))
                    .background(Color(0xFF2E7D32), RoundedCornerShape(24.dp))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Wifi,
                    contentDescription = "Online",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Back online",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

private data class WidgetLaunchAction(
    val openAiAssistant: Boolean = false,
    val startAiVoice: Boolean = false,
) {
    companion object {
        val None = WidgetLaunchAction()
    }
}

private class LocalizedContext(
    base: Context,
    private val localizedContext: Context
) : ContextWrapper(base) {
    override fun getResources(): Resources = localizedContext.resources
    override fun getAssets(): AssetManager = localizedContext.assets
}