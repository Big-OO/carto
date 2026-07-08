package com.shopify.carto.feature.ai_integration.ai

import android.content.Context
import android.util.Log
import com.shopify.carto.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.add
import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import java.util.concurrent.TimeUnit

class AIShoppingAgent(
    private val context: Context,
    private val appFunctionRunner: ShoppingAppFunctionRunner,
) {
    private val chatHistory = mutableListOf<JsonObject>()

    init {
        chatHistory.add(
            buildJsonObject {
                put("role", "system")
                put("content", "")
            }
        )
    }

    private fun loadAssetFile(path: String): String {
        return try {
            context.assets.open(path).bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading asset $path", e)
            ""
        }
    }

    private fun determinePersonaPath(query: String): String {
        val q = query.lowercase()
        return when {
            q.contains("outfit") || q.contains("wear") || q.contains("dress") || q.contains("suit") || q.contains("look") || q.contains("clothes for") || q.contains("attire") || q.contains("clothe") || q.contains("matching") -> "personas/outfit.md"
            q.contains("compare") || q.contains("vs") || q.contains("versus") || q.contains("difference") || q.contains("better than") || q.contains("which one") -> "personas/comparison.md"
            q.contains("style") || q.contains("trend") || q.contains("fashion") || q.contains("color") || q.contains("accessory") || q.contains("accessories") || q.contains("aesthetic") -> "personas/styling.md"
            else -> "personas/shopping.md"
        }
    }

    private fun buildSystemPrompt(personaPath: String, currentCurrency: String, exchangeRate: Double): String {
        val system = loadAssetFile("ai/system.md")
        val responseRules = loadAssetFile("ai/response_rules.md")
        val uiRules = if (!personaPath.contains("styling")) {
            loadAssetFile("ai/ui_rules.md")
        } else {
            ""
        }
        val safety = loadAssetFile("ai/safety.md")
        val tools = loadAssetFile("ai/tools.md")
        val persona = loadAssetFile("ai/$personaPath")

        return buildString {
            append("The current currency is $currentCurrency. The exchange rate is 1 USD = $exchangeRate $currentCurrency. ")
            append("Prices returned by search/product tools are stored in USD. When communicating with the user, you MUST convert all prices, discounts, and totals from USD to $currentCurrency by multiplying the USD price by $exchangeRate. Always present prices formatted in $currentCurrency. ")
            append("Note that the shipping fee is a flat 80 $currentCurrency.\n\n")
            if (system.isNotBlank()) {
                append(system).append("\n\n")
            }
            if (responseRules.isNotBlank()) {
                append("## Response Rules\n").append(responseRules).append("\n\n")
            }
            if (uiRules.isNotBlank()) {
                append("## UI Presentation Rules\n").append(uiRules).append("\n\n")
            }
            if (safety.isNotBlank()) {
                append("## Safety Constraints\n").append(safety).append("\n\n")
            }
            if (tools.isNotBlank()) {
                append("## Orchestration and Tools\n").append(tools).append("\n\n")
            }
            if (persona.isNotBlank()) {
                append("## Persona Specific Instructions\n").append(persona)
            }
        }.trim()
    }

    suspend fun sendMessage(userMessage: String, currentCurrency: String, exchangeRate: Double, onStep: (String) -> Unit): AgentResult {
        Log.d(TAG, "Sending message to Custom AI: $userMessage")

        val personaPath = determinePersonaPath(userMessage)
        val systemPrompt = buildSystemPrompt(personaPath, currentCurrency, exchangeRate)

        if (chatHistory.isNotEmpty() && chatHistory[0]["role"]?.jsonPrimitive?.content == "system") {
            chatHistory[0] = buildJsonObject {
                put("role", "system")
                put("content", systemPrompt)
            }
        } else {
            chatHistory.add(0, buildJsonObject {
                put("role", "system")
                put("content", systemPrompt)
            })
        }

        chatHistory.add(
            buildJsonObject {
                put("role", "user")
                put("content", userMessage)
            }
        )

        var finished = false
        var assistantContent = ""
        val toolProductIds = mutableListOf<Long>()

        while (!finished) {
            val requestBody = buildJsonObject {
                put("model", "gpt-oss:120b")
                put("messages", buildJsonArray { chatHistory.forEach { add(it) } })
                put("tools", getToolsJson())
                put("stream", false)
            }

            val responseString = callCustomApi(requestBody)
            Log.d(TAG, "Response from Custom AI: $responseString")

            val responseObj = Json.parseToJsonElement(responseString).jsonObject
            val messageObj = responseObj["message"]?.jsonObject 
                ?: responseObj["choices"]?.jsonArray?.firstOrNull()?.jsonObject?.get("message")?.jsonObject
                ?: throw IllegalStateException("Could not parse message from response: $responseString")

            val toolCalls = messageObj["tool_calls"]?.jsonArray

            if (toolCalls != null && toolCalls.isNotEmpty()) {
                // Append the assistant message with tool calls to history
                chatHistory.add(messageObj)

                for (toolCall in toolCalls) {
                    val callObj = toolCall.jsonObject
                    val functionObj = callObj["function"]?.jsonObject ?: continue
                    val functionName = functionObj["name"]?.jsonPrimitive?.content ?: continue

                    // Parse arguments safely (supporting both JSON Object and String-encoded JSON)
                    val argsElement = functionObj["arguments"] ?: buildJsonObject {}
                    val rawArgsMap = when (argsElement) {
                        is JsonObject -> argsElement
                        is JsonPrimitive -> {
                            try {
                                Json.parseToJsonElement(argsElement.content).jsonObject
                            } catch (e: Exception) {
                                buildJsonObject {}
                            }
                        }
                        else -> buildJsonObject {}
                    }

                    val sanitizedArgs = sanitizeJsonMap(rawArgsMap)

                    val stepMessage = when (functionName) {
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
                        "getCustomerInfo" -> "Retrieving your profile..."
                        "getShippingAddresses" -> "Loading shipping addresses..."
                        "validatePhone" -> "Validating phone number..."
                        "getOrderSummary" -> "Preparing order summary..."
                        "checkout" -> "Placing your order..."
                        "cancelOrder" -> "Cancelling order..."
                        else -> "Processing request..."
                    }
                    onStep(stepMessage)

                    val result = appFunctionRunner.execute(functionName, sanitizedArgs)
                    toolProductIds.addAll(extractProductIdsFromString(result))

                    val toolCallId = callObj["id"]?.jsonPrimitive?.content ?: ""
                    chatHistory.add(
                        buildJsonObject {
                            put("role", "tool")
                            put("name", functionName)
                            put("tool_call_id", toolCallId)
                            put("content", result)
                        }
                    )
                }
            } else {
                assistantContent = messageObj["content"]?.jsonPrimitive?.content ?: ""
                chatHistory.add(messageObj)
                finished = true
            }
        }

        val finalContent = if (assistantContent.isNotBlank()) assistantContent else "(The assistant did not return any text.)"
        val assistantProductIds = extractProductIdsFromString(finalContent)
        val chosenProductIds = if (assistantProductIds.isNotEmpty()) {
            assistantProductIds
        } else {
            toolProductIds.distinct()
        }

        return AgentResult(finalContent, chosenProductIds)
    }

    private suspend fun callCustomApi(body: JsonObject): String = withContext(Dispatchers.IO) {
        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        val baseUrl = if (BuildConfig.AI_API_BASE_URL.isNotBlank()) {
            BuildConfig.AI_API_BASE_URL.trimEnd('/')
        } else {
            "https://ollama.com"
        }
        val url = "$baseUrl/api/chat"

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = body.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .addHeader("Authorization", "Bearer ${BuildConfig.AI_API_KEY}")
            .build()

        client.newCall(request).execute().use { response ->
            val bodyString = response.body?.string() ?: ""
            if (!response.isSuccessful) {
                throw IllegalStateException("API error: ${response.code} ${response.message}\n$bodyString")
            }
            bodyString
        }
    }

    private fun sanitizeJsonMap(map: Map<String, JsonElement>): Map<String, JsonElement> {
        return map.mapValues { (_, value) ->
            if (value is JsonPrimitive && value.isString) {
                val strContent = value.content
                val intValue = strContent.toIntOrNull()
                if (intValue != null) {
                    JsonPrimitive(intValue)
                } else {
                    val longValue = strContent.toLongOrNull()
                    if (longValue != null) {
                        JsonPrimitive(longValue)
                    } else {
                        val booleanValue = strContent.toBooleanStrictOrNull()
                        if (booleanValue != null) {
                            JsonPrimitive(booleanValue)
                        } else {
                            value
                        }
                    }
                }
            } else {
                value
            }
        }
    }

    private fun getToolsJson(): JsonArray {
        return buildJsonArray {
            addJsonObject {
                put("type", "function")
                putJsonObject("function") {
                    put("name", "searchProducts")
                    put("description", "Search for products in the store by keyword, category, vendor, brand, or query.")
                    putJsonObject("parameters") {
                        put("type", "object")
                        putJsonObject("properties") {
                            putJsonObject("query") {
                                put("type", "string")
                                put("description", "The query or keyword to search for.")
                            }
                        }
                        putJsonArray("required") {
                            add("query")
                        }
                    }
                }
            }
            addJsonObject {
                put("type", "function")
                putJsonObject("function") {
                    put("name", "getProductDetails")
                    put("description", "Retrieve details for a single product by its unique product ID.")
                    putJsonObject("parameters") {
                        put("type", "object")
                        putJsonObject("properties") {
                            putJsonObject("productId") {
                                put("type", "integer")
                                put("description", "The numeric ID of the product.")
                            }
                        }
                        putJsonArray("required") {
                            add("productId")
                        }
                    }
                }
            }
            addJsonObject {
                put("type", "function")
                putJsonObject("function") {
                    put("name", "addToCart")
                    put("description", "Add a product variant to the shopping cart by product ID, with size and color (pass empty or 'none' if unspecified).")
                    putJsonObject("parameters") {
                        put("type", "object")
                        putJsonObject("properties") {
                            putJsonObject("productId") {
                                put("type", "integer")
                                put("description", "The numeric ID of the product.")
                            }
                            putJsonObject("quantity") {
                                put("type", "integer")
                                put("description", "The quantity to add.")
                            }
                            putJsonObject("size") {
                                put("type", "string")
                                put("description", "The size preference (e.g. S, M, L, or 'none').")
                            }
                            putJsonObject("color") {
                                put("type", "string")
                                put("description", "The color preference (e.g. Black, White, or 'none').")
                            }
                        }
                        putJsonArray("required") {
                            add("productId")
                            add("quantity")
                            add("size")
                            add("color")
                        }
                    }
                }
            }
            addJsonObject {
                put("type", "function")
                putJsonObject("function") {
                    put("name", "removeFromCart")
                    put("description", "Remove a line item from the shopping cart using its unique line ID.")
                    putJsonObject("parameters") {
                        put("type", "object")
                        putJsonObject("properties") {
                            putJsonObject("lineId") {
                                put("type", "string")
                                put("description", "The unique line ID of the item in the cart.")
                            }
                        }
                        putJsonArray("required") {
                            add("lineId")
                        }
                    }
                }
            }
            addJsonObject {
                put("type", "function")
                putJsonObject("function") {
                    put("name", "updateQuantity")
                    put("description", "Update the quantity of a product in the shopping cart.")
                    putJsonObject("parameters") {
                        put("type", "object")
                        putJsonObject("properties") {
                            putJsonObject("lineId") {
                                put("type", "string")
                                put("description", "The unique line ID of the item in the cart.")
                            }
                            putJsonObject("quantity") {
                                put("type", "integer")
                                put("description", "The new quantity.")
                            }
                        }
                        putJsonArray("required") {
                            add("lineId")
                            add("quantity")
                        }
                    }
                }
            }
            addJsonObject {
                put("type", "function")
                putJsonObject("function") {
                    put("name", "showCart")
                    put("description", "Retrieve and list all products currently in the user's shopping cart.")
                    putJsonObject("parameters") {
                        put("type", "object")
                        putJsonObject("properties") { }
                    }
                }
            }
            addJsonObject {
                put("type", "function")
                putJsonObject("function") {
                    put("name", "addToWishlist")
                    put("description", "Add a product to the user's wishlist/favorites using its product ID.")
                    putJsonObject("parameters") {
                        put("type", "object")
                        putJsonObject("properties") {
                            putJsonObject("productId") {
                                put("type", "integer")
                                put("description", "The numeric ID of the product.")
                            }
                        }
                        putJsonArray("required") {
                            add("productId")
                        }
                    }
                }
            }
            addJsonObject {
                put("type", "function")
                putJsonObject("function") {
                    put("name", "removeFromWishlist")
                    put("description", "Remove a product from the user's wishlist/favorites using its product ID.")
                    putJsonObject("parameters") {
                        put("type", "object")
                        putJsonObject("properties") {
                            putJsonObject("productId") {
                                put("type", "integer")
                                put("description", "The numeric ID of the product.")
                            }
                        }
                        putJsonArray("required") {
                            add("productId")
                        }
                    }
                }
            }
            addJsonObject {
                put("type", "function")
                putJsonObject("function") {
                    put("name", "showWishlist")
                    put("description", "Retrieve and list all products in the user's wishlist/favorites.")
                    putJsonObject("parameters") {
                        put("type", "object")
                        putJsonObject("properties") { }
                    }
                }
            }
            addJsonObject {
                put("type", "function")
                putJsonObject("function") {
                    put("name", "compareProducts")
                    put("description", "Compare details, price, vendor, stock, sizes, and colors between two products using their IDs.")
                    putJsonObject("parameters") {
                        put("type", "object")
                        putJsonObject("properties") {
                            putJsonObject("productId1") {
                                put("type", "integer")
                                put("description", "The ID of the first product.")
                            }
                            putJsonObject("productId2") {
                                put("type", "integer")
                                put("description", "The ID of the second product.")
                            }
                        }
                        putJsonArray("required") {
                            add("productId1")
                            add("productId2")
                        }
                    }
                }
            }
            addJsonObject {
                put("type", "function")
                putJsonObject("function") {
                    put("name", "generateOutfit")
                    put("description", "Generate a matching style outfit recommendation containing top, bottom, and shoes based on a preference.")
                    putJsonObject("parameters") {
                        put("type", "object")
                        putJsonObject("properties") {
                            putJsonObject("preference") {
                                put("type", "string")
                                put("description", "The style preference, e.g. casual, sporty, formal.")
                            }
                        }
                        putJsonArray("required") {
                            add("preference")
                        }
                    }
                }
            }
            addJsonObject {
                put("type", "function")
                putJsonObject("function") {
                    put("name", "getCustomerInfo")
                    put("description", "Retrieve the current customer's profile information (name, email, phone). Use this as the first step when placing an order to verify customer details.")
                    putJsonObject("parameters") {
                        put("type", "object")
                        putJsonObject("properties") { }
                    }
                }
            }
            addJsonObject {
                put("type", "function")
                putJsonObject("function") {
                    put("name", "getShippingAddresses")
                    put("description", "Retrieve all saved shipping addresses for the customer. Present them to the user so they can choose which address to ship to.")
                    putJsonObject("parameters") {
                        put("type", "object")
                        putJsonObject("properties") { }
                    }
                }
            }
            addJsonObject {
                put("type", "function")
                putJsonObject("function") {
                    put("name", "validatePhone")
                    put("description", "Validate and normalize a phone number. Returns VALID with normalized number or INVALID if the phone cannot be validated.")
                    putJsonObject("parameters") {
                        put("type", "object")
                        putJsonObject("properties") {
                            putJsonObject("phone") {
                                put("type", "string")
                                put("description", "The phone number to validate.")
                            }
                        }
                        putJsonArray("required") {
                            add("phone")
                        }
                    }
                }
            }
            addJsonObject {
                put("type", "function")
                putJsonObject("function") {
                    put("name", "getOrderSummary")
                    put("description", "Generate a complete order summary for user review WITHOUT placing the order. Shows all products, prices, shipping address, customer info, and total. Use this before asking the user for final confirmation on COD orders.")
                    putJsonObject("parameters") {
                        put("type", "object")
                        putJsonObject("properties") {
                            putJsonObject("addressId") {
                                put("type", "integer")
                                put("description", "ID of the selected shipping address. Pass 0 if using manual address/city.")
                            }
                            putJsonObject("phone") {
                                put("type", "string")
                                put("description", "The validated phone number.")
                            }
                            putJsonObject("paymentMethod") {
                                put("type", "string")
                                put("description", "Payment method: 'CASH_ON_DELIVERY', 'CARD', or 'DIGITAL_WALLET'.")
                            }
                            putJsonObject("firstName") {
                                put("type", "string")
                                put("description", "Override first name (only if user provided it manually).")
                            }
                            putJsonObject("lastName") {
                                put("type", "string")
                                put("description", "Override last name (only if user provided it manually).")
                            }
                            putJsonObject("email") {
                                put("type", "string")
                                put("description", "Override email (only if user provided it manually).")
                            }
                            putJsonObject("address") {
                                put("type", "string")
                                put("description", "Manual street address (only if no saved address selected).")
                            }
                            putJsonObject("city") {
                                put("type", "string")
                                put("description", "Manual city (only if no saved address selected).")
                            }
                        }
                        putJsonArray("required") {
                            add("addressId")
                            add("phone")
                            add("paymentMethod")
                        }
                    }
                }
            }
            addJsonObject {
                put("type", "function")
                putJsonObject("function") {
                    put("name", "checkout")
                    put("description", "Place the order AFTER all information has been verified and the user has explicitly confirmed. The 'confirmed' parameter MUST be true. For COD, only call after showing getOrderSummary and receiving user confirmation. For Card/Wallet, call to get the payment client secret.")
                    putJsonObject("parameters") {
                        put("type", "object")
                        putJsonObject("properties") {
                            putJsonObject("paymentMethod") {
                                put("type", "string")
                                put("description", "Payment method: 'CASH_ON_DELIVERY', 'CARD', or 'DIGITAL_WALLET'.")
                            }
                            putJsonObject("confirmed") {
                                put("type", "boolean")
                                put("description", "Must be true to place the order. Only set true after explicit user confirmation.")
                            }
                            putJsonObject("addressId") {
                                put("type", "integer")
                                put("description", "ID of the selected shipping address. Pass 0 if using manual address/city.")
                            }
                            putJsonObject("firstName") {
                                put("type", "string")
                                put("description", "Customer first name.")
                            }
                            putJsonObject("lastName") {
                                put("type", "string")
                                put("description", "Customer last name.")
                            }
                            putJsonObject("email") {
                                put("type", "string")
                                put("description", "Customer email.")
                            }
                            putJsonObject("phone") {
                                put("type", "string")
                                put("description", "Validated phone number.")
                            }
                            putJsonObject("address") {
                                put("type", "string")
                                put("description", "Manual street address (only if addressId is 0).")
                            }
                            putJsonObject("city") {
                                put("type", "string")
                                put("description", "Manual city (only if addressId is 0).")
                            }
                        }
                        putJsonArray("required") {
                            add("paymentMethod")
                            add("confirmed")
                            add("addressId")
                            add("firstName")
                            add("lastName")
                            add("email")
                            add("phone")
                        }
                    }
                }
            }
            addJsonObject {
                put("type", "function")
                putJsonObject("function") {
                    put("name", "cancelOrder")
                    put("description", "Cancel an existing order using its order ID.")
                    putJsonObject("parameters") {
                        put("type", "object")
                        putJsonObject("properties") {
                            putJsonObject("orderId") {
                                put("type", "string")
                                put("description", "The Shopify order ID to cancel.")
                            }
                        }
                        putJsonArray("required") {
                            add("orderId")
                        }
                    }
                }
            }
        }
    }

    private fun extractProductIdsFromString(text: String): List<Long> {
        val ids = mutableListOf<Long>()
        val digit13Regex = Regex("""\b(\d{13})\b""")
        digit13Regex.findAll(text).forEach { match ->
            match.value.toLongOrNull()?.let { ids.add(it) }
        }
        val labelRegex = Regex("""Product ID:\s*(\d+)""", RegexOption.IGNORE_CASE)
        labelRegex.findAll(text).forEach { match ->
            match.groupValues[1].toLongOrNull()?.let { ids.add(it) }
        }
        return ids.distinct()
    }

    companion object {
        private const val TAG = "AIShoppingAgent"
    }
}

data class AgentResult(
    val responseText: String,
    val productIds: List<Long>
)
