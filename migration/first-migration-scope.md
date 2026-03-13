# Rehabstöd — First Migration Scope

*Target: Spring Boot application with zero XML configuration, no `se.inera.intyg.infra` dependencies, Spring Boot auto-configuration for
JPA/JMS/Redis/Metrics, JUnit 5 only, and Spring Boot structured ECS logging.*

---

## 1. Objective

Complete the first migration step towards the [goal tech stack](goal-tech-stack.md). After this migration, rehabstöd will:

1. **Run as a Spring Boot application** — executable JAR with embedded Tomcat (no external WAR deployment).
2. **Use Spring Boot starters and auto-configuration** — replacing manual bean wiring for JPA, JMS, Redis, and metrics.
3. **Have zero XML-based Spring configuration** — all XML config files (local and infra-imported) eliminated and replaced with Java
   `@Configuration` classes or Spring Boot auto-configuration.
4. **Have no dependencies on `se.inera.intyg.infra`** — all infra functionality either inlined into the project, replaced with Spring Boot
   equivalents, or accessed via direct REST API calls. The `intygInfraVersion` property is removed from `build.gradle`.
5. **Expose REST APIs via Spring MVC** — already the case for application controllers; stub JAX-RS endpoints converted to Spring MVC.
6. **Use JUnit Jupiter exclusively** — no JUnit 4 tests or vintage engine.
7. **Use Spring Boot structured logging in ECS format** — replacing the manual logback-ecs-encoder setup.
8. **Use Spring Boot Actuator** — replacing the manual Prometheus servlet for health checks and metrics.

> **Explicitly out of scope for this migration:** module restructuring (hexagonal/domain module), Testcontainers, MapStruct, Gradle Kotlin
> DSL migration, WSDL2Java plugin migration, and any changes to schema library dependencies.

---

## 2. Current State Summary

| Aspect                         | Current State                                                                                                                                                                                                                                                                                                              |
|--------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Application type**           | Traditional Spring Framework WAR deployed on external Tomcat 10 via Gretty; bootstrapped by `ApplicationInitializer` (`WebApplicationInitializer`) which wires two Spring contexts (root + web), registers 12 servlet filters, 3 servlets (`DispatcherServlet`, `CXFServlet`, `MetricsServlet`), and 4 listeners          |
| **REST APIs**                  | Spring MVC (`@RestController`) — already on target framework. ~11 controller classes under `/api/*`                                                                                                                                                                                                                        |
| **Stub REST APIs**             | JAX-RS (`jakarta.ws.rs`) in integration stub modules (`SamtyckestjanstStubRestApi`, `SparrtjanstStubRestApi`) — registered via CXF JAX-RS; need conversion to Spring MVC                                                                                                                                                 |
| **SOAP endpoints**             | Apache CXF (`EndpointImpl`) for server-side stubs at `/services/*`, and CXF JAXWS clients for external SOAP services (Samtyckestjänst, Spärrtjänst, SRS)                                                                                                                                                                  |
| **Bean configuration**         | 28 Java `@Configuration` classes + 8 XML files (4 CXF TLS configs, 2 stub context files, 1 PU proxy config, 1 basic cache config). `ApplicationInitializer` programmatically registers filters/servlets/listeners. `@EnableWebMvc` is explicitly declared.                                                                |
| **`se.inera.intyg.infra` deps** | 16 modules across 4 complexity tiers: **Simple DTOs** (certificate ×6 files, logmessages ×22, driftbanner-dto ×2, rediscache ×3) · **Service wrappers** (dynamiclink ×3, ia-integration ×3, monitoring ×7) · **Broad models** (security-common ×38, security-authorities ×14, security-filter ×5) · **Complex engines** (sjukfall-engine ×29, integration.hsatk ×29, integration.pu ×3, security-siths ×1) + 2 runtime (hsa/pu-integration-intyg-proxy-service) |
| **Test framework**             | Mix of JUnit 4 (via junit-vintage-engine) and JUnit 5 (Jupiter) in `web` and `persistence` modules                                                                                                                                                                                                                       |
| **Logging**                    | Logback + `co.elastic.logging:logback-ecs-encoder` with `LogbackConfiguratorContextListener` and custom `logback-spring-base.xml`; custom `UserConverter` and `SessionConverter` inject user/session info into log patterns                                                                                               |
| **Metrics**                    | Prometheus `simpleclient_servlet` with `MetricsServlet` mapped to `/metrics` in `ApplicationInitializer`; `@PrometheusTimeMethod` for method-level timing                                                                                                                                                                 |
| **Persistence config**         | Manual `PersistenceConfig` with explicit DataSource, EntityManagerFactory, TransactionManager, and Liquibase beans; `@EnableJpaRepositories`; implements `TransactionManagementConfigurer`                                                                                                                                 |
| **JMS config**                 | Manual ActiveMQ `ActiveMQConnectionFactory`, `JmsTransactionManager`, and `JmsTemplate` beans in `JmsConfig`                                                                                                                                                                                                              |
| **Redis config**               | `common-redis-cache-core` (Inera infra) with `JedisConnectionFactory`; `@EnableRedisHttpSession`; `EmployeeNameCacheConfig` with custom TTL; imported `basic-cache-config.xml`                                                                                                                                             |
| **Static resources**           | `WebConfig` registers resource handlers for `/bower_components/**`, `/app/**`, `/components/**`, `/index.html`, `/favicon.ico`, `/robots.txt` with two `ViewResolver` beans (HTML + JSP). This is a concern for JAR packaging.                                                                                             |

