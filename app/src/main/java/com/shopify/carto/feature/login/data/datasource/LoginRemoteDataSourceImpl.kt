package com.shopify.carto.feature.login.data.datasource

import com.shopify.carto.feature.login.data.dto.UserDto
import com.shopify.carto.feature.login.data.mapper.toDto
import com.shopify.carto.feature.login.domain.datasource.LoginRemoteDataSource
import com.google.firebase.auth.FirebaseAuth
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
            firestore
                .collection(USERS_COLLECTION)
                .document(user.uid)
                .get()
                .await()
                .getLong(SHOPIFY_CUSTOMER_ID_FIELD)
                ?.toString()
        }.getOrNull()

        return user.toDto(customerId = customerId)
    }

    private companion object {
        const val USERS_COLLECTION = "users"
        const val SHOPIFY_CUSTOMER_ID_FIELD = "shopifyCustomerId"
    }
}
