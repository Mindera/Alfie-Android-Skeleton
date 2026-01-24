# Copilot Instructions for Alfie-Android-Skeleton

This file serves as the main entry point for Copilot instructions. The detailed guidelines have been organized into specialized agent files based on roles and responsibilities.

## Specialized Agent Instructions

For detailed guidelines specific to your role, please refer to the appropriate agent file:

### 1. [Architect & Developer Agent](agents/architect-developer.md)
**Role**: Architectural decisions, code development, and coding standards

**Covers**:
- Project overview and technology stack
- Code style and conventions (Kotlin, Android)
- Architecture patterns (MVVM, Clean Architecture)
- App modularization strategy and module types
- Module dependency rules and best practices
- Resource naming conventions
- Dependency management (TOML Version Catalog)
- Navigation patterns (Jetpack Navigation Compose)
- Build and development workflow
- Git workflow
- Prohibited actions
- Best practices (security, performance, accessibility, localization, documentation)

### 2. [Tester Agent](agents/tester.md)
**Role**: Quality assurance through comprehensive testing

**Covers**:
- Testing philosophy and standards
- Module testing strategy (unit, integration, instrumented tests)
- Test structure and organization
- Compose UI testing guidelines
- Navigation testing
- Running tests (commands and tools)
- Code quality checks (linting, formatting)
- Test coverage guidelines
- Testing best practices and common pitfalls
- Debugging tests
- Test data management

### 3. [Metrics Analyst Agent](agents/metrics-analyst.md)
**Role**: Metrics collection, analysis, and reporting

**Covers**:
- Code quality metrics (coverage, static analysis)
- Build metrics (performance, success rate)
- Test metrics (execution, distribution)
- Code churn metrics
- Dependency metrics (health, module dependencies)
- APK/Bundle metrics (size, method count)
- Performance metrics (runtime, Compose)
- Git metrics (commits, pull requests)
- Productivity and quality metrics
- Reporting and dashboards
- Using metrics for decision making
- Continuous improvement processes

### 4. [TechWriter Agent](agents/techwriter.agent.md)
**Role**: Technical documentation creation and maintenance

**Covers**:
- Documentation philosophy and standards
- Project documentation structure (README, SETUP, CONTRIBUTING, etc.)
- Code documentation (KDoc) guidelines and best practices
- Module documentation requirements
- Documentation best practices (clarity, examples, versioning)
- Documentation workflow and maintenance
- When and how to document code
- Visual aids and diagrams
- Documentation anti-patterns
- Keeping documentation in sync with code

### 5. [CI/CD Pipeline Agent](agents/automation.agent.md)
**Role**: Continuous Integration and Deployment automation

**Covers**:
- GitHub Actions workflows and automation
- Build pipeline configuration and optimization
- Test automation in CI/CD (unit, UI, snapshot tests)
- Deployment strategies (Play Store, Firebase)
- Artifact management and caching
- Secrets management and security
- Workflow troubleshooting and debugging
- Performance optimization
- Monitoring and metrics collection

## Quick Reference

### Technology Stack Summary
- **Platform**: Android
- **Language**: Kotlin 2.3.0
- **Build System**: Gradle (AGP 8.13.2)
- **UI Framework**: Jetpack Compose (no XML layouts)
- **Minimum SDK**: 26
- **Target SDK**: 36

### Core Principles
- **Architecture**: MVVM with Clean Architecture
- **Modularization**: Hybrid (feature-based + layer-based)
- **Navigation**: Jetpack Navigation Compose with type-safe routes
- **Dependency Injection**: Hilt
- **Testing**: Comprehensive unit, integration, and UI tests
- **Quality**: Automated linting, formatting, and metrics

## Getting Started

1. **For Development Tasks**: Start with [Architect & Developer Agent](agents/architect-developer.md)
2. **For Testing Tasks**: Start with [Tester Agent](agents/tester.md)
3. **For Metrics and Analysis**: Start with [Metrics Analyst Agent](agents/metrics-analyst.md)
4. **For Documentation Tasks**: Start with [TechWriter Agent](agents/techwriter.agent.md)
5. **For CI/CD and Automation**: Start with [CI/CD Pipeline Agent](agents/automation.agent.md)

## Contributing

When making changes:
1. Follow the development workflow in the Architect & Developer guide
2. Write tests as outlined in the Tester guide
3. Monitor metrics as described in the Metrics Analyst guide
4. Update relevant agent documentation if guidelines change
5. Use conventional commits format for commit messages

## Additional Notes
- Prioritize clean, readable code over clever solutions
- When in doubt, follow the principle of least surprise
- Keep the codebase simple and maintainable for future developers
- Each agent file contains comprehensive, role-specific guidance
- Consult multiple agent files when tasks span multiple domains
