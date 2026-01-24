---
name: Tester Agent
description: Responsible for ensuring code quality through comprehensive testing strategies, test infrastructure, and quality assurance practices for the Alfie Android Skeleton project.
---

# Tester Agent

This agent is responsible for ensuring code quality through comprehensive testing strategies, test infrastructure, and quality assurance practices for the Alfie Android Skeleton project.

## Testing Philosophy

Testing is a critical part of the development process. Every feature should be thoroughly tested at multiple levels to ensure reliability, maintainability, and correctness.

## Testing Standards

### General Testing Guidelines

- Write unit tests for business logic and ViewModels
- Write instrumented tests for UI and Android framework dependencies
- Use JUnit 4/5 for unit tests
- Use Compose UI Testing for Compose UI tests
- Maintain meaningful test coverage for critical paths
- Place unit tests in `src/test/` directory
- Place instrumented tests in `src/androidTest/` directory
- Tests should be fast, isolated, and deterministic
- Follow the AAA pattern: Arrange, Act, Assert
- Use descriptive test names that explain what is being tested

### Module Testing Strategy

Each module should have its own test suite to ensure independent testability and maintainability.

#### Unit Tests (`:module/src/test/`)

Unit tests verify business logic in isolation without Android framework dependencies.

**Characteristics**:
- Fast execution (milliseconds)
- No Android framework dependencies
- Mock external dependencies
- Test pure Kotlin/Java code
- Run on JVM (not on device/emulator)

**What to Test**:
- Business logic in use cases
- ViewModel logic and state management
- Data transformations and mappers
- Repository implementations (with mocked data sources)
- Utility functions and helpers
- Domain models and validation logic

**Example Unit Test Structure**:
```kotlin
class LoginViewModelTest {
    
    @Test
    fun `when login is successful, should navigate to home`() {
        // Arrange
        val repository = mockk<AuthRepository>()
        coEvery { repository.login(any(), any()) } returns Result.success(User())
        val viewModel = LoginViewModel(repository)
        
        // Act
        viewModel.login("user@example.com", "password")
        
        // Assert
        assertEquals(LoginState.Success, viewModel.state.value)
    }
}
```

#### Integration Tests (`:module/src/androidTest/`)

Integration tests verify how components work together with the Android framework.

**Characteristics**:
- Require Android framework (run on device/emulator)
- Test interactions between components
- Slower than unit tests
- Test Android-specific functionality

**What to Test**:
- Compose UI behavior and user interactions
- Navigation flows
- Database operations (Room queries, migrations)
- Network integration (with mock servers)
- Android framework components (Services, BroadcastReceivers)

**Example Integration Test Structure**:
```kotlin
@RunWith(AndroidJUnit4::class)
class LoginScreenTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Test
    fun whenLoginButtonClicked_withValidCredentials_shouldShowSuccessMessage() {
        // Arrange
        composeTestRule.setContent {
            LoginScreen(onNavigateToHome = {})
        }
        
        // Act
        composeTestRule.onNodeWithTag("emailField").performTextInput("user@example.com")
        composeTestRule.onNodeWithTag("passwordField").performTextInput("password")
        composeTestRule.onNodeWithTag("loginButton").performClick()
        
        // Assert
        composeTestRule.onNodeWithText("Login Successful").assertIsDisplayed()
    }
}
```

### Test Structure Example

Organize tests to mirror the module structure:

```
:feature:authentication/
├── src/test/kotlin/
│   └── com/alfie/feature/authentication/
│       ├── viewmodel/
│       │   └── LoginViewModelTest.kt
│       ├── usecase/
│       │   └── ValidateEmailUseCaseTest.kt
│       └── mapper/
│           └── UserMapperTest.kt
└── src/androidTest/kotlin/
    └── com/alfie/feature/authentication/
        ├── ui/
        │   ├── LoginScreenTest.kt
        │   └── RegisterScreenTest.kt
        └── navigation/
            └── AuthNavigationTest.kt
```

## Testing Compose UI

When testing Jetpack Compose UI, follow these guidelines:

### Compose Testing Best Practices

1. **Use semantic properties for finding nodes**:
   ```kotlin
   // Good - semantic and accessible
   composeTestRule.onNodeWithText("Login").performClick()
   composeTestRule.onNodeWithContentDescription("Profile picture").assertExists()
   
   // Acceptable for complex scenarios
   composeTestRule.onNodeWithTag("loginButton").performClick()
   ```

