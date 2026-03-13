# Incremental Migration Plan — Keeping the Application Working at Every Step

Based on [`first-migration-scope.md`](first-migration-scope.md) and [`goal-tech-stack.md`](goal-tech-stack.md), this plan breaks the
migration into **small, independently verifiable increments**. After each step the application must:
**compile ✅, pass all tests ✅, start ✅, and be deployable ✅**.

The phases from `first-migration-scope.md` are retained but each phase is split into atomic steps. Steps 1–11 do not change how the
application runs (still WAR/Tomcat/Gretty). Step 12 is the actual Spring Boot switch; it is small because all preparation is done. Steps
13–17 are safe because Spring Boot auto-configuration backs off gracefully when existing beans are present.

---

## Step 1 — Migrate all tests to JUnit 5 *(zero runtime impact)*

**Why first:** Purely test-code changes. Zero risk to the running application. Removes `junit-vintage-engine` early so every subsequent
step's test run is clean.

**Scope (four modules have JUnit 4 tests):**

- `web/build.gradle` — remove `testImplementation 'junit:junit'` and `testRuntimeOnly "org.junit.vintage:junit-vintage-engine"`
- `persistence/build.gradle` — same removals
- `common/build.gradle` — same removals
- For each JUnit 4 test file: update imports (`org.junit.*` → `org.junit.jupiter.api.*`), replace `@RunWith` with `@ExtendWith`,
  replace `@Rule ExpectedException` with `assertThrows`, etc. (see migration table in `first-migration-scope.md §3.6`).

**Verify:** `./gradlew test` — all tests pass. Application starts normally.

---

## Step 2 — Copy simple DTOs: `certificate`, `log-messages`, `driftbanner-dto`

**Why here:** Purely additive — copy classes into the project, switch local imports, but keep the infra JARs on the classpath. Zero
behavioural change. Sets the pattern for all later inlining steps.

**Scope:**

- Copy `certificate` DTOs (`DiagnosedCertificate`, `SickLeaveCertificate`, `TypedCertificateRequest`, `BaseCertificate`, builders) into
  `se.inera.intyg.rehabstod.common.model` or appropriate module packages. Update imports in `web` (6 files) and `integration/it-integration`
  (which also depends on `certificate`).
- Copy `log-messages` classes (`ActivityType`, `ResourceType`, `PdlLogMessage`, `PdlResource`, `Patient`, `Enhet`, `ActivityPurpose`) into a
  local package. Update imports in `web` (22 files) and `common` (which also depends on `log-messages`).
- Copy `driftbanner-dto` classes (`Application`, `Banner`) into a local package. Update imports in `web` (2 files).

> **Do not remove** the infra dependencies from build files yet — that is Step 11.

**Verify:** `./gradlew test` + start. All functionality unchanged.

---

## Step 3 — Inline `monitoring`: replace `@PrometheusTimeMethod` and `LogMarkers`

**Why here:** `monitoring` is used in three modules (`web`, `it-integration`, `wc-integration`). Replacing it early clears the path for
a clean Micrometer adoption in Step 14. The local `logging` module already has `LogMarkers` — use that.

**Scope:**

- Copy `MonitoringConfiguration` locally (or simply remove it — rehabstöd's monitoring annotation will be replaced by Micrometer `@Timed`
  in Step 14; for now, replace `@PrometheusTimeMethod` usages with a no-op stub or a local annotation that will be wired to Micrometer
  later).
- Replace `LogMarkers` infra import with the local `logging` module version in all files using it.
- Remove `@Import(MonitoringConfiguration.class)` from `ApplicationConfig`; register local config if needed.
- Address all three modules: `web`, `integration/it-integration`, `integration/wc-integration`.

**Verify:** `./gradlew test` + start. Prometheus metrics still served (the infra JAR is still on classpath; the manual setup remains until
Step 14). Application behaviour unchanged.

---

## Step 4 — Copy and inline `sjukfall-engine`

