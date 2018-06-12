package com.tacocat.catwithumbrella

import android.content.Context
import android.app.job.JobScheduler
import android.app.job.JobInfo
import android.content.ComponentName
import com.tacocat.catwithumbrella.service.CheckWeatherJobService


/**
 * Created by fallenstar on 3/22/18.
 */
object Util {

    val CHECK_WEATHER_JOB_ID = 1
    val PERIOD_MS = 6 * 60 * 60 * 1000L  // 6 HRS

    // schedule the start of the service every 6 hours
    fun sheduleJob(context: Context) {
        val serviceComponent = ComponentName(context, CheckWeatherJobService::class.java)
        val builder = JobInfo.Builder(CHECK_WEATHER_JOB_ID, serviceComponent)
        builder.setPeriodic(PERIOD_MS)
        builder.setPersisted(true)
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED) // require unmetered network

        val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        jobScheduler.schedule(builder.build())
    }
}