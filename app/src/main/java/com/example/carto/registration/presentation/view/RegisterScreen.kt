package com.example.carto.registration.presentation.view

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.rememberLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.carto.registration.presentation.state.RegisterFormUiState
import com.example.carto.registration.presentation.view.components.AuthInputField
import com.example.carto.registration.presentation.view.components.AuthPrimaryButton
import com.example.carto.registration.presentation.viewmodel.RegisterInteractionListener
import com.example.carto.registration.presentation.viewmodel.RegisterSideEffects
import com.example.carto.registration.presentation.viewmodel.RegisterViewModel
import kotlinx.coroutines.launch


@Composable
fun RegisterScreen() {
    val viewModel: RegisterViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    val lifecycleOwner = rememberLifecycleOwner()
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycleScope.launch {
            viewModel.effects.collect { effect ->
                when (effect) {
                    RegisterSideEffects.NavigateToLogin -> TODO()
                }
            }
        }
    }

    RegisterScreenContent(
        state = state,
        interactionListener = viewModel
    )
}

@Composable
private fun RegisterScreenContent(
    modifier: Modifier = Modifier,
    state: RegisterFormUiState,
    interactionListener: RegisterInteractionListener
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 58.dp, horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            "Create an account", style = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 32.sp,
            )
        )

        Text(
            "Let’s create your account.", style = TextStyle(
                fontSize = 16.sp, color = Color(0xFF808080)
            )
        )

        Spacer(Modifier.height(10.dp))

        AuthInputField(
            value = state.fullName.value,
            onValueChange = interactionListener::onFullNameValueChanged,
            label = "Full Name",
            placeholder = "Enter your full name",
            modifier = Modifier.fillMaxWidth(),
            isPassword = false,
            isError = state.fullName.isError,
            keyboardType = KeyboardType.Text,
            errorMessage = state.fullName.errorMessage,
        )

        Spacer(Modifier.height(10.dp))

        AuthInputField(
            value = state.email.value,
            onValueChange = interactionListener::onEmailValueChanged,
            label = "Email",
            placeholder = "Enter your email address",
            modifier = Modifier.fillMaxWidth(),
            isPassword = false,
            isError = state.email.isError,
            keyboardType = KeyboardType.Email,
            errorMessage = state.email.errorMessage,
        )

        Spacer(Modifier.height(10.dp))

        AuthInputField(
            modifier = Modifier.fillMaxWidth(),
            value = state.password.value,
            onValueChange = interactionListener::onPasswordValueChanged,
            label = "Password",
            placeholder = "Enter your password",
            isPassword = true,
            isError = state.password.isError,
            keyboardType = KeyboardType.Password,
            errorMessage = state.password.errorMessage,
        )

        Spacer(Modifier.height(20.dp))

        AuthPrimaryButton(
            onClick = {
                interactionListener.onRegister()
            },
            label = "Create an Account",
            enabled = true,
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                "Already have an account? ",
                style = TextStyle(
                    fontSize = 16.sp,
                    color = Color(0XFF808080),
                )
            )

            Spacer(Modifier.width(2.dp))

            Text(
                modifier = Modifier.clickable(true) {
                    interactionListener.onNavigateToLogin()
                },
                text = "Log In",
                style = TextStyle(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = Color.Black,
                    textDecoration = TextDecoration.Underline
                )
            )
        }
    }
}
