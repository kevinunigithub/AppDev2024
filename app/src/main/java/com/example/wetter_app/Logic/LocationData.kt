package com.example.wetter_app.Logic

import android.content.Context
import android.location.Location
import androidx.core.content.ContextCompat
import com.example.wetter_app.data.LocationResponse
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.Response

class LocationData(private val context: Context) {
    private val httpClient = OkHttpClient()
    private val objectMapper = jacksonObjectMapper()
    private var fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    private var longitude: Double? = null
    private var latitude: Double? = null

    fun getCurrentLocationData(onLocationFetched: (latitude: Double, longitude: Double) -> Unit) {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    latitude = it.latitude
                    longitude = it.longitude
                    onLocationFetched(it.latitude, it.longitude)
                }
            }
        } else {
            // Handle case when no access on location data
        }
    }

    suspend fun getLocationDataOfChosenPlace(query: String, onLocationFetched: (latitude: Double, longitude: Double) -> Unit) {
        val url = "https://nominatim.openstreetmap.org/search?q=${query}&format=geojson"
        val request = Request.Builder()
            .get()
            .url(url)
            .build()

        try {
            withContext(Dispatchers.IO) {
                val response: Response = httpClient.newCall(request).execute()
                if (response.isSuccessful) {
                    response.body?.let { responseBody ->
                        val responseBodyString = responseBody.string()
                        val locations: LocationResponse = objectMapper.readValue(responseBodyString)
                        if (locations.features.isNotEmpty()) {
                            val location = locations.features[0]
                            latitude = location.geometry.coordinates[1]
                            longitude = location.geometry.coordinates[0]
                            onLocationFetched(latitude!!, longitude!!)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun getSearchResults(query: String): List<String> {
        val url = "https://nominatim.openstreetmap.org/search?q=${query}&format=geojson"
        val request = Request.Builder()
            .get()
            .url(url)
            .build()
        val results = mutableListOf<String>()

        try {
            withContext(Dispatchers.IO) {
                val response: Response = httpClient.newCall(request).execute()
                if (response.isSuccessful) {
                    response.body?.let { responseBody ->
                        val responseBodyString = responseBody.string()
                        val locations: LocationResponse = objectMapper.readValue(responseBodyString)
                        results.addAll(locations.features.map { it.properties.displayName })
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return results
    }
}
