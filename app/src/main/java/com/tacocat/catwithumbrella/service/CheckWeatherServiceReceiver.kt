package com.tacocat.catwithumbrella.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.tacocat.catwithumbrella.Util

/**
 * Created by fallenstar on 3/22/18.
 */
class CheckWeatherServiceReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, p1: Intent?) {
        if(context != null) {
            Util.sheduleJob(context)
        }
    }
}