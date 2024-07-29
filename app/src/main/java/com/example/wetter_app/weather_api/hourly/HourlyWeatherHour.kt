package com.example.wetter_app.weather_api.hourly

//Class for one hour of hourly weather

data class HourlyWeatherHour(
    val latitude: Double,
    val longitude: Double,
    val elevation: Double,
    val time: String,
    val temperature: Double,
    val apparentTemperature: Double,
    val humidity: Double,
    val precipitationProbability: Double,
    val precipitation: Double,
    val windSpeed: Double,
    val weatherCode: Int

)

