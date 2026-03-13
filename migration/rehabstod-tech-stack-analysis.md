# Rehabstöd — Technical Stack Analysis

*Generated: 2026-03-13*

## Overview

**Rehabstöd** ("Rehab Support") is a Swedish healthcare rehabilitation coordination backend service, part of the **Inera/SKL Intyg**
platform. It's a multi-module **Java 21** server-side application packaged as a **WAR** and deployed on **Tomcat 10**. The application
provides APIs for managing sick leave cases (sjukfall), coordinating rehabilitation efforts, and integrating with national healthcare
registries.

---

## Core Platform

| Aspect                 | Technology    | Version/Details                                                              |
|------------------------|---------------|------------------------------------------------------------------------------|
| **Language**           | Java          | **21** (managed via Inera BOM toolchain)                                     |
| **Build System**       | Gradle        | **8.14.4** (via wrapper), multi-module, parallel builds                      |
| **Application Server** | Apache Tomcat | **10** (Jakarta EE namespace)                                                |
| **Packaging**          | WAR           | Deployed at configurable context path (default `/`)                          |
| **Containerization**   | Docker        | Dockerfile deploys WAR into Catalina                                         |
| **Dev Server**         | Gretty        | **4.1.10** — embedded Tomcat 10 for local development                        |

---

## Framework Stack

### Web & Service Layer

- **Spring Framework** (spring-webmvc, spring-jms, spring-test) — Core application framework (**not Spring Boot**)
- **Spring MVC** — Primary REST API layer (`@RestController`, `@RequestMapping`, `@GetMapping`, `@PostMapping`)
- **Apache CXF** (cxf-rt-frontend-jaxws, cxf-rt-frontend-jaxrs, cxf-rt-features-logging) — **SOAP/JAXWS** web services for both **clients**
  (integration modules) and **server-side stubs** (CXF Servlet registered at `/services/*`); CXF Bus configured with `LoggingFeature` via
  `@ImportResource("classpath:META-INF/cxf/cxf.xml")`
- **Jakarta EE** APIs (jakarta.ws.rs, jakarta.servlet, jakarta.jms, jakarta.persistence, jakarta.xml.bind) — Full Jakarta EE 10+ migration
  (not javax)
- **Jackson** (jackson-jakarta-rs-json-provider, jackson-databind, jackson-datatype-jsr310) — JSON serialization for REST endpoints;
  **`CustomObjectMapper`** in `rehabstod-common` registers custom `LocalDate`/`LocalDateTime` serializers/deserializers, sets
  `WRITE_DATES_AS_TIMESTAMPS=false`, `FAIL_ON_UNKNOWN_PROPERTIES=false`, applied globally via `WebMvcConfigurer.extendMessageConverters()`
- **Spring `RestClient`** (Spring 6.1+) — Used in `wc-integration` and `it-integration` for REST calls to WebCert and Intygstjänst
- **Spring `RestTemplate`** — Also used in `it-integration` (legacy pattern) and in `SecurityConfig` with a custom `SSLContext` that accepts
  self-signed certificates for dev/test environments
- **Apache HttpClient 5** (httpclient5) — HTTP client for external integrations with custom SSL handling
- **`commons-io:commons-io`** — Used in `it-integration`
- **`spring-context-support`** — Used in `it-integration` and `srs-integration`

### Persistence Layer

- **Spring Data JPA** — Repository abstraction
- **Hibernate ORM** — JPA implementation
- **HikariCP** — Connection pooling (min idle: 3, max pool: 20)
- **Liquibase** — Database schema migration
- **MySQL** (mysql-connector-j) — Production database
- **H2** — In-memory test database

### Messaging

- **Apache ActiveMQ** (activemq-spring) — JMS messaging via Spring JMS for PDL audit logging

### Caching / Scheduling

- **Redis** — Used for caching (profile `caching-enabled`), HTTP session store, and distributed locking
- **Jedis** — Redis client library (not Lettuce); `JedisConnectionFactory` is injected explicitly (e.g., in `JobConfig`)
- **Spring Session Data Redis** — Redis-backed HTTP session management (`@EnableRedisHttpSession`)
- **ShedLock** (shedlock-spring, shedlock-provider-redis-spring) — Distributed scheduled task locking via Redis; lock provider
  prefix is currently set to `"webcert"` (likely a copy-paste issue — should be `"rehabstod"`)
