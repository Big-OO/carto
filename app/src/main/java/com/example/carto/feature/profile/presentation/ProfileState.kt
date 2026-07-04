package com.example.carto.feature.profile.presentation

import com.example.carto.feature.profile.presentation.model.ProfileData


sealed interface ProfileState {
    object Loading : ProfileState
    data class Success(val profile: ProfileData) : ProfileState
    object Guest : ProfileState
    data class Error(val message: String) : ProfileState
}