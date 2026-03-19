# Step 9 — Convert JAX-RS Stubs to Spring MVC (Detailed Plan)

## Progress Tracker

> Update the Status column as each sub-step is completed.
> Statuses: `⬜ TODO` | `🔄 IN PROGRESS` | `✅ DONE` | `⏭️ SKIPPED`

| Step     | Description                                                         | Status  | Verified | Notes |
|----------|---------------------------------------------------------------------|---------|----------|-------|
| **9.1**  | Convert `SamtyckestjanstStubRestApi` to `@RestController`           | ⬜ TODO |          |       |
| **9.2**  | Convert `SparrtjanstStubRestApi` to `@RestController`               | ⬜ TODO |          |       |
| **9.3**  | Update `samtyckestjanst-stub-context.xml` — remove JAX-RS sections  | ⬜ TODO |          |       |
| **9.4**  | Update `sparrtjanst-stub-context.xml` — remove JAX-RS sections      | ⬜ TODO |          |       |
| **9.5**  | Remove JAX-RS dependencies from integration module build files      | ⬜ TODO |          |       |
| **9.6**  | Remove JAX-RS dependencies from `web/build.gradle`                  | ⬜ TODO |          |       |
| **9.7**  | Confirm SRS stub needs no changes                                   | ⬜ TODO |          |       |
| **9.8**  | Build verification: `./gradlew build`                               | ⬜ TODO |          |       |
| **9.9**  | Runtime verification: `./gradlew appRunDebug` — no exceptions       | ⬜ TODO |          |       |
| **9.10** | Integration tests: `./gradlew :rehabstod-restassured:test`          | ⬜ TODO |          |       |

---

## Background

### Current State

Rehabstod's main REST controllers already use Spring MVC (`@RestController`). Two integration-test
stub modules still use JAX-RS annotations, registered via CXF `<jaxrs:server>` in XML config:

| Stub Class                   | Module                                | JAX-RS Endpoints | Profile                     |
|------------------------------|---------------------------------------|------------------|-----------------------------|
| `SamtyckestjanstStubRestApi` | `rehabstod-samtyckestjanst-integration` | 5 (PUT/DELETE/GET) | `rhs-samtyckestjanst-stub`  |
| `SparrtjanstStubRestApi`     | `rehabstod-sparrtjanst-integration`     | 4 (PUT/DELETE/GET) | `rhs-sparrtjanst-stub`      |

The SRS stub (`SRSStubRestApi`) already uses `@RestController` + `@RequestMapping("/api/stub/srs")`
— **no conversion needed**.

### Servlet Architecture

- **DispatcherServlet** mapped to `/` — handles all Spring MVC controllers
- **CXFServlet** mapped to `/services/*` — handles JAXWS (SOAP) and JAXRS endpoints

Current JAX-RS stub URLs (through CXF):
- `http://localhost:8030/services/api/stub/samtyckestjanst-api/consent/...`
- `http://localhost:8030/services/api/stub/sparrtjanst-api/person/...`

After conversion (through DispatcherServlet):
- `http://localhost:8030/api/stub/samtyckestjanst-api/consent/...`
- `http://localhost:8030/api/stub/sparrtjanst-api/person/...`

**URL change impact:** The `/services/` prefix is removed. Analysis of the integration test suite
at `intyg-test` shows **no direct references** to `samtyckestjanst-api` or `sparrtjanst-api`.
These REST endpoints are used for manual test data setup; the SOAP stubs (JAXWS) at
`/services/stubs/...` are the primary integration test interface and remain unchanged.

### What Stays in XML (Until Step 10)

The stub XML files also register JAXWS SOAP endpoints:
- `samtyckestjanst-stub-context.xml`: `CheckConsentStub`, `RegisterExtendedConsentStub`
- `sparrtjanst-stub-context.xml`: `SparrtjanstIntegrationStub`

These JAXWS endpoints **remain in the XML** for now. They are converted to Java config in Step 10.

---

## Detailed Steps

### Step 9.1 — Convert `SamtyckestjanstStubRestApi`

**File:** `integration/samtyckestjanst-integration/src/main/java/.../stub/SamtyckestjanstStubRestApi.java`

**Changes:**

1. Add class-level annotations:
   ```java
   @RestController
   @Profile("rhs-samtyckestjanst-stub")
   @RequestMapping("/api/stub/samtyckestjanst-api")
   ```

