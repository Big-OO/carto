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
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
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

// ─── Screen ──────────────────────────────────────────────────────────────────

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
    // Accumulated rms amplitudes for waveform preview while recording
    val rmsHistory = remember { mutableStateListOf<Float>() }

    // ── SpeechRecognizerManager — DisposableEffect: correct per compose-side-effects ──
    val manager = remember {
        SpeechRecognizerManager(context) { state ->
            voiceState = state
            if (state is VoiceRecognitionState.Listening) {
                rmsHistory.add(state.rmsDb.coerceIn(0f, 12f))
                if (rmsHistory.size > 40) rmsHistory.removeFirst()
            }
        }
    }
    DisposableEffect(Unit) {
        onDispose { manager.destroy() }
    }

    // Clear rms on idle
    LaunchedEffect(voiceState) {
        when (val s = voiceState) {
            is VoiceRecognitionState.Partial -> textInput = s.text
            is VoiceRecognitionState.Error   -> {
                voiceState = VoiceRecognitionState.Idle
                rmsHistory.clear()
            }
            is VoiceRecognitionState.Result  -> {
                if (s.text.isNotBlank()) {
                    viewModel.sendMessage(s.text, isVoice = true)
                    textInput = ""
                    rmsHistory.clear()
                }
                voiceState = VoiceRecognitionState.Idle
            }
            else -> Unit
        }
    }

    // ── Runtime permission ────────────────────────────────────────────────────
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            voiceState = VoiceRecognitionState.ReadyForSpeech
            rmsHistory.clear()
            manager.startListening()
        } else {
            scope.launch { snackbarHostState.showSnackbar("Microphone permission required") }
        }
    }

    // Auto-scroll to latest message
    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    val isListening = voiceState is VoiceRecognitionState.ReadyForSpeech
        || voiceState is VoiceRecognitionState.Listening
        || voiceState is VoiceRecognitionState.Partial

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = { ChatHeader(onBackClick = onBackClick) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // ── Chat messages area ────────────────────────────────────────────
            Box(modifier = Modifier.weight(1f)) {
                if (uiState.messages.isEmpty() ||
                    (uiState.messages.size == 1 && uiState.messages.first().text.isBlank())
                ) {
                    EmptyState(onSuggestionClick = { viewModel.sendMessage(it) })
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
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

                        // Thinking indicator — no border, clean
                        val statusMsg = uiState.statusMessage
                        if (uiState.isProcessing && statusMsg != null) {
                            item { ThinkingBubble(statusMessage = statusMsg) }
                        }
                    }
                }
            }

            // ── Recording overlay (WhatsApp-style, shown while holding mic) ──
            AnimatedVisibility(
                visible = isListening,
                enter = fadeIn(tween(150)) + expandVertically(tween(150)),
                exit  = fadeOut(tween(150)) + shrinkVertically(tween(150))
            ) {
                VoiceRecordingBar(
                    rmsHistory = rmsHistory.toList(),
                    onCancelClick = {
                        manager.stopListening()
                        voiceState = VoiceRecognitionState.Idle
                        textInput = ""
                        rmsHistory.clear()
                    }
                )
            }

            // ── Input bar ─────────────────────────────────────────────────────
            ChatInput(
                textInput = textInput,
                isListening = isListening,
                isProcessing = uiState.isProcessing,
                onValueChange = { textInput = it },
                onSendClick = {
                    if (textInput.isNotBlank()) {
                        viewModel.sendMessage(textInput)
                        textInput = ""
                    }
                },
                onMicPressStart = {
                    // Hold started → request permission or start immediately
                    if (voiceState == VoiceRecognitionState.Idle) {
                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                },
                onMicPressEnd = {
                    // Finger lifted → stop & auto-send (result dispatched via LaunchedEffect)
                    if (isListening) manager.stopListening()
                },
                onMicCancel = {
                    manager.stopListening()
                    voiceState = VoiceRecognitionState.Idle
                    textInput = ""
                    rmsHistory.clear()
                }
            )
        }
    }
}

// ─── Chat Header ──────────────────────────────────────────────────────────────

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
                    val pulseTransition = rememberInfiniteTransition(label = "pulse")
                    val pulseAlpha by pulseTransition.animateFloat(
                        initialValue = 0.4f, targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            tween(1000, easing = FastOutSlowInEasing), RepeatMode.Reverse
                        ),
                        label = "pulseAlpha"
                    )
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .align(Alignment.BottomEnd)
                            .border(1.5.dp, MaterialTheme.colorScheme.surface, CircleShape)
                            .background(Color(0xFF4CAF50).copy(alpha = pulseAlpha), CircleShape)
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

