# Deferred Items

Items that were partially skipped during a migration step due to infra API coupling or other
constraints. Each entry describes what was deferred, why, and which step should resolve it.

---

## Step 2 — Copy simple DTOs

### `RSBannerJob` — still uses `se.inera.intyg.infra.driftbannerdto.Application`

**File:** `web/src/main/java/se/inera/intyg/rehabstod/jobs/RSBannerJob.java`

**Reason:** `RSBannerJob` extends `se.inera.intyg.infra.integration.ia.jobs.BannerJob`, whose
abstract method `getApplication()` returns `se.inera.intyg.infra.driftbannerdto.Application`.
Java covariant return types do not apply across unrelated types, so the local
`se.inera.intyg.rehabstod.web.dto.driftbanner.Application` cannot be used as the return type
until `BannerJob` (infra) is replaced or inlined.

**Resolution:** Switch to the local `Application` type when `BannerJob` is inlined into the
project (expected in a later step that removes the `ia-integration` infra dependency).

---

### `GetConfigResponse` — still uses `se.inera.intyg.infra.driftbannerdto.Banner`

**File:** `web/src/main/java/se/inera/intyg/rehabstod/web/controller/api/dto/GetConfigResponse.java`

**Reason:** `ConfigController` populates the `banners` field by calling
`IABannerService.getCurrentBanners()`, which returns
`List<se.inera.intyg.infra.driftbannerdto.Banner>`.
Switching `GetConfigResponse.banners` to the local `Banner` type would require a conversion step
in `ConfigController` that is out of scope for Step 2 (purely additive, no behavioural change).

**Resolution:** Switch to the local `Banner` type when `IABannerService` is replaced with a local
implementation (expected in the same step that inlines `BannerJob` and removes the
`ia-integration` infra dependency).

---

## Step 3 — Inline `monitoring`

### `ApplicationConfig` still imports `MonitoringConfiguration`

**File:** `web/src/main/java/se/inera/intyg/rehabstod/config/ApplicationConfig.java`

**Reason:**  
Removing `@Import(MonitoringConfiguration.class)` had to be reverted. Current runtime behavior for
`RSBannerJob` depends on monitoring wiring via `LogMcdHelper`, so removing `MonitoringConfiguration`
breaks Step 3 in the current state.

**Resolution:**  
Keep `MonitoringConfiguration` import in `ApplicationConfig` for now. Remove it in the step where
`RSBannerJob` and its infra coupling are replaced/inlined and the `LogMcdHelper` dependency is no
longer required.

---

## Step 5 — Inline `security-common` + `security-authorities`

### `BaseUserDetailsService` + `DefaultUserDetailsDecorator` — pulled forward from Step 6

**Files (infra):**
- `se.inera.intyg.infra.security.siths.BaseUserDetailsService`
- `se.inera.intyg.infra.security.siths.DefaultUserDetailsDecorator`

**Reason:** Step 6 originally planned to inline these two classes. They were pulled forward into
Step 5 due to a type incompatibility: `RehabstodUser extends (local) IntygUser`, but
`BaseUserDetailsService` declared methods returning `(infra) IntygUser`. The override chain was
incompatible, requiring `RehabstodUserDetailsService` to be fully rewritten.

**Resolution:** Done. `RehabstodUserDetailsService` no longer extends `BaseUserDetailsService`.
All logic from `BaseUserDetailsService` and `DefaultUserDetailsDecorator` is inlined. Step 6 did
not need to repeat this.

---

## Step 6 — Inline `security-filter` + `security-siths`

### `InternalApiFilter` — intentionally skipped

**File (infra):** `se.inera.intyg.infra.security.filter.InternalApiFilter`

**Reason:** This filter restricts API access to a specific port (`${internal.api.port}`). It is
not used anywhere in rehabstod (not registered in `ApplicationInitializer`, not declared as a
bean, no imports found). Copying it would add dead code.

**Resolution:** Leave it out. If a port-based API filter is needed in the future, implement it
directly in the rehabstod codebase without copying from infra.

---
