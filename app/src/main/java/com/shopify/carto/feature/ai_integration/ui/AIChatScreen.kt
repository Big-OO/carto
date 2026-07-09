package com.shopify.carto.feature.ai_integration.ui

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shopify.carto.feature.ai_integration.voice.SpeechRecognizerManager
import com.shopify.carto.feature.ai_integration.voice.VoiceRecognitionState
import com.shopify.carto.feature.ai_integration.ui.components.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIChatScreen(
    onProductClick: (Long) -> Unit,
    onBackClick: () -> Unit = {},
    onCheckoutClick: () -> Unit = {},
    autoStartVoice: Boolean = false,
    onAutoStartVoiceConsumed: () -> Unit = {},
    viewModel: AIChatViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedCurrency by viewModel.selectedCurrency.collectAsStateWithLifecycle()
    val favoriteIds by viewModel.favoriteIds.collectAsStateWithLifecycle()
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


    LaunchedEffect(autoStartVoice) {
        if (autoStartVoice) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            onAutoStartVoiceConsumed()
        }
    }

    // Auto-scroll to latest message
    LaunchedEffect(
        uiState.messages.size,
        uiState.isProcessing,
        uiState.statusMessage,
        uiState.messages.lastOrNull()?.text
    ) {
        val messageCount = uiState.messages.size
        val hasThinking = uiState.isProcessing && uiState.statusMessage != null
        val totalCount = messageCount + (if (hasThinking) 1 else 0)
        if (totalCount > 0) {
            listState.animateScrollToItem(totalCount - 1)
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
                                favoriteIds = favoriteIds,
                                isLastAiMessage = isLast && !message.isUser,
                                onProductClick = onProductClick,
                                onFavoriteClick = { viewModel.toggleProductFavorite(it) },
                                onAddToCartClick = { product ->
                                    viewModel.addProductToCart(product.id)
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Added ${product.title} to cart")
                                    }
                                },
                                onRegenerateClick = { viewModel.regenerateLastResponse() },
                                onOptionClick = { option ->
                                    if (option.equals("Go to Checkout", ignoreCase = true)) {
                                        onCheckoutClick()
                                    } else {
                                        viewModel.sendMessage(option)
                                    }
                                },
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
                    if (voiceState == VoiceRecognitionState.Idle) {
                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                },
                onMicPressEnd = {
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
