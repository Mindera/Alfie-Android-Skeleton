plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.mindera.alfie.networking"
    compileSdk = findProperty("compileSdkVersion").toString().toInt()

    defaultConfig {
        minSdk = findProperty("minSdkVersion").toString().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        val javaVersionValue = JavaVersion.toVersion(findProperty("javaVersion").toString().toInt())
        sourceCompatibility = javaVersionValue
        targetCompatibility = javaVersionValue
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.fromTarget(findProperty("javaVersion").toString()))
        }
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(project(":core:common"))
    
    // Hilt
    implementation(libs.bundles.hilt)
    ksp(libs.hilt.android.compiler)
    
    // Networking
    implementation(libs.bundles.networking)
    ksp(libs.moshi.codegen)
    
    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.kotlin.test)
    
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.runner)
}
