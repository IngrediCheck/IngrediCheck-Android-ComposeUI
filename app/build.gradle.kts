dependencies {
    implementation(libs.accompanist.pager)
    implementation(libs.accompanist.pager.indicators)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // AppAuth for OAuth with Apple
    implementation("net.openid:appauth:0.11.1")

    // Ktor client for Supabase HTTP requests
    implementation("io.ktor:ktor-client-okhttp:2.3.4")
    implementation("io.ktor:ktor-client-serialization:2.3.4")
    implementation("io.ktor:ktor-client-logging:2.3.4")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.4")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.4")

    // Kotlinx Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

    // Google Sign-In
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("com.google.code.gson:gson:2.10.1")

    // Supabase bundle
    implementation(libs.bundles.supabase)

    // Auth bundle
    implementation(libs.bundles.auth)

    // Navigation
    implementation(libs.navigation.compose)

    // Coroutines
    implementation(libs.coroutines.android)

    // Supabase modules
    implementation(libs.supabase.gotrue)
    implementation("io.github.jan-tennert.supabase:postgrest-kt:1.1.1")
    implementation("io.github.jan-tennert.supabase:gotrue-kt:1.1.1")
    implementation("io.github.jan-tennert.supabase:storage-kt:1.1.1")
}
