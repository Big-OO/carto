import java.util.Properties
import java.io.FileInputStream
import org.gradle.authentication.http.BasicAuthentication

pluginManagement {
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://jitpack.io")
        }
        maven {
            url = rootDir.toURI().resolve("app/libs")
        }
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    val localProperties = Properties()
    val localPropertiesFile = file("local.properties")

    if (localPropertiesFile.exists()) {
        localProperties.load(FileInputStream(localPropertiesFile))
    }

    val mapboxDownloadsToken =
        providers.gradleProperty("MAPBOX_DOWNLOADS_TOKEN").orNull
            ?: providers.gradleProperty("mapbox.downloads.token").orNull
            ?: localProperties.getProperty("MAPBOX_DOWNLOADS_TOKEN")
            ?: localProperties.getProperty("mapbox.downloads.token")
            ?: ""

    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven {
            url = rootDir.toURI().resolve("app/libs")
        }

        maven {
            url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
            authentication {
                create<BasicAuthentication>("basic")
            }
            credentials {
                username = "mapbox"
                password = mapboxDownloadsToken
            }
        }
    }
}

rootProject.name = "Carto"
include(":app")
include(":core")
