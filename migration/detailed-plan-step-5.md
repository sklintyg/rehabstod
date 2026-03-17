# Step 5 — Inline security models: `security-common` + `security-authorities`

## Problem Statement

The `web` module currently depends on two infra JARs for security models and authority resolution:
- `se.inera.intyg.infra:security-common` — 20 Java files (models, service interfaces, cookie, exception)
- `se.inera.intyg.infra:security-authorities` — 12 Java files (authorities resolver, config, helpers, validation)

This step copies all 32 main files and 5 infra test files locally into the `web` module, updates all
import references project-wide (45 existing files + the copied test files), removes the
`@ComponentScan` pointing at the infra authorities package, and removes the duplicate
`cookieSerializer` bean from `ApplicationConfig`.

The infra JARs remain on the classpath — they will be removed in Step 11.

---

## Target Package Structure (all in `web` module)

```
web/src/main/java/se/inera/intyg/rehabstod/security/
  common/
    model/         ← 16 files from security-common
    service/       ← 2 files  from security-common
    cookie/        ← 1 file   from security-common
    exception/     ← 1 file   from security-common
  authorities/
    (root)         ← 8 files  from security-authorities
    bootstrap/     ← 1 file   from security-authorities
    validation/    ← 3 files  from security-authorities

web/src/test/java/se/inera/intyg/rehabstod/security/
  authorities/     ← 4 test files from infra security-authorities tests
  common/cookie/   ← 1 test file  from infra security-common tests
```

---

## Sub-steps

### Sub-step 1 — Copy security-common main files (20 files)

