package com.example.carto.feature.addresses.data.remote

import com.example.carto.feature.addresses.data.remote.error.CustomerIdError
import com.example.carto.feature.addresses.data.result.AddressDataResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.io.IOException
import javax.inject.Inject

class CustomerIdDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) {
    suspend fun getShopifyCustomerId(): AddressDataResult<Long, CustomerIdError> {
        return try {
            val uid = firebaseAuth.currentUser?.uid
                ?: return AddressDataResult.Failure(
                    message = "No Firebase user is logged in",
                    error = CustomerIdError.MissingCustomer,
                )

            val snapshot = firestore.collection("users").document(uid).get().await()
            val customerId = when (val rawCustomerId = snapshot.get("shopifyCustomerId")) {
                is Long -> rawCustomerId
                is Int -> rawCustomerId.toLong()
                is Double -> rawCustomerId.toLong()
                is String -> rawCustomerId.toLongOrNull()
                else -> null
            }

            if (customerId == null) {
                AddressDataResult.Failure(
                    error = CustomerIdError.MissingCustomer,
                    message = "shopifyCustomerId is missing for user=$uid",
                )
            } else {
                AddressDataResult.Success(customerId)
            }
        } catch (throwable: IOException) {
            AddressDataResult.Failure(error = CustomerIdError.Network, message = throwable.message)
        } catch (throwable: Throwable) {
            AddressDataResult.Failure(error = CustomerIdError.Unknown, message = throwable.message)
        }
    }
}
