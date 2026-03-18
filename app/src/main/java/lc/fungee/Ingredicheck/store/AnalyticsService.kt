package lc.fungee.Ingredicheck.store

import android.os.Build
import android.content.Context
import android.util.Log
import lc.fungee.Ingredicheck.BuildConfig
import com.posthog.PostHog
import com.posthog.android.PostHogAndroid
import com.posthog.android.PostHogAndroidConfig

object AnalyticsService {
	private const val TAG = "AnalyticsService"

    private val apiKey: String
        get() = BuildConfig.POSTHOG_API_KEY

    private val host: String
        get() = BuildConfig.POSTHOG_HOST

    private val isEnabled: Boolean
        get() = apiKey.isNotBlank()

    fun configure(context: Context) {
		if (!isEnabled) {
			Log.d(TAG, "PostHog disabled (missing POSTHOG_API_KEY)")
			return
		}

		Log.d(
			TAG,
			"Configuring PostHog: host=$host key_present=${apiKey.isNotBlank()} sdk=${Build.VERSION.SDK_INT}"
		)

        val config = PostHogAndroidConfig(
            apiKey = apiKey,
            host = host
        ).apply {
            captureApplicationLifecycleEvents = true

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                sessionReplay = true
                sessionReplayConfig.maskAllTextInputs = false
                sessionReplayConfig.maskAllImages = false
                sessionReplayConfig.captureLogcat = true
                sessionReplayConfig.screenshot = true
                sessionReplayConfig.throttleDelayMs = 1000
                sessionReplayConfig.sampleRate = null
            } else {
                Log.d(TAG, "Session replay disabled (requires API 26+)")
            }
        }

        PostHogAndroid.setup(context, config)
		Log.d(TAG, "PostHog setup complete")
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
        if (!isEnabled) {
            Log.d(TAG, "capture skipped (disabled): event=$event")
            return
        }

        Log.d(TAG, "capture: event=$event props=${properties.keys}")
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
