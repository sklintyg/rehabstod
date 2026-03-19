# Step 10 — Convert All XML Bean Configuration to Java (Detailed Plan)

## Progress Tracker

> Update the Status column as each sub-step is completed.
> Statuses: `⬜ TODO` | `🔄 IN PROGRESS` | `✅ DONE` | `⏭️ SKIPPED`

| Step      | Description                                                            | Status  | Verified | Notes |
|-----------|------------------------------------------------------------------------|---------|----------|-------|
| **10.1**  | Convert `samtyckestjanst-stub-context.xml` JAXWS endpoints to Java    | ⬜ TODO |          |       |
| **10.2**  | Convert `sparrtjanst-stub-context.xml` JAXWS endpoint to Java         | ⬜ TODO |          |       |
| **10.3**  | Convert `samtyckestjanst-services-config.xml` TLS config to Java      | ⬜ TODO |          |       |
| **10.4**  | Convert `sparrtjanst-services-config.xml` TLS config to Java          | ⬜ TODO |          |       |
| **10.5**  | Remove `@ImportResource("classpath:META-INF/cxf/cxf.xml")` from both | ⬜ TODO |          |       |
| **10.6**  | Delete all 4 XML config files                                         | ⬜ TODO |          |       |
| **10.7**  | Final `@ImportResource` sweep — verify zero remain                    | ⬜ TODO |          |       |
| **10.8**  | Build verification: `./gradlew build`                                 | ⬜ TODO |          |       |
| **10.9**  | Runtime verification: `./gradlew appRunDebug` — no exceptions         | ⬜ TODO |          |       |
| **10.10** | Integration tests: `./gradlew :rehabstod-restassured:test`            | ⬜ TODO |          |       |

---

## Background

### Current State (after Step 9)

There are **6 active `@ImportResource` annotations** across the codebase:

| File | Import | Purpose |
|------|--------|---------|
| `SamtyckestjanstStubConfiguration` | `classpath:samtyckestjanst-stub-context.xml` | 2 JAXWS stub endpoints (consent) |
| `SparrtjanstStubConfiguration` | `classpath:sparrtjanst-stub-context.xml` | 1 JAXWS stub endpoint (blocking) |
| `SamtyckestjanstConfiguration` | `classpath:samtyckestjanst-services-config.xml` | CXF HTTP conduit TLS config |
| `SparrtjanstConfiguration` | `classpath:sparrtjanst-services-config.xml` | CXF HTTP conduit TLS config |
| `ApplicationConfig` | `classpath:META-INF/cxf/cxf.xml` | CXF Bus framework bootstrap |
| `WebConfig` | `classpath:META-INF/cxf/cxf.xml` | CXF Bus framework bootstrap (duplicate) |

**Target:** Zero `@ImportResource` annotations. All 4 local XML files deleted.

### Reference Pattern: SRS Stub (Already Converted)

`SRSIntegrationStubConfiguration` already demonstrates the correct Java pattern for JAXWS:

```java
@Autowired private Bus bus;
@Autowired private SRSStub srsStub;

@Bean
public EndpointImpl srsResponder() {
    EndpointImpl endpoint = new EndpointImpl(bus, srsStub);
    endpoint.publish("/stubs/get-risk-prediction-for-certificate/v1.0");
    return endpoint;
}
```

### Deferred Items Status

All previously deferred items are **already resolved** — no carryover for Step 10:
- ✅ Step 2: `RSBannerJob` / `GetConfigResponse` → Resolved in Step 7
- ✅ Step 3: `MonitoringConfiguration` → Resolved in Step 7
- ✅ Step 5: `BaseUserDetailsService` → Resolved in Step 5
- ✅ Step 6: `InternalApiFilter` → Intentionally skipped

---

## Detailed Steps

### Step 10.1 — Convert `samtyckestjanst-stub-context.xml` JAXWS endpoints to Java

**XML being replaced** (`samtyckestjanst-stub-context.xml`):
```xml
<jaxws:endpoint address="/stubs/.../CheckConsent/2/rivtabp21"
    implementor="...CheckConsentStub"/>
<jaxws:endpoint address="/stubs/.../RegisterExtendedConsent/2/rivtabp21"
    implementor="...RegisterExtendedConsentStub"/>
```

