package com.example.carto.feature.profile.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.carto.feature.profile.presentation.components.ProfileErrorContent
import com.example.carto.feature.profile.presentation.components.ProfileGuestContent
import com.example.carto.feature.profile.presentation.components.ProfileLoadingContent
import com.example.carto.feature.profile.presentation.components.ProfileSuccessContent
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    uiState: ProfileState,
    effectFlow: Flow<ProfileEffect>,
    onEvent: (ProfileEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when (uiState) {
            is ProfileState.Loading -> {
                ProfileLoadingContent()
            }
            is ProfileState.Guest -> {
                ProfileGuestContent(onEvent = onEvent)
            }
            is ProfileState.Error -> {
                ProfileErrorContent(
                    message = uiState.message,
                    onEvent = onEvent
                )
            }
            is ProfileState.Success -> {
                ProfileSuccessContent(
                    profile = uiState.profile,
                    effectFlow = effectFlow,
                    onEvent = onEvent
                )
            }
        }
    }
}
