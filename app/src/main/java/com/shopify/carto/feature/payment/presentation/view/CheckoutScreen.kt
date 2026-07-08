package com.shopify.carto.feature.payment.presentation.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shopify.carto.core.components.PrimaryButton
import com.shopify.carto.feature.payment.domain.model.PaymentMethod
import com.shopify.carto.feature.payment.presentation.state.CheckoutEvent
import com.shopify.carto.feature.payment.presentation.state.CheckoutUiState
import com.shopify.carto.feature.payment.presentation.state.CheckoutUiEvent
import com.shopify.carto.feature.payment.presentation.view.components.OrderSummarySection
import com.shopify.carto.feature.payment.presentation.view.components.PaymentMethodSelector
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.ui.platform.LocalContext
import com.paymob.paymob_sdk.PaymobSdk
import com.shopify.carto.BuildConfig
import com.shopify.carto.feature.payment.presentation.viewmodel.CheckoutViewModel
import kotlinx.coroutines.launch

tailrec fun Context.findActivity(): Activity {
    return when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> error("Activity not found")
    }
}

private fun launchPaymobSdk(
    activity: Activity,
    clientSecret: String,
    onSuccess: (String) -> Unit,
    onPending: () -> Unit,
    onFailure: (String) -> Unit
) {
    PaymobSdk.Builder(
        context = activity,
        clientSecret = clientSecret,
        publicKey = BuildConfig.PAYMOB_PUBLIC_KEY,
        paymobSdkListener = object : com.paymob.paymob_sdk.ui.PaymobSdkListener {
            override fun onSuccess(payResponse: java.util.HashMap<String, String?>) {
                val rawMap: Map<*, *> = payResponse
                val idValue = rawMap["id"] ?: rawMap["transaction_id"]
                val transactionId = idValue?.toString() ?: ""
                val successValue = rawMap["success"]
                val success = successValue?.toString()?.toBooleanStrictOrNull() ?: true

                if (success) {
                    onSuccess(transactionId)
                } else {
                    onFailure("Payment was not successful")
                }
            }

            override fun onFailure(msg: String?) {
                onFailure(msg ?: "Payment failed")
            }

            override fun onPending() {
                onPending()
            }
        }
    ).build().start()
}

@Composable
fun CheckoutScreen(
    onBackClick: () -> Unit,
    onPaymentSuccess: (transactionId: String, orderId: String) -> Unit,
    onPaymentFailed: (message: String) -> Unit,
    onNavigateToAddressesClick: () -> Unit,
    viewModel: CheckoutViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = androidx.compose.runtime.rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.onEvent(CheckoutEvent.RefreshCheckout)
        viewModel.events.collect { event ->
            when (event) {
                is CheckoutUiEvent.LaunchPaymob -> {
                    try {
                        launchPaymobSdk(
                            activity = context.findActivity(),
                            clientSecret = event.clientSecret,
                            onSuccess = { transactionId ->
                                scope.launch {
                                    viewModel.onPaymobSuccess(transactionId)
                                }
                            },
                            onPending = {
                                scope.launch {
                                    viewModel.onPaymobPending()
                                }
                            },
                            onFailure = { message ->
                                scope.launch {
                                    viewModel.onPaymobFailure(message)
                                }
                            }
                        )
                    } catch (e: Exception) {
                        scope.launch {
                            viewModel.onPaymobFailure("SDK Error: ${e.message}")
                        }
                    }
                }

                is CheckoutUiEvent.PaymentSuccess -> {
                    onPaymentSuccess(event.transactionId, event.orderId)
                }

                is CheckoutUiEvent.PaymentFailed -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(event.message)
                    }
                    onPaymentFailed(event.message)
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        CheckoutScreenContent(
            state = state,
            onEvent = viewModel::onEvent,
            onBackClick = onBackClick,
            onNavigateToAddressesClick = onNavigateToAddressesClick,
        )
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp)
        )
    }
}

