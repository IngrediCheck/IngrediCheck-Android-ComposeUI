package lc.fungee.IngrediCheck.analytics

import com.posthog.PostHog

object Analytics {
    // Generic capture helper that drops nulls and enforces snake_case property keys externally
    private fun capture(event: String, properties: Map<String, Any?> = emptyMap()) {
        val clean: Map<String, Any> = properties
            .filterValues { it != null }
            .mapValues { it.value as Any }
        PostHog.capture(event = event, properties = clean)
    }

    // Session Replay: force-send some events immediately if desired
    fun flush() {
        PostHog.flush()
    }

    // Product extraction
    fun trackProductExtraction(clientActivityId: String, productName: String?) {
        capture(
            event = "Extracted product details",
            properties = mapOf(
                "client_activity_id" to clientActivityId,
                "product_name" to (productName ?: "No Name Found")
            )
        )
    }

    // Preference input start (POST/PUT)
    fun trackUserPreferenceInput(
        requestId: String,
        endpoint: String,
        clientActivityId: String,
        preferenceText: String,
        method: String,
        itemId: String?,
        startTimeMs: Long
    ) {
        val props = mutableMapOf<String, Any?>(
            "request_id" to requestId,
            "endpoint" to endpoint,
            "client_activity_id" to clientActivityId,
            "preference_text" to preferenceText,
            "method" to method,
            "start_time" to startTimeMs.toString()
        )
        if (!itemId.isNullOrBlank()) props["item_id"] = itemId
        capture("User Inputed Preference", props)
    }

    fun trackPreferenceValidationSuccess(
        requestId: String,
        clientActivityId: String,
        preferenceText: String,
        latencyMs: Long
    ) {
        capture(
            event = "User Input Validation Successful",
            properties = mapOf(
                "request_id" to requestId,
                "client_activity_id" to clientActivityId,
                "preference_text" to preferenceText,
                "latency_ms" to latencyMs
            )
        )
    }

    fun trackPreferenceValidationError(
        requestId: String,
        clientActivityId: String,
        preferenceText: String,
        latencyMs: Long,
        error: String
    ) {
        capture(
            event = "User Input Validation Error",
            properties = mapOf(
                "request_id" to requestId,
                "client_activity_id" to clientActivityId,
                "preference_text" to preferenceText,
                "latency_ms" to latencyMs,
                "error" to error
            )
        )
    }

    fun trackPreferenceValidationBadResponse(
        requestId: String,
        clientActivityId: String,
        preferenceText: String,
        statusCode: Int,
        latencyMs: Long
    ) {
        capture(
            event = "User Input Validation: Bad response from the server",
            properties = mapOf(
                "request_id" to requestId,
                "client_activity_id" to clientActivityId,
                "preference_text" to preferenceText,
                "status_code" to statusCode,
                "latency_ms" to latencyMs
            )
        )
    }

    // Barcode scanning timing helpers
    fun trackBarcodeStarted(): Long {
        val startMs = System.currentTimeMillis()
        capture(
            event = "Barcode Started Scanning",
            properties = mapOf(
                "start_time" to (startMs.toDouble() / 1000.0)
            )
        )
        return startMs
    }

    fun trackBarcodeCompleted(startMs: Long, barcode: String) {
        val latencyMs = System.currentTimeMillis() - startMs
        capture(
            event = "Barcode Scanning Completed",
            properties = mapOf(
                "latency_ms" to latencyMs,
                "barcode_number" to barcode
            )
        )
    }

    fun trackInputValidation(durationMs: Long) {
        capture(
            event = "Input Validate",
            properties = mapOf(
                "duration_ms" to durationMs
            )
        )
        flush()
    }

    // iOS-parity events
    fun trackHomeViewAppeared() {
        PostHog.capture(event = "Home View Appeared")
    }

    fun trackButtonTapped(buttonType: String) {
        // Property key must match iOS exactly (capitalized, with space)
        PostHog.capture(event = "Button Tapped", properties = mapOf("Button Type" to buttonType))
    }

    fun trackImageCaptured(epochSeconds: Double) {
        // Property key must match iOS exactly
        PostHog.capture(event = "Image Captured", properties = mapOf("time" to epochSeconds))
    }
}