Source: `C:\GIT\Inera\Intyg\infra\security\common\src\main\java\se\inera\intyg\infra\security\common\`
Target package prefix: `se.inera.intyg.rehabstod.security.common`

**model/** (16 files):
| File | Notes |
|------|-------|
| `AuthConstants.java` | simple constants class |
| `AuthenticationMethod.java` | enum |
| `AuthoritiesConstants.java` | simple constants class |
| `Feature.java` | implements Serializable |
| `IntygUser.java` | ⚠️ HSA imports — keep `se.inera.intyg.infra.integration.hsatk.*` as-is |
| `Pilot.java` | implements Serializable |
| `PilotList.java` | references Pilot → update to local |
| `Privilege.java` | implements Serializable |
| `RequestOrigin.java` | implements Serializable |
| `Role.java` | implements Serializable |
| `RoleResolveResult.java` | references Role, Privilege → update to local |
| `Title.java` | implements Serializable |
| `TitleCode.java` | implements Serializable |
| `UserDetails.java` | interface |
| `UserOrigin.java` | implements Serializable |
| `UserOriginType.java` | enum |

**service/** (2 files):
| File | Notes |
|------|-------|
| `AuthenticationLogger.java` | interface |
| `CareUnitAccessHelper.java` | ⚠️ HSA imports — keep `se.inera.intyg.infra.integration.hsatk.*` as-is; references IntygUser → update to local |

**cookie/** (1 file):
| File | Notes |
|------|-------|
| `IneraCookieSerializer.java` | extends DefaultCookieSerializer (Spring Security web) |

**exception/** (1 file):
| File | Notes |
|------|-------|
| `GenericAuthenticationException.java` | extends Spring Security AuthenticationException |

---

### Sub-step 2 — Copy security-authorities main files (12 files)

Source: `C:\GIT\Inera\Intyg\infra\security\authorities\src\main\java\se\inera\intyg\infra\security\authorities\`
Target package prefix: `se.inera.intyg.rehabstod.security.authorities`

**root/** (8 files):
| File | Notes |
|------|-------|
| `AuthoritiesConfiguration.java` | references Role, Privilege, Feature, Title, TitleCode, RequestOrigin → update to local |
| `AuthoritiesException.java` | extends Spring Security AuthenticationException |
| `AuthoritiesHelper.java` | references AuthoritiesConfiguration, Role, Privilege, Feature, Title, TitleCode → update to local |
| `AuthoritiesResolverUtil.java` | references Role, Privilege, RequestOrigin → update to local |
| `CommonAuthoritiesResolver.java` | ⚠️ HSA imports — keep as-is; `@Service`; references IntygUser, AuthoritiesConfiguration → update to local |
| `CommonFeaturesResolver.java` | `@Service`; references Feature, FeaturesConfiguration → update to local |
| `FeaturesConfiguration.java` | references Feature → update to local |
| `FeaturesHelper.java` | `@Component`; references Feature, FeaturesConfiguration → update to local |

**bootstrap/** (1 file):
| File | Notes |
|------|-------|
| `SecurityConfigurationLoader.java` | extends YamlPropertiesFactoryBean; references AuthoritiesConfiguration, FeaturesConfiguration → update to local |

**validation/** (3 files):
| File | Notes |
|------|-------|
| `AuthExpectationSpecification.java` | interface; references IntygUser → update to local |
| `AuthExpectationSpecImpl.java` | implements AuthExpectationSpecification; references IntygUser, Role, Privilege, Feature, RequestOrigin, AuthoritiesException → update to local |
| `AuthoritiesValidator.java` | references AuthExpectationSpecification, AuthExpectationSpecImpl, IntygUser → update to local |

---

### Sub-step 3 — Copy infra test files (5 files)

Source: `C:\GIT\Inera\Intyg\infra\security\authorities\src\test\java\se\inera\intyg\infra\security\authorities\`
Source: `C:\GIT\Inera\Intyg\infra\security\common\src\test\java\se\inera\intyg\infra\security\common\cookie\`

Target: `web/src/test/java/se/inera/intyg/rehabstod/security/authorities/`
Target: `web/src/test/java/se/inera/intyg/rehabstod/security/common/cookie/`

| File | Notes |
|------|-------|
| `AuthoritiesHelperTest.java` | tests AuthoritiesHelper; update all infra security imports |
| `AuthoritiesResolverTest.java` | tests AuthoritiesResolverUtil; update all infra security imports |
| `CommonAuthoritiesResolverTest.java` | loads YAML fixtures; no HSA mocking needed; update imports |
| `FeaturesHelperTest.java` | tests FeaturesHelper; update all infra security imports |
| `IneraCookieSerializerTest.java` | parameterized JUnit 5; update all infra security imports |

The YAML test fixtures referenced by CommonAuthoritiesResolverTest (`AuthoritiesConfigurationLoaderTest/*.yaml`)
need to be checked — they likely already exist in the infra test resources. Copy them to
`web/src/test/resources/AuthoritiesConfigurationLoaderTest/` if not already present in rehabstod.

---

### Sub-step 4 — Batch replace imports in ~45 existing rehabstod files

Two PowerShell replacement passes across all `web/src/main`, `web/src/test` directories:

```
se.inera.intyg.infra.security.common.     →  se.inera.intyg.rehabstod.security.common.
se.inera.intyg.infra.security.authorities. →  se.inera.intyg.rehabstod.security.authorities.
```

**Scope:** 15 main files + 30 test files = 45 files total (all in `web` module — no integration modules affected).

---

### Sub-step 5 — Configuration changes (2 files)

**`SecurityConfig.java`** — remove `@ComponentScan("se.inera.intyg.infra.security.authorities")`:
- The `@Service`/`@Component` beans in local `se.inera.intyg.rehabstod.security.authorities` are
  auto-discovered by the application's main component scan (root package `se.inera.intyg.rehabstod`).
- Remove the `@ComponentScan` annotation and its import of `org.springframework.context.annotation.ComponentScan`.

**`ApplicationConfig.java`** — remove duplicate `cookieSerializer()` bean:
- Both `ApplicationConfig` and `WebSecurityConfig` declare a `cookieSerializer()` bean — this causes a
  `BeanDefinitionOverrideException` once the infra JAR is removed.
- Remove the bean + import from `ApplicationConfig`. Keep the one in `WebSecurityConfig`.
- Update `WebSecurityConfig`'s import to use the local `IneraCookieSerializer` (done in Sub-step 4).

---

### Sub-step 6 — Build and verify

```
.\gradlew.bat test --no-daemon
```

Expected: BUILD SUCCESSFUL, 466+ tests pass.

---

## Complexity / Risk Notes

- **HSA imports in 3 files**: `IntygUser`, `CareUnitAccessHelper`, `CommonAuthoritiesResolver` all
  reference `se.inera.intyg.infra.integration.hsatk.*`. These imports are intentionally left pointing
  to the infra JAR — HSA inlining is deferred to a later step.

- **YAML fixture for CommonAuthoritiesResolverTest**: The test expects YAML files at
  `classpath:AuthoritiesConfigurationLoaderTest/authorities-test.yaml` and `features-test.yaml`.
  Check `web/src/test/resources/` — these may already exist from prior test setup. If not, copy from
  `C:\GIT\Inera\Intyg\infra\security\authorities\src\test\resources\`.

- **`AuthoritiesValidator` in authorities.validation vs rehabstod's own**: The rehabstod web module
  has its own `web/src/main/java/se/inera/intyg/rehabstod/auth/authorities/validation/AuthoritiesValidator.java`
  — this is a rehabstod-specific class. The infra `AuthoritiesValidator` (also in `validation` package)
  is a separate, different class. No name clash at runtime because they're in different packages.

- **No build.gradle changes needed** for web — Spring Security, Jackson, and YAML (SnakeYAML) are
  already transitive dependencies of existing web module deps.

---

## Files Changed Summary

| Change | Count |
|--------|-------|
| New main Java files created | 32 |
| New test Java files created | 5 |
| Existing files with import updates | ~45 |
| Config files modified | 2 (`SecurityConfig`, `ApplicationConfig`) |
| **Total files touched** | **~84** |
