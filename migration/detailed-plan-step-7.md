# Step 7 — Inline service wrappers: `dynamiclink`, `ia-integration`, `rediscache`

## Problem Statement

The `web` module depends on three thin infra service wrappers with no shared dependencies on the
security or sjukfall code already inlined. Clearing them now empties the last "medium complexity"
infra bucket before tackling the HSA/PU replacement in Step 8.

- **`dynamiclink`:** 5 Java files — model, service interface/impl, repository interface/impl.
  Loaded via `@ComponentScan` in `InfraConfig`.
- **`ia-integration`:** 6 Java files + 2 XML configs — banner service, scheduled job, cache config,
  stub service, stub REST API. Loaded via `@ImportResource` in `IaConfiguration` /
  `IaStubConfiguration`.
- **`rediscache`:** `EmployeeNameCacheConfig` uses `RedisCacheOptionsSetter` from infra's
  `common-redis-cache-core`. `CacheConfigurationFromInfra` imports `basic-cache-config.xml`.

The infra JARs remain on the classpath — dependency lines will be removed in Step 11.

---

## Current State (after Step 6)

- Build: ✅ compiles, 613 tests pass, 0 failures
- `InfraConfig` has `@ComponentScan({"se.inera.intyg.infra.dynamiclink"})` — only purpose
- `IaConfiguration` has `@ImportResource("classpath:ia-services-config.xml")` + `@Import(IaStubConfiguration.class)`
- `IaStubConfiguration` has `@ImportResource("classpath:ia-stub-context.xml")` with `@Profile({"dev", "ia-stub"})`
- `CacheConfigurationFromInfra` has `@ImportResource({"classpath:basic-cache-config.xml"})`
- `ApplicationConfig` still imports `MonitoringConfiguration` (deferred from Step 3 — kept because
  `BannerJob` depends on `LogMDCHelper` from infra monitoring)
- Local `Banner`, `Application`, `BannerPriority` DTOs already exist in
  `se.inera.intyg.rehabstod.web.dto.driftbanner` (created in Step 2 but not yet used — deferred)

### Deferred items resolvable in this step

| From step | Item | Resolution |
|-----------|------|------------|
| Step 2 | `RSBannerJob` still uses `se.inera.intyg.infra.driftbannerdto.Application` | Switch to local `Application` when `BannerJob` is inlined |
| Step 2 | `GetConfigResponse` still uses `se.inera.intyg.infra.driftbannerdto.Banner` | Switch to local `Banner` when `IABannerService` is inlined |
| Step 3 | `ApplicationConfig` still imports `MonitoringConfiguration` | Remove when `LogMDCHelper` dependency in `BannerJob` is eliminated |

---

## Part A — Inline `dynamiclink` (5 files)

### Target Package

```
web/src/main/java/se/inera/intyg/rehabstod/dynamiclink/
  model/           ← DynamicLink
  service/         ← DynamicLinkService, DynamicLinkServiceImpl
  repository/      ← DynamicLinkRepository, DynamicLinkRepositoryImpl
```

### Files to Copy

Source: `C:\GIT\Inera\Intyg\infra\dynamiclink\src\main\java\se\inera\intyg\infra\dynamiclink\`

| Source file | Target package | Notes |
|---|---|---|
| `model/DynamicLink.java` | `rehabstod.dynamiclink.model` | POJO with key, url, text, tooltip, target |
| `service/DynamicLinkService.java` | `rehabstod.dynamiclink.service` | Interface: `getAllAsMap()`, `getAllAsList()`, `get(key)` |
| `service/DynamicLinkServiceImpl.java` | `rehabstod.dynamiclink.service` | `@Service`. Delegates to repository |
| `repository/DynamicLinkRepository.java` | `rehabstod.dynamiclink.repository` | Interface: `getAll()` |
| `repository/DynamicLinkRepositoryImpl.java` | `rehabstod.dynamiclink.repository` | `@Service`. Loads links from JSON file via `${dynamic.links.file}` property. Uses Jackson + `ResourceLoader` |

### Files That Need Import Updates

| File | Imports to change |
|---|---|
| `ConfigController.java` | `DynamicLink`, `DynamicLinkService` |
| `ConfigControllerTest.java` | `DynamicLinkService` |

### Config Changes

- `InfraConfig.java` — change `@ComponentScan({"se.inera.intyg.infra.dynamiclink"})` to
  `@ComponentScan({"se.inera.intyg.rehabstod.dynamiclink"})` so the local classes are auto-discovered.

---

## Part B — Inline `ia-integration` (6 files + 2 XML → Java config)

This is the most complex part of Step 7. It involves copying/rewriting the banner service, removing
XML configuration, converting to pure Java `@Configuration`, and resolving three deferred items.

### Target Package

```
web/src/main/java/se/inera/intyg/rehabstod/integration/ia/
  services/         ← IABannerService, IABannerServiceImpl
  jobs/             ← (inlined into RSBannerJob — no separate class needed)
  cache/            ← IaCacheConfiguration
  stub/             ← IABannerServiceStub, IAStubRestController (Spring MVC)
