package com.example.carto.registration.presentation.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import com.example.carto.R
import com.example.carto.registration.presentation.viewmodel.RegisterViewModel


@Composable
fun RegisterScreen() {
    val viewModel: RegisterViewModel

    RegisterScreenContent()
}

@Composable
private fun RegisterScreenContent(
    modifier: Modifier = Modifier
) {
    var usernameValue by remember { mutableStateOf("") }
    var emailValue by remember { mutableStateOf("") }
    var passwordValue by remember { mutableStateOf("") }

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
            value = usernameValue,
            onValueChange = { newValue -> usernameValue = newValue },
            label = "Full Name",
            placeholder = "Enter your full name",
            modifier = Modifier.fillMaxWidth(),
            isPassword = false,
            isError = false,
            keyboardType = KeyboardType.Text,
            errorMessage = "",
        )

        Spacer(Modifier.height(10.dp))

        AuthInputField(
            value = emailValue,
            onValueChange = { newValue -> emailValue = newValue },
            label = "Email",
            placeholder = "Enter your email address",
            modifier = Modifier.fillMaxWidth(),
            isPassword = false,
            isError = false,
            keyboardType = KeyboardType.Email,
            errorMessage = "",
        )

        Spacer(Modifier.height(10.dp))

        AuthInputField(
            modifier = Modifier.fillMaxWidth(),
            value = passwordValue,
            onValueChange = { newValue -> passwordValue = newValue },
            label = "Password",
            placeholder = "Enter your password",
            isPassword = true,
            isError = false,
            keyboardType = KeyboardType.Password,
            errorMessage = "",
        )

        Spacer(Modifier.height(20.dp))

        AuthPrimaryButton(
            onClick = {},
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
                    //TODO: Navigate to login Screen
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


@Composable
fun AuthPrimaryButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    label: String,
    enabled: Boolean = false
) {

    Button(
        modifier = modifier, onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Black,
            disabledContainerColor = Color(0xFFCCCCCC),
            disabledContentColor = Color.White,
            contentColor = Color.White,
        ),
        contentPadding = PaddingValues(vertical = 16.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                label,
                style = TextStyle(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                )
            )
        }
    }

}


@Composable
fun AuthInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    isError: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    errorMessage: String = ""
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = label,
            color = Color(0xFF000000),
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp,
            ),
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    placeholder, style = TextStyle(
                        color = Color(0XFF808080)
                    )
                )
            },
            isError = isError,
            visualTransformation = if (isPassword && !passwordVisible) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            trailingIcon = if (isPassword) {
                {
                    val image = if (passwordVisible) {
                        R.drawable.ic_visibility
                    } else {
                        R.drawable.ic_visibility_off
                    }
                    val description = if (passwordVisible) "Hide password" else "Show password"

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(painter = painterResource(image), contentDescription = description)
                    }
                }
            } else null,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0XFF808080),
                unfocusedBorderColor = Color(0XFF808080),
            ),
        )

        if (isError && errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}


@Composable
@Preview(showBackground = true, showSystemUi = true)
private fun RegisterScreenContentPreview(
    modifier: Modifier = Modifier
) {
    RegisterScreenContent(modifier = modifier)
}