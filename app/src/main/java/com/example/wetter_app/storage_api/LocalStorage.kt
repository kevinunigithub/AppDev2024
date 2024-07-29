package com.example.wetter_app.storage_api

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


class LocalStorage(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "settings.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "settings"
        private const val COLUMN_ID = "id"
        private const val COLUMN_SETTINGS = "settings"
    }

    private var settingsId = 1

    //Json serializer settings
    @OptIn(ExperimentalSerializationApi::class)
    private val jsonConverter = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        isLenient = true
        explicitNulls = false
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable =
            "CREATE TABLE $TABLE_NAME($COLUMN_ID INTEGER PRIMARY KEY, $COLUMN_SETTINGS TEXT)"
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val dropTable = "DROP TABLE IF EXISTS $TABLE_NAME"
        db?.execSQL(dropTable)
        onCreate(db)
    }

    //Insert new settings
    fun insert(settings: SettingsModel) {
        val parsedSettings = jsonConverter.encodeToString(settings)
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_SETTINGS, parsedSettings)
        }
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    //Get Settings -> always first Row
    fun get(): SettingsModel? {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME LIMIT 1 "
        val cursor = db.rawQuery(query, null)

        if (cursor.count == 0){
            return null
        }

        cursor.moveToFirst()

        val result = jsonConverter.decodeFromString<SettingsModel>(
            cursor.getString(
                cursor.getColumnIndexOrThrow(
                    COLUMN_SETTINGS
                )
            )
        )
        //Set settingsID -> for later update
        settingsId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
        cursor.close()
        return result
    }

    //Update Settings
    fun update(settings: SettingsModel) {

        val parsedSettings = jsonConverter.encodeToString(settings)
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_SETTINGS, parsedSettings)
        }
        val whereClause = "$COLUMN_ID = ?"
        db.update(TABLE_NAME, values, whereClause, arrayOf(settingsId.toString()))
        db.close()

    }

}