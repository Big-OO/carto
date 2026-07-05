package com.shopify.carto.feature.addresses.presentation.view

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.shopify.carto.R
import com.shopify.carto.feature.addresses.presentation.model.stringRes
import com.shopify.carto.feature.addresses.presentation.state.NewAddressEffect
import com.shopify.carto.feature.addresses.presentation.state.NewAddressUiState
import com.shopify.carto.feature.addresses.presentation.view.components.AddressMapPreviewCard
import com.shopify.carto.feature.addresses.presentation.view.components.AddressSuccessDialog
import com.shopify.carto.feature.addresses.presentation.view.components.AddressTextField
import com.shopify.carto.feature.addresses.presentation.view.components.AddressTopBar
import com.shopify.carto.feature.addresses.presentation.viewmodel.NewAddressInteractionListener
import com.shopify.carto.feature.addresses.presentation.viewmodel.NewAddressViewModel
import com.shopify.carto.feature.map.domain.model.SelectedMapAddress

@Composable
fun NewAddressScreen(
    onBackClick: () -> Unit,
    onSelectFromMapClick: () -> Unit,
    selectedMapAddress: SelectedMapAddress?,
    onMapAddressConsumed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: NewAddressViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val resources = LocalResources.current

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.effects.collect { effect ->
                when (effect) {
                    NewAddressEffect.OnNavigateBack -> onBackClick()
                }
            }
        }
    }

    LaunchedEffect(selectedMapAddress) {
        selectedMapAddress?.let { result ->
            viewModel.onMapAddressSelected(result)
            onMapAddressConsumed()
        }
    }

    LaunchedEffect(state.snackbarMessage) {
        state.snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(resources.getString(message.stringRes()))
            viewModel.consumeSnackbar()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        NewAddressContent(
            modifier = Modifier.padding(padding),
            state = state,
            interactionListener = viewModel,
            onSelectFromMapClick = onSelectFromMapClick,
        )
    }

    if (state.showSuccessDialog) {
        AddressSuccessDialog(
            onThanksClick = viewModel::dismissSuccess,
        )
    }
}

@Composable
private fun NewAddressContent(
    state: NewAddressUiState,
    interactionListener: NewAddressInteractionListener,
    onSelectFromMapClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
    ) {
        AddressTopBar(
            title = stringResource(R.string.addresses_new_address_title),
            onBackClick = interactionListener::onNavigateBack,
        )

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = stringResource(R.string.addresses_location_title),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold,
        )

        Spacer(modifier = Modifier.height(16.dp))

        AddressMapPreviewCard(
            onClick = onSelectFromMapClick,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(R.string.addresses_address_details_title),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold,
        )

        Spacer(modifier = Modifier.height(16.dp))

        AddressFormFields(
            state = state,
            interactionListener = interactionListener,
        )

        Spacer(modifier = Modifier.height(20.dp))

        SaveAddressButton(
            state = state,
            onClick = interactionListener::saveAddress,
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun AddressFormFields(
    state: NewAddressUiState,
    interactionListener: NewAddressInteractionListener,
) {
    AddressTextField(
        title = stringResource(R.string.addresses_address_nickname_label),
        value = state.form.name,
        onValueChange = interactionListener::onNameChanged,
        placeholder = stringResource(R.string.addresses_address_nickname_placeholder),
    )

    Spacer(modifier = Modifier.height(14.dp))

    AddressTextField(
        title = stringResource(R.string.addresses_full_address_label),
        value = state.form.address1,
        onValueChange = interactionListener::onAddressChanged,
        placeholder = stringResource(R.string.addresses_full_address_placeholder),
        singleLine = false,
    )

    Spacer(modifier = Modifier.height(14.dp))

    AddressTextField(
        title = stringResource(R.string.addresses_city_label),
        value = state.form.city,
        onValueChange = interactionListener::onCityChanged,
        placeholder = stringResource(R.string.addresses_city_placeholder),
    )

    Spacer(modifier = Modifier.height(14.dp))

    AddressTextField(
        title = stringResource(R.string.addresses_province_label),
        value = state.form.province,
        onValueChange = interactionListener::onProvinceChanged,
        placeholder = stringResource(R.string.addresses_province_placeholder),
    )

    Spacer(modifier = Modifier.height(14.dp))

    AddressTextField(
        title = stringResource(R.string.addresses_country_label),
        value = state.form.country,
        onValueChange = interactionListener::onCountryChanged,
        placeholder = stringResource(R.string.addresses_country_placeholder),
    )

    Spacer(modifier = Modifier.height(14.dp))

    AddressTextField(
        title = stringResource(R.string.addresses_zip_label),
        value = state.form.zip,
        onValueChange = interactionListener::onZipChanged,
        placeholder = stringResource(R.string.addresses_zip_placeholder),
    )

    Spacer(modifier = Modifier.height(8.dp))

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Checkbox(
            checked = state.form.isDefault,
            onCheckedChange = interactionListener::onDefaultChanged,
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.primary,
            ),
        )
        Text(
            text = stringResource(R.string.addresses_make_default_address),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun SaveAddressButton(
    state: NewAddressUiState,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
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
                text = stringResource(R.string.addresses_add_button),
                modifier = Modifier.padding(vertical = 8.dp),
            )
        }
    }
}
