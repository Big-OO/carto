package com.shopify.carto.feature.ai_integration.ui

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shopify.carto.feature.ai_integration.voice.SpeechRecognizerManager
import com.shopify.carto.feature.ai_integration.voice.VoiceRecognitionState
import com.shopify.carto.feature.home.domain.model.Product
import com.shopify.carto.feature.home.presentation.screens.components.ProductCard
import com.shopify.carto.feature.search.domain.model.SearchProduct
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIChatScreen(
    onProductClick: (Long) -> Unit,
    onBackClick: () -> Unit = {},
    viewModel: AIChatViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedCurrency by viewModel.selectedCurrency.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    var textInput by remember { mutableStateOf("") }
    var voiceState by remember { mutableStateOf<VoiceRecognitionState>(VoiceRecognitionState.Idle) }

    // ── SpeechRecognizerManager lifecycle wired to composition ─────────────
    // DisposableEffect: registered once, destroyed on leave — correct pattern per compose-side-effects
    val manager = remember {
        SpeechRecognizerManager(context) { state ->
            voiceState = state
            // On final result: populate the text field and immediately send
            if (state is VoiceRecognitionState.Result && state.text.isNotBlank()) {
                textInput = state.text
            }
        }
    }
    DisposableEffect(Unit) {
        onDispose { manager.destroy() }
    }

    // On partial result, keep updating text field live so user sees transcription
    LaunchedEffect(voiceState) {
        when (val s = voiceState) {
            is VoiceRecognitionState.Partial -> textInput = s.text
            is VoiceRecognitionState.Error   -> voiceState = VoiceRecognitionState.Idle
            else -> Unit
        }
    }

    // ── Runtime permission launcher ─────────────────────────────────────────
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            voiceState = VoiceRecognitionState.ReadyForSpeech
            manager.startListening()
        } else {
            scope.launch { snackbarHostState.showSnackbar("Microphone permission denied") }
        }
    }

    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            scope.launch {
                listState.animateScrollToItem(uiState.messages.size - 1)
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            ChatHeader(onBackClick = onBackClick)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Main Content Area
            Box(modifier = Modifier.weight(1f)) {
                if (uiState.messages.isEmpty() || (uiState.messages.size == 1 && uiState.messages.first().text.isBlank())) {
                    EmptyState(
                        onSuggestionClick = { prompt ->
                            viewModel.sendMessage(prompt)
                        }
                    )
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(uiState.messages, key = { it.id }) { message ->
                            val isLast = uiState.messages.lastOrNull() == message
                            MessageBubble(
                                message = message,
                                currency = selectedCurrency,
                                isLastAiMessage = isLast && !message.isUser,
                                onProductClick = onProductClick,
                                onFavoriteClick = { viewModel.toggleProductFavorite(it) },
                                onAddToCartClick = { viewModel.addProductToCart(it.id) },
                                onRegenerateClick = { viewModel.regenerateLastResponse() },
                                snackbarHostState = snackbarHostState,
                                context = context
                            )
                        }

                        val statusMsg = uiState.statusMessage
                        if (uiState.isProcessing && statusMsg != null) {
                            item {
                                ThinkingBubble(statusMessage = statusMsg)
                            }
                        }
                    }
                }
            }

            // Input Area
            ChatInput(
                textInput = textInput,
                voiceState = voiceState,
                onValueChange = { textInput = it },
                onSendClick = {
                    viewModel.sendMessage(textInput)
                    textInput = ""
                    voiceState = VoiceRecognitionState.Idle
                },
                onMicClick = {
                    // Already listening? stop. Otherwise request permission then start.
                    if (voiceState is VoiceRecognitionState.Listening ||
                        voiceState is VoiceRecognitionState.ReadyForSpeech ||
                        voiceState is VoiceRecognitionState.Partial
                    ) {
                        manager.stopListening()
                        voiceState = VoiceRecognitionState.Idle
                    } else {
                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                },
                isProcessing = uiState.isProcessing
            )
        }
    }
}

// ─── Chat Header ────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatHeader(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(modifier = Modifier.size(36.dp)) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxSize()
                        )
                    }
                    // Online indicator pulse
                    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                    val pulseAlpha by infiniteTransition.animateFloat(
                        initialValue = 0.4f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1000, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "pulse"
                    )
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .align(Alignment.BottomEnd)
                            .border(1.5.dp, MaterialTheme.colorScheme.surface, CircleShape)
                            .background(
                                Color(0xFF4CAF50).copy(alpha = pulseAlpha),
                                CircleShape
                            )
                    )
                }
                Column {
                    Text(
                        text = "Carto AI",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Your smart shopping assistant",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        ),
        modifier = modifier.shadow(2.dp)
    )
}

