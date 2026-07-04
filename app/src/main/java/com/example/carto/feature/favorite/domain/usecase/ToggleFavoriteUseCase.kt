package com.example.carto.feature.favorite.domain.usecase


import com.example.carto.feature.favorite.domain.model.FavoriteProduct
import com.example.carto.feature.favorite.domain.repository.FavoriteRepository
import javax.inject.Inject
import com.example.carto.feature.favorite.presentation.feedback.FavoriteFeedbackBus
import com.example.carto.feature.favorite.presentation.feedback.FavoriteFeedbackEvent // or whatever your event class name is

class ToggleFavoriteUseCase @Inject constructor(
    private val repository: FavoriteRepository,
    private val feedbackBus: FavoriteFeedbackBus
) {
    suspend operator fun invoke(
        productId: Long,
        name: String,
        imageUrl: String?,
        price: Double,
    ): Boolean {
        val isAdded = repository.toggleFavorite(
            FavoriteProduct(
                productId = productId,
                name = name,
                imageUrl = imageUrl,
                price = price,
                addedAt = System.currentTimeMillis(),
            )
        )

        feedbackBus.emit(
            FavoriteFeedbackEvent(
                productId = productId,
                productName = name,
                imageUrl = imageUrl,
                price = price,
                added = isAdded
            )
        )

        return isAdded
    }
}