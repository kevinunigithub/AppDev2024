package com.example.wetter_app.user_interface

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wetter_app.Logic.UnitSystem
import com.example.wetter_app.weather_api.hourly.HourlyWeatherHour

@Composable
fun WeatherDetailsDialog(hourlyWeatherData: List<HourlyWeatherHour>, onDismiss: () -> Unit) {
    val isMetric by UnitSystem.isMetric.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Hourly Weather Details",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.Black
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                items(hourlyWeatherData) { data ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                val temperature = if (isMetric) {
                                    "${data.temperature}°C"
                                } else {
                                    "${(data.temperature * 9 / 5 + 32).toInt()}°F"
                                }

                                val windSpeed = if (isMetric) {
                                    "${data.windSpeed} km/h"
                                } else {
                                    "${(data.windSpeed / 1.609).toInt()} mph"
                                }

                                Text(
                                    text = data.time,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color.Black
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Temp: $temperature",
                                    color = Color.Black
                                )
                                Text(
                                    text = "Wind: $windSpeed",
                                    color = Color.Black
                                )
                                Text(
                                    text = "Rain: ${data.precipitationProbability}%",
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE57373))
            ) {
                Text("Close", color = Color.White)
            }
        },
        dismissButton = null
    )
}