---

## 3. Migration Work Items

### 3.1 Spring Boot Application Bootstrap

**What changes:**

- Create a `@SpringBootApplication` main class (`RehabstodApplication`).
- Convert the `web` module from a WAR (Gretty/Tomcat plugin) to an executable JAR with the Spring Boot Gradle plugin.
- Replace `ApplicationInitializer` (`WebApplicationInitializer`) with Spring Boot auto-configuration:
  - Convert the 12 servlet filter registrations to `FilterRegistrationBean` beans.
  - Register `CXFServlet` as a `ServletRegistrationBean` at `/services/*`.
  - Remove listener registrations that Spring Boot handles automatically (`ContextLoaderListener`, `RequestContextListener`).
  - Convert `LogbackConfiguratorContextListener` usage to Spring Boot native logging (see §3.7).
  - Keep `HttpSessionEventPublisher` as a `@Bean`.
- Delete `web/src/main/webapp/WEB-INF/web.xml` — it contains only the `MetricsServlet` registration, which is replaced by Actuator (see §3.4.3). Executable JARs do not use `web.xml`.
- Remove `@EnableWebMvc` from `WebConfig` (conflicts with Spring Boot auto-configuration). Move any custom
  `WebMvcConfigurer` settings (message converters, resource handlers, interceptors) to a `WebMvcConfigurer` bean without `@EnableWebMvc`.
- Remove the Gretty plugin configuration from `web/build.gradle`.
- Update the `Dockerfile` to use a Spring Boot JAR base image instead of deploying a WAR into Catalina.

**Files affected:**

- `web/build.gradle` — switch from `war` + `org.gretty` plugins to `org.springframework.boot` plugin
- `build.gradle` — add Spring Boot plugin to the root project (apply false)
- New: `web/src/main/java/.../RehabstodApplication.java`
- Remove: `web/src/main/java/.../ApplicationInitializer.java`
- Remove: `web/src/main/webapp/WEB-INF/web.xml`
- Modify: `web/src/main/java/.../config/WebConfig.java` — remove `@EnableWebMvc`
- `Dockerfile` — change from WAR/Catalina to JAR-based

**Spring Boot starters to add:**

- `spring-boot-starter-web` (embedded Tomcat + Spring MVC + Jackson)
- `spring-boot-starter-data-jpa` (replaces manual JPA/Hibernate/HikariCP config)
- `spring-boot-starter-activemq` (replaces manual ActiveMQ connection factory config)
- `spring-boot-starter-actuator` (replaces manual Prometheus servlet)
- `spring-boot-starter-data-redis` (replaces manual Redis/Jedis config)

**Dependencies to remove (replaced by starters):**

- `org.springframework:spring-webmvc` (provided by `starter-web`)
- `org.springframework:spring-jms` (provided by `starter-activemq`)
- `org.springframework.data:spring-data-jpa` (provided by `starter-data-jpa`)
- `io.prometheus:simpleclient_servlet` (replaced by Actuator + Micrometer)
- `ch.qos.logback:logback-classic` (provided by `starter-web`)
- `com.fasterxml.jackson.jakarta.rs:jackson-jakarta-rs-json-provider` (replaced by Spring Boot Jackson auto-config)

---

### 3.2 Eliminate All XML Bean Configuration

**What changes:**

Every XML configuration file — both local and imported from Inera infra libraries — must be converted to Java `@Configuration` classes or
removed entirely when Spring Boot auto-configuration covers the concern. The goal is **zero `@ImportResource` annotations** remaining.

#### 3.2.1 Local XML Files (in this repository)

| XML File                              | Current Importer                    | Action                                                                                                                                                       |
|---------------------------------------|-------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `samtyckestjanst-services-config.xml` | `SamtyckestjanstConfiguration`      | **Convert** to Java — configure CXF HTTP conduit TLS programmatically via `TLSClientParameters`, `KeyManager`, `TrustManager`, cipher suite filters          |
| `samtyckestjanst-stub-context.xml`    | `SamtyckestjanstStubConfiguration`  | **Convert** to Java — register CXF JAXWS endpoints via `EndpointImpl.publish()` and convert JAX-RS stub to Spring MVC (see §3.3)                            |
| `sparrtjanst-services-config.xml`     | `SparrtjanstConfiguration`          | **Convert** to Java — same TLS conduit pattern as samtyckestjanst                                                                                             |
| `sparrtjanst-stub-context.xml`        | `SparrtjanstStubConfiguration`      | **Convert** to Java — same pattern as samtyckestjanst stub                                                                                                    |

**CXF TLS Java conversion pattern** (replaces both `*-services-config.xml` files):

