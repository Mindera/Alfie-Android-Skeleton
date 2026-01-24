plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.paparazzi)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.mindera.alfie.skeleton"
    compileSdk = findProperty("compileSdkVersion").toString().toInt()

    defaultConfig {
        applicationId = "com.mindera.alfie.skeleton"
        minSdk = findProperty("minSdkVersion").toString().toInt()
        targetSdk = findProperty("targetSdkVersion").toString().toInt()
        // Version code/name use BUILD_NUMBER from CI/CD, fallback to 1 and "1.0.0" for local builds
        versionCode = System.getenv("BUILD_NUMBER")?.toIntOrNull() ?: 1
        versionName = "1.0.${System.getenv("BUILD_NUMBER") ?: "0"}"

        testInstrumentationRunner = "com.mindera.alfie.skeleton.HiltTestRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            storeFile = System.getenv("KEYSTORE_FILE")?.let { file(it) }
            storePassword = System.getenv("KEYSTORE_PASSWORD")
            keyAlias = System.getenv("KEY_ALIAS")
            keyPassword = System.getenv("KEY_PASSWORD")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.findByName("release")
        }
    }

    compileOptions {
        val javaVersionValue = JavaVersion.toVersion(findProperty("javaVersion").toString().toInt())
        sourceCompatibility = javaVersionValue
        targetCompatibility = javaVersionValue
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.fromTarget(findProperty("javaVersion").toString()))

            freeCompilerArgs.addAll(
                listOf("-XXLanguage:+PropertyParamAnnotationDefaultTargetMode")
            )
        }
    }

    buildFeatures {
        compose = true
        // Add this line to enable BuildConfig generation
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

    dependencies {
        // Module dependencies
        implementation(project(":core:common"))
        implementation(project(":core:ui"))
        implementation(project(":core:navigation"))
        implementation(project(":domain"))
        implementation(project(":data"))
        implementation(project(":networking"))
        implementation(project(":feature:splash"))
        implementation(project(":feature:landing"))
        implementation(project(":feature:details"))
        
        implementation(platform(libs.androidx.compose.bom))
        implementation(libs.androidx.core.ktx)
        implementation(libs.bundles.lifecycle)
        implementation(libs.androidx.activity.compose)

        // Compose
        implementation(libs.bundles.compose)

        // Navigation
        implementation(libs.androidx.navigation.compose)
        implementation(libs.kotlinx.serialization.json)

        // Hilt
        implementation(libs.bundles.hilt)
        ksp(libs.hilt.android.compiler)

        debugImplementation(libs.bundles.compose.debug)

        // Testing
        kspAndroidTest(libs.hilt.android.compiler)
        testImplementation(libs.junit)
        testImplementation(libs.kotlin.test)
        testImplementation(libs.kotlinx.coroutines.test)
        testImplementation(libs.turbine)
        testImplementation(libs.mockk)

        // Android Testing
        androidTestImplementation(platform(libs.androidx.compose.bom))
        androidTestImplementation(libs.androidx.test.ext.junit)
        androidTestImplementation(libs.androidx.test.runner)
        androidTestImplementation(libs.androidx.espresso.core)
        androidTestImplementation(libs.androidx.test.runner)
        androidTestImplementation(libs.androidx.compose.ui.test.junit4)
        androidTestImplementation(libs.androidx.navigation.testing)
        androidTestImplementation(libs.hilt.android.testing)
        androidTestImplementation(libs.mockk.android)
    }
