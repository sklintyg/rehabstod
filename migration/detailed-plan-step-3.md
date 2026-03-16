# Step 3 plan: Inline `monitoring` and replace `LogMarkers`

## Problem and approach

We are implementing **Step 3** from `migration/incremental-migration-plan.md`: remove direct source-code coupling to
`se.inera.intyg.infra:monitoring` by replacing:

- `@PrometheusTimeMethod` imports/usages
- infra `LogMarkers` imports
- infra `MonitoringConfiguration` wiring

while keeping runtime behavior stable and keeping the infra dependency in Gradle for now (dependency cleanup is Step 11).

Approach: make a small, behavior-preserving local compatibility layer in this repo, switch all source imports to local classes, and remove
explicit monitoring config wiring that is no longer needed.

## Current state analysis (codebase snapshot)

### Direct infra-monitoring coupling found

- `web/src/main/java/se/inera/intyg/rehabstod/config/ApplicationConfig.java`
    - `@Import(MonitoringConfiguration.class)`
- `web/src/main/java/se/inera/intyg/rehabstod/config/ApplicationInitializer.java`
    - imports and registers `MonitoringConfiguration`
    - imports and registers `LogbackConfiguratorContextListener`
- `web/src/main/java/se/inera/intyg/rehabstod/config/InfraConfig.java`
    - `@ComponentScan` includes `se.inera.intyg.infra.monitoring.logging`
- `web/src/main/java/se/inera/intyg/rehabstod/service/monitoring/MonitoringLogServiceImpl.java`
    - imports infra `@PrometheusTimeMethod`
    - imports infra `LogMarkers`
- `web/src/main/java/se/inera/intyg/rehabstod/service/hsa/EmployeeNameServiceImpl.java`
    - imports infra `@PrometheusTimeMethod`
- `web/src/main/java/se/inera/intyg/rehabstod/service/sjukfall/SjukfallServiceImpl.java`
    - imports infra `@PrometheusTimeMethod`
- `integration/it-integration/src/main/java/.../IntygstjanstClientServiceImpl.java`
    - imports infra `@PrometheusTimeMethod`
- `integration/it-integration/src/main/java/.../IntygstjanstIntegrationServiceImpl.java`
    - imports infra `@PrometheusTimeMethod`

### Existing local replacement already available

- `logging/src/main/java/se/inera/intyg/rehabstod/logging/LogMarkers.java`
    - provides `MONITORING` and `PERFORMANCE` markers.

### Build files

- `web/build.gradle`, `integration/it-integration/build.gradle`, and `integration/wc-integration/build.gradle`
    - all currently declare `implementation "se.inera.intyg.infra:monitoring:${intygInfraVersion}"`.
    - Per migration sequence, these lines remain until Step 11.

### Security/endpoint note

- `WebSecurityConfig` already permits `/metrics`; this should remain unchanged in Step 3.

## Detailed implementation plan

1. Create local timing annotation (compatibility layer)
    - Add a local annotation with same semantic role as current `@PrometheusTimeMethod`, e.g.:
        - package candidate: `se.inera.intyg.rehabstod.monitoring.annotation`
        - annotation name: `PrometheusTimeMethod`
    - Make it a no-op annotation (runtime-retained, method target) for now.
    - Reason: keeps behavior stable and avoids introducing Micrometer before planned Step 14.

2. Replace all source imports of `@PrometheusTimeMethod`
    - Update the five implementation classes currently importing infra annotation:
        - `MonitoringLogServiceImpl`
        - `EmployeeNameServiceImpl`
        - `SjukfallServiceImpl`
        - `IntygstjanstClientServiceImpl`
        - `IntygstjanstIntegrationServiceImpl`
    - Keep annotation usage on methods unchanged; only import/package source changes.

3. Replace infra `LogMarkers` usage with local logging module
    - Update `MonitoringLogServiceImpl` import from:
        - `se.inera.intyg.infra.monitoring.logging.LogMarkers`
        - to `se.inera.intyg.rehabstod.logging.LogMarkers`.

4. Remove explicit monitoring configuration import/wiring in web config
    - In `ApplicationConfig`:
        - remove import `se.inera.intyg.infra.monitoring.MonitoringConfiguration`
        - remove `@Import(MonitoringConfiguration.class)`
    - In `ApplicationInitializer`:
        - remove import + registration of `MonitoringConfiguration.class` in `appContext.register(...)`.

5. Handle `LogbackConfiguratorContextListener` in `ApplicationInitializer`
    - **Decision confirmed:** remove infra listener import/registration in Step 3.
    - Validate startup logging still works with existing `logback.file` setup.
    - If removal causes startup regression, document it explicitly as a blocker for Step 3 completion (no silent fallback).

6. Update `InfraConfig` component scan
    - Remove `se.inera.intyg.infra.monitoring.logging` from `@ComponentScan`.
    - Keep `se.inera.intyg.infra.dynamiclink` untouched (belongs to Step 7).

7. Verify module impact (`web`, `it-integration`, `wc-integration`)
    - `web`: compile + tests + startup smoke.
    - `it-integration`: compile + tests to ensure local annotation is visible in module classpath.
    - `wc-integration`: confirm no source imports exist today; ensure module still builds unchanged.

8. Validation and guardrails
    - Run repository tests: `./gradlew test`.
    - Run app startup smoke (existing project startup command) and verify:
        - app boots
        - `/metrics` is still accessible as before
        - no bean wiring failures from removed `MonitoringConfiguration`.

## Verification checklist for done criteria

- No remaining source imports from:
    - `se.inera.intyg.infra.monitoring.annotation.PrometheusTimeMethod`
    - `se.inera.intyg.infra.monitoring.logging.LogMarkers`
    - `se.inera.intyg.infra.monitoring.MonitoringConfiguration`
- `ApplicationInitializer` no longer registers monitoring infra config/listener (or uses local replacement).
- `./gradlew test` passes.
- Startup smoke passes and behavior is unchanged.

## Risks and mitigations

- Risk: removing `LogbackConfiguratorContextListener` alters logging initialization.
    - Mitigation: remove in a dedicated commit chunk and smoke test immediately; treat any breakage as a hard blocker requiring explicit
      remediation.
- Risk: cross-module classpath visibility of local annotation (used in `integration/it-integration`).
    - Mitigation: place annotation in a shared module/package visible to both `web` and integration modules (prefer `rehabstod-common`).

## Notes for later steps

- Keep Gradle monitoring dependencies until Step 11, even if now unused by source.
    - Step 14/16 will handle Micrometer/Actuator and migration from compatibility annotation to `@Timed` (or complete removal if redundant
      with existing performance logging AOP).