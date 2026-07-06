package com.shopify.carto.feature.ai_integration.ai

import android.util.Log
import com.shopify.carto.BuildConfig
import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.FunctionDeclaration
import com.google.ai.client.generativeai.type.FunctionResponsePart
import com.google.ai.client.generativeai.type.Schema
import com.google.ai.client.generativeai.type.Tool
import com.google.ai.client.generativeai.type.content
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive

class AIShoppingAgent(
    private val appFunctionRunner: ShoppingAppFunctionRunner,
) {
    private val chat: Chat by lazy {
        GenerativeModel(
            modelName = MODEL_NAME,
            apiKey = BuildConfig.GEMINI_API_KEY,
            tools = listOf(Tool(functionDeclarations())),
            systemInstruction = content {
                text(
                    "You are Carto's premium AI Shopping Assistant. " +
                    "Help users search products, compare products, generate outfits, manage cart/wishlist, and discover insights. " +
                    "Always call appropriate App Functions rather than guessing or making up data. " +
                    "When listing products, display their details clearly and cleanly. " +
                    "Keep your responses friendly, helpful, and concise."
                )
            }
        ).startChat()
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

                val mappedArgs: Map<String, JsonElement> = call.args.mapValues { (_, value) ->
                    if (value == null) {
                        JsonNull
                    } else {
                        val intValue = value.toIntOrNull()
                        if (intValue != null) {
                            JsonPrimitive(intValue)
                        } else {
                            val longValue = value.toLongOrNull()
                            if (longValue != null) {
                                JsonPrimitive(longValue)
                            } else {
                                val booleanValue = value.toBooleanStrictOrNull()
                                if (booleanValue != null) {
                                    JsonPrimitive(booleanValue)
                                } else {
                                    JsonPrimitive(value)
                                }
                            }
                        }
                    }
                }

                val result = appFunctionRunner.execute(call.name, mappedArgs)
                FunctionResponsePart(call.name, org.json.JSONObject().put("result", result))
            }

            response = chat.sendMessage(
                content(role = "function") { resultParts.forEach { part(it) } },
            )
        }

        return response.text ?: "(The assistant did not return any text.)"
    }

    private fun functionDeclarations(): List<FunctionDeclaration> = listOf(
        FunctionDeclaration(
            name = "searchProducts",
            description = "Search for products in the store by keyword, category, vendor, brand, or query.",
            parameters = listOf(
                Schema.str("query", "The query or keyword to search for.")
            ),
            requiredParameters = listOf("query")
        ),
        FunctionDeclaration(
            name = "getProductDetails",
            description = "Retrieve details for a single product by its unique product ID.",
            parameters = listOf(
                Schema.int("productId", "The numeric ID of the product.")
            ),
            requiredParameters = listOf("productId")
        ),
        FunctionDeclaration(
            name = "addToCart",
            description = "Add a product variant to the shopping cart by product ID, with size and color (pass empty or 'none' if unspecified).",
            parameters = listOf(
                Schema.int("productId", "The numeric ID of the product."),
                Schema.int("quantity", "The quantity to add."),
                Schema.str("size", "The size preference (e.g. S, M, L, or 'none')."),
                Schema.str("color", "The color preference (e.g. Black, White, or 'none').")
            ),
            requiredParameters = listOf("productId", "quantity", "size", "color")
        ),
        FunctionDeclaration(
            name = "removeFromCart",
            description = "Remove a line item from the shopping cart using its unique line ID.",
            parameters = listOf(
                Schema.str("lineId", "The unique line ID of the item in the cart.")
            ),
            requiredParameters = listOf("lineId")
        ),
        FunctionDeclaration(
            name = "updateQuantity",
            description = "Update the quantity of a product in the shopping cart.",
            parameters = listOf(
                Schema.str("lineId", "The unique line ID of the item in the cart."),
                Schema.int("quantity", "The new quantity.")
            ),
            requiredParameters = listOf("lineId", "quantity")
        ),
        FunctionDeclaration(
            name = "showCart",
            description = "Retrieve and list all products currently in the user's shopping cart.",
            parameters = emptyList(),
            requiredParameters = emptyList()
        ),
        FunctionDeclaration(
            name = "addToWishlist",
            description = "Add a product to the user's wishlist/favorites using its product ID.",
            parameters = listOf(
                Schema.int("productId", "The numeric ID of the product.")
            ),
            requiredParameters = listOf("productId")
        ),
        FunctionDeclaration(
            name = "removeFromWishlist",
            description = "Remove a product from the user's wishlist/favorites using its product ID.",
            parameters = listOf(
                Schema.int("productId", "The numeric ID of the product.")
            ),
            requiredParameters = listOf("productId")
        ),
        FunctionDeclaration(
            name = "showWishlist",
            description = "Retrieve and list all products in the user's wishlist/favorites.",
            parameters = emptyList(),
            requiredParameters = emptyList()
        ),
        FunctionDeclaration(
            name = "compareProducts",
            description = "Compare details, price, vendor, stock, sizes, and colors between two products using their IDs.",
            parameters = listOf(
                Schema.int("productId1", "The ID of the first product."),
                Schema.int("productId2", "The ID of the second product.")
            ),
            requiredParameters = listOf("productId1", "productId2")
        ),
        FunctionDeclaration(
            name = "generateOutfit",
            description = "Generate a matching style outfit recommendation containing top, bottom, and shoes based on a preference.",
            parameters = listOf(
                Schema.str("preference", "The style preference, e.g. casual, sporty, formal.")
            ),
            requiredParameters = listOf("preference")
        )
    )

    companion object {
        const val MODEL_NAME = "gemini-1.5-flash"
        private const val TAG = "AIShoppingAgent"
    }
}
