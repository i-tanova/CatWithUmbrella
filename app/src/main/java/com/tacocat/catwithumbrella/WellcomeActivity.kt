package com.tacocat.catwithumbrella

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.transition.TransitionManager
import android.util.Log
import android.view.View
import com.google.android.gms.location.*
import com.tacocat.catwithumbrella.R.id.*
import com.tacocat.catwithumbrella.networking.ApiClient
import com.tacocat.catwithumbrella.networking.ApiInterface
import com.tacocat.catwithumbrella.networking.WeatherResponse
import kotlinx.android.synthetic.main.activity_wellcome.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*

class WellcomeActivity : AppCompatActivity() {

    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var apiInterface: ApiInterface? = null
    private var mLocationCallback: LocationCallback? = null


    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wellcome)
        Util.sheduleJob(applicationContext)

        locationTxtV.visibility = View.GONE
        weatherTxtV.visibility = View.GONE
        dayTxtV.visibility = View.GONE

        createLocationCallback()

        apiInterface = ApiClient.getClient().create(ApiInterface::class.java)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION), 3)
            return
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Request for permission
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                   1);
        }

            setLocation()
    }

    private fun createLocationCallback() {
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                Log.d("Tag", "onLocationResult")
                val location = locationResult!!.lastLocation
                if (location != null) {
                    mFusedLocationClient!!.removeLocationUpdates(mLocationCallback!!)

                    setLocationText(location)
                }
            }
        }
    }

    private fun setLocationText(location: Location?) {
        val address = getLocationName(location)
        if (address != null) {

            var arr = address.getAddressLine(0).split(",")
            var textArr = arr.takeLast(2)
            locationTxtV.text = "${textArr[0]}, ${textArr[1]}"
            TransitionManager.beginDelayedTransition(weatherContainer)
            locationTxtV.visibility = View.VISIBLE

            getWeather(address.latitude, address.longitude)
        }
    }

    private fun setLocation() {
        Log.d("Tag", "setLocation called")
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        mFusedLocationClient!!.requestLocationUpdates(locationRequest, mLocationCallback!!, null)
    }

    private fun getLocationName(location: Location?): Address? {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(
                    location!!.latitude,
                    location.longitude,
                    1)
            Log.d("Address", addresses.toString() + "")
            return addresses[0]
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        setLocation()
    }

    fun getWeather(lat: Double, lng: Double) {
        Log.d("Tag", "getWeather called")
        val call = apiInterface!!.getWeather("metric", lat, lng, "a9f07b3e58aa83b04d6299075004c1ce")
        call.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                onWeatherRetrieved(response)
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                call.cancel()
            }
        })
    }

    private fun onWeatherRetrieved(response: Response<WeatherResponse>) {
        Log.d("Tag", "getWeather response: " + response.body())

        weatherContainer.setOnClickListener{
            onWeatherRetrieved(response)
        }

        weatherTxtV.visibility = View.GONE
        dayTxtV.visibility = View.GONE

        val weatherResponse = response.body()
        showToday(weatherResponse)


        TransitionManager.beginDelayedTransition(weatherContainer)
        weatherTxtV.visibility = View.VISIBLE
        dayTxtV.visibility = View.VISIBLE

        dayTxtV.postDelayed({
            showTomorrow(weatherResponse)
        }, 3000)

        dayTxtV.postDelayed({
            showWeekend(weatherResponse)
        }, 7000)
    }

    private fun showToday(weatherResponse: WeatherResponse?) {
        val now = Calendar.getInstance()
        dayTxtV.text = "Today"
        setWeatherText(weatherResponse, now)
    }

    private fun showWeekend(weatherResponse: WeatherResponse?) {
        dayTxtV.visibility = View.GONE
        weatherTxtV.visibility = View.GONE


        dayTxtV.text = "Weekend"

        val calWeather = Calendar.getInstance()
        weatherTxtV.text = "NOT SURE"
        var willRain = false
        var found = false
        weatherResponse!!.list!!.forEach {
            calWeather.timeInMillis = it.dt * 1000
            Log.d("t", calWeather.get(Calendar.DAY_OF_WEEK).toString() + " weather: " + it.weather!![0].main)
            if (calWeather.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || calWeather.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                if (it.weather!![0].main.equals("Rain", true)) {
                    willRain = true
                }
                found = true
            }
        }

        if(willRain){
            weatherTxtV.text = "YES"
        }else if(found){
            weatherTxtV.text = "NO"
        }

        TransitionManager.beginDelayedTransition(weatherContainer)
        dayTxtV.visibility = View.VISIBLE
        weatherTxtV.visibility = View.VISIBLE
    }

    private fun setWeatherText(weatherResponse: WeatherResponse?, dateToCompare: Calendar) {
        val calWeather = Calendar.getInstance()
        weatherTxtV.text = "NO"
        weatherResponse!!.list!!.forEach {
            calWeather.timeInMillis = it.dt * 1000
            if (dateToCompare.get(Calendar.DATE) == calWeather.get(Calendar.DATE)) {
                if (it.weather!![0].main.equals("Rain", true)) {
                    weatherTxtV.text = "YES"
                    return
                }
            }
        }
    }



    private fun showTomorrow(weatherResponse: WeatherResponse) {
        dayTxtV.visibility = View.GONE
        weatherTxtV.visibility = View.GONE


        dayTxtV.text = "Tomorrow"
        var tomorrow= Calendar.getInstance()
        tomorrow.add(Calendar.DATE, 1)
        setWeatherText(weatherResponse, tomorrow)

        TransitionManager.beginDelayedTransition(weatherContainer)
        dayTxtV.visibility = View.VISIBLE
        weatherTxtV.visibility = View.VISIBLE
    }


    companion object {
        private val locationRequest = createLocationRequest()

        protected fun createLocationRequest(): LocationRequest {
            val mLocationRequest = LocationRequest()
            mLocationRequest.interval = 10000
            mLocationRequest.fastestInterval = 5000
            mLocationRequest.priority = LocationRequest.PRIORITY_LOW_POWER
            return mLocationRequest
        }
    }
}
