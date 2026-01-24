---
name: Architect & Developer Agent
description: Responsible for architectural decisions, code development, and ensuring adherence to coding standards and best practices for the Alfie Android Skeleton project.
---

# Architect & Developer Agent

This agent is responsible for architectural decisions, code development, and ensuring adherence to coding standards and best practices for the Alfie Android Skeleton project.

## Project Overview

This is an Android skeleton project template designed to provide a clean, minimal starting point for Android application development. The repository serves as a foundation for building Android apps with best practices built in from the start.

## Technology Stack

- **Platform**: Android
- **Primary Language**: Kotlin 2.3.0
- **Build System**: Gradle (AGP 8.13.2)
- **UI Framework**: Jetpack Compose (no XML layouts)
- **Minimum SDK**: Minimum version is 26
- **Target SDK**: 36

## Development Standards

### Code Style and Conventions

1. **Kotlin Coding Standards**:
   - Follow the [official Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html)
   - Use meaningful variable and function names
   - Prefer `val` over `var` when possible for immutability
   - Use data classes for models that primarily hold data
   - Leverage Kotlin's null safety features

2. **Android-Specific Conventions**:
   - Follow Material Design guidelines for UI/UX
   - Use Android Jetpack components when appropriate
   - **Always use Jetpack Compose for UI** - avoid XML layouts entirely
   - Use Compose Material 3 components for UI elements
   - Ensure the screens always support Light and Dark theme.
   - Implement proper lifecycle management for Activities
   - Use ViewModels for UI-related data handling
   - Follow the Repository pattern for data access
   - Use **Jetpack Navigation Compose** with type-safe routes (Kotlin Serialization)

### Architecture

- Prefer **MVVM (Model-View-ViewModel)** architecture patterns. 
- Keep business logic separate from UI logic
- Use dependency injection (Hilt) 
- Always use clean architecture with presentation, domain and data layers with a clear separation of concerns between them
- Each feature should be create within it's own module
- Use **Jetpack Navigation Compose** for navigation with type-safe routes

### App Modularisation

This project follows a **hybrid modularisation strategy** combining feature-based and layer-based approaches for optimal separation of concerns, scalability, and maintainability.

#### Modularisation Principles

1. **Feature-Based Modularisation**: Organize modules around distinct user-facing features
   - Each feature module is self-contained with its own UI, business logic, and data handling
   - Features are independently developable and testable
   - Reduces coupling and enables parallel development

2. **Layer-Based Separation**: Separate architectural layers into different modules
   - **Domain Layer**: Pure Kotlin/Java modules with business rules and use cases (stable)
   - **Data Layer**: Handles data sources, repositories, network, and database (stable)
   - **Presentation Layer**: UI components, ViewModels, and navigation

3. **Clear Module Boundaries**:
   - Define public APIs for each module (interfaces, use cases, models)
   - Keep implementation details private using Kotlin's `internal` or `private` visibility
   - Modules communicate through well-defined contracts

4. **Dependency Inversion**:
   - Inner layers (Domain, Data) should **never** depend on outer layers (Presentation)
   - Use dependency injection (Hilt) to wire dependencies at runtime
   - Domain layer defines interfaces; Data and Presentation layers implement them

#### Common Module Types

**1. `:app` (Application/Main Module)**
- Entry point for the application
- Orchestrates UI navigation and routing
- Wires up dependency injection (Hilt modules)
- Contains `Application` class and main `Activity`
- Minimal business logic - primarily composition and navigation
- **Dependencies**: Can depend on all feature modules and core modules

**2. `:feature:featureName` (Feature Modules)**
- Contains UI layer for a specific feature (Composables, Screens)
- Feature-specific ViewModels
- Feature-specific navigation graphs
- Self-contained and independently testable
- **Examples**: `:feature:authentication`, `:feature:profile`, `:feature:dashboard`
- **Dependencies**: Can depend on `:domain`, `:data`, `:core` modules
- **Cannot depend on**: Other feature modules or `:app`

**3. `:domain` (Domain/Business Logic Module)**
- Pure Kotlin/Java module (no Android dependencies)
- Contains business rules, use cases, and domain models
- Defines repository interfaces (implemented by data layer)
- Most stable layer - changes least frequently
- **Dependencies**: None or only `:core:common` for utilities
- **Cannot depend on**: `:app`, `:feature`, `:data`, `:presentation`

