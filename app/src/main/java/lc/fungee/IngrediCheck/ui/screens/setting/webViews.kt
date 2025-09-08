package lc.fungee.IngrediCheck.ui.screens.setting

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.dp
import lc.fungee.IngrediCheck.ui.theme.PrimaryGreen100

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewScreen(url: String) {
    var isLoading by remember { mutableStateOf(true) }
    var webView by remember { mutableStateOf<WebView?>(null) }
    var  haserror by remember { mutableStateOf(false)}


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White) // Set white background to avoid black flash
    ) {
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    // Set white background to prevent black flash
                    setBackgroundColor(android.graphics.Color.WHITE)

                    settings.javaScriptEnabled = true
                    settings.loadWithOverviewMode = true
                    settings.useWideViewPort = true
                    settings.builtInZoomControls = false
                    settings.displayZoomControls = false

                    // Enable scrolling
                    isVerticalScrollBarEnabled = true
                    isHorizontalScrollBarEnabled = false

                    // Allow scrolling with gestures
                    setOnTouchListener { v, event ->
                        v.parent.requestDisallowInterceptTouchEvent(true)
                        false
                    }

                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )

                    // Custom WebViewClient to handle loading states
                    webViewClient = object : WebViewClient() {
                        override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                            super.onPageStarted(view, url, favicon)
                            isLoading = true
                            haserror = false
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            isLoading = false
                        }
                        override fun onReceivedError(
                            view: WebView?,
                            request: android.webkit.WebResourceRequest?,
                            error: android.webkit.WebResourceError?
                        ) {
                            super.onReceivedError(view, request, error)
                            haserror = true   // 👈 mark error
                        }

                        override fun onReceivedHttpError(
                            view: WebView?,
                            request: android.webkit.WebResourceRequest?,
                            errorResponse: android.webkit.WebResourceResponse?
                        ) {
                            super.onReceivedHttpError(view, request, errorResponse)
                            haserror = true   // 👈 mark error
                        }

                        override fun onPageCommitVisible(view: WebView?, url: String?) {
                            super.onPageCommitVisible(view, url)
                            // This is called when the page becomes visible
                            isLoading = false
                        }
                    }

                    webView = this
                    loadUrl(url)
                }
            },
            modifier = Modifier.fillMaxSize()
        )
        if (haserror) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Black & white error message
                    androidx.compose.material3.Text(
                        text = "No Internet Connection",
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    androidx.compose.material3.Text(
                        text = "This page could not be loaded.",
                        color = Color.Gray
                    )
                }
            }
        }


        // Loading indicator
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = PrimaryGreen100,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }

    // Load URL when it changes
    LaunchedEffect(url) {
        webView?.loadUrl(url)
        isLoading = true
    }
}