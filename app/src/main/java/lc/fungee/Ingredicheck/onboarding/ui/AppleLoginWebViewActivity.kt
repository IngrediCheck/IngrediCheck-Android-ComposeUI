package lc.fungee.Ingredicheck.onboarding.ui

import android.R
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.activity.ComponentActivity

class AppleLoginWebViewActivity : ComponentActivity() {

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val webView = WebView(this)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.javaScriptCanOpenWindowsAutomatically = true
        webView.settings.setSupportMultipleWindows(true)
        CookieManager.getInstance().setAcceptCookie(true)
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)
        webView.layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        setContentView(webView)

        val authUrl = intent.getStringExtra("auth_url") ?: return
        val redirectUri = intent.getStringExtra("redirect_uri") ?: return

        fun sanitizeUrl(raw: String): String {
            val uri = runCatching { Uri.parse(raw) }.getOrNull() ?: return raw

            val scrubKeys = setOf("id_token", "access_token", "refresh_token", "code", "token")
            val scrubbedQuery = uri.queryParameterNames
                .sorted()
                .joinToString("&") { key ->
                    val value = if (scrubKeys.contains(key)) "<redacted>" else (uri.getQueryParameter(key) ?: "")
                    "${key}=${Uri.encode(value)}"
                }

            val scrubbedFragment = uri.fragment
                ?.split("&")
                ?.mapNotNull { part ->
                    val idx = part.indexOf('=')
                    if (idx <= 0) return@mapNotNull part
                    val k = part.substring(0, idx)
                    val v = if (scrubKeys.contains(k)) "<redacted>" else part.substring(idx + 1)
                    "${k}=${v}"
                }
                ?.joinToString("&")

            return uri.buildUpon()
                .clearQuery()
                .fragment(scrubbedFragment)
                .encodedQuery(scrubbedQuery.ifBlank { null })
                .build()
                .toString()
        }

        Log.d("AppleWebView", "Starting Apple WebView auth. redirectUri=$redirectUri")
        Log.d("AppleWebView", "Auth URL: ${sanitizeUrl(authUrl)}")

        webView.webChromeClient = object : WebChromeClient() {
            override fun onCreateWindow(
                view: WebView?,
                isDialog: Boolean,
                isUserGesture: Boolean,
                resultMsg: Message?
            ): Boolean {
                val newWebView = WebView(this@AppleLoginWebViewActivity).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                }
                (this@AppleLoginWebViewActivity.findViewById<ViewGroup>(R.id.content) as ViewGroup)
                    .addView(newWebView)
                val transport = resultMsg?.obj as? WebView.WebViewTransport
                transport?.webView = newWebView
                resultMsg?.sendToTarget()
                return true
            }
        }

        fun finishWithUrl(url: String) {
            val uri = Uri.parse(url)
            val resultIntent = Intent()

            Log.d("AppleWebView", "Redirect detected: ${sanitizeUrl(url)}")

            val error = uri.getQueryParameter("error")
            val errorDescription = uri.getQueryParameter("error_description")
            if (!error.isNullOrBlank()) {
                Log.e(
                    "AppleWebView",
                    "Redirect contained error. error=$error description=$errorDescription"
                )
                resultIntent.putExtra("error", error)
                if (!errorDescription.isNullOrBlank()) {
                    resultIntent.putExtra("error_description", errorDescription)
                }
                setResult(RESULT_OK, resultIntent)
                finish()
                return
            }

            var idToken: String? = uri.getQueryParameter("id_token")
            var code: String? = uri.getQueryParameter("code")
            val fragment = uri.fragment

            if (idToken == null && fragment?.contains("id_token=") == true) {
                val fragParams = Uri.parse("scheme://host?${fragment}")
                idToken = fragParams.getQueryParameter("id_token")
            }
            if (code == null && fragment?.contains("code=") == true) {
                val fragParams = Uri.parse("scheme://host?${fragment}")
                code = fragParams.getQueryParameter("code")
            }

            if (idToken != null) {
                resultIntent.putExtra("id_token", idToken)
            } else if (code != null) {
                resultIntent.putExtra("code", code)
            }

            setResult(RESULT_OK, resultIntent)
            finish()
        }

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (url != null && url.startsWith(redirectUri)) {
                    finishWithUrl(url)
                    return true
                }
                if (url != null) {
                    Log.d("AppleWebView", "Navigating: ${sanitizeUrl(url)}")
                }
                return false
            }

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url?.toString() ?: return false
                if (url.startsWith(redirectUri)) {
                    finishWithUrl(url)
                    return true
                }

                Log.d("AppleWebView", "Navigating: ${sanitizeUrl(url)}")
                return false
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                super.onPageStarted(view, url, favicon)
                if (url != null) {
                    Log.d("AppleWebView", "Page started: ${sanitizeUrl(url)}")
                }
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                if (url != null) {
                    Log.d("AppleWebView", "Page finished: ${sanitizeUrl(url)}")
                }
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)

                val failingUrl = request?.url?.toString()
                Log.e(
                    "AppleWebView",
                    "WebView error code=${error?.errorCode} description=${error?.description} url=${failingUrl?.let { sanitizeUrl(it) }}"
                )
                setResult(RESULT_CANCELED)
                finish()
            }
        }

        webView.loadUrl(authUrl)
    }
}
