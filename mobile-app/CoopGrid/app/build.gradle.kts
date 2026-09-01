plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.example.coopgrid"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.coopgrid"
        minSdk = 28
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}


dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    implementation("com.google.code.gson:gson:2.10.1")
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.compose)
    // Standard WorkManager (KTX version for Coroutines)
    implementation(libs.androidx.work.runtime.ktx)
    // Hilt Integration for Worker
    implementation(libs.androidx.hilt.work)
    // Kapt for Hilt Compiler (Make sure 'kotlin-kapt' plugin is applied)
    ksp(libs.androidx.hilt.compiler)
    // 1. Extended Icons
    implementation(libs.androidx.compose.material.icons.extended)

    // 2. DataStore Preferences
    implementation(libs.androidx.datastore.preferences)

    // 3. Accompanist Permissions
    implementation(libs.accompanist.permissions)

    // 1. Room Runtime
    implementation(libs.androidx.room.runtime)

    // 2. Room KTX (suspend functions use karne ke liye zaroori hai)
    implementation(libs.androidx.room.ktx)

    // 3. Room Compiler (KSP use kar rahe hain toh)
    ksp(libs.androidx.room.compiler)

    // 4. Guava (Sirf tabhi jab aapne service/repo mein use kiya ho)
    implementation(libs.androidx.room.guava)
    // Hilt ka use suru ho raha hai yaha se
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // 1. Retrofit Core
    implementation(libs.retrofit.core)

    // 2. Retrofit Converter for Kotlin Serialization
    implementation(libs.retrofit.kotlin.serialization)

    // 3. OkHttp Logging Interceptor (Debugging ke liye)
    implementation(libs.okhttp.logging)

    // 4. Kotlinx Serialization JSON (Parsing ke liye)
    implementation(libs.kotlinx.serialization.json)

    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}