- **Inera Common Redis Cache** (common-redis-cache-core) — Shared Redis caching abstraction
- **`@EnableAsync`** — Enabled in `JobConfig` for async method execution (thread pool size: 10)

### Monitoring & Observability

- **Prometheus** (simpleclient_servlet) — Metrics endpoint (`/metrics` via web.xml servlet mapping); individual service methods use
  `@PrometheusTimeMethod` annotation for method-level timing
- **Logback** with **Elastic ECS encoder** (logback-ecs-encoder) — Structured JSON logging for ELK/Elastic stack
- **SLF4J** — Logging API
- **AspectJ** (aspectjweaver) — AOP for `@PerformanceLogging` cross-cutting concerns (captures start/end time, duration, class, method,
  outcome, and writes via MDC with `LogMarkers.PERFORMANCE` marker)
- **Custom Logback converters** — `UserConverter` and `SessionConverter` (extend `ClassicConverter`) inject user HSA ID and session ID
  into log patterns from the Spring Security context

### Code Generation & Utilities

- **Lombok** — Annotation-based boilerplate reduction (`@Slf4j`, `@RequiredArgsConstructor`, `@Builder`, `@Data`)
- **JAXB2 Basics** (jaxb2-basics) — XML/SOAP schema code generation
- **Guava** — General utility library
- **Commons Lang3** (commons-lang3) — String/object utilities
- **Apache POI** (poi-ooxml) — Excel export functionality for sick leave reports

### Security

- **Spring Security** (spring-security-config, spring-security-web) — Core security framework
- **Spring Security SAML 2.0** (spring-security-saml2-service-provider) — SAML2 authentication with SITHS using `OpenSaml4AuthenticationProvider`;
  requires LoA2/LoA3 (`http://id.sambi.se/loa/loa2`, `loa3`); custom `Saml2AuthenticationToken` wrapping a `RehabstodUser` principal
- **Custom SAML logout** — `OpenSaml4LogoutRequestResolver` populates `NameID` and `SessionIndex`; PKCS12 keystore for signing credentials
- **CSRF Protection** — Cookie-based CSRF with `SpaCsrfTokenRequestHandler` and `CsrfCookieFilter` (SPA-optimized)
- **`RSSecurityHeadersFilter`** — Sets `Strict-Transport-Security`, `X-XSS-Protection`, `X-Frame-Options: DENY`,
  `Content-Security-Policy: frame-ancestors 'none'`, `Referrer-Policy`
- **`UnitSelectedAssuranceFilter`** — Ensures authenticated users have selected a care unit (`valdVardenhet`) before accessing `/api/*`
- **`PdlConsentGivenAssuranceFilter`** — Ensures PDL (Patient Data Law) consent is given before accessing `/api/*`
- **`SessionTimeoutFilter`** — Tracks session timeout; configurable skip URLs (e.g., session ping endpoint)
- **`AuthenticationEventListener`** — `@EventListener` for `InteractiveAuthenticationSuccessEvent` and `LogoutSuccessEvent`; writes to
  `MonitoringLogService` for audit logging
- **Inera Security** (security-authorities, security-common, security-filter, security-siths) — Custom security filters, authority
  resolution, and SITHS integration
- **`HashPatientIdHelper`** — Hashes patient IDs before logging for GDPR compliance

---

## Domain-Specific / Inera Ecosystem Dependencies

This is a significant part of the stack — the app relies heavily on internal Inera libraries:

