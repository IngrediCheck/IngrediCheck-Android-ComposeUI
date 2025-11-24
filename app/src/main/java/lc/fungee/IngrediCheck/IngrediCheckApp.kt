package lc.fungee.IngrediCheck

import android.app.Application
import com.posthog.android.PostHogAndroid
import com.posthog.android.PostHogAndroidConfig
import lc.fungee.IngrediCheck.di.AppContainer

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
        // Note: is_internal is registered after device registration completes (server-driven)
    }

    companion object {
        lateinit var appInstance: IngrediCheckApp
            private set
    }
}
