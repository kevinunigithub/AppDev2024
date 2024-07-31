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
    var reference: String? = null //Can be accessed form outside to display User reference for import in other devices
        private set
    var remoteEnabled = false //This can be accessed from outside to read its status
        private set
    // + Whatever other settings are here
    //Add other settings variables here, in the SettingsModel Class and getSettingsModel() Function, they can be public


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
            storage.insert(
                SettingsModel(
                    locationList,
                    true,
                    reference,
                    false
                )
            )
            settingsInitialized = true
            //Remote DB is initialized for later use
            db = RemoteDatabase(reference)
        }

    }

    //Get Location by id
    @OptIn(ExperimentalStdlibApi::class)
    fun getLocation(id: Int): Location? {
        return locationList?.get(id.toHexString())
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
        if (remoteEnabled) {
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
        return SettingsModel(
            locationList,
            settingsInitialized,
            reference,
            remoteEnabled
        )
    }

    //Enable Remote DB functionality -> expanded with reference for possible settings import on enabling
    fun enableRemoteDb(import: Boolean, ref: String?): Int {
        //Possible return status:
        // 0 = Remote already enabled
        // 1 = Remote enabling success
        // 2 = Import Failed
        // 3 = Empty reference
        if (remoteEnabled) {
            return 0
        }
        remoteEnabled = true

        if (import) {
            if (ref.isNullOrEmpty()) { //If empty reference -> return
                return 3
            } else {
                val importResponse = importRemote(ref)
                return if (importResponse == 1) {
                    1
                } else {
                    2 //Import only fails if invalid reference or no internet connection
                }
            }
        } else {
            reference = db.createNew(getSettingsModel())
            storage.update(getSettingsModel())
            Log.i("REMOTE", "Remote sync enabled!")
            return 1
        }

    }

    //Sync settings from DB to device -> can also be called from outside initialize() for manual trigger
    fun synchronize() {
        val settingsRemote = db.getSettings()
        val settingsLocal = storage.get()
        if (settingsRemote != null) {
            //Synchronize depending on modifiedTime
            if (settingsLocal != null && (settingsLocal.modifiedTime > settingsRemote.modifiedTime)) {
                db.updateSettings(settingsLocal)
            } else {
                storage.update(settingsRemote)
            }
        } else {
            //If no entry in DB -> upload
            db.updateSettings(getSettingsModel())
        }
        Log.i("SYNC", "Synchronization successfull")
    }

    //Import settings from DB to device via reference -> can be triggered from outside
    fun importRemote(ref: String): Int {
        //Return codes are as follows:
        // 0 = Error -> wrong reference or no internet connection
        // 1 = Success

        if (!remoteEnabled || ref.isEmpty()) { //Remote must be enabled and reference not empty
            return 0
        }

        val importedSettings = db.importSettings(ref)
        if (importedSettings != null) {
            settingsInitialized = importedSettings.settingsInitialized
            locationList = importedSettings.locationList
            reference = importedSettings.reference
            remoteEnabled = importedSettings.remoteEnabled
            storage.update(importedSettings)
            return 1
        } else {
            Log.w("IMPORT", "Import from DB Failed!")
            return 0
        }
    }

}
