package com.cebolarekords.player.ui.streaming

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
        modifier = modifier.clip(RoundedCornerShape(12.dp)), // Consistência nas bordas.
        factory = { context ->
            WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                webViewClient = WebViewClient()
                settings.javaScriptEnabled = true

                // REFINAMENTO: Impede a reprodução automática de mídia.
                // A mídia só iniciará com um gesto do usuário.
                // Isso corrige o problema do player do YouTube iniciar sozinho, melhorando a performance e a UX.
                settings.mediaPlaybackRequiresUserGesture = true
            }
        },
        update = { webView ->
            // REFINAMENTO: Verifica se a URL atual é diferente da nova para evitar recargas desnecessárias
            // em recomposições, otimizando a performance.
            if (webView.url != embedUrl) {
                webView.loadUrl(embedUrl)
            }
        }
    )
}