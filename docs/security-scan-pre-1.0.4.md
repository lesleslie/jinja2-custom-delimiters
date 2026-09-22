# Security scan findings (pre-1.0.4)

## osv-scanner fast-hook (crackerjack)

**Status:** Not a real CVE — false positive.

**What was reported:** `osv-scanner :: FAILED | 5.03s | issues=1`

**Investigation:** Crackerjack's `osv-scanner` hook runs:

```
osv-scanner scan source --lockfile=uv.lock --format=json
```

This expects a Python `uv.lock` lockfile. `jinja2-custom-delimiters` is a
JetBrains plugin built with Gradle — there is no `uv.lock` and the project
has no Python dependencies. `osv-scanner` reports a single "issue" because
it cannot resolve the `--lockfile=uv.lock` path.

**Direct reproduction:**

```
$ osv-scanner scan source --lockfile=uv.lock --format=table
failed to resolve path: stat .../jinja2-custom-delimiters/uv.lock: no such file or directory
```

**No real CVE was identified.** `osv-scanner` does not natively support
Gradle lockfile scanning, so it cannot introspect this project's Kotlin /
Java / IntelliJ Platform dependencies.

**Resolution:** Defer to post-1.0.4 (not blocking paid-plugin release).

The fast-hook is failing on missing-lockfile noise, not on a real
vulnerability. A follow-up could either (i) generate a Gradle dependency
lockfile that `osv-scanner` can scan, (ii) override crackerjack's
`osv-scanner` tool command to target a Kotlin lockfile format, or
(iii) file a crackerjack issue to make the lockfile path
project-type-aware.

## format-json / check-json / uv-lock / mdformat fast-hooks

**Status:** Not applicable.

These four fast-hooks are Python-ecosystem tooling that hard-fails on a
Kotlin/Gradle project:

- `format-json` (65 issues) — pre-existing formatter noise from
  non-canonical JSON files in the repo.
- `check-json` (65 issues) — same JSON files.
- `uv-lock` (1 issue) — same missing-`uv.lock` as osv-scanner above.
- `mdformat` (1 issue) — confirmed pre-existing on Task 2 base
  (`f6d3824`) via `git stash` round-trip.

None of these four hooks run as part of the `kotlin.ktlint`,
`kotlin.detekt`, or `kotlin.test` audit surface. Crackerjack's
hard-coded `osv-scanner` invocation ships a Python-flavored command
regardless of the project type; until crackerjack supports a
project-type-aware tool command override, these failures will surface on
every non-Python project. Verified pre-existing — not introduced by
Task 1, Task 2, or Task 3.

## Audit surface summary

The 3 mandatory hooks the release requires (`kotlin.ktlint`,
`kotlin.detekt`, `kotlin.test`) are wired but cannot run end-to-end
until the 4 unrelated Python-ecosystem fast-hooks are no longer in the
critical path. Crackerjack does not currently expose a config-level
mechanism for skipping individual hooks (see
`docs/CRACKERJACK_HOOK_DISABLE_LIMITATION.md` for details; this is a
crackerjack schema gap, not a project issue).