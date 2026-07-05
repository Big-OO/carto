package com.shopify.carto.feature.addresses.presentation.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.LocalContext
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
import com.shopify.carto.feature.addresses.presentation.state.AddressesUiState
import com.shopify.carto.feature.addresses.presentation.view.components.AddressCard
import com.shopify.carto.feature.addresses.presentation.view.components.AddressTopBar
import com.shopify.carto.feature.addresses.presentation.viewmodel.AddressesInteractionListener
import com.shopify.carto.feature.addresses.presentation.viewmodel.AddressesViewModel

@Composable
fun AddressesScreen(
    onBackClick: () -> Unit,
    onAddNewAddressClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel = hiltViewModel<AddressesViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val resources = LocalResources.current

    LaunchedEffect(state.snackbarMessage) {
        state.snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(resources.getString(message.stringRes()))
            viewModel.consumeSnackbar()
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.loadAddresses()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        AddressesContent(
            state = state,
            interactionListener = viewModel,
            onBackClick = onBackClick,
            onAddNewAddressClick = onAddNewAddressClick,
            modifier = Modifier.padding(padding),
        )
    }
}

@Composable
private fun AddressesContent(
    state: AddressesUiState,
    interactionListener: AddressesInteractionListener,
    onBackClick: () -> Unit,
    onAddNewAddressClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
    ) {
        AddressTopBar(
            title = stringResource(R.string.addresses_title),
            onBackClick = onBackClick,
        )

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = stringResource(R.string.addresses_saved_address_title),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold,
        )

        Spacer(modifier = Modifier.height(12.dp))

        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }

            state.addresses.isEmpty() -> {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(R.string.addresses_empty_addresses),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(state.addresses, key = { it.id }) { address ->
                        AddressCard(
                            address = address,
                            selected = state.selectedAddressId == address.id,
                            isRemoving = state.removingAddressId == address.id,
                            onClick = { interactionListener.selectAddress(address.id) },
                            onRemoveClick = { interactionListener.removeAddress(address.id) },
                        )
                    }
                }
            }
        }

        OutlinedButton(
            onClick = onAddNewAddressClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null)
            Text(text = stringResource(R.string.addresses_add_new_address))
        }

        Spacer(modifier = Modifier.height(20.dp))

        AnimatedVisibility(
            visible = state.hasDefaultAddressChange,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Button(
                onClick = interactionListener::applyDefaultAddress,
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isApplying,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            ) {
                AnimatedVisibility(visible = state.isApplying, enter = fadeIn(), exit = fadeOut()) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp,
                    )
                }
                AnimatedVisibility(visible = !state.isApplying, enter = fadeIn(), exit = fadeOut()) {
                    Text(
                        text = stringResource(R.string.addresses_apply_default),
                        modifier = Modifier.padding(vertical = 8.dp),
                    )
                }
            }
        }
    }
}
