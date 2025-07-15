package com.cebola.rekords.ui.streaming

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebContentPlayer(
    embedUrl: String,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier.clip(RoundedCornerShape(12.dp)),
        factory = { context ->
            WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                webViewClient = WebViewClient()
                settings.javaScriptEnabled = true
                settings.mediaPlaybackRequiresUserGesture = true
            }
        },
        update = { webView ->
            if (webView.url != embedUrl) {
                webView.loadUrl(embedUrl)
            }
        }
    )
}