// ─── Empty State ──────────────────────────────────────────────────────────────

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
        Text(text = "👋", fontSize = 56.sp, modifier = Modifier.padding(bottom = 16.dp))
        Text(
            text = "Welcome to Carto AI",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Ask me anything about products, fashion, outfits, comparisons, or shopping advice!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 0.dp).padding(bottom = 32.dp)
        )
        Text(
            text = "Try asking:",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.Start).padding(bottom = 12.dp)
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            listOf(
                "What should I wear today? 👕",
                "Show best running shoes 👟",
                "Compare Nike vs Adidas ⚔️",
                "Show my cart details 🛒"
            ).forEach { suggestion ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { onSuggestionClick(suggestion) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
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

// ─── Message Bubble ───────────────────────────────────────────────────────────

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
    val scope = rememberCoroutineScope()
    val userShape = RoundedCornerShape(20.dp, 20.dp, 4.dp, 20.dp)
    val aiShape   = RoundedCornerShape(4.dp, 20.dp, 20.dp, 20.dp)
    val hasProducts = message.products.isNotEmpty()

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
                                            text = message.text,
                                            color = if (isUser) Color.White
                                                    else MaterialTheme.colorScheme.onSurface
                                        )
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
                                cb.setPrimaryClip(ClipData.newPlainText("Carto AI", message.text))
                                scope.launch { snackbarHostState.showSnackbar("Copied") }
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
                onProductClick = onProductClick,
                onFavoriteClick = onFavoriteClick,
                onAddToCartClick = onAddToCartClick
            )
        }
    }
}

// ─── Voice Message Bubble ──────────────────────────────────────────────────────

@Composable
fun VoiceMessageBubble(
    modifier: Modifier = Modifier
) {
    // Static decorative waveform (real rmsHistory not stored per-message for simplicity)
    val barHeights = remember {
        listOf(0.3f, 0.6f, 0.9f, 0.5f, 0.8f, 0.4f, 1.0f, 0.6f, 0.3f, 0.7f,
               0.5f, 0.9f, 0.4f, 0.8f, 0.6f, 0.3f, 0.7f, 1.0f, 0.5f, 0.4f)
    }
    Surface(
        color = MaterialTheme.colorScheme.primary,
        shape = RoundedCornerShape(20.dp, 20.dp, 4.dp, 20.dp),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = "Voice message",
                tint = Color.White.copy(alpha = 0.9f),
                modifier = Modifier.size(16.dp)
            )
            // Waveform bars
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                barHeights.forEach { h ->
                    Box(
                        modifier = Modifier
                            .width(3.dp)
                            .height((h * 20).dp + 4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color.White.copy(alpha = 0.85f))
                    )
                }
            }
        }
    }
}

// ─── Products Carousel ─────────────────────────────────────────────────────────

@Composable
fun ProductsCarousel(
    products: List<SearchProduct>,
    currency: com.shopify.carto.feature.settings.domain.model.Currency,
    onProductClick: (Long) -> Unit,
    onFavoriteClick: (SearchProduct) -> Unit,
    onAddToCartClick: (SearchProduct) -> Unit,
    modifier: Modifier = Modifier
) {
    // Always use LazyRow — single product gets full width card, multiple scroll
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
    ) {
        items(products, key = { it.id }) { product ->
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

// ─── Product Card Wrapper ──────────────────────────────────────────────────────

@Composable
fun ProductChatCardWrapper(
    product: SearchProduct,
    currency: com.shopify.carto.feature.settings.domain.model.Currency,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onAddToCartClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val homeProduct = remember(product.id) {
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

    Box(modifier = modifier.width(168.dp)) {
        ProductCard(
            product = homeProduct,
            currency = currency,
            onClick = { _ -> onClick() },
            onFavoriteClick = { _ -> onFavoriteClick() }
        )
        // Cart overlay chip
        FilledIconButton(
            onClick = onAddToCartClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(6.dp)
                .size(30.dp),
            shape = CircleShape,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = "Add to Cart",
                modifier = Modifier.size(15.dp)
            )
        }
    }
}

// ─── Copy Action Row (no Share) ────────────────────────────────────────────────

@Composable
fun CopyActionRow(
    showRegenerate: Boolean,
    onCopyClick: () -> Unit,
    onRegenerateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(top = 2.dp, start = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onCopyClick, modifier = Modifier.size(28.dp)) {
            Icon(
                imageVector = Icons.Default.ContentCopy,
                contentDescription = "Copy",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f),
                modifier = Modifier.size(13.dp)
            )
        }
        if (showRegenerate) {
            IconButton(onClick = onRegenerateClick, modifier = Modifier.size(28.dp)) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Regenerate",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f),
                    modifier = Modifier.size(13.dp)
                )
            }
        }
    }
}