```

### Files to Copy / Rewrite

Source: `C:\GIT\Inera\Intyg\infra\integration\ia-integration\src\main\java\se\inera\intyg\infra\integration\ia\`

| Source file | Action | Notes |
|---|---|---|
| `services/IABannerService.java` | **Copy + modify** | Change return types to local `Banner` / `Application` |
| `services/IABannerServiceImpl.java` | **Copy + modify** | Use local `Banner` / `Application`. Uses `@Autowired` field injection for `RestTemplate`, `Cache`, and `@Value` for URL. Spring processes these annotations on beans created via `@Bean` methods. |
| `jobs/BannerJob.java` | **Inline into `RSBannerJob`** | No separate abstract class needed — RSBannerJob is the only subclass. Remove `LogMDCHelper` dependency (see below) |
| `cache/IaCacheConfiguration.java` | **Copy + modify** | Use Spring `RedisCacheManager` directly instead of `RedisCacheOptionsSetter`. Cache name is `"iaCache:rehabstod"` (from `${app.name}` = `rehabstod`). Cache expiry from `${intygsadmin.cache.expiry}` = `86400`. Also provides `@Bean("iaRestTemplate")` RestTemplate. |
| `stub/IABannerServiceStub.java` | **Copy + modify** | Use local types. Extends local `IABannerServiceImpl` |
| `stub/IAStubRestApi.java` | **Copy + convert to Spring MVC** | Replace JAX-RS (`@GET`, `@PUT`, `@DELETE`) with Spring MVC (`@RestController`). Aligns with §3.2.2 goal |

### Bean Wiring Approach for IABannerServiceImpl

The infra XML creates `IABannerServiceImpl` via `<bean class="..."/>` without explicit property
injection — it relies on `@Autowired` field annotations on the class.

**Approach:** Use `@Bean` methods in `IaConfiguration` returning `new IABannerServiceImpl()`.
Spring's bean post-processing pipeline will process `@Autowired`, `@Qualifier`, and `@Value`
annotations on the returned instance. This is standard Spring `@Bean` behavior.

Dependencies the bean needs from the context:
- `@Qualifier("iaRestTemplate") RestTemplate` — provided by `IaCacheConfiguration.restTemplate()`
- `Cache iaCache` — provided by `IaCacheConfiguration.iaCache()`
- `@Value("${intygsadmin.url}") String` — from `application.properties` / dev properties

### BannerJob inlining into RSBannerJob

Infra's `BannerJob` (abstract):
- `@Scheduled(cron = "${intygsadmin.cron}")` + `@SchedulerLock`
- Injects `IABannerService` and `LogMDCHelper`
- `run()` method: `logMDCHelper.run(() -> iaBannerService.loadBanners(getApplication()))`
- Abstract `getApplication()` overridden by RSBannerJob to return `Application.REHABSTOD`

`LogMDCHelper` (from infra monitoring) wraps a `Runnable` with MDC trace-id context. It is **not
used anywhere else in rehabstod** and `MonitoringConfiguration` was kept solely for this.

**Decision:** Inline `BannerJob.run()` logic directly into `RSBannerJob`. Replace
`logMDCHelper.run(...)` by calling the lambda body directly (the scheduled job runs in a thread
pool where MDC context propagation via this helper adds minimal value). This eliminates the
`LogMDCHelper` dependency and allows removing `MonitoringConfiguration` from `ApplicationConfig`.

Rewritten `RSBannerJob`:
```java
@Component
public class RSBannerJob {

