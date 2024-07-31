package com.example.wetter_app.Logic

import com.example.wetter_app.weather_api.CurrentWeather
import com.example.wetter_app.weather_api.WeatherAPI
import com.example.wetter_app.weather_api.daily.DailyWeatherDay
import com.example.wetter_app.weather_api.hourly.HourlyWeatherHour
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WeatherDataHandler(private val api: WeatherAPI) {

    private var currentWeather: CurrentWeather? = null
    private var hourlyWeather: List<HourlyWeatherHour>? = null
    private var dailyWeather: List<DailyWeatherDay>? = null

    // Initialize by fetching both current and hourly weather data
    suspend fun initialize(lat: Double, lon: Double): Triple<CurrentWeather?, List<HourlyWeatherHour>?, List<DailyWeatherDay>?> {
        return withContext(Dispatchers.IO) {
            try {
                val currentWeather = api.currentWeather(lat, lon)
                val hourlyWeather = api.hourlyWeather(lat, lon)
                val dailyWeather = api.dailyWeather(lat, lon)
                this@WeatherDataHandler.currentWeather = currentWeather
                this@WeatherDataHandler.hourlyWeather = hourlyWeather
                this@WeatherDataHandler.dailyWeather = dailyWeather
                Triple(currentWeather, hourlyWeather, dailyWeather)
            } catch (e: Exception) {
                // TODO: handle error
                Triple(null, null, null)
            }
        }
    }

    // Fetch and update current weather data
    suspend fun syncWeather(lat: Double, lon: Double): CurrentWeather? {
        return withContext(Dispatchers.IO) {
            try {
                val newCurrentWeather = api.currentWeather(lat, lon)
                this@WeatherDataHandler.currentWeather = newCurrentWeather
                newCurrentWeather
            } catch (e: Exception) {
                //todo: handle error
                null
            }
        }
    }

    // Fetch and update hourly weather data
    suspend fun syncHourlyWeather(lat: Double, lon: Double): List<HourlyWeatherHour>? {
        return withContext(Dispatchers.IO) {
            try {
                val newHourlyWeather = api.hourlyWeather(lat, lon)
                this@WeatherDataHandler.hourlyWeather = newHourlyWeather
                newHourlyWeather
            } catch (e: Exception) {
                //todo: handle error
                null
            }
        }
    }

    suspend fun syncDailyWeather(lat: Double, lon: Double): List<DailyWeatherDay>? {
        return withContext(Dispatchers.IO) {
            try {
                val newDailyWeater = api.dailyWeather(lat, lon)
                this@WeatherDataHandler.dailyWeather = newDailyWeater
                newDailyWeater
            } catch (e: Exception) {
                //todo: handle error
                null
            }
        }
    }
}