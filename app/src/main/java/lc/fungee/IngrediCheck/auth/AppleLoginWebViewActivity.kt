package lc.fungee.IngrediCheck.auth

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
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
        webView.layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        setContentView(webView)

        val authUrl = intent.getStringExtra("auth_url") ?: return
        val redirectUri = intent.getStringExtra("redirect_uri") ?: return

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (url != null && url.startsWith(redirectUri)) {
                    val uri = Uri.parse(url)
                    val idToken = uri.getQueryParameter("id_token")
                    val code = uri.getQueryParameter("code")
                    val resultIntent = Intent()
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

