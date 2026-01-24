# Setup Instructions

## Prerequisites

Before building this project, ensure you have the following installed:

1. **JDK 17 or higher**
   ```bash
   java -version
   ```

2. **Android SDK**
   - Install Android Studio or Android Command Line Tools
   - SDK Platform 36 (Android 16)
   - Build Tools 34.0.0 or higher

3. **Environment Variables**
   ```bash
   export ANDROID_HOME=$HOME/Android/Sdk
   export PATH=$PATH:$ANDROID_HOME/platform-tools
   export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin
   ```

## First Time Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd Alfie-Android-Skeleton
   ```

2. **Make gradlew executable** (Linux/Mac only)
   ```bash
   chmod +x gradlew
   ```

3. **Sync Gradle and download dependencies**
   ```bash
   ./gradlew --version
   ```
   This will download Gradle wrapper and all project dependencies.

## Dependency Management

This project uses **Gradle Version Catalog** (TOML) for centralized dependency management.

### Version Catalog Structure

Dependencies are defined in `gradle/libs.versions.toml`:

```toml
[versions]
# Define versions
kotlin = "2.3.0"
hilt = "2.58"

[libraries]
# Define libraries
hilt-android = { group = "com.google.dagger", name = "hilt-android", version.ref = "hilt" }

[plugins]
# Define plugins
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }

[bundles]
# Group related dependencies
compose = ["androidx-compose-ui", "androidx-compose-material3"]
```

### Adding Dependencies

1. **Add version** in `[versions]` section
2. **Add library** in `[libraries]` section
3. **Use in build.gradle.kts**:
   ```kotlin
   dependencies {
       implementation(libs.library.name)
   }
   ```

### Benefits

- All versions centralized in one file
- Type-safe dependency references
- Easy version updates
- Dependency bundling for related libraries

## Building the Project

### Debug Build
```bash
./gradlew assembleDebug
```
Output: `app/build/outputs/apk/debug/app-debug.apk`

### Release Build
```bash
./gradlew assembleRelease
```
Output: `app/build/outputs/apk/release/app-release.apk`

### Build AAB (Android App Bundle)
```bash
./gradlew bundleRelease
```
Output: `app/build/outputs/bundle/release/app-release.aab`

## Running Tests

### Unit Tests
```bash
./gradlew testDebugUnitTest
```
Reports: `app/build/reports/tests/testDebugUnitTest/index.html`

### Instrumented Tests (requires device/emulator)
```bash
./gradlew connectedDebugAndroidTest
```
Reports: `app/build/reports/androidTests/connected/index.html`

### Snapshot Tests
```bash
# Record snapshots
./gradlew recordPaparazziDebug

# Verify snapshots
./gradlew verifyPaparazziDebug
```

### Lint
```bash
./gradlew lint
```
Reports: `app/build/reports/lint-results-debug.html`

### All Checks
```bash
./gradlew check
```

## Installing on Device

### Debug APK
```bash
./gradlew installDebug
```

### Launch App
```bash
adb shell am start -n com.mindera.alfie.skeleton/.MainActivity
```

## Testing Deep Links

```bash
adb shell am start -W -a android.intent.action.VIEW \
  -d "alfie://landing.page" com.mindera.alfie.skeleton
```

## Signing Release Builds

### Generate Keystore
```bash
keytool -genkey -v -keystore release.keystore \
  -alias alfie -keyalg RSA -keysize 2048 -validity 10000
