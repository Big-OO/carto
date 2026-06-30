package com.example.carto.feature.register.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carto.feature.register.domain.model.RegisterFailure
import com.example.carto.feature.register.domain.model.RegisterFailureType
import com.example.carto.feature.register.domain.model.RegisterRequest
import com.example.carto.feature.register.domain.model.RegisterResult
import com.example.carto.feature.register.domain.model.ValidationFormError
import com.example.carto.feature.register.domain.usecases.RegisterUserUseCase
import com.example.carto.feature.register.domain.usecases.ValidateEmailUseCase
import com.example.carto.feature.register.domain.usecases.ValidateFullNameUseCase
import com.example.carto.feature.register.domain.usecases.ValidatePasswordUseCase
import com.example.carto.feature.register.presentation.state.RegisterFormUiState
import com.example.carto.feature.register.presentation.uimodels.FieldType
import com.example.carto.feature.register.presentation.utils.toUserMessage
import com.example.carto.feature.register.presentation.utils.toValidatedInput
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val validateFullNameUseCase: ValidateFullNameUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase,
    private val registerUserUseCase: RegisterUserUseCase,
) : ViewModel(), RegisterInteractionListener {

    private companion object {
        const val TAG = "RegisterViewModel"
    }


    private val _state = MutableStateFlow(RegisterFormUiState())
    val state = _state.asStateFlow()

    private val _effects = MutableSharedFlow<RegisterSideEffects>()
    val effects = _effects.asSharedFlow()


    override fun onEmailValueChanged(newValue: String) {
        _state.update {
            it.copy(
                email = it.email.copy(
                    value = newValue,
                    isError = false,
                    errorMessage = "",
                ),
                generalErrorMessage = "",
            )
        }
    }

    override fun isEmailIsValid(): Boolean {
        return validateEmailUseCase(_state.value.email.value) == ValidationFormError.Valid
    }

    override fun isPasswordIsValid(): Boolean {
        return validatePasswordUseCase(_state.value.password.value) == ValidationFormError.Valid
    }

    override fun togglePasswordVisibility(){
        _state.update {
            it.copy(isPasswordVisible = !it.isPasswordVisible)
        }
    }

    override fun isFullNameIsValid(): Boolean {
        return validateFullNameUseCase(_state.value.fullName.value) == ValidationFormError.Valid
    }

    override fun onPasswordValueChanged(newValue: String) {
        _state.update {
            it.copy(
                password = it.password.copy(
                    value = newValue,
                    isError = false,
                    errorMessage = "",
                ),
                generalErrorMessage = "",
            )
        }
    }

    override fun onFullNameValueChanged(newValue: String) {
        _state.update {
            it.copy(
                fullName = it.fullName.copy(
                    value = newValue,
                    isError = false,
                    errorMessage = "",
                ),
                generalErrorMessage = "",
            )
        }
    }

    override fun onRegister() {
        if (_state.value.isLoading) {
            return
        }

        if (!validateForm()) {
            return
        }

        val currentState = _state.value

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, generalErrorMessage = "") }

            when (
                val result = runCatching {
                    registerUserUseCase(
                        RegisterRequest(
                            fullName = currentState.fullName.value.trim(),
                            email = currentState.email.value.trim(),
                            password = currentState.password.value,
                        )
                    )
                }.getOrElse { exception ->
                    RegisterResult.Failure(
                        RegisterFailure(
                            type = RegisterFailureType.Unknown,
                            message = "Unexpected ViewModel registration failure: ${exception::class.java.name}. ${
                                exception.message.orEmpty().ifBlank { "No message provided." }
                            }",
                        )
                    )
                }
            ) {
                is RegisterResult.Success -> {
                    _state.update { it.copy(isLoading = false) }
                    _effects.emit(RegisterSideEffects.NavigateToLogin)
                }

                is RegisterResult.Failure -> {
                    Log.e(
                        TAG,
                        "Register failed. type=${result.failure.type}, details=${result.failure.message}"
                    )

                    _state.update {
                        it.copy(
                            isLoading = false,
                            generalErrorMessage = result.failure.toUserMessage(),
                        )
                    }
                }
            }
        }
    }

    override fun onNavigateToLogin() {
        viewModelScope.launch {
            _effects.emit(RegisterSideEffects.NavigateToLogin)
        }
    }

    private fun validateForm(): Boolean {
        val currentState = _state.value

        val fullNameValidation = validateFullNameUseCase(currentState.fullName.value)
        val emailValidation = validateEmailUseCase(currentState.email.value)
        val passwordValidation = validatePasswordUseCase(currentState.password.value)

        _state.update {
            it.copy(
                fullName = it.fullName.toValidatedInput(fullNameValidation, FieldType.FullName),
                email = it.email.toValidatedInput(emailValidation, FieldType.Email),
                password = it.password.toValidatedInput(passwordValidation, FieldType.Password),
                generalErrorMessage = "",
            )
        }

        return fullNameValidation == ValidationFormError.Valid &&
                emailValidation == ValidationFormError.Valid &&
                passwordValidation == ValidationFormError.Valid
    }
}
