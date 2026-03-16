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
`IABannerService.getCurrentBanners()`, which returns `List<se.inera.intyg.infra.driftbannerdto.Banner>`.
Switching `GetConfigResponse.banners` to the local `Banner` type would require a conversion step
in `ConfigController` that is out of scope for Step 2 (purely additive, no behavioural change).

**Resolution:** Switch to the local `Banner` type when `IABannerService` is replaced with a local
implementation (expected in the same step that inlines `BannerJob` and removes the
`ia-integration` infra dependency).
