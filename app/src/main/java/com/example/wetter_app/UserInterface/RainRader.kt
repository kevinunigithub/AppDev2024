package com.example.wetter_app.UserInterface

import android.net.http.SslError
import android.webkit.SslErrorHandler
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun RainRadar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(horizontal = 6.dp)
            .height(600.dp)
            .background(Color.Transparent)
    ) {
        WebViewComponent(
            url = "https://www.ventusky.com/?p=46.606;14.359;11&l=rain-3h",
            //vielleicht besser: https://www.rainviewer.com/map.html?loc=46.63,14.31,8&oFa=1&oC=1&oU=1&oCS=0&oF=0&oAP=0&rmt=1&c=1&lp=1&sm=1&sn=1
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
        )
    }
}

@Composable
fun WebViewComponent(url: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    AndroidView(factory = {
        WebView(context).apply {
            webViewClient = object : WebViewClient() {
                override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                    handler?.proceed()
                }
            }

            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.cacheMode = WebSettings.LOAD_NO_CACHE
            settings.useWideViewPort = true
            settings.loadWithOverviewMode = true

            loadUrl(url)
        }
    }, modifier = modifier)
}