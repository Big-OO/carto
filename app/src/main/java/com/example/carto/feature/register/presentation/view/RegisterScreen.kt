package com.example.carto.feature.register.presentation.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.rememberLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.example.carto.core.components.PrimaryButton
import com.example.carto.core.components.TextField
import com.example.carto.feature.register.presentation.state.RegisterFormUiState
import com.example.carto.feature.register.presentation.viewmodel.RegisterInteractionListener
import com.example.carto.feature.register.presentation.viewmodel.RegisterSideEffects
import com.example.carto.feature.register.presentation.viewmodel.RegisterViewModel

@Composable
fun RegisterScreen(
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
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 58.dp, horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            "Create an account", style = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 32.sp,
            )
        )

        Text(
            "Let’s create your account.", style = TextStyle(
                fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        TextField(
            value = state.fullName.value,
            onValueChange = interactionListener::onFullNameValueChanged,
            title = "Full Name",
            placeholder = "Enter your full name",
            isValidate = interactionListener.isFullNameIsValid(),
            errorMessage = state.fullName.errorMessage,
        )

        TextField(
            value = state.email.value,
            onValueChange = interactionListener::onEmailValueChanged,
            title = "Email",
            placeholder = "Enter your email address",
            isValidate = interactionListener.isEmailIsValid(),
            errorMessage = state.email.errorMessage,
        )

        TextField(
            value = state.password.value,
            onValueChange = interactionListener::onPasswordValueChanged,
            title = "Password",
            placeholder = "Enter your password",
            isPassword = true,
            isValidate = interactionListener.isPasswordIsValid(),
            errorMessage = state.password.errorMessage,
            isPasswordVisible = state.isPasswordVisible,
            onPasswordToggle = interactionListener::togglePasswordVisibility
        )

        if (state.generalErrorMessage.isNotBlank()) {
            Text(
                text = state.generalErrorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        Spacer(Modifier.height(20.dp))

        PrimaryButton(
            onCLick = interactionListener::onRegister,
            text = if (state.isLoading) "Creating account..." else "Create an Account",
            enabled = !state.isLoading && interactionListener.isPasswordIsValid() &&
                    interactionListener.isEmailIsValid() && interactionListener.isFullNameIsValid(),
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                "Already have an account? ",
                style = TextStyle(
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            )

            Spacer(Modifier.width(2.dp))

            Text(
                modifier = Modifier.clickable(enabled = !state.isLoading) {
                    interactionListener.onNavigateToLogin()
                },
                text = "Log In",
                style = TextStyle(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary,
                    textDecoration = TextDecoration.Underline,
                )
            )
        }
    }
}
