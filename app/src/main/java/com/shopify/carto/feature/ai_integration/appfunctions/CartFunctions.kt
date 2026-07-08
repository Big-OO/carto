package com.shopify.carto.feature.ai_integration.appfunctions

import androidx.appfunctions.AppFunctionContext
import androidx.appfunctions.service.AppFunction
import com.shopify.carto.feature.shopping_cart.domain.usecase.AddToCartUseCase
import com.shopify.carto.feature.shopping_cart.domain.usecase.ObserveCartUseCase
import com.shopify.carto.feature.product_details.domain.usecase.RemoveFromCartUseCase
import com.shopify.carto.feature.shopping_cart.domain.usecase.UpdateCartQuantityUseCase
import com.shopify.carto.feature.product_details.domain.usecase.GetProductDetailsUseCase
import com.shopify.carto.feature.product_details.domain.model.merchandiseId
import javax.inject.Inject
import kotlinx.coroutines.flow.first

class CartFunctions @Inject constructor(
    private val addToCartUseCase: AddToCartUseCase,
    private val removeFromCartUseCase: RemoveFromCartUseCase,
    private val updateCartQuantityUseCase: UpdateCartQuantityUseCase,
    private val observeCartUseCase: ObserveCartUseCase,
    private val getProductDetailsUseCase: GetProductDetailsUseCase
) {

    /**
     * Add a product to the shopping cart.
     *
     * Use this when the user wants to add an item to their cart/basket, purchase something, or select a product to buy.
     *
     * @param productId The ID of the product.
     * @param quantity The quantity of the product to add. Defaults to 1.
     * @param size Optional size preference (e.g., "S", "M", "L", "42").
     * @param color Optional color preference (e.g., "Black", "White").
     * @return Confirmation that the product was added to the cart.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun addToCart(
        appFunctionContext: AppFunctionContext,
        productId: Long,
        quantity: Int,
        size: String,
        color: String
    ): String {
        val detailsResult = getProductDetailsUseCase(productId)
        if (detailsResult.isFailure) {
            return "Failed to find product details to add to cart."
        }
        val product = detailsResult.getOrThrow()
        val resolvedSize = size.takeIf { it.isNotBlank() && !it.equals("none", ignoreCase = true) }
        val resolvedColor = color.takeIf { it.isNotBlank() && !it.equals("none", ignoreCase = true) }
        val variant = product.findVariant(resolvedSize, resolvedColor) ?: product.variants.firstOrNull()
        if (variant == null) {
            return "No variants available for this product."
        }

        val merchandiseId = variant.merchandiseId
        val result = addToCartUseCase(merchandiseId, quantity)
        return if (result.isSuccess) {
            "Added ${quantity}x '${product.title}' (Size: ${variant.size ?: "Default"}, Color: ${variant.color ?: "Default"}) to your cart."
        } else {
            "Failed to add item to cart: ${result.exceptionOrNull()?.message ?: "Unknown error"}"
        }
    }

    /**
     * Remove an item from the shopping cart.
     *
     * Use this when the user wants to delete, remove, or clear an item from their cart/basket.
     *
     * @param lineId The unique line ID of the item in the cart.
     * @return Confirmation that the item was removed.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun removeFromCart(appFunctionContext: AppFunctionContext, lineId: String): String {
        val result = removeFromCartUseCase(lineId)
        return if (result.isSuccess) {
            "Removed item from cart."
        } else {
            "Failed to remove item: ${result.exceptionOrNull()?.message}"
        }
    }

    /**
     * Update the quantity of a cart item.
     *
     * Use this when the user wants to increase, decrease, or update the amount of a product in their cart.
     *
     * @param lineId The unique line ID of the item in the cart.
     * @param quantity The new quantity.
     * @return Confirmation of the quantity change.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun updateQuantity(appFunctionContext: AppFunctionContext, lineId: String, quantity: Int): String {
        val result = updateCartQuantityUseCase(lineId, quantity)
        return if (result.isSuccess) {
            "Updated quantity to $quantity."
        } else {
            "Failed to update quantity: ${result.exceptionOrNull()?.message}"
        }
    }

    /**
     * Display all items in the user's shopping cart.
     *
     * Use this when the user asks to see their cart, view their basket, check their order subtotal, or list cart items.
     *
     * @return A list of cart items and total prices.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun showCart(appFunctionContext: AppFunctionContext): String {
        return try {
            val cartResult = observeCartUseCase().first()
            val cart = cartResult.getOrThrow()
            if (cart.isEmpty) {
                "Your cart is currently empty."
            } else {
                "Cart Subtotal: ${cart.subtotal} ${cart.currency}\n" +
                        "Total: ${cart.total} ${cart.currency}\n" +
                        "Items in Cart:\n" +
                        cart.lines.joinToString("\n") { line ->
                            "- Line ID: ${line.id}\n  Product: ${line.productTitle}\n  Variant: ${line.variantTitle}\n  Quantity: ${line.quantity}\n  Price: ${line.price} ${cart.currency}\n  Line Total: ${line.lineTotal} ${cart.currency}\n---"
                        }
            }
        } catch (e: Exception) {
            "Failed to read cart contents: ${e.message}"
        }
    }
}
