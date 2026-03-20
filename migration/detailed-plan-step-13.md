# Step 13 — Spring Boot Bootstrap *(the big switch)*

## Progress Tracker

| Sub-step | Description | Status |
|----------|-------------|--------|
| 13.1 | Add Spring Boot plugin to Gradle build files | ⬜ TODO |
| 13.2 | Switch `web` module from WAR to Spring Boot JAR | ⬜ TODO |
| 13.3 | Create `RehabstodApplication.java` | ⬜ TODO |
| 13.4 | Remove `@EnableWebMvc` from `WebConfig` | ⬜ TODO |
| 13.5 | Collapse dual-context into single Spring Boot context | ⬜ TODO |
| 13.6 | Convert filter registrations: 7 manual `FilterRegistrationBean` beans, 3 auto-managed, 1 dropped | ⬜ TODO |
| 13.7 | Register `CXFServlet` as `ServletRegistrationBean` | ⬜ TODO |
| 13.8 | Register `HttpSessionEventPublisher` as `@Bean` | ⬜ TODO |
| 13.9 | Replace custom Logback setup with Spring Boot ECS structured logging | ⬜ TODO |
| 13.10 | Remove `web.xml` | ⬜ TODO |
| 13.11 | Remove `tomcat-gretty.xml` and Gretty config | ⬜ TODO |
| 13.12 | Migrate embedded Tomcat settings to `application.properties` | ⬜ TODO |
| 13.13 | Delete legacy files (`BasicCacheConfiguration.java`, `basic-cache-config.xml`, `ApplicationInitializer.java`) | ⬜ TODO |
| 13.14 | Update `ApplicationConfig` — remove `@ImportResource` for CXF | ⬜ TODO |
| 13.15 | Simplify `BasicCacheConfig` → rename to `RedisConfig`, remove `caching-enabled` profile gate | ⬜ TODO |
| 13.16 | Rename and clean up configuration classes (`TlsConfig`, `IaConfig`, delete `SjukfallConfig`) | ⬜ TODO |
| 13.17 | Verify build, tests, startup, endpoints | ⬜ TODO |

---

## Current State (after Step 12)

- **Build:** WAR packaging via `war` plugin, developed with `org.gretty` / Tomcat 10
- **Entry point:** `ApplicationInitializer` implements `WebApplicationInitializer` with programmatic
  servlet/filter registration
- **Context architecture:** **Dual-context** — root `AnnotationConfigWebApplicationContext` with 22
  config classes + child web context with `WebConfig` only
- **Filter chain:** 12 filters registered in strict order in `ApplicationInitializer`
- **CXF:** `CXFServlet` registered at `/services/*`; `SpringBus` bean + `@ImportResource("classpath:META-INF/cxf/cxf.xml")` in `ApplicationConfig`
- **DispatcherServlet:** mapped to `/` with `WebConfig` as its context
- **Logback:** Custom `LogbackConfiguratorContextListener` reads `-Dlogback.file` system property;
  `logback-spring-base.xml` defines `CONSOLE` and `ECS_JSON_CONSOLE` (via `co.elastic.logging:logback-ecs-encoder`) appenders
- **`web.xml`:** Only defines Prometheus `MetricsServlet` at `/metrics`
- **Dockerfile:** WAR deployment `ADD /web/build/libs/*.war $CATALINA_HOME/webapps/${context_path}.war`
- **No `@SpringBootApplication` class exists**
- **No Spring Boot starters on the classpath**
- **No Spring Boot plugin in any build file**

### Key Configuration Classes Registered in ApplicationInitializer

```
appContext.register(
    WebSecurityConfig.class,          // @EnableWebSecurity, @EnableRedisHttpSession, SAML2
    ApplicationConfig.class,          // @EnableTransactionManagement, CXF Bus, @PropertySource, @ComponentScan
    BasicCacheConfig.class,           // @EnableCaching, Redis/Jedis, RedisCacheManager
    ServiceConfig.class,              // @EnableScheduling, @ComponentScan(service, auth, common)
    IaConfiguration.class,            // IA banner service, @Import(IaStubConfiguration)
    JobConfig.class,                  // @EnableAsync, @EnableScheduling, @EnableSchedulerLock, ShedLock
    IntygstjanstIntegrationConfiguration.class,
    IntygstjanstRestIntegrationConfiguration.class,
    IntygstjanstIntegrationClientConfiguration.class,
    SamtyckestjanstConfiguration.class,    // @Import(SamtyckestjanstStubConfiguration)
    SamtyckestjanstClientConfiguration.class,
    SamtyckestjanstStubConfiguration.class,
    SparrtjanstConfiguration.class,        // @Import(SparrtjanstStubConfiguration)
    SparrtjanstClientConfiguration.class,
    SparrtjanstStubConfiguration.class,
    SRSIntegrationConfiguration.class,
    SRSIntegrationClientConfiguration.class,
    SRSIntegrationStubConfiguration.class,
    JmsConfig.class,                  // ActiveMQ ConnectionFactory, JmsTemplate
    SecurityConfig.class,             // RestTemplate (SSL), @ComponentScan(security.authorities)
    SjukfallConfig.class,             // Empty @Configuration
    PersistenceConfig.class           // @EnableJpaRepositories, extends PersistenceConfigBase
);
```

### Current Filter Registration Order (12 filters)

Registered programmatically in `ApplicationInitializer.onStartup()` — the order below is the
**exact execution order** in the filter chain:

| # | Bean / Filter name | Class | URL | dispatch | Init Parameters |
|---|---|---|---|---|---|
| 1 | `characterEncodingFilter` | `CharacterEncodingFilter` | `/*` | false | `encoding=UTF-8`, `forceEncoding=true` |
| 2 | `springSessionRepositoryFilter` | `DelegatingFilterProxy` | `/*` | false | — |
| 3 | `requestContextHolderUpdateFilter` | `RequestContextHolderUpdateFilter` | `/*` | false | — |
| 4 | `mdcServletFilter` | `DelegatingFilterProxy` → bean | `/*` | false | — |
| 5 | `sessionTimeoutFilter` | `SessionTimeoutFilter` | `/*` | false | `skipRenewSessionUrls=SESSION_STATUS_CHECK_URI` |
| 6 | `springSecurityFilterChain` | `DelegatingFilterProxy` | `/*` | false | — |
| 7 | `mdcUserServletFilter` | `DelegatingFilterProxy` → bean | `/*` | false | — |
| 8 | `principalUpdatedFilter` | `DelegatingFilterProxy` → bean | `/*` | false | `targetFilterLifecycle=true` |
| 9 | `unitSelectedAssuranceFilter` | `DelegatingFilterProxy` → bean | `/api/*` | false | `targetFilterLifecycle=true`, `ignoredUrls=...` |
| 10 | `pdlConsentGivenAssuranceFilter` | `DelegatingFilterProxy` → bean | `/api/*` | false | `targetFilterLifecycle=true`, `ignoredUrls=...` |
| 11 | `hiddenHttpMethodFilter` | `HiddenHttpMethodFilter` | `/*` | false | — |
| 12 | `securityHeadersFilter` | `RSSecurityHeadersFilter` | `/*` | **true** | — |

> **Note:** Filters #1 (`characterEncodingFilter`), #2 (`springSessionRepositoryFilter`), and #6 (`springSecurityFilterChain`) are
> auto-managed by Spring Boot / Spring Security / Spring Session — they must **not** be registered
> manually. Filter #11 (`hiddenHttpMethodFilter`) is **dropped entirely** — this SPA has no use for
> the `_method` override and Spring Boot 3.x defaults it off. See §13.6 for details.

---

## Sub-step 13.1 — Add Spring Boot plugin to Gradle build files

### Root `build.gradle`