```

### Set Environment Variables
```bash
export KEYSTORE_FILE=/path/to/release.keystore
export KEYSTORE_PASSWORD=your_keystore_password
export KEY_ALIAS=alfie
export KEY_PASSWORD=your_key_password
export BUILD_NUMBER=1
```

### Build Signed Release
```bash
./gradlew assembleRelease
```

## CI/CD Setup

### Required GitHub Secrets

Set these in: Settings → Secrets and variables → Actions

1. **KEYSTORE_BASE64**: Base64 encoded keystore file
   ```bash
   base64 release.keystore > keystore.txt
   # Copy content to GitHub secret
   ```

2. **KEYSTORE_PASSWORD**: Keystore password
3. **KEY_ALIAS**: Key alias (e.g., "alfie")
4. **KEY_PASSWORD**: Key password
5. **PLAY_STORE_SERVICE_ACCOUNT_JSON**: Google Play service account JSON

### Workflow Triggers

- **PR Checks**: Automatically run on pull requests to `main`
- **Release**: Automatically run on push to `main`
- **Play Store Deploy**: Manual trigger via GitHub Actions UI

## Troubleshooting

### Gradle Daemon Issues
```bash
./gradlew --stop
./gradlew clean build --no-daemon
```

### Clear Gradle Cache
```bash
rm -rf ~/.gradle/caches/
./gradlew clean build --refresh-dependencies
```

### Android SDK Not Found
Ensure `ANDROID_HOME` is set correctly:
```bash
echo $ANDROID_HOME
ls $ANDROID_HOME/platforms
```

### Build Tools Version
Check installed build tools:
```bash
ls $ANDROID_HOME/build-tools/
```

If missing, install via SDK Manager:
```bash
sdkmanager "build-tools;34.0.0"
```

## IDE Setup

### Android Studio
1. Open Android Studio
2. Select "Open an Existing Project"
3. Navigate to project root directory
4. Wait for Gradle sync to complete
5. Run → Run 'app'

### IntelliJ IDEA
1. File → Open → Select project root
2. Import as Gradle project
3. Wait for indexing
4. Run configuration: Android App

## Project Structure

```
app/
├── src/
│   ├── main/                   # Main source set
│   │   ├── java/              # Kotlin source files
│   │   ├── res/               # Resources
│   │   └── AndroidManifest.xml
│   ├── test/                  # Unit tests
│   └── androidTest/           # Instrumented tests
└── build.gradle.kts           # Module build file
```

## Useful Gradle Commands

```bash
# List all tasks
./gradlew tasks

# List all dependencies
./gradlew dependencies

# Dependency tree
./gradlew app:dependencies

# Build scan
./gradlew build --scan

# Parallel build
./gradlew build --parallel

# Offline mode
./gradlew build --offline
```

## Next Steps

After successful build:
1. Run the app on emulator/device
2. Test deep linking
3. Run all tests
4. Set up CI/CD with GitHub Actions
5. Configure code signing for release builds

## 📱 Navigation Testing

### Testing Type-Safe Navigation

The app uses Jetpack Navigation Compose with type-safe routes. You can test navigation in the following ways:

#### 1. Manual UI Testing
```bash
# Install and run the app
./gradlew installDebug
adb shell am start -n com.mindera.alfie.skeleton/.MainActivity

# Test navigation flow:
# - Wait on splash screen (auto-navigates after 2s)
# - Click any tile on landing page
# - Verify details screen shows
# - Press back button to return
```

#### 2. Deep Link Testing
```bash
# Test deep linking to landing page
adb shell am start -W -a android.intent.action.VIEW \
  -d "alfie://landing.page" com.mindera.alfie.skeleton

# This should bypass the splash and go directly to the landing page
```

#### 3. Automated Navigation Testing
```bash
# Run instrumented tests that verify navigation
./gradlew connectedDebugAndroidTest --tests "*.ui.*"

# These tests use navigation-testing library to verify:
# - Navigation to correct destinations
# - Proper argument passing
# - Back stack behavior
```

### Adding New Navigation Destinations

1. **Define a new route** in `Screen.kt`:
   ```kotlin
   @Serializable
   data class NewScreen(val param: String)
   ```

2. **Add to NavHost** in `AlfieNavHost.kt`:
   ```kotlin
   composable<Screen.NewScreen> { backStackEntry ->
       val route = backStackEntry.toRoute<Screen.NewScreen>()
       NewScreenComposable(param = route.param)
   }
   ```

3. **Navigate to the screen**:
   ```kotlin
   navController.navigate(Screen.NewScreen(param = "value"))
   ```

4. **Write tests** in `src/androidTest`:
   ```kotlin
   @Test
   fun testNavigationToNewScreen() {
       val navController = TestNavHostController(context)
       // Test navigation behavior
   }
   ```
