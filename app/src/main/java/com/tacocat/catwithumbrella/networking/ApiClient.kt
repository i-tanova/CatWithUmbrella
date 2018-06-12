package com.tacocat.catwithumbrella.networking

import com.tacocat.catwithumbrella.Constants
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by fallenstar on 12/2/17.
 */
class ApiClient {

    companion object {

        lateinit var retrofit: Retrofit

        fun getClient() : Retrofit{
           var interceptor: HttpLoggingInterceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

            var client: OkHttpClient = OkHttpClient.Builder().addInterceptor(interceptor).build()

            retrofit = Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build()

            return retrofit
        }
    }

}