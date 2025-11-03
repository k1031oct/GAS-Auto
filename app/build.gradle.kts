import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.gws.auto.mobile.android"
    compileSdk = 36

    // --- Robust Signing Config ---
    val signingProperties = Properties()
    val keystorePropertiesFile = rootProject.file("keystore.properties")
    if (keystorePropertiesFile.exists()) {
        keystorePropertiesFile.inputStream().use { signingProperties.load(it) }
    }

    System.getenv("SIGNING_KEY_FILE")?.let { signingProperties.setProperty("storeFile", it) }
    System.getenv("KEY_STORE_PASSWORD")?.let { signingProperties.setProperty("storePassword", it) }
    System.getenv("KEY_ALIAS")?.let { signingProperties.setProperty("keyAlias", it) }
    System.getenv("KEY_PASSWORD")?.let { signingProperties.setProperty("keyPassword", it) }

    signingConfigs {
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
        viewBinding = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/INDEX.LIST"
        }
    }
}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:34.5.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")

    implementation("com.google.android.gms:play-services-auth:21.4.0")
    implementation("androidx.credentials:credentials:1.5.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.5.0")
    implementation("com.google.android.libraries.identity:googleid:1.1.0")
    implementation("com.google.api-client:google-api-client-android:2.8.1")
    implementation("com.google.http-client:google-http-client-gson:2.0.2")
    implementation("com.google.apis:google-api-services-drive:v3-rev20230822-2.0.0")
    implementation("com.google.apis:google-api-services-sheets:v4-rev20230815-2.0.0")
    implementation("com.google.oauth-client:google-oauth-client:1.39.0")
    implementation("com.google.http-client:google-http-client-android:2.0.2")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.10.2")

    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.4")
    implementation("androidx.recyclerview:recyclerview:1.4.0")
    implementation("androidx.work:work-runtime-ktx:2.11.0")

    // Android UI libraries
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.13.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")

    // Navigation Component
    implementation("androidx.navigation:navigation-fragment-ktx:2.9.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.9.5")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
}
