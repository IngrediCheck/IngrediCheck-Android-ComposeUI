package lc.fungee.IngrediCheck.model.repository

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.booleanPreferencesKey

import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import lc.fungee.IngrediCheck.model.dto.DietaryPreference
import lc.fungee.IngrediCheck.model.dto.PreferenceValidationResult
import lc.fungee.IngrediCheck.model.entities.SafeEatsEndpoint
import lc.fungee.IngrediCheck.analytics.Analytics
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit
import java.util.UUID

private val Context.dataStore by preferencesDataStore("dietary_prefs")



object PrefKeys {
    val AUTO_SCAN = booleanPreferencesKey("auto_scan_on_app_start")
    // One-shot flag: when true, the app will auto-open the scanner on next launch and then clear it
    val AUTO_SCAN_PENDING = booleanPreferencesKey("auto_scan_pending")
}
@OptIn(kotlin.time.ExperimentalTime::class)
class PreferenceRepository(
    private val context: Context,
    private val supabaseClient: SupabaseClient,
    private val functionsBaseUrl: String,
    private val anonKey: String

) {

    val autoScanFlow: Flow<Boolean> =
        context.dataStore.data.map { it[PrefKeys.AUTO_SCAN] ?: false }

    // One-shot pending flag flow
    val autoScanPendingFlow: Flow<Boolean> =
        context.dataStore.data.map { it[PrefKeys.AUTO_SCAN_PENDING] ?: false }

    suspend fun setAutoScan(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[PrefKeys.AUTO_SCAN] = enabled
        }
    }

    suspend fun setAutoScanPending(pending: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[PrefKeys.AUTO_SCAN_PENDING] = pending
        }
    }

    // Returns current pending value and clears it if it was true
    suspend fun consumeAutoScanPending(): Boolean {
        val wasPending = context.dataStore.data.first()[PrefKeys.AUTO_SCAN_PENDING] ?: false
        if (wasPending) {
            context.dataStore.edit { prefs ->
                prefs[PrefKeys.AUTO_SCAN_PENDING] = false
            }
        }
        return wasPending
    }
    companion object {
        private val PREFS_KEY = stringPreferencesKey("preferences_json")
        private val GRANDFATHERED_KEY = stringPreferencesKey("grandfathered_prefs")
    }

    private val json = Json { ignoreUnknownKeys = true }
    private val client = OkHttpClient.Builder()
        .callTimeout(30, TimeUnit.SECONDS)
        .build()

    /** Save preferences locally */
    suspend fun saveLocal(preferences: List<DietaryPreference>) {
        val jsonString = json.encodeToString(ListSerializer(DietaryPreference.serializer()), preferences)
        context.dataStore.edit { prefs ->
            prefs[PREFS_KEY] = jsonString
        }
    }

    /** Load preferences from local DataStore */
    fun getLocal(): Flow<List<DietaryPreference>> =
        context.dataStore.data.map { prefs ->
            prefs[PREFS_KEY]?.let {
                runCatching { json.decodeFromString(ListSerializer(DietaryPreference.serializer()), it) }
                    .getOrDefault(emptyList())
            } ?: emptyList()
        }

    /** Save "grandfathered" prefs (strings only) */
    suspend fun saveGrandfathered(prefs: List<String>) {
        context.dataStore.edit { it[GRANDFATHERED_KEY] = prefs.joinToString("|") }
    }

    /** Get and clear grandfathered prefs */
    private suspend fun consumeGrandfathered(): List<String> {
        val prefs = context.dataStore.data.first()[GRANDFATHERED_KEY]
        if (prefs != null) {
            context.dataStore.edit { it.remove(GRANDFATHERED_KEY) }
            return prefs.split("|").filter { it.isNotBlank() }
        }
        return emptyList()
    }

    /** Upload grandfathered prefs if any */
    private suspend fun uploadGrandfatheredPreferences() {
        val legacyPrefs = consumeGrandfathered()
        if (legacyPrefs.isNotEmpty()) {
            val token = currentToken() ?: throw Exception("Not authenticated")
            val url = "$functionsBaseUrl/${SafeEatsEndpoint.PREFERENCE_LISTS_GRANDFATHERED.format()}"
            val body = json.encodeToString(ListSerializer(String.serializer()), legacyPrefs)
            val req = Request.Builder()
                .url(url)
                .post(okhttp3.RequestBody.create("application/json".toMediaTypeOrNull(), body))
                .addHeader("Authorization", "Bearer $token")
                .addHeader("apikey", anonKey)
                .build()
            client.newCall(req).execute().use { resp ->
                if (resp.code != 201) throw Exception("Grandfathered upload failed: ${resp.code}")
            }
        }
    }

    /** Fetch remote prefs and store locally */
    suspend fun fetchAndStore(): List<DietaryPreference> = withContext(Dispatchers.IO) {
        try {
            uploadGrandfatheredPreferences()
            val token = currentToken() ?: throw Exception("Not authenticated")
            val url = "$functionsBaseUrl/${SafeEatsEndpoint.PREFERENCE_LISTS_DEFAULT.format()}"
            Log.d("PreferenceRepo", "GET $url")
            val req = Request.Builder()
                .url(url)
                .get()
                .addHeader("Authorization", "Bearer $token")
                .addHeader("apikey", anonKey)
                .build()

            client.newCall(req).execute().use { resp ->
                val body = resp.body?.string().orEmpty()
                Log.d("PreferenceRepo", "Fetch code: ${resp.code}, body: ${body.take(200)}")
                when (resp.code) {
                    200 -> {
                        val list = if (body.isBlank()) emptyList() else json.decodeFromString(
                            ListSerializer(DietaryPreference.serializer()), body
                        )
                        saveLocal(list)
                        list
                    }
                    204 -> {
                        saveLocal(emptyList())
                        emptyList()
                    }
                    401 -> {
                        Log.e("PreferenceRepo", "Authentication failed - token may be expired")
                        // Use Supabase SDK to sign out and clear session
                        try {
                            supabaseClient.auth.signOut()
                        } catch (e: Exception) {
                            Log.e("PreferenceRepo", "Error signing out", e)
                        }
                        throw Exception("Authentication failed. Please log in again.")
                    }
                    else -> throw Exception("Something went wrong. Please try again later.")
                }
            }
        } catch (e: java.net.UnknownHostException) {
            Log.e("PreferenceRepo", "Network error: Unable to resolve host", e)
            throw Exception("Something went wrong. Please check your internet connection.")
        } catch (e: java.io.IOException) {
            Log.e("PreferenceRepo", "Network/IO error", e)
            throw Exception("Something went wrong. Please try again later.")
        } catch (e: Exception) {
            // Re-throw if it's already a user-friendly message
            if (e.message?.contains("Authentication failed") == true || 
                e.message?.contains("Something went wrong") == true) {
                throw e
            }
            Log.e("PreferenceRepo", "Unexpected error in fetchAndStore", e)
            throw Exception("Something went wrong. Please try again later.")
        }
    }

    /** Add or edit preference */suspend fun addOrEditPreference(
        clientActivityId: String,
        preferenceText: String,
        id: Int? = null
    ): PreferenceValidationResult = withContext(Dispatchers.IO) {
        val token = currentToken() ?: throw Exception("Not authenticated")
        val url = if (id != null) {
            "$functionsBaseUrl/${SafeEatsEndpoint.PREFERENCE_LISTS_DEFAULT_ITEMS.format(id.toString())}"
        } else {
            "$functionsBaseUrl/${SafeEatsEndpoint.PREFERENCE_LISTS_DEFAULT.format()}"
        }
        Log.d("PreferenceRepo", "Making request to: $url")

        // PostHog: track input start
        val requestId = UUID.randomUUID().toString()
        val startTime = System.currentTimeMillis()
        val endpoint = if (id != null) "preference_lists_default_items" else "preference_lists_default"
        val method = if (id != null) "PUT" else "POST"
        Analytics.trackUserPreferenceInput(
            requestId = requestId,
            endpoint = endpoint,
            clientActivityId = clientActivityId,
            preferenceText = preferenceText,
            method = method,
            itemId = id?.toString(),
            startTimeMs = startTime
        )

        val multipart = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("clientActivityId", clientActivityId)
            .addFormDataPart("preference", preferenceText)
            .build()

        val reqBuilder = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .addHeader("apikey", anonKey)

        val request = if (id != null) reqBuilder.put(multipart).build()
        else reqBuilder.post(multipart).build()

        Log.d("PreferenceRepo", "Request headers: Authorization=Bearer $token, apikey=$anonKey")
        Log.d("PreferenceRepo", "Request body: clientActivityId=$clientActivityId, preference=$preferenceText")

        try {
            client.newCall(request).execute().use { resp ->
                val code = resp.code
                val body = resp.body?.string().orEmpty()
                Log.d("PreferenceRepo", "Response code: $code, body: $body")

                val latencyMs = System.currentTimeMillis() - startTime

                if (code !in listOf(200, 201, 204, 422)) {
                    Analytics.trackPreferenceValidationBadResponse(
                        requestId = requestId,
                        clientActivityId = clientActivityId,
                        preferenceText = preferenceText,
                        statusCode = code,
                        latencyMs = latencyMs
                    )
                    throw Exception("Something went wrong. Please try again later.")
                }

                if (code == 204) {
                    Analytics.trackPreferenceValidationSuccess(
                        requestId = requestId,
                        clientActivityId = clientActivityId,
                        preferenceText = preferenceText,
                        latencyMs = latencyMs
                    )
                    return@withContext PreferenceValidationResult.Success(
                        DietaryPreference(
                            id = id ?: -1,
                            text = preferenceText,
                            annotatedText = ""
                        )
                    )
                }

                val elem = json.parseToJsonElement(body).jsonObject
                val resultStr = elem["result"]?.toString()?.trim('"')
                return@withContext when (resultStr) {
                    "success" -> {
                        Analytics.trackPreferenceValidationSuccess(
                            requestId = requestId,
                            clientActivityId = clientActivityId,
                            preferenceText = preferenceText,
                            latencyMs = latencyMs
                        )
                        val prefElem = elem["preference"] ?: elem
                        val pref = json.decodeFromJsonElement(DietaryPreference.serializer(), prefElem)
                        PreferenceValidationResult.Success(pref)
                    }
                    "failure" -> {
                        // Server signalled validation failure; often 422, but treat explicitly
                        Analytics.trackPreferenceValidationBadResponse(
                            requestId = requestId,
                            clientActivityId = clientActivityId,
                            preferenceText = preferenceText,
                            statusCode = code,
                            latencyMs = latencyMs
                        )
                        val explanation = elem["explanation"]?.toString()?.trim('"') ?: "Validation failed"
                        PreferenceValidationResult.Failure(explanation)
                    }
                    else -> {
                        Analytics.trackPreferenceValidationBadResponse(
                            requestId = requestId,
                            clientActivityId = clientActivityId,
                            preferenceText = preferenceText,
                            statusCode = code,
                            latencyMs = latencyMs
                        )
                        PreferenceValidationResult.Failure("Something went wrong. Please try again later.")
                    }
                }
            }
        } catch (e: java.net.UnknownHostException) {
            val latencyMs = System.currentTimeMillis() - startTime
            Analytics.trackPreferenceValidationError(
                requestId = requestId,
                clientActivityId = clientActivityId,
                preferenceText = preferenceText,
                latencyMs = latencyMs,
                error = "Network error: Unable to resolve host"
            )
            Log.e("PreferenceRepo", "Network error: Unable to resolve host", e)
            throw Exception("Something went wrong. Please check your internet connection.")
        } catch (e: java.io.IOException) {
            val latencyMs = System.currentTimeMillis() - startTime
            Analytics.trackPreferenceValidationError(
                requestId = requestId,
                clientActivityId = clientActivityId,
                preferenceText = preferenceText,
                latencyMs = latencyMs,
                error = "Network/IO error"
            )
            Log.e("PreferenceRepo", "Network/IO error", e)
            throw Exception("Something went wrong. Please try again later.")
        } catch (e: Exception) {
            val latencyMs = System.currentTimeMillis() - startTime
            Analytics.trackPreferenceValidationError(
                requestId = requestId,
                clientActivityId = clientActivityId,
                preferenceText = preferenceText,
                latencyMs = latencyMs,
                error = e.message ?: "Unknown error"
            )
            // Re-throw if it's already a user-friendly message
            if (e.message?.contains("Something went wrong") == true) {
                throw e
            }
            Log.e("PreferenceRepo", "Unexpected error in addOrEditPreference", e)
            throw Exception("Something went wrong. Please try again later.")
        }
    }


    /** Delete preference */
    suspend fun deletePreference(id: Int, clientActivityId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val token = currentToken() ?: throw Exception("Not authenticated")
            val url = "$functionsBaseUrl/${SafeEatsEndpoint.PREFERENCE_LISTS_DEFAULT_ITEMS.format(id.toString())}"
            val multipart = MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("clientActivityId", clientActivityId)
                .build()
            Log.d("PreferenceRepo", "DELETE $url")
            val req = Request.Builder()
                .url(url)
                .delete(multipart)
                .addHeader("Authorization", "Bearer $token")
                .addHeader("apikey", anonKey)
                .build()
            client.newCall(req).execute().use { resp ->
                val body = resp.body?.string().orEmpty()
                Log.d("PreferenceRepo", "Delete code: ${resp.code}, body: ${body.take(200)}")
                resp.code in listOf(200, 204)
            }
        } catch (e: java.net.UnknownHostException) {
            Log.e("PreferenceRepo", "Network error: Unable to resolve host", e)
            throw Exception("Something went wrong. Please check your internet connection.")
        } catch (e: java.io.IOException) {
            Log.e("PreferenceRepo", "Network/IO error", e)
            throw Exception("Something went wrong. Please try again later.")
        } catch (e: Exception) {
            // Re-throw if it's already a user-friendly message
            if (e.message?.contains("Not authenticated") == true || 
                e.message?.contains("Something went wrong") == true) {
                throw e
            }
            Log.e("PreferenceRepo", "Unexpected error in deletePreference", e)
            throw Exception("Something went wrong. Please try again later.")
        }
    }
    suspend fun clearAllLocalData() {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }
    /** Delete account and all remote data for the current user via Edge Function (requires service role on server). */
    suspend fun deleteAccountRemote(): Boolean = withContext(Dispatchers.IO) {
        try {
            val token = currentToken() ?: throw Exception("Not authenticated")
            val url = "$functionsBaseUrl/${SafeEatsEndpoint.DELETEME.format()}"
            Log.d("PreferenceRepo", "POST $url (delete account)")
            val req = Request.Builder()
                .url(url)
                .post(okhttp3.RequestBody.create("application/json".toMediaTypeOrNull(), "{}"))
                .addHeader("Authorization", "Bearer $token")
                .addHeader("apikey", anonKey)
                .build()
            client.newCall(req).execute().use { resp ->
                val body = resp.body?.string().orEmpty()
                Log.d("PreferenceRepo", "Delete account code: ${resp.code}, body: ${body.take(200)}")
                resp.code in listOf(200, 204)
            }
        } catch (e: java.net.UnknownHostException) {
            Log.e("PreferenceRepo", "Network error: Unable to resolve host", e)
            throw Exception("Something went wrong. Please check your internet connection.")
        } catch (e: java.io.IOException) {
            Log.e("PreferenceRepo", "Network/IO error", e)
            throw Exception("Something went wrong. Please try again later.")
        } catch (e: Exception) {
            // Re-throw if it's already a user-friendly message
            if (e.message?.contains("Not authenticated") == true || 
                e.message?.contains("Something went wrong") == true) {
                throw e
            }
            Log.e("PreferenceRepo", "Unexpected error in deleteAccountRemote", e)
            throw Exception("Something went wrong. Please try again later.")
        }
    }
    fun currentToken(): String? {
        return try {
            // Always use Supabase SDK for token management
            // SDK handles automatic refresh and session management
            val session = supabaseClient.auth.currentSessionOrNull()
            if (session != null) {
                Log.d("PreferenceRepo", "Using SDK token: ${session.accessToken.take(20)}...")
                Log.d("PreferenceRepo", "Token expires at: ${session.expiresAt}")
                Log.d("PreferenceRepo", "User email: ${session.user?.email}")
                session.accessToken
            } else {
                Log.d("PreferenceRepo", "No active session found")
                null
            }
        } catch (e: Throwable) {
            Log.e("PreferenceRepo", "Error getting token from SDK", e)
            null
        }
    }
}
