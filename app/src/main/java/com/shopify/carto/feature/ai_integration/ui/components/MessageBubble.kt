package com.shopify.carto.feature.ai_integration.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import com.shopify.carto.R
import com.shopify.carto.feature.ai_integration.ui.ChatMessage
import com.shopify.carto.feature.ai_integration.ui.MessageType
import com.shopify.carto.feature.currency.domain.model.Currency
import com.shopify.carto.feature.search.domain.model.SearchProduct

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MessageBubble(
    message: ChatMessage,
    currency: Currency,
    favoriteIds: Set<Long>,
    isLastAiMessage: Boolean,
    onProductClick: (Long) -> Unit,
    onFavoriteClick: (SearchProduct) -> Unit,
    onAddToCartClick: (SearchProduct) -> Unit,
    onRegenerateClick: () -> Unit,
    onOptionClick: (String) -> Unit,
    snackbarHostState: SnackbarHostState,
    context: Context,
    modifier: Modifier = Modifier
) {
    val isUser = message.isUser
    val scope = rememberCoroutineScope()
    val userShape = RoundedCornerShape(20.dp, 20.dp, 4.dp, 20.dp)
    val aiShape   = RoundedCornerShape(4.dp, 20.dp, 20.dp, 20.dp)
    val hasProducts = message.products.isNotEmpty()
    Log.d("Tago: ","hase products:-> $hasProducts")

    val animVisible = rememberSaveable(message.id) { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        animVisible.value = true
    }

    AnimatedVisibility(
        visible = animVisible.value,
        enter = fadeIn(animationSpec = tween(400, easing = FastOutSlowInEasing)) + 
                slideInVertically(animationSpec = tween(400, easing = FastOutSlowInEasing)) { it / 3 },
        exit = fadeOut(animationSpec = tween(200))
    ) {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            // ── AI product response: cards FIRST at full width ──────────────────
            // Products are never described in text — always shown exclusively as cards.
            if (!isUser && hasProducts) {
                ProductsCarousel(
                    products = message.products,
                    currency = currency,
                    favoriteIds = favoriteIds,
                    onProductClick = onProductClick,
                    onFavoriteClick = onFavoriteClick,
                    onAddToCartClick = onAddToCartClick
                )
            }

            // ── Message bubble row ────────────────────────────────────────────────
            // Skip text bubble entirely when: AI has products but no meaningful text
            val skipTextBubble = !isUser && hasProducts && message.text.isBlank()

            if (!skipTextBubble) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
                    verticalAlignment = Alignment.Bottom
                ) {
                    // AI avatar
                    if (!isUser) {
                        Surface(
                            modifier = Modifier.padding(end = 6.dp, bottom = 2.dp).size(26.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(5.dp)
                            )
                        }
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .then(
                                if (isUser) Modifier.padding(start = 52.dp)
                                else Modifier.padding(end = 52.dp)
                            )
                    ) {
                        when {
                            // Error bubble
                            message.type == MessageType.ERROR -> {
                                ErrorCard(
                                    errorMessage = message.text,
                                    onRetryClick = onRegenerateClick
                                )
                            }
                            // Voice message bubble (user)
                            message.isVoiceMessage && isUser -> {
                                VoiceMessageBubble()
                            }
                            // Normal or product-intro text bubble
                            else -> {
                                if (message.text.isNotBlank()) {
                                    Surface(
                                        color = if (isUser)
                                            MaterialTheme.colorScheme.primary
                                        else
                                            MaterialTheme.colorScheme.surfaceVariant,
                                        shape = if (isUser) userShape else aiShape
                                    ) {
                                        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                                            MarkdownRenderer(
                                                text = if (message.text == "WELCOME_PLACEHOLDER") {
                                                    stringResource(id = R.string.ai_welcome_assistant_message)
                                                } else {
                                                    message.text
                                                },
                                                color = if (isUser) Color.White
                                                        else MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }

                                if (!isUser && message.options.isNotEmpty()) {
                                    var optionsVisible by remember { mutableStateOf(false) }
                                    LaunchedEffect(message.isTypingFinished) {
                                        if (message.isTypingFinished) {
                                            optionsVisible = true
                                        }
                                    }
                                    AnimatedVisibility(
                                        visible = optionsVisible,
                                        enter = fadeIn(animationSpec = tween(300)),
                                        exit = fadeOut(animationSpec = tween(300))
                                    ) {
                                        Column {
                                            Spacer(modifier = Modifier.height(8.dp))
                                            FlowRow(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                verticalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                val sortedOptions = remember(message.options) { message.options.sorted() }
                                                sortedOptions.forEachIndexed { index, option ->
                                                    var chipVisible by remember { mutableStateOf(false) }
                                                    LaunchedEffect(Unit) {
                                                        delay(index * 60L)
                                                        chipVisible = true
                                                    }
                                                    AnimatedVisibility(
                                                        visible = chipVisible,
                                                        enter = fadeIn(tween(250)) + slideInVertically(tween(250)) { it / 2 },
                                                        exit = fadeOut(tween(150))
                                                    ) {
                                                        val interactionSource = remember { MutableInteractionSource() }
                                                        val isPressed by interactionSource.collectIsPressedAsState()
                                                        val scale by animateFloatAsState(
                                                            targetValue = if (isPressed) 0.95f else 1f,
                                                            animationSpec = spring(
                                                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                                                stiffness = Spring.StiffnessLow
                                                            ),
                                                            label = "chipScale"
                                                        )
                                                        Surface(
                                                            onClick = { onOptionClick(option) },
                                                            shape = RoundedCornerShape(20.dp),
                                                            color = MaterialTheme.colorScheme.primaryContainer,
                                                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                                            border = BorderStroke(
                                                                width = 1.dp,
                                                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                                            ),
                                                            shadowElevation = if (isPressed) 1.dp else 2.dp,
                                                            interactionSource = interactionSource,
                                                            modifier = Modifier
                                                                .graphicsLayer {
                                                                    scaleX = scale
                                                                    scaleY = scale
                                                                }
                                                        ) {
                                                            Text(
                                                                text = option,
                                                                style = MaterialTheme.typography.bodyMedium,
                                                                fontWeight = FontWeight.SemiBold,
                                                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Action row — AI only, no Share
                        if (!isUser && message.type != MessageType.ERROR) {
                            CopyActionRow(
                                showRegenerate = isLastAiMessage,
                                onCopyClick = {
                                    val cb = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val welcomeText = if (message.text == "WELCOME_PLACEHOLDER") {
                                        context.getString(R.string.ai_welcome_assistant_message)
                                    } else {
                                        message.text
                                    }
                                    cb.setPrimaryClip(ClipData.newPlainText("Carto AI", welcomeText))
                                    scope.launch { snackbarHostState.showSnackbar(context.getString(R.string.ai_copied_toast)) }
                                },
                                onRegenerateClick = onRegenerateClick
                            )
                        }
                    }

                    // User side spacer
                    if (isUser) Spacer(modifier = Modifier.size(6.dp))
                }
            }

            // ── User messages with products (edge case) ───────────────────────────
            // Not expected, but guard anyway
            if (isUser && hasProducts) {
                ProductsCarousel(
                    products = message.products,
                    currency = currency,
                    favoriteIds = favoriteIds,
                    onProductClick = onProductClick,
                    onFavoriteClick = onFavoriteClick,
                    onAddToCartClick = onAddToCartClick
                )
            }
        }
    }
}
