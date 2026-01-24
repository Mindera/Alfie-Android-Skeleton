# Alfie Android Skeleton

This is a showcase for a AI Driven developed Android App.
A boilerplate sample of an Android App Codebase with MVVM Architecture and Unit, UI and Snapshot tests together with configuration for Copilot, and several automations (PR Tests, Releases, Auto-Tagging) 

## 📋 Overview

Alfie is a sample Android application showcasing best practices in Android development. The app features a splash screen, a grid-based landing page, and detailed views with deep linking support.

## 🏗️ Architecture

The application follows the **MVVM (Model-View-ViewModel)** architecture pattern combined with Clean Architecture principles and a modularized structure:

```
Alfie-Android-Skeleton/
├── app/                    # Application module (orchestration)
│   ├── di/                # Dependency Injection (Hilt modules)
│   └── MainActivity.kt    # Entry point
├── feature/               # Feature modules (presentation layer)
│   ├── splash/           # Splash screen feature
│   ├── landing/          # Landing page with grid layout
│   └── details/          # Details page
├── core/                  # Core shared modules
│   ├── ui/               # Shared UI components and theme
│   └── navigation/       # Navigation setup and routes
├── domain/                # Business logic layer (pure Kotlin)
└── data/                  # Data layer (repositories, data sources)
```

### Key Architectural Components:

- **View**: Composable functions that define the UI
- **ViewModel**: Manages UI state and business logic using StateFlow
- **Navigation**: Type-safe navigation using Jetpack Navigation Compose with Kotlin Serialization
- **Dependency Injection**: Hilt for compile-time DI
- **Modularization**: Hybrid strategy combining feature-based and layer-based modules

## 🛠️ Tech Stack

| Category | Technology |
|----------|-----------|
| **Language** | Kotlin 2.3.0 |
| **UI Framework** | Jetpack Compose (BOM 2026.01.00) |
| **Architecture** | MVVM + Clean Architecture |
| **Dependency Injection** | Hilt 2.58 |
| **Navigation** | Jetpack Navigation Compose 2.9.6 |
| **Async** | Kotlin Coroutines 1.10.2 |
| **UI Design** | Material Design 3 |
| **Image Loading** | Coil 2.7.0 |
| **Testing** | JUnit, Compose Testing, Paparazzi |
| **Build System** | Gradle (AGP 8.13.2) with TOML Version Catalog |

## 📱 Features

### 1. Splash Screen
- Displays "ALFIE" branding
- Shows loading indicator
- Auto-navigates to landing page after 2 seconds

### 2. Landing Page
- Grid layout with 2 tiles per row
- Each tile displays an image placeholder and title
- Clickable tiles navigate to details page
- Supports deep linking via `alfie://landing.page`

### 3. Details Page
- Full-width top image
- Lorem ipsum text content
- Back navigation support

### 4. Deep Linking
The app supports the following deep link:
```
alfie://landing.page
```

Test deep linking with ADB:
```bash
adb shell am start -W -a android.intent.action.VIEW -d "alfie://landing.page" com.mindera.alfie.skeleton
```

## 🧭 Navigation Architecture

The app uses **Jetpack Navigation Compose** with type-safe routes for seamless navigation between screens.

### Navigation Setup

The navigation graph is defined in `AlfieNavHost.kt` using a centralized NavHost:

```kotlin
@Composable
fun AlfieNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash
    ) {
        composable<Screen.Splash> {
            SplashScreen(onNavigateToLanding = {
                navController.navigate(Screen.Landing) {
                    popUpTo(Screen.Splash) { inclusive = true }
                }
            })
        }
        
        composable<Screen.Landing> {
            LandingScreen(onTileClick = { tileId ->
                navController.navigate(Screen.Details(tileId))
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

### Route Definitions

Routes are defined as a sealed class with Kotlin Serialization for type safety:

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

### Key Navigation Patterns

1. **Simple Navigation**: Navigate to a destination without parameters
   ```kotlin
   navController.navigate(Screen.Landing)
   ```

2. **Navigation with Arguments**: Pass typed parameters to destinations
   ```kotlin
   navController.navigate(Screen.Details(tileId = 42))
   ```

3. **Pop Behavior**: Remove screens from back stack
   ```kotlin
   navController.navigate(Screen.Landing) {
       popUpTo(Screen.Splash) { inclusive = true }
   }
   ```

4. **Back Navigation**: Navigate back to previous screen
   ```kotlin
   navController.popBackStack()
   ```

5. **Deep Linking**: Handle external navigation via Intent
   ```kotlin
   LaunchedEffect(intent) {
       intent?.data?.let { uri ->
           if (uri.scheme == "alfie" && uri.host == "landing.page") {
               navController.navigate(Screen.Landing)
           }
       }
   }
   ```

### Benefits of This Approach

- ✅ **Type Safety**: Compile-time validation of navigation arguments
- ✅ **No String Routes**: Eliminates typos and runtime errors from string-based routes
- ✅ **IDE Support**: Full autocomplete and refactoring support
- ✅ **Serialization**: Automatic argument serialization/deserialization
- ✅ **Testability**: Easy to test navigation logic with `TestNavHostController`

## 🚀 Getting Started

### Prerequisites
- JDK 17 or higher
- Android Studio Hedgehog or later
- Android SDK 34
- Gradle 8.2+

### Building the Project

1. **Clone the repository**
```bash
git clone https://github.com/your-org/Alfie-Android-Simple.git
cd Alfie-Android-Simple
```

2. **Build the project**
```bash
./gradlew build
```

3. **Run the app**
```bash
./gradlew installDebug
```

### Running on Emulator/Device
Open the project in Android Studio and click the Run button, or use:
```bash
./gradlew installDebug
adb shell am start -n com.mindera.alfie.skeleton/.MainActivity
```

## 📦 Dependency Management

The project uses **Gradle Version Catalog** (TOML) for centralized dependency management.

### Version Catalog Location

All dependencies are defined in `gradle/libs.versions.toml`:

```toml
[versions]
kotlin = "1.9.24"
compose-bom = "2024.02.00"
navigation-compose = "2.8.5"
hilt = "2.50"

[libraries]
androidx-navigation-compose = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navigation-compose" }
hilt-android = { group = "com.google.dagger", name = "hilt-android", version.ref = "hilt" }

