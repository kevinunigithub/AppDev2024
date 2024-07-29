package com.example.wetter_app.weather_api.hourly

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

//Class for parsed hourly weather, used to create actual results

@Serializable
data class HourlyWeatherParsed(
    val latitude: Double,
    val longitude: Double,
    val elevation: Double,
    val hourly: Hourly
)

@Serializable
data class Hourly(
    val time: List<String>,
    @SerialName("temperature_2m")
    val temperature: List<Double>,
    @SerialName("relative_humidity_2m")
    val humidity: List<Double>,
    @SerialName("apparent_temperature")
    val apparentTemperature: List<Double>,
    @SerialName("precipitation_probability")
    val precipitationProbability: List<Double>,
    val precipitation: List<Double>,
    @SerialName("wind_speed_10m")
    val windSpeed: List<Double>,
    @SerialName("weather_code")
    val weatherCode: List<Int>
)
