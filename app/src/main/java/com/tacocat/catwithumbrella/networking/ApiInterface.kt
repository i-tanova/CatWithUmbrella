package com.tacocat.catwithumbrella.networking


import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by fallenstar on 12/2/17.
 */
interface ApiInterface{

    @GET("/data/2.5/forecast?")
    fun getWeather(
                   @Query(value = "units",encoded = true) units: String,
                   @Query(value = "lat",encoded = true) lat: Double,
                   @Query(value = "lon",encoded = true) lng: Double,
                   @Query(value = "APPID",encoded = true) appiKey: String): Call<WeatherResponse>
}