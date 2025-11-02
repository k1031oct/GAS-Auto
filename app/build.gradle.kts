import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.gws.auto.mobile.android"
    compileSdk = 36

    // --- Robust Signing Config ---
    // This logic ensures that signing configs are only set up when the necessary
    // information is present. This prevents errors during debug builds in a clean CI environment.

    // 1. Prepare a properties object to hold signing information.
    val signingProperties = Properties()
    val keystorePropertiesFile = rootProject.file("keystore.properties")
    if (keystorePropertiesFile.exists()) {
        keystorePropertiesFile.inputStream().use { signingProperties.load(it) }
    }

    // 2. In a CI environment, override with environment variables if they exist.
    // These are provided by the release.yml workflow, matching the secrets in MANUAL_SETUP.md.
    System.getenv("SIGNING_KEY_FILE")?.let { signingProperties.setProperty("storeFile", it) }
    System.getenv("KEY_STORE_PASSWORD")?.let { signingProperties.setProperty("storePassword", it) }
    System.getenv("KEY_ALIAS")?.let { signingProperties.setProperty("keyAlias", it) }
    System.getenv("KEY_PASSWORD")?.let { signingProperties.setProperty("keyPassword", it) }

    signingConfigs {
        // 3. Only create the 'release' signing config if all necessary properties are available.
        if (signingProperties.getProperty("storeFile") != null &&
            signingProperties.getProperty("storePassword") != null &&
            signingProperties.getProperty("keyAlias") != null &&
            signingProperties.getProperty("keyPassword") != null) {

            create("release") {
                storeFile = file(signingProperties.getProperty("storeFile"))
                storePassword = signingProperties.getProperty("storePassword")
                keyAlias = signingProperties.getProperty("keyAlias")
                keyPassword = signingProperties.getProperty("keyPassword")
            }
        }
    }

    defaultConfig {
        applicationId = "com.gws.auto.mobile.android"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // 4. Assign the signing config only if it was successfully created.
            signingConfig = signingConfigs.findByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_1_8)
        }
    }

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    // implementation(platform("com.google.firebase:firebase-bom:34.5.0"))
    implementation("com.google.firebase:firebase-analytics-ktx:22.5.0")
    implementation("com.google.firebase:firebase-crashlytics-ktx:19.4.4")

    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.4")
    implementation("androidx.activity:activity-compose:1.11.0")
    implementation(platform("androidx.compose:compose-bom:2025.10.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
    androidTestImplementation(platform("androidx.compose:compose-bom:2025.10.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