// ─── Voice Recording Bar (WhatsApp-style overlay above input) ─────────────────

@Composable
fun VoiceRecordingBar(
    rmsHistory: List<Float>,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "recPulse")
    val recAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(600), RepeatMode.Reverse),
        label = "recAlpha"
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.95f),
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Pulsing red record dot
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(Color(0xFFE53935).copy(alpha = recAlpha), CircleShape)
            )
            Text(
                text = "Recording…",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Medium
            )
            // Live waveform bars from rmsHistory
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val displayBars = if (rmsHistory.isEmpty()) List(20) { 0.2f } else rmsHistory
                displayBars.takeLast(24).forEach { rms ->
                    val normalised = (rms / 12f).coerceIn(0.05f, 1f)
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height((normalised * 28).dp + 4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.75f))
                    )
                }
            }
            // Cancel button
            TextButton(
                onClick = onCancelClick,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Cancel",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

// ─── Chat Input ───────────────────────────────────────────────────────────────

/**
 * WhatsApp-style input:
 * - No text typed    → mic button (hold to record, release = send)
 * - Text in field    → send button
 * - While recording  → VoiceRecordingBar shown above (see AIChatScreen)
 *
 * Uses [AnimatedContent] per compose-animations "swap composable content" rule.
 * Mic uses [pointerInput] detectTapGestures(onPress) for press-and-hold semantics.
 */
@Composable
fun ChatInput(
    textInput: String,
    isListening: Boolean,
    isProcessing: Boolean,
    onValueChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onMicPressStart: () -> Unit,
    onMicPressEnd: () -> Unit,
    onMicCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hasText = textInput.isNotBlank()
    val trailingKey = when {
        hasText      -> "send"
        isListening  -> "recording"
        else         -> "mic"
    }

    Surface(
        tonalElevation = 4.dp,
        shadowElevation = 8.dp,
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Text field — full width minus trailing button
            OutlinedTextField(
                value = textInput,
                onValueChange = onValueChange,
                placeholder = {
                    Text(
                        text = if (isListening) "🎙 Listening…" else "Message Carto AI…",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                },
                modifier = Modifier.weight(1f),
                maxLines = 5,
                shape = RoundedCornerShape(24.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(
                    onSend = { if (textInput.isNotBlank() && !isProcessing) onSendClick() }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                )
            )

            // Trailing animated button — Send | Mic | Recording indicator
            AnimatedContent(
                targetState = trailingKey,
                transitionSpec = {
                    (fadeIn(tween(160)) + scaleIn(tween(160), initialScale = 0.7f))
                        .togetherWith(fadeOut(tween(100)) + scaleOut(tween(100), targetScale = 0.7f))
                },
                contentKey = { it },
                label = "trailingBtn"
            ) { key ->
                when (key) {
                    "send" -> FilledIconButton(
                        onClick = { if (!isProcessing) onSendClick() },
                        enabled = !isProcessing,
                        modifier = Modifier.size(42.dp),
                        shape = CircleShape,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    "recording" -> {
                        // While actively recording, show an animated red mic (pulsing)
                        val recTransition = rememberInfiniteTransition(label = "micActive")
                        val recScale by recTransition.animateFloat(
                            1f, 1.18f,
                            infiniteRepeatable(tween(450), RepeatMode.Reverse),
                            label = "recScale"
                        )
                        FilledIconButton(
                            onClick = onMicPressEnd,
                            modifier = Modifier
                                .size(42.dp)
                                .graphicsLayer { scaleX = recScale; scaleY = recScale },
                            shape = CircleShape,
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Stop,
                                contentDescription = "Stop recording",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    else -> {
                        // Press-and-hold mic: detectTapGestures(onPress) gives us press/release
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.secondaryContainer)
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onPress = {
                                            onMicPressStart()
                                            // Suspend until finger lifts; tryAwaitRelease = true if no cancel
                                            val released = tryAwaitRelease()
                                            if (released) {
                                                onMicPressEnd()
                                            } else {
                                                onMicCancel()
                                            }
                                        }
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Hold to record",
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─── Thinking Bubble (clean, no border) ───────────────────────────────────────

@Composable
fun ThinkingBubble(
    statusMessage: String,
    modifier: Modifier = Modifier
) {
    val t = rememberInfiniteTransition(label = "think")
    val d1 by t.animateFloat(0.2f, 1f, infiniteRepeatable(tween(500, delayMillis = 0), RepeatMode.Reverse), "d1")
    val d2 by t.animateFloat(0.2f, 1f, infiniteRepeatable(tween(500, delayMillis = 160), RepeatMode.Reverse), "d2")
    val d3 by t.animateFloat(0.2f, 1f, infiniteRepeatable(tween(500, delayMillis = 320), RepeatMode.Reverse), "d3")

    Row(
        verticalAlignment = Alignment.Bottom,
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        // AI avatar
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

        // Bubble — no border, no shadow
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(4.dp, 20.dp, 20.dp, 20.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                Text(
                    text = statusMessage,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf(d1, d2, d3).forEach { alpha ->
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .background(
                                    MaterialTheme.colorScheme.primary.copy(alpha = alpha),
                                    CircleShape
                                )
                        )
                    }
                }
            }
        }
    }
}

// ─── Error Card ───────────────────────────────────────────────────────────────

@Composable
fun ErrorCard(
    errorMessage: String,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(16.dp)
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
                modifier = Modifier.size(22.dp)
            )
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onErrorContainer,
                textAlign = TextAlign.Center,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
            Button(
                onClick = onRetryClick,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 6.dp)
            ) {
                Text("Retry", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onError)
            }
        }
    }
}

// ─── Markdown Renderer ────────────────────────────────────────────────────────

@Composable
fun MarkdownRenderer(
    text: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    val segments = remember(text) { parseMessageSegments(text) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        segments.forEach { segment ->
            when (segment) {
                is ChatSegment.Table     -> ComparisonTable(headers = segment.headers, rows = segment.rows)
                is ChatSegment.CodeBlock -> CodeBlock(language = segment.language, code = segment.code)
                is ChatSegment.Text     -> TextSegmentRenderer(text = segment.content, color = color)
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
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        lines.forEach { line ->
            val trimmed = line.trim()
            if (trimmed.isBlank()) return@forEach

            when {
                trimmed == "---" || trimmed == "***" -> HorizontalDivider(
                    modifier = Modifier.padding(vertical = 6.dp),
                    color = color.copy(alpha = 0.25f)
                )
                trimmed.startsWith("### ") -> Text(
                    text = trimmed.removePrefix("### "),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = color,
                    modifier = Modifier.padding(top = 4.dp)
                )
                trimmed.startsWith("## ") -> Text(
                    text = trimmed.removePrefix("## "),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = color,
                    modifier = Modifier.padding(top = 6.dp)
                )
                trimmed.startsWith("# ") -> Text(
                    text = trimmed.removePrefix("# "),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = color,
                    modifier = Modifier.padding(top = 8.dp)
                )
                trimmed.startsWith("> ") -> Surface(
                    color = color.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                        Box(
                            modifier = Modifier
                                .width(3.dp)
                                .height(20.dp)
                                .background(color.copy(alpha = 0.4f), RoundedCornerShape(2.dp))
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = buildInlineStyledText(trimmed.removePrefix("> "), color),
                            style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                            color = color.copy(alpha = 0.85f)
                        )
                    }
                }
                trimmed.matches(Regex("^[-*] .+")) -> Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(text = "•", color = color.copy(alpha = 0.8f), style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 1.dp))
                    Text(
                        text = buildInlineStyledText(trimmed.removePrefix("- ").removePrefix("* "), color),
                        style = MaterialTheme.typography.bodyMedium,
                        color = color
                    )
                }
                trimmed.matches(Regex("^\\d+\\. .+")) -> {
                    val dotIdx = trimmed.indexOf(". ")
                    val num = trimmed.substring(0, dotIdx)
                    val content = trimmed.substring(dotIdx + 2)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(text = "$num.", color = color.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.width(22.dp))
                        Text(
                            text = buildInlineStyledText(content, color),
                            style = MaterialTheme.typography.bodyMedium,
                            color = color
                        )
                    }
                }
                else -> Text(
                    text = buildInlineStyledText(trimmed, color),
                    style = MaterialTheme.typography.bodyMedium,
                    color = color
                )
            }
        }
    }
}

// Build inline AnnotatedString with bold, italic, and code spans
private fun buildInlineStyledText(text: String, defaultColor: Color): AnnotatedString {
    // Clean any stray markdown not handled elsewhere
    val cleaned = text
        .replace(Regex("^#{1,6}\\s+"), "")
        .trim()

    return buildAnnotatedString {
        val pattern = Regex("""\*\*(.+?)\*\*|\*(.+?)\*|`(.+?)`""")
        var cursor = 0
        pattern.findAll(cleaned).forEach { match ->
            val range = match.range
            if (cursor < range.first) append(cleaned.substring(cursor, range.first))
            val matchValue = match.value
            when {
                matchValue.startsWith("**") -> withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(match.groupValues[1])
                }
                matchValue.startsWith("`") -> withStyle(
                    SpanStyle(fontFamily = FontFamily.Monospace, background = defaultColor.copy(alpha = 0.12f))
                ) {
                    append(match.groupValues[3])
                }
                else -> withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                    append(match.groupValues[2])
                }
            }
            cursor = range.last + 1
        }
        if (cursor < cleaned.length) append(cleaned.substring(cursor))
    }
}

