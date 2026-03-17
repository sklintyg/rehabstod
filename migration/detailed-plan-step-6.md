# Step 6 — Inline `security-filter` + `security-siths`

## Problem Statement

The `web` module depends on two infra JARs for servlet filters and authentication exceptions:
- `se.inera.intyg.infra:security-filter` — 5 filter classes (4 used by rehabstod)
- `se.inera.intyg.infra:security-siths` — 3 exception classes (already used via `RehabstodUserDetailsService`)

> **Note from Step 5:** The `BaseUserDetailsService` + `DefaultUserDetailsDecorator` inlining that
> was originally planned for this step was **pulled forward into Step 5** due to type
> incompatibility. `RehabstodUserDetailsService` no longer extends `BaseUserDetailsService`.
> This step therefore only needs to handle **filters** and **exceptions**.

The infra JARs remain on the classpath — they will be removed in Step 11.

---

## Current State (what was done before)

From `step-5-summary.md`:
- `RehabstodUserDetailsService` is already fully inlined — no longer extends `BaseUserDetailsService`
- It still imports 3 exception classes from `se.inera.intyg.infra.security.exception.*`
- All filter classes are still imported from `se.inera.intyg.infra.security.filter.*`

---

## Target Package Structure

```
web/src/main/java/se/inera/intyg/rehabstod/security/
  filter/            ← 4 filter classes from infra security-filter
  exception/         ← 3 exception classes from infra security-siths

web/src/test/java/se/inera/intyg/rehabstod/security/
  filter/            ← 2 test files from infra security-filter
```

---

## Files to Copy

### From `security-filter` (4 of 5 — `InternalApiFilter` is NOT used in rehabstod)

| Source file | Target package | Notes |
|---|---|---|
| `SessionTimeoutFilter.java` | `rehabstod.security.filter` | Extends `OncePerRequestFilter`. Used in `ApplicationInitializer` and referenced by `SessionStatusController` for `SECONDS_UNTIL_SESSIONEXPIRE_ATTRIBUTE_KEY` |
| `RequestContextHolderUpdateFilter.java` | `rehabstod.security.filter` | Extends `OncePerRequestFilter`. Used in `ApplicationInitializer` |
| `PrincipalUpdatedFilter.java` | `rehabstod.security.filter` | Extends `OncePerRequestFilter`. Bean in `ApplicationConfig`, registered via `DelegatingFilterProxy` in `ApplicationInitializer` |
| `SecurityHeadersFilter.java` | `rehabstod.security.filter` | Extends `OncePerRequestFilter`. Parent class of `RSSecurityHeadersFilter` |

### From `security-siths` exceptions (3 files)

| Source file | Target package | Notes |
|---|---|---|
| `HsaServiceException.java` | `rehabstod.security.exception` | Extends `AuthenticationException`. Used in `RehabstodUserDetailsService` and `CustomAuthenticationFailureHandler` |
| `MissingHsaEmployeeInformation.java` | `rehabstod.security.exception` | Extends `AuthenticationException`. Used in `RehabstodUserDetailsService` |
| `MissingMedarbetaruppdragException.java` | `rehabstod.security.exception` | Extends `AuthenticationException`. Used in `RehabstodUserDetailsService` and `CustomAuthenticationFailureHandler` |

### Test files from `security-filter` (2 files)

| Source file | Target package | Notes |
|---|---|---|
| `SessionTimeoutFilterTest.java` | `rehabstod.security.filter` | 8 test methods, uses Mockito |
| `PrincipalUpdatedFilterTest.java` | `rehabstod.security.filter` | 3+ test methods, includes inner `SomeUser` test class |

---

## Files That Need Import Updates

### Filter imports (`se.inera.intyg.infra.security.filter.*` → `se.inera.intyg.rehabstod.security.filter.*`)

| File | Imports to change |
|---|---|
| `ApplicationInitializer.java` | `RequestContextHolderUpdateFilter`, `SessionTimeoutFilter` |
| `ApplicationConfig.java` | `PrincipalUpdatedFilter` |
| `RSSecurityHeadersFilter.java` | `SecurityHeadersFilter` |
| `SessionStatusController.java` | `SessionTimeoutFilter` (for constant reference) |

### Exception imports (`se.inera.intyg.infra.security.exception.*` → `se.inera.intyg.rehabstod.security.exception.*`)

| File | Imports to change |
|---|---|
| `RehabstodUserDetailsService.java` | `HsaServiceException`, `MissingHsaEmployeeInformation`, `MissingMedarbetaruppdragException` |
| `CustomAuthenticationFailureHandler.java` | `MissingMedarbetaruppdragException`, `HsaServiceException` |
| `RehabstodUserDetailsServiceTest.java` | All 3 exceptions (check actual usage) |

---

## Sub-steps

### Sub-step 1 — Create target directories and copy filter classes (4 files)