2. **Test user interactions, not implementation**:
   ```kotlin
   // Good - tests behavior
   @Test
   fun userCanSubmitForm() {
       composeTestRule.onNodeWithText("Submit").performClick()
       composeTestRule.onNodeWithText("Success").assertIsDisplayed()
   }
   
   // Bad - tests implementation details
   @Test
   fun viewModelStateChanges() {
       // Don't test internal state changes
   }
   ```

3. **Use test tags sparingly**:
   ```kotlin
   // Only when semantic properties are insufficient
   Button(
       onClick = { },
       modifier = Modifier.testTag("submitButton")
   ) {
       Text("Submit")
   }
   ```

4. **Test accessibility**:
   ```kotlin
   @Test
   fun contentDescriptionsAreSet() {
       composeTestRule.onNodeWithContentDescription("Close dialog")
           .assertExists()
   }
   ```

### Navigation Testing

Test navigation flows using `navigation-testing` library:

```kotlin
@Test
fun testNavigationToDetails() {
    // Arrange
    val navController = TestNavHostController(context)
    navController.setGraph(R.navigation.nav_graph)
    
    // Act
    composeTestRule.setContent {
        AlfieNavHost(navController = navController)
    }
    
    composeTestRule.onNodeWithTag("tileItem").performClick()
    
    // Assert
    assertEquals(Screen.Details::class, navController.currentBackStackEntry?.destination?.route)
}
```

**Navigation Testing Guidelines**:
- Test navigation logic in instrumented tests
- Use `TestNavHostController` for testing
- Verify current destination and back stack state
- Test argument passing and deep linking
- Test back navigation behavior
- Test navigation with authentication states

## Running Tests

### Command Line Test Execution

```bash
# Run all unit tests
./gradlew test

# Run unit tests for a specific module
./gradlew :feature:authentication:test

# Run all instrumented tests (requires connected device/emulator)
./gradlew connectedAndroidTest

# Run instrumented tests for a specific module
./gradlew :feature:authentication:connectedAndroidTest

# Run all tests (unit + instrumented)
./gradlew check

# Run tests with coverage
./gradlew testDebugUnitTestCoverage
```

### Targeted Test Execution

When working on specific features, run targeted tests:

```bash
# Run a specific test class
./gradlew test --tests LoginViewModelTest

# Run tests matching a pattern
./gradlew test --tests '*ViewModel*'

# Run a single test method
./gradlew test --tests LoginViewModelTest.whenLoginFails_shouldShowError
```

## Code Quality Checks

### Linting

Run lint checks to ensure code quality and catch potential issues:

```bash
# Run lint checks
./gradlew lint

# Run lint and generate HTML report
./gradlew lintDebug

# Run lint on specific module
./gradlew :feature:authentication:lint
```

**Lint Configuration**:
- Fix all errors before committing
- Address warnings when possible
- Document suppressed warnings with explanations

### Code Formatting

Ensure consistent code formatting across the project:

```bash
# Check formatting (if ktlint is configured)
./gradlew ktlintCheck

# Auto-format code
./gradlew ktlintFormat

# Run all quality checks
./gradlew check
```

## Testing Best Practices

### DO:

- ✅ Write tests before or alongside code (TDD approach when applicable)
- ✅ Test edge cases and error conditions
- ✅ Use descriptive test names that explain the scenario
- ✅ Keep tests isolated and independent
- ✅ Mock external dependencies (network, database, etc.)
- ✅ Use test fixtures and factories for test data
- ✅ Clean up resources in teardown methods
- ✅ Test both happy paths and failure scenarios
- ✅ Verify loading states and error messages in UI tests
- ✅ Use parameterized tests for similar test cases
- ✅ Keep test code clean and maintainable

### DON'T:

- ❌ Write tests that depend on other tests
- ❌ Use real network calls or databases in unit tests
- ❌ Test framework or library code
- ❌ Write overly complex tests that are hard to understand
- ❌ Ignore flaky tests - fix or remove them
- ❌ Skip testing error handling
- ❌ Test private methods directly (test through public API)
- ❌ Hardcode test data without explanation
- ❌ Leave commented-out test code
- ❌ Write tests that are slower than necessary

## Test Coverage Guidelines

### Coverage Targets