| Category                                       | Libraries                                                                                                                                   | Purpose                                                                                                       |
|------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------|
| **Inera Infra** (`infraVersion: 4.1.0`)        | certificate, hsa-integration-api, pu-integration-api, ia-integration, log-messages, monitoring, security-filter, security-authorities, security-common, security-siths, sjukfall-engine, common-redis-cache-core, driftbanner-dto, dynamiclink | Shared infrastructure: HSA (healthcare address registry), PU (person data), sick leave calculations, security |
| **Inera Infra Runtime**                        | hsa-integration-intyg-proxy-service, pu-integration-intyg-proxy-service                                                                    | HSA and PU integration via intyg-proxy-service REST APIs (runtimeOnly)                                        |
| **Schema Libraries**                           | clinicalprocess-healthcond-rehabilitation, clinicalprocess-healthcond-certificate, clinicalprocess-healthcond-srs, schemas-contract, informationsecurity-authorization-consent, informationsecurity-authorization-blocking | RIV-TA SOAP contract schemas (Swedish national interoperability standards)                                    |
| **Inera BOM** (`intygBomVersion: 1.0.0.14`)   | platform, catalog                                                                                                                           | Centralized dependency version management                                                                     |

---

## Module Structure

| Module                                    | Purpose                                                                       |
|-------------------------------------------|-------------------------------------------------------------------------------|
| **`rehabstod-web`**                       | Main WAR module — controllers, services, Spring config, security              |
| **`rehabstod-common`**                    | Shared utilities and common classes                                           |
| **`rehabstod-persistence`**               | JPA entities, repositories, Liquibase migrations                              |
| **`rehabstod-logging`**                   | Cross-cutting logging concerns (MDC, performance logging, AOP)                |
| **`rehabstod-it-integration`**            | Intygstjänst integration (SOAP + REST client)                                 |
| **`rehabstod-srs-integration`**           | SRS (risk prediction service) integration (SOAP)                              |
| **`rehabstod-samtyckestjanst-integration`** | Samtyckestjänst (consent service) integration (SOAP + stub)                 |
| **`rehabstod-sparrtjanst-integration`**   | Spärrtjänst (blocking service) integration (SOAP + stub)                      |
| **`rehabstod-wc-integration`**            | WebCert integration (REST client)                                             |

---

## Integration Landscape

The service integrates with these external services:

| Service                       | Protocol  | Description                                                      |
|-------------------------------|-----------|------------------------------------------------------------------|
| **Intygstjänst**              | REST+SOAP | Certificate service — sick leave data retrieval                  |
| **WebCert**                   | REST      | Web certificate service — certificate additions                  |
| **SRS**                       | SOAP/WSDL | Risk prediction — diagnosis risk assessment                      |
| **Samtyckestjänst**           | SOAP/WSDL | Consent service — patient consent checking/registration          |
| **Spärrtjänst**               | SOAP/WSDL | Blocking service — data access blocking checks                   |
| **HSA** (via proxy service)   | REST      | Healthcare address registry — organization/unit lookups          |
| **PU** (via proxy service)    | REST      | Population registry — person data lookups                        |
| **IA**                        | REST      | Internal application integration                                 |

All SOAP services connect via **NTJP** (Nationella TjänstePlattformen) with mutual TLS.

---

## Testing Stack

- **JUnit 5** (Jupiter) — Primary test framework
- **JUnit 4** (via junit-vintage-engine) — Legacy test support
- **Mockito** (with dedicated Java agent) — Mocking
- **Spring Test** — Integration testing
- **H2** — In-memory database for tests (profile: `h2`)

---

## Quality & CI/CD

- **SonarQube** — Static code analysis (project: `intyg-rehabstod`)
- **JaCoCo** — Code coverage (HTML + XML reports)
- **CycloneDX** — Software Bill of Materials (SBOM) generation
- **Spotless** — Code formatting (Google Java Format, license headers)
- **Jenkins** — CI/CD (Jenkins.properties present)
- **Ben Manes Versions** — Dependency update checking

---

## Configuration & Profiles

### Spring Profiles

| Profile                       | Purpose                                          |
|-------------------------------|--------------------------------------------------|
| `dev`                         | Development mode (local services, embedded broker)|
| `caching-enabled`            | Activates Redis caching                           |
| `rhs-security-test`          | Test security configuration                       |
| `rhs-samtyckestjanst-stub`   | Use consent service stub                          |
| `rhs-sparrtjanst-stub`       | Use blocking service stub                         |
| `rhs-srs-stub`               | Use SRS stub                                      |
| `ia-stub`                     | Use internal application stub                     |
| `testability`                 | Enable testing endpoints                          |
| `h2`                          | H2 in-memory database (testing)                   |

