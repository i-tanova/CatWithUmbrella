package com.tacocat.catwithumbrella.service

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import com.tacocat.catwithumbrella.Util


/**
 * Created by fallenstar on 3/22/18.
 */
class CheckWeatherJobService : JobService(){

    override fun onStopJob(p0: JobParameters?): Boolean {
        return true
    }

    override fun onStartJob(p0: JobParameters?): Boolean {
        val service = Intent(applicationContext, GetWeatherService::class.java)
        applicationContext.startService(service)
        return true
    }

}