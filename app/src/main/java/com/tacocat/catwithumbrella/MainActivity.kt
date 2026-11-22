package com.tacocat.catwithumbrella

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tacocat.catwithumbrella.ui.CatTheme
import com.tacocat.catwithumbrella.ui.WeatherScreen
import com.tacocat.catwithumbrella.work.RainCheckWorker

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CatTheme {
                val viewModel: WeatherViewModel = viewModel()
                val state by viewModel.state.collectAsStateWithLifecycle()

                val permissionLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestMultiplePermissions()
                ) { viewModel.load() }

                LaunchedEffect(Unit) {
                    val permissions = buildList {
                        add(Manifest.permission.ACCESS_FINE_LOCATION)
                        add(Manifest.permission.ACCESS_COARSE_LOCATION)
                        if (Build.VERSION.SDK_INT >= 33) add(Manifest.permission.POST_NOTIFICATIONS)
                    }
                    if (RainCheckWorker.hasLocationPermission(this@MainActivity)) {
                        viewModel.load()
                    } else {
                        permissionLauncher.launch(permissions.toTypedArray())
                    }
                }

                WeatherScreen(state)
            }
        }
    }
}
