package com.tacocat.catwithumbrella.networking

import android.util.Log
import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose
import java.util.*


/**
 * Created by fallenstar on 12/2/17.
 */
class WeatherResponse {

    @SerializedName("cod")
    @Expose
    var cod: String? = null
    @SerializedName("message")
    @Expose
    var message: Double? = null
    @SerializedName("cnt")
    @Expose
    var cnt: Int? = null
    @SerializedName("list")
    @Expose
    var list: List<WeatherInfo>? = null


    class WeatherInfo {
        @SerializedName("dt")
        @Expose
        var dt: Long = 0
        @SerializedName("main")
        @Expose
        var main: Temperature? = null
        @SerializedName("weather")
        @Expose
        var weather: List<Weather>? = null
        @SerializedName("clouds")
        @Expose
        var clouds: Clouds? = null
        @SerializedName("wind")
        @Expose
        var wind: Wind? = null
        @SerializedName("sys")
        @Expose
        var sys: Sys? = null
        @SerializedName("dt_txt")
        @Expose
        var dtTxt: String? = null

        override fun toString(): String {
            return "WeatherInfo(dtTxt=$dtTxt, weather=$weather)"
        }
    }

    inner class Weather {

        @SerializedName("id")
        @Expose
        var id: Int? = null
        @SerializedName("main")
        @Expose
        var main: String? = null
        @SerializedName("description")
        @Expose
        var description: String? = null
        @SerializedName("icon")
        @Expose
        var icon: String? = null

        override fun toString(): String {
            return "Weather(id=$id, main=$main, description=$description, icon=$icon)"
        }

    }

    inner class Temperature {
        @SerializedName("temp")
        @Expose
        var temp: Double? = null
        @SerializedName("temp_min")
        @Expose
        var tempMin: Double? = null
        @SerializedName("temp_max")
        @Expose
        var tempMax: Double? = null
        @SerializedName("pressure")
        @Expose
        var pressure: Double? = null
        @SerializedName("sea_level")
        @Expose
        var seaLevel: Double? = null
        @SerializedName("grnd_level")
        @Expose
        var grndLevel: Double? = null
        @SerializedName("humidity")
        @Expose
        var humidity: Int? = null
        @SerializedName("temp_kf")
        @Expose
        var tempKf: Double? = null
    }


    inner class Clouds {

        @SerializedName("all")
        @Expose
        var all: Int? = null

    }

    inner class Wind {

        @SerializedName("speed")
        @Expose
        var speed: Double? = null
        @SerializedName("deg")
        @Expose
        var deg: Double? = null

    }

    inner class Sys {

        @SerializedName("pod")
        @Expose
        var pod: String? = null

    }
}