package com.example.wetter_app.storage_api

import kotlinx.serialization.Serializable

//Class holds the settings for storage and retrieval

@Serializable
data class SettingsModel(
    var locationList: MutableMap<String, Location>? = null,
    var settingsInitialized: Boolean = false,
    var reference : String? = null,
    var remoteEnabled : Boolean = false,
    var modifiedTime : Long = System.currentTimeMillis()
)

