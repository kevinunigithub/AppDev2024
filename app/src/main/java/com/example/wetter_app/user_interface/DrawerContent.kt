package com.example.wetter_app.user_interface

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.wetter_app.Logic.LocationData
import com.example.wetter_app.Logic.UnitSystem
import com.example.wetter_app.R
import com.example.wetter_app.data.LocationModel
import kotlinx.coroutines.launch

@Composable
fun DrawerContent(navController: NavHostController, closeDrawer: () -> Unit) {
    val context = LocalContext.current as Activity
    val locationData = remember { LocationData(context) }
    val (searchResults, setSearchResults) = remember { mutableStateOf(emptyList<String>()) }
    val coroutineScope = rememberCoroutineScope()
    val isMetric by UnitSystem.isMetric.collectAsState()
    val myLocation = "My Location"

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .background(Color(0xFFE57373))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { closeDrawer() }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                painter = painterResource(id = R.drawable.ic_right_arrow),
                contentDescription = "",
                modifier = Modifier.size(24.dp)
            )
        }

        Row {
            SearchBar(
                onSearch = { query ->
                    coroutineScope.launch {
                        val results = locationData.getSearchResults(query)
                        setSearchResults(results)
                    }
                },
                searchResults = searchResults,
                onSelectResult = { result ->
                    coroutineScope.launch {
                        if (result.equals(myLocation, ignoreCase = true)) {
                            locationData.getCurrentLocationData { lat, lon ->
                                LocationModel.updateLocation(myLocation, lat, lon)
                            }
                        } else {
                            locationData.getLocationDataOfChosenPlace(result) { lat, lon ->
                                LocationModel.updateLocation(result, lat, lon)
                            }
                        }
                        closeDrawer()
                    }
                }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    UnitSystem.toggleUnitSystem()
                    closeDrawer()
                }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Switch to ${if (isMetric) "Imperial" else "Metric"} Units",
                color = Color.Black,
                fontSize = 16.sp,
            )
        }

        Text(
            text = "About",
            color = Color.Black,
            fontSize = 16.sp,
            modifier = Modifier
                .clickable {
                    navController.navigate("aboutPage")
                    closeDrawer()
                }
                .padding(16.dp)
        )
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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )

            if (expanded && searchResults.isNotEmpty()) {
                val displayResults = listOf("My Location") + searchResults

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp)
                        .background(Color.White)
                        .border(1.dp, Color.Gray)
                ) {
                    LazyColumn {
                        items(displayResults) { result ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        text = result
                                        expanded = false
                                        onSelectResult(result)
                                    }
                                    .padding(16.dp)
                            ) {
                                Text(result)
                            }
                        }
                    }
                }
            }
        }
    }
}
