package com.example.carto.feature.settings.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.carto.R
import com.example.carto.feature.settings.presentation.components.ExpandableSelectionItem
import com.example.carto.feature.settings.presentation.components.SettingsCard
import com.example.carto.feature.settings.presentation.components.SettingsItem
import com.example.carto.feature.settings.presentation.components.SettingsSection
import com.example.carto.feature.settings.domain.model.AppLanguage
import com.example.carto.feature.settings.domain.model.AppTheme
import com.example.carto.feature.settings.domain.model.Currency
import com.example.carto.ui.theme.CartoTheme
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    var isScreenVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(50.milliseconds)
        isScreenVisible = true
    }

    val selectedCurrency by viewModel.currency.collectAsState()
    val selectedTheme by viewModel.theme.collectAsState()
    val selectedLanguage by viewModel.language.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings),
                        style = CartoTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = CartoTheme.colors.primary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CartoTheme.colors.surface
                )
            )
        },
        containerColor = CartoTheme.colors.background
    ) { paddingValues ->
        AnimatedVisibility(
            visible = isScreenVisible,
            enter = fadeIn(animationSpec = tween(500)) +
                    slideInVertically(
                        initialOffsetY = { it / 8 },
                        animationSpec = tween(500)
                    )
        ) {
            LazyColumn(
                modifier = Modifier
                    .background(CartoTheme.colors.background)
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item { Spacer(modifier = Modifier.height(8.dp)) }

                item {
                    SettingsSection(title = "Account Settings") {
                        SettingsCard {
                            SettingsItem(
                                title = "Saved Locations",
                                icon = Icons.Outlined.LocationOn,
                                onClick = { }
                            )
                            HorizontalDivider(color = CartoTheme.colors.outline)
                            SettingsItem(
                                title = "Order History",
                                icon = Icons.Outlined.History,
                                onClick = { }
                            )
                            HorizontalDivider(color = CartoTheme.colors.outline)
                            SettingsItem(
                                title = "Payment Methods",
                                icon = Icons.Outlined.Payment,
                                onClick = { }
                            )
                        }
                    }
                }

                item {
                    SettingsSection(title = "Market Preference") {
                        SettingsCard {
                            ExpandableSelectionItem(
                                title = "Currency",
                                icon = Icons.Outlined.CurrencyExchange,
                                options = Currency.entries,
                                selectedOption = selectedCurrency,
                                onOptionSelected = { viewModel.setCurrency(it) },
                                optionLabel = { it.displayName }
                            )
                        }
                    }
                }

                item {
                    SettingsSection(title = "Application Settings") {
                        SettingsCard {
                            ExpandableSelectionItem(
                                title = "Theme",
                                icon = Icons.Outlined.DarkMode,
                                options = AppTheme.entries,
                                selectedOption = selectedTheme,
                                onOptionSelected = { viewModel.setTheme(it) },
                                optionLabel = { it.displayName }
                            )
                            HorizontalDivider(color = CartoTheme.colors.outline)
                            ExpandableSelectionItem(
                                title = "Language",
                                icon = Icons.Outlined.Language,
                                options = AppLanguage.entries,
                                selectedOption = selectedLanguage,
                                onOptionSelected = { viewModel.setLanguage(it) },
                                optionLabel = { it.displayName }
                            )
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(100.dp)) }
            }
        }
    }
}