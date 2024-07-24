package com.example.wetter_app.Logic

data class HourlyWeatherData(
    val hour: String,
    val temperature: Int,
    val windSpeed: Int,
    val rainPercentage: Int
)