**Why here:** The sjukfall (sick leave) engine is core business logic used in four modules. Copying it while the infra JAR is still present
is safe — the local copy replaces the import without changing runtime behaviour.

**Scope:**

- Copy all DTOs (`IntygData`, `IntygParametrar`, `SjukfallEnhet`, `RekoStatusTypeDTO`, `Vardgivare`, `Patient`, `DiagnosKod`,
  `DiagnosKapitel`, `DiagnosKategori`, `Lakare`, `Formaga`, `OccupationTypeDTO`) and `SjukfallEngineServiceImpl` into a local package
  (e.g., `se.inera.intyg.rehabstod.sjukfall`).
- Register the local `SjukfallEngineServiceImpl` as `@Service`. Remove the `@ComponentScan` for `se.inera.intyg.infra.sjukfall.services` in
  `SjukfallConfig`.
- Update imports in all four modules: `web` (29 files), `common`, `integration/it-integration`, `integration/sparrtjanst-integration`.

**Verify:** `./gradlew test` + start. Sick leave calculations produce identical results.

---

## Step 5 — Inline security models: `security-common` + `security-authorities`

**Why before filter/siths:** `security-common` models are the foundation that `security-authorities`, `security-filter`, and `security-siths`
all build on. Inline them first so subsequent steps can reference local types.

**Scope:**

- Copy `security-common` classes (`Role`, `Feature`, `Privilege`, `IntygUser`, `UserOrigin`, `UserOriginType`, `RequestOrigin`, `Title`,
  `TitleCode`, `AuthenticationLogger`, `CareUnitAccessHelper`, `AuthoritiesConstants`, `IneraCookieSerializer`) into a local package. Update
  imports in `web` (38 files).
- Copy `security-authorities` classes (`CommonAuthoritiesResolver`, `AuthoritiesException`, `SecurityConfigurationLoader`,
  `AuthoritiesConfiguration`, `AuthExpectationSpecImpl`, `AuthExpectationSpecification`) into a local package. Update imports in `web` (14
  files).

> Both infra JARs remain on the classpath during this step.

**Verify:** `./gradlew test` + start. Authentication and authorisation work identically.

---

## Step 6 — Inline `security-filter` + `security-siths`

**Why together:** `security-filter` provides filters registered in `ApplicationInitializer`; `security-siths` provides
`BaseUserDetailsService` which `RehabstodUserDetailsService` extends. Both are small and interdependent.

**Scope:**

- Copy `security-filter` classes (`SessionTimeoutFilter`, `RequestContextHolderUpdateFilter`, `PrincipalUpdatedFilter`,
  `SecurityHeadersFilter`) into a local package. Update registration in `ApplicationInitializer` (5 files affected).
- Inline `BaseUserDetailsService` logic directly into `RehabstodUserDetailsService` (1 file). Remove the `extends BaseUserDetailsService`
  declaration once inlined.

**Verify:** `./gradlew test` + start. Login/logout and session timeout still work correctly.

---

## Step 7 — Inline service wrappers: `dynamiclink`, `ia-integration`, `rediscache`

**Why here:** These are thin service wrappers with no shared dependencies on the security or sjukfall code already inlined. Clearing them
now empties the last "medium complexity" infra bucket before tackling the HSA/PU replacement.

**Scope:**

- **`dynamiclink`:** Copy `DynamicLink` and `DynamicLinkService` locally. Remove the `@ComponentScan` for
  `se.inera.intyg.infra.dynamiclink` from `InfraConfig`.
- **`ia-integration`:** Copy or reimplement `IABannerService` interface and `BannerJob` locally. Remove `@ImportResource` for
  `ia-services-config.xml` and `ia-stub-context.xml` from `IaConfiguration` and `IaStubConfiguration`. Convert both to pure Java
  `@Configuration` (aligns with §3.2.2 from scope doc).
