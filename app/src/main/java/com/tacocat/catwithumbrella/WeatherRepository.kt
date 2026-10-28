package com.tacocat.catwithumbrella

import com.tacocat.catwithumbrella.networking.ApiClient
import com.tacocat.catwithumbrella.networking.WeatherInfo
import java.util.Calendar

enum class Day { Today, Tomorrow, Weekend }

object WeatherRepository {

    suspend fun forecast(lat: Double, lng: Double): List<WeatherInfo> =
        ApiClient.weatherApi.getWeather("metric", lat, lng, Constants.API_KEY).list.orEmpty()

    private fun Long.toCalendar() = Calendar.getInstance().also { it.timeInMillis = this * 1000 }

    private fun sameDay(a: Calendar, b: Calendar) =
        a.get(Calendar.YEAR) == b.get(Calendar.YEAR) && a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR)

    /** Returns true/false, or null when the forecast has no data for the requested day. */
    fun willRain(forecast: List<WeatherInfo>, day: Day): Boolean? {
        val target = Calendar.getInstance()
        val entries = when (day) {
            Day.Today -> forecast.filter { sameDay(it.dt.toCalendar(), target) }
            Day.Tomorrow -> {
                target.add(Calendar.DATE, 1)
                forecast.filter { sameDay(it.dt.toCalendar(), target) }
            }
            Day.Weekend -> forecast.filter {
                it.dt.toCalendar().get(Calendar.DAY_OF_WEEK) in listOf(Calendar.SATURDAY, Calendar.SUNDAY)
            }
        }
        // Today keeps the original behaviour: no entries means "NO".
        if (entries.isEmpty()) return if (day == Day.Today) false else null
        return entries.any { it.isRain }
    }
}
