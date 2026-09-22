# jinja2-custom-delimiters 1.0.4 — Pre-update Audit Summary

Generated: 2026-09-21

This audit was generated as Phase 1 of the v1.0.4 update plan (Tasks 0–16). It
confirms which CRITICAL items from the December 2025 audit have been closed in
the v1.0.1 / v1.0.3 development cycle, documents the one HIGH item still open
for this release, and verifies the `com.intellij.modules.python` module
dependency remains valid for PyCharm 2026.x.

## Sources reviewed

| Document | Purpose |
|----------|---------|
| `AUDIT-SUMMARY.md` | Dec 2025 high-level audit re-evaluation (status: READY FOR RELEASE) |
| `CRITICAL-AUDIT-REPORT-2025.md` | Dec 2025 detailed audit with CRITICAL/HIGH/MEDIUM/LOW findings |
| `FIXES-COMPLETED.md` | Dec 27, 2025 fix report — the v1.0.1 commit narrative |
| `TEST-FIXES-SUMMARY.md` | Dec 27, 2025 test-suite fix report — 11/11 tests passing |
| `git log 2bbc0f8..HEAD` | 9 commits on top of v1.0.1 (Dec 27, 2025 → Sep 21, 2026) |

Note: there is no `v1.0.1` git tag. The v1.0.1 release corresponds to commit
`2bbc0f8` ("version 1.0.1", 2025-12-27). The `git log` range used below is
`2bbc0f8..HEAD`. Likewise, the v1.0.3 release was the commit `0731363`
("bump version", 2026-01-20) — no `v1.0.3` tag was created.

## Closed criticals (verified by `git show`)

| ID | Item | Closed in | Evidence |
|----|------|-----------|----------|
| CRITICAL #3 | Test field-access → getters/setters (76 conversions in `Jinja2DelimitersSettingsTest.java`) | `2bbc0f8` | `git show 2bbc0f8 -- .../Jinja2DelimitersSettingsTest.java` shows all 11 test methods converted from `settings.blockStartString` → `settings.setBlockStartString()` / `settings.getBlockStartString()` |
| CRITICAL #4 | Live templates referencing non-existent `JINJA2_CUSTOM` context | `2bbc0f8` | `git show --stat 2bbc0f8` shows `src/main/resources/liveTemplates/Jinja2.xml` deleted (-148 lines) |
| CRITICAL #6 | Obsolete test files for removed features | `2bbc0f8` | `git show --stat 2bbc0f8` shows three test files deleted: `lexer/CustomJinja2LexerTest.java` (-300), `parser/CustomJinja2ParserTest.java` (-379), `integration/PluginIntegrationTest.java` (-335) |

> **Correction to plan brief**: the implementation plan brief stated that
> CRITICAL #3 was closed in commit `e0ae8af` (April 2026). The audit shows
> CRITICAL #3 was actually closed in `2bbc0f8` (Dec 2025, the v1.0.1 commit
> itself) alongside CRITICAL #4 and #6. Commit `e0ae8af` is the
> "tighten paid-license enforcement and metadata" work (introducing
> `LicenseGate`, `MarketplaceLicenseChecker`, `DelimiterConversionUtil`) and
> does not contain the test getter/setter conversion.

## Open items addressed by this release

### HIGH #6 — Inconsistent delimiter null-handling (STILL OPEN)

**Locations** (current code as of `HEAD`):

- `src/main/java/com/wedgwoodwebworks/jinja2customdelimiters/settings/Jinja2DelimitersSettings.java`:
  - Lines 47–83: All 8 getters still use the pattern
    `return field != null ? field : "default";`. The CHANGELOG entry for
    v1.0.3 ("Rejected null settings values consistently in the persistent
    settings service") refers to **setter** null rejection (lines 87–115
    use `requireNonNull(...)`), not the getter null-coalescing inconsistency.
  - Lines 130–132: `safeEquals(expected, actual)` returns `true` when
    `actual == null` — i.e., null is treated as matching the default.
    This is inconsistent with the getters, which return the default string
    when the field is null.
  - Combined effect: when a field is null, `getBlockStartString()` returns
    `"{%"`, but `isUsingCustomDelimiters()` treats null as "using defaults".
- `src/main/java/com/wedgwoodwebworks/jinja2customdelimiters/licensing/LicenseGate.java`:
  - Lines 14, 19: `return licensed == null || licensed;` — null license state
    is treated as "licensed". This file was introduced in `e0ae8af`
    (April 2026), AFTER v1.0.3, so its null-handling was never audited.
  - Lines 18–19 use the same pattern in a different method.