- **`rediscache`:** Remove `RedisCacheOptionsSetter` usages from `EmployeeNameCacheConfig`. Adapt the cache config to use Spring
  `RedisCacheConfiguration` directly (preparation for Step 16). Also remove `common-redis-cache-core` from
  `integration/sparrtjanst-integration/build.gradle` and `integration/samtyckestjanst-integration/build.gradle`.

**Verify:** `./gradlew test` + start. Dynamic links load. Banner service operates. Caching (if profile active) works.

---

## Step 8 — Replace HSA & PU integrations with direct REST clients

**Why here:** The most architecturally significant infra replacement. Done while still on traditional Spring/WAR so only one large change
is in flight at a time.

**Scope:**

- **HSA:** Define local DTOs for HSA models (`Vardenhet`, `Vardgivare`, `Mottagning`, `PersonInformation`, etc.). Implement
  `HsaOrganizationsService` and `HsaEmployeeService` locally using Spring `RestTemplate` (not `RestClient` yet — not on Spring Boot).
  Call `intyg-proxy-service` REST APIs directly. Remove `hsa-integration-api` and `hsa-integration-intyg-proxy-service` usages (29 files in
  `web`).
- **PU:** Define local `PersonSvar` and `Person` DTOs. Implement `PuService` locally calling `intyg-proxy-service` REST API. Remove
  `PuIntygProxyServiceConfiguration` and its `@ImportResource` for `pu-integration-intyg-proxy-service-config.xml`. Remove
  `pu-integration-api` and `pu-integration-intyg-proxy-service` usages (3 files in `web`).

**Verify:** `./gradlew test` + start. Employee searches, care unit lookups, and patient PU lookups return correct results via the new REST
clients.

---

## Step 9 — Convert JAX-RS stubs to Spring MVC

**Why before XML conversion:** The JAX-RS stub registration lives inside `samtyckestjanst-stub-context.xml` and
`sparrtjanst-stub-context.xml`. Converting the stubs to `@RestController` first makes the XML conversion in Step 10 a mechanical
find-and-replace rather than a combined refactor.

**Scope:**

- Convert `SamtyckestjanstStubRestApi` → `@RestController @Profile("rhs-samtyckestjanst-stub")` (drop `@Path`, `@Produces`, `@PathParam`
  etc. — see mapping table in `first-migration-scope.md §3.3`).
- Convert `SparrtjanstStubRestApi` → `@RestController @Profile("rhs-sparrtjanst-stub")`.
- Remove `jakarta.ws.rs:jakarta.ws.rs-api` and `cxf-rt-frontend-jaxrs` from the affected integration module build files.

**Verify:** `./gradlew test` + start with stub profiles active. Stub REST endpoints return correct responses. No JAX-RS annotations remain.

---

## Step 10 — Convert all XML bean configuration to Java

**Why here:** All infra-imported XML sources (ia, PU, basic-cache-config) are already gone. Only 4 local XML files remain. Converting them
now means `@ImportResource` disappears before the Spring Boot switch.

**Scope:**

- **`samtyckestjanst-services-config.xml`** → `SamtyckestjanstTlsConfig.java` (programmatic CXF `HTTPConduitConfigurer` with TLS
  parameters, key/trust managers, cipher suite filters).
- **`samtyckestjanst-stub-context.xml`** → absorbed by the `@RestController` created in Step 9; remove the XML file.
- **`sparrtjanst-services-config.xml`** → `SparrtjanstTlsConfig.java` (same TLS pattern).
- **`sparrtjanst-stub-context.xml`** → absorbed by the `@RestController` created in Step 9; remove the XML file.
- **`classpath:META-INF/cxf/cxf.xml`** → remove `@ImportResource` from `ApplicationConfig` and `WebConfig`; configure CXF `Bus` as a
  `@Bean` via `SpringBus` (or rely on the CXF Spring Boot starter in Step 12 to auto-load it).
- Remove `web/src/main/webapp/WEB-INF/web.xml` — already listed as a §3.1 action; confirm removal here so no XML file is carried into the
  Spring Boot step.

