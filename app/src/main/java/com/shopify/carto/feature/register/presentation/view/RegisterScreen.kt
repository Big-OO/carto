package com.shopify.carto.feature.register.presentation.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.rememberLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.shopify.carto.core.components.PrimaryButton
import com.shopify.carto.core.components.TextField
import com.shopify.carto.feature.register.presentation.state.RegisterFormInput
import com.shopify.carto.feature.register.presentation.state.RegisterFormUiState
import com.shopify.carto.feature.register.presentation.viewmodel.RegisterInteractionListener
import com.shopify.carto.feature.register.presentation.viewmodel.RegisterSideEffects
import com.shopify.carto.feature.register.presentation.viewmodel.RegisterViewModel
import kotlinx.coroutines.delay

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    onNavigateToLogin: () -> Unit = {},
) {
    val viewModel: RegisterViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    val lifecycleOwner = rememberLifecycleOwner()
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.effects.collect { effect ->
                when (effect) {
                    RegisterSideEffects.NavigateToLogin -> onNavigateToLogin()
                }
            }
        }
    }

    RegisterScreenContent(
        modifier = modifier,
        state = state,
        interactionListener = viewModel,
    )
}

@Composable
private fun RegisterScreenContent(
    modifier: Modifier = Modifier,
    state: RegisterFormUiState,
    interactionListener: RegisterInteractionListener,
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(80)
        visible = true
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.Start,
            ) {
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(400)) + slideInVertically(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMediumLow,
                        ),
                        initialOffsetY = { -60 },
                    ),
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Create an account",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onBackground,
                            lineHeight = 36.sp,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Let’s create your account.",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(36.dp))

                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(500, delayMillis = 100)) + slideInVertically(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMediumLow,
                        ),
                        initialOffsetY = { 80 },
                    ),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0f)),
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            TextField(
                                title = "Full Name",
                                value = state.fullName.value,
                                isValidate = state.fullName.isValidField(
                                    isValid = interactionListener.isFullNameIsValid(),
                                ),
                                errorMessage = state.fullName.visibleErrorMessage(),
                                isPassword = false,
                            ) {
                                interactionListener.onFullNameValueChanged(it)
                            }

                            TextField(
                                title = "Email",
                                value = state.email.value,
                                isValidate = state.email.isValidField(
                                    isValid = interactionListener.isEmailIsValid(),
                                ),
                                errorMessage = state.email.visibleErrorMessage(),
                                isPassword = false,
                            ) {
                                interactionListener.onEmailValueChanged(it)
                            }

                            TextField(
                                title = "Password",
                                value = state.password.value,
                                isValidate = state.password.isValidField(
                                    isValid = interactionListener.isPasswordIsValid(),
                                ),
                                errorMessage = state.password.visibleErrorMessage(),
                                isPassword = true,
                                isPasswordVisible = state.isPasswordVisible,
                                onPasswordToggle = interactionListener::togglePasswordVisibility,
                            ) {
                                interactionListener.onPasswordValueChanged(it)
                            }

                            Text(
                                text = "Password must be at least 8 characters and include uppercase, lowercase, a number, and a special character.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 6.dp)
                            )

                            AnimatedVisibility(visible = state.generalErrorMessage.isNotBlank()) {
                                Text(
                                    text = state.generalErrorMessage,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            PrimaryButton(
                                text = if (state.isLoading) "Creating account…" else "Create an Account",
                                enabled = !state.isLoading && interactionListener.isPasswordIsValid() &&
                                        interactionListener.isEmailIsValid() &&
                                        interactionListener.isFullNameIsValid(),
                            ) {
                                interactionListener.onRegister()
                            }

                            AnimatedVisibility(visible = state.isLoading) {
                                LinearProgressIndicator(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(4.dp)),
                                    color = MaterialTheme.colorScheme.primary,
                                    trackColor = MaterialTheme.colorScheme.outline,
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.height(24.dp))

                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(800, delayMillis = 350)),
                ) {
                    val loginText = buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 14.sp,
                            ),
                        ) {
                            append("Already have an account? ")
                        }
                        withStyle(
                            SpanStyle(
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                textDecoration = TextDecoration.Underline,
                            ),
                        ) {
                            append("Log In")
                        }
                    }

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = loginText,
                            modifier = Modifier.clickable(enabled = !state.isLoading) {
                                interactionListener.onNavigateToLogin()
                            },
                        )
                    }
                }
            }
        }
    }
}

private fun RegisterFormInput.visibleErrorMessage(): String? {
    return errorMessage.takeIf { isError && it.isNotBlank() }
}

private fun RegisterFormInput.isValidField(isValid: Boolean): Boolean {
    return value.isNotBlank() && !isError && isValid
}