    private static final Logger LOG = LoggerFactory.getLogger(RSBannerJob.class);

    @Autowired
    private IABannerService iaBannerService;

    @Scheduled(cron = "${intygsadmin.cron}")
    @SchedulerLock(name = "BannerJob.run", lockAtLeastFor = "PT30S", lockAtMostFor = "PT10M")
    public void run() {
        List<Banner> banners = iaBannerService.loadBanners(Application.REHABSTOD);
        LOG.debug("Loaded banners from IA, found {} banners", banners.size());
    }
}
```

### ⚠️ ShedLock status (pre-existing issue)

**Finding:** `@EnableSchedulerLock` is NOT present anywhere in the codebase. Only
`shedlock-provider-redis-spring` is declared in `build.gradle` (not `shedlock-spring`). This means
`@SchedulerLock` on infra's `BannerJob` is currently a **no-op** — the AOP interceptor that
processes it is not configured.

`JobConfig` has `@EnableScheduling` and defines a `LockProvider` bean, but without
`@EnableSchedulerLock` (from `shedlock-spring` module), the lock annotations are never intercepted.

**Recommendation for this step:**
1. Add `@SchedulerLock` to the inlined `RSBannerJob` anyway — it documents locking intent
2. Add `shedlock-spring` dependency to `web/build.gradle`:
   `implementation "net.javacrumbs.shedlock:shedlock-spring"`
3. Add `@EnableSchedulerLock(defaultLockAtMostFor = "PT10M")` to `JobConfig`
4. Fix the `RedisLockProvider` prefix from `"webcert"` to `"rehabstod"` (pre-existing copy-paste bug)

Items 2–4 are **optional improvements** — they fix a pre-existing issue, not something introduced
by this step. If out of scope, at minimum document the issue.

### Switch to local `Banner` / `Application` types (resolves Step 2 deferred items)

Local DTOs already exist in `se.inera.intyg.rehabstod.web.dto.driftbanner`:
- `Application` — enum with WEBCERT, REHABSTOD, INTYGSSTATISTIK, MINA_INTYG
- `Banner` — POJO with id, createdAt, application, message, displayFrom, displayTo, priority
- `BannerPriority` — enum with LAG, MEDEL, HOG

These have the same JSON shape as the infra `driftbannerdto` types, so `RestTemplate` deserialization
will work unchanged.

Files to switch to local types:
| File | Change |
|---|---|
| `RSBannerJob.java` | `Application.REHABSTOD` → local `Application` |
| `GetConfigResponse.java` | `List<Banner> banners` → local `Banner` |
| `ConfigController.java` | Return type of `getCurrentBanners()` uses local `Banner` (implicit via IABannerService) |
| `IABannerService.java` (local copy) | Interface uses local `Banner` / `Application` |
| `IABannerServiceImpl.java` (local copy) | Uses local `Banner` / `Application` |

### Remove `MonitoringConfiguration` (resolves Step 3 deferred item)

After eliminating `LogMDCHelper` from `BannerJob`:
- `ApplicationConfig.java` — remove `@Import(MonitoringConfiguration.class)` and its import statement
- `ApplicationInitializer.java` — remove `MonitoringConfiguration.class` from `appContext.register(...)` and its import statement

### ⚠️ `LogbackConfiguratorContextListener` (requires replacement)

`ApplicationInitializer.java` registers:
```java
servletContext.setInitParameter("logbackConfigParameter", "logback.file");
servletContext.addListener(new LogbackConfiguratorContextListener());
```

This listener (from infra monitoring) reads the `logbackConfigParameter` servlet context parameter,
looks up the system property `logback.file`, and configures Logback from that path. If the system
property is absent, it falls back to `classpath:logback-spring.xml`.

**This listener is imported from `se.inera.intyg.infra.monitoring.logging`** — removing
`MonitoringConfiguration` alone won't remove the import, but the class comes from the infra
`monitoring` JAR. Since we're not removing JAR dependencies until Step 11, the class is still on
the classpath. However, the Step 3 plan marked this listener for removal.

**Decision:** Create a minimal local replacement:
```java
// se.inera.intyg.rehabstod.logging.LogbackConfiguratorContextListener
public class LogbackConfiguratorContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        String paramName = sce.getServletContext().getInitParameter("logbackConfigParameter");
        String configFile = (paramName != null) ? System.getProperty(paramName) : null;
        if (configFile != null) {
            // Re-initialize Logback from the specified file
            LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(context);
            context.reset();
            configurator.doConfigure(configFile);
        }
    }
}
```

This replaces the infra listener with a local one, preserving the `-Dlogback.file=...` mechanism
used in production deployments. Update `ApplicationInitializer` to import the local class instead.

### XML → Java Config Conversion

**`IaConfiguration.java`** — currently:
```java
@Configuration
@ImportResource("classpath:ia-services-config.xml")
@Import(IaStubConfiguration.class)
public class IaConfiguration {}
```

The XML (`ia-services-config.xml`) conditionally creates beans based on profiles:
- `ia-stub` profile → `IABannerServiceStub` bean
- `!ia-stub` profile → `IABannerServiceImpl` bean
- `qa,prod` or `caching-enabled` profile → `IaCacheConfiguration` bean

Replace with pure Java:
```java
@Configuration
@Import(IaStubConfiguration.class)
public class IaConfiguration {

