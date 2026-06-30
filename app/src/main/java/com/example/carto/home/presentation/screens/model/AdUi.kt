package com.example.carto.home.presentation.screens.model

data class AdUi(
    val id: String,
    val title: String,
    val subtitle: String,
    val buttonText: String,
    val color1: Long,
    val color2: Long,
    val route: String? = null // may navigate in the future
)
