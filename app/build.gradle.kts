import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.google.services)
}

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) {
        file.inputStream().use(::load)
    }
}

fun localProperty(key: String, defaultValue: String = ""): String {
    return localProperties.getProperty(key) ?: defaultValue
}

fun String.asBuildConfigString(): String = "\"${replace("\\", "\\\\").replace("\"", "\\\"")}\""

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
    arg("room.incremental", "true")
    arg("room.expandProjection", "true")
}

hilt {
    enableAggregatingTask = true
}

android {
    namespace = "com.example.carto"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.example.carto"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(
            "String",
            "SHOPIFY_HOSTNAME",
            localProperty("shopify.hostname", "mad46-and7.myshopify.com").asBuildConfigString()
        )
        buildConfigField(
            "String",
            "SHOPIFY_API_VERSION",
            localProperty("shopify.api.version", "2026-01").asBuildConfigString()
        )
        buildConfigField(
            "String",
            "SHOPIFY_ADMIN_ACCESS_TOKEN",
            localProperty("shopify.admin.access.token").asBuildConfigString()
        )
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
        }

        release {
            isMinifyEnabled = false

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    // =========================================================================
    // Core
    // =========================================================================
    implementation(libs.androidx.core.ktx)

    // =========================================================================
    // Activity & Lifecycle
    // =========================================================================
    implementation(libs.androidx.activity.compose)

    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // =========================================================================
    // Compose
    // =========================================================================
    implementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(platform(libs.androidx.compose.bom))

    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    implementation(libs.material.icons.extended)

    // Material3
    implementation(libs.material3)

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    // =========================================================================
    // Navigation
    // =========================================================================
    implementation(libs.androidx.navigation.compose)

    // =========================================================================
    // Coroutines & Serialization
    // =========================================================================
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.play.services)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.datetime)

    // =========================================================================
    // Networking
    // =========================================================================
    implementation(platform(libs.okhttp.bom))

    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.retrofit.converter.scalars)

    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)

    // =========================================================================
    // Database
    // =========================================================================
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)

    ksp(libs.room.compiler)

    // =========================================================================
    // Firebase
    // =========================================================================
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)

    // =========================================================================
    // Dependency Injection
    // =========================================================================
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)

    ksp(libs.hilt.compiler)

    // =========================================================================
    // Image Loading
    // =========================================================================
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    // =========================================================================
    // Utilities
    // =========================================================================
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.work.runtime.ktx)

    // Optional (remove if not used)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)

    // =========================================================================
    // Unit Testing
    // =========================================================================
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.androidx.arch.core.testing)
    testImplementation(libs.room.testing)
    testImplementation(libs.mockwebserver3)
    testImplementation(libs.hilt.android.testing)

    kspTest(libs.hilt.compiler)

    // =========================================================================
    // Instrumentation Testing
    // =========================================================================
    androidTestImplementation(libs.androidx.test.core.ktx)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.junit.ktx)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.rules)

    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    androidTestImplementation(libs.room.testing)
    androidTestImplementation(libs.mockk.android)
    androidTestImplementation(libs.mockwebserver3)
    androidTestImplementation(libs.turbine)
    androidTestImplementation(libs.hilt.android.testing)

    kspAndroidTest(libs.hilt.compiler)
}