    @Bean
    @Profile("!ia-stub")
    public IABannerServiceImpl iaBannerService() { return new IABannerServiceImpl(); }

    @Bean
    @Profile("ia-stub")
    public IABannerServiceStub iaBannerServiceStub() { return new IABannerServiceStub(); }

    @Bean
    @Profile({"qa", "prod", "caching-enabled"})
    public IaCacheConfiguration iaCacheConfiguration() { return new IaCacheConfiguration(); }
}
```

> **Note on profile logic:** The original XML uses nested `<beans>` to express AND for the
> caching-enabled case (`!prod` AND `!qa` AND `caching-enabled`). The Java replacement uses
> `@Profile({"qa", "prod", "caching-enabled"})` which is OR semantics — this is intentionally
> broader and safe: if any of these profiles is active, caching is enabled. This matches the
> effective production behavior.

**`IaStubConfiguration.java`** — currently:
```java
@Configuration
@ImportResource("classpath:ia-stub-context.xml")
@Profile({"dev", "ia-stub"})
public class IaStubConfiguration {}
```

The XML (`ia-stub-context.xml`) registers `IAStubRestApi` as a CXF JAX-RS server at `/api/ia-api`.

Replace with a Spring MVC `@RestController` (convert the JAX-RS stub). The new controller handles
GET/PUT/DELETE for banner management in dev/stub mode. `IaStubConfiguration` can be simplified to
an empty `@Configuration` or deleted if the `@RestController` with `@Profile` is auto-discovered
by the existing component scan in `JobConfig` (which scans `se.inera.intyg.rehabstod.jobs`).

Since the stub controller lives under `rehabstod.integration.ia.stub`, it needs to be
component-scanned. **Options:**
1. Add `@ComponentScan("se.inera.intyg.rehabstod.integration.ia.stub")` to `IaStubConfiguration`
2. Register it as a `@Bean` in `IaStubConfiguration`

Option 1 is simpler and consistent with how other stubs work.

### Files That Need Import Updates (ia-integration)

| File | Imports to change |
|---|---|
| `ConfigController.java` | `IABannerService` → local |
| `ConfigControllerTest.java` | `IABannerService` → local |
| `RSBannerJob.java` | Complete rewrite (see above) |
| `GetConfigResponse.java` | `se.inera.intyg.infra.driftbannerdto.Banner` → local `Banner` |

---

## Part C — Adapt `rediscache` usage

### Scope

The migration plan says:
> Remove `RedisCacheOptionsSetter` usages from `EmployeeNameCacheConfig`. Adapt the cache config to
> use Spring `RedisCacheConfiguration` directly (preparation for Step 16).
> **Do not remove** the `common-redis-cache-core` dependency from build files yet — that is Step 11.

### Approach

Replace `CacheConfigurationFromInfra` (which imports `basic-cache-config.xml`) with a local Java
`@Configuration` class that directly uses Spring Data Redis APIs. This eliminates the XML config
and the `RedisCacheOptionsSetter` middleman.

The infra's `basic-cache-config.xml` + `BasicCacheConfiguration` provides:
1. `JedisConnectionFactory` (standalone / sentinel / cluster based on profile)
2. `CacheFactory` (extends `RedisCacheManager`) with default TTL
3. `RedisCacheOptionsSetter` (helper to create caches with custom TTL)
4. `@Bean(name = "rediscache") RedisTemplate` (for direct Redis operations)
5. `@EnableCaching` + `NoOpCacheManager` fallback for non-caching profiles

### ⚠️ Must provide `@Bean(name = "rediscache") RedisTemplate`

`SparrtjanstStubStore` and `SamtyckestjanstStubStore` inject a `RedisTemplate` using both
`@Qualifier("rediscache")` and `@Resource(name = "rediscache")`. The local `BasicCacheConfig`
**must** provide this named bean or both stubs will fail to autowire at startup.

```java
@Bean(name = "rediscache")
RedisTemplate<Object, Object> redisTemplate() {
    RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(jedisConnectionFactory());
    redisTemplate.setKeySerializer(new StringRedisSerializer());
    return redisTemplate;
}
```

### Local replacement: `BasicCacheConfig.java`

Create `se.inera.intyg.rehabstod.config.BasicCacheConfig` that provides the same beans using
Spring Data Redis directly:

```java
@Configuration
@EnableCaching
public class BasicCacheConfig {

