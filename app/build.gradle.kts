plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "lc.fungee.IngrediCheck"
    compileSdk = 35

    defaultConfig {
        applicationId = "llc.fungee.IngrediCheck"
        minSdk = 31
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        manifestPlaceholders["appAuthRedirectScheme"] = "io.supabase.ingredicheck"
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
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.firebase.crashlytics.buildtools)
    implementation(libs.androidx.material3.window.size.class1.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    //If you're looking for the latest stable version, it's 1.3.2.


    // OAuth for Apple Sign-In
    implementation("net.openid:appauth:0.11.1")

    // Ktor client for HTTP + Serialization
    implementation("io.ktor:ktor-client-okhttp:2.3.4")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.4")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.4")
    implementation("io.ktor:ktor-client-logging:2.3.4")

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

    // Supabase Kotlin SDK - aligned versions
    implementation("io.github.jan-tennert.supabase:supabase-kt:3.2.2")
    implementation("io.github.jan-tennert.supabase:auth-kt:3.2.2")
    implementation("io.github.jan-tennert.supabase:postgrest-kt:3.2.2")

    // AndroidX ViewModel + Activity KTX
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.activity:activity-ktx:1.8.0")
    implementation("io.ktor:ktor-client-core:3.2.2")
    implementation("io.ktor:ktor-client-okhttp:3.2.2")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")
    //cameraa
    implementation(libs.camerax.core)
    implementation(libs.camerax.camera2)
    implementation(libs.camerax.lifecycle)
    implementation(libs.camerax.view)
    implementation("io.coil-kt:coil-compose:2.6.0")
    implementation("com.google.mlkit:text-recognition:16.0.0")
    implementation(libs.mlkit.barcode)


}
