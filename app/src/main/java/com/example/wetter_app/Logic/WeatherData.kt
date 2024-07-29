package com.example.wetter_app.Logic

data class WeatherData(
    val date: String,
    val location: String,
    val hourlyData: List<HourlyWeatherData>
) {
    val averageTemperature: Int
        get() = hourlyData.map { it.temperature }.average().toInt()

    val averageWindSpeed: Int
        get() = hourlyData.map { it.windSpeed }.average().toInt()

    val averageRainPercentage: Int
        get() = hourlyData.map { it.rainPercentage }.average().toInt()
}