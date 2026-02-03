package lc.fungee.Ingredicheck.auth

import android.app.Activity
import android.net.Uri
import android.util.Log
import androidx.browser.customtabs.CustomTabsIntent

object AppleSignInManager {

    fun startAppleSignIn(activity: Activity) {
        val appRedirect = "${AuthEnv.OAUTH_REDIRECT_SCHEME}://${AuthEnv.OAUTH_REDIRECT_HOST}"

        val authUri = Uri.parse(AuthEnv.SUPABASE_URL).buildUpon()
            .appendPath("auth")
            .appendPath("v1")
            .appendPath("authorize")
            .appendQueryParameter("provider", "apple")
            .appendQueryParameter("redirect_to", appRedirect)
            .appendQueryParameter("flow_type", "pkce")
            .build()

        Log.d("AppleSignIn", "Starting Apple OAuth in Custom Tabs. redirect_to=$appRedirect")
        Log.d("AppleSignIn", "Apple authorize URL: $authUri")

        val customTabsIntent = CustomTabsIntent.Builder().build()
        customTabsIntent.launchUrl(activity, authUri)
    }
}
