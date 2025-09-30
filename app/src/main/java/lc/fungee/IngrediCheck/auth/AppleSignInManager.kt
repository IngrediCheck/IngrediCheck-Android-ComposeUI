package lc.fungee.IngrediCheck.auth

import android.app.Activity
import android.net.Uri
import android.util.Log
import androidx.browser.customtabs.CustomTabsIntent

object AppleSignInManager {

    fun startAppleSignIn(activity: Activity) {
        // Open Supabase authorize endpoint. Supabase will handle Apple OAuth and
        // redirect back to our app scheme with a `code` which we exchange for a session.
        val supabaseUrl = AuthEnv.SUPABASE_URL
        val appRedirect = "${AppleAuthConfig.APP_SCHEME}://${AppleAuthConfig.APP_HOST}"

        val authUri = Uri.parse(supabaseUrl).buildUpon()
            .appendPath("auth")
            .appendPath("v1")
            .appendPath("authorize")
            .appendQueryParameter("provider", "apple")
            .appendQueryParameter("redirect_to", appRedirect)
            .appendQueryParameter("flow_type", "pkce")
            .build()

        Log.d("AppleSignInManager", "Launching Supabase authorize URL: $authUri")
        val customTabsIntent = CustomTabsIntent.Builder().build()
        customTabsIntent.launchUrl(activity, authUri)
    }
}
