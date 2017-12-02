package com.tacocat.catwithumbrella

import com.google.gson.annotations.SerializedName

/**
 * Created by fallenstar on 12/2/17.
 */
class WeatherResponse {

    @SerializedName("list")
    var weatherInfoList: List<WeatherInfo>? = null

            class WeatherInfo {
                @SerializedName("dt")
                var dt: Long = 0
                @SerializedName("weather")
                var weatherList: List<Weather>? = null
            }

            class Weather{
                @SerializedName("id")
                var id: Int = 0
                @SerializedName("main")
                var main: String = ""
                @SerializedName("description")
                var description: String = ""
            }
}