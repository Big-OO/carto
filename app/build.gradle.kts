import java.util.Properties

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

val packageName: String = "com.shopify.carto"


plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.google.services)
    alias(libs.plugins.apollo)
    alias(libs.plugins.kotlin.serialization)
}

apollo {
    service("shopify") {
        packageName.set("com.shopify.carto.core.graphql.shopify")
        srcDir("src/main/graphql/shopify")
        schemaFile.set(file("src/main/graphql/shopify/schema.json"))
        packageNamesFromFilePaths()
    }

    service("shopifyAdmin") {
        packageName.set("com.shopify.carto.core.graphql.admin")
        srcDir("src/main/graphql/admin")
        schemaFile.set(file("src/main/graphql/admin/schema.json"))

        introspection {
            endpointUrl.set("https://mad46-and7.myshopify.com/admin/api/2026-01/graphql.json")
            schemaFile.set(file("src/main/graphql/admin/schema.json"))
            headers.put(
                "X-Shopify-Access-Token",
                localProperty("shopify.admin.access.token")
            )
        }

        packageNamesFromFilePaths()
    }
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
    arg("room.incremental", "true")
    arg("room.expandProjection", "true")
    arg("appfunctions:aggregateAppFunctions", "true")
}

hilt {
    enableAggregatingTask = true
}

android {
    namespace = packageName
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = packageName
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
        buildConfigField(
            "String",
            "SHOPIFY_STOREFRONT_ACCESS_TOKEN",
            localProperty("storefront.access.token").asBuildConfigString()
        )
        buildConfigField(
            "String",
            "MAPBOX_ACCESS_TOKEN",
            localProperty("mapbox.access.token").asBuildConfigString()
        )
        buildConfigField(
            "String",
            "MAPBOX_DOWNLOADS_TOKEN",
            localProperty("mapbox.downloads.token").asBuildConfigString()
        )
        buildConfigField(
            "String",
            "PAYMOB_PUBLIC_KEY",
            localProperty("paymob.public.key").asBuildConfigString()
        )
        buildConfigField(
            "String",
            "PAYMOB_INTEGRATION_ID",
            localProperty("paymob.integration.id").asBuildConfigString()
        )
        buildConfigField(
            "String",
            "PAYMOB_WALLET_INTEGRATION_ID",
            localProperty("paymob.wallet.integration.id").asBuildConfigString()
        )
        buildConfigField(
            "String",
            "SUPABASE_BASE_URL",
            localProperty("supabase.base.url").asBuildConfigString()
        )
        buildConfigField(
            "String",
            "PAYMOB_FLASH_BASE_URL",
            localProperty("paymob.flash.base.url").asBuildConfigString()
        )
        buildConfigField(
            "String",
            "AI_API_BASE_URL",
            localProperty("ai.api.base.url").asBuildConfigString()
        )
        buildConfigField(
            "String",
            "AI_API_KEY",
            localProperty("ai.api.key").asBuildConfigString()
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

    buildFeatures {
        compose = true
        buildConfig = true
        dataBinding = true
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }

    packaging {
        resources {
            excludes += setOf(
                "/META-INF/{AL2.0,LGPL2.1}",
                "META-INF/LICENSE.md",
                "META-INF/LICENSE-notice.md"
            )
        }
    }

    lint {
        abortOnError = true
        warningsAsErrors = false
        baseline = file("lint-baseline.xml")
    }
}

ksp {
    arg("appfunctions:aggregateAppFunctions", "true")
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    implementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.material3)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.datastore.preferences)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.play.services)

    implementation(platform(libs.okhttp.bom))
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.retrofit.converter.scalars)
    implementation(libs.apollo.runtime)

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.play.services.auth)

    implementation(libs.hilt.android)
    implementation(libs.hilt.lifecycle.viewmodel.compose)
    ksp(libs.hilt.compiler)
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.hilt.compiler)

    // WorkManager
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.hilt.work)
    ksp(libs.androidx.hilt.compiler)

    implementation(libs.paymob.sdk)

    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.androidx.arch.core.testing)
    testImplementation(libs.room.testing)
    testImplementation(libs.hilt.android.testing)
    kspTest(libs.hilt.compiler)

    androidTestImplementation(libs.androidx.test.core.ktx)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.junit.ktx)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.rules)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.room.testing)
    androidTestImplementation(libs.mockk.android)
    androidTestImplementation(libs.turbine)
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.hilt.compiler)

    implementation(libs.paymob.sdk)

    implementation(libs.androidx.appfunctions)
    implementation(libs.androidx.appfunctions.service)
    ksp(libs.androidx.appfunctions.compiler)

    implementation(libs.mapbox.android.maps)
    implementation(libs.mapbox.maps.compose)
    implementation(libs.mapbox.search.android)
    implementation(libs.play.services.location)
}
