package com.shopify.carto.feature.ai_integration.appfunctions

import androidx.appfunctions.AppFunctionContext
import androidx.appfunctions.service.AppFunction
import com.shopify.carto.feature.favorite.domain.usecase.ToggleFavoriteUseCase
import com.shopify.carto.feature.favorite.domain.usecase.RemoveFavoriteUseCase
import com.shopify.carto.feature.favorite.domain.usecase.ObserveFavoritesUseCase
import com.shopify.carto.feature.favorite.domain.usecase.ObserveFavoriteIdsUseCase
import com.shopify.carto.feature.product_details.domain.usecase.GetProductDetailsUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.first

class WishlistFunctions @Inject constructor(
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase,
    private val observeFavoritesUseCase: ObserveFavoritesUseCase,
    private val observeFavoriteIdsUseCase: ObserveFavoriteIdsUseCase,
    private val getProductDetailsUseCase: GetProductDetailsUseCase
) {

    /**
     * Add a product to the wishlist/favorites.
     *
     * Use this when the user wants to save, favorite, wishlist, or remember a product for later.
     *
     * @param productId The ID of the product.
     * @return Confirmation that the product was added.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun addToWishlist(appFunctionContext: AppFunctionContext, productId: Long): String {
        try {
            val favoriteIds = observeFavoriteIdsUseCase().first()
            if (favoriteIds.contains(productId)) {
                return "Product is already in your wishlist."
            }

            val detailsResult = getProductDetailsUseCase(productId)
            if (detailsResult.isFailure) {
                return "Failed to find product details to add to wishlist."
            }
            val product = detailsResult.getOrThrow()
            val price = product.price
            val name = product.title
            val imageUrl = product.images.firstOrNull()

            val isAdded = toggleFavoriteUseCase(productId, name, imageUrl, price)
            return if (isAdded) {
                "Added '$name' to your wishlist."
            } else {
                "Product was removed from wishlist."
            }
        } catch (e: Exception) {
            return "Failed to add product to wishlist: ${e.message}"
        }
    }

    /**
     * Remove a product from the wishlist/favorites.
     *
     * Use this when the user wants to remove, delete, or unfavorite a product from their wishlist.
     *
     * @param productId The ID of the product.
     * @return Confirmation that the product was removed.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun removeFromWishlist(appFunctionContext: AppFunctionContext, productId: Long): String {
        removeFavoriteUseCase(productId)
        return "Removed product $productId from your wishlist."
    }

    /**
     * Show all products in the user's wishlist/favorites.
     *
     * Use this when the user wants to view their wishlist, see their favorite products, or list saved items.
     *
     * @return A list of favorited products.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun showWishlist(appFunctionContext: AppFunctionContext): String {
        return try {
            val favorites = observeFavoritesUseCase().first()
            if (favorites.isEmpty()) {
                "Your wishlist is currently empty."
            } else {
                "Wishlist Items:\n" +
                        favorites.joinToString("\n") { item ->
                            "- Product ID: ${item.productId}\n  Name: ${item.name}\n  Price: ${item.price}\n---"
                        }
            }
        } catch (e: Exception) {
            "Failed to read wishlist: ${e.message}"
        }
    }
}
