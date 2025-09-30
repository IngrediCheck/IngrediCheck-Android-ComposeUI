package lc.fungee.IngrediCheck.auth

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.os.Message
import android.webkit.*
import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import android.view.ViewGroup
import android.widget.FrameLayout

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

        webView.webChromeClient = object : WebChromeClient() {
            override fun onCreateWindow(view: WebView?, isDialog: Boolean, isUserGesture: Boolean, resultMsg: Message?): Boolean {
                val newWebView = WebView(this@AppleLoginWebViewActivity).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                }
                (this@AppleLoginWebViewActivity.findViewById<ViewGroup>(android.R.id.content) as ViewGroup)
                    .addView(newWebView)
                val transport = resultMsg?.obj as? WebView.WebViewTransport
                transport?.webView = newWebView
                resultMsg?.sendToTarget()
                return true
            }
        }

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (url != null && url.startsWith(redirectUri)) {
                    val uri = Uri.parse(url)
                    val resultIntent = Intent()
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
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                    return true
                }
                return false
            }

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url?.toString() ?: return false
                if (url.startsWith(redirectUri)) {
                    val uri = Uri.parse(url)
                    val resultIntent = Intent()
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
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                    return true
                }
                return false
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                super.onReceivedError(view, request, error)
                setResult(Activity.RESULT_CANCELED)
                finish()
            }
        }

        webView.loadUrl(authUrl)
    }
}

