# Releasing jinja2-custom-delimiters

Canonical 11-phase release process. Follow phases in order.

## Phase 0 — Toolchain prep

- Bump `intelliJPlatform` Gradle plugin in `gradle/libs.versions.toml` to latest 2.x
- Bump `gradleVersion` in `gradle.properties` to match on-disk wrapper
- Bump `foojay-resolver-convention` in `settings.gradle.kts` for Java metadata
- Run `./gradlew help --configuration-cache` — must succeed

## Phase 1 — Audit ingestion

- Read `AUDIT-SUMMARY.md`, `CRITICAL-AUDIT-REPORT-2025.md`, `FIXES-COMPLETED.md`, `TEST-FIXES-SUMMARY.md`
- Read full commit log since last release
- Verify which open criticals still apply

## Phase 2 — Repo hygiene

- Add runtime artifact patterns to `.gitignore`
- Delete `.github/workflows/*`, `.github/dependabot.yml`, `codecov.yml`
- Keep `.github/FUNDING.yml`

## Phase 3 — Crackerjack wire-up

- Apply ktlint + detekt Gradle plugins
- Create `.crackerjack.toml`
- Verify all 3 hooks emit via `mcp__crackerjack__kotlin_list_hooks`

## Phase 4 — Implementation

- 4a: Java toolchain bump
- 4b: plugin.xml `<product-descriptor>` + `<change-notes>`
- 4c: Tech-debt sweep
- 4d: License silent-fallback removal
- 4e: New tests
- 4f: Docs update

## Phase 5 — Plugin Verifier

- `./gradlew verifyPlugin` against all available PyCharm Professional builds
- Manually inspect each `build/reports/pluginVerifier/index.html`

### Crackerjack audit gate limitations

The crackerjack `kotlin.ktlint`, `kotlin.detekt`, `kotlin.test` audit hooks
cannot run end-to-end because crackerjack currently lacks per-hook
disabling. Four Python-ecosystem fast_hooks (`format-json`, `check-json`,
`mdformat`, `uv-lock`, `osv-scanner`) fail on this Gradle project before
the 3 mandatory hooks run. This is a crackerjack upstream gap, not a
project issue.

The audit gate is downgraded from "blocking" to "soft check". Substantive
validation (`./gradlew test`, `./gradlew verifyPlugin`, Plugin Verifier HTML
inspection, manual sandbox QA) remains the load-bearing gate.

## Phase 6 — Manual sandbox QA

- `./gradlew runIdeForUiTests`
- 6-checklist items (open file, settings, save, format, restart, hard-fail)

## Phase 7 — Pack + archive

- `./gradlew buildPlugin`
- Archive `.zip` to `~/.mahavishnu/artifacts/<plugin>/<version>/`
- `shasum -a 256` the archive

## Phase 8 — Pre-publish verification

- `crackerjack run` exit 0 (soft check; see Phase 5 limitation)
- `./gradlew test` exit 0
- `./gradlew verifyPlugin` exit 0 (re-run)

## Phase 9 — MCP auth preflight + version bump

- Verify `MAHAVISHNU_AUTH_ENABLED=true`, `MAHAVISHNU_JWT_SECRET`, `MAHAVISHNU_PROJECT_ROOTS` registration
- Operator explicitly consents to `git push` (per `feedback-bodai-push-is-user-controlled.md`)
- `mcp__crackerjack__kotlin_bump_version(level="patch", project_root=..., dry_run=true)`
- Verify proposed version
- `dry_run=false`

## Phase 10 — Final publish

- `./gradlew publishPlugin`
- Confirm in JetBrains Marketplace admin panel
- Verify Marketplace-side compatibility range matches `sinceBuild`/`untilBuild`

## Phase 11 — Post-publish first-week monitor

- Check GitHub Issues for criticals filed against the new release
- Verify Marketplace install on PyCharm 2026.3
- If criticals filed: hotfix `<next-version>` → same pipeline
