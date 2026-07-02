package com.example.carto.feature.addresses.data.remote

import com.example.carto.feature.addresses.data.result.AddressDataResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CustomerIdDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) {
    suspend fun getShopifyCustomerId(): AddressDataResult<Long> {
        return try {
            val uid = firebaseAuth.currentUser?.uid
                ?: return AddressDataResult.Failure(developerMessage = "No Firebase user is logged in")

            val snapshot = firestore.collection("users").document(uid).get().await()
            val rawCustomerId = snapshot.get("shopifyCustomerId")

            val customerId = when (rawCustomerId) {
                is Long -> rawCustomerId
                is Int -> rawCustomerId.toLong()
                is Double -> rawCustomerId.toLong()
                is String -> rawCustomerId.toLongOrNull()
                else -> null
            }

            if (customerId == null) {
                AddressDataResult.Failure(developerMessage = "shopifyCustomerId is missing for user=$uid")
            } else {
                AddressDataResult.Success(customerId)
            }
        } catch (throwable: Throwable) {
            AddressDataResult.Failure(developerMessage = throwable.message)
        }
    }
}