Add the Spring Boot Gradle plugin with `apply false` so submodules can opt in:

```gradle
plugins {
    alias(libs.plugins.com.github.ben.manes.versions)
    alias(libs.plugins.com.diffplug.spotless)
    alias(libs.plugins.org.cyclonedx.bom)
    alias(libs.plugins.org.sonarqube)
    // REMOVE: id "org.gretty" version "4.1.10" apply false
    id 'org.springframework.boot' version "${springBootVersion}" apply false
}
```

**Spring Boot version:** The exact version must match whatever is managed by the Intyg BOM
(`se.inera.intyg.bom:platform:1.0.0.14`). Check the BOM's dependency management for the
`org.springframework.boot:spring-boot-dependencies` version to ensure alignment.

⚠️ If the BOM does not manage a Spring Boot version, determine the version from the
existing Spring Framework dependencies on the classpath. The Spring Boot version must be compatible
with the Spring Framework version in the BOM. For Spring Framework 6.1.x–6.2.x, use
Spring Boot 3.2.x–3.4.x. For Spring Framework 6.0.x, use Spring Boot 3.0.x–3.1.x.

Also add the `spring-boot-dependencies` BOM to dependency management so all submodules inherit
managed versions. This can be done either:
- **(A)** In the root `subprojects {}` block:
  ```gradle
  dependencies {
      implementation platform("org.springframework.boot:spring-boot-dependencies:${springBootVersion}")
  }
  ```
- **(B)** Or rely on the existing Intyg BOM if it already imports the Spring Boot BOM transitively.

**Decision:** Check whether `se.inera.intyg.bom:platform:1.0.0.14` already imports
`spring-boot-dependencies`. If yes, no additional BOM import is needed. If no, add option (A).

### Files Changed

| File | Change |
|------|--------|
| `build.gradle` (root) | Remove `org.gretty` plugin line; add `org.springframework.boot` plugin with `apply false` |

---

## Sub-step 13.2 — Switch `web` module from WAR to Spring Boot JAR

### `web/build.gradle`

Replace the WAR/Gretty plugins with Spring Boot:

**Before:**
```gradle
apply plugin: 'org.cyclonedx.bom'
apply plugin: 'org.gretty'
apply plugin: 'war'
```

**After:**
```gradle
apply plugin: 'org.cyclonedx.bom'
apply plugin: 'org.springframework.boot'
```

**Remove the entire `gretty { ... }` block** (lines 7–36 of current `web/build.gradle`).

**Remove the `test` environment override** for `catalina.base`:
```gradle
// REMOVE:
test {
    environment "catalina.base", "${buildDirectory}/catalina.base"
}
```

**Add `spring-boot-starter-web`** to dependencies (replaces standalone `spring-webmvc` +
`jakarta.servlet-api`):

```gradle
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'

    // REMOVE these (now provided by spring-boot-starter-web):
    // implementation "org.springframework:spring-webmvc"
    // testRuntimeOnly "jakarta.servlet:jakarta.servlet-api"
}
```

⚠️ **Keep all other dependencies unchanged in this step.** JPA, JMS, Redis, Prometheus, CXF, and
Logback dependencies remain as-is — they will be migrated in Steps 14–18.

⚠️ **`compileOnly "jakarta.servlet-api"`** — Spring Boot starter-web provides this transitively.
Verify it's no longer needed as `compileOnly`; if other submodules reference
`jakarta.servlet-api`, keep it in those submodules' build files.

### Handling `bootJar` vs `jar`

The Spring Boot plugin disables the standard `jar` task and enables `bootJar` by default for the
module it's applied to. This is the desired behaviour for the `web` module (the application module).

For library submodules (`common`, `persistence`, `logging`, `integration/*`, `redis-cache`), the
Spring Boot plugin is **not applied** — they continue producing regular JARs.

### Files Changed

| File | Change |
|------|--------|
| `web/build.gradle` | Remove `war`, `org.gretty` plugins; remove `gretty {}` block; add `org.springframework.boot` plugin; add `spring-boot-starter-web`; remove `spring-webmvc` and `jakarta.servlet-api` |

---

## Sub-step 13.3 — Create `RehabstodApplication.java`

Create the Spring Boot main class:

**Path:** `web/src/main/java/se/inera/intyg/rehabstod/RehabstodApplication.java`

```java
package se.inera.intyg.rehabstod;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.JmsAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@SpringBootApplication(exclude = {
    DataSourceAutoConfiguration.class,
    HibernateJpaAutoConfiguration.class,
    JpaRepositoriesAutoConfiguration.class,
    JmsAutoConfiguration.class,
    ActiveMQAutoConfiguration.class,
    RedisAutoConfiguration.class,
    RedisRepositoriesAutoConfiguration.class
})
public class RehabstodApplication {

    public static void main(String[] args) {
        SpringApplication.run(RehabstodApplication.class, args);
    }
}
```

### Key Design Decisions

**1. `@SpringBootApplication` base package:**
The class is placed in `se.inera.intyg.rehabstod` — the root package shared by every module.
`@SpringBootApplication` implies `@ComponentScan("se.inera.intyg.rehabstod")`, which
transitively covers all sub-packages across **all** modules: `web`, `common`, `persistence`,
`integration.*`, `logging`, `redis-cache`.

Every `@Configuration`, `@Service`, `@Component`, `@Repository`, and `@RestController` class
across all modules lives under this root. Spring Boot finds and registers them all automatically.

**2. No explicit `@Import` required:**
The old `ApplicationInitializer` had to call `appContext.register(WebSecurityConfig.class, ApplicationConfig.class, ...)` because `AnnotationConfigWebApplicationContext` does not scan — it only loads what you explicitly register. In Spring Boot, `@ComponentScan` replaces this entirely.

Every configuration class that was manually registered in `ApplicationInitializer`
(`ApplicationConfig`, `BasicCacheConfig`, `ServiceConfig`, integration configs, etc.) already
lives in a package under `se.inera.intyg.rehabstod.*`. They are all found by the component
scan. Using `@Import` to re-list them would cause **redundant processing** (Spring deduplicates
them, but it adds confusion and import sprawl). `@Import` is reserved for classes that are
**outside** the scan root — which is not the case here.

Profile-conditional configs work correctly without `@Import`: `@Profile("rhs-srs-stub")` on
`SRSIntegrationStubConfiguration`, `@Profile("!rhs-srs-stub")` on
`SRSIntegrationClientConfiguration`, etc. — the component scan finds all of them; Spring
evaluates the `@Profile` condition at context refresh and activates only the matching ones.

**3. Auto-configuration exclusions (transitional):**

These exclusions exist because the corresponding infrastructure is still managed by hand-rolled
config classes from the pre-Spring Boot era. They are **explicitly temporary** — each one is
removed in the step that properly migrates that domain to Spring Boot auto-configuration.

| Exclusion | Reason | Removed in |
|---|---|---|
| `DataSourceAutoConfiguration` | `PersistenceConfigBase` declares `DataSource` manually | Step 14 |
| `HibernateJpaAutoConfiguration` | `PersistenceConfigBase` declares `EntityManagerFactory` manually | Step 14 |
| `JpaRepositoriesAutoConfiguration` | `PersistenceConfig` has `@EnableJpaRepositories` | Step 14 |
| `JmsAutoConfiguration` | `JmsConfig` declares `ConnectionFactory` and `JmsTemplate` manually | Step 15 |
| `ActiveMQAutoConfiguration` | Same — manual `ConnectionFactory` bean conflicts | Step 15 |
| `RedisAutoConfiguration` | `RedisConfig` declares `JedisConnectionFactory` manually (always active after Step 13.15 removes the profile gate) | Step 17 |
| `RedisRepositoriesAutoConfiguration` | No Spring Data Redis repositories used; avoids repo scanning overhead | Step 17 |

