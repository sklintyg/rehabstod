# Step 5 Summary — Inline `security-common` + `security-authorities`

## Status: Partially Complete — Tests Failing

Build compiles: ✅  
Tests passing: ~591 / 602  
Tests failing: 11 (see section below)  
Committed: ❌ (not yet committed)

---

## What Was Done

### 1. Copied 32 main Java files into local packages

All files from infra's `security-common` and `security-authorities` modules were copied into:

```
web/src/main/java/se/inera/intyg/rehabstod/security/
  common/
    model/         ← 16 files (IntygUser, Role, Privilege, Feature, etc.)
    service/       ← 2 files  (AuthenticationLogger, CareUnitAccessHelper)
    cookie/        ← 1 file   (IneraCookieSerializer)
    exception/     ← 1 file   (GenericAuthenticationException)
  authorities/
    (root)         ← 8 files  (AuthoritiesConfiguration, CommonAuthoritiesResolver, etc.)
    bootstrap/     ← 1 file   (SecurityConfigurationLoader)
    validation/    ← 3 files  (AuthoritiesValidator, AuthExpectationSpecImpl, AuthExpectationSpecification)
```

Package declarations updated from `se.inera.intyg.infra.security.*` to
`se.inera.intyg.rehabstod.security.*` throughout.

### 2. Copied 5 test Java files from infra

```
web/src/test/java/se/inera/intyg/rehabstod/security/
  authorities/     ← AuthoritiesHelperTest, AuthoritiesResolverTest,
                      CommonAuthoritiesResolverTest, FeaturesHelperTest
  common/cookie/   ← IneraCookieSerializerTest
```

Two additional YAML fixtures for these tests were created (infra's YAML content):
- `web/src/test/resources/AuthoritiesConfigurationLoaderTest/security-authorities-test.yaml`
- `web/src/test/resources/AuthoritiesConfigurationLoaderTest/security-features-test.yaml`

The copied tests reference these `security-*` files rather than the rehabstod-specific originals,
so the two sets of tests remain isolated.

### 3. Inlined BaseUserDetailsService + DefaultUserDetailsDecorator (pulled forward from Step 6)

`RehabstodUserDetailsService` was originally `extends BaseUserDetailsService`. This caused a type
incompatibility because `RehabstodUser extends (local) IntygUser` but `BaseUserDetailsService`
returns `(infra) IntygUser`. The class was completely rewritten to inline the logic:

- No longer extends anything
- All fields `@Autowired` directly
- All methods from `BaseUserDetailsService` and `DefaultUserDetailsDecorator` inlined
- `setCommonAuthoritiesResolver()` setter kept (needed by `RehabstodUserDetailsServiceTest`)
- Uses local `se.inera.intyg.rehabstod.security.*` types throughout

**This means Step 6 (`BaseUserDetailsService` inlining) is already done — do not repeat it.**

### 4. Replaced infra security imports in ~45 existing files

All occurrences of:
- `se.inera.intyg.infra.security.common.*`
- `se.inera.intyg.infra.security.authorities.*`

were replaced with the local equivalents in `web/src/main` and `web/src/test`.

Modified files include: `RehabstodUser`, `RehabstodUnitChangeService`, `UserServiceImpl`,
`FeatureServiceImpl`, `BaseExportService`, `GetUserResponse`, `AuthoritiesValidator` and ~38
test files.

### 5. Configuration changes

**`SecurityConfig.java`** — removed `@ComponentScan("se.inera.intyg.infra.security.authorities")`.
Local beans are now auto-discovered by the root component scan of `se.inera.intyg.rehabstod`.

**`ApplicationConfig.java`** — removed duplicate `cookieSerializer()` bean. The bean definition
was present in both `ApplicationConfig` and `WebSecurityConfig`; the latter was kept.

### 6. Build dependency

Added `implementation "org.yaml:snakeyaml:2.4"` to `web/build.gradle`. The
`SecurityConfigurationLoader` uses SnakeYAML directly for YAML parsing and the transitive
dependency was not guaranteed to be present.

---

## What Was Intentionally Left Using Infra Imports

These infra imports are **by design** and must NOT be replaced until the corresponding later step:

| Import prefix | Used in | When to inline |
|---|---|---|
| `se.inera.intyg.infra.integration.hsatk.*` | `IntygUser`, `CareUnitAccessHelper`, `CommonAuthoritiesResolver`, `RehabstodUserDetailsService` | Step that inlines HSA integration |
| `se.inera.intyg.infra.security.exception.*` (`HsaServiceException`, `MissingHsaEmployeeInformation`, `MissingMedarbetaruppdragException`) | `RehabstodUserDetailsService` | Step that inlines security-siths |

---

## Important Technical Decisions for Future Steps

### Encoding rule (CRITICAL)
**Always use Python for file writes, never PowerShell `Set-Content` or `[System.IO.File]::WriteAllText`.**

- PowerShell `Set-Content -NoNewline` on Windows PS 5.1 → writes UTF-16 LE (not UTF-8)
- `[System.IO.File]::WriteAllText(path, content)` → writes UTF-8 BOM
- Python `open(path, 'w', encoding='utf-8')` → correct UTF-8 without BOM

Use this Python template for any future bulk replacement:
```python
import os
replacements = [('old.package.', 'new.package.')]
for root, _, files in os.walk('web/src'):
    for f in files:
        if f.endswith('.java'):
            path = os.path.join(root, f)
            with open(path, 'r', encoding='utf-8-sig') as fp:
                content = fp.read()
            new = content
            for old, new_val in replacements:
                new = new.replace(old, new_val)
            if new != content:
                with open(path, 'w', encoding='utf-8', newline='') as fp:
                    fp.write(new)
```

### `BaseUserDetailsService` inlining (already done)
Step 6 originally planned to inline `BaseUserDetailsService` + `DefaultUserDetailsDecorator`.
This was **pulled forward into Step 5** due to type incompatibility. Step 6 should not redo this.
The `deferred-items.md` should be updated to note this.

### `UserOrigin` optional injection
`RehabstodUserDetailsService` declares:
```java
@Autowired(required = false)
private Optional<UserOrigin> userOrigin;
```
Spring injects `Optional.empty()` when no `UserOrigin` bean is present. This is safe — the code
calls `userOrigin.ifPresent(...)` without null checks.

### `setCommonAuthoritiesResolver()` setter
`RehabstodUserDetailsService` has a public setter `setCommonAuthoritiesResolver()` that exists
solely for test injection. Do not remove it — it is called directly by
`RehabstodUserDetailsServiceTest`.

### Two sets of YAML test fixtures
After this step there are two sets of YAML fixtures for security tests:

| File | Used by | Content |
|---|---|---|
| `authorities-test.yaml` | `RehabstodAuthoritiesResolverTest`, `SecurityConfigurationLoaderTest` | Rehabstod-specific: LAKARE + REHABKOORDINATOR, VISA_SJUKFALL only |
| `security-authorities-test.yaml` | `AuthoritiesHelperTest`, `AuthoritiesResolverTest`, `CommonAuthoritiesResolverTest` | Infra full set: 6 roles, 5 privileges, 3 origins, 3 intygstyper |

Similarly `features-test.yaml` (rehabstod-specific, SRS only) vs `security-features-test.yaml`
(infra full set with many features and pilots).

This separation must be preserved — do not merge them.