@Composable
private fun CheckoutScreenContent(
    state: CheckoutUiState,
    onEvent: (CheckoutEvent) -> Unit,
    onBackClick: () -> Unit,
    onNavigateToAddressesClick: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .statusBarsPadding()
        ) {
            CheckoutTopBar(onBackClick = onBackClick)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                Spacer(modifier = Modifier.height(4.dp))

                DeliveryAddressSection(
                    state = state,
                    onNavigateToAddressesClick = onNavigateToAddressesClick,
                )

                CustomerInfoSection(
                    state = state,
                    onEvent = onEvent,
                )

                OrderSummarySection(
                    items = state.orderItems,
                    subtotalAmountCents = state.subtotalAmountCents,
                    shippingFeeCents = state.shippingFeeCents,
                    discountAmountCents = state.discountAmountCents,
                    totalAmountCents = state.totalAmountCents,
                    promoCodeInput = state.promoCodeInput,
                    promoCodeError = state.promoCodeError,
                    appliedPromoCode = state.appliedPromoCode,
                    onPromoCodeInputChange = { onEvent(CheckoutEvent.UpdatePromoCodeInput(it)) },
                    onApplyPromoCode = { onEvent(CheckoutEvent.ApplyPromoCode(state.promoCodeInput)) },
                )

                PaymentMethodSelector(
                    selectedMethod = state.selectedPaymentMethod,
                    onMethodSelected = { onEvent(CheckoutEvent.SelectPaymentMethod(it)) },
                )

                Spacer(modifier = Modifier.height(80.dp))
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
            ) {
                PrimaryButton(
                    text = when (state.selectedPaymentMethod) {
                        PaymentMethod.CARD -> "Pay with Card"
                        PaymentMethod.DIGITAL_WALLET -> "Pay with Wallet"
                        PaymentMethod.CASH_ON_DELIVERY -> "Place Order (COD)"
                    },
                    enabled = !state.isProcessing,
                    onCLick = { onEvent(CheckoutEvent.PlaceOrder) },
                )
            }
        }

        AnimatedVisibility(
            visible = state.isProcessing,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 3.dp,
                    )
                    Text(
                        text = "Processing your order...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
            }
        }
    }
}

@Composable
private fun CheckoutTopBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.onBackground,
            )
        }
        Text(
            text = "Checkout",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

@Composable
private fun CustomerInfoSection(
    state: CheckoutUiState,
    onEvent: (CheckoutEvent) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "Customer Information",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            CheckoutTextField(
                value = state.customerFirstName,
                onValueChange = { onEvent(CheckoutEvent.UpdateFirstName(it)) },
                label = "First Name",
                error = state.validationErrors["firstName"],
                modifier = Modifier.weight(1f),
                imeAction = ImeAction.Next,
            )
            CheckoutTextField(
                value = state.customerLastName,
                onValueChange = { onEvent(CheckoutEvent.UpdateLastName(it)) },
                label = "Last Name",
                error = state.validationErrors["lastName"],
                modifier = Modifier.weight(1f),
                imeAction = ImeAction.Next,
            )
        }

        CheckoutTextField(
            value = state.customerEmail,
            onValueChange = { onEvent(CheckoutEvent.UpdateEmail(it)) },
            label = "Email",
            error = state.validationErrors["email"],
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next,
        )

        CheckoutTextField(
            value = state.customerPhone,
            onValueChange = { onEvent(CheckoutEvent.UpdatePhone(it)) },
            label = "Phone Number",
            error = state.validationErrors["phone"],
            keyboardType = KeyboardType.Phone,
            imeAction = ImeAction.Next,
        )
    }
}

@Composable
private fun DeliveryAddressSection(
    state: CheckoutUiState,
    onNavigateToAddressesClick: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Delivery Address",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground,
            )
            TextButton(onClick = onNavigateToAddressesClick) {
                Text(
                    text = "Change",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
            border = CardDefaults.outlinedCardBorder(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Address",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Column(modifier = Modifier.weight(1f)) {
                    val addr = state.selectedAddress
                    if (addr != null) {
                        Text(
                            text = addr.nickname.ifBlank { "Delivery Address" },
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${addr.address1}, ${addr.city}, ${addr.country}\nPhone: ${addr.phone}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 20.sp
                        )
                    } else if (state.address.isNotBlank()) {
                        Text(
                            text = "Current Address",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${state.address}, ${state.city}\nPhone: ${state.customerPhone}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 20.sp
                        )
                    } else {
                        Text(
                            text = "No address selected",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Click 'Change' to choose or add a delivery address.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CheckoutTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: String? = null,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Default,
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = {
                Text(
                    text = label,
                    fontSize = 13.sp,
                )
            },
            isError = error != null,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                errorBorderColor = MaterialTheme.colorScheme.error,
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = imeAction,
            ),
        )
        if (error != null) {
            Text(
                text = error,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                fontSize = 11.sp,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp),
            )
        }
    }
}