    @Configuration
    @Profile({"caching-enabled", "prod", "qa"})
    static class RedisCacheConfig {
        @Value("${redis.host}") String redisHost;
        @Value("${redis.port}") String redisPort;
        @Value("${redis.password}") String redisPassword;
        @Value("${redis.cache.default_entry_expiry_time_in_seconds}") long defaultEntryExpiry;
        @Value("${redis.sentinel.master.name}") String redisSentinelMasterName;
        @Value("${redis.read.timeout:PT1M}") String redisReadTimeout;
        @Value("${redis.cluster.nodes:}") String redisClusterNodes;
        @Value("${redis.cluster.password:}") String redisClusterPassword;
        @Value("${redis.cluster.max.redirects:3}") Integer redisClusterMaxRedirects;
        @Value("${redis.cluster.read.timeout:PT1M}") String redisClusterReadTimeout;
        @Value("${employee.name.cache.expiry}") long employeeNameCacheExpiry;

        @Resource private Environment environment;

        @Bean
        JedisConnectionFactory jedisConnectionFactory() {
            // Standalone / Sentinel / Cluster based on active profiles
            // Replicates infra BasicCacheConfiguration logic
        }

        @Bean
        public RedisCacheManager cacheManager() {
            RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(defaultEntryExpiry));

            Map<String, RedisCacheConfiguration> cacheConfigs = Map.of(
                "employeeName", RedisCacheConfiguration.defaultCacheConfig()
                    .entryTtl(Duration.ofSeconds(employeeNameCacheExpiry))
            );

            return RedisCacheManager.builder(
                    RedisCacheWriter.nonLockingRedisCacheWriter(jedisConnectionFactory()))
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigs)
                .build();
        }

        @Bean(name = "rediscache")
        RedisTemplate<Object, Object> redisTemplate() {
            RedisTemplate<Object, Object> template = new RedisTemplate<>();
            template.setConnectionFactory(jedisConnectionFactory());
            template.setKeySerializer(new StringRedisSerializer());
            return template;
        }
    }
}
```

### ⚠️ Profile mutual exclusivity for NoOpCacheManager

The original XML uses nested `<beans>` to express: `!caching-enabled AND !prod → NoOpCacheManager`.
Spring's `@Profile` annotation uses OR semantics, so `@Profile({"!caching-enabled", "!prod"})` means
`!caching-enabled OR !prod` — which is WRONG (it would activate in prod since prod is !caching-enabled).

**Solution:** Use a custom Spring `Condition` for the NoOp fallback:

```java
public class NoCachingCondition implements Condition {
    @Override
    public boolean matches(ConditionContext ctx, AnnotatedTypeMetadata metadata) {
        Environment env = ctx.getEnvironment();
        return !env.acceptsProfiles(Profiles.of("caching-enabled", "prod", "qa"));
    }
}

