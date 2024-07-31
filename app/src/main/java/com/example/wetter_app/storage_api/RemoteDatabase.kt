package com.example.wetter_app.storage_api

import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.getValue
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

class RemoteDatabase(reference: String?) {
    //Database for synchronization across devices
    //No actual user authentication was used -> user gets a unique key for reference

    private var firebaseRef = FirebaseDatabase.getInstance().getReference("settings/$reference")
    private lateinit var userReference : String

    //Json serializer settings
    @OptIn(ExperimentalSerializationApi::class)
    private val jsonConverter = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        isLenient = true
        explicitNulls = false
    }

    //Create New settings in DB
    fun createNew(settings: SettingsModel): String {
        //Create new Key
        val dbPath = FirebaseDatabase.getInstance().getReference("settings")
        val newReference = dbPath.push().key!!
        //Update reference
        firebaseRef = FirebaseDatabase.getInstance().getReference("settings/$newReference")
        //Set current Settings
        firebaseRef.setValue(settings)
        userReference = newReference
        return newReference
    }

    //Get Settings from Database
    fun getSettings(): SettingsModel? {
        var result: SettingsModel? = null
        firebaseRef.get().addOnSuccessListener {
            result = it.getValue<SettingsModel>()
        }.addOnFailureListener {
            Log.e("Get Settings", "Failed to get Settings from DB")
        }
        return result
    }

    //Import Settings from Database -> Also set DB settings accordingly
    fun importSettings(reference: String): SettingsModel? {
        var result: SettingsModel? = null
        val path = FirebaseDatabase.getInstance().getReference("settings/$reference")
        path.get().addOnSuccessListener {
            result = it.getValue<SettingsModel>()
            firebaseRef = path
            userReference = reference
        }.addOnFailureListener {
            Log.e("Get Settings", "Failed to get Settings from DB")
        }
        return result
    }

    //Update Settings -> no real need to update specific fields as there is very little data for now
    fun updateSettings(settings: SettingsModel) {
        firebaseRef.setValue(settings)
        userReference = settings.reference.toString()
    }

}