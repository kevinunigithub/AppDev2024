package com.example.wetter_app.weather_api

class WeatherCodesLookup {
    val codes = mapOf(
        0 to "Clear sky",
        1 to "Mainly Clear",
        2 to "Partly Cloudy",
        3 to "Overcast",
        45 to "Fog",
        48 to "Rime Fog",
        51 to "Light Drizzle",
        52 to "Moderate Drizzle",
        53 to "Dense Drizzle",
        61 to "Light Rain",
        63 to "Moderate Rain",
        65 to "Heavy Rain",
        66 to "Light Freezing Rain",
        67 to "Heavy Freezing Rain",
        71 to "Light Snow Fall",
        73 to "Moderate Snow Fall",
        75 to "Heavy Snow Fall",
        77 to "Snow Grains",
        80 to "Light Rain Shower",
        81 to "Moderate Rain Shower",
        82 to "Violent Rain Shower",
        85 to "Slight Snow Shower",
        86 to "Heavy Snow Shower",
        95 to "Thunderstorm",
        96 to "Thunderstorm Slight Hail",
        99 to "Thunderstorm Heavy Hail"
    )

    fun getWeatherName(id: Int): String {
        return if (codes[id] == null) {
            "Unknown"
        } else {
            codes[id].orEmpty()
        }
    }
}