```java
@Configuration
@Profile("!rhs-samtyckestjanst-stub")
public class SamtyckestjanstTlsConfig {

    @Bean
    public HTTPConduitConfigurer samtyckestjanstConduitConfigurer(
            @Value("${ntjp.ws.certificate.file}") String certFile,
            @Value("${ntjp.ws.certificate.password}") String certPassword,
            @Value("${ntjp.ws.key.manager.password}") String keyPassword,
            @Value("${ntjp.ws.truststore.file}") String trustFile,
            @Value("${ntjp.ws.truststore.password}") String trustPassword) {
        // Programmatic CXF HTTP conduit with TLS client parameters,
        // key/trust managers, and cipher suite filtering
    }
}
```

#### 3.2.2 Infra-Imported XML Files (from `se.inera.intyg.infra` libraries)

These XML files come from Inera infra JARs and are currently loaded via `@ImportResource`. They are eliminated as part of the infra
dependency removal (§3.5):

| XML File (classpath)                                 | Current Importer                       | Action                                                                                                       |
|------------------------------------------------------|----------------------------------------|--------------------------------------------------------------------------------------------------------------|
| `classpath:ia-services-config.xml`                   | `IaConfiguration`                      | **Remove** — replace IA SOAP client with local Java config or direct REST client                              |
| `classpath:ia-stub-context.xml`                      | `IaStubConfiguration`                  | **Remove** — replace with local Java `@Configuration` + `@Profile("ia-stub")`                                 |
| `classpath:pu-integration-intyg-proxy-service-config.xml` | `PuIntygProxyServiceConfiguration` | **Remove** — replace PU integration with direct REST client to intyg-proxy-service (§3.5)                    |
| `classpath:basic-cache-config.xml`                   | `CacheConfigurationFromInfra`          | **Remove** — replaced by Spring Boot Redis auto-configuration (§3.4.4)                                        |

#### 3.2.3 CXF Framework XML

| XML File (classpath)            | Current Importer                     | Action                                                                                                        |
|---------------------------------|--------------------------------------|---------------------------------------------------------------------------------------------------------------|
| `classpath:META-INF/cxf/cxf.xml` | `ApplicationConfig`, `WebConfig`   | **Remove `@ImportResource`** — CXF's Spring Boot starter auto-loads this. If not using the starter, configure CXF Bus programmatically via `SpringBus` `@Bean`. |

**After this step:** Zero `@ImportResource` annotations remain in the codebase. All Spring configuration is Java-only.

---

### 3.3 Stub JAX-RS Endpoints → Spring MVC

**What changes:**

The application's own REST controllers already use Spring MVC. However, integration stub modules use JAX-RS annotations registered via CXF:

| Stub Class                      | Module                             | Current Path                | Action                                                           |
|---------------------------------|------------------------------------|-----------------------------|------------------------------------------------------------------|
| `SamtyckestjanstStubRestApi`    | `rehabstod-samtyckestjanst-integration` | CXF JAX-RS stub endpoint | **Convert** to `@RestController` with `@Profile("rhs-samtyckestjanst-stub")` |
| `SparrtjanstStubRestApi`        | `rehabstod-sparrtjanst-integration`     | CXF JAX-RS stub endpoint | **Convert** to `@RestController` with `@Profile("rhs-sparrtjanst-stub")`     |

**Migration pattern:**

| JAX-RS                                  | Spring MVC                                                         |
|-----------------------------------------|--------------------------------------------------------------------|
| `@Path("/...")`                         | `@RestController` + `@RequestMapping("/...")`                      |
| `@PUT`                                  | `@PutMapping`                                                      |
| `@Produces(MediaType.APPLICATION_JSON)` | `produces = MediaType.APPLICATION_JSON_VALUE` (or rely on default) |
| `@PathParam`                            | `@PathVariable`                                                    |
| `@QueryParam`                           | `@RequestParam`                                                    |

**After conversion:**

- Remove `jakarta.ws.rs:jakarta.ws.rs-api` dependency from `web/build.gradle`
- Remove `org.apache.cxf:cxf-rt-frontend-jaxrs` dependency (only needed for JAX-RS stubs; CXF JAXWS is retained for SOAP)
- Remove CXF JAX-RS server registration from stub XML configs (these are converted to Spring MVC in §3.2)

**Note:** The main application SOAP endpoints (CXF `jaxws:endpoint`) are **not** being converted. They remain as CXF endpoints configured
via Java.

---

### 3.4 Auto-Configuration Replacements

**What changes:**

Replace manually configured beans with Spring Boot auto-configuration.

#### 3.4.1 JPA / DataSource (replaces `PersistenceConfig`)

- **Remove:** `PersistenceConfig` (manual `DataSource`, `EntityManagerFactory`, `TransactionManager`, `SpringLiquibase` beans).
- **Remove:** `PersistenceConfigDev` (H2 profile — replaced by Spring Boot profile-specific properties).
- **Replace with:** `spring-boot-starter-data-jpa` auto-configuration.
- **Keep:** `@EnableJpaRepositories` on the main application class or a config class for explicit base package scanning.
- **Configure via `application.properties`:**
  ```properties
  spring.datasource.url=jdbc:mysql://${db.server}:${db.port}/${db.name}?useSSL=false&serverTimezone=Europe/Stockholm&allowPublicKeyRetrieval=true
  spring.datasource.username=${db.username}
  spring.datasource.password=${db.password}
  spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
  spring.datasource.hikari.maximum-pool-size=20
  spring.datasource.hikari.minimum-idle=3
  spring.datasource.hikari.connection-timeout=3000
  spring.datasource.hikari.idle-timeout=15000
  spring.jpa.hibernate.ddl-auto=none
  spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
  spring.jpa.properties.hibernate.format_sql=true
  spring.liquibase.change-log=classpath:changelog/changelog.xml
  ```