A fully migrated application (after Step 17) will have an empty `exclude` list.

> **Note:** CXF + Spring Boot coexistence has been validated in the
> [intygstjanst migration](https://github.com/sklintyg/intygstjanst) (Step 10 — Spring Boot
> Bootstrap), using the same `ServletRegistrationBean<CXFServlet>` pattern.

---

## Sub-step 13.4 — Remove `@EnableWebMvc` from `WebConfig`

**File:** `web/src/main/java/se/inera/intyg/rehabstod/config/WebConfig.java`

**Before:**
```java
@EnableWebMvc
@Configuration
@EnableAspectJAutoProxy
@ComponentScan({...})
public class WebConfig implements WebMvcConfigurer {
```

**After:**
```java
@Configuration
@EnableAspectJAutoProxy
@ComponentScan({...})
public class WebConfig implements WebMvcConfigurer {
```

### Why Remove `@EnableWebMvc`

`@EnableWebMvc` disables Spring Boot's `WebMvcAutoConfiguration`, which provides:
- Sensible default message converters (Jackson with JSR310 support)
- Static resource handling
- Default content negotiation
- `Validator` auto-configuration
- Error handling (`/error` endpoint)

When `@EnableWebMvc` is removed, Spring Boot auto-configuration activates **but backs off** for
any `WebMvcConfigurer` beans it finds. This means:
- `WebConfig`'s `extendMessageConverters()`, `addInterceptors()`, and
  `configureDefaultServletHandling()` all continue to work.
- Spring Boot adds its defaults **on top** rather than replacing them.

### Impact on `configureDefaultServletHandling()`

Spring Boot's embedded Tomcat does **not** register a default servlet by default.
`configurer.enable()` in `WebConfig.configureDefaultServletHandling()` will register one.
This is fine and matches the current behaviour. However, Spring Boot 3.x logs a deprecation
warning for default servlet handling. If static resources are served by Spring Boot's
`ResourceHttpRequestHandler` (configured in Step 12), consider removing
`configureDefaultServletHandling()` entirely in this step or a follow-up.

### Impact on `CustomObjectMapper`

The current `extendMessageConverters()` replaces the default Jackson `ObjectMapper` with
`CustomObjectMapper` (which sets `WRITE_DATES_AS_TIMESTAMPS=false` and
`FAIL_ON_UNKNOWN_PROPERTIES=false`). With Spring Boot, the preferred approach is:

- **Option A (recommended for this step):** Keep `extendMessageConverters()` as-is. It works.
- **Option B (future cleanup):** Define the `ObjectMapper` as a `@Bean` and configure via
  `application.properties` (`spring.jackson.serialization.write-dates-as-timestamps=false`,
  `spring.jackson.deserialization.fail-on-unknown-properties=false`). Remove
  `extendMessageConverters()`.

**Decision:** Use Option A for this step. Option B can be done as a cleanup later.

### Files Changed

| File | Change |
|------|--------|
| `web/src/main/java/.../config/WebConfig.java` | Remove `@EnableWebMvc` annotation and its import |

---

## Sub-step 13.5 — Collapse dual-context into single Spring Boot context

### Current Architecture

The application currently uses **two** Spring `ApplicationContext` instances:

1. **Root context** (created via `AnnotationConfigWebApplicationContext` + `ContextLoaderListener`)
   — contains all 22 configuration classes, business services, data access, integration, security