**Plan allocation**:

- Task 6 fixes the `Jinja2DelimitersSettings` half (initialize fields with
  defaults, drop the null-coalescing getters, delete `safeEquals`/`safeStringEmpty`).
- Task 7 fixes the `LicenseGate` half (treat null license state as
  unknown / unverified, not as licensed).

## Module dependency check

**`<depends>com.intellij.modules.python</depends>` at `src/main/resources/META-INF/plugin.xml:33`**

- Verified against JetBrains' official IntelliJ Platform SDK documentation
  page ("Plugin Compatibility", dated 2026-04-02 — most recent snapshot
  available). The page still lists `com.intellij.modules.python` as a valid
  module dependency providing "Python language PSI Model, Inspections,
  Intentions, Completion, Refactoring, Test Framework." No rename was found
  in available documentation.
- Web search for "com.intellij.modules.python PyCharm 2026.3 module" returned
  no evidence of a rename. The JetBrains SDK page warns "not all products
  define and declare modules" — implying it is possible the module is dropped
  or renamed in a future PyCharm build, but no specific change for 2026.3
  was found.
- **Resolution**: assume the module ID is still `com.intellij.modules.python`.
  Task 1 must re-verify by either (a) opening `plugin.xml` in PyCharm 2026.x
  and using `<depends>` code completion to confirm the ID is offered, or
  (b) inspecting the downloaded `PY-263.*` IDE artifacts for the module
  declaration. If a rename is discovered at that point, edit
  `plugin.xml:33` to use the new ID.

## Verifier matrix availability

`build.gradle.kts:109-119` declares:

```kotlin
pluginVerification {
    ides {
        select {
            types = listOf(IntelliJPlatformType.PyCharmProfessional)
            channels = listOf(ProductRelease.Channel.RELEASE)
            sinceBuild = providers.gradleProperty("pluginSinceBuild").get()
            untilBuild = providers.gradleProperty("pluginUntilBuild").get()
        }
    }
}
```

with `pluginSinceBuild = 252` and `pluginUntilBuild = 253.*` in `gradle.properties`.

Local artifact cache at `.intellijPlatform/localPlatformArtifacts/`:

| Build | Cached locally? | Notes |
|-------|------------------|-------|
| PY-243.26574.90 (2024.3) | Yes | Pre-v1.0.1 era, irrelevant for 1.0.4 |
| PY-252.23892.439 (2025.2) | Yes | Lower bound of the new range |
| PY-252.25557.178 (2025.2.x patch) | Yes | Lower bound of the new range |
| PY-253.* (2025.3) | No | Will be fetched at Task 10 verifyPlugin time |
| PY-261.* (2026.1) | No | Will be fetched at Task 10 verifyPlugin time |
| PY-262.* (2026.2) | No | Will be fetched at Task 10 verifyPlugin time |
| PY-263.* (2026.3) | No | Will be fetched at Task 10 verifyPlugin time |

The IntelliJ Platform Gradle Plugin (declared in `build.gradle.kts`) handles
fetching from JetBrains' CDN on first build. Local cache absence is **not** a
blocker for Task 0; the matrix availability for Task 10 is confirmed by
gradle.properties / build.gradle.kts configuration rather than local cache
state.

For PyCharm 2026.3 availability: the PyCharm 2026.3 EAP cycle began
2026-02-03 (per the JetBrains blog); today's date is 2026-09-21. EAP-to-stable
gaps are typically 3–4 months, so PyCharm 2026.3 should be in RELEASE channel
by the time Task 10 runs. If 2026.3 is not yet stable when Task 10 runs, the
plan's fallback (drop PY-263 from `pluginVerification.ides.select`) applies.

## Summary

- All three open CRITICAL items from the Dec 2025 audit are **closed** (in
  `2bbc0f8`).
- The one HIGH item the plan addresses — HIGH #6 null-handling — is
  **confirmed open** in both `Jinja2DelimitersSettings` and `LicenseGate`.
  Plan tasks 6 and 7 are correctly scoped to fix this.
- The `com.intellij.modules.python` module dependency is **still valid**
  per JetBrains documentation; Task 1 should re-verify against the actual
  PyCharm 2026.x build.
- Verifier matrix is configured for `sinceBuild = 252 / untilBuild = 253.*`;
  Task 10 will widen this to `252 / 2026.x` and fetch the 2026.x builds
  on first run.

**Audit verdict**: ready to proceed to Task 1 (implementation phase).
