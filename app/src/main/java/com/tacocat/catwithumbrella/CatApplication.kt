package com.tacocat.catwithumbrella

import android.app.Application
import com.tacocat.catwithumbrella.work.RainCheckWorker
import timber.log.Timber

class CatApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
        RainCheckWorker.schedule(this)
    }
}