@Configuration
@Conditional(NoCachingCondition.class)
static class NoOpConfig {
    @Bean
    public CacheManager cacheManager() { return new NoOpCacheManager(); }
}
```

This ensures the NoOpCacheManager is ONLY created when none of the caching profiles are active.

### IA cache in BasicCacheConfig vs. IaCacheConfiguration

The `IaCacheConfiguration` creates two beans:
1. `Cache iaCache` — `"iaCache:rehabstod"` with TTL from `${intygsadmin.cache.expiry}` (86400s)
2. `RestTemplate iaRestTemplate` — plain `new RestTemplate()`

Since `IaCacheConfiguration` is profile-gated (`qa,prod,caching-enabled`), the `iaCache` bean
only exists when caching is enabled. When it's not, `IABannerServiceImpl` must handle a null cache
gracefully (it does — `queryCache()` returns empty list if cache returns null).

**Approach:** Keep `IaCacheConfiguration` as a separate local `@Configuration` class (it's
profile-gated via `IaConfiguration`). Modify it to use the `RedisCacheManager` directly:

```java
@Configuration
public class IaCacheConfiguration {
    public static final String CACHE_KEY = "BANNER";

    @Value("${app.name:noname}") private String appName;
    @Value("${intygsadmin.cache.expiry}") private String iaCacheExpirySeconds;
    @Autowired private RedisCacheManager cacheManager;

    @Bean
    public Cache iaCache() {
        // Create cache with custom TTL via the cache manager
        String cacheName = "iaCache:" + appName;
        // The cache manager's getCache() creates with default TTL;
        // For custom TTL, dynamically create via RedisCacheManager
        return ((RedisCacheManager) cacheManager).getCache(cacheName);
    }

