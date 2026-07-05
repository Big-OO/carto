package com.shopify.carto.feature.register.presentation.viewmodel

interface RegisterInteractionListener {
    fun onEmailValueChanged(newValue: String)
    fun isEmailIsValid(): Boolean
    fun isPhoneNumberIsValid(): Boolean
    fun isPasswordIsValid(): Boolean
    fun togglePasswordVisibility ()
    fun isFullNameIsValid(): Boolean
    fun onPasswordValueChanged(newValue: String)
    fun onPhoneNumberValueChanged(newValue: String)
    fun onFullNameValueChanged(newValue: String)
    fun onRegister()
    fun onNavigateToLogin()
}