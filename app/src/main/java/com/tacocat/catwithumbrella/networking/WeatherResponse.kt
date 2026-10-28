package com.tacocat.catwithumbrella.networking

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("list") val list: List<WeatherInfo>? = null,
)

data class WeatherInfo(
    @SerializedName("dt") val dt: Long = 0,
    @SerializedName("weather") val weather: List<Weather>? = null,
    @SerializedName("dt_txt") val dtTxt: String? = null,
) {
    val isRain: Boolean get() = weather?.firstOrNull()?.main.equals("Rain", ignoreCase = true)
}

data class Weather(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("main") val main: String? = null,
    @SerializedName("description") val description: String? = null,
)