### Configuration Classes (28 total)

Key configurations: `ApplicationConfig`, `WebSecurityConfig`, `WebConfig`, `ServiceConfig`, `InfraConfig`, `JmsConfig`, `JobConfig`,
`PersistenceConfig`, `EmployeeNameCacheConfig`, `SjukfallConfig`, plus per-integration-module client and stub configurations.

Notable annotations in use: `@EnableTransactionManagement` (implements `TransactionManagementConfigurer`), `@EnableWebSecurity`,
`@EnableRedisHttpSession`, `@EnableAsync`, `@EnableScheduling` (declared in both `JobConfig` and `ServiceConfig` — redundant),
`@EnableAspectJAutoProxy`, `@EnableWebMvc`, `@EnableJpaRepositories`.

### Application Initializer (`ApplicationInitializer`)

The entire application is bootstrapped via `ApplicationInitializer implements WebApplicationInitializer` — the programmatic equivalent of
`web.xml`. It:

- Wires **two Spring application contexts**: a root context (infra, persistence, security, services) and a web context (`WebConfig`)
- Registers **12 servlet filters** in this order:

| Order | Filter | Scope |
|-------|--------|-------|
| 1 | `CharacterEncodingFilter` (UTF-8) | `/*` |
| 2 | `springSessionRepositoryFilter` (Redis session) | `/*` |
| 3 | `RequestContextHolderUpdateFilter` | `/*` |
| 4 | `MdcServletFilter` (trace/session/span IDs) | `/*` |
| 5 | `SessionTimeoutFilter` (skips session-ping URL) | `/*` |
| 6 | `springSecurityFilterChain` | `/*` |
| 7 | `MdcUserServletFilter` (user HSA ID, org in MDC) | `/*` |
| 8 | `PrincipalUpdatedFilter` (Inera infra) | `/*` |
| 9 | `UnitSelectedAssuranceFilter` (care unit check) | `/api/*` |
| 10 | `PdlConsentGivenAssuranceFilter` (PDL consent) | `/api/*` |
| 11 | `HiddenHttpMethodFilter` | `/*` |
| 12 | `RSSecurityHeadersFilter` (HSTS, CSP, etc.) | `/*` |

- Registers **3 servlets**: `DispatcherServlet` (`/`), `CXFServlet` (`/services/*`), `MetricsServlet` (`/metrics`)
- Registers **4 listeners**: `HttpSessionEventPublisher`, `RequestContextListener`, `LogbackConfiguratorContextListener`,
  `ContextLoaderListener`

### Spring XML Configuration (8 files)

- `samtyckestjanst-services-config.xml` — CXF SOAP client TLS config (cipher suite filtering for NTJP)
- `samtyckestjanst-stub-context.xml` — Stub endpoint definitions (JAXWS + JAXRS with `CustomObjectMapper`)
- `sparrtjanst-services-config.xml` — CXF SOAP client TLS config (cipher suite filtering for NTJP)
- `sparrtjanst-stub-context.xml` — Stub endpoint definitions
- `ia-services-config.xml` — IA service CXF client config
- `ia-stub-context.xml` — IA stub endpoint definitions
- `pu-integration-intyg-proxy-service-config.xml` — PU proxy service config (from Inera infra)
- `basic-cache-config.xml` — Redis cache infrastructure config (from Inera infra, via `CacheConfigurationFromInfra`)

### Tomcat Configuration (`tomcat-gretty.xml`)

Used both for local development (Gretty) and as reference for production. Key elements:

- **`RemoteIpValve`** — Processes `X-Forwarded-For` and `X-Forwarded-Proto` headers for reverse-proxy deployments
- **`ErrorReportValve`** — Controls error page visibility
- **`AprLifecycleListener`** with `SSLEngine=on`
- Memory leak prevention listeners (`JreMemoryLeakPreventionListener`, `ThreadLocalLeakPreventionListener`)

---

