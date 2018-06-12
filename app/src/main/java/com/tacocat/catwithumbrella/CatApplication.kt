package com.tacocat.catwithumbrella

import android.app.Application
import com.tacocat.catwithumbrella.util.FileLoggingTree
import timber.log.Timber

/**
 * Created by fallenstar on 4/3/18.
 */
class CatApplication : Application() {


    override fun onCreate() {
        super.onCreate()
        Timber.plant(FileLoggingTree(getApplicationContext()));
    }
}