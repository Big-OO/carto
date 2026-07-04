package com.example.carto.core.auth

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject
import javax.inject.Singleton


interface AuthUidProvider {
    fun observeUid(): Flow<String?>

    fun currentUid(): String?
}

@Singleton
class FirebaseAuthUidProvider @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
) : AuthUidProvider {

    override fun observeUid(): Flow<String?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser?.uid)
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }.distinctUntilChanged()

    override fun currentUid(): String? = firebaseAuth.currentUser?.uid
}
