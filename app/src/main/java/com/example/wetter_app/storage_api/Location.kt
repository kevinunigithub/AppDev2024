package com.example.wetter_app.storage_api

import kotlinx.serialization.Serializable

@Serializable
data class Location(
    val id: Int,
    val name: String,
    val latitude: Double,
    val longitude: Double
)