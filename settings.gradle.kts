pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Alfie"
include(":app")

// Core modules
include(":core:common")
include(":core:ui")
include(":core:navigation")

// Domain layer
include(":domain")

// Data layer
include(":data")
include(":networking")

// Feature modules
include(":feature:splash")
include(":feature:landing")
include(":feature:details")
