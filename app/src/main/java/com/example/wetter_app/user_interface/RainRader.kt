package com.example.wetter_app.user_interface

import android.net.http.SslError
import android.webkit.SslErrorHandler
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun RainRadar(lat: Double, long: Double) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(horizontal = 6.dp)
            .height(100.dp)
    ) {

        WebViewComponent(
            url = "https://www.ventusky.com/?p=${lat};${long};11&l=rain-3h",
            modifier = Modifier
                .fillMaxSize()
        )
    }
}

@Composable
fun WebViewComponent(url: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    AndroidView(factory = {
        WebView(context).apply {
            webViewClient = object : WebViewClient() {
                override fun onReceivedSslError(
                    view: WebView?,
                    handler: SslErrorHandler?,
                    error: SslError?
                ) {
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