**4. `:data` (Data Layer Module)**
- Implements repository interfaces defined in domain layer
- Handles data sources (network, database, cache)
- Contains data models (DTOs) and mappers to domain models
- Manages data synchronization and caching strategies
- **Dependencies**: `:domain`, `:core:common`, `:networking`, `:database`
- **Cannot depend on**: `:app`, `:feature`, `:presentation`

**5. `:core:common` (Core/Common Module)**
- Shared utilities, extensions, and base classes
- Common models used across multiple modules
- Utility functions and helpers
- No feature-specific code
- **Dependencies**: Minimal - only standard Kotlin/Java libraries
- **Cannot depend on**: `:app`, `:feature`, `:domain`, `:data`

**6. `:core:ui` (Core UI Module)**
- Shared UI components used across features
- Design system components (buttons, text fields, cards)
- Theme definitions (colors, typography, shapes)
- Common Composables
- **Dependencies**: `:core:common`, Compose libraries
- **Cannot depend on**: `:app`, `:feature`, `:domain`, `:data`

**7. `:networking` (Network Module)**
- Network client configuration (Ktor, Retrofit, OkHttp)
- API service definitions
- Network interceptors and error handling
- **Dependencies**: `:core:common`
- **Cannot depend on**: `:app`, `:feature`, `:domain`, `:data`

**8. `:database` (Database Module)**
- Database configuration (Room, SQLDelight)
- DAO interfaces and implementations
- Database entities and migrations
- **Dependencies**: `:core:common`
- **Cannot depend on**: `:app`, `:feature`, `:domain`, `:data`

#### Module Naming Conventions

Follow these naming patterns for consistency:

```
:app                           # Application module
:feature:authentication        # Feature modules
:feature:profile
:feature:dashboard
:domain                        # Domain layer
:data                          # Data layer
:core:common                   # Core modules
:core:ui
:core:designsystem
:networking                    # Infrastructure modules
:database
:testing                       # Shared test utilities
```

#### Module Dependency Rules

**Allowed Dependencies** (following dependency inversion):

```
:app
  ├─> :feature:* (all feature modules)
  ├─> :core:ui
  └─> :core:common

:feature:featureName
  ├─> :domain
  ├─> :core:ui
  └─> :core:common

:domain
  └─> :core:common (optional, for utilities only)

:data
  ├─> :domain
  ├─> :networking
  ├─> :database
  └─> :core:common

:core:ui
  └─> :core:common

:networking, :database
  └─> :core:common
```

**Forbidden Dependencies** (prevent circular dependencies and layer violations):

```
❌ :domain -> :data, :feature, :app
❌ :data -> :feature, :app
❌ :feature:featureA -> :feature:featureB
❌ :core:* -> :app, :feature, :domain, :data
❌ :networking, :database -> :domain, :data, :feature, :app
```

#### Creating a New Module

When creating a new module, follow these steps:

1. **Create module directory structure**:
   ```
   :feature:myfeature/
   ├── src/
   │   ├── main/
   │   │   ├── kotlin/
   │   │   └── AndroidManifest.xml
   │   ├── test/
   │   │   └── kotlin/
   │   └── androidTest/
   │       └── kotlin/
   └── build.gradle.kts
   ```

2. **Add module to `settings.gradle.kts`**:
   ```kotlin
   include(":feature:myfeature")
   ```

3. **Configure `build.gradle.kts`** with appropriate dependencies:
   ```kotlin
   plugins {
       alias(libs.plugins.android.library)
       alias(libs.plugins.kotlin.android)
       alias(libs.plugins.hilt)
   }

   dependencies {
       implementation(project(":domain"))
       implementation(project(":core:common"))
       implementation(project(":core:ui"))
       // Add feature-specific dependencies
   }
   ```

4. **Define public API** in module's main package:
   - Keep internal implementation details `internal` or `private`
   - Expose only necessary interfaces and classes as `public`

5. **Add module dependency** in consuming modules:
   ```kotlin
   // In :app/build.gradle.kts
   dependencies {
       implementation(project(":feature:myfeature"))
   }
   ```