- **Entity scanning:** Use `@EntityScan` on the main application class to cover `se.inera.intyg.rehabstod.persistence`.

#### 3.4.2 JMS / ActiveMQ (replaces `JmsConfig`)

- **Remove:** The manual `ActiveMQConnectionFactory`, `JmsTransactionManager`, and `JmsTemplate` beans.
- **Replace with:** `spring-boot-starter-activemq` auto-configuration.
- **Keep:** Custom `Queue` beans (e.g., PDL logging queue) and the dedicated `jmsPDLLogTemplate` if it has specific settings.
- **Configure via `application.properties`:**
  ```properties
  spring.activemq.broker-url=${activemq.broker.url}
  spring.activemq.user=${activemq.broker.username}
  spring.activemq.password=${activemq.broker.password}
  ```

#### 3.4.3 Metrics / Health (replaces Prometheus servlet)

- **Remove:** `io.prometheus:simpleclient_servlet` dependency and `web/src/main/webapp/WEB-INF/web.xml` (which registers `MetricsServlet` at `/metrics` — deleted entirely in §3.1).
- **Replace with:** `spring-boot-starter-actuator` with Micrometer Prometheus registry.
- **Migrate:** `@PrometheusTimeMethod` annotations → Micrometer `@Timed` annotations (or evaluate if `PerformanceLogging` already covers
  the timing concern sufficiently).
- **Configure via `application.properties`:**
  ```properties
  management.endpoints.web.exposure.include=health,info,prometheus
  management.endpoint.health.show-details=never
  ```

#### 3.4.4 Redis / Caching (replaces `basic-cache-config.xml` + manual Redis)

- **Remove:** `@ImportResource("classpath:basic-cache-config.xml")` in `CacheConfigurationFromInfra`.
- **Remove:** `common-redis-cache-core` dependency.
- **Replace with:** `spring-boot-starter-data-redis` auto-configuration.
- **Keep:** `EmployeeNameCacheConfig` (custom TTL) — adapt to use Spring Boot's `RedisCacheConfiguration`.
- **Keep:** `@EnableRedisHttpSession` — Spring Session Data Redis remains, now auto-configured.
- **Jedis → Lettuce decision:** Spring Boot defaults to Lettuce. Evaluate whether to keep Jedis (add `spring-boot-starter-data-redis`
  with Jedis dependency and exclude Lettuce) or switch to Lettuce. Switching to Lettuce is recommended for alignment with Spring Boot
  defaults, but may require testing with Sentinel configuration.
- **Configure via `application.properties`:**
  ```properties
  spring.data.redis.host=${redis.host}
  spring.data.redis.port=${redis.port}
  spring.data.redis.password=${redis.password}
  ```
- **Fix ShedLock prefix:** While updating `JobConfig`, change the lock provider prefix from `"webcert"` to `"rehabstod"`.

---

### 3.5 Remove All `se.inera.intyg.infra` Dependencies

**What changes:**

Every `se.inera.intyg.infra` dependency must be removed. The functionality they provide is either inlined into the project (DTOs copied,
services reimplemented), replaced with Spring Boot equivalents, or accessed via direct REST API calls to `intyg-proxy-service`. The goal is
to eliminate the `intygInfraVersion` property from `build.gradle` entirely.

#### 3.5.1 Simple DTOs & Enums — Copy Into Project

These modules contain only data transfer objects, enums, or simple utilities with no framework dependencies. Copy the used classes into a
local package (e.g., `se.inera.intyg.rehabstod.common.model` or module-appropriate locations).

| Infra Module        | Files Using It | Classes to Copy                                                                                                         | Effort |
|---------------------|----------------|-------------------------------------------------------------------------------------------------------------------------|--------|
| **certificate**     | 6 files        | `DiagnosedCertificate`, `SickLeaveCertificate`, `SickLeaveCertificate.WorkCapacity`, `TypedCertificateRequest`, `BaseCertificate`, builders | Low    |
| **log-messages**    | 22 files       | `ActivityType`, `ResourceType`, `PdlLogMessage`, `PdlResource`, `Patient`, `Enhet`, `ActivityPurpose`                   | Low    |
| **driftbanner-dto** | 2 files        | `Application`, `Banner`                                                                                                  | Low    |

#### 3.5.2 Security Models & Utilities — Copy Into Project

These modules contain models, enums, and utility classes used broadly across the auth and service layers. Copy all used classes and adapt
package references.