**Changes to `SamtyckestjanstStubConfiguration.java`:**

1. Remove `@ImportResource("classpath:samtyckestjanst-stub-context.xml")`
2. Add `@Autowired Bus bus`
3. Add `@Component` to `CheckConsentStub` and `RegisterExtendedConsentStub` (they have no
   stereotype annotation — they were XML-instantiated beans. `@ComponentScan` on the config already
   scans the stub package, so adding `@Component` makes them injectable.)
4. Add two `@Bean EndpointImpl` methods following the SRS pattern:

```java
@Autowired private Bus bus;
@Autowired private CheckConsentStub checkConsentStub;
@Autowired private RegisterExtendedConsentStub registerExtendedConsentStub;

@Bean
public EndpointImpl checkConsentEndpoint() {
    EndpointImpl endpoint = new EndpointImpl(bus, checkConsentStub);
    endpoint.publish("/stubs/informationsecurity/authorization/consent/CheckConsent/2/rivtabp21");
    return endpoint;
}

@Bean
public EndpointImpl registerExtendedConsentEndpoint() {
    EndpointImpl endpoint = new EndpointImpl(bus, registerExtendedConsentStub);
    endpoint.publish("/stubs/informationsecurity/authorization/consent/RegisterExtendedConsent/2/rivtabp21");
    return endpoint;
}
```

**Files modified:**
- `SamtyckestjanstStubConfiguration.java` — remove `@ImportResource`, add beans
- `CheckConsentStub.java` — add `@Component`
- `RegisterExtendedConsentStub.java` — add `@Component`

---

### Step 10.2 — Convert `sparrtjanst-stub-context.xml` JAXWS endpoint to Java

**XML being replaced** (`sparrtjanst-stub-context.xml`):
```xml
<jaxws:endpoint address="/stubs/.../CheckBlocks/4/rivtabp21"
    implementor="...SparrtjanstIntegrationStub"/>
```

**Changes to `SparrtjanstStubConfiguration.java`:**

1. Remove `@ImportResource("classpath:sparrtjanst-stub-context.xml")`
2. Add `@Autowired Bus bus`
3. Add `@Component` to `SparrtjanstIntegrationStub`
4. Add one `@Bean EndpointImpl` method:

```java
@Autowired private Bus bus;
@Autowired private SparrtjanstIntegrationStub sparrtjanstIntegrationStub;

@Bean
public EndpointImpl checkBlocksEndpoint() {
    EndpointImpl endpoint = new EndpointImpl(bus, sparrtjanstIntegrationStub);
    endpoint.publish("/stubs/informationsecurity/authorization/blocking/CheckBlocks/4/rivtabp21");
    return endpoint;
}
```

**Files modified:**
- `SparrtjanstStubConfiguration.java` — remove `@ImportResource`, add bean
- `SparrtjanstIntegrationStub.java` — add `@Component`

---

### Step 10.3 — Convert `samtyckestjanst-services-config.xml` TLS config to Java

**XML being replaced** (`samtyckestjanst-services-config.xml`):
```xml
<beans profile="!rhs-samtyckestjanst-stub">
  <http:conduit name="\{urn:riv:(informationsecurity:authorization:consent):.*.http-conduit">
    <http:client AllowChunking="false" AutoRedirect="true" Connection="Keep-Alive"/>
    <http:tlsClientParameters disableCNCheck="true">
      <sec:keyManagers keyPassword="${ntjp.ws.key.manager.password}">
        <sec:keyStore file="${ntjp.ws.certificate.file}" password="..." type="..."/>
      </sec:keyManagers>
      <sec:trustManagers>
        <sec:keyStore file="${ntjp.ws.truststore.file}" password="..." type="..."/>
      </sec:trustManagers>
      <sec:cipherSuitesFilter>...</sec:cipherSuitesFilter>
    </http:tlsClientParameters>
  </http:conduit>
</beans>
```