2. Apply annotation mapping (per `first-migration-scope.md §3.3`):

   | JAX-RS                            | Spring MVC                            |
   |-----------------------------------|---------------------------------------|
   | `@PUT`                            | `@PutMapping`                         |
   | `@GET`                            | `@GetMapping`                         |
   | `@DELETE`                         | `@DeleteMapping`                      |
   | `@Path("/consent/{personId}")`    | path param on method annotation       |
   | `@PathParam("personId")`          | `@PathVariable("personId")`           |
   | `@QueryParam("vardgivareId")`     | `@RequestParam("vardgivareId")`       |
   | `@Produces(MediaType.APPLICATION_JSON)` | `produces = MediaType.APPLICATION_JSON_VALUE` on method annotation |
   | `Response.ok().build()`           | `ResponseEntity.ok().build()`         |
   | `Response.ok(body).build()`       | `ResponseEntity.ok(body)`             |

3. Remove all `jakarta.ws.rs.*` imports, replace with `org.springframework.web.bind.annotation.*`
   and `org.springframework.http.ResponseEntity` + `org.springframework.http.MediaType`.

4. Remove `@Autowired` field injection, replace with constructor injection.

**Endpoint summary after conversion:**

| Method         | Path                        | Query Params                                   | Returns                    |
|----------------|-----------------------------|-------------------------------------------------|---------------------------|
| `@PutMapping`  | `/consent/{personId}`       | vardgivareId, vardenhetId, employeeId, from, to | `ResponseEntity<Void>`    |
| `@DeleteMapping` | `/consent/{personId}`     | —                                               | `ResponseEntity<Void>`    |
| `@DeleteMapping` | `/consent`                | —                                               | `ResponseEntity<Void>`    |
| `@GetMapping`  | `/consent/{personId}`       | vardgivareId, vardenhetId                        | `ResponseEntity<Boolean>` |
| `@GetMapping`  | `/consent`                  | —                                               | `ResponseEntity<List<ConsentData>>` |

---

### Step 9.2 — Convert `SparrtjanstStubRestApi`

**File:** `integration/sparrtjanst-integration/src/main/java/.../stub/SparrtjanstStubRestApi.java`

**Changes:** Same annotation mapping as Step 9.1.

1. Add class-level annotations:
   ```java
   @RestController
   @Profile("rhs-sparrtjanst-stub")
   @RequestMapping("/api/stub/sparrtjanst-api")
   ```

2. Convert all method annotations, parameters, and return types.

3. Replace field injection with constructor injection.

**Endpoint summary after conversion:**

| Method         | Path                  | Query Params                     | Returns                         |
|----------------|-----------------------|----------------------------------|---------------------------------|
| `@PutMapping`  | `/person/{personId}`  | from, to, vardgivare, vardenhet  | `ResponseEntity<Void>`          |
| `@DeleteMapping` | `/person/{personId}` | —                               | `ResponseEntity<Void>`          |
| `@DeleteMapping` | `/person`           | —                               | `ResponseEntity<Void>`          |
| `@GetMapping`  | `/`                   | —                               | `ResponseEntity<List<BlockData>>` |

---

### Step 9.3 — Update `samtyckestjanst-stub-context.xml`

**File:** `integration/samtyckestjanst-integration/src/main/resources/samtyckestjanst-stub-context.xml`

**Remove:**
- The `<bean id="samtyckestjanstStubRestApi">` definition (line 38-39) — now a `@RestController`
- The entire `<jaxrs:server address="/api/stub/samtyckestjanst-api">` block (lines 41-51)
- The `<bean id="customJacksonJsonProvider">` definition (lines 53-58) — no longer needed for JAX-RS
- The `jaxrs` namespace declaration and schema location

**Keep:**
- The two `<jaxws:endpoint>` blocks for `CheckConsentStub` and `RegisterExtendedConsentStub`
- The `jaxws` namespace

---

### Step 9.4 — Update `sparrtjanst-stub-context.xml`

**File:** `integration/sparrtjanst-integration/src/main/resources/sparrtjanst-stub-context.xml`

**Remove:**
- The `<bean id="sparrtjanstStubRestApi">` definition (line 34)
- The entire `<jaxrs:server address="/api/stub/sparrtjanst-api">` block (lines 36-46)
- The `<bean id="customJacksonJsonProvider">` definition (lines 48-52)
- The `jaxrs` namespace declaration and schema location

**Keep:**
- The `<jaxws:endpoint>` block for `SparrtjanstIntegrationStub`
- The `jaxws` namespace

---

### Step 9.5 — Remove JAX-RS dependencies from integration modules

**Files:**
- `integration/samtyckestjanst-integration/build.gradle`
- `integration/sparrtjanst-integration/build.gradle`

**Remove from both:**
```gradle
implementation "com.fasterxml.jackson.jakarta.rs:jackson-jakarta-rs-json-provider"
implementation "jakarta.ws.rs:jakarta.ws.rs-api"
```

**Add Spring Web dependency to both** (needed for `@RestController`, `@RequestMapping`, etc.):
```gradle
implementation "org.springframework:spring-webmvc"
```

**Keep in both:** `cxf-rt-frontend-jaxws` (still needed for SOAP stubs in the XML).

---