| Infra Module              | Files Using It | Classes to Copy                                                                                                                  | Effort |
|---------------------------|----------------|----------------------------------------------------------------------------------------------------------------------------------|--------|
| **security-common**       | 38 files       | `Role`, `Feature`, `Privilege`, `IntygUser`, `UserOrigin`, `UserOriginType`, `RequestOrigin`, `Title`, `TitleCode`, `AuthenticationLogger`, `CareUnitAccessHelper`, `AuthoritiesConstants`, `IneraCookieSerializer` | Medium |
| **security-authorities**  | 14 files       | `CommonAuthoritiesResolver`, `AuthoritiesException`, `SecurityConfigurationLoader`, `AuthoritiesConfiguration`, `AuthExpectationSpecImpl`, `AuthExpectationSpecification` | Medium |
| **security-filter**       | 5 files        | `SessionTimeoutFilter`, `RequestContextHolderUpdateFilter`, `PrincipalUpdatedFilter`, `SecurityHeadersFilter`                    | Medium |
| **security-siths**        | 1 file         | `BaseUserDetailsService` — `RehabstodUserDetailsService` extends this; inline the base class logic into `RehabstodUserDetailsService` | Medium |

**Note:** The security modules are interconnected. `security-common` models are used by `security-authorities` and `security-siths`. Plan
the inlining order: copy `security-common` models first, then `security-authorities`, then `security-filter`, then `security-siths`.

#### 3.5.3 Domain Engine — Copy Into Project

The sjukfall (sick leave) engine is core business logic with a significant number of DTOs and a service implementation.

| Infra Module        | Files Using It | Classes to Copy                                                                                                                                     | Effort |
|---------------------|----------------|-----------------------------------------------------------------------------------------------------------------------------------------------------|--------|
| **sjukfall-engine** | 29 files       | `IntygData`, `IntygParametrar`, `SjukfallEnhet`, `RekoStatusTypeDTO`, `RekoStatusDTO`, `Vardgivare`, `Patient`, `DiagnosKod`, `DiagnosKapitel`, `DiagnosKategori`, `Lakare`, `Formaga`, `OccupationTypeDTO`, `SjukfallEngineService`, `SjukfallEngineServiceImpl` | High   |

**Strategy:** Copy all DTOs and the `SjukfallEngineServiceImpl` into a local package. Remove the `@ComponentScan` for
`se.inera.intyg.infra.sjukfall.services` in `SjukfallConfig` and register the local implementation as a `@Service`.

#### 3.5.4 Service Wrappers — Inline or Replace

These modules provide thin service layers that can be reimplemented locally or replaced with direct calls.

| Infra Module        | Files Using It | Strategy                                                                                                           | Effort |
|---------------------|----------------|--------------------------------------------------------------------------------------------------------------------|--------|
| **monitoring**      | 7 files        | **Replace.** Remove `@PrometheusTimeMethod` annotation and AOP aspect. Replace with Micrometer `@Timed` (§3.4.3). Remove `MonitoringConfiguration` import. Replace `LogMarkers` with local version. Remove `LogbackConfiguratorContextListener`. | Low    |
| **dynamiclink**     | 3 files        | **Inline.** Copy `DynamicLinkService` and `DynamicLink` into the project. Remove `@ComponentScan` for `se.inera.intyg.infra.dynamiclink`. | Low    |
| **ia-integration**  | 3 files        | **Inline.** Copy `IABannerService` interface and reimplement locally (or replace with direct REST call). Copy `BannerJob` and adapt. Remove `@ImportResource` for `ia-services-config.xml` and `ia-stub-context.xml`. | Low    |
| **rediscache**      | 3 files        | **Replace.** Remove `RedisCacheOptionsSetter` usage. Replace with Spring Boot's `RedisCacheConfiguration` (§3.4.4). | Low    |

#### 3.5.5 HSA & PU Integrations — Replace with Direct REST Clients

These are the most architecturally significant replacements. The current setup uses infra libraries that internally call
`intyg-proxy-service` via SOAP/REST. Replace with direct REST calls using Spring's `RestClient`.

| Infra Module                                                                              | Files Using It | Strategy                                                                                                   | Effort |
|-------------------------------------------------------------------------------------------|----------------|------------------------------------------------------------------------------------------------------------|--------|
| **hsa-integration-api** + runtime **hsa-integration-intyg-proxy-service**                 | 29 files       | **Replace with REST client.** Call `intyg-proxy-service` REST API directly. Define local DTOs for HSA models (`Vardenhet`, `Vardgivare`, `Mottagning`, `PersonInformation`, etc.) and a local `HsaOrganizationsService` / `HsaEmployeeService` that calls the proxy. | High   |
| **pu-integration-api** + runtime **pu-integration-intyg-proxy-service** + XML config      | 3 files        | **Replace with REST client.** Call `intyg-proxy-service` REST API directly. Define local `PersonSvar`, `Person` DTOs and a local `PuService` implementation. Remove `PuIntygProxyServiceConfiguration` and its `@ImportResource`. | Medium |

#### 3.5.6 Dependency Lines to Remove from Build Files

**From `web/build.gradle`:**

