# GitHub Copilot Custom Agents

This document explains how to configure and use custom GitHub Copilot agents in the Alfie Android Skeleton project.

## Overview

GitHub Copilot supports custom agents that can be tailored to specific tasks within your repository. These agents are defined using Markdown files stored in the `.github/agents/` directory and can provide specialized assistance for various development activities.

## Directory Structure

Custom agents for this project should be placed in:

```
.github/
└── agents/
    ├── docs-agent.md
    ├── android-expert.md
    ├── test-helper.md
    └── [your-agent].md
```

## Agent Configuration Format

Each agent is defined in a separate Markdown file with a YAML frontmatter section followed by detailed instructions.

### Basic Template

```markdown
---
name: agent_name
description: Brief description of the agent's purpose
target: github-copilot  # or vscode
tools:
  - read
  - search
  - edit
infer: true
metadata:
  owner: team-name
  version: "1.0"
---

# Agent Instructions

Your detailed instructions, guidelines, and examples go here.

## Goals
- Primary objective 1
- Primary objective 2

## Boundaries
❌ Things the agent should NOT do
✅ Things the agent SHOULD do

## Examples
[Provide concrete examples of expected behavior]
```

### YAML Frontmatter Fields

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `name` | string | Yes | Unique identifier for the agent |
| `description` | string | Yes | Brief summary of the agent's purpose |
| `target` | string | No | Context where agent runs (`vscode`, `github-copilot`) |
| `tools` | list/string | No | Tools the agent can use (`read`, `search`, `edit`, etc.) |
| `infer` | boolean | No | Whether Copilot can auto-select this agent |
| `metadata` | object | No | Additional annotations (owner, version, etc.) |

## Example Agents for Android Development

### 1. Android Architecture Expert

**File**: `.github/agents/android-expert.md`

```markdown
---
name: android_expert
description: Expert in Android development with MVVM and Jetpack Compose
tools:
  - read
  - search
  - edit
infer: true
metadata:
  owner: android-team
  version: "1.0"
---

# Android Architecture Expert

You are an expert Android developer specializing in MVVM architecture and Jetpack Compose.

## Primary Goals
- Maintain MVVM architecture patterns
- Use Jetpack Compose for all UI (no XML layouts)
- Follow Material Design 3 guidelines
- Ensure proper dependency injection with Hilt

## Tech Stack
- Kotlin 2.3.0
- Jetpack Compose (BOM 2026.01.00)
- Hilt 2.58
- Navigation Compose 2.9.6
- Coroutines 1.10.2
- Min SDK: 26
- Target SDK: 36

## Guidelines
✅ Use StateFlow for state management
✅ Keep ViewModels testable
✅ Use type-safe navigation with Kotlin Serialization
✅ Follow the Repository pattern

❌ Never use XML layouts
❌ Never hardcode strings (use string resources)
❌ Never block the main thread
❌ Never pass NavController to composables
```

### 2. Testing Expert

**File**: `.github/agents/test-helper.md`

```markdown
---
name: test_helper
description: Expert in Android testing (Unit, UI, and Snapshot tests)
tools:
  - read
  - search
  - edit
infer: true
metadata:
  owner: qa-team
  version: "1.0"
---

# Android Testing Expert

You specialize in creating comprehensive tests for Android applications.

## Testing Strategy
- **Unit Tests**: ViewModels and business logic (`src/test/`)
- **UI Tests**: Compose UI tests (`src/androidTest/`)
- **Snapshot Tests**: Paparazzi for visual regression

## Commands
```bash
# Unit tests
./gradlew testDebugUnitTest

# UI tests
./gradlew connectedDebugAndroidTest

# Snapshot tests
./gradlew recordPaparazziDebug
./gradlew verifyPaparazziDebug
```

## Guidelines
✅ Test ViewModels with StateFlow assertions
✅ Use Compose Testing for UI verification
✅ Mock dependencies with Hilt testing utilities
✅ Maintain high coverage for critical paths

❌ Never skip testing error cases
❌ Never write flaky tests
❌ Never test implementation details
```

### 3. Documentation Writer

**File**: `.github/agents/techwriter.agent.md`

