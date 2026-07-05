package com.shopify.carto.feature.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shopify.carto.core.session.domain.usecase.ClearAppSessionUseCase
import com.shopify.carto.core.session.domain.usecase.ObserveAppSessionUseCase
import com.shopify.carto.feature.profile.domain.usecase.ObserveProfileUseCase
import com.shopify.carto.feature.profile.domain.usecase.RefreshProfileUseCase
import com.shopify.carto.feature.profile.domain.usecase.UpdateProfileUseCase
import com.shopify.carto.feature.profile.presentation.model.ProfileData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val observeAppSessionUseCase: ObserveAppSessionUseCase,
    private val observeProfileUseCase: ObserveProfileUseCase,
    private val refreshProfileUseCase: RefreshProfileUseCase,
    private val clearAppSessionUseCase: ClearAppSessionUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _effect = Channel<ProfileEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        _uiState.value = ProfileState.Loading
        viewModelScope.launch {
            observeAppSessionUseCase().collect { session ->
                if (session.isGuest || session.customerId == null) {
                    _uiState.value = ProfileState.Guest
                } else {
                    val customerIdLong = session.customerId.toLongOrNull()
                    if (customerIdLong == null) {
                        _uiState.value = ProfileState.Error("Invalid Customer ID")
                        return@collect
                    }

                    launch {
                        observeProfileUseCase(customerIdLong).collect { profile ->
                            if (profile != null) {
                                val spent = if (profile.totalSpent.startsWith("$")) {
                                    profile.totalSpent
                                } else {
                                    "$${profile.totalSpent}"
                                }

                                _uiState.value = ProfileState.Success(
                                    ProfileData(
                                        id = profile.id,
                                        name = "${profile.firstName} ${profile.lastName}".trim(),
                                        email = profile.email,
                                        phone = profile.phone,
                                        ordersCount = profile.ordersCount,
                                        totalSpent = spent
                                    )
                                )
                            } else {
                                if (_uiState.value !is ProfileState.Success) {
                                    _uiState.value = ProfileState.Loading
                                }
                            }
                        }
                    }

                    launch {
                        val result = refreshProfileUseCase(customerIdLong)
                        result.onFailure { exception ->
                            if (_uiState.value !is ProfileState.Success) {
                                _uiState.value = ProfileState.Error(
                                    exception.message ?: "Failed to load profile. Please check your internet connection."
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    fun onEvent(event: ProfileEvent) {
        when (event) {
            ProfileEvent.LogoutClicked -> logout()
            ProfileEvent.LoginClicked -> sendEffect(ProfileEffect.NavigateToLogin)
            ProfileEvent.RetryClicked -> loadProfile()
            ProfileEvent.SettingClicked -> sendEffect(ProfileEffect.NavigateToSettings)
            is ProfileEvent.SaveProfileClicked -> saveProfile(event.name)
        }
    }

    private fun logout() {
        viewModelScope.launch {
            clearAppSessionUseCase()
            sendEffect(ProfileEffect.NavigateToLogin)
        }
    }

    private fun saveProfile(name: String) {
        viewModelScope.launch {
            val session = observeAppSessionUseCase().first()
            val customerIdLong = session.customerId?.toLongOrNull()
            if (customerIdLong == null) {
                sendEffect(ProfileEffect.ShowError("No active session or customer ID"))
                return@launch
            }

            val currentState = _uiState.value
            val email = if (currentState is ProfileState.Success) {
                currentState.profile.email
            } else {
                ""
            }
            val phone = if (currentState is ProfileState.Success) {
                currentState.profile.phone
            } else {
                null
            }

            val nameParts = name.trim().split(" ")
            val firstName = nameParts.firstOrNull().orEmpty()
            val lastName = if (nameParts.size > 1) nameParts.drop(1).joinToString(" ") else ""

            android.util.Log.d("ProfileViewModel", "Attempting name update. CustomerId: $customerIdLong, Name: $firstName $lastName, Phone: $phone")

            val result = updateProfileUseCase(
                customerId = customerIdLong,
                firstName = firstName,
                lastName = lastName,
                email = email,
                phone = phone
            )

            result.onSuccess {
                android.util.Log.d("ProfileViewModel", "Profile updated successfully in Shopify and local DB!")
                sendEffect(ProfileEffect.ShowSuccess("Profile updated successfully!"))
            }.onFailure { exception ->
                android.util.Log.e("ProfileViewModel", "Profile update failed!", exception)
                sendEffect(ProfileEffect.ShowError(exception.message ?: "Failed to update profile"))
            }
        }
    }

    private fun sendEffect(effect: ProfileEffect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}
