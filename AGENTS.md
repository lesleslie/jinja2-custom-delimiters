# Repository Guidelines

## Project Structure & Module Organization

- `src/main/java/com/wedgwoodwebworks/jinja2customdelimiters/`: plugin implementation (format processors, settings, services).
- `src/main/resources/META-INF/plugin.xml`: plugin manifest and registrations.
- `src/main/resources/icons/`: plugin assets.
- `src/test/java/` and `src/test/resources/`: IntelliJ Platform tests and test fixtures.
- `gradle/`, `build.gradle.kts`, `gradle.properties`, `settings.gradle.kts`: Gradle and IntelliJ Platform build configuration.
- `assets/` and `build/`: design assets and build outputs (do not commit generated build output).

## Build, Test, and Development Commands

- `./gradlew build`: compiles, runs tests, and assembles the plugin.
- `./gradlew test`: runs the IntelliJ Platform test suite.
- `./gradlew runIde`: launches a sandbox IDE for manual testing.
- `./gradlew runIdeForUiTests`: launches IDE with robot-server settings for UI tests.

## Coding Style & Naming Conventions

- **Java 25 toolchain (Azul) is REQUIRED** (was Java 21; bumped in 2026.2.1 for 2026.2 platform). `foojay-resolver-convention` (>= 1.1.0) auto-provisions Azul Zulu 25.
- Use IntelliJ IDEA’s default Java code style (4-space indentation, braces on the same line, wrap as IntelliJ formats by default).
- Keep package names under `com.wedgwoodwebworks.jinja2customdelimiters` and mirror existing naming (e.g., `Jinja2Delimiters...`).
- No formatter or linter is configured; keep changes minimal and consistent with nearby code.

## Testing Guidelines

- Tests use the IntelliJ Platform test framework (`BasePlatformTestCase`).
- Place new tests in `src/test/java` and name them `*Test.java`.
- Run `./gradlew test` before submitting changes.
- No explicit coverage threshold is enforced; add tests for new behavior and edge cases.

## Commit & Pull Request Guidelines

- Commit history shows short, imperative messages such as `Bump ...` and release commits like `version 1.0.1`.
- Use concise subject lines; use `version x.y.z` for release bumps.
- PRs should include a clear summary, testing notes (commands run), and linked issues. Add screenshots or GIFs if UI or settings screens change.
- Update `CHANGELOG.md` for user-visible changes; it is used for release notes.

## Security & Configuration Tips

- Publishing/signing expects environment variables: `CERTIFICATE_CHAIN`, `PRIVATE_KEY`, `PRIVATE_KEY_PASSWORD`, and `PUBLISH_TOKEN`.
- Avoid committing secrets; use local environment configuration when publishing.

## Operational Notes

### Marketplace signing certificate renewal

- `CERTIFICATE_CHAIN` / `PRIVATE_KEY` / `PRIVATE_KEY_PASSWORD` are issued for **3 years** per JetBrains Marketplace policy.
- Rotate before expiry or publish will block.
- Renewal cadence reminder: set a calendar event 60 days before expiry.

### `PUBLISH_TOKEN` rotation

- Rotate annually.

### MCP tool prerequisites for `mcp__crackerjack__kotlin_bump_version`

- `MAHAVISHNU_AUTH_ENABLED=true`
- `MAHAVISHNU_JWT_SECRET` set (32+ chars)
- The plugin repo path must be registered in `MAHAVISHNU_PROJECT_ROOTS` (one-time per project)
- The tool raises `PermissionError` otherwise — check env vars before invoking
