# Rehabstöd — First Migration Scope

*Target: Spring Boot application with minimal XML configuration, Spring Boot auto-configuration for JPA/JMS/Redis/Metrics, JUnit 5 only,
and Spring Boot structured ECS logging.*

---

## 1. Objective

Complete the first migration step towards the [goal tech stack](goal-tech-stack.md). After this migration, rehabstöd will:

1. **Run as a Spring Boot application** — executable JAR with embedded Tomcat (no external WAR deployment).
2. **Use Spring Boot starters and auto-configuration** — replacing manual bean wiring for JPA, JMS, Redis, and metrics.
3. **Have minimal XML-based Spring configuration** — only where required by CXF TLS configurations; all other XML eliminated.
4. **Expose REST APIs via Spring MVC** — already the case for application controllers; stub JAX-RS endpoints converted to Spring MVC.
5. **Use JUnit Jupiter exclusively** — no JUnit 4 tests or vintage engine.
6. **Use Spring Boot structured logging in ECS format** — replacing the manual logback-ecs-encoder setup.
7. **Use Spring Boot Actuator** — replacing the manual Prometheus servlet for health checks and metrics.

> **Explicitly out of scope for this migration:** module restructuring (hexagonal/domain module), Testcontainers, MapStruct, Gradle Kotlin
> DSL migration, WSDL2Java plugin migration, removal of `se.inera.intyg.infra` dependencies, and any changes to schema library dependencies.

---

## 2. Current State Summary

| Aspect                         | Current State                                                                                                                                                                                                                                                                                                              |
|--------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Application type**           | Traditional Spring Framework WAR deployed on external Tomcat 10 via Gretty; bootstrapped by `ApplicationInitializer` (`WebApplicationInitializer`) which wires two Spring contexts (root + web), registers 12 servlet filters, 3 servlets (`DispatcherServlet`, `CXFServlet`, `MetricsServlet`), and 4 listeners          |
| **REST APIs**                  | Spring MVC (`@RestController`) — already on target framework. ~11 controller classes under `/api/*`                                                                                                                                                                                                                        |
| **Stub REST APIs**             | JAX-RS (`jakarta.ws.rs`) in integration stub modules (`SamtyckestjanstStubRestApi`, `SparrtjanstStubRestApi`) — registered via CXF JAX-RS; need conversion to Spring MVC                                                                                                                                                 |
| **SOAP endpoints**             | Apache CXF (`EndpointImpl`) for server-side stubs at `/services/*`, and CXF JAXWS clients for external SOAP services (Samtyckestjänst, Spärrtjänst, SRS)                                                                                                                                                                  |
| **Bean configuration**         | 28 Java `@Configuration` classes + 8 XML files (4 CXF TLS configs, 2 stub context files, 1 PU proxy config, 1 basic cache config). `ApplicationInitializer` programmatically registers filters/servlets/listeners. `@EnableWebMvc` is explicitly declared.                                                                |
| **`se.inera.intyg.infra` deps** | 16 modules: certificate, hsa-integration-api, pu-integration-api, ia-integration, log-messages, monitoring, security-authorities, security-common, security-filter, security-siths, sjukfall-engine, common-redis-cache-core, driftbanner-dto, dynamiclink + 2 runtime (hsa/pu-integration-intyg-proxy-service)            |
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
  - Remove the `MetricsServlet` registration (replaced by Actuator, see §3.4.3).
  - Remove listener registrations that Spring Boot handles automatically (`ContextLoaderListener`, `RequestContextListener`).
  - Convert `LogbackConfiguratorContextListener` usage to Spring Boot native logging (see §3.7).
  - Keep `HttpSessionEventPublisher` as a `@Bean`.
- Remove `@EnableWebMvc` from `WebConfig` (conflicts with Spring Boot auto-configuration). Move any custom
  `WebMvcConfigurer` settings (message converters, resource handlers, interceptors) to a `WebMvcConfigurer` bean without `@EnableWebMvc`.
- Remove the Gretty plugin configuration from `web/build.gradle`.
- Update the `Dockerfile` to use a Spring Boot JAR base image instead of deploying a WAR into Catalina.

**Files affected:**

- `web/build.gradle` — switch from `war` + `org.gretty` plugins to `org.springframework.boot` plugin
- `build.gradle` — add Spring Boot plugin to the root project (apply false)
- New: `web/src/main/java/.../RehabstodApplication.java`
- Remove: `web/src/main/java/.../ApplicationInitializer.java`
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

### 3.2 Eliminate XML Bean Configuration

**What changes:**

Each XML configuration file must be converted to Java `@Configuration` classes or removed entirely when Spring Boot auto-configuration
covers the concern.

