package com.tacocat.catwithumbrella.networking

import com.tacocat.catwithumbrella.BuildConfig
import com.tacocat.catwithumbrella.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    val weatherApi: ApiInterface by lazy {
        val client = OkHttpClient.Builder().apply {
            if (BuildConfig.DEBUG) {
                // Query string contains the API key, so only log in debug builds.
                addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
            }
        }.build()

        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(ApiInterface::class.java)
    }
}
