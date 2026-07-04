package com.shopify.carto.feature.profile.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.LocalMall
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.shopify.carto.core.components.PrimaryButton
import com.shopify.carto.core.components.SecondaryButton
import com.shopify.carto.feature.profile.presentation.ProfileEffect
import com.shopify.carto.feature.profile.presentation.ProfileEvent
import com.shopify.carto.feature.profile.presentation.model.ProfileData
import com.shopify.carto.ui.theme.CartoTheme
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSuccessContent(
    profile: ProfileData,
    effectFlow: Flow<ProfileEffect>,
    onEvent: (ProfileEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var visible by remember { mutableStateOf(false) }
    var showEditBottomSheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    if (showEditBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showEditBottomSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = Color.White
        ) {
            EditProfileBottomSheetContent(
                currentName = profile.name,
                effectFlow = effectFlow,
                onDismiss = { showEditBottomSheet = false },
                onEvent = onEvent
            )
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically(),
        modifier = modifier.fillMaxSize()
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
            Spacer(Modifier.height(16.dp))

            ProfileHeader(profile.name, profile.id)

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PrimaryButton(
                    text = "Edit Profile",
                    enabled = true,
                    modifier = Modifier.weight(1f),
                    onCLick = { showEditBottomSheet = true },
                )
                SecondaryButton(
                    text = "Settings",
                    icon = Icons.Outlined.Settings,
                    modifier = Modifier.weight(1f),
                    color = CartoTheme.colors.primary
                ) { onEvent(ProfileEvent.SettingClicked) }
            }

            Spacer(Modifier.height(24.dp))

            ProfileInfoCard(
                icon = Icons.Outlined.Email,
                title = "Email Address",
                value = profile.email
            )

            Spacer(Modifier.height(12.dp))

            ProfileInfoCard(
                icon = Icons.Outlined.Phone,
                title = "Phone Number",
                value = profile.phone ?: "Not provided"
            )

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ProfileInfoCard(
                    icon = Icons.Outlined.LocalMall,
                    title = "Total Orders",
                    value = profile.ordersCount.toString(),
                    modifier = Modifier.weight(1f)
                )

                ProfileInfoCard(
                    icon = Icons.Outlined.Payments,
                    title = "Total Spent",
                    value = profile.totalSpent,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(24.dp))

            SecondaryButton(
                text = "Logout",
                icon = Icons.Outlined.Logout,
                color = CartoTheme.colors.error
            ) { onEvent(ProfileEvent.LogoutClicked) }
        }
    }
}