2. **Web context** (child, created as DispatcherServlet's context) — contains only `WebConfig`
   with `@ComponentScan` for web controllers

Spring Boot uses a **single** application context. The `DispatcherServlet` is auto-configured and
shares the same context as everything else.

### What Changes

- `ApplicationInitializer` created two contexts. `RehabstodApplication` creates one.
- `WebConfig`'s `@ComponentScan` packages (`se.inera.intyg.rehabstod.web`, etc.) are a subset of
  the `@SpringBootApplication` base package scan — they will be picked up automatically.
- **No bean visibility issues** — in the old dual-context model, root beans were visible to web
  beans (child sees parent). In a single context, everything is visible to everything. This is
  strictly less restrictive, so no breakage expected.

### Potential Issue: Duplicate Component Scanning

Both `ApplicationConfig` and `WebConfig` have `@ComponentScan` annotations. With a single
context, overlapping packages could cause duplicate bean registration warnings. The
`@SpringBootApplication` base scan at `se.inera.intyg.rehabstod` already covers all sub-packages.

**Resolution:** The explicit `@ComponentScan` annotations on `ApplicationConfig`, `WebConfig`,
`ServiceConfig`, `SecurityConfig`, and `JobConfig` are **additive** — Spring deduplicates beans
by class. No duplicates will occur; Spring detects that the same class was found via multiple
scan paths and registers it only once.

### Files Changed

No file changes in this sub-step — this is an architectural consequence of creating
`RehabstodApplication.java` (13.3) and deleting `ApplicationInitializer` (13.13).

---

## Sub-step 13.6 — Convert filter registrations to `FilterRegistrationBean` beans

### Which Filters to Register Manually

Of the 12 original filters in `ApplicationInitializer`, several are **auto-managed by Spring Boot** and
must **NOT** be re-registered:

| # | Filter | Manual registration needed? | Reason |
|---|--------|---------------------------|--------|
| 1 | `characterEncodingFilter` | ❌ **No** | Spring Boot auto-configures `CharacterEncodingFilter` with UTF-8. Set `server.servlet.encoding.force=true` in `application.properties`. |
| 2 | `springSessionRepositoryFilter` | ❌ **No** | `@EnableRedisHttpSession` (on `WebSecurityConfig`) auto-registers this filter. |
| 6 | `springSecurityFilterChain` | ❌ **No** | `@EnableWebSecurity` (on `WebSecurityConfig`) auto-registers the Spring Security filter chain. |
| 11 | `hiddenHttpMethodFilter` | ❌ **Dropped** | Only useful for HTML form PUT/DELETE via `_method` parameter. This SPA does not use it. Spring Boot 3.x defaults this to disabled. |

> ⚠️ **Spring Boot registers `characterEncodingFilter` at order `Ordered.HIGHEST_PRECEDENCE`
> and `springSessionRepositoryFilter` at order `Ordered.HIGHEST_PRECEDENCE + 50` by default.**
> This preserves the ordering requirement (encoding before session).

The remaining **7 filters** must be registered manually as `FilterRegistrationBean` beans.

### Filter Registration Configuration Class

Create a new configuration class (or add to an existing one like `ApplicationConfig`):

**Path:** `web/src/main/java/se/inera/intyg/rehabstod/config/FilterConfig.java`

```java
package se.inera.intyg.rehabstod.config;

import static se.inera.intyg.rehabstod.web.controller.api.SessionStatusController.SESSION_STATUS_CHECK_URI;
import static se.inera.intyg.rehabstod.web.controller.api.SessionStatusController.SESSION_STATUS_EXTEND;
import static se.inera.intyg.rehabstod.web.controller.api.SessionStatusController.SESSION_STATUS_REQUEST_MAPPING;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import se.inera.intyg.rehabstod.auth.RSSecurityHeadersFilter;
import se.inera.intyg.rehabstod.logging.MdcServletFilter;
import se.inera.intyg.rehabstod.logging.MdcUserServletFilter;
import se.inera.intyg.rehabstod.security.filter.PrincipalUpdatedFilter;
import se.inera.intyg.rehabstod.security.filter.RequestContextHolderUpdateFilter;
import se.inera.intyg.rehabstod.security.filter.SessionTimeoutFilter;
import se.inera.intyg.rehabstod.web.filters.PdlConsentGivenAssuranceFilter;
import se.inera.intyg.rehabstod.web.filters.UnitSelectedAssuranceFilter;

@Configuration
public class FilterConfig {

    // Order constants — gaps of 10 allow future insertion without reordering.
    // Filters auto-registered by Spring Boot fill the lowest order values:
    //   CharacterEncodingFilter  → Ordered.HIGHEST_PRECEDENCE       (auto)
    //   springSessionRepository  → Ordered.HIGHEST_PRECEDENCE + 50  (auto)
    //   springSecurityFilterChain → 0 (Spring Security default)     (auto)
    //
    // Our custom filters are placed around the auto-registered ones to preserve
    // the original execution order from ApplicationInitializer.

    private static final int ORDER_REQUEST_CONTEXT_HOLDER = -90;
    private static final int ORDER_MDC_SERVLET = -80;
    private static final int ORDER_SESSION_TIMEOUT = -70;
    // springSecurityFilterChain is at order 0 (auto)
    private static final int ORDER_MDC_USER = 10;
    private static final int ORDER_PRINCIPAL_UPDATED = 20;
    private static final int ORDER_UNIT_SELECTED = 30;
    private static final int ORDER_PDL_CONSENT = 40;
    private static final int ORDER_SECURITY_HEADERS = 100;

    // --- Filter #3 ---
    @Bean
    public FilterRegistrationBean<RequestContextHolderUpdateFilter>
            requestContextHolderUpdateFilterRegistration() {
        FilterRegistrationBean<RequestContextHolderUpdateFilter> reg =
            new FilterRegistrationBean<>();
        reg.setFilter(new RequestContextHolderUpdateFilter());
        reg.addUrlPatterns("/*");
        reg.setOrder(ORDER_REQUEST_CONTEXT_HOLDER);
        reg.setName("requestContextHolderUpdateFilter");
        return reg;
    }

    // --- Filter #4 ---
    @Bean
    public FilterRegistrationBean<MdcServletFilter> mdcServletFilterRegistration(
            MdcServletFilter mdcServletFilter) {
        FilterRegistrationBean<MdcServletFilter> reg = new FilterRegistrationBean<>();
        reg.setFilter(mdcServletFilter);
        reg.addUrlPatterns("/*");
        reg.setOrder(ORDER_MDC_SERVLET);
        reg.setName("mdcServletFilter");
        return reg;
    }

    // --- Filter #5 ---
    @Bean
    public FilterRegistrationBean<SessionTimeoutFilter> sessionTimeoutFilterRegistration() {
        SessionTimeoutFilter filter = new SessionTimeoutFilter();
        filter.setSkipRenewSessionUrls(SESSION_STATUS_CHECK_URI);
        FilterRegistrationBean<SessionTimeoutFilter> reg = new FilterRegistrationBean<>();
        reg.setFilter(filter);
        reg.addUrlPatterns("/*");
        reg.setOrder(ORDER_SESSION_TIMEOUT);
        reg.setName("sessionTimeoutFilter");
        return reg;
    }

    // --- Filter #7 ---
    @Bean
    public FilterRegistrationBean<MdcUserServletFilter> mdcUserServletFilterRegistration(
            MdcUserServletFilter mdcUserServletFilter) {
        FilterRegistrationBean<MdcUserServletFilter> reg = new FilterRegistrationBean<>();
        reg.setFilter(mdcUserServletFilter);
        reg.addUrlPatterns("/*");
        reg.setOrder(ORDER_MDC_USER);
        reg.setName("mdcUserServletFilter");
        return reg;
    }

    // --- Filter #8 ---
    @Bean
    public FilterRegistrationBean<PrincipalUpdatedFilter> principalUpdatedFilterRegistration(
            PrincipalUpdatedFilter principalUpdatedFilter) {
        FilterRegistrationBean<PrincipalUpdatedFilter> reg = new FilterRegistrationBean<>();
        reg.setFilter(principalUpdatedFilter);
        reg.addUrlPatterns("/*");
        reg.setOrder(ORDER_PRINCIPAL_UPDATED);
        reg.setName("principalUpdatedFilter");
        return reg;
    }

    // --- Filter #9 ---
    @Bean
    public FilterRegistrationBean<UnitSelectedAssuranceFilter>
            unitSelectedAssuranceFilterRegistration(
                UnitSelectedAssuranceFilter unitSelectedAssuranceFilter) {
        unitSelectedAssuranceFilter.setIgnoredUrls(
            SESSION_STATUS_CHECK_URI
                + ",/api/config,/api/user,/api/user/andraenhet");
        FilterRegistrationBean<UnitSelectedAssuranceFilter> reg =
            new FilterRegistrationBean<>();
        reg.setFilter(unitSelectedAssuranceFilter);
        reg.addUrlPatterns("/api/*");
        reg.setOrder(ORDER_UNIT_SELECTED);
        reg.setName("unitSelectedAssuranceFilter");
        return reg;
    }

    // --- Filter #10 ---
    @Bean
    public FilterRegistrationBean<PdlConsentGivenAssuranceFilter>
            pdlConsentGivenAssuranceFilterRegistration(
                PdlConsentGivenAssuranceFilter pdlConsentGivenAssuranceFilter) {
        pdlConsentGivenAssuranceFilter.setIgnoredUrls(
            SESSION_STATUS_CHECK_URI
                + "," + SESSION_STATUS_REQUEST_MAPPING + SESSION_STATUS_EXTEND
                + ",/api/config,/api/user,/api/user/giveconsent,/api/sjukfall/summary"
                + ",/api/stub,/api/sickleaves,/api/lu,/api/testability,/api/log/error");
        FilterRegistrationBean<PdlConsentGivenAssuranceFilter> reg =
            new FilterRegistrationBean<>();
        reg.setFilter(pdlConsentGivenAssuranceFilter);
        reg.addUrlPatterns("/api/*");
        reg.setOrder(ORDER_PDL_CONSENT);
        reg.setName("pdlConsentGivenAssuranceFilter");
        return reg;
    }

    // --- Filter #12 ---
    @Bean
    public FilterRegistrationBean<RSSecurityHeadersFilter> securityHeadersFilterRegistration() {
        FilterRegistrationBean<RSSecurityHeadersFilter> reg = new FilterRegistrationBean<>();
        reg.setFilter(new RSSecurityHeadersFilter());
        reg.addUrlPatterns("/*");
        reg.setOrder(ORDER_SECURITY_HEADERS);
        reg.setName("securityHeadersFilter");
        reg.setMatchAfter(true);  // equivalent of dispatch=true in the old registration
        return reg;
    }
}
```

### Init Parameter Migration

The old `ApplicationInitializer` set init parameters via `setInitParameter()` on
`FilterRegistration.Dynamic`. In Spring Boot, there is no `FilterRegistration.Dynamic`. Instead:

| Filter | Old init parameter | Spring Boot equivalent |
|--------|-------------------|----------------------|
| `sessionTimeoutFilter` | `skipRenewSessionUrls` | Call setter `setSkipRenewSessionUrls()` directly on the filter instance |
| `unitSelectedAssuranceFilter` | `ignoredUrls`, `targetFilterLifecycle` | Call setter `setIgnoredUrls()` directly; `targetFilterLifecycle` is irrelevant — no `DelegatingFilterProxy` |
| `pdlConsentGivenAssuranceFilter` | `ignoredUrls`, `targetFilterLifecycle` | Same as above |
| `principalUpdatedFilter` | `targetFilterLifecycle` | Irrelevant — no `DelegatingFilterProxy` |

⚠️ **Verify that `SessionTimeoutFilter`, `UnitSelectedAssuranceFilter`, and
`PdlConsentGivenAssuranceFilter` have setter methods** for their init parameters. If they
currently read init parameters from `FilterConfig.getInitParameter()` in `init()`, they must
be refactored to accept values via constructor or setter before `FilterRegistrationBean` can
inject them. This is a small code change to those filter classes.

### Preventing Double-Registration

When Spring Boot detects a `Filter` bean in the context, it auto-registers it with default
settings. Since `PrincipalUpdatedFilter`, `UnitSelectedAssuranceFilter`,
`PdlConsentGivenAssuranceFilter`, and `MdcServletFilter` / `MdcUserServletFilter` are defined
as `@Bean`s (in `ApplicationConfig`) and also registered via `FilterRegistrationBean`, Spring
Boot would register them **twice**.

**Fix:** For every filter bean that is explicitly registered via `FilterRegistrationBean`,
ensure that the bean definition itself is **not** auto-registered by Spring Boot. Two approaches:

- **(A) Preferred:** The `FilterRegistrationBean` already references the bean. Spring Boot
  detects that a `FilterRegistrationBean` exists for a given `Filter` bean and **skips
  auto-registration** for it. No extra work needed if the bean names match.
- **(B) Fallback:** Add a `FilterRegistrationBean` with `.setEnabled(false)` for each filter
  bean that should not be auto-registered.

### Files Changed

| File | Change | Notes |
|------|--------|-------|
| `web/src/main/java/.../config/FilterConfig.java` | **New file** | All 7 manual `FilterRegistrationBean` beans |
| `SessionTimeoutFilter.java` | Add setter for `skipRenewSessionUrls` if currently only read from `FilterConfig` | Verify current implementation |
| `UnitSelectedAssuranceFilter.java` | Add setter for `ignoredUrls` if needed | Verify current implementation |
| `PdlConsentGivenAssuranceFilter.java` | Add setter for `ignoredUrls` if needed | Verify current implementation |

---

## Sub-step 13.7 — Register `CXFServlet` as `ServletRegistrationBean`

### Current Registration (ApplicationInitializer lines 171–175)

```java
ServletRegistration.Dynamic cxfServlet =
    servletContext.addServlet("services", new CXFServlet());
cxfServlet.setLoadOnStartup(1);
cxfServlet.addMapping("/services/*");
```

### Spring Boot Replacement

Add to `ApplicationConfig.java` (or a new `CxfConfig.java`):

```java
@Bean
public ServletRegistrationBean<CXFServlet> cxfServletRegistration() {
    ServletRegistrationBean<CXFServlet> registration =
        new ServletRegistrationBean<>(new CXFServlet(), "/services/*");
    registration.setName("services");
    registration.setLoadOnStartup(1);
    return registration;
}
```

### DispatcherServlet URL Mapping

Spring Boot auto-configures the `DispatcherServlet` at `/` — exactly matching the current manual
mapping in `ApplicationInitializer`. **No additional configuration needed.**

If a different mapping is desired (e.g., to avoid the DispatcherServlet catching `/services/*`
requests), configure:
```properties
# Not needed — DispatcherServlet at / is the default and CXFServlet at /services/* takes precedence
# spring.mvc.servlet.path=/
```

### CXF Bus Configuration

The `SpringBus` bean and `@ImportResource("classpath:META-INF/cxf/cxf.xml")` in
`ApplicationConfig` remain unchanged. The CXF `Bus` bean is picked up automatically by the
`CXFServlet` from the Spring context.

⚠️ **Do NOT add `cxf-spring-boot-starter-jaxws`.** That starter would enable CXF
auto-configuration, which conflicts with the existing manual configuration. Keep using manual
CXF configuration.

### Files Changed

| File | Change |
|------|--------|
| `web/src/main/java/.../config/ApplicationConfig.java` | Add `cxfServletRegistration()` bean method |

---

## Sub-step 13.8 — Register `HttpSessionEventPublisher` as `@Bean`

### Current Registration (ApplicationInitializer line 178)

```java
servletContext.addListener(new HttpSessionEventPublisher());
```

### Spring Boot Replacement

Add to `WebSecurityConfig.java` (or any `@Configuration` class):

```java
@Bean
public HttpSessionEventPublisher httpSessionEventPublisher() {
    return new HttpSessionEventPublisher();
}
```

Spring Boot automatically detects `ServletContextListener` beans and registers them.

### `RequestContextListener`

The current code also registers `RequestContextListener` (line 179). In Spring Boot, the
`RequestContextFilter` is auto-configured and replaces the need for `RequestContextListener`.
**Do not register `RequestContextListener` as a bean** — it would conflict with Spring Boot's
`RequestContextFilter`.

### Files Changed

| File | Change |
|------|--------|
| `web/src/main/java/.../config/WebSecurityConfig.java` | Add `httpSessionEventPublisher()` bean |

---

## Sub-step 13.9 — Replace custom Logback setup with Spring Boot ECS structured logging

### Current Behaviour

**`ApplicationInitializer` lines 60–61:**
```java
servletContext.setInitParameter("logbackConfigParameter", "logback.file");
servletContext.addListener(new LogbackConfiguratorContextListener());
```

`LogbackConfiguratorContextListener` reads `-Dlogback.file` system property and configures
Logback from that file. The fallback is `classpath:logback-spring.xml`.

**Logback XML chain:**
- `devops/dev/config/logback-spring.xml` → includes `logback/logback-spring-base.xml`
- `logback-spring-base.xml` defines two appenders:
  - `CONSOLE` — plain text `%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n`
  - `ECS_JSON_CONSOLE` — uses `co.elastic.logging:logback-ecs-encoder` (commented out in dev)

**ECS dependency:** `co.elastic.logging:logback-ecs-encoder` in `web/build.gradle`.

### Spring Boot Replacement — Native ECS Structured Logging

Spring Boot 3.4+ provides built-in ECS structured logging, eliminating the need for the Elastic
encoder library and custom logback XML files. Instead of carrying forward the old logback XML
chain, we switch to Spring Boot's native structured logging in this step.

**Add to `application.properties`:**
```properties
logging.structured.format.console=ecs
logging.structured.ecs.service.name=rehabstod
logging.structured.ecs.service.environment=${spring.profiles.active:default}
```

This produces ECS-formatted JSON on stdout — the same format the old `EcsEncoder` produced, but
without any external library or XML configuration.

### What Gets Removed

| Item | Action | Reason |
|------|--------|--------|
| `LogbackConfiguratorContextListener.java` | **Delete** | Spring Boot manages Logback natively; no `ServletContextListener` needed |
| `devops/dev/config/logback-spring.xml` | **Delete** | No longer needed; Spring Boot ECS logging is configured via properties |
| `web/src/main/resources/logback/logback-spring-base.xml` | **Delete** | The `CONSOLE` and `ECS_JSON_CONSOLE` appenders are replaced by Spring Boot structured logging |
| `co.elastic.logging:logback-ecs-encoder` dependency | **Remove from `web/build.gradle`** | Replaced by Spring Boot native ECS support |
| `-Dlogback.file=...` JVM arg | **Remove from `bootRun` / run configs** | No longer applicable |

### Impact on `UserConverter` and `SessionConverter`

The current logback XML does **not** reference `UserConverter` or `SessionConverter` in any
active appender pattern — they were only used in the old infra logging setup. The MDC-based
approach via `MdcServletFilter` and `MdcUserServletFilter` already populates `SESSION_ID` and
`USER_ID` into MDC, which Spring Boot's ECS structured output includes automatically.

**Decision:** `UserConverter.java` and `SessionConverter.java` are unused by the current logback
config. They can be deleted in this step or kept for a cleanup pass. The MDC fields
(`session.id`, `user.id`) populated by the MDC filters are included in ECS output automatically.

### Dev Logging Override

For local development, plain text console output may be preferred over ECS JSON. Use a
profile-specific property to disable structured logging in dev:

**In `devops/dev/config/application-dev.properties`:**
```properties
# Plain text console logging for local development (overrides ECS JSON)
logging.structured.format.console=
```

This gives developers readable logs locally while production uses ECS JSON.

### Files Changed

| File | Change |
|------|--------|
| `web/src/main/resources/application.properties` | Add `logging.structured.*` properties |
| `web/build.gradle` | Remove `co.elastic.logging:logback-ecs-encoder` dependency |
| `devops/dev/config/application-dev.properties` | Add `logging.structured.format.console=` override for plain text dev logging |

### Files Deleted

| File | Reason |
|------|--------|
| `logging/src/main/java/.../logging/LogbackConfiguratorContextListener.java` | Replaced by Spring Boot native logging |
| `web/src/main/resources/logback/logback-spring-base.xml` | Replaced by Spring Boot ECS structured logging |
| `devops/dev/config/logback-spring.xml` | Replaced by Spring Boot ECS structured logging |
| `web/src/main/java/.../logging/UserConverter.java` | Unused — MDC fields cover user context |
| `web/src/main/java/.../logging/SessionConverter.java` | Unused — MDC fields cover session context |

---

## Sub-step 13.10 — Remove `web.xml`

### Current Content

```xml
<web-app version="3.0" metadata-complete="true">
  <servlet>
    <servlet-name>metrics</servlet-name>
    <servlet-class>io.prometheus.client.servlet.jakarta.exporter.MetricsServlet</servlet-class>
  </servlet>
  <servlet-mapping>
    <servlet-name>metrics</servlet-name>
    <url-pattern>/metrics</url-pattern>
  </servlet-mapping>
</web-app>
```

### Migration

The Prometheus `MetricsServlet` registration moves to a `ServletRegistrationBean`. Add to
`ApplicationConfig.java` (or a separate config class):

```java
@Bean
public ServletRegistrationBean<io.prometheus.client.servlet.jakarta.exporter.MetricsServlet>
        metricsServletRegistration() {
    return new ServletRegistrationBean<>(
        new io.prometheus.client.servlet.jakarta.exporter.MetricsServlet(), "/metrics");
}
```

> **Note:** This is a temporary measure. Step 16 replaces the Prometheus simple-client servlet
> with Spring Boot Actuator + Micrometer. The `metricsServletRegistration` bean and the
> `simpleclient_servlet` dependency will be removed in that step.

### Files Deleted

| File | Notes |
|------|-------|
| `web/src/main/webapp/WEB-INF/web.xml` | Delete entirely |

### Files Changed

| File | Change |
|------|--------|
| `web/src/main/java/.../config/ApplicationConfig.java` | Add `metricsServletRegistration()` bean |

---

## Sub-step 13.11 — Remove `tomcat-gretty.xml` and Gretty config

### Files Deleted

| File | Notes |
|------|-------|
| `web/tomcat-gretty.xml` | Gretty-specific server configuration; not used by Spring Boot |

### Equivalent Spring Boot Configuration

The settings from `tomcat-gretty.xml` that need equivalents:

| Gretty/Tomcat Setting | Spring Boot Equivalent | Notes |
|---|---|---|
| `RemoteIpValve` (X-Forwarded-For / X-Forwarded-Proto) | `server.forward-headers-strategy=native` | Spring Boot configures Tomcat's `RemoteIpValve` automatically |
| `ErrorReportValve` (showReport, showServerInfo) | *(Spring Boot defaults)* | Spring Boot disables server info/stack traces by default — no property needed |
| HTTP port | `server.port=8030` | Overridden per environment via `-Dserver.port` |
| Connection timeout 20s | `server.tomcat.connection-timeout=20s` | |
| `contextPath=/` | *(default)* | Spring Boot defaults to `/` — no property needed |

These go in `application.properties` — see §13.12.

---

## Sub-step 13.12 — Migrate embedded Tomcat settings to `application.properties`

Add the following to `web/src/main/resources/application.properties`:

```properties
################################################
#
# Spring Boot Server Configuration
#
################################################
server.port=8030
server.forward-headers-strategy=native
server.tomcat.connection-timeout=20s

# Character encoding (replaces CharacterEncodingFilter manual registration)
server.servlet.encoding.force=true

# ECS structured logging (replaces LogbackConfiguratorContextListener + logback XML + EcsEncoder)
logging.structured.format.console=ecs
logging.structured.ecs.service.name=rehabstod
logging.structured.ecs.service.environment=${spring.profiles.active:default}
```

> **Omitted (redundant defaults):** `server.servlet.context-path=/` (default),
> `server.servlet.encoding.charset=UTF-8` (default), `server.servlet.encoding.enabled=true`
> (default), `spring.mvc.hiddenmethod.filter.enabled` (see note below).

### HiddenHttpMethodFilter

Spring Boot 3.x defaults `spring.mvc.hiddenmethod.filter.enabled=false`. The current app
registers `HiddenHttpMethodFilter` in `ApplicationInitializer`, but it is only useful for
HTML form submissions that need PUT/DELETE via a hidden `_method` field. This is a REST/SPA
application — **the filter is not needed**. Do not add the property; the filter is simply
dropped.

If any endpoint relies on `_method` form parameter (unlikely in this SPA), add
`spring.mvc.hiddenmethod.filter.enabled=true` to restore it.

### Dev Override

Update `devops/dev/config/application-dev.properties`:

```properties
# Plain text console logging for local development (overrides ECS JSON)
logging.structured.format.console=
```

In the old Gretty setup, the port was dynamic based on `instance` variable. With Spring Boot,
the port is set via `server.port` property or `-Dserver.port=...` command-line argument.

---

## Sub-step 13.13 — Delete legacy files

### 13.13a — Delete `BasicCacheConfiguration.java` and `basic-cache-config.xml`

> ⚠️ **Critical — must happen before the Spring Boot context starts.**

The `redis-cache/core` module contains two classes that are no longer needed and **will cause
a bean conflict** in Spring Boot if left in place:

**`BasicCacheConfiguration.java`** (`se.inera.intyg.rehabstod.rediscache.core`) is annotated
`@Configuration @EnableCaching`. Because it lives under `se.inera.intyg.rehabstod.*`,
`@SpringBootApplication`'s component scan will pick it up *in addition to* `BasicCacheConfig`
in the `web` module. Both classes declare beans named `jedisConnectionFactory`, `cacheManager`,
and `rediscache` — a direct bean conflict that will prevent the application from starting.

`BasicCacheConfiguration` is already dead code: it is only referenced from `basic-cache-config.xml`,
and that XML file has never been loaded in the Java-config setup (`ApplicationInitializer` does
not import it, nor does any `@ImportResource` reference it). It has been superseded by
`BasicCacheConfig.java` (web module) since Step 7.

| File | Action | Reason |
|------|--------|--------|
| `redis-cache/core/src/main/java/se/inera/intyg/rehabstod/rediscache/core/BasicCacheConfiguration.java` | **Delete** | Duplicate of `BasicCacheConfig`; would cause bean conflict via component scan |
| `redis-cache/core/src/main/resources/basic-cache-config.xml` | **Delete** | Legacy Spring XML; never loaded in current Java-config setup |

The remaining classes in `redis-cache/core` — `CacheFactory`, `RedisCacheOptionsSetter`,
`ConnectionStringUtil` — are plain Java classes (no `@Configuration`). They are **not**
auto-picked-up by the component scan, cause no conflicts, and are still actively used:

- `CacheFactory` is instantiated directly in `BasicCacheConfig.RedisCacheConfig.cacheManager()`
- `RedisCacheOptionsSetter` is declared as a `@Bean` in `BasicCacheConfig` and injected into
  `IntygProxyServiceHsaCacheConfiguration` (integration module)
- `ConnectionStringUtil` is used inside `BasicCacheConfig`'s connection string parsing

> **Step 17 follow-up:** Once Redis configuration is fully migrated in Step 17, the entire
> `redis-cache` module should be dissolved — move `CacheFactory`, `RedisCacheOptionsSetter`,
> and `ConnectionStringUtil` into `web/src/main/java/se/inera/intyg/rehabstod/config/cache/`
> (updating all imports), remove `redis-cache` from `settings.gradle` and all `build.gradle`
> dependency declarations, and delete the module directory.

---

### 13.13b — Delete `ApplicationInitializer.java`

#### Pre-deletion Checklist

Before deleting, verify that **every responsibility** of `ApplicationInitializer` has been migrated:

| Responsibility | Migrated to | Sub-step |
|---|---|---|
| `LogbackConfiguratorContextListener` registration | Spring Boot ECS structured logging properties | 13.9 |
| Root `AnnotationConfigWebApplicationContext` + 22 config classes | `@SpringBootApplication` component scan (no `@Import` needed) | 13.3 |
| `ContextLoaderListener` | Spring Boot internal | 13.3 |
| `DispatcherServlet` + `WebConfig` context | Spring Boot auto-config | 13.4, 13.5 |
| 12 filter registrations → 7 manual beans + 3 auto-managed + 1 dropped | `FilterConfig.java` + Spring Boot auto-config | 13.6 |
| `CXFServlet` at `/services/*` | `ServletRegistrationBean` | 13.7 |
| `HttpSessionEventPublisher` listener | `@Bean` | 13.8 |
| `RequestContextListener` | Spring Boot `RequestContextFilter` (auto) | 13.8 |

#### Files Deleted

| File | Notes |
|------|-------|
| `web/src/main/java/se/inera/intyg/rehabstod/config/ApplicationInitializer.java` | All responsibilities migrated |

---

## Sub-step 13.14 — Update `ApplicationConfig` — remove `@ImportResource` for CXF

### Current

```java
@ImportResource({"classpath:META-INF/cxf/cxf.xml"})
```

### Decision

The `classpath:META-INF/cxf/cxf.xml` is the standard CXF bootstrap XML that configures the
default CXF bus features. With the `SpringBus` bean already defined explicitly, this
`@ImportResource` **may** still be needed if:
- CXF features (like logging) are configured in `cxf.xml`
- CXF interceptors or extensions are loaded via this file

**Action:** Test whether removing `@ImportResource` causes CXF endpoints to fail. If CXF works
without it (because the `SpringBus` bean is sufficient), remove it. If not, keep it.

⚠️ The `@ImportResource` annotation loads XML-based Spring bean definitions. Spring Boot can
handle this, but it's a legacy pattern. If kept, it remains functional.

### Also in `ApplicationConfig`

Remove the `@PropertySource` annotation:

**Before:**
```java
@PropertySource(
    ignoreResourceNotFound = true,
    value = {"classpath:application.properties", "file:${dev.config.file}"})
```

**After:** Remove entirely.

**Why:** Spring Boot automatically loads `classpath:application.properties`. The dev override
file (`file:${dev.config.file}`) can be loaded via:
```properties
# In application.properties or via command line:
spring.config.import=optional:file:${dev.config.file}
```

Or via the command-line argument:
```
--spring.config.additional-location=file:${dev.config.file}
```

This replaces the manual `@PropertySource` with Spring Boot's native externalized configuration.

### The `PropertySourcesPlaceholderConfigurer` Bean

```java
@Bean
public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
    return new PropertySourcesPlaceholderConfigurer();
}
```

**Remove this bean.** Spring Boot auto-configures property placeholder resolution. An explicit
`PropertySourcesPlaceholderConfigurer` is not needed and can interfere with Spring Boot's
property resolution order.

### Files Changed

| File | Change |
|------|--------|
| `web/src/main/java/.../config/ApplicationConfig.java` | Remove `@PropertySource`; remove `propertyConfigInDev()` bean; test removing `@ImportResource` |
| `web/src/main/resources/application.properties` | Add `spring.config.import=optional:file:${dev.config.file:}` |

---

## Sub-step 13.15 — Simplify and rename `BasicCacheConfig` → `RedisConfig`

### Current

`BasicCacheConfig.RedisCacheConfig` is guarded by `@Profile({"caching-enabled", "prod", "qa"})`.
When none of those profiles are active, `BasicCacheConfig.NoOpConfig` kicks in via
`@Conditional(NoCachingCondition.class)` to provide a `NoOpCacheManager`.

This dual-config pattern exists because caching was originally an optional feature toggled
per environment. In the Spring Boot world all environments (including local dev) use a Redis
container, so the profile gate is no longer needed — cache configuration should always load.

The class is also renamed to `RedisConfig` to reflect its actual responsibility (Redis
connection and cache management), consistent with the config naming convention used for
`TlsConfig`, `JmsConfig`, etc.

### Target

**Rename:** `BasicCacheConfig.java` → `RedisConfig.java`
(same package: `se.inera.intyg.rehabstod.config`)

```java
// Before
@Configuration
@Profile({"caching-enabled", "prod", "qa"})
static class RedisCacheConfig { ... }

@Configuration
@Conditional(NoCachingCondition.class)
static class NoOpConfig {
    @Bean
    public CacheManager cacheManager() {
        return new NoOpCacheManager();
    }
}
```

```java
// After — rename outer class, remove @Profile and NoOpConfig entirely
@Configuration
@EnableCaching
public class RedisConfig {
    @Configuration
    static class RedisCacheConfig { ... } // @Profile removed
    // NoOpConfig: deleted
}
```

### Files Deleted / Renamed

| Action | File | Reason |
|--------|------|--------|
| Rename | `BasicCacheConfig.java` → `RedisConfig.java` | Clearer name; reflects Redis responsibility |
| Delete | `NoCachingCondition.java` | No longer needed once `NoOpConfig` is removed |

### Impact

- `caching-enabled` profile no longer has any effect on cache configuration. Remove it from:
  - `README.md` (dev profile example on line 24)
  - `web/build.gradle` bootRun profile list (line 27)
  - Any environment-specific property files or Docker run scripts that reference it
- Local development requires a Redis instance to be running (e.g. via `docker compose up redis`).
- The `RedisAutoConfiguration` / `RedisRepositoriesAutoConfiguration` exclusions in
  `RehabstodApplication` remain — we still use a manual `JedisConnectionFactory`.

---

## Sub-step 13.16 — Rename and clean up configuration classes

As part of the Spring Boot migration, several config classes in `web/config/` are renamed to
reflect their actual responsibility and to follow a consistent `*Config` naming pattern.

### Renames

| Old file | New file | Change |
|----------|----------|--------|
| `SecurityConfig.java` | `TlsConfig.java` | Name was misleading — the class only configures a TLS-trusting `RestTemplate`. `WebSecurityConfig` is the real security config. Also remove redundant `@ComponentScan({"se.inera.intyg.rehabstod.security.authorities"})` — `@SpringBootApplication` root scan already covers this package. |
| `IaConfiguration.java` | `IaConfig.java` | Rename to align with `*Config` convention. Verify whether `@Import(IaStubConfiguration.class)` is still needed — if `IaStubConfiguration` is in the component scan path it is redundant; remove if so. |

### Deletions

| File | Reason |
|------|--------|
| `SjukfallConfig.java` | Empty `@Configuration` class with no beans; provides no value. |

### No-Change

`ApplicationConfig`, `JobConfig`, `JmsConfig`, `ServiceConfig`, `WebConfig`, `WebSecurityConfig`
keep their names — they are already clear.

> **Integration module naming (not in Step 13 scope):** The `integration/hsa-integration-intyg-proxy-service`
> module has `IntygProxyServiceConfiguration` and `IntygProxyServiceHsaCacheConfiguration`.
> Consolidating or renaming these (e.g. `HsaConfig`) is a separate cleanup within that module's
> scope and does not affect the web module bootstrap.

---

## Sub-step 13.17 — Verify build, tests, startup, endpoints

### Build Verification

```bash
./gradlew clean build
```

**Expected:** Build succeeds. `bootJar` task produces
`web/build/libs/rehabstod-web-<version>.jar`.

### Test Verification

```bash
./gradlew test
```

**Expected:** All existing tests pass. Watch for:
- Tests that depend on WAR packaging or servlet-container-specific behaviour
- Tests that reference `catalina.base` environment variable (removed from `web/build.gradle`)
- Tests that use `@WebMvcTest` or `@SpringBootTest` (these should work, but may need
  `@ActiveProfiles` to be set)

### Startup Verification

```bash
java -jar web/build/libs/rehabstod-web-<version>.jar \
  --spring.profiles.active=dev,rhs-samtyckestjanst-stub,rhs-sparrtjanst-stub,rhs-srs-stub,caching-enabled,rhs-security-test,ia-stub,testability \
  -Dapplication.dir=devops/dev \
  -Ddev.config.file=devops/dev/config/application-dev.properties
```

Or using Gradle:
```bash
./gradlew bootRun --args='--spring.profiles.active=dev,rhs-samtyckestjanst-stub,rhs-sparrtjanst-stub,rhs-srs-stub,caching-enabled,rhs-security-test,ia-stub,testability'
```

### Endpoint Verification Checklist

| Endpoint | Method | Expected |
|----------|--------|----------|
| `GET /api/config` | REST | 200 OK with config JSON |
| `GET /metrics` | Prometheus | 200 OK with Prometheus metrics (temporary, replaced in Step 16) |
| `GET /services` | CXF | CXF service listing page |
| `GET /saml2/service-provider-metadata/siths` | SAML | SAML metadata XML |
| `POST /login/saml2/sso/siths` | SAML | SAML login flow (test with IdP) |
| `POST /logout/saml2/slo` | SAML | SAML logout flow |
| `GET /api/session-auth-check/ping` | REST | 200 OK (permits all) |
| CXF SOAP stub endpoints | SOAP | Verify stubs respond when stub profiles active |

### SAML End-to-End Test

⚠️ **This is the highest-risk verification.** SAML 2.0 login/logout with session management
via Redis must be tested end-to-end:

1. Access a protected URL → redirect to IdP
2. Authenticate at IdP → redirect back to ACS URL
3. Session created in Redis → verify with `redis-cli KEYS *`
4. Access protected API → 200 OK
5. Logout → SLO request to IdP → session destroyed in Redis

---

## Risk Assessment

| Risk | Likelihood | Impact | Mitigation |
|------|-----------|--------|------------|
| **Filter order changes** break authentication/session flow | Medium | High | Compare filter execution order before/after using debug logging. Set `logging.level.org.springframework.web.filter=DEBUG` during testing. |
| **`@EnableWebMvc` removal** changes MVC behaviour (content negotiation, converters) | Low | Medium | The `WebMvcConfigurer` methods are preserved. Spring Boot auto-config backs off gracefully. Test all REST endpoints for correct JSON responses. |
| **SAML session management** fails with embedded Tomcat | Low | High | SAML2 login was validated with Spring Security 6.x in the current app. `@EnableRedisHttpSession` remains. The only change is embedded vs external Tomcat — session handling is identical. |
| **CXF Bus** not initialized correctly | Low | Medium | `SpringBus` bean + `@ImportResource("classpath:META-INF/cxf/cxf.xml")` remain. Test SOAP stub endpoints. |
| **Dual-context → single-context** causes bean conflicts | Low | Low | Spring deduplicates component-scanned beans. No conflicting bean names expected. |
| **Property resolution order** changes | Medium | Medium | Spring Boot has a different property resolution order than `@PropertySource`. Verify all `${...}` placeholders resolve. Test with both default and dev profiles. |
| **`configureDefaultServletHandling()`** deprecation warning | Certain | Low | Functional — only a warning. Can be addressed in follow-up. |
| **`catalina.base` references** in tests or code | Low | Low | Grep for `catalina.base` — only used in `web/build.gradle` test block (removed). |

---

## Files Summary

### New Files

| File | Purpose |
|------|---------|
| `web/src/main/java/se/inera/intyg/rehabstod/RehabstodApplication.java` | Spring Boot main class |
| `web/src/main/java/se/inera/intyg/rehabstod/config/FilterConfig.java` | `FilterRegistrationBean` definitions for 7 custom filters |

### Modified Files

| File | Change |
|------|--------|
| `build.gradle` (root) | Remove `org.gretty` plugin; add `org.springframework.boot` plugin |
| `web/build.gradle` | Remove `war`/`org.gretty`; add `org.springframework.boot`; add `spring-boot-starter-web`; remove `spring-webmvc`/`jakarta.servlet-api`; remove `gretty {}` block; remove `co.elastic.logging:logback-ecs-encoder` |
| `web/src/main/java/.../config/WebConfig.java` | Remove `@EnableWebMvc` |
| `web/src/main/java/.../config/ApplicationConfig.java` | Add `cxfServletRegistration()` + `metricsServletRegistration()` beans; remove `@PropertySource`; remove `propertyConfigInDev()`; evaluate removing `@ImportResource` |
| `web/src/main/java/.../config/WebSecurityConfig.java` | Add `httpSessionEventPublisher()` bean |
| `web/src/main/resources/application.properties` | Add server config, encoding, ECS structured logging, dev config import |
| `devops/dev/config/application-dev.properties` | Add plain-text logging override for dev |
| `SessionTimeoutFilter.java` | Add setter for `skipRenewSessionUrls` (if needed) |
| `UnitSelectedAssuranceFilter.java` | Add setter for `ignoredUrls` (if needed) |
| `PdlConsentGivenAssuranceFilter.java` | Add setter for `ignoredUrls` (if needed) |

### Deleted Files

| File | Reason |
|------|--------|
| `web/src/main/java/se/inera/intyg/rehabstod/config/ApplicationInitializer.java` | Replaced by `RehabstodApplication` + `FilterConfig` |
| `web/src/main/webapp/WEB-INF/web.xml` | Prometheus servlet moved to `ServletRegistrationBean`; JAR packaging has no `web.xml` |
| `web/tomcat-gretty.xml` | Gretty removed; Tomcat settings moved to `application.properties` |
| `logging/src/main/java/.../logging/LogbackConfiguratorContextListener.java` | Replaced by Spring Boot native logging |
| `web/src/main/resources/logback/logback-spring-base.xml` | Replaced by Spring Boot ECS structured logging |
| `devops/dev/config/logback-spring.xml` | Replaced by Spring Boot ECS structured logging |
| `web/src/main/java/.../logging/UserConverter.java` | Unused — MDC fields cover user context |
| `web/src/main/java/.../logging/SessionConverter.java` | Unused — MDC fields cover session context |

### NOT Changed in This Step (deferred)

| File/Concern | Deferred to |
|---|---|
| `PersistenceConfig` / `PersistenceConfigBase` (JPA, DataSource, Liquibase) | Step 14 |
| `JmsConfig` (ActiveMQ, JmsTemplate) | Step 15 |
| Prometheus `simpleclient_servlet` → Actuator | Step 16 |
| `BasicCacheConfig` (Redis, Jedis) | Step 17 |
| `JobConfig` ShedLock prefix `"webcert"` → `"rehabstod"` | Step 17 |
| Dockerfile WAR → JAR | Step 18 |