Copy from `C:\GIT\Inera\Intyg\infra\security\filter\src\main\java\se\inera\intyg\infra\security\filter\`:
- `SessionTimeoutFilter.java`
- `RequestContextHolderUpdateFilter.java`
- `PrincipalUpdatedFilter.java`
- `SecurityHeadersFilter.java`

Into: `web/src/main/java/se/inera/intyg/rehabstod/security/filter/`

Update package declarations: `se.inera.intyg.infra.security.filter` → `se.inera.intyg.rehabstod.security.filter`

**Do NOT copy `InternalApiFilter.java`** — it is not used in rehabstod.

### Sub-step 2 — Copy exception classes (3 files)

Copy from `C:\GIT\Inera\Intyg\infra\security\siths\src\main\java\se\inera\intyg\infra\security\exception\`:
- `HsaServiceException.java`
- `MissingHsaEmployeeInformation.java`
- `MissingMedarbetaruppdragException.java`

Into: `web/src/main/java/se/inera/intyg/rehabstod/security/exception/`

Update package declarations: `se.inera.intyg.infra.security.exception` → `se.inera.intyg.rehabstod.security.exception`

### Sub-step 3 — Copy test files (2 files)

Copy from `C:\GIT\Inera\Intyg\infra\security\filter\src\test\java\se\inera\intyg\infra\security\filter\`:
- `SessionTimeoutFilterTest.java`
- `PrincipalUpdatedFilterTest.java`

Into: `web/src/test/java/se/inera/intyg/rehabstod/security/filter/`

Update package declarations and all infra security imports to local.

### Sub-step 4 — Replace imports in existing rehabstod files

Use Python (see encoding rule in `step-5-summary.md`) for reliable UTF-8 replacement:

```
se.inera.intyg.infra.security.filter.  →  se.inera.intyg.rehabstod.security.filter.
se.inera.intyg.infra.security.exception.  →  se.inera.intyg.rehabstod.security.exception.
```

**Expected files affected** (~7 main + test files):
- `ApplicationInitializer.java`
- `ApplicationConfig.java`
- `RSSecurityHeadersFilter.java`
- `SessionStatusController.java`
- `RehabstodUserDetailsService.java`
- `CustomAuthenticationFailureHandler.java`
- `RehabstodUserDetailsServiceTest.java` (and any other test files referencing exceptions)

### Sub-step 5 — Build and verify

Run: `.\gradlew.bat test --no-daemon`

Expected: BUILD SUCCESSFUL with all tests passing.

**Note:** The current Step 5 has 11 failing tests due to YAML encoding corruption and
`RehabstodUserDetailsServiceTest` mock mismatches. These are pre-existing failures from Step 5 and
are unrelated to Step 6. Step 6 should not introduce any NEW failures beyond those 11.

### Sub-step 6 — Create `step-6-summary.md`

After completing all sub-steps, create a summary document at `migration/step-6-summary.md`
following the same pattern as `step-5-summary.md`. The summary should include:
- What was done (files copied, imports changed)
- What was intentionally skipped or deferred
- Any difficulties or unexpected issues encountered during implementation
- Any technical decisions that affect future steps
- The final test results
- Outstanding issues (if any)
- Updated list of remaining infra imports after this step

### Sub-step 7 — Update `deferred-items.md`

Add an entry for Step 6 noting:
- `BaseUserDetailsService` inlining was already done in Step 5 (not repeated)
- `InternalApiFilter` was intentionally not copied (not used in rehabstod)

---

## Complexity / Risk Notes

- **Very low risk** — This step copies simple, self-contained classes with no complex
  interdependencies. The filter classes extend `OncePerRequestFilter` (Spring framework) and the
  exception classes extend `AuthenticationException` (Spring Security). No infra-to-infra
  cross-references exist in these files.

- **`RSSecurityHeadersFilter extends SecurityHeadersFilter`**: After copying, this extends
  the LOCAL `SecurityHeadersFilter` instead of infra's. The behaviour is identical since the
  local copy is an exact replica.

- **No configuration changes needed**: Unlike Step 5, there are no `@ComponentScan` or `@Bean`
  changes required. `PrincipalUpdatedFilter` is already instantiated as a bean in
  `ApplicationConfig`; after the import update it will reference the local class instead.

- **Encoding**: Use Python for all file writes (per Step 5 lesson). Do NOT use PowerShell
  `Set-Content` or `[System.IO.File]::WriteAllText`.

- **Pre-existing test failures from Step 5**: 11 tests are currently failing. Step 6 must not
  introduce additional failures. Track both pre-existing and new failures separately in the
  summary.

---

## Files Changed Summary

| Change | Count |
|--------|-------|
| New main Java files created | 7 (4 filters + 3 exceptions) |
| New test Java files created | 2 |
| Existing files with import updates | ~7 |
| **Total files touched** | **~16** |
