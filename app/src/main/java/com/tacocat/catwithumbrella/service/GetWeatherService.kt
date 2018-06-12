package com.tacocat.catwithumbrella.service

import android.Manifest
import android.app.IntentService
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.support.v4.app.ActivityCompat
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import android.support.v4.app.NotificationManagerCompat
import com.tacocat.catwithumbrella.networking.ApiClient
import com.tacocat.catwithumbrella.networking.ApiInterface
import com.tacocat.catwithumbrella.R
import com.tacocat.catwithumbrella.networking.WeatherResponse
import timber.log.Timber


/**
 * Created by fallenstar on 3/22/18.
 */
class GetWeatherService : IntentService("GetWeather") {

    // will be called asynchronously by Android
    override fun onHandleIntent(p0: Intent?) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        fusedLocationClient.lastLocation
                .addOnSuccessListener { location : Location? ->
                    if(location != null){
                        getWeather(location.latitude, location.longitude)
                }
    }

}


    fun getWeather(lat: Double, lng: Double) {
        Log.d("Tag", "getWeather called")
        val call = ApiClient.getClient().create(ApiInterface::class.java)!!.getWeather("metric", lat, lng, "a9f07b3e58aa83b04d6299075004c1ce")
        call.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                onWeatherRetrieved(response.body())
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                call.cancel()
            }
        })
    }

    private fun onWeatherRetrieved(weatherResponse: WeatherResponse) {
        Timber.log(1, "\n\nresponse: ${weatherResponse!!.list!!.toString()}" )
        showWillRainNotification()

        var tomorrow= Calendar.getInstance()
        tomorrow.add(Calendar.DATE, 1)

        val calWeather = Calendar.getInstance()

        weatherResponse!!.list!!.forEach {
            calWeather.timeInMillis = it.dt * 1000
            if (tomorrow.get(Calendar.DATE) == calWeather.get(Calendar.DATE)) {
                if (it.weather!![0].main.equals("Rain", true)) {
                   // showWillRainNotification()
                     Timber.log(1, "\n ****** Tomorrow will rain  ****** \n ")
                    return
                }
            }
        }
        }

    private fun showWillRainNotification() {
        val mBuilder = NotificationCompat.Builder(this, "Rain")
                .setSmallIcon(R.drawable.rainy_weather)
                //.setContentTitle("Rain")
                //.setContentText("Bring umbrella tomorrow! ${Date()}")
                .setContentTitle("Check weather")
                .setContentText("${Date()}")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(1, mBuilder.build());
    }
}