**After this step:** `grep -r "@ImportResource" --include="*.java" src/` returns no results.

**Verify:** `./gradlew test` + start. All CXF TLS connections work (Samtyckestjänst, Spärrtjänst). Stubs respond under their profiles.

---

## Step 11 — Remove all `se.inera.intyg.infra` dependencies from all build files

**Why a dedicated step:** Making the dependency removal a single, focused commit gives a clean compile-time proof that no infra code is
referenced anywhere. Each module is handled:

| Module | Lines removed |
|---|---|
| `web/build.gradle` | 16 infra lines (see §3.5.6) |
| `common/build.gradle` | `log-messages`, `sjukfall-engine` |
| `integration/it-integration/build.gradle` | `certificate`, `monitoring`, `sjukfall-engine` |
| `integration/sparrtjanst-integration/build.gradle` | `common-redis-cache-core`, `sjukfall-engine` |
| `integration/samtyckestjanst-integration/build.gradle` | `common-redis-cache-core` |
| `integration/wc-integration/build.gradle` | `monitoring` |
| `build.gradle` (root) | `intygInfraVersion` property |

Also remove all `@ComponentScan` entries referencing `se.inera.intyg.infra.*` packages (in `InfraConfig`, `SjukfallConfig`, and others).

**Verify:** `./gradlew build` — compiles cleanly. All tests pass. `grep -r "se.inera.intyg.infra" --include="*.gradle"` returns no results.
Application starts.

---

## Step 12 — Resolve static resources and view resolvers

**Why before the Spring Boot switch:** Static resource handling changes significantly between WAR/DispatcherServlet and Spring Boot JAR.
Determining the right strategy now prevents surprises in Step 13.

**Decision required (confirm before implementing):**

1. Is the Angular/Bower frontend actively served by rehabstöd, or is it deployed separately?
2. If served: move assets to `src/main/resources/static/` and remove the `WebConfig` resource handlers for `/bower_components/**`, `/app/**`,
   `/components/**`.
3. If not served: remove the resource handlers and both `ViewResolver` beans (HTML + JSP) from `WebConfig` entirely.
4. Remove JSP `ViewResolver` — Spring Boot executable JARs have no embedded JSP support. If any JSP templates are still in use they must
   be migrated (e.g., to Thymeleaf) before Step 13.

**Verify:** `./gradlew test` + start. Frontend assets load correctly (or are confirmed absent). No JSP usage remains.

---

## Step 13 — Spring Boot bootstrap *(the big switch)*

**Why now:** All preparation is complete. The codebase has no infra deps, no XML config, no JAX-RS, and a resolved frontend strategy. This
step only changes the application entry point and build packaging.

**Scope:**

- Add `org.springframework.boot` plugin to root `build.gradle` (`apply false`) and `web/build.gradle` (apply + `bootJar`).
- Remove `org.gretty` plugin and `war` plugin from `web/build.gradle`. Remove `web/tomcat-gretty.xml`.
- Create `RehabstodApplication.java` with `@SpringBootApplication`.
- Add `spring-boot-starter-web` starter.
- Convert the 12 `ApplicationInitializer` filter registrations to `FilterRegistrationBean` beans (preserving exact order — see §3.1).
- Register `CXFServlet` as a `ServletRegistrationBean` at `/services/*`.
- Keep `HttpSessionEventPublisher` as a `@Bean`.
- Remove `@EnableWebMvc` from `WebConfig`; retain all `WebMvcConfigurer` settings as-is.
- **Do not change** JPA, JMS, Redis, or metrics config in this step.

> **De-risk spike:** Before investing in Steps 1–12, run a quick spike on a throwaway branch — add the Spring Boot plugin and main class,
> and verify that CXF + Spring Boot coexist. If it starts, proceed with confidence.

**Verify:** `./gradlew bootRun` — application starts as an executable JAR. All REST endpoints respond. All SOAP stubs respond. SAML
login/logout flow works end-to-end.

