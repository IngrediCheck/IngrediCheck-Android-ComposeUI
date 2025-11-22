package lc.fungee.IngrediCheck

import android.app.Application
import android.content.pm.ApplicationInfo
import android.os.Build
import com.posthog.android.PostHogAndroid
import com.posthog.android.PostHogAndroidConfig
import lc.fungee.IngrediCheck.di.AppContainer
import com.posthog.PostHog

class IngrediCheckApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        configurePostHog()
        container = AppContainer(this)
        appInstance = this
    }

    private fun configurePostHog() {
        val POSTHOG_API_KEY = "phc_BFYelq2GeyigXBP3MgML57wKoWfLe5MW7m6HMYhtX8m"
        val POSTHOG_HOST = "https://us.i.posthog.com"

        val config = PostHogAndroidConfig(
            apiKey = POSTHOG_API_KEY,
            host = POSTHOG_HOST
        ).apply {
            // Enable useful defaults
            captureApplicationLifecycleEvents = true
            // For v2, session replay configuration options are limited; enable if available in this version
            // sessionReplay = true
        }

        PostHogAndroid.setup(this, config)
        PostHog.register("is_internal", defaultInternalFlag())
    }

    private fun defaultInternalFlag(): Boolean {
        if ((applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0) return true
        val fingerprint = Build.FINGERPRINT.lowercase()
        val model = Build.MODEL.lowercase()
        val product = Build.PRODUCT.lowercase()
        val hardware = Build.HARDWARE.lowercase()
        return fingerprint.contains("generic") ||
                fingerprint.contains("unknown") ||
                model.contains("emulator") ||
                model.contains("android sdk built for x86") ||
                product.contains("sdk") ||
                product.contains("emulator") ||
                hardware.contains("goldfish") ||
                hardware.contains("ranchu")
    }

    companion object {
        lateinit var appInstance: IngrediCheckApp
            private set
    }
}
