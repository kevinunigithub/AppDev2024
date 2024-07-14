package com.example.wetter_app.UserInterface


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wetter_app.Logic.ImageHandler
import com.example.wetter_app.Logic.WeatherData
import com.example.wetter_app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherMainScreen() {
    val weatherDataList = listOf(
        WeatherData("Monday, 13th July 2024", 25, 10, "Klagenfurt"),
        WeatherData("Tuesday, 14th July 2024", 28, 12, "Klagenfurt"),
        WeatherData("Wednesday, 15th July 2024", 22, 8, "Klagenfurt")
    )

    val imageHandler = ImageHandler()


    val (currentWeatherIndex, setCurrentWeatherIndex) = remember { mutableStateOf(0) }

    val currentWeatherData = weatherDataList[currentWeatherIndex]

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE57373))
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                "${currentWeatherData.location}",
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                currentWeatherData.date,
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Normal
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { /*Will be implemented later*/ }) {
                            Image(
                                painter = painterResource(id = R.drawable.menu),
                                contentDescription = "Menu",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.smallTopAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            containerColor = Color.Transparent
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { setCurrentWeatherIndex((currentWeatherIndex - 1 + weatherDataList.size) % weatherDataList.size) }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_left_arrow),
                            contentDescription = "Previous Weather",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            "Temperature: ${currentWeatherData.temperature}°C",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            "Windspeed: ${currentWeatherData.windSpeed} km/h",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Image(
                        painter = painterResource(id = imageHandler.getImageResId(currentWeatherData.temperature, currentWeatherData.windSpeed)),
                        contentDescription = "Weather Image",
                        modifier = Modifier.size(150.dp)
                    )
                    IconButton(
                        onClick = { setCurrentWeatherIndex((currentWeatherIndex + 1) % weatherDataList.size) }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_right_arrow),
                            contentDescription = "Next Weather",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.size(10.dp))

                RainRadar()
            }
        }
    }
}