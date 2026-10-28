package com.tacocat.catwithumbrella.networking

import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {

    @GET("data/2.5/forecast")
    suspend fun getWeather(
        @Query("units") units: String,
        @Query("lat") lat: Double,
        @Query("lon") lng: Double,
        @Query("APPID") apiKey: String,
    ): WeatherResponse
}
