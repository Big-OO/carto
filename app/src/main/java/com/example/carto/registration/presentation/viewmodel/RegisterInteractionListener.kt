package com.example.carto.registration.presentation.viewmodel

interface RegisterInteractionListener {
    fun onEmailValueChanged(newValue: String)
    fun onPasswordValueChanged(newValue: String)
    fun onFullNameValueChanged(newValue: String)
    fun onRegister()
    fun onNavigateToLogin()
}