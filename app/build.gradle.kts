import java.util.Properties
import com.google.firebase.appdistribution.gradle.firebaseAppDistribution

// --- Start of Version Auto-Increment Logic ---
val versionPropsFile = file("version.properties")

if (!versionPropsFile.canRead()) {
    throw GradleException("Could not read version.properties! Create one with: \nVERSION_CODE=1\nVERSION_MAJOR=1\nVERSION_MINOR=0\nVERSION_PATCH=0")
}

val versionProps = Properties()
versionProps.load(versionPropsFile.reader())

var versionCode = versionProps["VERSION_CODE"].toString().toInt()
var versionPatch = versionProps["VERSION_PATCH"].toString().toInt()

// Increment for the current build
versionCode++
versionPatch++

// Update properties file for the next build
versionProps["VERSION_CODE"] = versionCode.toString()
versionProps["VERSION_PATCH"] = versionPatch.toString()
versionProps.store(versionPropsFile.writer(), "Auto-updated by Gradle build")
// --- End of Version Auto-Increment Logic ---

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.firebase.appdistribution)
}

android {
    namespace = "com.gws.auto.mobile.android"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.gws.auto.mobile.android"
        minSdk = 24
        targetSdk = 36
        
        // Set the updated versions from the logic above
        this.versionCode = versionCode
        this.versionName = "${versionProps["VERSION_MAJOR"]}.${versionProps["VERSION_MINOR"]}.$versionPatch"
        
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        debug {
            firebaseAppDistribution {
                // The 'token' property was removed in a recent version of the App Distribution plugin.
                // The plugin now automatically uses the FIREBASE_TOKEN environment variable if it's set.
                appId = System.getenv("FIREBASE_APP_ID")
                groups = "testers"
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }

    buildFeatures {
        compose = true
        viewBinding = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/DEPENDENCIES"
        }
    }
}

dependencies {
    // Firebase - Now using version catalog
    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)

    // Google & AndroidX - All using version catalog
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.google.api.client.android)
    implementation(libs.google.http.client.gson)
    implementation(libs.google.api.services.drive)
    implementation(libs.google.api.services.sheets)
    implementation(libs.google.oauth.client)
    implementation(libs.kotlinx.coroutines.play.services)
    implementation(libs.androidx.splashscreen)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.bundles.navigation)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    debugImplementation(libs.androidx.ui.tooling)

    // Third Party
    implementation(libs.bundles.vico)
    implementation(libs.coil.compose)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.ext.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
}