## Key Observations

1. **Not Spring Boot** — This is a traditional Spring Framework + WAR deployment, not Spring Boot. Uses Gretty plugin for local Tomcat 10
   development. The `ApplicationInitializer` (`WebApplicationInitializer`) replaces the role of `SpringApplication` and auto-configuration.
2. **REST-first with SOAP integrations** — The application's own API layer uses Spring MVC (`@RestController`), while external service
   integrations use a mix of SOAP (CXF/JAXWS) and REST clients (`RestClient`, `RestTemplate`). CXF also serves SOAP **stubs** server-side
   at `/services/*`.
3. **Static resources ARE bundled** — Contrary to a "backend API only" view, `WebConfig` registers resource handlers for frontend assets
   (`/bower_components/**`, `/app/**`, `/components/**`, `/index.html`). Two `ViewResolver` beans exist (HTML + JSP). This is a concern for
   JAR packaging since Spring Boot has limited JSP support.
4. **Jakarta EE migration completed** — All namespaces use `jakarta.*`, not `javax.*`, indicating a successful migration to Jakarta EE 10+.
5. **Heavy coupling to Inera ecosystem** — ~16 internal Inera infrastructure dependencies plus schema libraries. The app is tightly
   integrated into the larger Intyg platform.
6. **Redis-centric session and caching** — Redis serves three purposes: HTTP session store, application caching, and distributed lock
   provider (ShedLock). **Jedis** is the client library (not Lettuce, which is Spring Boot's default).
7. **SAML 2.0 authentication** — Healthcare-grade authentication via SITHS with `OpenSaml4AuthenticationProvider`, LoA2/LoA3 assurance
   levels, custom `Saml2AuthenticationToken`, and authority resolution filters. PKCS12 keystore for signing.
8. **Mixed configuration** — Combines Java `@Configuration` classes (28) with Spring XML config files (8), primarily XML for legacy
   CXF/SOAP TLS configurations and Inera infra-provided context files.
9. **JUnit 4 legacy** — Still includes junit-vintage-engine for legacy JUnit 4 test support alongside JUnit 5 Jupiter.
10. **Stub-based development** — Each external service integration module includes a stub implementation activated via Spring profiles,
    enabling local development without external dependencies.
11. **Custom Jackson ObjectMapper** — `CustomObjectMapper` in `rehabstod-common` defines custom `LocalDate`/`LocalDateTime` serialization.
    Applied globally by patching `MappingJackson2HttpMessageConverter` in `WebConfig`. `@PrometheusTimeMethod` is used for method-level
    metrics — must migrate to Micrometer `@Timed` in Spring Boot.
12. **ShedLock lock prefix bug** — `JobConfig` creates `RedisLockProvider(jedisConnectionFactory, "webcert")`. The prefix "webcert"
    appears to be a copy-paste error; it should be "rehabstod" to avoid Redis key collisions with WebCert if they share the same instance.
13. **GDPR-aware logging** — `HashPatientIdHelper` hashes patient IDs before they appear in log statements, ensuring compliance with
    patient data protection requirements.

---

## Suggested Additional Analysis

1. **Dependency vulnerability scan** — Check the third-party libraries for known CVEs (CycloneDX BOM is already generated).
2. **Database schema review** — Analyze the Liquibase changelogs to understand the data model.
3. **API surface analysis** — Catalog the REST endpoints exposed by the web module's controllers.
4. **Spring configuration deep-dive** — Map how the 28 `@Configuration` classes wire together and which profiles control what.
5. **Inera BOM version catalog inspection** — Understand exactly which versions of Spring, Hibernate, CXF, etc. are being pulled in
   (they're managed centrally by the BOM).
6. **JUnit 4 test inventory** — Identify which tests still use JUnit 4 to plan the JUnit 5 migration effort.
7. **CXF JAX-RS evaluation** — The `cxf-rt-frontend-jaxrs` dependency is included but JAX-RS is only used in stub REST APIs; evaluate
   whether it can be removed in favor of Spring MVC stubs.
8. **Redis configuration audit** — Review cache TTL policies, session serialization, and Sentinel/HA setup.
