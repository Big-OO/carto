package com.example.carto.registration.data.firebase

import com.example.carto.registration.data.result.RegisterDataResult
import com.example.carto.registration.domain.model.RegisterFailure
import com.example.carto.registration.domain.model.RegisterFailureType
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.io.IOException
import javax.inject.Inject

class FirebaseRegisterDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) {
    suspend fun createUser(
        fullName: String,
        email: String,
        password: String,
    ): RegisterDataResult<String> {
        return try {
            val authResult = firebaseAuth
                .createUserWithEmailAndPassword(email.trim(), password)
                .await()

            val user = authResult.user
                ?: return RegisterDataResult.Failure(
                    RegisterFailure(
                        type = RegisterFailureType.FirebaseSyncFailed,
                        message = "Firebase returned a successful auth result without a FirebaseUser.",
                    )
                )

            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(fullName.trim())
                .build()

            user.updateProfile(profileUpdates).await()
            user.sendEmailVerification().await()

            RegisterDataResult.Success(user.uid)
        } catch (exception: Exception) {
            RegisterDataResult.Failure(exception.toRegisterFailure())
        }
    }

    suspend fun saveUserProfile(
        firebaseUid: String,
        fullName: String,
        email: String,
        shopifyCustomerId: Long,
    ): RegisterDataResult<Unit> {
        return try {
            val userDocument = mapOf(
                "uid" to firebaseUid,
                "fullName" to fullName.trim(),
                "email" to email.trim(),
                "shopifyCustomerId" to shopifyCustomerId,
                "emailVerified" to false,
                "provider" to "email_password",
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp(),
            )

            firestore
                .collection(USERS_COLLECTION)
                .document(firebaseUid)
                .set(userDocument)
                .await()

            RegisterDataResult.Success(Unit)
        } catch (exception: Exception) {
            RegisterDataResult.Failure(exception.toRegisterFailure())
        }
    }

    suspend fun deleteCurrentUserSilently() {
        runCatching {
            firebaseAuth.currentUser?.delete()?.await()
        }
    }

    private fun Exception.toRegisterFailure(): RegisterFailure {
        return when (this) {
            is FirebaseAuthUserCollisionException -> RegisterFailure(
                type = RegisterFailureType.EmailAlreadyUsed,
                message = "Firebase Auth rejected registration because this email already exists. $safeMessage",
            )

            is FirebaseAuthWeakPasswordException -> RegisterFailure(
                type = RegisterFailureType.WeakPassword,
                message = "Firebase Auth rejected registration because the password is weak. $safeMessage",
            )

            is FirebaseAuthInvalidCredentialsException -> RegisterFailure(
                type = RegisterFailureType.InvalidEmail,
                message = "Firebase Auth rejected registration because the email/password credentials are invalid. $safeMessage",
            )

            is FirebaseNetworkException -> RegisterFailure(
                type = RegisterFailureType.Network,
                message = "Firebase Auth/Firestore network failure. $safeMessage",
            )

            is IOException -> RegisterFailure(
                type = RegisterFailureType.Network,
                message = "Firebase IO/network failure. $safeMessage",
            )

            is FirebaseException -> RegisterFailure(
                type = RegisterFailureType.FirebaseSyncFailed,
                message = "Firebase operation failed. $safeMessage",
            )

            else -> RegisterFailure(
                type = RegisterFailureType.Unknown,
                message = "Unexpected Firebase registration error: ${this::class.java.name}. $safeMessage",
            )
        }
    }

    private val Exception.safeMessage: String
        get() = message.orEmpty().ifBlank { "No message provided." }

    private companion object {
        const val USERS_COLLECTION = "users"
    }
}
