package com.example.carto.feature.favorite.presentation.feedback

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

data class FavoriteFeedbackEvent(
    val productId: Long,
    val productName: String,
    val imageUrl: String?,
    val price: Double,
    val added: Boolean,
)

@Singleton
class FavoriteFeedbackBus @Inject constructor() {

    private val _events = MutableSharedFlow<FavoriteFeedbackEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<FavoriteFeedbackEvent> = _events.asSharedFlow()

    suspend fun emit(event: FavoriteFeedbackEvent) {
        _events.emit(event)
    }
}