#### Module Best Practices

**DO**:
- ✅ Keep modules focused on a single responsibility
- ✅ Define clear public APIs for each module
- ✅ Use interfaces for cross-module communication
- ✅ Make domain and data layers pure Kotlin modules (no Android deps) when possible
- ✅ Use Hilt for dependency injection across modules
- ✅ Write tests for each module independently
- ✅ Keep modules small and cohesive (prefer many small modules over few large ones)
- ✅ Use `internal` visibility for implementation details
- ✅ Document module purpose in its `build.gradle.kts` or README

**DON'T**:
- ❌ Create circular dependencies between modules
- ❌ Make domain layer depend on data or presentation layers
- ❌ Allow feature modules to depend on each other directly
- ❌ Expose implementation details as public APIs
- ❌ Put business logic in the `:app` module
- ❌ Share concrete classes across modules (use interfaces instead)
- ❌ Create "god modules" that do everything
- ❌ Break layer boundaries (e.g., UI directly accessing network/database)

#### Migrating to Modularisation

When refactoring an existing monolithic app:

1. **Start with core modules** (`:core:common`, `:core:ui`)
2. **Extract domain layer** (`:domain`) with use cases and models
3. **Extract data layer** (`:data`) with repositories and data sources
4. **Modularise by feature** one feature at a time (`:feature:*`)
5. **Update dependency injection** to work across modules
6. **Migrate tests** to respective module test directories
7. **Remove old code** from `:app` module as features are extracted
8. **Validate dependencies** using Gradle's dependency analysis tools

#### Tools for Maintaining Module Boundaries

Use these Gradle tasks to maintain clean architecture:

```bash
# Visualize module dependencies
./gradlew :app:dependencies

# Check for circular dependencies
./gradlew buildDependencies

# Analyze module dependency graph
./gradlew app:dependencyInsight --dependency <module-name>
```

Consider using dependency rules in `build.gradle.kts`:

```kotlin
// Enforce module boundaries
configurations.all {
    resolutionStrategy {
        // Ensure domain doesn't depend on Android
        eachDependency {
            if (requested.group == "com.android" && requested.module.name.contains("domain")) {
                throw GradleException("Domain module cannot depend on Android framework")
            }
        }
    }
}
```

### Resource Naming

- **Composables**: Use PascalCase for composable function names (e.g., `SubmitButton`, `UserProfileScreen`, `EmailTextField`)
- **Drawables**: Use descriptive names with prefix indicating type (e.g., `ic_*` for icons, `bg_*` for backgrounds)
- **Strings**: Use lowercase with underscores (e.g., `app_name`, `error_message_network`)
- **Colors**: Use semantic names (e.g., `colorPrimary`, `textColorSecondary`) or describe the purpose

### Dependencies

- **Only use widely community-adopted and actively maintained libraries**
- Prefer official Android Jetpack libraries over third-party alternatives
- Keep dependencies on the latest stable version, but ensure cross-compatibility between dependencies
- Avoid adding unnecessary dependencies
- **This project uses TOML Version Catalog** (`gradle/libs.versions.toml`) for centralized dependency management
- Always add new dependencies to the version catalog, not directly in build.gradle.kts
- Check for security vulnerabilities before adding new dependencies
- Verify library is actively maintained (recent commits, active issues/PRs) before adding

### Adding Dependencies with Version Catalog

1. **Add the version** to `gradle/libs.versions.toml`:
   ```toml
   [versions]
   new-library = "1.0.0"
   ```

2. **Add the library reference**:
   ```toml
   [libraries]
   new-library = { group = "com.example", name = "library", version.ref = "new-library" }
   ```

3. **Use in build.gradle.kts**:
   ```kotlin
   dependencies {
       implementation(libs.new.library)
   }
   ```

4. **For plugin dependencies**, add to `[plugins]` section:
   ```toml
   [plugins]
   new-plugin = { id = "com.example.plugin", version.ref = "new-library" }
   ```

5. **Use bundles for related dependencies**:
   ```toml
   [bundles]
   feature-name = ["library-one", "library-two", "library-three"]
   ```

   Then use:
   ```kotlin
   implementation(libs.bundles.feature.name)
   ```

