plugins {
    alias(libs.plugins.kotlin.jvm)
}

java {
    val javaVersionValue = JavaVersion.toVersion(findProperty("javaVersion").toString().toInt())
    sourceCompatibility = javaVersionValue
    targetCompatibility = javaVersionValue
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.fromTarget(findProperty("javaVersion").toString()))
    }
}

dependencies {
    // Coroutines
    implementation(libs.bundles.coroutines)
    
    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotlinx.coroutines.test)
}
