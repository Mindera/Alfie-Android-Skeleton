plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.mindera.alfie.core.ui"
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
        compose = true
    }
}

dependencies {
    implementation(project(":core:common"))
    
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.core.ktx)
    implementation(libs.bundles.compose)
    
    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.kotlin.test)
    
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.runner)
}
