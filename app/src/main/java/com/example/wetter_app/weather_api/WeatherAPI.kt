package com.example.wetter_app.weather_api

import com.example.wetter_app.weather_api.daily.DailyWeatherDay
import com.example.wetter_app.weather_api.daily.DailyWeatherParsed
import com.example.wetter_app.weather_api.hourly.HourlyWeatherHour
import com.example.wetter_app.weather_api.hourly.HourlyWeatherParsed
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class WeatherAPI {
    //This class makes the API Requests for Current, Hourly and Daily forecasts with ktor

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                ignoreUnknownKeys = true
            })
        }
    }

    suspend fun currentWeather(lat: Double, long: Double): CurrentWeather {
        //Get current weather for location
        val result: CurrentWeather =
            client.get("https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$long&current=temperature_2m,relative_humidity_2m,apparent_temperature,precipitation,weather_code,wind_speed_10m&timezone=auto")
                .body()
        return result
    }

    suspend fun hourlyWeather(lat: Double, long: Double): ArrayList<HourlyWeatherHour> {
        //Get weather for next 24 hours hourly
        val parsedResult: HourlyWeatherParsed =
            client.get("https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$long&hourly=temperature_2m,relative_humidity_2m,apparent_temperature,precipitation_probability,precipitation,weather_code,wind_speed_10m&timezone=auto&forecast_days=1&forecast_hours=24")
                .body()
        val result = ArrayList<HourlyWeatherHour>()

        //Create Entry for every Hour
        for (i in parsedResult.hourly.time.indices) {
            result.add(
                HourlyWeatherHour(
                    parsedResult.latitude,
                    parsedResult.longitude,
                    parsedResult.elevation,
                    parsedResult.hourly.time[i],
                    parsedResult.hourly.temperature[i],
                    parsedResult.hourly.apparentTemperature[i],
                    parsedResult.hourly.humidity[i],
                    parsedResult.hourly.precipitationProbability[i],
                    parsedResult.hourly.precipitation[i],
                    parsedResult.hourly.windSpeed[i],
                    parsedResult.hourly.weatherCode[i]
                )
            )
        }
        return result
    }

    suspend fun dailyWeather(lat: Double, long: Double): ArrayList<DailyWeatherDay> {
        //Get weather for past 2 Days and next 7 Days
        val parsedResult: DailyWeatherParsed =
            client.get("https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$long&daily=weather_code,temperature_2m_max,temperature_2m_min,precipitation_probability_max&timezone=auto&past_days=2")
                .body()
        val result = ArrayList<DailyWeatherDay>()

        //Create Entry for every Day -> current Day is at index 2
        for (i in parsedResult.daily.time.indices) {
            result.add(
                DailyWeatherDay(
                    parsedResult.latitude,
                    parsedResult.longitude,
                    parsedResult.elevation,
                    parsedResult.daily.time[i],
                    parsedResult.daily.maxTemperature[i],
                    parsedResult.daily.minTemperature[i],
                    parsedResult.daily.maxPrecipitationProbability[i],
                    parsedResult.daily.weatherCode[i]
                )
            )
        }
        return result
    }

    //Stop API -> should be called on closing the app
    fun stop() {
        client.close()
    }

}