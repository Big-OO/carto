package com.example.carto.feature.favorite.data.remote

import com.example.carto.core.firebase.FirestoreCollections
import com.example.carto.feature.favorite.data.remote.model.FavoriteRemoteModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreFavoriteRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
) : FavoriteRemoteDataSource {

    override fun observeFavorites(userId: String): Flow<List<FavoriteRemoteModel>> = callbackFlow {
        val registration = favoritesCollection(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val favorites = snapshot?.documents.orEmpty().mapNotNull { it.toFavoriteRemoteModel() }
                trySend(favorites)
            }
        awaitClose { registration.remove() }
    }

    override suspend fun getFavoritesOnce(userId: String): List<FavoriteRemoteModel> {
        val snapshot = favoritesCollection(userId).get().await()
        return snapshot.documents.mapNotNull { it.toFavoriteRemoteModel() }
    }

    override suspend fun upsert(userId: String, favorite: FavoriteRemoteModel) {
        favoritesCollection(userId)
            .document(favorite.productId.toString())
            .set(favorite.toFirestoreMap())
            .await()
    }

    override suspend fun delete(userId: String, productId: Long) {
        favoritesCollection(userId)
            .document(productId.toString())
            .delete()
            .await()
    }

    private fun favoritesCollection(userId: String) = firestore
        .collection(FirestoreCollections.USERS)
        .document(userId)
        .collection(FirestoreCollections.FAVORITES)

    private fun DocumentSnapshot.toFavoriteRemoteModel(): FavoriteRemoteModel? {
        val productId = id.toLongOrNull() ?: return null
        val name = getString("name") ?: return null
        val price = getDouble("price") ?: return null
        val addedAt = getLong("addedAt") ?: return null
        return FavoriteRemoteModel(
            productId = productId,
            name = name,
            imageUrl = getString("imageUrl"),
            price = price,
            addedAt = addedAt,
        )
    }

    private fun FavoriteRemoteModel.toFirestoreMap(): Map<String, Any?> = mapOf(
        "productId" to productId,
        "name" to name,
        "imageUrl" to imageUrl,
        "price" to price,
        "addedAt" to addedAt,
        "updatedAt" to FieldValue.serverTimestamp(),
    )
}
