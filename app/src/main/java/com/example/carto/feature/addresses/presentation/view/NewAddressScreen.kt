package com.example.carto.feature.addresses.presentation.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.carto.feature.addresses.presentation.view.components.AddressSuccessDialog
import com.example.carto.feature.addresses.presentation.view.components.AddressTextField
import com.example.carto.feature.addresses.presentation.view.components.AddressTopBar
import com.example.carto.feature.addresses.presentation.viewmodel.NewAddressViewModel
import com.example.carto.feature.map.domain.model.SelectedMapAddress

@Composable
fun NewAddressScreen(
    onBackClick: () -> Unit,
    onSelectFromMapClick: () -> Unit,
    selectedMapAddress: SelectedMapAddress?,
    onMapAddressConsumed: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NewAddressViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(selectedMapAddress) {
        selectedMapAddress?.let {
            viewModel.onMapAddressSelected(it)
            onMapAddressConsumed()
        }
    }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { snackbarHostState.showSnackbar(it) }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
        ) {
            AddressTopBar(
                title = "New Address",
                onBackClick = onBackClick,
            )

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Address",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.SemiBold,
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onSelectFromMapClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
            ) {
                Icon(imageVector = Icons.Outlined.Map, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(text = "Select from map")
            }

            Spacer(modifier = Modifier.height(16.dp))

            AddressTextField(
                title = "Address Nickname",
                value = state.form.name,
                onValueChange = viewModel::onNameChanged,
                placeholder = "Home, Office, Apartment...",
            )

            Spacer(modifier = Modifier.height(14.dp))

            AddressTextField(
                title = "Full Address",
                value = state.form.address1,
                onValueChange = viewModel::onAddressChanged,
                placeholder = "Enter your full address...",
                singleLine = false,
            )

            Spacer(modifier = Modifier.height(14.dp))

            AddressTextField(
                title = "City",
                value = state.form.city,
                onValueChange = viewModel::onCityChanged,
                placeholder = "Giza",
            )

            Spacer(modifier = Modifier.height(14.dp))

            AddressTextField(
                title = "Province",
                value = state.form.province,
                onValueChange = viewModel::onProvinceChanged,
                placeholder = "Cairo",
            )

            Spacer(modifier = Modifier.height(14.dp))

            AddressTextField(
                title = "Country",
                value = state.form.country,
                onValueChange = viewModel::onCountryChanged,
                placeholder = "Egypt",
            )

            Spacer(modifier = Modifier.height(14.dp))

            AddressTextField(
                title = "ZIP Code",
                value = state.form.zip,
                onValueChange = viewModel::onZipChanged,
                placeholder = "11511",
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Checkbox(
                    checked = state.form.isDefault,
                    onCheckedChange = viewModel::onDefaultChanged,
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary,
                    ),
                )
                Text(
                    text = "Make this as a default address",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { viewModel.saveAddress() },
                modifier = Modifier.fillMaxWidth(),
                enabled = state.canSave && !state.isSaving,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            ) {
                AnimatedVisibility(visible = state.isSaving, enter = fadeIn(), exit = fadeOut()) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp,
                    )
                }
                AnimatedVisibility(visible = !state.isSaving, enter = fadeIn(), exit = fadeOut()) {
                    Text(
                        text = "Add",
                        modifier = Modifier.padding(vertical = 8.dp),
                    )
                }
            }
        }
    }

    if (state.showSuccessDialog) {
        AddressSuccessDialog(
            onThanksClick = {
                viewModel.dismissSuccess(onBackClick)
            }
        )
    }
}