**Approach:** Apply TLS configuration directly in `SamtyckestjanstClientConfiguration.java` where
the `JaxWsProxyFactoryBean` client is already created and the `HTTPConduit` is already accessed
(for timeouts). This is cleaner than using a regex-based conduit configurer.

**Changes to `SamtyckestjanstClientConfiguration.java`:**

1. Add `@Value` fields for TLS properties:
   ```java
   @Value("${ntjp.ws.certificate.file}") private String certFile;
   @Value("${ntjp.ws.certificate.password}") private String certPassword;
   @Value("${ntjp.ws.certificate.type}") private String certType;
   @Value("${ntjp.ws.key.manager.password}") private String keyManagerPassword;
   @Value("${ntjp.ws.truststore.file}") private String truststoreFile;
   @Value("${ntjp.ws.truststore.password}") private String truststorePassword;
   @Value("${ntjp.ws.truststore.type}") private String truststoreType;
   ```

2. Add `@Profile("!rhs-samtyckestjanst-stub")` to the TLS-applying method or conditionally apply
   TLS (in dev with stubs active, the SOAP client beans don't exist because the stub beans satisfy
   the interface — but actually, looking at the config, the `ClientConfiguration` doesn't have a
   profile. It creates the SOAP client proxy regardless. The TLS XML has a `!stub` profile because
   in dev there's no TLS cert file. The client config creates the proxy with `localhost` URLs that
   work without TLS in dev.)

   **Better approach:** The `SamtyckestjanstConfiguration` (parent config) already imports both
   the stub config AND the services-config XML. The XML has `profile="!rhs-samtyckestjanst-stub"`.
   Move the TLS configuration into the parent `SamtyckestjanstConfiguration` as a
   `@Profile("!rhs-samtyckestjanst-stub")` inner class or separate method. Alternatively, since
   `SamtyckestjanstClientConfiguration` creates the CXF clients, apply TLS there with a profile
   guard.

   **Simplest approach:** Create a new `SamtyckestjanstTlsConfig` inner class or standalone class
   with `@Profile("!rhs-samtyckestjanst-stub")` that configures TLS on the CXF Bus as an
   `HTTPConduitConfigurer`. This cleanly separates TLS from the client factory and mirrors the
   XML's profile-based activation.

3. Create `SamtyckestjanstTlsConfig` (can be an inner `@Configuration` class in
   `SamtyckestjanstConfiguration` or a standalone file):

```java
@Configuration
@Profile("!rhs-samtyckestjanst-stub")
public class SamtyckestjanstTlsConfig {

    @Value("${ntjp.ws.certificate.file}") private String certFile;
    @Value("${ntjp.ws.certificate.password}") private String certPassword;
    @Value("${ntjp.ws.certificate.type}") private String certType;
    @Value("${ntjp.ws.key.manager.password}") private String keyManagerPassword;
    @Value("${ntjp.ws.truststore.file}") private String truststoreFile;
    @Value("${ntjp.ws.truststore.password}") private String truststorePassword;
    @Value("${ntjp.ws.truststore.type}") private String truststoreType;

    @Bean
    public HTTPConduitConfigurer samtyckestjanstConduitConfigurer() {
        return (name, address, conduit) -> {
            if (name != null && name.matches("\\{urn:riv:(informationsecurity:authorization:consent):.*\\.http-conduit")) {
                configureTls(conduit);
                configureHttpClient(conduit);
            }
        };
    }

    private void configureTls(HTTPConduit conduit) {
        TLSClientParameters tlsParams = new TLSClientParameters();
        tlsParams.setDisableCNCheck(true);
        // KeyManagers, TrustManagers, CipherSuiteFilter...
        conduit.setTlsClientParameters(tlsParams);
    }

    private void configureHttpClient(HTTPConduit conduit) {
        HTTPClientPolicy policy = new HTTPClientPolicy();
        policy.setAllowChunking(false);
        policy.setAutoRedirect(true);
        policy.setConnection(ConnectionType.KEEP_ALIVE);
        conduit.setClient(policy);
    }
}
```

   **Issue:** CXF only supports a single `HTTPConduitConfigurer` bean globally. If both
   samtyckestjanst and sparrtjanst register one, only the last wins.

   **Better approach:** Create a single shared `CxfTlsConduitConfigurer` bean that handles all
   conduit patterns, or apply TLS directly in the `ClientConfiguration.applyTimeouts()` method
   (which already has access to the `HTTPConduit`).

   **Best approach (direct on conduit):** Extend `applyTimeouts()` in
   `SamtyckestjanstClientConfiguration` to also apply TLS. Wrap in a profile guard:

