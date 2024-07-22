package com.example.wetter_app.UserInterface


import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
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
    val weatherDataList = listOf(
        WeatherData("Monday, 13th July 2024", 25, 10, "Klagenfurt"),
        WeatherData("Tuesday, 14th July 2024", 28, 12, "Klagenfurt"),
        WeatherData("Wednesday, 15th July 2024", 22, 8, "Klagenfurt")
    )

    val imageHandler = ImageHandler()
    val (location, setLocation) = remember { mutableStateOf("Klagenfurt") }
    val (currentWeatherIndex, setCurrentWeatherIndex) = remember { mutableStateOf(0) }
    val (searchResults, setSearchResults) = remember { mutableStateOf(emptyList<String>()) }
    val (selectedLocation, setSelectedLocation) = remember { mutableStateOf<Pair<Double, Double>?>(null) }
    val coroutineScope = rememberCoroutineScope()

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