## Build and Development Workflow

When add code always follow this flow:
- Develop feature
- Create or update tests (Unit and UI)
- Run linter (and fix any detected issues)
- Run Unit tests and UI tests (and fix any detected issues)
- If all pass, update any document needed to reflect the changes and if a relevant change update the copilot instructions
- create the commit with a clear commit message explaining the changes following this convention: https://www.conventionalcommits.org/en/v1.0.0/#specification

### Building the Project

```bash
# Clean build
./gradlew clean build

# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease
```

## Prohibited Actions

- **Never commit**:
  - API keys, passwords, or any sensitive credentials
  - Local configuration files (local.properties)
  - Generated files (build/, .gradle/)
  - IDE-specific files unless necessary (.idea/* except for code style)
  - Large binary files or APKs

- **Never modify**:
  - `.gitignore` to include files that should be ignored
  - Security-related configurations without explicit approval
  - Build configuration in ways that would break CI/CD

- **Avoid**:
  - XML layouts (use Jetpack Compose instead)
  - Hardcoding values that should be in resources or configuration
  - Leaving TODO/FIXME comments without context or tracking
  - Deep nesting of Composables (extract reusable components)
  - Memory leaks (always clean up listeners, properly manage lifecycle)
  - Don't revert the version of dependencies to older versions.

## Best Practices

### 1. Security
- Always validate user input
- Use HTTPS for all network communication
- Store sensitive data securely (using EncryptedSharedPreferences or Keystore)
- Implement proper authentication and authorization
- Follow OWASP Mobile Security guidelines

### 2. Performance
- Avoid blocking the main thread
- Use Kotlin coroutines for asynchronous operations
- Optimize images and resources
- Implement proper caching strategies
- Use ProGuard/R8 for release builds, ensure when code is added it's has no issues to run after proguard runs
- Follow Compose performance best practices (remember, derivedStateOf, key())

### 3. Accessibility
- Provide content descriptions for images and icons
- Ensure proper touch target sizes
- Support TalkBack and other accessibility services
- Use semantic markup
- Emsure all screens support portrait and landscape for proper Android 16+ support

### 4. Localization
- Always use string resources, never hardcode strings in code
- Support RTL (right-to-left) languages
- Test with different locales

### 5. Documentation

- Document complex logic and algorithms
- Use KDoc for public APIs (see TechWriter Agent for detailed guidelines)
- Keep documentation updated (documentation changes should be handled by TechWriter Agent)
- Document architectural decisions in PROJECT_SUMMARY.md

## Git Workflow

- Use meaningful commit messages following conventional commits format
- Create feature branches from main/develop
- Keep commits atomic and focused
- Write descriptive PR descriptions
- Reference issue numbers in commits and PRs

## Navigation Patterns (Jetpack Navigation Compose)

This project uses **Jetpack Navigation Compose** with type-safe routes. Follow these patterns when working with navigation:

### 1. Route Definitions

Define all routes as a sealed class with Kotlin Serialization in `Screen.kt`:

```kotlin
sealed class Screen {
    @Serializable
    object Splash
    
    @Serializable
    object Landing
    
    @Serializable
    data class Details(val tileId: Int)
}
```

**Guidelines**:
- Use `object` for destinations without parameters
- Use `data class` for destinations that require arguments
- Always annotate with `@Serializable`
- Keep routes in a single sealed class for organization
- Use descriptive names that match the screen/feature

### 2. NavHost Setup

Define the navigation graph in `AlfieNavHost.kt`:

```kotlin
@Composable
fun AlfieNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash,
        modifier = modifier
    ) {
        composable<Screen.Splash> {
            SplashScreen(onNavigateToLanding = {
                navController.navigate(Screen.Landing) {
                    popUpTo(Screen.Splash) { inclusive = true }
                }
            })
        }
        
        composable<Screen.Details> { backStackEntry ->
            val details = backStackEntry.toRoute<Screen.Details>()
            DetailsScreen(
                tileId = details.tileId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
```

**Guidelines**:
- Use `composable<Screen.Type>` for type-safe route registration
- Extract route arguments using `backStackEntry.toRoute<Screen.Type>()`
- Pass navigation callbacks to composables, not the navController itself
- Use descriptive callback names (e.g., `onNavigateToLanding`, `onTileClick`)

### 3. Navigation Actions

**Simple Navigation** (no parameters):
```kotlin
navController.navigate(Screen.Landing)
```

**Navigation with Arguments**:
```kotlin
navController.navigate(Screen.Details(tileId = 42))
```

**Navigation with Back Stack Control**:
```kotlin
navController.navigate(Screen.Landing) {
    popUpTo(Screen.Splash) { inclusive = true }  // Remove splash from back stack
}
```

**Back Navigation**:
```kotlin
navController.popBackStack()
```

**Pop to Specific Destination**:
```kotlin
navController.popBackStack(Screen.Landing, inclusive = false)
```

### 4. Screen Composables

Keep navigation logic out of screen composables. Use callbacks:

```kotlin
@Composable
fun LandingScreen(
    onTileClick: (Int) -> Unit,  // Navigation callback
    viewModel: LandingViewModel = hiltViewModel()
) {
    // UI implementation
    TileGrid(onTileClick = onTileClick)
}
```

**Guidelines**:
- Never pass `NavController` directly to screen composables
- Use callbacks (lambda parameters) for navigation actions
- Keep screens testable by avoiding direct navigation dependencies
- Use `hiltViewModel()` for ViewModel injection in navigation composables

### 5. Deep Linking

Configure deep links in `AndroidManifest.xml`:

```xml
<intent-filter>
    <action android:name="android.intent.action.VIEW" />
    <category android:name="android.intent.category.DEFAULT" />
    <category android:name="android.intent.category.BROWSABLE" />
    <data android:scheme="alfie" android:host="landing.page" />
</intent-filter>
```

Handle deep links in `AlfieNavHost.kt`:

```kotlin
LaunchedEffect(intent) {
    intent?.data?.let { uri ->
        if (uri.scheme == "alfie" && uri.host == "landing.page") {
            navController.navigate(Screen.Landing) {
                popUpTo(Screen.Splash) { inclusive = true }
            }
        }
    }
}
```

### 6. Common Patterns

**Single Top Navigation** (avoid duplicate screens):
```kotlin
navController.navigate(Screen.Landing) {
    launchSingleTop = true
}
```

**Clear Back Stack** (for logout or similar):
```kotlin
navController.navigate(Screen.Login) {
    popUpTo(0) { inclusive = true }
}
```

**Conditional Navigation**:
```kotlin
if (isLoggedIn) {
    navController.navigate(Screen.Home)
} else {
    navController.navigate(Screen.Login)
}
```

### 7. What NOT to Do

❌ **Don't use string-based routes**:
```kotlin
// Wrong
navController.navigate("details/$tileId")

// Correct
navController.navigate(Screen.Details(tileId))
```

❌ **Don't pass NavController to composables**:
```kotlin
// Wrong
@Composable
fun MyScreen(navController: NavController)

// Correct
@Composable
fun MyScreen(onNavigate: () -> Unit)
```

❌ **Don't forget @Serializable annotation**:
```kotlin
// Wrong
data class Details(val id: Int)

// Correct
@Serializable
data class Details(val id: Int)
```

❌ **Don't handle navigation in ViewModels** (unless using a navigation event pattern):
```kotlin
// Wrong - direct navigation in ViewModel
class MyViewModel(private val navController: NavController)

// Correct - emit events that UI handles
class MyViewModel : ViewModel() {
    private val _navigationEvent = MutableStateFlow<NavigationEvent?>(null)
    val navigationEvent: StateFlow<NavigationEvent?> = _navigationEvent
}
```

### 8. Adding New Screens

When adding a new screen:

1. ✅ Define the route in `Screen.kt`
2. ✅ Create the screen composable with navigation callbacks
3. ✅ Add the destination to `AlfieNavHost.kt`
4. ✅ Update calling screens to navigate to the new destination
5. ✅ Add navigation tests
6. ✅ Update AndroidManifest.xml if deep linking is needed

## Additional Notes
- Prioritize clean, readable code over clever solutions
- When in doubt, follow the principle of least surprise
- Keep the codebase simple and maintainable for future developers
