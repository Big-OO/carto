package com.shopify.carto.feature.ai_integration.ai

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appfunctions.AppFunctionData
import androidx.appfunctions.AppFunctionManager
import androidx.appfunctions.AppFunctionSearchSpec
import androidx.appfunctions.ExecuteAppFunctionRequest
import androidx.appfunctions.ExecuteAppFunctionResponse
import androidx.appfunctions.metadata.AppFunctionBooleanTypeMetadata
import androidx.appfunctions.metadata.AppFunctionDoubleTypeMetadata
import androidx.appfunctions.metadata.AppFunctionFloatTypeMetadata
import androidx.appfunctions.metadata.AppFunctionIntTypeMetadata
import androidx.appfunctions.metadata.AppFunctionLongTypeMetadata
import androidx.appfunctions.metadata.AppFunctionMetadata
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.double
import kotlinx.serialization.json.float
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long

class ShoppingAppFunctionRunner(
    private val context: Context,
) {
    private val packageName: String = context.packageName

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    suspend fun execute(simpleName: String, args: Map<String, JsonElement>): String {
        Log.d(TAG, "Executing App Function: $simpleName with args: $args")
        val manager = AppFunctionManager.getInstance(context)
            ?: return "Error: AppFunctionManager not available."

        val metadataList = discoverFunctions()
        val metadata = metadataList.firstOrNull { it.id.substringAfterLast('#') == simpleName }
            ?: return "Error: Function '$simpleName' not found."

        try {
            val request = ExecuteAppFunctionRequest(
                packageName,
                metadata.id,
                buildParameters(metadata, args),
            )

            val startTime = System.currentTimeMillis()
            val response = manager.executeAppFunction(request)
            val duration = System.currentTimeMillis() - startTime
            Log.d(TAG, "Completed execute: $simpleName in $duration ms")

            return when (response) {
                is ExecuteAppFunctionResponse.Success -> readReturnValue(metadata, response.returnValue)
                is ExecuteAppFunctionResponse.Error -> {
                    Log.e(TAG, "Execution failed: ${response.error.errorMessage}")
                    "Error: ${response.error.errorMessage}"
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during execution of $simpleName", e)
            return "Error: ${e.message}"
        }
    }

    private suspend fun discoverFunctions(): List<AppFunctionMetadata> {
        val manager = AppFunctionManager.getInstance(context) ?: return emptyList()
        val spec = AppFunctionSearchSpec(packageNames = setOf(packageName))
        return manager
            .observeAppFunctions(spec)
            .first()
            .flatMap { it.appFunctions }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun buildParameters(metadata: AppFunctionMetadata, args: Map<String, JsonElement>): AppFunctionData {
        val builder = AppFunctionData.Builder(metadata.parameters, metadata.components)
        metadata.parameters.forEach { parameter ->
            val value = args[parameter.name]?.jsonPrimitive ?: return@forEach
            when (parameter.dataType) {
                is AppFunctionIntTypeMetadata -> builder.setInt(parameter.name, value.int)
                is AppFunctionLongTypeMetadata -> builder.setLong(parameter.name, value.long)
                is AppFunctionFloatTypeMetadata -> builder.setFloat(parameter.name, value.float)
                is AppFunctionDoubleTypeMetadata -> builder.setDouble(parameter.name, value.double)
                is AppFunctionBooleanTypeMetadata -> builder.setBoolean(parameter.name, value.boolean)
                else -> builder.setString(parameter.name, value.content)
            }
        }
        return builder.build()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun readReturnValue(metadata: AppFunctionMetadata, data: AppFunctionData): String {
        val key = ExecuteAppFunctionResponse.Success.PROPERTY_RETURN_VALUE
        return when (metadata.response.valueType) {
            is AppFunctionIntTypeMetadata -> data.getInt(key).toString()
            is AppFunctionLongTypeMetadata -> data.getLong(key).toString()
            is AppFunctionFloatTypeMetadata -> data.getFloat(key).toString()
            is AppFunctionDoubleTypeMetadata -> data.getDouble(key).toString()
            is AppFunctionBooleanTypeMetadata -> data.getBoolean(key).toString()
            else -> data.getString(key) ?: "Success."
        }
    }

    companion object {
        private const val TAG = "ShoppingAppFunctionRunner"
    }
}