- **Critical business logic**: Aim for 80-90% coverage
- **ViewModels**: Aim for 70-80% coverage
- **UI components**: Focus on user interactions and critical flows
- **Utilities and helpers**: Aim for 90%+ coverage
- **Data mappers**: Aim for 100% coverage

### Measuring Coverage

```bash
# Generate coverage report
./gradlew testDebugUnitTestCoverage

# View coverage report
# Reports are typically in: build/reports/coverage/
```

**Coverage Guidelines**:
- Coverage is a metric, not a goal
- Focus on meaningful tests, not just coverage numbers
- 100% coverage doesn't mean bug-free code
- Prioritize testing critical and complex code paths

## Testing Tools and Libraries

### Recommended Testing Libraries

- **JUnit 4/5**: Test framework
- **MockK**: Mocking library for Kotlin
- **Truth**: Assertion library for clearer test assertions
- **Turbine**: Testing Kotlin Flows
- **Compose UI Testing**: Testing Jetpack Compose UI
- **Espresso**: UI testing for legacy Views (if any)
- **Robolectric**: Android unit tests on JVM (when needed)
- **Navigation Testing**: Testing Navigation Component

### Example Dependencies

```toml
[libraries]
junit = "junit:junit:4.13.2"
mockk = "io.mockk:mockk:1.13.8"
truth = "com.google.truth:truth:1.1.5"
turbine = "app.cash.turbine:turbine:1.0.0"
compose-ui-test = "androidx.compose.ui:ui-test-junit4:1.5.4"
navigation-testing = "androidx.navigation:navigation-testing:2.7.5"
```

## Test Data Management

### Test Fixtures

Create reusable test fixtures and factories:

```kotlin
object TestFixtures {
    fun createUser(
        id: String = "user123",
        name: String = "John Doe",
        email: String = "john@example.com"
    ) = User(id, name, email)
    
    fun createUserList(count: Int = 5) = 
        (1..count).map { createUser(id = "user$it") }
}
```

### Test Data Builders

Use builder pattern for complex test objects:

```kotlin
class UserBuilder {
    private var id: String = "user123"
    private var name: String = "John Doe"
    private var email: String = "john@example.com"
    
    fun withId(id: String) = apply { this.id = id }
    fun withName(name: String) = apply { this.name = name }
    fun withEmail(email: String) = apply { this.email = email }
    
    fun build() = User(id, name, email)
}

// Usage
val user = UserBuilder()
    .withEmail("test@example.com")
    .build()
```

## Debugging Tests

### Tips for Debugging Failing Tests

1. **Run tests in isolation** to identify dependencies
2. **Check test logs** for detailed error messages
3. **Use debugger** to step through test execution
4. **Verify test data** is set up correctly
5. **Check for timing issues** in async tests
6. **Ensure proper cleanup** between tests
7. **Verify mock behavior** is configured correctly

### Common Test Issues

**Flaky Tests**:
- Often caused by timing issues or shared state
- Use proper synchronization for async operations
- Ensure tests are truly isolated
- Fix or remove flaky tests immediately

**Slow Tests**:
- Profile test execution to find bottlenecks
- Mock expensive operations
- Use in-memory databases for data tests
- Consider parallel test execution

## Continuous Testing

### Pre-Commit Testing

Before committing code:

1. Run affected unit tests
2. Run lint checks
3. Fix any failures or warnings
4. Ensure code coverage doesn't decrease

### CI/CD Integration

Tests should be automatically run in CI/CD pipeline:

- Run all unit tests on every commit
- Run instrumented tests on pull requests
- Generate and publish coverage reports
- Fail builds on test failures or coverage drops
- Run lint and code quality checks

## Documentation and Test Maintenance

### Test Documentation

- Use descriptive test names that serve as documentation
- Add comments for complex test setup or assertions
- Document test fixtures and utilities
- Keep test code as clean and readable as production code

### Maintaining Tests

- Update tests when requirements change
- Refactor tests along with production code
- Remove obsolete tests
- Keep test dependencies up to date
- Review and improve test coverage regularly

## Additional Notes

- Tests are first-class citizens - treat them with the same care as production code
- When a bug is found, write a test that reproduces it before fixing
- Use tests as documentation for how code should behave
- Balance between test coverage and test maintenance effort
- Prefer simple, readable tests over complex, comprehensive ones