```java
@Value("${ntjp.ws.certificate.file:}") private String certFile;
// ... other TLS properties

private void applyConduitConfig(Client client) {
    if (client != null) {
        HTTPConduit conduit = (HTTPConduit) client.getConduit();
        applyTimeouts(conduit);
        if (!certFile.isEmpty()) {
            applyTls(conduit);
        }
    }
}
```

   This avoids the single-`HTTPConduitConfigurer` limitation and is simpler.

**Recommended approach:** Apply TLS directly in each `ClientConfiguration` class, guarded by
whether the cert file property is non-empty (in dev with stubs, these properties resolve to
`dummy`/empty, and the client proxy points to localhost stubs anyway — TLS is irrelevant).

**Changes to `SamtyckestjanstConfiguration.java`:**
- Remove `@ImportResource("classpath:samtyckestjanst-services-config.xml")`

---

### Step 10.4 — Convert `sparrtjanst-services-config.xml` TLS config to Java

Same pattern as Step 10.3, applied to `SparrtjanstClientConfiguration.java`.

**Changes to `SparrtjanstConfiguration.java`:**
- Remove `@ImportResource("classpath:sparrtjanst-services-config.xml")`

---

### Step 10.5 — Remove `@ImportResource("classpath:META-INF/cxf/cxf.xml")`

**Files:**
- `web/src/main/java/.../config/ApplicationConfig.java` (line 47)
- `web/src/main/java/.../config/WebConfig.java` (line 52)

**Why safe to remove:** `META-INF/cxf/cxf.xml` is a CXF-provided XML that bootstraps the CXF
`Bus` bean. The Bus is already available because:

1. CXF's `CXFServlet` (registered in `ApplicationInitializer`) bootstraps a `SpringBus` into the
   Spring context automatically.
2. `ApplicationConfig` already `@Autowired Bus bus` and configures features on it in `@PostConstruct`.
3. Both `ApplicationConfig` and `WebConfig` import the same file — one of them is redundant anyway.

Removing `@ImportResource` simply stops loading the CXF Bus from XML; CXF's auto-discovery
via `CXFServlet` ensures the Bus is still available.

**Changes:**
- `ApplicationConfig.java` — remove `@ImportResource({"classpath:META-INF/cxf/cxf.xml"})`
- `WebConfig.java` — remove `@ImportResource({"classpath:META-INF/cxf/cxf.xml"})` and the
  `import org.springframework.context.annotation.ImportResource;` statement

---

### Step 10.6 — Delete all 4 XML config files

**Delete:**
1. `integration/samtyckestjanst-integration/src/main/resources/samtyckestjanst-stub-context.xml`
2. `integration/samtyckestjanst-integration/src/main/resources/samtyckestjanst-services-config.xml`
3. `integration/sparrtjanst-integration/src/main/resources/sparrtjanst-stub-context.xml`
4. `integration/sparrtjanst-integration/src/main/resources/sparrtjanst-services-config.xml`

---

### Step 10.7 — Final `@ImportResource` sweep

```bash
grep -r "@ImportResource" --include="*.java" .
```

**Expected:** Zero results (the IT-integration and SRS-integration javadoc comments that mention
`@ImportResource` should have already been cleaned up or are just documentation).

---

### Step 10.8 — Build verification

```bash
./gradlew build
```

**Expected:** All modules compile, all tests pass (including samtyckestjanst + sparrtjanst unit
tests which test the service/client layers).

---

### Step 10.9 — Runtime verification

```bash
./gradlew appRunDebug
```

**Verify:**
- Application starts without runtime exceptions
- CXF Bus initializes (log line: `Creating Service {urn:riv:...}`)
- JAXWS stub endpoints respond:
  - SOAP stubs are registered at `/services/stubs/...`
  - REST stubs respond at `/api/stub/...` (from Step 9)
