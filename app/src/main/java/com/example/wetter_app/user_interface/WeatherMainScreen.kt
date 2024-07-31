package com.example.wetter_app.user_interface

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.wetter_app.Logic.ImageHandler
import com.example.wetter_app.Logic.UnitSystem
import com.example.wetter_app.R
import kotlinx.coroutines.launch
import com.example.wetter_app.data.LocationModel
import com.example.wetter_app.weather_api.Weather
import com.example.wetter_app.weather_api.WeatherAPI
import com.example.wetter_app.weather_api.hourly.HourlyWeatherHour
import kotlinx.datetime.Clock
import com.example.wetter_app.Logic.WeatherDataHandler
import com.example.wetter_app.weather_api.daily.DailyWeatherDay
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun WeatherApp() {
    val navController: NavHostController = rememberNavController()
    val drawerState = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val drawerWidth = 250.dp

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE57373))
    ) {
        NavHost(navController = navController, startDestination = "weatherMainScreen") {
            composable(route = "weatherMainScreen") {
                WeatherMainScreen(
                    navController = navController,
                    openDrawer = { drawerState.value = true },
                    weatherDataHandler = WeatherDataHandler(WeatherAPI())
                )
            }
            composable(route = "aboutPage") {
                AboutPage(
                    navController = navController
                )
            }
        }

        AnimatedVisibility(
            visible = drawerState.value,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(drawerWidth)
                    .background(Color.White)
            ) {
                DrawerContent(navController) {
                    coroutineScope.launch {
                        drawerState.value = false
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherMainScreen(
    navController: NavHostController,
    openDrawer: () -> Unit,
    weatherDataHandler: WeatherDataHandler
) {
    val locationName by LocationModel.locationName.collectAsState()
    val latitude by LocationModel.latitude.collectAsState()
    val longitude by LocationModel.longitude.collectAsState()
    val isMetric by UnitSystem.isMetric.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var currentWeather by remember { mutableStateOf<Weather?>(null) }
    var hourlyWeatherData by remember { mutableStateOf<List<HourlyWeatherHour>?>(null) }
    var dailyWeatherData by remember { mutableStateOf<List<DailyWeatherDay>?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var lastFetchTimeMillis by remember { mutableStateOf<Long?>(null) }
    val imageHandler = ImageHandler()
    var showRainRadar by remember { mutableStateOf(false) }
    var currentDayIndex by remember { mutableStateOf(0) }

    LaunchedEffect(latitude, longitude) {
        try {
            val (current, hourly, daily) = weatherDataHandler.initialize(latitude, longitude)
            currentWeather = current?.current
            hourlyWeatherData = hourly
            dailyWeatherData = daily
            showDialog = false
            lastFetchTimeMillis = Clock.System.now().toEpochMilliseconds()
            currentDayIndex = 0
        } catch (e: Exception) {
            println("Error fetching weather data: ${e.message}")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE57373))
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    locationName,
                                    color = Color.White,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                                lastFetchTimeMillis?.let { lastFetchTime ->
                                    val currentTime = Clock.System.now().toEpochMilliseconds()
                                    val minutesAgo = (currentTime - lastFetchTime) / (1000 * 60)
                                    Text(
                                        text = "Data fetched $minutesAgo minutes ago",
                                        color = Color.White,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { openDrawer() }) {
                            Image(
                                painter = painterResource(id = R.drawable.menu),
                                contentDescription = "Menu",
                                Modifier.size(28.dp)
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            coroutineScope.launch {
                                try {
                                    currentWeather = weatherDataHandler.syncWeather(latitude, longitude)?.current
                                    hourlyWeatherData = weatherDataHandler.syncHourlyWeather(latitude, longitude)
                                    dailyWeatherData = weatherDataHandler.syncDailyWeather(latitude, longitude)
                                    lastFetchTimeMillis = Clock.System.now().toEpochMilliseconds()
                                } catch (e: Exception) {
                                    println("Error syncing weather data: ${e.message}")
                                }
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Sync Data",
                                tint = Color.White
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
                    .padding(padding)
                    .padding(horizontal = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    if (showRainRadar) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 64.dp)
                        ) {
                            RainRadar(latitude, longitude)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                    currentWeather?.let { weather ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(350.dp)
                                .padding(horizontal = 16.dp)
                                .clickable { showDialog = true },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Image(
                                    painter = painterResource(
                                        id = imageHandler.getImageResId(
                                            weather.temperature,
                                            weather.windSpeed
                                        )
                                    ),
                                    contentDescription = "Weather Image",
                                    modifier = Modifier.size(150.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Temperature: ${if (isMetric) "${weather.temperature}°C" else "${(weather.temperature * 9 / 5 + 32).toInt()}°F"}",
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    "Windspeed: ${if (isMetric) "${weather.windSpeed} km/h" else "${(weather.windSpeed / 1.609).toInt()} mph"}",
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    "Humidity: ${weather.humidity}%",
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    "Precipitation: ${weather.precipitation} mm",
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Text(
                        text = "Forecast",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )

                    dailyWeatherData?.let { dailyData ->
                        if (dailyData.isNotEmpty()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                IconButton(
                                    onClick = {
                                        if (currentDayIndex > 0) {
                                            currentDayIndex -= 1
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowBack,
                                        contentDescription = "Previous Day",
                                        tint = Color.White
                                    )
                                }

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    val currentDay = dailyData.getOrNull(currentDayIndex)
                                    if (currentDay != null) {
                                        val parsedDate = LocalDate.parse(currentDay.time)
                                        val formattedDate = parsedDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
                                        Text(
                                            text = formattedDate,
                                            color = Color.White,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = "Max Temp: ${currentDay.maxTemperature}°${if (isMetric) "C" else "F"}",
                                            color = Color.White,
                                            fontSize = 15.sp
                                        )
                                        Text(
                                            text = "Min Temp: ${currentDay.minTemperature}°${if (isMetric) "C" else "F"}",
                                            color = Color.White,
                                            fontSize = 15.sp
                                        )
                                        Text(
                                            text = "Precipitation: ${currentDay.maxPrecipitationProbability}%",
                                            color = Color.White,
                                            fontSize = 15.sp
                                        )
                                    } else {
                                        Text(
                                            text = "No data available",
                                            color = Color.White,
                                            fontSize = 15.sp
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = {
                                        if (currentDayIndex < dailyData.size - 1) {
                                            currentDayIndex += 1
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowForward,
                                        contentDescription = "Next Day",
                                        tint = Color.White
                                    )
                                }
                            }
                        } else {
                            Text(
                                text = "No daily weather data available",
                                color = Color.White,
                                fontSize = 15.sp
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(onClick = { showRainRadar = !showRainRadar }) {
                            Text(
                                text = if (showRainRadar) "Hide Rain Radar" else "Show Rain Radar"
                            )
                        }
                    }
                }

                if (showDialog) {
                    hourlyWeatherData?.let { data ->
                        WeatherDetailsDialog(hourlyWeatherData = data, onDismiss = { showDialog = false })
                    }
                }
            }
        }
    }
}
