// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.firebase.appdistribution) apply false
    alias(libs.plugins.hilt) apply false
}

subprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }
}

allprojects {
    configurations.all {
        resolutionStrategy {
            force("io.grpc:grpc-core:1.59.1")
            force("io.grpc:grpc-android:1.59.1")
            force("io.grpc:grpc-okhttp:1.59.1")
            force("io.grpc:grpc-protobuf-lite:1.59.1")
            force("io.grpc:grpc-stub:1.59.1")
        }
    }
}
