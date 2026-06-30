package com.example.carto.network

import com.example.carto.home.data.HomeApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.jvm.java

object RetrofitProvider {

    fun create(hostName: String , accessToken: String ): HomeApiService {

        val client = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor { chain ->
                val request = chain.request()
                    .newBuilder()
                    .addHeader("X-Shopify-Access-Token", accessToken)
                    .addHeader("Content-Type", "application/json")
                    .build()
                chain.proceed(request)
            }
            .build()

        return Retrofit.Builder()
            .baseUrl("https://$hostName/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(HomeApiService::class.java)
    }
}