// ─── Empty State Welcome Screen ─────────────────────────────────────────────

@Composable
fun EmptyState(
    onSuggestionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "👋",
            fontSize = 56.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = "Welcome to Carto AI",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Ask me anything about products, fashion, clothing, outfits, comparisons, or shopping advice!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 32.dp)
        )

        Text(
            text = "Try asking:",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 12.dp)
        )

        val suggestions = listOf(
            "What should I wear today? 👕",
            "Show best running shoes 👟",
            "Compare Nike vs Adidas shoes ⚔️",
            "Show my cart details 🛒"
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            suggestions.forEach { suggestion ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSuggestionClick(suggestion) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = suggestion,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

// ─── Message Bubble Redesign ────────────────────────────────────────────────

@Composable
fun MessageBubble(
    message: ChatMessage,
    currency: com.shopify.carto.feature.settings.domain.model.Currency,
    isLastAiMessage: Boolean,
    onProductClick: (Long) -> Unit,
    onFavoriteClick: (SearchProduct) -> Unit,
    onAddToCartClick: (SearchProduct) -> Unit,
    onRegenerateClick: () -> Unit,
    snackbarHostState: SnackbarHostState,
    context: Context,
    modifier: Modifier = Modifier
) {
    val isUser = message.isUser
    val alignment = if (isUser) Alignment.End else Alignment.Start
    val shape = if (isUser) {
        RoundedCornerShape(20.dp, 20.dp, 4.dp, 20.dp)
    } else {
        RoundedCornerShape(20.dp, 20.dp, 20.dp, 4.dp)
    }
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
            verticalAlignment = Alignment.Top
        ) {
            if (!isUser) {
                // AI icon avatar next to the message bubble
                Surface(
                    modifier = Modifier
                        .padding(end = 8.dp, top = 4.dp)
                        .size(28.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(6.dp)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f, fill = false)) {
                if (message.type == MessageType.ERROR) {
                    ErrorCard(
                        errorMessage = message.text,
                        onRetryClick = onRegenerateClick
                    )
                } else {
                    Surface(
                        color = if (isUser) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        },
                        shape = shape,
                        border = if (isUser) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                        modifier = Modifier.shadow(1.dp, shape)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            MarkdownRenderer(
                                text = message.text,
                                color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Action Row beneath bubbles
                CopyActionRow(
                    message = message,
                    showRegenerate = isLastAiMessage && message.type != MessageType.ERROR,
                    onCopyClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Carto AI Message", message.text)
                        clipboard.setPrimaryClip(clip)
                        scope.launch {
                            snackbarHostState.showSnackbar("Copied")
                        }
                    },
                    onShareClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Carto AI Message", message.text)
                        clipboard.setPrimaryClip(clip)
                        scope.launch {
                            snackbarHostState.showSnackbar("Copied to clipboard for sharing!")
                        }
                    },
                    onRegenerateClick = onRegenerateClick
                )

                // Recommended Product cards Carousel / Grid layout
                if (message.products.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    if (message.products.size <= 2) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            message.products.forEach { product ->
                                Box(modifier = Modifier.weight(1f)) {
                                    ProductChatCardWrapper(
                                        product = product,
                                        currency = currency,
                                        onClick = { onProductClick(product.id) },
                                        onFavoriteClick = { onFavoriteClick(product) },
                                        onAddToCartClick = { onAddToCartClick(product) }
                                    )
                                }
                            }
                        }
                    } else {
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp)
                        ) {
                            items(message.products, key = { it.id }) { product ->
                                ProductChatCardWrapper(
                                    product = product,
                                    currency = currency,
                                    onClick = { onProductClick(product.id) },
                                    onFavoriteClick = { onFavoriteClick(product) },
                                    onAddToCartClick = { onAddToCartClick(product) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─── Product Card Reusable Wrapper ───────────────────────────────────────────

@Composable
fun ProductChatCardWrapper(
    product: SearchProduct,
    currency: com.shopify.carto.feature.settings.domain.model.Currency,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onAddToCartClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val homeProduct = remember(product) {
        Product(
            id = product.id,
            name = product.title,
            price = product.price,
            compareAtPrice = product.compareAtPrice,
            imageUrl = product.imageUrl,
            imageCount = 1,
            vendor = product.vendor,
            productType = product.productType,
            variantCount = 1,
            totalStock = 10,
            createdAt = "",
            isNew = false,
            isOnSale = product.compareAtPrice != null && product.compareAtPrice > product.price,
            isLowStock = false
        )
    }

    Box(modifier = modifier.width(160.dp)) {
        ProductCard(
            product = homeProduct,
            currency = currency,
            onClick = { onClick() },
            onFavoriteClick = { onFavoriteClick() }
        )

        // Cart overlay button
        IconButton(
            onClick = onAddToCartClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp)
                .size(32.dp)
                .background(MaterialTheme.colorScheme.primary, CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = "Add to Cart",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

// ─── Copy Action Row ────────────────────────────────────────────────────────

@Composable
fun CopyActionRow(
    message: ChatMessage,
    showRegenerate: Boolean,
    onCopyClick: () -> Unit,
    onShareClick: () -> Unit,
    onRegenerateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onCopyClick,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ContentCopy,
                contentDescription = "Copy message",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(12.dp)
            )
        }

        IconButton(
            onClick = onShareClick,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "Share message",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(12.dp)
            )
        }

        if (showRegenerate) {
            IconButton(
                onClick = onRegenerateClick,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Regenerate message",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}

// ─── Chat Input Redesign ────────────────────────────────────────────────────

/**
 * WhatsApp-style input bar:
 * - Text field empty  →  mic button (start voice input)
 * - Text field has content OR processing  →  send button
 * - Voice active  →  placeholder shows listening state, trailing button = stop (■)
 *
 * Button morphs with [AnimatedContent] (fade cross-dissolve, 180 ms) — compose-animations rule:
 * "swap between different composable content → AnimatedContent".
 */
@Composable
fun ChatInput(
    textInput: String,
    voiceState: VoiceRecognitionState,
    onValueChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onMicClick: () -> Unit,
    isProcessing: Boolean,
    modifier: Modifier = Modifier
) {
    val isListening = voiceState is VoiceRecognitionState.ReadyForSpeech
        || voiceState is VoiceRecognitionState.Listening
        || voiceState is VoiceRecognitionState.Partial

    // Derive which trailing action to show:
    //  hasText or isProcessing → send  |  else → mic/stop
    val hasText = textInput.isNotBlank()
    val showSend = hasText && !isListening
    val showStop = isListening
    // "stop" or "mic" share the same slot; send occupies it when text is present

    // Waveform-like pulse scale for the mic button while listening
    val infiniteTransition = rememberInfiniteTransition(label = "micPulse")
    val micScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.20f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "micScale"
    )
    // Only use the pulse scale when actually listening
    val trailingScale = if (isListening) micScale else 1f

    Surface(
        tonalElevation = 4.dp,
        shadowElevation = 8.dp,
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column {
            // Live transcription hint strip (shown while voice active)
            AnimatedVisibility(
                visible = isListening,
                enter = fadeIn(tween(200)) + expandVertically(),
                exit  = fadeOut(tween(200)) + shrinkVertically()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f))
                        .padding(horizontal = 20.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Animated dots to signal active listening
                    val dotTransition = rememberInfiniteTransition(label = "listenDots")
                    val dot1 by dotTransition.animateFloat(
                        0.3f, 1f,
                        infiniteRepeatable(tween(500, delayMillis = 0), RepeatMode.Reverse),
                        label = "d1"
                    )
                    val dot2 by dotTransition.animateFloat(
                        0.3f, 1f,
                        infiniteRepeatable(tween(500, delayMillis = 150), RepeatMode.Reverse),
                        label = "d2"
                    )
                    val dot3 by dotTransition.animateFloat(
                        0.3f, 1f,
                        infiniteRepeatable(tween(500, delayMillis = 300), RepeatMode.Reverse),
                        label = "d3"
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                        listOf(dot1, dot2, dot3).forEach { alpha ->
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(
                                        MaterialTheme.colorScheme.primary.copy(alpha = alpha),
                                        CircleShape
                                    )
                            )
                        }
                    }
                    Text(
                        text = if (textInput.isNotBlank()) textInput else "Listening…",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Attachment button (left side)
                IconButton(
                    onClick = { /* Placeholder attachment */ },
                    modifier = Modifier.size(36.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add attachment"
                    )
                }

                // Text field — placeholder adapts when listening
                OutlinedTextField(
                    value = textInput,
                    onValueChange = onValueChange,
                    placeholder = {
                        Text(
                            text = when {
                                isListening -> "🎙 Listening…"
                                else        -> "Message Carto AI…"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    },
                    modifier = Modifier.weight(1f),
                    maxLines = 5,
                    shape = RoundedCornerShape(24.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (textInput.isNotBlank() && !isProcessing) onSendClick()
                        }
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (isListening)
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        else
                            Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    )
                )

                // ── Trailing action button: Mic | Stop | Send ────────────────
                // Three visual states keyed to a simple enum so AnimatedContent
                // only transitions on shape-change, not data-change.
                val trailingKey = when {
                    showSend -> "send"
                    showStop -> "stop"
                    else     -> "mic"
                }
                AnimatedContent(
                    targetState = trailingKey,
                    transitionSpec = {
                        (fadeIn(tween(180)) + scaleIn(tween(180), initialScale = 0.75f))
                            .togetherWith(fadeOut(tween(120)) + scaleOut(tween(120), targetScale = 0.75f))
                    },
                    contentKey = { it }, // shape-based key per compose-animations rule
                    label = "trailingButton"
                ) { key ->
                    when (key) {
                        "send" -> FilledIconButton(
                            onClick = { if (!isProcessing) onSendClick() },
                            enabled = !isProcessing,
                            modifier = Modifier.size(40.dp),
                            shape = CircleShape,
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send message",
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        "stop" -> FilledIconButton(
                            onClick = onMicClick, // stops listening
                            modifier = Modifier
                                .size(40.dp)
                                // scale animated via trailingScale (pulse while listening)
                                .graphicsLayer { scaleX = trailingScale; scaleY = trailingScale },
                            shape = CircleShape,
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Stop,
                                contentDescription = "Stop listening",
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        else -> FilledIconButton(
                            onClick = onMicClick, // requests permission + starts listening
                            modifier = Modifier.size(40.dp),
                            shape = CircleShape,
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Start voice input",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─── Thinking Bubble Redesign ───────────────────────────────────────────────

@Composable
fun ThinkingBubble(
    statusMessage: String
) {
    val infiniteTransition = rememberInfiniteTransition(label = "thinking")
    val dot1Alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = 0, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot1"
    )
    val dot2Alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = 200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot2"
    )
    val dot3Alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = 400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot3"
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxWidth()
        ) {
            Surface(
                modifier = Modifier
                    .padding(end = 8.dp, top = 4.dp)
                    .size(28.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(6.dp)
                )
            }

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                shape = RoundedCornerShape(20.dp, 20.dp, 20.dp, 4.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                modifier = Modifier.shadow(1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(bottom = 6.dp)
                    ) {
                        Text(
                            text = statusMessage,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = dot1Alpha), CircleShape)
                        )
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = dot2Alpha), CircleShape)
                        )
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = dot3Alpha), CircleShape)
                        )
                    }
                }
            }
        }
    }
}

// ─── Error Card ─────────────────────────────────────────────────────────────

@Composable
fun ErrorCard(
    errorMessage: String,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Error",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                textAlign = TextAlign.Center
            )
            Button(
                onClick = onRetryClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text(
                    text = "Retry",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onError
                )
            }
        }
    }
}

// ─── Markdown / Custom Text Segment Renderers ─────────────────────────────────

@Composable
fun MarkdownRenderer(
    text: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    val segments = remember(text) { parseMessageSegments(text) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        segments.forEach { segment ->
            when (segment) {
                is ChatSegment.Table -> {
                    ComparisonTable(headers = segment.headers, rows = segment.rows)
                }
                is ChatSegment.CodeBlock -> {
                    CodeBlock(language = segment.language, code = segment.code)
                }
                is ChatSegment.Text -> {
                    TextSegmentRenderer(text = segment.content, color = color)
                }
            }
        }
    }
}

@Composable
fun TextSegmentRenderer(
    text: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    val lines = text.split("\n")
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        lines.forEach { line ->
            val trimmedLine = line.trim()
            if (trimmedLine.isBlank()) return@forEach

            // Remove markdown separators
            if (trimmedLine == "---" || trimmedLine == "***") {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )
                return@forEach
            }

            // Headings
            if (trimmedLine.startsWith("### ")) {
                Text(
                    text = parseMarkdownInline(trimmedLine.substring(4).trim()),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = color,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
            } else if (trimmedLine.startsWith("## ")) {
                Text(
                    text = parseMarkdownInline(trimmedLine.substring(3).trim()),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = color,
                    modifier = Modifier.padding(top = 12.dp, bottom = 6.dp)
                )
            } else if (trimmedLine.startsWith("# ")) {
                Text(
                    text = parseMarkdownInline(trimmedLine.substring(2).trim()),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = color,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
            }
            // Blockquotes
            else if (trimmedLine.startsWith("> ")) {
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    shape = RoundedCornerShape(4.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = parseMarkdownInline(trimmedLine.substring(2).trim()),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            // Bullet list
            else if (trimmedLine.startsWith("-") || trimmedLine.startsWith("*")) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, top = 2.dp, bottom = 2.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "• ",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                    Text(
                        text = parseMarkdownInline(trimmedLine.substring(1).trim()),
                        style = MaterialTheme.typography.bodyMedium,
                        color = color,
                        lineHeight = 20.sp
                    )
                }
            }
            // Numbered list
            else if (trimmedLine.firstOrNull()?.isDigit() == true && trimmedLine.contains(". ")) {
                val dotIndex = trimmedLine.indexOf(". ")
                if (dotIndex in 1..4) {
                    val numPrefix = trimmedLine.substring(0, dotIndex + 2)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp, top = 2.dp, bottom = 2.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = numPrefix,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = color
                        )
                        Text(
                            text = parseMarkdownInline(trimmedLine.substring(dotIndex + 2).trim()),
                            style = MaterialTheme.typography.bodyMedium,
                            color = color,
                            lineHeight = 20.sp
                        )
                    }
                    return@forEach
                }

                Text(
                    text = parseMarkdownInline(line),
                    style = MaterialTheme.typography.bodyMedium,
                    color = color,
                    lineHeight = 20.sp
                )
            }
            // Paragraph
            else {
                Text(
                    text = parseMarkdownInline(line),
                    style = MaterialTheme.typography.bodyMedium,
                    color = color,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

private fun parseMarkdownInline(text: String): AnnotatedString {
    return buildAnnotatedString {
        var cursor = 0
        val regex = Regex("""(\*\*.*?\*\*|\*.*?\*|`.*?`)""")
        val matches = regex.findAll(text).toList()

        for (match in matches) {
            val range = match.groups[0]!!.range
            if (range.first > cursor) {
                append(text.substring(cursor, range.first))
            }
            val matchValue = match.groups[0]!!.value

            when {
                matchValue.startsWith("**") && matchValue.endsWith("**") -> {
                    val content = matchValue.substring(2, matchValue.length - 2)
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(content)
                    }
                }
                matchValue.startsWith("*") && matchValue.endsWith("*") -> {
                    val content = matchValue.substring(1, matchValue.length - 1)
                    withStyle(style = SpanStyle(fontStyle = FontStyle.Italic)) {
                        append(content)
                    }
                }
                matchValue.startsWith("`") && matchValue.endsWith("`") -> {
                    val content = matchValue.substring(1, matchValue.length - 1)
                    withStyle(
                        style = SpanStyle(
                            fontFamily = FontFamily.Monospace,
                            background = Color.LightGray.copy(alpha = 0.2f),
                            color = Color(0xFFC7254E)
                        )
                    ) {
                        append(content)
                    }
                }
            }
            cursor = range.last + 1
        }

        if (cursor < text.length) {
            append(text.substring(cursor))
        }
    }
}

// ─── Comparison Table ───────────────────────────────────────────────────────

@Composable
fun ComparisonTable(
    headers: List<String>,
    rows: List<List<String>>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Box(modifier = Modifier.horizontalScroll(rememberScrollState())) {
            Column(modifier = Modifier.padding(12.dp)) {
                // Table Header Row
                Row(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(vertical = 10.dp, horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    headers.forEach { header ->
                        Text(
                            text = header,
                            modifier = Modifier.widthIn(min = 90.dp, max = 160.dp),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Table Data Rows
                rows.forEachIndexed { index, row ->
                    val rowBg = if (index % 2 == 0) {
                        Color.Transparent
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(rowBg, RoundedCornerShape(6.dp))
                            .padding(vertical = 8.dp, horizontal = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        row.forEach { cell ->
                            Text(
                                text = cell,
                                modifier = Modifier.widthIn(min = 90.dp, max = 160.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─── Code Block ─────────────────────────────────────────────────────────────

@Composable
fun CodeBlock(
    language: String,
    code: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1E1E)
        )
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF2D2D2D))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = language.ifBlank { "code" }.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.LightGray,
                    fontWeight = FontWeight.Bold
                )

                val context = LocalContext.current
                val clipboard = remember { context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager }

                Text(
                    text = "Copy",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Cyan,
                    modifier = Modifier
                        .clickable {
                            val clip = ClipData.newPlainText("Code Block", code)
                            clipboard.setPrimaryClip(clip)
                        }
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }

            Text(
                text = code,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .horizontalScroll(rememberScrollState()),
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.Monospace,
                    color = Color.White
                )
            )
        }
    }
}

// ─── Parser Helper Segments ─────────────────────────────────────────────────

sealed class ChatSegment {
    data class Text(val content: String) : ChatSegment()
    data class Table(val headers: List<String>, val rows: List<List<String>>) : ChatSegment()
    data class CodeBlock(val language: String, val code: String) : ChatSegment()
}

fun parseMessageSegments(text: String): List<ChatSegment> {
    val segments = mutableListOf<ChatSegment>()
    val lines = text.split("\n")

    var mode = "TEXT"
    val currentTableLines = mutableListOf<String>()
    val currentTextLines = mutableListOf<String>()
    val currentCodeLines = mutableListOf<String>()
    var codeLanguage = ""

    fun flushText() {
        if (currentTextLines.isNotEmpty()) {
            segments.add(ChatSegment.Text(currentTextLines.joinToString("\n")))
            currentTextLines.clear()
        }
    }

    fun flushTable() {
        if (currentTableLines.isNotEmpty()) {
            val tableLines = currentTableLines.toList()
            currentTableLines.clear()

            val parsedHeaders = mutableListOf<String>()
            val parsedRows = mutableListOf<List<String>>()

            tableLines.forEachIndexed { index, line ->
                val cells = line.split("|")
                    .map { it.trim() }
                    .filterIndexed { i, _ -> i > 0 && i < line.split("|").lastIndex }

                if (index == 0) {
                    parsedHeaders.addAll(cells)
                } else if (line.contains("---") || line.contains("- -")) {
                    // ignore separator
                } else {
                    parsedRows.add(cells)
                }
            }

            if (parsedHeaders.isNotEmpty()) {
                segments.add(ChatSegment.Table(parsedHeaders, parsedRows))
            } else {
                segments.add(ChatSegment.Text(tableLines.joinToString("\n")))
            }
        }
    }

    fun flushCode() {
        if (currentCodeLines.isNotEmpty()) {
            segments.add(ChatSegment.CodeBlock(codeLanguage, currentCodeLines.joinToString("\n")))
            currentCodeLines.clear()
            codeLanguage = ""
        }
    }

    for (line in lines) {
        val trimmed = line.trim()
        val isCodeBoundary = trimmed.startsWith("```")
        val isTableLine = trimmed.startsWith("|") && trimmed.endsWith("|") && trimmed.count { it == '|' } >= 2

        when (mode) {
            "CODE" -> {
                if (isCodeBoundary) {
                    flushCode()
                    mode = "TEXT"
                } else {
                    currentCodeLines.add(line)
                }
            }
            "TABLE" -> {
                if (isTableLine) {
                    currentTableLines.add(line)
                } else {
                    flushTable()
                    if (isCodeBoundary) {
                        codeLanguage = trimmed.substring(3).trim()
                        mode = "CODE"
                    } else {
                        currentTextLines.add(line)
                        mode = "TEXT"
                    }
                }
            }
            else -> {
                if (isCodeBoundary) {
                    flushText()
                    codeLanguage = trimmed.substring(3).trim()
                    mode = "CODE"
                } else if (isTableLine) {
                    flushText()
                    currentTableLines.add(line)
                    mode = "TABLE"
                } else {
                    currentTextLines.add(line)
                }
            }
        }
    }

    when (mode) {
        "CODE" -> flushCode()
        "TABLE" -> flushTable()
        else -> flushText()
    }

    return segments
}