[bundles]
compose = ["androidx-compose-ui", "androidx-compose-ui-graphics", "androidx-compose-material3"]
```

### Using Dependencies in Build Files

In `build.gradle.kts` files, reference dependencies using the catalog:

```kotlin
dependencies {
    // Single dependency
    implementation(libs.androidx.navigation.compose)
    
    // Dependency bundle
    implementation(libs.bundles.compose)
    
    // Platform/BOM
    implementation(platform(libs.androidx.compose.bom))
}
```

### Benefits

- ✅ **Centralized Version Management**: All versions in one place
- ✅ **Type-Safe**: IDE autocomplete for dependency names
- ✅ **Consistency**: Same versions across all modules
- ✅ **Easy Updates**: Update version in one location
- ✅ **Dependency Bundles**: Group related dependencies together

### Adding New Dependencies

1. Add the version to `gradle/libs.versions.toml`:
   ```toml
   [versions]
   new-library = "1.0.0"
   ```

2. Add the library reference:
   ```toml
   [libraries]
   new-library = { group = "com.example", name = "library", version.ref = "new-library" }
   ```

3. Use in your build file:
   ```kotlin
   implementation(libs.new.library)
   ```

## 🧪 Testing

The project includes comprehensive test coverage:

### Unit Tests
Tests for ViewModels and business logic:
```bash
./gradlew testDebugUnitTest
```

### UI Tests (Instrumented)
Compose UI tests running on emulator/device:
```bash
./gradlew connectedDebugAndroidTest
```

### Snapshot Tests
Visual regression testing using Paparazzi:
```bash
./gradlew verifyPaparazziDebug
```

To record new snapshots:
```bash
./gradlew recordPaparazziDebug
```

### Run All Tests
```bash
./gradlew test connectedAndroidTest
```

### Linting
```bash
./gradlew lint
```

## 🔄 CI/CD Pipeline

The project uses GitHub Actions for continuous integration and deployment.

### PR Workflow (`pr-check.yml`)
Triggered on pull requests to `main`:
- **Parallel Jobs**: Linting, Unit Tests, UI Tests
- **Strategy**: Fail-fast (jobs stop if one fails)
- **Requirement**: All checks must pass for PR merge
- **Merge Queue**: Enabled for sequential merging

### Auto Label Workflow (`auto-label.yml`)
Triggered on pull request events (opened, synchronize, reopened):
- Automatically adds labels based on changed files:
  - `documentation`: Any `.md` file changes
  - `ai-config`: Changes to `.github/copilot/` or `.github/agents/` files
  - `tests`: Changes to test files (`*Test.kt`, `*Tests.kt`, or files in `test/` or `androidTest/` directories)
- Helps with PR organization and filtering

### Release Workflow (`release.yml`)
Triggered on push to `main` (after merge):
- **Sequential Jobs**:
  1. **Unit Tests**: Runs all unit tests
  2. **UI Tests**: Runs instrumented tests on Android emulator
  3. **Snapshot Tests**: Runs Paparazzi screenshot tests
  4. **Build & Release**: Only proceeds if all tests pass
- Builds unsigned debug APK and signed release APK
- Builds signed release AAB
- Creates GitHub release with auto-incremented version (v1.0.X)
- Includes commit messages and test status in release notes
- Uploads unsigned APK, signed APK, and AAB to release
- Distributes signed APK to Firebase App Distribution (if configured)

**Version Format**: `1.0.BUILD_NUMBER`

### Deploy Workflow (`deploy-playstore.yml`)
Manual trigger for Play Store deployment:
- Builds signed release AAB
- Uploads to Play Store internal track
- Requires service account credentials

### PR Metrics Workflow (`pr-metrics.yml`)
Generates weekly reports tracking PR review activity:
- **Schedule**: Runs every Monday at 9:00 AM UTC
- **Manual Trigger**: Can be triggered manually with custom lookback period
- **Metrics Tracked**:
  - All PR authors
  - All reviewers and their activity levels
  - Review comments per reviewer (including comments that imply code changes)
  - Average review time
  - Approved PRs that failed CI checks
  - Inactive reviewers (team members not reviewing)
- **Output**: Report uploaded as artifact and displayed in workflow summary (formatted as markdown table)
- **Retention**: Reports retained for 90 days

The metrics help track team engagement, identify bottlenecks, and improve the review process. See [.github/scripts/README.md](.github/scripts/README.md) for more details.
### AI Metrics Workflow (`ai-metrics-report.yml`)
Automated weekly reporting for AI development productivity:
- **Schedule**: Every Monday at 9:00 AM UTC
- **Manual Trigger**: Available via GitHub Actions UI
- **Output**: Creates a PR with comprehensive metrics report

#### Tracked Metrics:
- **PR Lifecycle**: Issue → PR → Merge timelines
- **CI Performance**: Time to first successful build, success rates
- **Human Interactions**: Review comments, manual commits, reviewers
- **AI vs Human**: Productivity comparison between AI and human-created PRs
- **Code Changes**: Files changed, lines added/deleted

The report is automatically generated and submitted as a Pull Request in the `metrics/` directory. See [`.github/scripts/README.md`](.github/scripts/README.md) for details.

### Required Secrets

Configure these in GitHub Settings → Secrets:

| Secret Name | Description | Required For |
|-------------|-------------|--------------|
| `KEYSTORE_BASE64` | Base64 encoded keystore file | Release signing |
| `KEYSTORE_PASSWORD` | Keystore password | Release signing |
| `KEY_ALIAS` | Key alias | Release signing |
| `KEY_PASSWORD` | Key password | Release signing |
| `FIREBASE_APP_ID` | Firebase App Distribution app ID | Firebase distribution (optional) |
| `FIREBASE_SERVICE_CREDENTIALS` | Firebase service account JSON | Firebase distribution (optional) |
| `PLAY_STORE_SERVICE_ACCOUNT_JSON` | Google Play service account JSON | Play Store deployment |

**Generate keystore**:
```bash
keytool -genkey -v -keystore release.keystore -alias alfie -keyalg RSA -keysize 2048 -validity 10000
base64 release.keystore > keystore.txt
```

**Note**: Firebase secrets are optional. If not configured, the release workflow will skip Firebase App Distribution.

## 📦 Project Structure

```
Alfie-Android-Simple/
├── .github/
│   ├── workflows/
│   │   ├── pr-check.yml          # PR validation
│   │   ├── release.yml           # Release automation
│   │   └── deploy-playstore.yml  # Play Store deployment
│   └── CODEOWNERS                # Auto code review assignment
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/mindera/alfie/skeleton/
│   │   │   │   ├── di/           # Hilt modules
│   │   │   │   ├── ui/
│   │   │   │   │   ├── splash/
│   │   │   │   │   ├── landing/
│   │   │   │   │   ├── details/
│   │   │   │   │   ├── navigation/
│   │   │   │   │   └── theme/
│   │   │   │   ├── MainActivity.kt
│   │   │   │   └── AlfieApplication.kt
│   │   │   ├── res/
│   │   │   └── AndroidManifest.xml
│   │   ├── test/                 # Unit tests
│   │   └── androidTest/          # UI tests
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
└── README.md
```

## 🎨 Design Patterns

### State Management
- **StateFlow**: Unidirectional data flow
- **Immutable State**: State objects are read-only
- **Single Source of Truth**: ViewModel holds the state

### Navigation (Jetpack Navigation Compose)
- **Type-Safe Navigation**: Using Kotlin Serialization with sealed class routes
- **NavHost**: Centralized navigation graph in `AlfieNavHost.kt`
- **Declarative Routes**: Screen routes defined as serializable sealed classes
- **Deep Linking**: Intent filters for external navigation support
- **Back Stack Management**: Proper back navigation with `popBackStack()` and `popUpTo` patterns
- **Parameter Passing**: Type-safe argument passing via data classes (e.g., `Screen.Details(tileId)`)
- **Integration**: Seamless Hilt ViewModel integration with `hiltViewModel()`

### Dependency Injection
- **Constructor Injection**: ViewModels receive dependencies via constructor
- **Scoping**: Application-level and ViewModel-level scopes
- **Testing**: Easy mocking with Hilt testing utilities

## 🤝 Contributing

### Code Review
All code changes require review by @copilot (configured in CODEOWNERS).

### Pull Request Process
1. Create a feature branch from `main`
2. Make your changes following the existing code style
3. Ensure all tests pass locally
4. Push and create a pull request
5. Wait for CI checks to pass
6. Address review comments
7. Merge when approved

### Code Style
- Follow official Kotlin coding conventions
- Use meaningful variable names
- Keep functions small and focused
- Write tests for new features

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🔗 Resources

- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)
- [Hilt Documentation](https://dagger.dev/hilt/)
- [Material Design 3](https://m3.material.io/)
- [Kotlin Coroutines Guide](https://kotlinlang.org/docs/coroutines-guide.html)

## 📞 Support

For issues and feature requests, please use the [GitHub Issues](https://github.com/your-org/Alfie-Android-Simple/issues) page.
