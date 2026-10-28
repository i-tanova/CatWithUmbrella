package com.tacocat.catwithumbrella

import android.annotation.SuppressLint
import android.app.Application
import android.location.Geocoder
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.tacocat.catwithumbrella.networking.WeatherInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.Locale

data class WeatherUiState(
    val locationName: String? = null,
    val forecast: List<WeatherInfo>? = null,
)

class WeatherViewModel(app: Application) : AndroidViewModel(app) {

    private val _state = MutableStateFlow(WeatherUiState())
    val state: StateFlow<WeatherUiState> = _state.asStateFlow()

    private var loading = false

    @SuppressLint("MissingPermission")
    fun load() {
        if (loading || _state.value.forecast != null) return
        loading = true
        viewModelScope.launch {
            try {
                val client = LocationServices.getFusedLocationProviderClient(getApplication<Application>())
                val request = CurrentLocationRequest.Builder().setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY).build()
                val location = client.getCurrentLocation(request, null).await() ?: return@launch
                _state.value = _state.value.copy(locationName = locationName(location))
                val forecast = WeatherRepository.forecast(location.latitude, location.longitude)
                _state.value = _state.value.copy(forecast = forecast)
            } catch (e: Exception) {
                Timber.e(e, "Failed to load weather")
            } finally {
                loading = false
            }
        }
    }

    @Suppress("DEPRECATION")
    private suspend fun locationName(location: Location): String? = withContext(Dispatchers.IO) {
        try {
            val address = Geocoder(getApplication(), Locale.getDefault())
                .getFromLocation(location.latitude, location.longitude, 1)?.firstOrNull()
            address?.getAddressLine(0)?.split(",")?.takeLast(2)?.joinToString(",")?.trim()
        } catch (e: Exception) {
            Timber.e(e, "Geocoding failed")
            null
        }
    }
}
