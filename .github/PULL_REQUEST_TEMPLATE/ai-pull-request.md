---
name: AI Pull Request
about: Pull request template for AI-driven development
---

## 🤖 AI-Generated Pull Request

### 📋 Issue Reference
<!-- Link to the issue this PR addresses -->
Closes #

### 🎯 Task Summary
<!-- Brief description of what this PR accomplishes -->


### 🔧 Changes Made
<!-- Detailed list of changes made by the AI agent -->
- [ ] Feature/Component created:
- [ ] Files modified:
- [ ] Tests added/updated:
- [ ] Documentation updated:

### 🏗️ Architecture & Design Decisions
<!-- Explain architectural choices and patterns used -->
**Pattern Used**: <!-- e.g., MVVM, Repository Pattern, Use Case -->

**Module(s) Affected**: <!-- e.g., feature/landing, core/ui, domain/usecases -->

**Dependency Injection**: <!-- How Hilt/DI was utilized -->

**Navigation Changes**: <!-- If navigation routes were added/modified -->

**State Management**: <!-- How StateFlow/state is managed -->


### 🧪 Testing Coverage
<!-- Comprehensive testing information -->
#### Unit Tests
- [ ] ViewModel tests
- [ ] Repository/Use case tests  
- [ ] Model/Data class tests
- Coverage: <!-- e.g., 85% -->

#### UI Tests (Instrumented)
- [ ] Compose UI tests
- [ ] Navigation tests
- [ ] User interaction tests

#### Snapshot Tests
- [ ] Paparazzi screenshots recorded
- [ ] Visual regression tests added

#### Test Execution Results
```bash
<!-- Paste test results here -->
./gradlew testDebugUnitTest
./gradlew connectedDebugAndroidTest (if applicable)
./gradlew verifyPaparazziDebug (if applicable)
```

### 📦 Dependencies
<!-- List any new dependencies added -->
- [ ] No new dependencies
- [ ] New dependencies added (list below):
  ```toml
  # Paste from gradle/libs.versions.toml
  ```

### 🎨 Code Quality
<!-- Automated checks and quality metrics -->
- [ ] Lint checks passed: `./gradlew lint`
- [ ] Code formatting applied: Follows Kotlin conventions
- [ ] No compiler warnings
- [ ] KDoc comments added for public APIs
- [ ] Resource naming follows conventions

### 🔒 Security Considerations
<!-- Any security-related changes or considerations -->
- [ ] No sensitive data hardcoded
- [ ] No security vulnerabilities introduced
- [ ] Follows secure coding practices

### 📸 Screenshots/Videos
<!-- For UI changes, include screenshots or screen recordings -->
<!-- Use: adb shell screencap or Android Studio's screenshot tool -->


### 🔄 CI/CD Impact
<!-- Impact on build and deployment -->
- [ ] Build configuration changes: No
- [ ] New GitHub Actions workflows: No
- [ ] Release notes required: No

### 🤔 AI Agent Context
<!-- Information for future AI agents -->
**Model Used**: <!-- e.g., GPT-4, Claude, Copilot -->

**Agent Type**: <!-- e.g., architect-developer, tester, techwriter -->

**Iterations Required**: <!-- Number of attempts/refinements -->

**Challenges Encountered**: 
<!-- Any issues the AI faced and how they were resolved -->


### 📝 Additional Notes
<!-- Any other relevant information -->


---

### ✅ Pre-merge Checklist
<!-- Verify before requesting review -->
- [ ] Code follows project architecture (MVVM + Clean Architecture)
- [ ] Module dependency rules respected (no circular dependencies)
- [ ] All tests pass locally
- [ ] No unrelated changes included
- [ ] Commit messages follow conventional format
- [ ] Documentation updated if needed
- [ ] No merge conflicts with main branch
- [ ] CI checks are passing

### 👥 Reviewers
<!-- Tag relevant reviewers -->
@copilot <!-- Auto-assigned via CODEOWNERS -->