```groovy
// ALL of these are removed:
implementation "se.inera.intyg.infra:certificate:${intygInfraVersion}"
implementation "se.inera.intyg.infra:common-redis-cache-core:${intygInfraVersion}"
implementation "se.inera.intyg.infra:driftbanner-dto:${intygInfraVersion}"
implementation "se.inera.intyg.infra:dynamiclink:${intygInfraVersion}"
implementation "se.inera.intyg.infra:hsa-integration-api:${intygInfraVersion}"
implementation "se.inera.intyg.infra:pu-integration-api:${intygInfraVersion}"
implementation "se.inera.intyg.infra:ia-integration:${intygInfraVersion}"
implementation "se.inera.intyg.infra:log-messages:${intygInfraVersion}"
implementation "se.inera.intyg.infra:monitoring:${intygInfraVersion}"
implementation "se.inera.intyg.infra:security-authorities:${intygInfraVersion}"
implementation "se.inera.intyg.infra:security-common:${intygInfraVersion}"
implementation "se.inera.intyg.infra:security-filter:${intygInfraVersion}"
implementation "se.inera.intyg.infra:security-siths:${intygInfraVersion}"
implementation "se.inera.intyg.infra:sjukfall-engine:${intygInfraVersion}"
runtimeOnly "se.inera.intyg.infra:hsa-integration-intyg-proxy-service:${intygInfraVersion}"
runtimeOnly "se.inera.intyg.infra:pu-integration-intyg-proxy-service:${intygInfraVersion}"
```

**From `build.gradle` (root):**

```groovy
// Remove entirely:
intygInfraVersion = System.properties['infraVersion'] ?: '4.1.0-SNAPSHOT'
```

**From other submodule build files** (these are also present and must be cleaned up):

```groovy
// common/build.gradle — remove:
implementation "se.inera.intyg.infra:log-messages:${intygInfraVersion}"
implementation "se.inera.intyg.infra:sjukfall-engine:${intygInfraVersion}"

// integration/it-integration/build.gradle — remove:
implementation "se.inera.intyg.infra:certificate:${intygInfraVersion}"
implementation "se.inera.intyg.infra:monitoring:${intygInfraVersion}"
implementation "se.inera.intyg.infra:sjukfall-engine:${intygInfraVersion}"

// integration/sparrtjanst-integration/build.gradle — remove:
implementation "se.inera.intyg.infra:common-redis-cache-core:${intygInfraVersion}"
implementation "se.inera.intyg.infra:sjukfall-engine:${intygInfraVersion}"

// integration/samtyckestjanst-integration/build.gradle — remove:
implementation "se.inera.intyg.infra:common-redis-cache-core:${intygInfraVersion}"

// integration/wc-integration/build.gradle — remove:
implementation "se.inera.intyg.infra:monitoring:${intygInfraVersion}"
```

> **Note:** The `intygInfraVersion` property cannot be removed from root `build.gradle` until all of the above submodule references are also cleared.

**Also remove all `@ComponentScan` entries referencing `se.inera.intyg.infra.*` packages** (found in `InfraConfig`, `SjukfallConfig`,
`PuIntygProxyServiceConfiguration`, and others).

---

### 3.6 Migrate All Tests to JUnit Jupiter

**What changes:**

All remaining JUnit 4 tests must be migrated to JUnit 5 (Jupiter).

**Migration pattern per file:**

| JUnit 4                                   | JUnit 5                                                   |
|-------------------------------------------|-----------------------------------------------------------|
| `import org.junit.Test`                   | `import org.junit.jupiter.api.Test`                       |
| `import org.junit.Before`                 | `import org.junit.jupiter.api.BeforeEach`                 |
| `import org.junit.After`                  | `import org.junit.jupiter.api.AfterEach`                  |
| `import org.junit.BeforeClass`            | `import org.junit.jupiter.api.BeforeAll`                  |
| `import org.junit.Assert.*`               | `import org.junit.jupiter.api.Assertions.*`               |
| `import org.junit.runner.RunWith`         | `import org.junit.jupiter.api.extension.ExtendWith`       |
| `@RunWith(MockitoJUnitRunner.class)`      | `@ExtendWith(MockitoExtension.class)`                     |
| `@RunWith(SpringJUnit4ClassRunner.class)` | `@ExtendWith(SpringExtension.class)` or `@SpringBootTest` |
| `@Rule ExpectedException`                 | `assertThrows(...)`                                       |

**Dependency changes:**

In `web/build.gradle`:

```groovy
// Remove:
testImplementation 'junit:junit'
testRuntimeOnly "org.junit.vintage:junit-vintage-engine"
```

In `persistence/build.gradle` (if applicable):

```groovy
// Remove:
testImplementation "junit:junit"
testRuntimeOnly "org.junit.vintage:junit-vintage-engine"
```

---

### 3.7 Logging: Spring Boot Structured ECS Logging

**What changes:**

Replace the manual Logback ECS encoder setup with Spring Boot 3.4+'s native structured logging support.

**Remove:**

- `co.elastic.logging:logback-ecs-encoder` dependency from `logging/build.gradle`
- `logback-spring-base.xml` (defines the `ECS_JSON_CONSOLE` appender)
- `LogbackConfiguratorContextListener` class (manual Logback initialization)
- The `LogbackConfiguratorContextListener` registration in `ApplicationInitializer` (already removed with the initializer)
- Development `logback-spring.xml` in devops config (if it exists)

**Replace with `application.properties`:**

