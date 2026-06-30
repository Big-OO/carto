package com.example.carto.search.di

import android.content.Context
import androidx.room.Room
import com.example.carto.BuildConfig
import com.example.carto.search.data.local.SearchHistoryDao
import com.example.carto.search.data.local.SearchHistoryDatabase
import com.example.carto.search.data.remote.SearchShopifyApi
import com.example.carto.search.data.remote.SearchShopifyConfig
import com.example.carto.search.data.repository.SearchRepositoryImpl
import com.example.carto.search.domain.repository.SearchRepository
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

@Module
@InstallIn(SingletonComponent::class)
object SearchDIModule {
    @Provides
    @Singleton
    fun provideSearchHistoryDatabase(
        @ApplicationContext context: Context,
    ): SearchHistoryDatabase {
        return Room.databaseBuilder(
            context,
            SearchHistoryDatabase::class.java,
            SearchHistoryDatabase.DATABASE_NAME,
        ).build()
    }

    @Provides
    @Singleton
    fun provideSearchHistoryDao(database: SearchHistoryDatabase): SearchHistoryDao {
        return database.searchHistoryDao()
    }

    @Provides
    @Singleton
    fun provideSearchRepository(repository: SearchRepositoryImpl): SearchRepository {
        return repository
    }

    @Provides
    @Singleton
    fun provideSearchShopifyConfig(): SearchShopifyConfig {
        return SearchShopifyConfig(
            hostname = BuildConfig.SHOPIFY_HOSTNAME,
            apiVersion = BuildConfig.SHOPIFY_API_VERSION,
            adminAccessToken = BuildConfig.SHOPIFY_ADMIN_ACCESS_TOKEN,
        )
    }

    @Provides
    @Singleton
    fun provideSearchShopifyApi(config: SearchShopifyConfig): SearchShopifyApi {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BASIC
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        val client = OkHttpClient.Builder()
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

        return Retrofit.Builder()
            .baseUrl("https://${config.hostname}/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SearchShopifyApi::class.java)
    }
}
