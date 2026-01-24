# Contributing to Alfie

Thank you for your interest in contributing to Alfie! This document provides guidelines and instructions for contributing.

## Code of Conduct

- Be respectful and inclusive
- Provide constructive feedback
- Focus on what is best for the community

## How to Contribute

### Reporting Bugs

1. Check if the bug has already been reported in Issues
2. If not, create a new issue with:
   - Clear title and description
   - Steps to reproduce
   - Expected vs actual behavior
   - Screenshots if applicable
   - Device/emulator info and Android version

### Suggesting Enhancements

1. Open an issue with:
   - Clear description of the enhancement
   - Use cases and benefits
   - Potential implementation approach

### Pull Requests

1. **Fork and Clone**
   ```bash
   git clone https://github.com/your-username/Alfie-Android-Skeleton.git
   cd Alfie-Android-Skeleton
   ```

2. **Create a Branch**
   ```bash
   git checkout -b feature/your-feature-name
   # or
   git checkout -b fix/your-bug-fix
   ```

3. **Make Changes**
   - Follow the code style guidelines below
   - Write/update tests for your changes
   - Update documentation if needed

4. **Test Locally**
   ```bash
   ./gradlew check
   ./gradlew connectedDebugAndroidTest
   ```

5. **Commit Changes**
   ```bash
   git add .
   git commit -m "Brief description of changes"
   ```
   
   Commit message format:
   - Use present tense ("Add feature" not "Added feature")
   - First line: brief summary (50 chars or less)
   - Body: detailed explanation if needed

6. **Push and Create PR**
   ```bash
   git push origin feature/your-feature-name
   ```
   Then create a pull request on GitHub

7. **Code Review**
   - Wait for CI checks to pass
   - Address review comments
   - Update PR as needed

## Code Style Guidelines

### Kotlin

- Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use meaningful variable and function names
- Keep functions small and focused (ideally <20 lines)
- Prefer immutability (use `val` over `var`)
- Use Kotlin standard library functions

```kotlin
// Good
val items = listOf(1, 2, 3)
val doubled = items.map { it * 2 }

// Avoid
var items = ArrayList<Int>()
items.add(1)
items.add(2)
```

### Jetpack Compose

- One composable per file when possible
- Use `@Preview` annotations for composables
- Keep composables stateless when possible
- Hoist state to ViewModel when appropriate

```kotlin
@Composable
fun MyScreen(
    viewModel: MyViewModel = hiltViewModel(),
    onNavigate: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    MyScreenContent(
        state = state,
        onAction = viewModel::handleAction,
        onNavigate = onNavigate
    )
}

@Composable
private fun MyScreenContent(
    state: MyState,
    onAction: (Action) -> Unit,
    onNavigate: () -> Unit
) {
    // UI implementation
}
```

### Architecture

- Follow MVVM pattern
- ViewModels should not reference Android framework classes (except SavedStateHandle)
- Use StateFlow for state management
- Keep business logic in ViewModels, not in Composables

```kotlin
@HiltViewModel
class MyViewModel @Inject constructor(
    private val repository: MyRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(MyState())
    val state: StateFlow<MyState> = _state.asStateFlow()
    
    fun handleAction(action: Action) {
        viewModelScope.launch {
            // Handle action
        }
    }
}
```

### Testing

- Write unit tests for all ViewModels
- Write UI tests for critical user flows
- Use snapshot tests for visual regression testing
- Aim for >80% code coverage

```kotlin
// ViewModel test
@Test
fun `when action is triggered then state updates correctly`() = runTest {
    val viewModel = MyViewModel()
    
    viewModel.handleAction(SomeAction)
    
    assertEquals(ExpectedState, viewModel.state.value)
}

// UI test
@Test
fun myScreen_displaysCorrectContent() {
    composeTestRule.setContent {
        MyScreen(onNavigate = {})
    }
    
    composeTestRule
        .onNodeWithText("Expected Text")
        .assertIsDisplayed()
}
```

### File Organization

```
feature/
├── FeatureScreen.kt        # Composable
├── FeatureViewModel.kt     # ViewModel
├── FeatureState.kt         # State data class (if complex)
└── FeatureModels.kt        # Feature-specific models
```

## Testing Requirements

All pull requests must:

1. Pass lint checks
2. Pass all existing tests
3. Include new tests for new features
4. Maintain or improve code coverage

Run locally before submitting:
```bash
./gradlew lint
./gradlew testDebugUnitTest
./gradlew connectedDebugAndroidTest
```

## Documentation

Update documentation when:
- Adding new features
- Changing existing functionality
- Modifying build process
- Updating dependencies

Documentation includes:
- README.md
- Code comments (when necessary)
- KDoc for public APIs
- Setup instructions

## Dependency Management

- Minimize new dependencies
- Use latest stable versions
- Document why a dependency is needed
- Update gradle/libs.versions.toml if using version catalog

Before adding a dependency, consider:
- Is it maintained?
- What's the size impact?
- Are there security concerns?
- Can we achieve this without a dependency?

## Review Process

1. **Automated Checks**: CI must pass
2. **Code Review**: At least one approval required (@copilot auto-assigned)
3. **Testing**: Verify on device/emulator
4. **Merge**: Squash and merge to main

## Branch Strategy

- `main`: Production-ready code
- `feature/*`: New features
- `fix/*`: Bug fixes
- `chore/*`: Maintenance tasks
- `refactor/*`: Code refactoring

## Release Process

Releases are automated via GitHub Actions:
1. PR merged to main
2. CI builds and tests
3. Release created with auto-incremented version
4. APK/AAB attached to release

## Getting Help

- Open an issue for questions
- Check existing issues and PRs
- Review documentation

## License

By contributing, you agree that your contributions will be licensed under the MIT License.

## Thank You!

Your contributions make this project better for everyone!
