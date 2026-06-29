package com.example.carto.feature.register.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.carto.feature.login.presentation.components.PrimaryButton
import com.example.carto.feature.login.presentation.components.TextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Scaffold(
        containerColor = Color.White
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.Start
            ) {
                // Header
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Create Account",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground,
                        lineHeight = 36.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Sign up to get started",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(36.dp))

                // Form
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TextField(
                        title = "Full Name",
                        value = name,
                        onValueChange = { name = it }
                    )

                    TextField(
                        title = "Email",
                        value = email,
                        onValueChange = { email = it }
                    )

                    TextField(
                        title = "Password",
                        value = password,
                        isPassword = true,
                        onValueChange = { password = it }
                    )

                    TextField(
                        title = "Confirm Password",
                        value = confirmPassword,
                        isPassword = true,
                        onValueChange = { confirmPassword = it }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    PrimaryButton(
                        text = "Register",
                        enabled = name.isNotBlank() && email.isNotBlank() && password.length >= 6 && password == confirmPassword,
                        onCLick = {
                            // On successful registration, pop back
                            onBack()
                        }
                    )
                }

                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.height(24.dp))

                // Back link
                val signInText = buildAnnotatedString {
                    withStyle(
                        SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                    ) {
                        append("Already have an account? ")
                    }
                    withStyle(
                        SpanStyle(
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            textDecoration = TextDecoration.Underline
                        )
                    ) {
                        append("Sign In")
                    }
                }

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = signInText,
                        modifier = Modifier.clickable {
                            onBack()
                        }
                    )
                }
            }
        }
    }
}
