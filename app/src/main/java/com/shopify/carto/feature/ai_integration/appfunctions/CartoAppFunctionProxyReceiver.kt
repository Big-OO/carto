package com.shopify.carto.feature.ai_integration.appfunctions

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.ResultReceiver
import android.util.Log
import androidx.appfunctions.AppFunctionContext
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull

@AndroidEntryPoint
class CartoAppFunctionProxyReceiver : BroadcastReceiver() {

    @Inject
    lateinit var searchFunctions: SearchFunctions

    @Inject
    lateinit var cartFunctions: CartFunctions

    @Inject
    lateinit var wishlistFunctions: WishlistFunctions

    @Inject
    lateinit var compareFunctions: CompareFunctions

    @Inject
    lateinit var outfitFunctions: OutfitFunctions

    @Inject
    lateinit var checkoutFunctions: CheckoutFunctions

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        val functionId = intent.getStringExtra("function_id") ?: return
        val parametersJson = intent.getStringExtra("parameters_json") ?: "{}"
        val callback = intent.getParcelableExtra<android.os.Messenger>("callback") ?: return

        val pendingResult = goAsync()

        scope.launch {
            val simpleName = functionId.substringAfterLast('#')
            Log.d("AivoDebug", "CartoProxy: Executing $simpleName with $parametersJson")
            val result = try {
                executeLocally(context, functionId, parametersJson)
            } catch (e: Exception) {
                Log.e("AivoDebug", "CartoProxy: Failed execution", e)
                "Error executing proxy: ${e.message}"
            }
            Log.d("AivoDebug", "CartoProxy: Result is $result")
            if (simpleName == "searchProducts" || simpleName == "getProductDetails") {
                Log.i("CartoProxyLogger", "Item data returned to calling app (contains Image URL): $result")
            }
            val bundle = Bundle().apply {
                putString("result", result)
            }
            val msg = android.os.Message.obtain().apply {
                data = bundle
            }
            try {
                callback.send(msg)
            } catch (e: Exception) {
                Log.e("AivoDebug", "CartoProxy: Failed to send reply message", e)
            }
            pendingResult.finish()
        }
    }

    private suspend fun executeLocally(context: Context, functionId: String, parametersJson: String): String {
        val appContext = object : AppFunctionContext {
            override val context: android.content.Context
                get() = context.applicationContext
        }

        val simpleName = functionId.substringAfterLast('#')
        val jsonElement = try {
            Json.parseToJsonElement(parametersJson).jsonObject
        } catch (e: Exception) {
            null
        }

        return when (simpleName) {
            "addToCart" -> {
                val productId = jsonElement?.get("productId")?.jsonPrimitive?.longOrNull ?: 0L
                val quantity = jsonElement?.get("quantity")?.jsonPrimitive?.longOrNull?.toInt() ?: 1
                val size = jsonElement?.get("size")?.jsonPrimitive?.content ?: ""
                val color = jsonElement?.get("color")?.jsonPrimitive?.content ?: ""
                cartFunctions.addToCart(appContext, productId, quantity, size, color)
            }
            "removeFromCart" -> {
                val lineId = jsonElement?.get("lineId")?.jsonPrimitive?.content ?: ""
                cartFunctions.removeFromCart(appContext, lineId)
            }
            "showCart" -> {
                cartFunctions.showCart(appContext)
            }
            "updateQuantity" -> {
                val lineId = jsonElement?.get("lineId")?.jsonPrimitive?.content ?: ""
                val quantity = jsonElement?.get("quantity")?.jsonPrimitive?.longOrNull?.toInt() ?: 1
                cartFunctions.updateQuantity(appContext, lineId, quantity)
            }
            "compareProducts" -> {
                val productId1 = jsonElement?.get("productId1")?.jsonPrimitive?.longOrNull ?: 0L
                val productId2 = jsonElement?.get("productId2")?.jsonPrimitive?.longOrNull ?: 0L
                compareFunctions.compareProducts(appContext, productId1, productId2)
            }
            "generateOutfit" -> {
                val occasion = jsonElement?.get("occasion")?.jsonPrimitive?.content ?: ""
                outfitFunctions.generateOutfit(appContext, occasion)
            }
            "getProductDetails" -> {
                val productId = jsonElement?.get("productId")?.jsonPrimitive?.longOrNull ?: 0L
                searchFunctions.getProductDetails(appContext, productId)
            }
            "searchProducts" -> {
                val query = jsonElement?.get("query")?.jsonPrimitive?.content ?: ""
                searchFunctions.searchProducts(appContext, query)
            }
            "addToWishlist" -> {
                val productId = jsonElement?.get("productId")?.jsonPrimitive?.longOrNull ?: 0L
                wishlistFunctions.addToWishlist(appContext, productId)
            }
            "removeFromWishlist" -> {
                val productId = jsonElement?.get("productId")?.jsonPrimitive?.longOrNull ?: 0L
                wishlistFunctions.removeFromWishlist(appContext, productId)
            }
            "showWishlist" -> {
                wishlistFunctions.showWishlist(appContext)
            }
            "getCustomerInfo" -> {
                checkoutFunctions.getCustomerInfo(appContext)
            }
            "getShippingAddresses" -> {
                checkoutFunctions.getShippingAddresses(appContext)
            }
            "validatePhone" -> {
                val phone = jsonElement?.get("phone")?.jsonPrimitive?.content ?: ""
                checkoutFunctions.validatePhone(appContext, phone)
            }
            "getOrderSummary" -> {
                val addressId = jsonElement?.get("addressId")?.jsonPrimitive?.longOrNull ?: 0L
                val phone = jsonElement?.get("phone")?.jsonPrimitive?.content ?: ""
                val paymentMethod = jsonElement?.get("paymentMethod")?.jsonPrimitive?.content ?: "CASH_ON_DELIVERY"
                val firstName = jsonElement?.get("firstName")?.jsonPrimitive?.content ?: ""
                val lastName = jsonElement?.get("lastName")?.jsonPrimitive?.content ?: ""
                val email = jsonElement?.get("email")?.jsonPrimitive?.content ?: ""
                val address = jsonElement?.get("address")?.jsonPrimitive?.content ?: ""
                val city = jsonElement?.get("city")?.jsonPrimitive?.content ?: ""
                val discountCode = jsonElement?.get("discountCode")?.jsonPrimitive?.content ?: ""
                checkoutFunctions.getOrderSummary(appContext, addressId, phone, paymentMethod, firstName, lastName, email, address, city, discountCode)
            }
            "checkout" -> {
                val paymentMethod = jsonElement?.get("paymentMethod")?.jsonPrimitive?.content ?: "CASH_ON_DELIVERY"
                val confirmed = jsonElement?.get("confirmed")?.jsonPrimitive?.content?.toBooleanStrictOrNull() ?: false
                val addressId = jsonElement?.get("addressId")?.jsonPrimitive?.longOrNull ?: 0L
                val firstName = jsonElement?.get("firstName")?.jsonPrimitive?.content ?: ""
                val lastName = jsonElement?.get("lastName")?.jsonPrimitive?.content ?: ""
                val email = jsonElement?.get("email")?.jsonPrimitive?.content ?: ""
                val phone = jsonElement?.get("phone")?.jsonPrimitive?.content ?: ""
                val address = jsonElement?.get("address")?.jsonPrimitive?.content ?: ""
                val city = jsonElement?.get("city")?.jsonPrimitive?.content ?: ""
                val discountCode = jsonElement?.get("discountCode")?.jsonPrimitive?.content ?: ""
                checkoutFunctions.checkout(appContext, paymentMethod, confirmed, addressId, firstName, lastName, email, phone, address, city, discountCode)
            }
            "applyDiscountCode" -> {
                val code = jsonElement?.get("code")?.jsonPrimitive?.content ?: ""
                checkoutFunctions.applyDiscountCode(appContext, code)
            }
            "cancelOrder" -> {
                val orderId = jsonElement?.get("orderId")?.jsonPrimitive?.content ?: ""
                checkoutFunctions.cancelOrder(appContext, orderId)
            }
            else -> "Error: Function $simpleName not supported by Carto proxy."
        }
    }
}
