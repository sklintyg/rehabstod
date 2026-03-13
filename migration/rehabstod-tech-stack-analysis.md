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
- **Apache CXF** (cxf-rt-frontend-jaxws, cxf-rt-frontend-jaxrs, cxf-rt-features-logging) — **SOAP/JAXWS** web services and stub endpoints
- **Jakarta EE** APIs (jakarta.ws.rs, jakarta.servlet, jakarta.jms, jakarta.persistence, jakarta.xml.bind) — Full Jakarta EE 10+ migration
  (not javax)
- **Jackson** (jackson-jakarta-rs-json-provider) — JSON serialization for REST endpoints
- **Apache HttpClient 5** (httpclient5) — HTTP client for external integrations with custom SSL handling

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
- **Spring Session Data Redis** — Redis-backed HTTP session management (`@EnableRedisHttpSession`)
- **ShedLock** (shedlock-spring, shedlock-provider-redis-spring) — Distributed scheduled task locking via Redis
- **Inera Common Redis Cache** (common-redis-cache-core) — Shared Redis caching abstraction

### Monitoring & Observability

- **Prometheus** (simpleclient_servlet) — Metrics endpoint (`/metrics` via web.xml servlet mapping)
- **Logback** with **Elastic ECS encoder** (logback-ecs-encoder) — Structured JSON logging for ELK/Elastic stack
- **SLF4J** — Logging API
- **AspectJ** (aspectjweaver) — AOP for `@PerformanceLogging` cross-cutting concerns

### Code Generation & Utilities

- **Lombok** — Annotation-based boilerplate reduction (`@Slf4j`, `@RequiredArgsConstructor`, `@Builder`, `@Data`)
- **JAXB2 Basics** (jaxb2-basics) — XML/SOAP schema code generation
- **Guava** — General utility library
- **Commons Lang3** (commons-lang3) — String/object utilities
- **Apache POI** (poi-ooxml) — Excel export functionality for sick leave reports

### Security

- **Spring Security** (spring-security-config, spring-security-web) — Core security framework
- **Spring Security SAML 2.0** (spring-security-saml2-service-provider) — SAML2 authentication with SITHS
- **Inera Security** (security-authorities, security-common, security-filter, security-siths) — Custom security filters, authority
  resolution, and SITHS integration
- **CSRF Protection** — Cookie-based CSRF with SPA-optimized handler

---

## Domain-Specific / Inera Ecosystem Dependencies

This is a significant part of the stack — the app relies heavily on internal Inera libraries:

| Category                                       | Libraries                                                                                                                                   | Purpose                                                                                                       |
|------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------|
| **Inera Infra** (`infraVersion: 4.1.0`)        | certificate, hsa-integration-api, pu-integration-api, ia-integration, log-messages, monitoring, security-filter, security-authorities, security-common, security-siths, sjukfall-engine, common-redis-cache-core, driftbanner-dto, dynamiclink | Shared infrastructure: HSA (healthcare address registry), PU (person data), sick leave calculations, security |
| **Inera Infra Runtime**                        | hsa-integration-intyg-proxy-service, pu-integration-intyg-proxy-service                                                                    | HSA and PU integration via intyg-proxy-service REST APIs (runtimeOnly)                                        |
| **Schema Libraries**                           | clinicalprocess-healthcond-rehabilitation, clinicalprocess-healthcond-srs, schemas-contract, informationsecurity-authorization-consent, informationsecurity-authorization-blocking | RIV-TA SOAP contract schemas (Swedish national interoperability standards)                                    |
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

### Configuration Classes (31 total)

Key configurations: `ApplicationConfig`, `WebSecurityConfig`, `WebConfig`, `ServiceConfig`, `InfraConfig`, `JmsConfig`, `JobConfig`,
`PersistenceConfig`, `EmployeeNameCacheConfig`, `SjukfallConfig`, plus per-integration-module client and stub configurations.

### Spring XML Configuration (4 files)

- `samtyckestjanst-services-config.xml` — CXF SOAP client TLS config
- `samtyckestjanst-stub-context.xml` — Stub endpoint definitions
- `sparrtjanst-services-config.xml` — CXF SOAP client TLS config
- `sparrtjanst-stub-context.xml` — Stub endpoint definitions

---

## Key Observations

1. **Not Spring Boot** — This is a traditional Spring Framework + WAR deployment, not Spring Boot. Uses Gretty plugin for local Tomcat 10
   development.
2. **REST-first with SOAP integrations** — The application's own API layer uses Spring MVC (`@RestController`), while external service
   integrations use a mix of SOAP (CXF/JAXWS) and REST clients.
3. **Backend API only** — No frontend code is bundled; the web module serves only REST APIs. The frontend is deployed separately.
4. **Jakarta EE migration completed** — All namespaces use `jakarta.*`, not `javax.*`, indicating a successful migration to Jakarta EE 10+.
5. **Heavy coupling to Inera ecosystem** — ~16 internal Inera infrastructure dependencies plus schema libraries. The app is tightly
   integrated into the larger Intyg platform.
6. **Redis-centric session and caching** — Redis serves three purposes: HTTP session store, application caching, and distributed lock
   provider (ShedLock).
7. **SAML 2.0 authentication** — Healthcare-grade authentication via SITHS with custom authority resolution and multiple assurance filters.
8. **Mixed configuration** — Combines Java `@Configuration` classes (31) with Spring XML config files (4), primarily XML for legacy
   CXF/SOAP TLS configurations.
9. **JUnit 4 legacy** — Still includes junit-vintage-engine for legacy JUnit 4 test support alongside JUnit 5 Jupiter.
10. **Stub-based development** — Each external service integration module includes a stub implementation activated via Spring profiles,
    enabling local development without external dependencies.

---

## Suggested Additional Analysis

1. **Dependency vulnerability scan** — Check the third-party libraries for known CVEs (CycloneDX BOM is already generated).
2. **Database schema review** — Analyze the Liquibase changelogs to understand the data model.
3. **API surface analysis** — Catalog the REST endpoints exposed by the web module's controllers.
4. **Spring configuration deep-dive** — Map how the 31 `@Configuration` classes wire together and which profiles control what.
5. **Inera BOM version catalog inspection** — Understand exactly which versions of Spring, Hibernate, CXF, etc. are being pulled in
   (they're managed centrally by the BOM).
6. **JUnit 4 test inventory** — Identify which tests still use JUnit 4 to plan the JUnit 5 migration effort.
7. **CXF JAX-RS evaluation** — The `cxf-rt-frontend-jaxrs` dependency is included but JAX-RS is only used in stub REST APIs; evaluate
   whether it can be removed in favor of Spring MVC stubs.
8. **Redis configuration audit** — Review cache TTL policies, session serialization, and Sentinel/HA setup.
