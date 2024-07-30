package com.example.wetter_app.storage_api

import android.content.Context
import android.util.Log

//The SettingsManager Class is used for managing App settings and their synchronization to local storage as well as the remote DB

class SettingsManager(context: Context) {

    private var storage = LocalStorage(context)
    private lateinit var db: RemoteDatabase


    //Settings
    private var settingsInitialized = false
    private var locationList: MutableMap<String, Location>? = mutableMapOf()
    private var reference: String? = null
    private var remoteEnabled = false
    // + Whatever other settings are here
    //Add other settings variables here and in the SettingsModel, they can be public


    //Initialize settings on app start -> needs to be in onCreate of main
    fun initialize() {
        val settings = storage.get()
        if (settings != null) {
            Log.i("Settings", "Load settings")
            settingsInitialized = true
            locationList = settings.locationList
            reference = settings.reference
            remoteEnabled = settings.remoteEnabled
            db = RemoteDatabase(reference)
            if (settings.remoteEnabled) {
                synchronize()
            }

        } else {
            Log.i("Settings", "New settings")
            //Essentially first app start -> everything is null
            storage.insert(SettingsModel(locationList, true, reference, false))
            db = RemoteDatabase(reference)
        }

    }

    //Get Location by id
    fun getLocation(id: Int): Location? {
        return locationList?.get(id.toString())
    }

    //Get all Locations
    fun getAllLocations(): MutableMap<String, Location>? {
        return locationList
    }

    //Add Location to list -> id gets incremented regardless
    @OptIn(ExperimentalStdlibApi::class)
    fun addLocation(name: String, lat: Double, long: Double) {
        if (locationList == null) {
            locationList = mutableMapOf()
        }
        //ID was converted to Hex String because the DB would make an ArrayList out of the Map
        val maxId = locationList?.entries?.maxByOrNull { it.key.hexToInt() }?.key?.hexToInt()
        var newId = 0
        if (maxId != null) {
            newId = maxId + 1
        }

        //If name already exists -> replace
        val nameExists = locationList?.entries?.find { it.value.name == name }?.key?.hexToInt()
        if (nameExists != null) {
            newId = nameExists
        }

        val newLocation = Location(newId, name, lat, long)

        locationList?.put(newId.toHexString(), newLocation)

        storage.update(getSettingsModel())
        if(remoteEnabled){
         db.updateSettings(getSettingsModel())   
        } 
    }

    //Remove Location
    fun removeLocation(id: Int) {
        locationList?.remove(id.toString())
        storage.update(getSettingsModel())
        if (remoteEnabled) {
            db.updateSettings(getSettingsModel())
        }
    }

    //Helper function to avoid repeated code for getting new SettingsModel
    private fun getSettingsModel(): SettingsModel {
        return SettingsModel(locationList, settingsInitialized, reference, remoteEnabled)
    }

    //Enable Remote DB functionality -> TODO: expand with reference for possible settings import
    fun enableRemoteDb() {
        if (remoteEnabled) {
            return
        }
        remoteEnabled = true
        reference = db.createNew(SettingsModel(locationList, settingsInitialized, reference, true))
        Log.i("New DB Entry", reference.toString())
        storage.update(getSettingsModel())
    }

    //Sync settings from DB to device
    fun synchronize() {
        val settings = db.getSettings()
        if (settings != null) {
            settingsInitialized = settings.settingsInitialized
            locationList = settings.locationList
            reference = settings.reference
            remoteEnabled = settings.remoteEnabled
        } else {
            Log.w("Sync", "Synchronization from DB Failed!")
        }

    }

}
