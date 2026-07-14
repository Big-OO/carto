package com.shopify.carto.feature.home_widget.presentation

import com.shopify.carto.feature.home_widget.domain.usecase.GetHomeProfileWidgetDataUseCase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface HomeProfileWidgetEntryPoint {
    fun getHomeProfileWidgetDataUseCase(): GetHomeProfileWidgetDataUseCase
}
