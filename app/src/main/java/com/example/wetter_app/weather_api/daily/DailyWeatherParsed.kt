package com.example.wetter_app.weather_api.daily

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DailyWeatherParsed(
    val latitude: Double,
    val longitude: Double,
    val elevation: Double,
    val daily: Daily
)

@Serializable
data class Daily(
    val time: List<String>,
    @SerialName("temperature_2m_max")
    val maxTemperature: List<Double>,
    @SerialName("temperature_2m_min")
    val minTemperature: List<Double>,
    @SerialName("precipitation_probability_max")
    val maxPrecipitationProbability: List<Double>,
    @SerialName("weather_code")
    val weatherCode: List<Int>
)