```properties
logging.structured.format.console=ecs
logging.structured.ecs.service.name=rehabstod
logging.structured.ecs.service.environment=${spring.profiles.active:default}
```

**Retain:**

- The `logging` module's `MdcServletFilter`, `MdcHelper`, `MdcLogConstants`, `PerformanceLogging`, and `PerformanceLoggingAdvice` — these
  are local to the project and work with any Logback setup. Register `MdcServletFilter` as a `FilterRegistrationBean`.
- The custom `UserConverter` and `SessionConverter` Logback converters — evaluate if they can be replaced with MDC-based fields in the ECS
  structured output, or retain them for custom log patterns.
- The local `LogMarkers` class in the `logging` module.

---

### 3.8 Static Resources & View Resolvers

**What changes:**

The current `WebConfig` registers resource handlers for frontend assets (`/bower_components/**`, `/app/**`, `/components/**`) and two
`ViewResolver` beans (HTML + JSP). This needs attention for JAR packaging since:

- Spring Boot executable JARs have limited JSP support.
- Bower-based frontend assets should be served from `src/main/resources/static/` or externally.

**Evaluate:**

1. **Are the static resources actively used?** If the frontend is deployed separately (as indicated by the backend-API nature of the
   controllers), the resource handlers may be vestigial and can be removed.
2. **If resources are still served:** Move them to `src/main/resources/static/` for Spring Boot compatibility.
3. **JSP ViewResolver:** Remove if JSP templates are not in active use. If they are, consider migrating to Thymeleaf or serving via a
   separate mechanism.

**Decision required:** Confirm whether the frontend assets in `WebConfig` are actively used or can be removed.

---

## 4. Migration Order

The work items have dependencies. The recommended execution order, designed so the application compiles, passes all tests, starts, and is
deployable after each step:

```
┌─────────────────────────────────────────────────────┐
│ Phase 0: Test Modernization (zero runtime risk)     │
│                                                     │
│  3.6  Migrate all tests to JUnit Jupiter            │
└──────────────────────┬──────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────┐
│ Phase 1: Inline Infra Dependencies                  │
│  (still WAR/Tomcat — no runtime change)             │
│                                                     │
│  3.5.1  Copy simple DTOs (certificate, logmessages, │
│         driftbanner-dto)                             │
│  3.5.2  Copy security models (security-common,      │
│         security-authorities, security-filter,       │
│         security-siths)                              │
│  3.5.3  Copy sjukfall-engine                         │
│  3.5.4  Inline service wrappers (monitoring,         │
│         dynamiclink, ia-integration, rediscache)     │
│  3.5.5  Replace HSA & PU with REST clients           │
│  3.5.6  Remove all infra deps from build.gradle      │
└──────────────────────┬──────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────┐
│ Phase 2: Pre-Boot Cleanup                           │
│                                                     │
│  3.3  Convert stub JAX-RS → Spring MVC              │
│  3.2  Convert all XML config → Java                 │
│  3.8  Resolve static resources / view resolvers     │
└──────────────────────┬──────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────┐
│ Phase 3: Spring Boot Switch                         │
│                                                     │
│  3.1  Spring Boot bootstrap                         │
│       (ApplicationInitializer → @SpringBootApplication,│
│        WAR → JAR, remove Gretty, remove @EnableWebMvc)│
└──────────────────────┬──────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────┐
│ Phase 4: Auto-Configuration & Logging               │
│                                                     │
│  3.4  Auto-configuration replacements               │
│       (JPA, JMS, Metrics, Redis — one at a time)    │
│  3.7  Spring Boot ECS logging                       │
│  —    Dockerfile update (WAR/Catalina → JAR)        │
└─────────────────────────────────────────────────────┘
```

**Rationale:**

- **Phase 0** (JUnit 5) has zero runtime risk — only test code changes. It removes the vintage engine dependency early.
- **Phase 1** (infra removal) is done while still on traditional Spring/WAR. This is critical: by inlining and replacing infra dependencies
  *before* the Spring Boot switch, we avoid debugging two major changes at once. Each sub-step (3.5.1–3.5.6) can be verified independently
  with `./gradlew test` + application start. The infra imports are replaced incrementally — copy classes, update imports, verify, then remove
  the dependency.
- **Phase 2** prepares the codebase for Spring Boot by eliminating JAX-RS stubs, all remaining XML configs (now possible since infra XML
  sources are gone), and frontend concerns.
- **Phase 3** is the actual Spring Boot switch. Because rehabstöd already uses Spring MVC, has no infra dependencies, and has all-Java
  configuration, this step is primarily about replacing `ApplicationInitializer` with `@SpringBootApplication` and re-registering
  filters/servlets as beans.
- **Phase 4** is safe because Spring Boot auto-config backs off when existing beans are present, so each concern (JPA, JMS, Redis, metrics)
  can be swapped one at a time.

---

## 5. Verification Criteria

The migration is complete when:

- [ ] The application starts as a Spring Boot executable JAR (`java -jar rehabstod.jar`).
- [ ] Zero XML files are used for Spring bean configuration (`grep -r "@ImportResource" --include="*.java" src/` returns no results).
- [ ] Zero `se.inera.intyg.infra` dependencies remain (`grep -r "se.inera.intyg.infra" build.gradle` returns no results across all
  modules).