| XML File                                       | Source     | Action                                                                                                                                                     |
|------------------------------------------------|------------|------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `samtyckestjanst-services-config.xml`          | Local      | **Convert** to Java `@Configuration` class — CXF HTTP conduit TLS settings can be configured programmatically via `JaxWsProxyFactoryBean`                  |
| `samtyckestjanst-stub-context.xml`             | Local      | **Convert** to Java `@Configuration` with `@Profile("rhs-samtyckestjanst-stub")` — register CXF JAXWS endpoints and JAX-RS stubs as Spring MVC            |
| `sparrtjanst-services-config.xml`              | Local      | **Convert** to Java `@Configuration` class — same pattern as samtyckestjanst                                                                                |
| `sparrtjanst-stub-context.xml`                 | Local      | **Convert** to Java `@Configuration` with `@Profile("rhs-sparrtjanst-stub")` — same pattern as samtyckestjanst                                             |
| `ia-services-config.xml`                       | Infra      | **Replace** `@ImportResource` in `IaConfiguration` with Java-based config or evaluate if IA integration can use REST client directly                        |
| `ia-stub-context.xml`                          | Infra      | **Replace** `@ImportResource` in `IaStubConfiguration` with Java-based config                                                                               |
| `pu-integration-intyg-proxy-service-config.xml`| Infra      | **Replace** `@ImportResource` in `PuIntygProxyServiceConfiguration` with Java-based config or direct REST client                                            |
| `basic-cache-config.xml`                       | Infra      | **Remove.** Replaced by Spring Boot Redis auto-configuration (see §3.4.4)                                                                                   |
| `META-INF/cxf/cxf.xml` (imported)             | CXF        | **Keep** — this is a CXF framework resource. The `@ImportResource` for it moves into a dedicated CXF config class.                                          |

**Note:** Several XML files originate from Inera infra libraries (`ia-services-config.xml`, `ia-stub-context.xml`,
`pu-integration-intyg-proxy-service-config.xml`, `basic-cache-config.xml`). These are loaded via `@ImportResource` in Java config classes.
Removing them requires either inlining the bean definitions or replacing the infra library with a local implementation. Where this is too
costly, the `@ImportResource` can be retained temporarily and marked for a later migration phase.

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

- **Remove:** `io.prometheus:simpleclient_servlet` dependency and the `MetricsServlet` registration in `ApplicationInitializer`.
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

### 3.5 Migrate All Tests to JUnit Jupiter

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

### 3.6 Logging: Spring Boot Structured ECS Logging

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

