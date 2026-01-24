plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.mindera.alfie.core.navigation"
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
}

dependencies {
    implementation(project(":core:common"))
    
    // Only AndroidX annotations for @DrawableRes
    implementation(libs.androidx.core.ktx)
    
    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.kotlin.test)
    
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.runner)
}
