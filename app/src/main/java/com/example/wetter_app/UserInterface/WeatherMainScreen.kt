package com.example.wetter_app.UserInterface

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.wetter_app.Logic.HourlyWeatherData
import com.example.wetter_app.Logic.ImageHandler
import com.example.wetter_app.Logic.LocationData
import com.example.wetter_app.Logic.WeatherData
import com.example.wetter_app.R
import kotlinx.coroutines.launch

@Composable
fun WeatherApp() {
    val navController: NavHostController = rememberNavController()
    NavHost(navController = navController, startDestination = "weatherMainScreen") {
        composable(route = "weatherMainScreen") {
            WeatherMainScreen(
                navController = navController
            )
        }
        composable(route = "aboutPage") {
            AboutPage(
                navController = navController
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherMainScreen(navController: NavHostController) {
    val context = LocalContext.current as Activity
    val locationData = remember { LocationData(context) }
    //sample Data
    val weatherDataList = listOf(
        WeatherData(
            "Monday, 13th July 2024", "Klagenfurt", listOf(
                HourlyWeatherData("13:00", 25, 10, 20),
                HourlyWeatherData("14:00", 26, 11, 30),
                HourlyWeatherData("15:00", 27, 12, 25)
            )
        ),
        WeatherData(
            "Tuesday, 14th July 2024", "Klagenfurt", listOf(
                HourlyWeatherData("13:00", 28, 13, 10),
                HourlyWeatherData("14:00", 29, 14, 15),
                HourlyWeatherData("15:00", 30, 15, 10)
            )
        ),
        WeatherData(
            "Wednesday, 15th July 2024", "Klagenfurt", listOf(
                HourlyWeatherData("13:00", 22, 8, 50),
                HourlyWeatherData("14:00", 21, 9, 55),
                HourlyWeatherData("15:00", 20, 10, 60)
            )
        )
    )

    val imageHandler = ImageHandler()
    val (location, setLocation) = remember { mutableStateOf("Klagenfurt") }
    val (currentWeatherIndex, setCurrentWeatherIndex) = remember { mutableStateOf(0) }
    val (searchResults, setSearchResults) = remember { mutableStateOf(emptyList<String>()) }
    val (selectedLocation, setSelectedLocation) = remember { mutableStateOf<Pair<Double, Double>?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val (showDialog, setShowDialog) = remember { mutableStateOf(false) }

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
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                currentWeatherData.date,
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* Will be implemented later */ }) {
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
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SearchBar(
                    onSearch = { query ->
                        coroutineScope.launch {
                            if (query.equals("My Location", ignoreCase = true)) {
                                locationData.getCurrentLocationData { lat, lon ->
                                    setSelectedLocation(lat to lon)
                                    setLocation("My Location")
                                }
                            } else {
                                val results = locationData.getSearchResults(query)
                                setSearchResults(results)
                            }
                        }
                    },
                    searchResults = searchResults,
                    onSelectResult = { result ->
                        coroutineScope.launch {
                            if (result.equals("My Location", ignoreCase = true)) {
                                locationData.getCurrentLocationData { lat, lon ->
                                    setSelectedLocation(lat to lon)
                                    setLocation("My Location")
                                    // Call on display weather data of location
                                }
                            } else {
                                locationData.getLocationDataOfChosenPlace(result) { lat, lon ->
                                    setSelectedLocation(lat to lon)
                                    setLocation(result)
                                    // Call on display weather data of location
                                }
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .padding(horizontal = 16.dp)
                        .clickable { setShowDialog(true) },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    IconButton(
                        onClick = { setCurrentWeatherIndex((currentWeatherIndex - 1 + weatherDataList.size) % weatherDataList.size) },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_left_arrow),
                            contentDescription = "Previous Weather",
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp)
                    ) {
                        Text(
                            "Temperature: ${currentWeatherData.averageTemperature}°C",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            "Windspeed: ${currentWeatherData.averageWindSpeed} km/h",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            "Rain: ${currentWeatherData.averageRainPercentage}%",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Image(
                            painter = painterResource(id = imageHandler.getImageResId(currentWeatherData.averageTemperature, currentWeatherData.averageWindSpeed)),
                            contentDescription = "Weather Image",
                            modifier = Modifier
                                .size(200.dp)
                        )
                    }

                    IconButton(
                        onClick = { setCurrentWeatherIndex((currentWeatherIndex + 1) % weatherDataList.size) },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_right_arrow),
                            contentDescription = "Next Weather",
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                RainRadar()

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "About",
                    color = Color.White,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .clickable { navController.navigate("aboutPage") }
                        .padding(16.dp)
                )
            }
        }

        if (showDialog) {
            WeatherDetailsDialog(weatherData = currentWeatherData, onDismiss = { setShowDialog(false) })
        }
    }
}

@Composable
fun SearchBar(
    onSearch: (query: String) -> Unit,
    searchResults: List<String>,
    onSelectResult: (result: String) -> Unit
) {
    var text by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Column {
            TextField(
                value = text,
                onValueChange = {
                    text = it
                    expanded = it.isNotEmpty()
                    onSearch(it)
                },
                label = { Text("Search location") },
                modifier = Modifier.fillMaxWidth()
            )

            if (expanded && searchResults.isNotEmpty()) {
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    searchResults.forEach { result ->
                        DropdownMenuItem(
                            onClick = {
                                text = result
                                expanded = false
                                onSelectResult(result)
                            }
                        ) {
                            Text(result)
                        }
                    }
                }
            }
        }
    }
}