### 3.7 Static Resources & View Resolvers

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
│  3.5  Migrate all tests to JUnit Jupiter            │
└──────────────────────┬──────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────┐
│ Phase 1: Pre-Boot Cleanup                           │
│                                                     │
│  3.3  Convert stub JAX-RS → Spring MVC              │
│  3.2  Convert XML config → Java (where possible)    │
│  3.7  Resolve static resources / view resolvers     │
└──────────────────────┬──────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────┐
│ Phase 2: Spring Boot Switch                         │
│                                                     │
│  3.1  Spring Boot bootstrap                         │
│       (ApplicationInitializer → @SpringBootApplication,│
│        WAR → JAR, remove Gretty, remove @EnableWebMvc)│
└──────────────────────┬──────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────┐
│ Phase 3: Auto-Configuration & Logging               │
│                                                     │
│  3.4  Auto-configuration replacements               │
│       (JPA, JMS, Metrics, Redis — one at a time)    │
│  3.6  Spring Boot ECS logging                       │
│  —    Dockerfile update (WAR/Catalina → JAR)        │
└─────────────────────────────────────────────────────┘
```

**Rationale:**

- **Phase 0** (JUnit 5) has zero runtime risk — only test code changes. It removes the vintage engine dependency early.
- **Phase 1** prepares the codebase for Spring Boot by eliminating JAX-RS stubs, XML configs, and frontend concerns that could cause issues
  with the Boot switch.
- **Phase 2** is the actual Spring Boot switch. Because rehabstöd already uses Spring MVC and has extensive Java `@Configuration` classes
  (28), this step is primarily about replacing `ApplicationInitializer` with `@SpringBootApplication` and re-registering filters/servlets as
  beans.
- **Phase 3** is safe because Spring Boot auto-config backs off when existing beans are present, so each concern (JPA, JMS, Redis, metrics)
  can be swapped one at a time.

---

## 5. Verification Criteria

The migration is complete when:

- [ ] The application starts as a Spring Boot executable JAR (`java -jar rehabstod.jar`).
- [ ] No XML files are used for Spring bean configuration, except where `@ImportResource` is retained for Inera infra library XML that
  cannot yet be replaced (tracked as tech debt).
- [ ] All REST endpoints respond correctly using Spring MVC (`@RestController`).
- [ ] All SOAP stubs respond correctly via CXF at `/services/*` (configured in Java).
- [ ] All SOAP client integrations (Samtyckestjänst, Spärrtjänst, SRS) function correctly with TLS.
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
| `se.inera.intyg.infra` dependency reduction      | Core platform dependency; requires separate analysis per module        |
| ShedLock evaluation (keep vs. remove)            | Functional decision; retain for now (fix prefix only)                  |
| Spring Boot Starter WebFlux                      | Only needed if reactive HTTP clients are adopted                       |
| Jedis → Lettuce migration                        | Can be evaluated after Spring Boot is stable; Jedis still works        |
| Remove `se.inera.intyg.common` dependencies      | Core business dependency; requires separate analysis                   |
| Kubernetes configuration migration               | Deployment concern; separate from application migration                |

---

## 7. Key Differences from Intygstjänst Migration

The rehabstöd migration differs from the intygstjänst reference in several important ways:

| Aspect                             | Intygstjänst                                                | Rehabstöd                                                                |
|------------------------------------|-------------------------------------------------------------|--------------------------------------------------------------------------|
| **REST framework**                 | JAX-RS (`jakarta.ws.rs`) — ~15 controllers to convert       | Already Spring MVC — no controller migration needed                      |
| **Bean configuration**             | Mostly XML (5+ XML files, ~4 Java config classes)           | Mostly Java (28 `@Configuration` classes, 8 XML files)                   |
| **Application bootstrap**          | `web.xml` / `ContextLoaderListener`                         | `ApplicationInitializer` (`WebApplicationInitializer`) — programmatic    |
| **Infra deps to remove**           | `se.inera.intyg.intygstjanst` (own infra modules)           | `se.inera.intyg.infra` (shared platform — harder to inline, out of scope)|
| **SAML security**                  | No (security at infrastructure level)                       | Yes — SAML 2.0 with complex filter chain (12 filters)                    |
| **Static resources**               | Not present                                                 | Resource handlers for frontend assets (needs evaluation)                 |
| **Redis usage**                    | Caching + ShedLock                                          | Caching + ShedLock + HTTP session store                                  |
| **Scope of "remove infra deps"**   | Full removal of own infra deps (inline/replace)             | Deferred — infra deps are shared platform modules, not app-specific      |

**The biggest simplification:** Rehabstöd already uses Spring MVC for all application REST controllers, so there is no JAX-RS → Spring MVC
migration for the main API surface. Only 2 stub classes need conversion.

**The biggest risk:** The `ApplicationInitializer` registers 12 servlet filters in a specific order. The Spring Boot migration must
preserve this exact filter ordering using `FilterRegistrationBean` with explicit `setOrder()` values.

---

## 8. Risk Assessment

| Risk                                                | Likelihood | Impact | Mitigation                                                                                 |
|-----------------------------------------------------|------------|--------|--------------------------------------------------------------------------------------------|
| CXF + Spring Boot compatibility issues              | Medium     | High   | CXF has documented Spring Boot support; test early with a spike                            |
| Filter ordering changes break security              | Medium     | High   | Map exact filter order from `ApplicationInitializer`; use `FilterRegistrationBean.setOrder()` |
| `@EnableWebMvc` removal changes MVC behavior        | Medium     | Medium | Thoroughly test resource handling, message converters, content negotiation after removal    |
| SAML 2.0 + Spring Boot session management conflicts | Medium     | High   | Test SAML login/logout flow end-to-end after Boot switch                                   |
| Inera infra XML imports (`@ImportResource`) fail     | Low        | Medium | Keep `@ImportResource` temporarily where needed; track as tech debt                        |
| Static resource handling breaks with JAR packaging  | Medium     | Low    | Evaluate if resources are needed; move to `resources/static/` if so                        |
| Redis Jedis vs. Lettuce incompatibility             | Low        | Medium | Keep Jedis initially; switch to Lettuce in a later step                                    |
| Custom Jackson ObjectMapper conflicts with Boot     | Medium     | Medium | Ensure `CustomObjectMapper` is registered as a Spring Boot `@Bean` or `Jackson2ObjectMapperBuilderCustomizer` |
| Spring Boot auto-config conflicts with manual beans | Low        | Medium | Spring Boot backs off gracefully; swap one concern at a time in Phase 3                    |
| Property name changes break deployments             | Medium     | Low    | Create a property mapping document; update deployment configurations                       |