---

## Step 14 — JPA auto-configuration

**Why one concern at a time:** Spring Boot auto-config backs off when existing beans are present. Removing one manual config at a time
makes failures easy to attribute.

**Scope:**

- Add `spring-boot-starter-data-jpa` (or confirm it is already pulled transitively).
- Remove `PersistenceConfig` and `PersistenceConfigBase` (manual `DataSource`, `EntityManagerFactory`, `TransactionManager`,
  `SpringLiquibase` beans). Remove `PersistenceConfigDev` (H2 profile).
- Add `@EntityScan("se.inera.intyg.rehabstod.persistence")` and `@EnableJpaRepositories` to the main application class.
- Move all DB/Hibernate/Liquibase properties to `application.properties` under `spring.datasource.*`, `spring.jpa.*`, and
  `spring.liquibase.*` keys (see §3.4.1 for the property mapping).
- Remove explicit `HikariCP` and `hibernate-hikaricp` dependencies from `persistence/build.gradle` (now provided by starter).

**Verify:** `./gradlew bootRun` — starts, connects to DB, Liquibase changelog runs. `./gradlew test` passes.

---

## Step 15 — JMS auto-configuration

**Scope:**

- Add `spring-boot-starter-activemq`.
- Remove the manual `ActiveMQConnectionFactory`, `JmsTransactionManager`, and `JmsTemplate` beans from `JmsConfig`.
- Keep custom `Queue` beans and the dedicated `jmsPDLLogTemplate` (specific destination + session-transacted settings).
- Move broker properties to `application.properties` under `spring.activemq.*`.
- Remove explicit `activemq-spring` and `spring-jms` dependencies (now provided by starter).

**Verify:** `./gradlew bootRun` — JMS connections established. PDL log messages delivered to queue. `./gradlew test` passes.

---

## Step 16 — Spring Boot Actuator + Micrometer (replaces Prometheus servlet)

**Scope:**

- Add `spring-boot-starter-actuator` and `micrometer-registry-prometheus`.
- Remove `io.prometheus:simpleclient_servlet` dependency.
- Configure `management.endpoints.web.exposure.include=health,info,prometheus` in `application.properties`.
- Replace `@PrometheusTimeMethod` usages with Micrometer `@Timed` (or remove if `PerformanceLogging` AOP already covers the concern — see
  §3.4.3 decision note).

**Verify:** `GET /actuator/health` → `{"status":"UP"}`. `GET /actuator/prometheus` → Prometheus metrics. `./gradlew test` passes.

---

## Step 17 — Redis auto-configuration + ShedLock prefix fix

**Scope:**

- Add `spring-boot-starter-data-redis`. Decide at this point whether to keep Jedis (add Jedis dependency, exclude Lettuce) or switch to
  Lettuce (Spring Boot default). Keeping Jedis is lower risk initially.
- Remove the manual `JedisConnectionFactory` bean from `JobConfig` and the `@ImportResource("classpath:basic-cache-config.xml")` from
  `CacheConfigurationFromInfra`. Remove `common-redis-cache-core` dependency (already done in Step 11 for integration modules).
- Adapt `EmployeeNameCacheConfig` to use `RedisCacheConfiguration` directly.
- Keep `@EnableRedisHttpSession` in `WebSecurityConfig` — Spring Session Data Redis continues to work.
- **Fix ShedLock prefix:** Change the `RedisLockProvider` prefix in `JobConfig` from `"webcert"` to `"rehabstod"`.
- Move Redis properties to `application.properties` under `spring.data.redis.*`.

**Verify:** `./gradlew bootRun` — Redis session store works (login persists across requests). Employee name cache populates under
`caching-enabled` profile. ShedLock acquires locks with the `rehabstod` prefix.

---

## Step 18 — Spring Boot ECS structured logging + Dockerfile update

**Scope:**

