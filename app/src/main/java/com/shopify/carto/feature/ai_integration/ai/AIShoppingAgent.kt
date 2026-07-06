package com.shopify.carto.feature.ai_integration.ai

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.ai.Chat
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.FunctionDeclaration
import com.google.firebase.ai.type.FunctionResponsePart
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.Tool
import com.google.firebase.ai.type.content
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class AIShoppingAgent(
    private val appFunctionRunner: ShoppingAppFunctionRunner,
) {
    private val chat: Chat by lazy {
        Firebase.ai(backend = GenerativeBackend.googleAI())
            .generativeModel(
                modelName = MODEL_NAME,
                tools = listOf(Tool.functionDeclarations(functionDeclarations())),
                systemInstruction = content {
                    text(
                        "You are Carto's premium AI Shopping Assistant. " +
                        "Help users search products, compare products, generate outfits, manage cart/wishlist, and discover insights. " +
                        "Always call appropriate App Functions rather than guessing or making up data. " +
                        "When listing products, display their details clearly and cleanly. " +
                        "Keep your responses friendly, helpful, and concise."
                    )
                }
            )
            .startChat()
    }

    suspend fun sendMessage(userMessage: String, onStep: (String) -> Unit): String {
        Log.d(TAG, "Sending message to AI: $userMessage")
        var response = chat.sendMessage(userMessage)

        while (response.functionCalls.isNotEmpty()) {
            val resultParts = response.functionCalls.map { call ->
                val stepMessage = when (call.name) {
                    "searchProducts" -> "Searching products..."
                    "getProductDetails" -> "Fetching product details..."
                    "addToCart" -> "Adding product to cart..."
                    "removeFromCart" -> "Removing product from cart..."
                    "updateQuantity" -> "Updating quantity..."
                    "showCart" -> "Loading cart..."
                    "addToWishlist" -> "Adding product to wishlist..."
                    "removeFromWishlist" -> "Removing product from wishlist..."
                    "showWishlist" -> "Loading wishlist..."
                    "compareProducts" -> "Comparing products..."
                    "generateOutfit" -> "Generating outfit..."
                    else -> "Processing request..."
                }
                onStep(stepMessage)

                val result = appFunctionRunner.execute(call.name, call.args)
                FunctionResponsePart(call.name, buildJsonObject { put("result", result) })
            }

            response = chat.sendMessage(
                content(role = "function") { resultParts.forEach { part(it) } },
            )
        }

        return response.text ?: "(The assistant did not return any text.)"
    }

    private fun functionDeclarations(): List<FunctionDeclaration> = listOf(
        FunctionDeclaration(
            "searchProducts",
            "Search for products in the store by keyword, category, vendor, brand, or query.",
            mapOf("query" to Schema.string("The query or keyword to search for.")),
            listOf("query"),
        ),
        FunctionDeclaration(
            "getProductDetails",
            "Retrieve details for a single product by its unique product ID.",
            mapOf("productId" to Schema.integer("The numeric ID of the product.")),
            listOf("productId"),
        ),
        FunctionDeclaration(
            "addToCart",
            "Add a product variant to the shopping cart by product ID, with size and color (pass empty or 'none' if unspecified).",
            mapOf(
                "productId" to Schema.integer("The numeric ID of the product."),
                "quantity" to Schema.integer("The quantity to add."),
                "size" to Schema.string("The size preference (e.g. S, M, L, or 'none')."),
                "color" to Schema.string("The color preference (e.g. Black, White, or 'none').")
            ),
            listOf("productId", "quantity", "size", "color"),
        ),
        FunctionDeclaration(
            "removeFromCart",
            "Remove a line item from the shopping cart using its unique line ID.",
            mapOf("lineId" to Schema.string("The unique line ID of the item in the cart.")),
            listOf("lineId"),
        ),
        FunctionDeclaration(
            "updateQuantity",
            "Update the quantity of a product in the shopping cart.",
            mapOf(
                "lineId" to Schema.string("The unique line ID of the item in the cart."),
                "quantity" to Schema.integer("The new quantity.")
            ),
            listOf("lineId", "quantity"),
        ),
        FunctionDeclaration(
            "showCart",
            "Retrieve and list all products currently in the user's shopping cart.",
            emptyMap(),
        ),
        FunctionDeclaration(
            "addToWishlist",
            "Add a product to the user's wishlist/favorites using its product ID.",
            mapOf("productId" to Schema.integer("The numeric ID of the product.")),
            listOf("productId"),
        ),
        FunctionDeclaration(
            "removeFromWishlist",
            "Remove a product from the user's wishlist/favorites using its product ID.",
            mapOf("productId" to Schema.integer("The numeric ID of the product.")),
            listOf("productId"),
        ),
        FunctionDeclaration(
            "showWishlist",
            "Retrieve and list all products in the user's wishlist/favorites.",
            emptyMap(),
        ),
        FunctionDeclaration(
            "compareProducts",
            "Compare details, price, vendor, stock, sizes, and colors between two products using their IDs.",
            mapOf(
                "productId1" to Schema.integer("The ID of the first product."),
                "productId2" to Schema.integer("The ID of the second product.")
            ),
            listOf("productId1", "productId2"),
        ),
        FunctionDeclaration(
            "generateOutfit",
            "Generate a matching style outfit recommendation containing top, bottom, and shoes based on a preference.",
            mapOf("preference" to Schema.string("The style preference, e.g. casual, sporty, formal.")),
            listOf("preference"),
        )
    )

    companion object {
        const val MODEL_NAME = "gemini-3.5-flash"
        private const val TAG = "AIShoppingAgent"
    }
}
