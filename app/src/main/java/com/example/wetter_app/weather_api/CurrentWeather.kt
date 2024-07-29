package com.example.wetter_app.weather_api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

//Class for current weather

@Serializable
data class CurrentWeather(
    val latitude: Double,
    val longitude: Double,
    val elevation: Double,
    val current: Weather
)

@Serializable
data class Weather(
    val time: String,
    @SerialName("temperature_2m")
    val temperature: Double,
    @SerialName("relative_humidity_2m")
    val humidity: Double,
    @SerialName("apparent_temperature")
    val apparentTemperature: Double,
    val precipitation: Double,
    @SerialName("wind_speed_10m")
    val windSpeed: Double,
    @SerialName("weather_code")
    val weatherCode: Int
)
