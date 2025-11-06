
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "lc.fungee.IngrediCheck"
    compileSdk = 36

    defaultConfig {
        applicationId = "llc.fungee.IngrediCheck"
        minSdk = 31
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

// Enforce a browser version compatible with current AGP (8.8.1)
configurations.all {
    resolutionStrategy {
        force("androidx.browser:browser:1.8.0")
    }
}

dependencies {
    implementation(libs.accompanist.pager)
    implementation(libs.accompanist.pager.indicators)
    implementation(libs.navigation.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
//    implementation("androidx.compose.ui:ui:1.7.5")
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.firebase.crashlytics.buildtools)
    implementation(libs.androidx.material3.window.size.class1.android)
    implementation(libs.androidx.animation.core.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    //If you're looking for the latest stable version, it's 1.3.2.


    // Using Custom Tabs for Apple Sign-In (no AppAuth dependency)
    implementation("androidx.compose.ui:ui:1.8.0")
    implementation("androidx.browser:browser:1.8.0")

    // Android 12+ SplashScreen compat (provides postSplashScreenTheme attribute)
    implementation("androidx.core:core-splashscreen:1.0.1")

    // Ktor engine used by supabase-kt internals
    implementation("io.ktor:ktor-client-okhttp:3.1.1")

    // Kotlinx Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")

    // Google Sign-In
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    // JSON parsing (optional)
    implementation("com.google.code.gson:gson:2.10.1")

    // DataStore Preferences
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // Image Loading
    implementation("io.coil-kt:coil-compose:2.4.0")
    implementation("io.coil-kt:coil-svg:2.4.0")

    // HTTP Client (OkHttp)
    implementation("com.squareup.okhttp3:okhttp:4.11.0")

    // Supabase Kotlin SDK - aligned versions (3.2.4 includes signInWithIdToken)
    implementation("io.github.jan-tennert.supabase:supabase-kt:3.2.4")
    implementation("io.github.jan-tennert.supabase:auth-kt:3.2.4")
    implementation("io.github.jan-tennert.supabase:postgrest-kt:3.2.4")
    implementation("io.github.jan-tennert.supabase:storage-kt:3.2.4")

    // AndroidX ViewModel + Activity KTX
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.activity:activity-ktx:1.8.0")
    // Keep Ktor aligned on 2.x to avoid conflicts with other libs

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.1")
    //cameraa
    implementation(libs.camerax.core)
    implementation(libs.camerax.camera2)
    implementation(libs.camerax.lifecycle)
    implementation(libs.camerax.view)
    implementation("io.coil-kt:coil-compose:2.6.0")
    implementation("com.google.mlkit:text-recognition:16.0.0")
    implementation(libs.mlkit.barcode)
    implementation(libs.mlkit.text.recognition)
    implementation("androidx.exifinterface:exifinterface:1.3.7")

    // PostHog Analytics (correct artifact coordinates)
    implementation(libs.posthog.android)

}
