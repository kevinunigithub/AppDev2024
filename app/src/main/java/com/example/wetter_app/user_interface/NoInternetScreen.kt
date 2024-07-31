package com.example.wetter_app.user_interface

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wetter_app.R

@Composable
fun NoInternetScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE57373)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "No Internet",
                color = Color.White,
                modifier = Modifier.padding(bottom = 20.dp),
                fontSize = 24.sp
            )
            Text(
                text = "Please make sure you are connected and restart the app",
                color = Color.White,
                modifier = Modifier.padding(bottom = 12.dp),
                fontSize = 16.sp,
            )
            Image(
                painter = painterResource(id = R.drawable.sad),
                contentDescription = "No Internet",
                modifier = Modifier.size(250.dp)
            )
        }
    }
}