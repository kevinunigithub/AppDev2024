package com.example.wetter_app.weather_api.daily

//Class for one day of weather

data class DailyWeatherDay(
    val latitude: Double,
    val longitude: Double,
    val elevation: Double,
    val time: String,
    val maxTemperature: Double,
    val minTemperature: Double,
    val maxPrecipitationProbability: Double,
    val weatherCode: Int
)
