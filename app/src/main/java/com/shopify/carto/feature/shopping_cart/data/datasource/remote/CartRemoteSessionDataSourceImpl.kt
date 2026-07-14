package com.shopify.carto.feature.shopping_cart.data.datasource.remote

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.shopify.carto.core.auth.AuthUidProvider
import com.shopify.carto.core.common.exception.DataException
import com.shopify.carto.core.firebase.FirestoreCollections
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

private const val TAG = "CartSessionRemoteDS"
private const val COLLECTION_USER_CARTS = "user_carts"
private const val FIELD_CART_ID = "cartId"
private const val FIELD_UPDATED_AT = "updatedAt"

class CartSessionRemoteDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val authUidProvider: AuthUidProvider
) : CartSessionRemoteDataSource {

    override suspend fun getCartId(customerId: String): Result<String?> {
        return try {
            val uid = authUidProvider.currentUid()
            var cartId: String? = null
            if (!uid.isNullOrBlank()) {
                cartId = runCatching {
                    firestore.collection(COLLECTION_USER_CARTS)
                        .document(uid)
                        .get()
                        .await()
                        .getString(FIELD_CART_ID)
                }.getOrNull()

                if (cartId == null) {
                    cartId = runCatching {
                        firestore.collection(FirestoreCollections.USERS)
                            .document(uid)
                            .get()
                            .await()
                            .getString(FIELD_CART_ID)
                    }.getOrNull()
                }
            }
            if (cartId == null && customerId.isNotBlank()) {
                cartId = runCatching {
                    firestore.collection(COLLECTION_USER_CARTS)
                        .document(customerId)
                        .get()
                        .await()
                        .getString(FIELD_CART_ID)
                }.getOrNull()

                if (cartId == null) {
                    cartId = runCatching {
                        firestore.collection(FirestoreCollections.USERS)
                            .document(customerId)
                            .get()
                            .await()
                            .getString(FIELD_CART_ID)
                    }.getOrNull()
                }
            }
            Result.success(cartId)
        } catch (exception: Exception) {
            if (exception is CancellationException) throw exception
            Log.e(TAG, "getCartId failed: ${exception.message}", exception)
            Result.failure(DataException.Unknown(exception))
        }
    }

    override suspend fun saveCartId(customerId: String, cartId: String): Result<Unit> {
        return try {
            val data = mapOf(
                FIELD_CART_ID to cartId,
                FIELD_UPDATED_AT to FieldValue.serverTimestamp()
            )
            val uid = authUidProvider.currentUid()
            if (!uid.isNullOrBlank()) {
                runCatching {
                    firestore.collection(COLLECTION_USER_CARTS)
                        .document(uid)
                        .set(data)
                        .await()
                }
                runCatching {
                    firestore.collection(FirestoreCollections.USERS)
                        .document(uid)
                        .set(mapOf(FIELD_CART_ID to cartId), com.google.firebase.firestore.SetOptions.merge())
                        .await()
                }
            }
            if (customerId.isNotBlank() && customerId != uid) {
                runCatching {
                    firestore.collection(COLLECTION_USER_CARTS)
                        .document(customerId)
                        .set(data)
                        .await()
                }
                runCatching {
                    firestore.collection(FirestoreCollections.USERS)
                        .document(customerId)
                        .set(mapOf(FIELD_CART_ID to cartId), com.google.firebase.firestore.SetOptions.merge())
                        .await()
                }
            }
            Result.success(Unit)
        } catch (exception: Exception) {
            if (exception is CancellationException) throw exception
            Log.e(TAG, "saveCartId failed: ${exception.message}", exception)
            Result.failure(DataException.Unknown(exception))
        }
    }

    override suspend fun clearCartId(customerId: String): Result<Unit> {
        return try {
            val uid = authUidProvider.currentUid()
            if (!uid.isNullOrBlank()) {
                runCatching {
                    firestore.collection(COLLECTION_USER_CARTS)
                        .document(uid)
                        .delete()
                        .await()
                }
                runCatching {
                    firestore.collection(FirestoreCollections.USERS)
                        .document(uid)
                        .update(FIELD_CART_ID, FieldValue.delete())
                        .await()
                }
            }
            if (customerId.isNotBlank() && customerId != uid) {
                runCatching {
                    firestore.collection(COLLECTION_USER_CARTS)
                        .document(customerId)
                        .delete()
                        .await()
                }
            }
            Result.success(Unit)
        } catch (exception: Exception) {
            if (exception is CancellationException) throw exception
            Log.e(TAG, "clearCartId failed: ${exception.message}", exception)
            Result.failure(DataException.Unknown(exception))
        }
    }
}