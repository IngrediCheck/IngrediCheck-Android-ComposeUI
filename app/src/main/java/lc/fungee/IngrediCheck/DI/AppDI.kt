package lc.fungee.IngrediCheck.DI

import androidx.compose.runtime.staticCompositionLocalOf
import lc.fungee.IngrediCheck.data.repository.PreferenceRepository

// Holds base URL + anonKey
data class ApiConfig(
    val functionsBaseUrl: String,
    val anonKey: String
)

// Make PreferenceRepository available everywhere
val LocalPreferenceRepository =
    staticCompositionLocalOf<PreferenceRepository> { error("PreferenceRepository not provided") }

// Make ApiConfig available everywhere
val LocalApiConfig =
    staticCompositionLocalOf<ApiConfig> { error("ApiConfig not provided") }