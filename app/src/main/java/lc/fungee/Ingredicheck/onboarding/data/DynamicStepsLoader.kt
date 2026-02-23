package lc.fungee.Ingredicheck.onboarding.data

import android.content.Context
import android.util.Log
import kotlinx.serialization.json.Json
import java.io.IOException

private const val ASSET_FILE = "dynamicJsonData.json"
private const val TAG = "DynamicJsonData"

private val json = Json { ignoreUnknownKeys = true }

/**
 * Loads and caches onboarding steps from assets/dynamicJsonData.json (same as iOS).
 * Call [ensureLoaded] when you have a context (e.g. from OnboardingHost); then
 * [OnboardingChipData] will use dynamic steps when [getSteps] is non-null.
 */
object DynamicStepsLoader {

    @Volatile
    private var cachedSteps: List<DynamicStep>? = null

    /** Returns cached steps if already loaded; null if not loaded or load failed. */
    fun getSteps(): List<DynamicStep>? = cachedSteps

    /** Loads from assets if not yet loaded. Safe to call multiple times. */
    fun ensureLoaded(context: Context): List<DynamicStep>? {
        if (cachedSteps != null) {
            Log.d(TAG, "JSON data: using cached steps (count=${cachedSteps!!.size}), already loaded — UI will use same data after restart")
            return cachedSteps
        }
        val steps = loadFromAssets(context)
        if (steps != null) {
            cachedSteps = steps
            Log.d(TAG, "JSON data: loaded successfully from assets. steps count=${steps.size}, stepIds=${steps.map { it.id }}")
        } else {
            Log.w(TAG, "JSON data: load failed — no steps available; UI will have empty onboarding steps")
        }
        return steps
    }

    private fun loadFromAssets(context: Context): List<DynamicStep>? {
        Log.d(TAG, "JSON data: loading from assets/$ASSET_FILE...")
        return try {
            context.assets.open(ASSET_FILE).use { input ->
                val text = input.bufferedReader().use { it.readText() }
                val payload = json.decodeFromString<DynamicStepsPayload>(text)
                val steps = payload.steps
                Log.d(TAG, "JSON data: parse OK. steps count=${steps.size} — all cases (including after restart) will use this data")
                steps
            }
        } catch (e: IOException) {
            Log.e(TAG, "JSON data: load failed (IOException) ${e.message}", e)
            null
        } catch (e: kotlinx.serialization.SerializationException) {
            Log.e(TAG, "JSON data: load failed (parse error) ${e.message}", e)
            null
        }
    }
}