    @Bean("iaRestTemplate")
    public RestTemplate restTemplate() { return new RestTemplate(); }
}
```

**Issue:** `RedisCacheManager.getCache()` uses the default TTL, not a custom one. To set a custom
TTL for the IA cache, add it to `BasicCacheConfig.cacheManager()` alongside "employeeName":

```java
Map<String, RedisCacheConfiguration> cacheConfigs = Map.of(
    "employeeName", RedisCacheConfiguration.defaultCacheConfig()
        .entryTtl(Duration.ofSeconds(employeeNameCacheExpiry)),
    "iaCache:rehabstod", RedisCacheConfiguration.defaultCacheConfig()
        .entryTtl(Duration.ofSeconds(iaCacheExpiry))
);
```

Then `IaCacheConfiguration.iaCache()` can simply call `cacheManager.getCache("iaCache:rehabstod")`
and get the pre-configured TTL.

### Files to change

| File | Change |
|---|---|
| `CacheConfigurationFromInfra.java` | **Delete** — replaced by `BasicCacheConfig` |
| `EmployeeNameCacheConfig.java` | **Delete** — cache TTL is now configured in `BasicCacheConfig` |
| `ApplicationInitializer.java` | Replace `CacheConfigurationFromInfra.class` with `BasicCacheConfig.class`. Remove `EmployeeNameCacheConfig.class` |
| `IaCacheConfiguration.java` (local copy) | Use `RedisCacheManager.getCache()` instead of `RedisCacheOptionsSetter` |

### Stub configurations (Samtyckestjanst, Sparrtjanst)

The stub configs in the `integration` modules also use `RedisCacheOptionsSetter`:
- `SparrtjanstStubConfiguration.java` — `redisCacheOptionsSetter.createCache("sparrtjanstStubCache", ...)`
- `SamtyckestjanstStubConfiguration.java` — `redisCacheOptionsSetter.createCache("samtyckestjanstStubCache", ...)`

**Recommended:** Update them to use `RedisCacheManager.getCache(name)` directly. Add the stub cache
names to `BasicCacheConfig` with their TTLs, or just use the default TTL (acceptable for stubs).
This keeps the codebase consistent and fully eliminates `RedisCacheOptionsSetter` usage.

---

## Sub-steps

### Sub-step 1 — Copy dynamiclink classes (5 files)

Copy from infra to `web/src/main/java/se/inera/intyg/rehabstod/dynamiclink/`.
Update package declarations: `se.inera.intyg.infra.dynamiclink` → `se.inera.intyg.rehabstod.dynamiclink`.
Update internal imports within the copied files.

### Sub-step 2 — Update dynamiclink references

- `InfraConfig.java` — change `@ComponentScan` to `se.inera.intyg.rehabstod.dynamiclink`
- `ConfigController.java` — update DynamicLink + DynamicLinkService imports
- `ConfigControllerTest.java` — update DynamicLinkService import

### Sub-step 3 — Copy and adapt ia-integration classes

Copy and modify IABannerService, IABannerServiceImpl, IaCacheConfiguration, IABannerServiceStub
into `web/src/main/java/se/inera/intyg/rehabstod/integration/ia/`.

Key modifications:
- All files: update package declarations
- IABannerService: return types use local `Banner` / `Application`
- IABannerServiceImpl: use local `Banner` / `Application`, inject `@Qualifier("iaRestTemplate")`
- IaCacheConfiguration: use `RedisCacheManager.getCache()` instead of `RedisCacheOptionsSetter`.
  Move the `iaCache:rehabstod` TTL configuration to `BasicCacheConfig`.

### Sub-step 4 — Convert IAStubRestApi to Spring MVC

Create `IAStubRestController.java` in `rehabstod.integration.ia.stub` as a `@RestController`
with `@Profile({"dev", "ia-stub"})` and `@RequestMapping("/api/ia-api")`:
- `GET /` → get banners from cache
- `PUT /banner` → add banner
- `DELETE /cache` → clear cache

### Sub-step 5 — Rewrite RSBannerJob

Inline `BannerJob` logic directly. Remove `extends BannerJob`. Switch to local `Application` type.
Remove `LogMDCHelper` dependency. Add `@Scheduled` + `@SchedulerLock` annotations.

### Sub-step 6 — Convert IaConfiguration / IaStubConfiguration to Java config

- `IaConfiguration.java` — remove `@ImportResource`, add `@Bean` methods with profile conditions
- `IaStubConfiguration.java` — remove `@ImportResource`, add
  `@ComponentScan("se.inera.intyg.rehabstod.integration.ia.stub")` for stub controller discovery

### Sub-step 7 — Switch remaining files to local Banner / Application types

- `GetConfigResponse.java` — change `Banner` import to local
- `ConfigController.java` — `IABannerService` import → local (already done in sub-step 3 imports)

### Sub-step 8 — Remove MonitoringConfiguration + replace LogbackConfiguratorContextListener

- `ApplicationConfig.java` — remove `@Import(MonitoringConfiguration.class)` + import statement
- `ApplicationInitializer.java`:
  - Remove `MonitoringConfiguration.class` from `appContext.register(...)` + import
  - Replace infra `LogbackConfiguratorContextListener` with local version (see Part B above)
- Create local `LogbackConfiguratorContextListener.java` in `se.inera.intyg.rehabstod.logging`
- **Verify:** Application starts with correct logging configuration in both dev and prod profiles

### Sub-step 9 — Replace cache infrastructure

- Create `BasicCacheConfig.java` — local Java `@Configuration` replacing `basic-cache-config.xml`
  - **Must include:** `@Bean(name = "rediscache") RedisTemplate` for stub store compatibility
  - **Must include:** `NoCachingCondition` for correct NoOp fallback profile logic
  - **Must include:** Pre-configured cache TTLs for `"employeeName"` and `"iaCache:rehabstod"`
- Delete `CacheConfigurationFromInfra.java`
- Delete `EmployeeNameCacheConfig.java` (cache configured centrally)
- Update `ApplicationInitializer.java` — replace config class registration
- Update stub configs (Sparrtjanst, Samtyckestjanst) to use `RedisCacheManager.getCache()` instead
  of `RedisCacheOptionsSetter`
- Update `IaCacheConfiguration` (local) to use `RedisCacheManager.getCache()`

### Sub-step 10 — (Optional) Fix ShedLock configuration

- Add `shedlock-spring` dependency to `web/build.gradle`
- Add `@EnableSchedulerLock(defaultLockAtMostFor = "PT10M")` to `JobConfig`
- Fix `RedisLockProvider` prefix: `"webcert"` → `"rehabstod"`

This is an optional improvement fixing a pre-existing issue. Can be deferred if out of scope.

### Sub-step 11 — Build and verify

Run: `.\gradlew.bat test --no-daemon`

Expected: BUILD SUCCESSFUL with all 613 tests passing (no regressions from Step 6).

**Additional verification needed (manual / profile-based):**
- `ia-stub` profile: verify stub REST API responds at `/api/ia-api` (now Spring MVC)
- `caching-enabled` profile: verify Redis cache operations work
- Default/test profile: verify `NoOpCacheManager` fallback works
- Verify Logback initialization works correctly after listener replacement

### Sub-step 12 — Create `step-7-summary.md`

Following the same pattern as previous summaries. Include:
- What was done, files created/modified/deleted
- Deferred items resolved (Step 2 Banner/Application, Step 3 MonitoringConfiguration)
- Difficulties or unexpected issues
- Updated list of remaining infra imports
- Final test results

### Sub-step 13 — Update `deferred-items.md`

- Mark Step 2 deferred items as resolved (Banner, Application)
- Mark Step 3 deferred item as resolved (MonitoringConfiguration)
- Add any new deferred items from this step (e.g., ShedLock fix if deferred)

---

## Complexity / Risk Notes

- **Dynamiclink: Very low risk.** Simple copy-paste with package rename. Self-contained classes
  with no infra cross-dependencies.

- **IA-integration: Medium risk.** The XML → Java config conversion changes how beans are created
  and profile-activated. Careful attention to profile logic is needed:
  - `ia-stub` profile → stub service (no HTTP calls)
  - `!ia-stub` → real service (HTTP calls to IA)
  - `qa,prod,caching-enabled` → cache enabled
  The profile conditions in the XML must be exactly replicated in Java `@Profile` annotations.

- **IABannerServiceImpl bean lifecycle:** The `@Bean` method approach (`return new IABannerServiceImpl()`)
  works because Spring processes `@Autowired` and `@Value` annotations on `@Bean`-returned objects.
  This is standard behavior, but worth a sanity check during implementation.

- **IAStubRestApi → Spring MVC: Low risk.** The stub is only used in dev/test profiles. JAX-RS →
  Spring MVC is straightforward for the 3 endpoints. However, verify that the CXF servlet
  no longer needs to handle `/api/ia-api` after the conversion.

- **BannerJob inlining: Low risk.** RSBannerJob is the only subclass. Removing `LogMDCHelper`
  slightly changes logging behavior (no automatic trace-id in scheduled job logs), but this is
  acceptable — proper scheduled-job MDC handling will be addressed in Step 14 (logging migration).

- **MonitoringConfiguration removal: Medium risk.** The `LogbackConfiguratorContextListener` must
  be replaced locally to preserve the `-Dlogback.file` mechanism used in production. A simple local
  listener handles this. Verify startup logging in both dev and prod profiles.

- **Cache replacement: Medium-high risk.** This is the riskiest part of Step 7:
  - Connection factory logic (standalone vs sentinel vs cluster) must exactly replicate infra
  - `@Bean(name = "rediscache") RedisTemplate` is required for stub stores
  - Profile conditions must be mutually exclusive (use `NoCachingCondition`)
  - Pre-configured cache TTLs must match current behavior (`"employeeName"` = 86400s,
    `"iaCache:rehabstod"` = 86400s, default = 86400s)

- **ShedLock: Low risk.** `@SchedulerLock` is currently a no-op (no interceptor configured).
  Adding `@EnableSchedulerLock` is an improvement but technically a behavior change (adding
  distributed locking). If uncertain, omit and defer to a later step.

- **Encoding**: Use Python for all file writes (per Step 5 lesson). Do NOT use PowerShell
  `Set-Content` or `[System.IO.File]::WriteAllText`.

---

## Files Changed Summary

| Change | Count |
|--------|-------|
| New main Java files created | ~12 (5 dynamiclink + 4 ia-integration + 1 stub controller + 1 cache config + 1 logback listener) |
| New main Java files created (util) | 1 (NoCachingCondition) |
| New test Java files created | 0 (no infra tests to copy) |
| Existing files rewritten | ~3 (RSBannerJob, IaConfiguration, IaStubConfiguration) |
| Existing files with import updates | ~5 (ConfigController, ConfigControllerTest, GetConfigResponse, ApplicationConfig, ApplicationInitializer) |
| Existing files deleted | ~2 (CacheConfigurationFromInfra, EmployeeNameCacheConfig) |
| Existing files updated (stubs) | 2 (SparrtjanstStubConfig, SamtyckestjanstStubConfig) |
| **Total files touched** | **~26** |