```markdown
---
name: techwriter
description: Expert technical writer for Android project documentation
tools:
  - read
  - search
  - edit
infer: true
metadata:
  owner: docs-team
  version: "1.0"
---

# TechWriter Agent

You are a technical writer specializing in Android project documentation.

## Goals
- Maintain clear, accurate README and documentation
- Document architectural decisions
- Keep setup instructions up to date
- Write KDoc for public APIs

## Documentation Structure
- `README.md`: Project overview and getting started
- `SETUP.md`: Detailed setup instructions
- `CONTRIBUTING.md`: Contribution guidelines
- `PROJECT_SUMMARY.md`: Architecture and design decisions
- `.github/copilot-instructions.md`: Copilot guidance
- `.github/agents/*.agent.md`: Specialized agent documentation

## Documentation Best Practices
- Use clear, concise language
- Provide working code examples
- Include command-line snippets with expected output
- Specify exact versions for all dependencies
- Add visual aids (diagrams, tables, emojis) for better readability
- Keep documentation in sync with code changes
- Test all commands before documenting

## Style Guide
- Use emojis for section headers (📱, 🛠️, 📦, 🏗️, etc.)
- Format code blocks with language tags
- Use tables for technology stack and comparisons
- Link to official documentation and related files
- Keep examples current with latest patterns

## KDoc Guidelines
- Document all public APIs and interfaces
- Include parameter and return value descriptions
- Provide usage examples for complex functions
- Document exceptions that may be thrown

## Boundaries
❌ Never modify code files (except KDoc comments)
❌ Never commit incomplete documentation
❌ Never document deprecated patterns
✅ Always verify commands work before documenting
✅ Keep documentation in sync with code changes
✅ Update all affected docs when making changes
✅ Link to canonical sources instead of duplicating content
```

## Agent Activation

Agents can be activated in several ways:

1. **Automatic Inference**: If `infer: true` is set, Copilot will automatically select the agent when relevant
2. **Manual Selection**: Use `@agent_name` to explicitly invoke a specific agent
3. **Context-Based**: Copilot selects based on the current file/task context

## Best Practices

### 1. Clear Responsibilities
Define specific, non-overlapping responsibilities for each agent to avoid conflicts.

### 2. Explicit Commands
Include exact commands that should be run, especially for build/test/lint operations:

```markdown
## Commands
```bash
./gradlew lint
./gradlew testDebugUnitTest
./gradlew connectedDebugAndroidTest
```
```

### 3. Boundaries First
Put critical "do not do" items at the top to prevent mistakes:

```markdown
## Boundaries (Critical)
❌ Never commit secrets or API keys
❌ Never modify .gitignore to include build artifacts
❌ Never use XML layouts (Compose only)
```

### 4. Concrete Examples
Provide code examples rather than just descriptions:

```markdown
## Example: Correct ViewModel State Management

```kotlin
class MyViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
}
```
```

### 5. Version Specificity
Specify exact versions of tools and libraries:

```markdown
## Stack
- Kotlin: 2.3.0
- Compose BOM: 2026.01.00
- AGP: 8.13.2
- Target SDK: 36
- Min SDK: 26
```

## Integration with CI/CD

The `.github/workflows/auto-label.yml` workflow automatically labels PRs that modify agent configurations. Here's how it works:

```yaml
# Excerpt from .github/workflows/auto-label.yml
- name: Get changed files
  id: changed-files
  uses: tj-actions/changed-files@v41
  with:
    files_yaml: |
      documentation:
        - '**/*.md'
      ai-config:
        - '.github/copilot/**'
        - '.github/agents/**'
      tests:
        - '**/test/**/*.kt'
        - '**/androidTest/**/*.kt'
        - '**/*Test.kt'
        - '**/*Tests.kt'
        
- name: Add ai-config label
  if: steps.changed-files.outputs.ai-config_any_changed == 'true'
  uses: actions/github-script@v7
  with:
    script: |
      github.rest.issues.addLabels({
        owner: context.repo.owner,
        repo: context.repo.repo,
        issue_number: context.issue.number,
        labels: ['ai-config']
      })
```

When you modify files in `.github/agents/` or `.github/copilot/`, the PR will automatically receive the `ai-config` label for easy tracking and filtering.

## Existing Configuration

This project already has:
- **Copilot Instructions**: `.github/copilot-instructions.md` - Main entry point for Copilot guidance
- **Specialized Agents**:
  - `architect-developer.agent.md` - Architecture and development guidelines
  - `tester.agent.md` - Testing strategies and quality assurance
  - `metrics-analyst.agent.md` - Metrics collection and analysis
  - `techwriter.agent.md` - Documentation standards and practices
  - `automation.agent.md` - CI/CD pipelines and GitHub Actions automation
- **CODEOWNERS**: Code review assignment to `@copilot`

Custom agents complement these by providing task-specific expertise.

## Creating Your Own Agent

To create a new custom agent:

1. **Create the file**: `.github/agents/your-agent-name.md`
2. **Define frontmatter**: Specify name, description, tools, and metadata
3. **Write instructions**: Clear goals, boundaries, examples, and commands
4. **Test the agent**: Verify it works as expected with `@your-agent-name`
5. **Document it**: Add entry to this file under the Examples section

## Priority and Precedence

Agents follow this priority order:
1. **Project level**: `.github/agents/` (highest priority) ⭐
2. **Organization level**: `{org}/.github/agents/`
3. **User level**: `~/.copilot/agents/` (lowest priority)

If agents with the same name exist at multiple levels, the project-level agent takes precedence.

## Resources

For more information on GitHub Copilot custom agents:

- [GitHub Copilot Custom Agents Documentation](https://docs.github.com/en/copilot/reference/custom-agents-configuration) - Official GitHub documentation on custom agents configuration
- [How to Write a Great agents.md](https://github.blog/ai-and-ml/github-copilot/how-to-write-a-great-agents-md-lessons-from-over-2500-repositories/) - Best practices from GitHub's analysis of 2,500+ repositories
- [Project Copilot Instructions](.github/copilot-instructions.md) - This project's general Copilot guidelines

## Contributing to Agent Configuration

When modifying or adding agents:

1. Follow the template structure above
2. Be specific about tools, versions, and commands
3. Include concrete code examples
4. Define clear boundaries
5. Test the agent before committing
6. Update this documentation with new agents

---

**Note**: Custom agents are a powerful way to encode project-specific knowledge and best practices. Keep them focused, well-documented, and regularly updated as the project evolves.
