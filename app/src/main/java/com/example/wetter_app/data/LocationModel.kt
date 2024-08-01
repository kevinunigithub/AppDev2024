package com.example.wetter_app.data

import com.example.wetter_app.storage_api.SettingsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object LocationModel {
    private lateinit var settingsManager: SettingsManager

    private val _locationName = MutableStateFlow("My Location")
    val locationName: StateFlow<String> get() = _locationName

    private val _latitude = MutableStateFlow(0.0)
    val latitude: StateFlow<Double> get() = _latitude

    private val _longitude = MutableStateFlow(0.0)
    val longitude: StateFlow<Double> get() = _longitude

    @OptIn(ExperimentalStdlibApi::class)
    fun initialize(settingsManager: SettingsManager) {
        this.settingsManager = settingsManager
        val maxId = settingsManager.getAllLocations()?.entries?.maxByOrNull { it.key.hexToInt() }?.key?.hexToInt()
        if (maxId != null) {
            settingsManager.getLocation(maxId)?.let { location ->
                updateLocation(location.name, location.latitude, location.longitude)
            }
        }
    }

    fun updateLocation(name: String, lat: Double, lon: Double) {
        val truncatedName = name.substringBefore(",")
        _locationName.value = truncatedName
        _latitude.value = lat
        _longitude.value = lon
        settingsManager.addLocation(truncatedName, lat, lon)
    }
}