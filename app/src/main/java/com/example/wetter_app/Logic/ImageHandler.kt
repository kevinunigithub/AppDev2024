package com.example.wetter_app.Logic

import com.example.wetter_app.R

class ImageHandler {
    fun getImageResId(temp: Int, windSpeed: Int): Int {
        return when {
            temp > 25 -> R.drawable.sun
            temp < -5 -> R.drawable.ice
            windSpeed > 10 -> R.drawable.wind
            else -> R.drawable.rain
        }
    }
}