- **Logging:** Remove `co.elastic.logging:logback-ecs-encoder` from `logging/build.gradle`. Remove `logback-spring-base.xml` and
  `LogbackConfiguratorContextListener`. Add to `application.properties`:
  ```properties
  logging.structured.format.console=ecs
  logging.structured.ecs.service.name=rehabstod
  logging.structured.ecs.service.environment=${spring.profiles.active:default}
  ```
  Evaluate `UserConverter` and `SessionConverter` — replace with MDC fields in the ECS structured output or retain for custom patterns.
- **Dockerfile:** Replace the WAR/Catalina deployment with a Spring Boot JAR image:
  ```dockerfile
  COPY web/build/libs/*.jar app.jar
  ENTRYPOINT ["java", "-jar", "/app.jar"]
  ```

**Verify:** Docker image builds and runs. ECS-formatted JSON logs appear on stdout. Application starts from the container. All endpoints
respond.

---

## Summary

| Step | Description | Phase | App broken? |
|------|-------------|-------|-------------|
| **1** | Migrate all tests to JUnit 5 | 0 — Test Modernisation | ❌ No |
| **2** | Copy simple DTOs (certificate, log-messages, driftbanner-dto) | 1 — Inline Infra | ❌ No |
| **3** | Inline `monitoring` + replace `LogMarkers` | 1 — Inline Infra | ❌ No |
| **4** | Copy and inline `sjukfall-engine` | 1 — Inline Infra | ❌ No |
| **5** | Inline `security-common` + `security-authorities` | 1 — Inline Infra | ❌ No |
| **6** | Inline `security-filter` + `security-siths` | 1 — Inline Infra | ❌ No |
| **7** | Inline `dynamiclink`, `ia-integration`, `rediscache` | 1 — Inline Infra | ❌ No |
| **8** | Replace HSA & PU with direct REST clients | 1 — Inline Infra | ❌ No |
| **9** | Convert JAX-RS stubs to Spring MVC | 2 — Pre-Boot Cleanup | ❌ No |
| **10** | Convert all XML config to Java | 2 — Pre-Boot Cleanup | ❌ No |
| **11** | Remove all `se.inera.intyg.infra` deps from all build files | 2 — Pre-Boot Cleanup | ❌ No |
| **12** | Resolve static resources and view resolvers | 2 — Pre-Boot Cleanup | ❌ No |
| **13** | **Spring Boot bootstrap** | 3 — Spring Boot Switch | ❌ No |
| **14** | JPA auto-configuration | 4 — Auto-Config | ❌ No |
| **15** | JMS auto-configuration | 4 — Auto-Config | ❌ No |
| **16** | Actuator + Micrometer (metrics) | 4 — Auto-Config | ❌ No |
| **17** | Redis auto-configuration + ShedLock prefix fix | 4 — Auto-Config | ❌ No |
| **18** | Spring Boot ECS logging + Dockerfile | 4 — Auto-Config | ❌ No |

Steps 1–12 are still WAR/Tomcat/Gretty — only production code and configuration change. Step 13 is the actual runtime switch.
Steps 14–18 each swap one infrastructure concern using Spring Boot auto-configuration.

---

## Highest-Risk Steps

**Step 13 (Spring Boot bootstrap)** is the highest-risk step overall due to:
- SAML 2.0 session management interacting with embedded Tomcat and Spring Session Redis.
- Filter order changes (12 filters must be re-registered in exactly the right order via `FilterRegistrationBean.setOrder()`).
- `@EnableWebMvc` removal potentially changing MVC behaviour (content negotiation, message converters).
- CXF JAXWS compatibility with Spring Boot auto-configuration.

**Mitigation:** Run the de-risk spike described in Step 13 early. Test SAML login/logout end-to-end immediately after the switch before
proceeding to Steps 14–18.

**Step 8 (HSA/PU REST clients)** is the second-highest risk because the REST response shapes from `intyg-proxy-service` must be verified
to match what the infra library previously delivered. Test with real endpoints, not just unit tests.