- No `ClassNotFoundException` or `BeanCreationException` in console

---

### Step 10.10 — Integration tests

```bash
cd C:\GIT\Inera\Intyg\intyg-test
./gradlew :rehabstod-restassured:test -Dtest.environment=dev
```

**Expected:** All tests pass. SOAP stubs are critical for integration tests.

---

## Key Decisions

### TLS Configuration Strategy

Two viable approaches:

| Approach | Pros | Cons |
|----------|------|------|
| **A. Direct on `HTTPConduit` in `ClientConfiguration`** | Simple, no global configurer conflicts, TLS applied exactly where needed | Mixes TLS concerns with client factory |
| **B. Shared `HTTPConduitConfigurer` bean** | Clean separation, mirrors XML pattern | Single-bean limitation — must handle all patterns in one configurer |

**Chosen: Approach A** — Apply TLS directly in each `ClientConfiguration.applyTimeouts()` method.
- The method already accesses the `HTTPConduit`
- Profile-guarded by checking if cert file is non-empty
- Both samtyckestjanst and sparrtjanst TLS configs use identical properties (`ntjp.ws.*`)
- Avoids the single-`HTTPConduitConfigurer` limitation

### CXF Bus Bootstrap

After removing `@ImportResource("classpath:META-INF/cxf/cxf.xml")`, the CXF Bus is still created
by the `CXFServlet` registered in `ApplicationInitializer`. The `@PostConstruct init()` method
in `ApplicationConfig` continues to configure logging features on the Bus.

If the Bus is not automatically available after removal (unlikely but possible), add:
```java
@Bean
public SpringBus cxf() {
    return new SpringBus();
}
```

---

## Risk Assessment

| Risk | Likelihood | Mitigation |
|------|-----------|-----------|
| CXF Bus not available after removing cxf.xml import | Low | CXFServlet creates it; fallback: add `SpringBus` `@Bean` |
| TLS not applied to SOAP clients in production | Medium | Test with non-stub profile; verify conduit TLS params in logs |
| JAXWS stub endpoints not registered (stubs broken in dev) | Low | SRS pattern proven; verify with SOAP calls to stub endpoints |
| `@Autowired` in stub classes fails (no Spring bean) | Low | Adding `@Component` + `@ComponentScan` covers it |
| Cipher suite filter not applied correctly in Java | Low | Replicate exact same filters from XML |

---

## Files Summary

### New files
- None (all changes are to existing files)

### Modified files
| File | Change |
|------|--------|
| `SamtyckestjanstStubConfiguration.java` | Remove `@ImportResource`, add `EndpointImpl` beans |
| `SparrtjanstStubConfiguration.java` | Remove `@ImportResource`, add `EndpointImpl` bean |
| `CheckConsentStub.java` | Add `@Component` |
| `RegisterExtendedConsentStub.java` | Add `@Component` |
| `SparrtjanstIntegrationStub.java` | Add `@Component` |
| `SamtyckestjanstConfiguration.java` | Remove `@ImportResource` |
| `SparrtjanstConfiguration.java` | Remove `@ImportResource` |
| `SamtyckestjanstClientConfiguration.java` | Add TLS configuration to conduit setup |
| `SparrtjanstClientConfiguration.java` | Add TLS configuration to conduit setup |
| `ApplicationConfig.java` | Remove `@ImportResource(cxf.xml)` |
| `WebConfig.java` | Remove `@ImportResource(cxf.xml)` |

### Deleted files
| File | Reason |
|------|--------|
| `samtyckestjanst-stub-context.xml` | JAXWS endpoints moved to Java config |
| `samtyckestjanst-services-config.xml` | TLS config moved to `ClientConfiguration` |
| `sparrtjanst-stub-context.xml` | JAXWS endpoint moved to Java config |
| `sparrtjanst-services-config.xml` | TLS config moved to `ClientConfiguration` |

### After this step
- `grep -r "@ImportResource" --include="*.java"` → **zero results**
- No Spring XML bean configuration files remain in the project
- Only logback XML files remain (logging framework requirement, not Spring beans)
