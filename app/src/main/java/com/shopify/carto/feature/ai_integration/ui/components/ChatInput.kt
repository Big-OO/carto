package com.shopify.carto.feature.ai_integration.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.shopify.carto.R

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
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Text field — full width minus trailing button
            OutlinedTextField(
                value = textInput,
                onValueChange = onValueChange,
                placeholder = {
                    Text(
                        text = if (isListening) stringResource(id = R.string.ai_listening) else stringResource(id = R.string.ai_message_placeholder),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                },
                modifier = Modifier.weight(1f),
                maxLines = 5,
                shape = RoundedCornerShape(28.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(
                    onSend = { if (textInput.isNotBlank() && !isProcessing) onSendClick() }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
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
                        var isPressed by remember { mutableStateOf(false) }
                        val scale by animateFloatAsState(
                            targetValue = if (isPressed) 1.2f else 1f,
                            animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                            label = "micScale"
                        )
                        val elevation by animateDpAsState(
                            targetValue = if (isPressed) 8.dp else 2.dp,
                            label = "micElevation"
                        )
                        val containerColor by animateColorAsState(
                            targetValue = if (isPressed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer,
                            label = "micColor"
                        )
                        val contentColor by animateColorAsState(
                            targetValue = if (isPressed) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer,
                            label = "micContentColor"
                        )

                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                }
                                .shadow(elevation, CircleShape)
                                .clip(CircleShape)
                                .background(containerColor)
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onPress = {
                                            isPressed = true
                                            onMicPressStart()
                                            try {
                                                val released = tryAwaitRelease()
                                                if (released) {
                                                    onMicPressEnd()
                                                } else {
                                                    onMicCancel()
                                                }
                                            } finally {
                                                isPressed = false
                                            }
                                        }
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Hold to record",
                                tint = contentColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