// ─── Comparison Table ─────────────────────────────────────────────────────────

@Composable
fun ComparisonTable(
    headers: List<String>,
    rows: List<List<String>>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth().padding(vertical = 6.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Box(modifier = Modifier.horizontalScroll(rememberScrollState())) {
            Column(modifier = Modifier.padding(12.dp)) {
                // Header row
                Row(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(vertical = 8.dp, horizontal = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    headers.forEach { header ->
                        Text(
                            text = header,
                            modifier = Modifier.widthIn(min = 80.dp, max = 150.dp),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                rows.forEachIndexed { index, row ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (index % 2 == 1) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                else Color.Transparent,
                                RoundedCornerShape(6.dp)
                            )
                            .padding(vertical = 7.dp, horizontal = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        row.forEach { cell ->
                            Text(
                                text = cell,
                                modifier = Modifier.widthIn(min = 80.dp, max = 150.dp),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─── Code Block ───────────────────────────────────────────────────────────────

@Composable
fun CodeBlock(
    language: String,
    code: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A2E))
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF16213E))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = language.ifBlank { "code" }.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF7BC8F6),
                    fontWeight = FontWeight.Bold
                )
                val context = LocalContext.current
                val clipboard = remember { context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager }
                Text(
                    text = "Copy",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF4FC3F7),
                    modifier = Modifier
                        .clickable { clipboard.setPrimaryClip(ClipData.newPlainText("Code", code)) }
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
            Text(
                text = code,
                modifier = Modifier.fillMaxWidth().padding(12.dp).horizontalScroll(rememberScrollState()),
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFFD4E6F1)
                )
            )
        }
    }
}

// ─── Parser ───────────────────────────────────────────────────────────────────

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
                if (index == 0) parsedHeaders.addAll(cells)
                else if (!line.contains("---")) parsedRows.add(cells)
            }
            if (parsedHeaders.isNotEmpty()) segments.add(ChatSegment.Table(parsedHeaders, parsedRows))
            else segments.add(ChatSegment.Text(tableLines.joinToString("\n")))
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
            "CODE" -> if (isCodeBoundary) { flushCode(); mode = "TEXT" } else currentCodeLines.add(line)
            "TABLE" -> if (isTableLine) currentTableLines.add(line)
                       else { flushTable(); if (isCodeBoundary) { codeLanguage = trimmed.substring(3).trim(); mode = "CODE" } else { currentTextLines.add(line); mode = "TEXT" } }
            else -> when {
                isCodeBoundary -> { flushText(); codeLanguage = trimmed.substring(3).trim(); mode = "CODE" }
                isTableLine    -> { flushText(); currentTableLines.add(line); mode = "TABLE" }
                else           -> currentTextLines.add(line)
            }
        }
    }
    when (mode) {
        "CODE"  -> flushCode()
        "TABLE" -> flushTable()
        else    -> flushText()
    }
    return segments
}
