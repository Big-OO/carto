package com.example.carto.feature.favorite.data.repository

import com.example.carto.feature.favorite.data.mapper.toDomain
import com.example.carto.feature.favorite.data.mapper.toEntity
import com.example.carto.feature.favorite.domain.model.FavoriteProduct
import com.example.carto.feature.favorite.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import com.example.carto.feature.favorite.data.local.FavoriteLocalDataSource
//
//class FavoriteRepositoryImpl @Inject constructor(
//    private val localDataSource: FavoriteLocalDataSource,
//) : FavoriteRepository {
//
//    override fun observeFavorites(): Flow<List<FavoriteProduct>> =
//        localDataSource.observeFavorites().map { entities -> entities.map { it.toDomain() } }
//
//    override fun observeFavoriteIds(): Flow<Set<Long>> =
//        localDataSource.observeFavoriteIds().map { it.toSet() }
//
//    override suspend fun toggleFavorite(product: FavoriteProduct): Boolean {
//        val alreadyFavorite = localDataSource.exists(product.productId)
//        return if (alreadyFavorite) {
//            localDataSource.deleteById(product.productId)
//            false
//        } else {
//            localDataSource.insert(product.toEntity())
//            true
//        }
//    }
//
//    override suspend fun removeFavorite(productId: Long) {
//        localDataSource.deleteById(productId)
//    }
//}


import android.util.Log
import com.example.carto.core.auth.AuthUidProvider
import com.example.carto.core.di.ApplicationScope
import com.example.carto.feature.favorite.data.local.FavoriteProductEntity.Companion.GUEST_USER_ID
import com.example.carto.feature.favorite.data.mapper.toRemoteModel
import com.example.carto.feature.favorite.data.remote.FavoriteRemoteDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Singleton

@Singleton
class FavoriteRepositoryImpl @Inject constructor(
    private val localDataSource: FavoriteLocalDataSource,
    private val remoteDataSource: FavoriteRemoteDataSource,
    private val authUidProvider: AuthUidProvider,
    @ApplicationScope private val appScope: CoroutineScope,
) : FavoriteRepository {

    private val mirrorMutex = Mutex()
    private var mirrorJob: Job? = null

    init {
        appScope.launch {
            authUidProvider.observeUid()
                .distinctUntilChanged()
                .collect { uid -> onAuthUidChanged(uid) }
        }
    }

    private suspend fun onAuthUidChanged(uid: String?) {
        mirrorMutex.withLock {
            mirrorJob?.cancel()
            mirrorJob = null

            if (uid == null) return@withLock

            migrateGuestFavoritesIfNeeded(uid)

            mirrorJob = appScope.launch {
                remoteDataSource.observeFavorites(uid)
                    .catch { error ->
                        Log.w(TAG, "Favorites mirror listener failed for $uid", error)
                    }
                    .collect { remoteFavorites ->
                        localDataSource.replaceForUser(
                            userId = uid,
                            entities = remoteFavorites.map { it.toEntity(uid) },
                        )
                    }
            }
        }
    }

    private suspend fun migrateGuestFavoritesIfNeeded(uid: String) {
        val guestFavorites = localDataSource.getAllOnce(GUEST_USER_ID)
        if (guestFavorites.isEmpty()) return

        runCatching {
            guestFavorites.forEach { entity -> remoteDataSource.upsert(uid, entity.toRemoteModel()) }
        }.onFailure { error ->
            Log.w(TAG, "Failed migrating guest favorites to $uid, will retry next sign-in", error)
            return
        }

        localDataSource.clearForUser(GUEST_USER_ID)
    }

    private fun effectiveUserId(): String = authUidProvider.currentUid() ?: GUEST_USER_ID

    override fun observeFavorites(): Flow<List<FavoriteProduct>> =
        authUidProvider.observeUid()
            .map { it ?: GUEST_USER_ID }
            .distinctUntilChanged()
            .flatMapLatest { userId -> localDataSource.observeFavorites(userId) }
            .map { entities -> entities.map { it.toDomain() } }

    override fun observeFavoriteIds(): Flow<Set<Long>> =
        authUidProvider.observeUid()
            .map { it ?: GUEST_USER_ID }
            .distinctUntilChanged()
            .flatMapLatest { userId -> localDataSource.observeFavoriteIds(userId) }
            .map { it.toSet() }

    override suspend fun toggleFavorite(product: FavoriteProduct): Boolean {
        val userId = effectiveUserId()
        val alreadyFavorite = localDataSource.exists(product.productId, userId)

        return if (alreadyFavorite) {
            localDataSource.deleteById(product.productId, userId)
            pushDeleteIfSignedIn(userId, product.productId)
            false
        } else {
            localDataSource.insert(product.toEntity(userId))
            pushUpsertIfSignedIn(userId, product)
            true
        }
    }

    override suspend fun removeFavorite(productId: Long) {
        val userId = effectiveUserId()
        localDataSource.deleteById(productId, userId)
        pushDeleteIfSignedIn(userId, productId)
    }

    private fun pushUpsertIfSignedIn(userId: String, product: FavoriteProduct) {
        if (userId == GUEST_USER_ID) return
        appScope.launch {
            runCatching { remoteDataSource.upsert(userId, product.toRemoteModel()) }
                .onFailure { Log.w(TAG, "Failed pushing favorite ${product.productId} to $userId", it) }
        }
    }

    private fun pushDeleteIfSignedIn(userId: String, productId: Long) {
        if (userId == GUEST_USER_ID) return
        appScope.launch {
            runCatching { remoteDataSource.delete(userId, productId) }
                .onFailure { Log.w(TAG, "Failed deleting favorite $productId for $userId", it) }
        }
    }

    private companion object {
        const val TAG = "FavoriteRepository"
    }
}
