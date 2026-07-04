package com.example.carto.core.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.room.Room
import com.example.carto.BuildConfig
import com.example.carto.core.config.ShopifyConfig
import com.example.carto.core.database.CartoDatabase
import com.example.carto.feature.favorite.data.local.FavoriteProductDao
import com.example.carto.feature.search.data.local.SearchHistoryDao
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

import com.example.carto.feature.profile.data.local.CustomerProfileDao

@Module
@InstallIn(SingletonComponent::class)
object SharedDIModule {

    private const val APP_DATA_STORE_NAME = "carto_session.preferences_pb"


    @Provides
    @Singleton
    fun provideAppDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() },
            scope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
            produceFile = { context.preferencesDataStoreFile(APP_DATA_STORE_NAME) },
        )
    }

    @Provides
    @Singleton
    fun provideShopifyConfig(): ShopifyConfig {
        return ShopifyConfig(
            hostname = BuildConfig.SHOPIFY_HOSTNAME,
            apiVersion = BuildConfig.SHOPIFY_API_VERSION,
            adminAccessToken = BuildConfig.SHOPIFY_ADMIN_ACCESS_TOKEN,
        )
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(config: ShopifyConfig): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BASIC
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val request = chain.request()
                    .newBuilder()
                    .addHeader("Accept", "application/json")
                    .addHeader("Content-Type", "application/json")
                    .addHeader("X-Shopify-Access-Token", config.adminAccessToken)
                    .build()

                chain.proceed(request)
            }
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        config: ShopifyConfig,
        client: OkHttpClient,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://${config.hostname}/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideCartoDatabase(
        @ApplicationContext context: Context,
    ): CartoDatabase {
        return Room.databaseBuilder(
            context,
            CartoDatabase::class.java,
            CartoDatabase.DATABASE_NAME,
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    @Singleton
    fun provideCustomerProfileDao(database: CartoDatabase): CustomerProfileDao {
        return database.customerProfileDao()
    }

    @Provides
    @Singleton
    fun provideSearchHistoryDao(database: CartoDatabase): SearchHistoryDao {
        return database.searchHistoryDao()
    }
    @Provides
    @Singleton
    fun provideFavoriteProductDao(database: CartoDatabase): FavoriteProductDao {
        return database.favoriteProductDao()
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }
}
