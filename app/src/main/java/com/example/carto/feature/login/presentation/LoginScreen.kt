package com.example.carto.feature.login.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.carto.R
import com.example.carto.feature.login.data.datasource.LoginRemoteDataSourceImpl
import com.example.carto.feature.login.data.repository.LoginRepositoryImpl
import com.example.carto.feature.login.presentation.components.PrimaryButton
import com.example.carto.feature.login.presentation.components.TextField
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import okhttp3.Dispatcher

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(modifier: Modifier = Modifier) {
    var email by remember { mutableStateOf("abdallah.elsobky@gmail.com") }
    var password by remember { mutableStateOf("1q2w3e4r") }
    val repository = LoginRepositoryImpl(
        remoteDataSource = LoginRemoteDataSourceImpl(
            firebaseAuth = FirebaseAuth.getInstance()
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.login_to_your_account),
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.it_s_great_to_see_you_again),
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(32.dp))

        TextField(title = stringResource(R.string.email), value = email, isValidate = true) {
            email = it
        }

        Spacer(modifier = Modifier.height(16.dp))
        TextField(title = stringResource(R.string.password), value = password) {
            password = it
        }
        Spacer(modifier = Modifier.height(16.dp))

        val forgotPasswordText = buildAnnotatedString {
            withStyle(style = SpanStyle(color = Color.Gray, fontSize = 12.sp)) {
                append(stringResource(R.string.forgot_your_password))
            }
            withStyle(
                style = SpanStyle(
                    color = Color.Black,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline
                )
            ) {
                append(stringResource(R.string.reset_your_password))
            }
        }
        Text(text = forgotPasswordText, modifier = Modifier.clickable {
            // navigate to reset password
        })

        Spacer(modifier = Modifier.height(24.dp))

        PrimaryButton(stringResource(R.string.login)){
            CoroutineScope(Dispatchers.IO).launch {
                repository.login(email, password)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray)
            Text(
                text = "Or",
                modifier = Modifier.padding(horizontal = 16.dp),
                color = Color.Gray,
                fontSize = 12.sp
            )
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray)
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedButton(
            onClick = { },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, Color.LightGray)
        ) {
            Image(
                painter = painterResource(
                    id = R.drawable.google_ic
                ),
                contentDescription = "Google",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.login_with_google),
                color = Color.Black,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))


        Spacer(modifier = Modifier.weight(1f))

        val joinText = buildAnnotatedString {
            withStyle(style = SpanStyle(color = Color.Gray, fontSize = 14.sp)) {
                append(stringResource(R.string.don_t_have_an_account))
            }
            withStyle(
                style = SpanStyle(
                    color = Color.Black,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline
                )
            ) {
                append(stringResource(R.string.join))
            }
        }

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text(text = joinText, modifier = Modifier.clickable {
                // navigate to register
            })
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen()
}