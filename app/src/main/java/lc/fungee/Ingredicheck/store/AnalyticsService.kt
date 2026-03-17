package lc.fungee.Ingredicheck.store

import android.os.Build
import android.content.Context
import lc.fungee.Ingredicheck.BuildConfig
import com.posthog.PostHog
import com.posthog.android.PostHogAndroid
import com.posthog.android.PostHogAndroidConfig

object AnalyticsService {
    private const val POSTHOG_API_KEY: String = "phc_BFYelq2GeyigXBP3MgML57wKoWfLe5MW7m6HMYhtX8m"
    private const val POSTHOG_HOST: String = "https://us.i.posthog.com"

    private val isEnabled: Boolean
        get() = !BuildConfig.DEBUG

    fun configure(context: Context) {
        if (!isEnabled) return

        val config = PostHogAndroidConfig(
            apiKey = POSTHOG_API_KEY,
            host = POSTHOG_HOST
        ).apply {
            captureApplicationLifecycleEvents = true

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                sessionReplay = true
                sessionReplayConfig.maskAllTextInputs = false
                sessionReplayConfig.maskAllImages = false
                sessionReplayConfig.screenshot = true
                sessionReplayConfig.throttleDelayMs = 1000
            }
        }

        PostHogAndroid.setup(context, config)
    }

    fun resetAnalytics(preservingInternalFlag: Boolean?) {
        if (!isEnabled) return
        PostHog.reset()
        if (preservingInternalFlag != null) {
            registerInternalSuperProperty(preservingInternalFlag)
        }
    }

    fun registerInternalSuperProperty(isInternal: Boolean) {
        if (!isEnabled) return
        PostHog.register("is_internal", isInternal)
    }

    fun capture(event: String, properties: Map<String, Any> = emptyMap()) {
        if (!isEnabled) return
        PostHog.capture(event = event, properties = properties)
    }

    fun trackOnboarding(event: String, properties: Map<String, Any> = emptyMap()) {
        capture(event, properties)
    }

    fun captureApiError(
        endpoint: String,
        errorType: String,
        statusCode: Int? = null,
        error: String? = null
    ) {
        val props = buildMap<String, Any> {
            put("endpoint", endpoint)
            put("error_type", errorType)
            if (statusCode != null) put("status_code", statusCode)
            if (error != null) put("error", error)
        }
        capture("API Error", props)
    }

    fun refreshAnalyticsIdentity(
        distinctId: String,
        email: String?,
        isInternalUser: Boolean?,
        authProvider: String
    ) {
        if (!isEnabled) return

        val userProps = buildMap<String, Any> {
            if (isInternalUser != null) put("is_internal", isInternalUser)
            if (!email.isNullOrBlank()) put("email", email)
            put("auth_provider", authProvider)
        }

        PostHog.identify(
            distinctId = distinctId,
            userProperties = userProps
        )
    }
}
