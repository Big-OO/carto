package com.shopify.carto.feature.profile.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shopify.carto.R
import com.shopify.carto.core.components.PrimaryButton
import com.shopify.carto.core.components.TextField
import com.shopify.carto.feature.profile.presentation.ProfileEffect
import com.shopify.carto.feature.profile.presentation.ProfileEvent
import com.shopify.carto.ui.theme.CartoTheme
import kotlinx.coroutines.flow.Flow

@Composable
fun EditProfileBottomSheetContent(
    currentName: String,
    effectFlow: Flow<ProfileEffect>,
    onDismiss: () -> Unit,
    onEvent: (ProfileEvent) -> Unit
) {
    var name by remember { mutableStateOf(currentName) }
    var nameError by remember { mutableStateOf<String?>(null) }
    
    var isSaving by remember { mutableStateOf(false) }
    var updateError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        effectFlow.collect { effect ->
            when (effect) {
                is ProfileEffect.ShowSuccess -> {
                    isSaving = false
                    onDismiss()
                }
                is ProfileEffect.ShowError -> {
                    isSaving = false
                    updateError = effect.message
                }
                else -> {}
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.accountUpdateProfileTitle),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = CartoTheme.colors.onSurface
        )

        TextField(
            title = stringResource(id = R.string.accountFullNameTitle),
            value = name,
            placeholder = stringResource(id = R.string.accountEnterYourFullNamePlaceHolder),
            errorMessage = nameError,
            onValueChange = { newValue ->
                name = newValue
                val trimmed = newValue.trim()
                nameError = when {
                    trimmed.length < 3 -> "Name must be at least 3 characters long"
                    trimmed.split(Regex("\\s+")).filter { it.isNotEmpty() }.size < 2 -> "Please enter both first and last name"
                    else -> null
                }
            }
        )

        if (updateError != null) {
            Text(
                text = updateError ?: "",
                color = CartoTheme.colors.error,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        val isSaveEnabled = name.trim().length >= 3 && nameError == null && !isSaving

        if (isSaving) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(color = CartoTheme.colors.primary)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = stringResource(id = R.string.accountSaveChangesLoadingTitle), fontSize = 14.sp, color = Color.Gray)
            }
        } else {
            PrimaryButton(
                text = stringResource(id = R.string.accountSaveChangesTitle),
                enabled = isSaveEnabled,
                onCLick = {
                    isSaving = true
                    updateError = null
                    onEvent(ProfileEvent.SaveProfileClicked(name))
                }
            )

            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.Black
                )
            ) {
                Text(
                    text = stringResource(id = R.string.accountCancelTitle),
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}