package com.shopify.carto.feature.login.data.datasource

import android.util.Log
import com.shopify.carto.feature.login.data.dto.UserDto
import com.shopify.carto.feature.login.data.mapper.toDto
import com.shopify.carto.feature.login.domain.datasource.LoginRemoteDataSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LoginRemoteDataSourceImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : LoginRemoteDataSource {

    override suspend fun login(
        email: String,
        password: String,
    ): UserDto {
        val result = firebaseAuth
            .signInWithEmailAndPassword(email, password)
            .await()

        val user = requireNotNull(result.user)
        val customerId = runCatching {
            val snapshot = firestore
                .collection(USERS_COLLECTION)
                .document(user.uid)
                .get()
                .await()
            when (val raw = snapshot.get(SHOPIFY_CUSTOMER_ID_FIELD)) {
                is Long -> raw.toString()
                is Int -> raw.toString()
                is Double -> raw.toLong().toString()
                is String -> raw
                else -> null
            }
        }.getOrNull()

        return user.toDto(customerId = customerId)
    }

    override suspend fun loginWithGoogle(
        idToken: String,
    ): UserDto {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val result = firebaseAuth
            .signInWithCredential(credential)
            .await()

        val user = requireNotNull(result.user)
        val customerId = runCatching {
            val docRef = firestore
                .collection(USERS_COLLECTION)
                .document(user.uid)
            val snapshot = docRef.get().await()

            if (!snapshot.exists()) {
                val docData = hashMapOf(
                    "email" to (user.email ?: ""),
                    "displayName" to (user.displayName ?: "Google User")
                )
                docRef.set(docData).await()
            }

            when (val raw = snapshot.get(SHOPIFY_CUSTOMER_ID_FIELD)) {
                is Long -> raw.toString()
                is Int -> raw.toString()
                is Double -> raw.toLong().toString()
                is String -> raw
                else -> null
            }
        }.getOrNull()

        return user.toDto(customerId = customerId)
    }

    private companion object {
        const val USERS_COLLECTION = "users"
        const val SHOPIFY_CUSTOMER_ID_FIELD = "shopifyCustomerId"
    }
}
