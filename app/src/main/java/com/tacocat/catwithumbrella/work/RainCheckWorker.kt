package com.tacocat.catwithumbrella.work

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.google.android.gms.location.LocationServices
import com.tacocat.catwithumbrella.Day
import com.tacocat.catwithumbrella.R
import com.tacocat.catwithumbrella.WeatherRepository
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.util.concurrent.TimeUnit

class RainCheckWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    @SuppressLint("MissingPermission")
    override suspend fun doWork(): Result {
        if (!hasLocationPermission(applicationContext)) return Result.success()
        return try {
            val location = LocationServices.getFusedLocationProviderClient(applicationContext)
                .lastLocation.await() ?: return Result.success()
            val forecast = WeatherRepository.forecast(location.latitude, location.longitude)
            if (WeatherRepository.willRain(forecast, Day.Tomorrow) == true) {
                Timber.d("Tomorrow will rain")
                showWillRainNotification()
            }
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "Rain check failed")
            Result.retry()
        }
    }

    private fun showWillRainNotification() {
        val context = applicationContext
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, context.getString(R.string.rain_channel), NotificationManager.IMPORTANCE_DEFAULT)
            context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.rainy_weather)
            .setContentTitle(context.getString(R.string.rain_title))
            .setContentText(context.getString(R.string.rain_text))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
    }

    companion object {
        private const val CHANNEL_ID = "Rain"
        private const val NOTIFICATION_ID = 1
        private const val WORK_NAME = "check_weather"

        fun hasLocationPermission(context: Context) =
            listOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION).any {
                ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
            }

        // WorkManager persists across reboots, replacing the old JobScheduler + boot receiver.
        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<RainCheckWorker>(6, TimeUnit.HOURS)
                .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.UNMETERED).build())
                .build()
            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, request)
        }
    }
}