- [ ] The `intygInfraVersion` property is removed from root `build.gradle`.
- [ ] `grep -r "import se.inera.intyg.infra" --include="*.java" src/` returns no results.
- [ ] All REST endpoints respond correctly using Spring MVC (`@RestController`).
- [ ] All SOAP stubs respond correctly via CXF at `/services/*` (configured in Java).
- [ ] All SOAP client integrations (Samtyckestjänst, Spärrtjänst, SRS) function correctly with TLS.
- [ ] HSA and PU lookups work correctly via direct REST calls to `intyg-proxy-service`.
- [ ] `grep -r "jakarta.ws.rs" --include="*.java" src/` returns no results (no JAX-RS usage).
- [ ] `grep -r "import org.junit\." --include="*.java" src/ | grep -v jupiter` returns no results.
- [ ] `junit-vintage-engine` is not in any `build.gradle`.
- [ ] `junit:junit` is not in any `build.gradle`.
- [ ] Structured ECS JSON logs are produced when `logging.structured.format.console=ecs` is set.
- [ ] Spring Boot Actuator health endpoint responds at `/actuator/health`.
- [ ] Prometheus metrics are available at `/actuator/prometheus`.
- [ ] SAML 2.0 authentication (SITHS) works correctly — login, logout, session management.
- [ ] Redis session store, caching, and ShedLock distributed locking all function correctly.
- [ ] All existing tests pass.
- [ ] The Docker image builds and runs successfully with the new JAR packaging.

---

## 6. Out of Scope

The following items from the [goal tech stack](goal-tech-stack.md) are **intentionally deferred** to later migrations:

| Item                                             | Reason for Deferral                                                    |
|--------------------------------------------------|------------------------------------------------------------------------|
| Hexagonal/domain module architecture             | Requires significant refactoring beyond the Spring Boot migration      |
| Gradle Kotlin DSL migration                      | Independent concern; can be done separately                            |
| WSDL2Java plugin for code generation             | Independent concern; existing generated code works                     |
| Testcontainers (MySQL, ActiveMQ, MockServer)     | Can be adopted after Spring Boot is in place                           |
| MapStruct for DTO mapping                        | Additive improvement; not blocking                                     |
| Awaitility for async testing                     | Additive improvement; not blocking                                     |
| ShedLock evaluation (keep vs. remove)            | Functional decision; retain for now (fix prefix only)                  |
| Spring Boot Starter WebFlux                      | Only needed if reactive HTTP clients are adopted                       |
| Jedis → Lettuce migration                        | Can be evaluated after Spring Boot is stable; Jedis still works        |
| Remove `se.inera.intyg.common` dependencies      | Core business dependency; requires separate analysis                   |
| Remove schema library dependencies               | Required for SOAP interoperability; not blocking migration             |
| Kubernetes configuration migration               | Deployment concern; separate from application migration                |

---

## 7. Risk Assessment

| Risk                                                | Likelihood | Impact | Mitigation                                                                                 |
|-----------------------------------------------------|------------|--------|--------------------------------------------------------------------------------------------|
| CXF + Spring Boot compatibility issues              | Medium     | High   | CXF has documented Spring Boot support; test early with a spike                            |
| Filter ordering changes break security              | Medium     | High   | Map exact filter order from `ApplicationInitializer`; use `FilterRegistrationBean.setOrder()` |
| `@EnableWebMvc` removal changes MVC behavior        | Medium     | Medium | Thoroughly test resource handling, message converters, content negotiation after removal    |
| SAML 2.0 + Spring Boot session management conflicts | Medium     | High   | Test SAML login/logout flow end-to-end after Boot switch                                   |
| Inlined security classes diverge from infra upstream | Low        | Low    | Intentional — we are decoupling from infra; local ownership of security stack              |
| `BaseUserDetailsService` inlining breaks SAML auth  | Medium     | High   | Inline carefully; test authentication flow after each change; keep integration tests       |
| HSA REST client differs from infra SOAP behavior    | Medium     | Medium | Compare response formats; test with real intyg-proxy-service endpoints                    |
| Sjukfall-engine inlined logic has hidden infra deps  | Low        | Medium | Trace all transitive imports when copying; compile-test after each class copy              |
| Static resource handling breaks with JAR packaging  | Medium     | Low    | Evaluate if resources are needed; move to `resources/static/` if so                        |
| Redis Jedis vs. Lettuce incompatibility             | Low        | Medium | Keep Jedis initially; switch to Lettuce in a later step                                    |
| Custom Jackson ObjectMapper conflicts with Boot     | Medium     | Medium | Ensure `CustomObjectMapper` is registered as a Spring Boot `@Bean` or `Jackson2ObjectMapperBuilderCustomizer` |
| Spring Boot auto-config conflicts with manual beans | Low        | Medium | Spring Boot backs off gracefully; swap one concern at a time in Phase 4                    |
| Property name changes break deployments             | Medium     | Low    | Create a property mapping document; update deployment configurations                       |
| Circular dependencies when inlining security stack  | Medium     | Medium | Inline in dependency order: security-common → authorities → filter → siths                 |
