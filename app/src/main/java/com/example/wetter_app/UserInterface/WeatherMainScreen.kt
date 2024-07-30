package com.example.wetter_app.UserInterface

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
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
fun WeatherMainScreen(navController: NavHostController, openDrawer: () -> Unit, weatherDataHandler: WeatherDataHandler) {
    val locationName by LocationModel.locationName.collectAsState()
    val latitude by LocationModel.latitude.collectAsState()
    val longitude by LocationModel.longitude.collectAsState()
    val isMetric by UnitSystem.isMetric.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var currentWeather by remember { mutableStateOf<Weather?>(null) }
    var hourlyWeatherData by remember { mutableStateOf<List<HourlyWeatherHour>?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var lastFetchTimeMillis by remember { mutableStateOf<Long?>(null) }
    val imageHandler = ImageHandler()
    var showRainRadar by remember { mutableStateOf(false) }

    LaunchedEffect(latitude, longitude) {
        try {
            val (current, hourly) = weatherDataHandler.initialize(latitude, longitude)
            currentWeather = current?.current
            hourlyWeatherData = hourly
            showDialog = false
            lastFetchTimeMillis = Clock.System.now().toEpochMilliseconds()
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
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp)
                                    .padding(horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                IconButton(onClick = { openDrawer() }) {
                                    Image(
                                        painter = painterResource(id = R.drawable.menu),
                                        contentDescription = "Menu",
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
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
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Weather Data",
                                        color = Color.White,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            coroutineScope.launch {
                                try {
                                    currentWeather = weatherDataHandler.syncWeather(latitude, longitude)?.current
                                    hourlyWeatherData = weatherDataHandler.syncHourlyWeather(latitude, longitude)
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
                        IconButton(onClick = {
                            UnitSystem.toggleUnitSystem()
                        }) {
                            Icon(
                                imageVector = if (isMetric) Icons.Default.Settings else Icons.Default.Settings,
                                contentDescription = "Toggle Units",
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
                        RainRadar(latitude, longitude)
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(550.dp)
                            .padding(horizontal = 16.dp)
                            .clickable { showDialog = true },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        currentWeather?.let { weather ->
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

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "About",
                    color = Color.White,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .clickable { navController.navigate("aboutPage") }
                        .padding(16.dp)
                )
            }

            if (showDialog) {
                hourlyWeatherData?.let { data ->
                    WeatherDetailsDialog(hourlyWeatherData = data, onDismiss = { showDialog = false })
                }
            }
        }
    }
}
