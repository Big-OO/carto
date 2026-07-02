package com.example.carto.feature.profile.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.LocalMall
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.carto.core.components.PrimaryButton
import com.example.carto.core.components.SecondaryButton
import com.example.carto.feature.profile.presentation.components.ProfileHeader
import com.example.carto.feature.profile.presentation.components.ProfileInfoCard

@Composable
fun ProfileScreen(
    state: ProfileState = ProfileState(
        id = "8780104335414",
        name = "Abdallah Elsobky",
        email = "sobky@gmail.com",
        phone = null,
        ordersCount = 0,
        totalSpent = "$70.00"
    ),
    onEditProfile: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    var visible by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        visible = true
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn() + slideInVertically()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(
                        enabled = true,
                        state = scrollState
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(24.dp))

                ProfileHeader(state.name, state.id)

                Spacer(Modifier.height(32.dp))

                ProfileInfoCard(
                    icon = Icons.Outlined.Email,
                    title = "Email",
                    value = state.email
                )

                Spacer(Modifier.height(12.dp))

                ProfileInfoCard(
                    icon = Icons.Outlined.Phone,
                    title = "Phone",
                    value = state.phone ?: "Not provided"
                )

                Spacer(Modifier.height(12.dp))

                ProfileInfoCard(
                    icon = Icons.Outlined.LocalMall,
                    title = "Orders",
                    value = state.ordersCount.toString()
                )

                Spacer(Modifier.height(12.dp))

                ProfileInfoCard(
                    icon = Icons.Outlined.Payments,
                    title = "Total Spent",
                    value = state.totalSpent
                )

                Spacer(Modifier.height(32.dp))
                PrimaryButton(text = "Edit Profile", enabled = true, onCLick = { onEditProfile() })
                Spacer(Modifier.height(16.dp))
                SecondaryButton(
                    text = "Logout",
                    icon = Icons.Outlined.Logout,
                    color = Color.Red
                ) { onLogout() }
                Spacer(Modifier.height(100.dp))
            }
        }
    }
}


