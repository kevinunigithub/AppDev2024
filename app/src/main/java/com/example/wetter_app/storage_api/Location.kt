package com.example.wetter_app.storage_api

import kotlinx.serialization.Serializable

//Location Data Class

@Serializable
data class Location(
    val id: Int = 0,
    val name: String = "Default",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)