### Step 9.6 — Remove JAX-RS dependencies from `web/build.gradle`

**File:** `web/build.gradle`

**Remove:**
```gradle
implementation "jakarta.ws.rs:jakarta.ws.rs-api"                            // line 86
implementation "com.fasterxml.jackson.jakarta.rs:jackson-jakarta-rs-json-provider"  // line 81
implementation "org.apache.cxf:cxf-rt-frontend-jaxrs"                      // line 90
```

**Keep:**
```gradle
implementation "org.apache.cxf:cxf-rt-frontend-jaxws"    // still needed for SOAP
implementation "org.apache.cxf:cxf-rt-transports-http"    // still needed for CXF transport
implementation "org.apache.cxf:cxf-rt-features-logging"   // still needed for CXF logging
```

---

### Step 9.7 — Confirm SRS stub needs no changes

**File:** `integration/srs-integration/src/main/java/.../stub/api/SRSStubRestApi.java`

Verify that:
- Already `@RestController` with `@RequestMapping("/api/stub/srs")` ✅
- Uses Spring MVC annotations (`RequestMethod.GET`) ✅
- No `jakarta.ws.rs` imports ✅
- SOAP endpoint (`SRSStub`) uses JAXWS, not JAX-RS ✅

**Result: No changes needed.**

---

### Step 9.8 — Build Verification

```bash
./gradlew build
```

**Expected:**
- All modules compile without errors
- All unit tests pass
- No `jakarta.ws.rs` imports remain in any non-SOAP source file

**Post-build check:**
```bash
grep -r "jakarta.ws.rs" integration/samtyckestjanst-integration/src/main/java/
grep -r "jakarta.ws.rs" integration/sparrtjanst-integration/src/main/java/
grep -r "cxf-rt-frontend-jaxrs" --include="*.gradle"
```

All three should return no matches.

---

### Step 9.9 — Runtime Verification

```bash
./gradlew appRunDebug
```

**Verify:**
- Application starts without runtime exceptions
- Stub profiles are active: `rhs-samtyckestjanst-stub`, `rhs-sparrtjanst-stub`, `rhs-srs-stub`
- Stub REST endpoints respond correctly:
  - `GET http://localhost:8030/api/stub/samtyckestjanst-api/consent` → `200 OK`
  - `GET http://localhost:8030/api/stub/sparrtjanst-api/` → `200 OK`
  - `GET http://localhost:8030/api/stub/srs/active` → `200 OK` (unchanged)

---

### Step 9.10 — Integration Tests

```bash
cd C:\GIT\Inera\Intyg\intyg-test
./gradlew :rehabstod-restassured:test -Dtest.environment=dev
```

**Expected:** All integration tests pass. The tests primarily interact with SOAP stubs (JAXWS)
which remain unchanged. The REST stub endpoints are not directly called by the test suite.

---

## Annotation Mapping Reference (from first-migration-scope.md §3.3)

| JAX-RS                                  | Spring MVC                                                         |
|-----------------------------------------|--------------------------------------------------------------------|
| `@Path("/...")`                         | `@RestController` + `@RequestMapping("/...")`                      |
| `@PUT`                                  | `@PutMapping`                                                      |
| `@GET`                                  | `@GetMapping`                                                      |
| `@DELETE`                               | `@DeleteMapping`                                                   |
| `@Produces(MediaType.APPLICATION_JSON)` | `produces = MediaType.APPLICATION_JSON_VALUE` (or rely on default) |
| `@PathParam`                            | `@PathVariable`                                                    |
| `@QueryParam`                           | `@RequestParam`                                                    |
| `Response.ok().build()`                 | `ResponseEntity.ok().build()`                                      |
| `Response.ok(body).build()`             | `ResponseEntity.ok(body)`                                          |

---

## Deferred Items (carried forward from earlier steps)

These items are NOT addressed in Step 9 but remain open:
- **RSBannerJob** still uses infra `BannerJob` base class and `Application` type (from Step 2)
- **GetConfigResponse** still uses infra `Banner` type (from Step 2)
- **ApplicationConfig** still imports `MonitoringConfiguration` from infra (from Step 3)

---

## Risk Assessment

| Risk | Mitigation |
|------|-----------|
| URL path change (`/services/api/stub/...` → `/api/stub/...`) | Integration tests don't call these REST endpoints directly; SOAP stubs unchanged |
| `customJacksonJsonProvider` removal affects JSON serialization | Spring MVC uses `CustomObjectMapper` already configured in `WebConfig`; verify JSON output matches |
| Removing `cxf-rt-frontend-jaxrs` breaks compilation elsewhere | Grep for imports before removal; JAXWS does not depend on JAX-RS frontend |
| `@Profile` on `@RestController` not picked up | `SamtyckestjanstStubConfiguration` already component-scans the stub package and is profile-gated |
