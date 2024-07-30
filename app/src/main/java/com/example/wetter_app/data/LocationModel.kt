package com.example.wetter_app.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


object LocationModel {
    private val _locationName = MutableStateFlow("My Location")
    val locationName: StateFlow<String> get() = _locationName

    private val _latitude = MutableStateFlow(0.0)
    val latitude: StateFlow<Double> get() = _latitude

    private val _longitude = MutableStateFlow(0.0)
    val longitude: StateFlow<Double> get() = _longitude

    fun updateLocation(name: String, lat: Double, lon: Double) {
        _locationName.value = name
        _latitude.value = lat
        _longitude.value = lon
    }
}