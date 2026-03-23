# Steps 14–17 — Spring Boot Auto-Configuration

## Progress Tracker

| Sub-step | Description | Status |
|----------|-------------|--------|
| **14.1** | Migrate `db.*` and `hibernate.*` properties to `spring.datasource.*` / `spring.jpa.*` | ✅ DONE |
| **14.2** | Replace manual `DataSource`, `EntityManagerFactory`, `TransactionManager` beans with Spring Boot auto-config | ✅ DONE |
| **14.3** | Let Spring Boot auto-configure Liquibase via `spring.liquibase.*` | ✅ DONE |
| **14.4** | Clean up `PersistenceConfigBase` and `PersistenceConfig` | ✅ DONE |
| **14.5** | Remove JPA exclusions from `RehabstodApplication` | ✅ DONE |
| **14.6** | Clean up `persistence/build.gradle` | ✅ DONE |
| **14.7** | Verify JPA step | ✅ DONE |
| **15.1** | Migrate `activemq.*` properties to `spring.activemq.*` | ✅ DONE |
| **15.2** | Remove manual `ConnectionFactory` and `JmsTransactionManager` from `JmsConfig` | ✅ DONE |
| **15.3** | Remove JMS exclusions from `RehabstodApplication` | ✅ DONE |
| **15.4** | Clean up `web/build.gradle` JMS deps | ✅ DONE |
| **15.5** | Verify JMS step | ✅ DONE |
| **16.1** | Add `spring-boot-starter-actuator` to `web/build.gradle`; remove `simpleclient_servlet_jakarta` | ✅ DONE |
| **16.2** | Configure Actuator endpoints in `application.properties` | ✅ DONE |
| **16.3** | Remove manual `MetricsServlet` bean and `simpleclient_servlet_jakarta` dependency | ✅ DONE |
| **16.4** | Verify Actuator step | ✅ DONE |
| **17.1** | Fix ShedLock prefix: `"webcert"` → `"rehabstod"` in `JobConfig.java` | ✅ DONE |
| **17.2** | Remove dead `"se.inera.intyg.rehabstod.rediscache"` from `ApplicationConfig` `@ComponentScan` | ✅ DONE |
| **17.3** | Migrate `redis.*` properties to `spring.data.redis.*` in `application.properties` | ✅ DONE |
| **17.4** | Remove manual `JedisConnectionFactory` bean; let Spring Boot auto-configure it | ✅ DONE |
| **17.5** | Update `RedisCacheConfig` to inject auto-configured `RedisConnectionFactory` | ✅ DONE |
| **17.6** | Remove Redis exclusions from `RehabstodApplication` | ✅ DONE |
| **17.7** | Verify Redis step | ✅ DONE |

---

## Current State (after Step 13)

The app runs as a Spring Boot JAR with embedded Tomcat. All filter registrations, CXF servlet,
and logging are done. The following manual configurations are still active:

- **JPA:** `PersistenceConfigBase` manually creates `DataSource` (HikariCP), `EntityManagerFactory`,
  `JpaTransactionManager`, and `SpringLiquibase`. Properties use `db.*` and `hibernate.*` namespace.
- **JMS:** `JmsConfig` manually creates `ActiveMQConnectionFactory`, `JmsTransactionManager`, and two
  `JmsTemplate` beans. Properties use `activemq.*` namespace.
- **Metrics:** `ApplicationConfig` registers a raw Prometheus `MetricsServlet` at `/metrics`.
  No Actuator is configured.
- **Redis:** `RedisConfig` manually creates a `JedisConnectionFactory` (with standalone/sentinel/cluster
  topology switching via Spring profiles), `RedisCacheManager`, and `RedisTemplate`. Properties use
  `redis.*` namespace. ShedLock in `JobConfig` uses prefix `"webcert"` (bug — should be `"rehabstod"`).
- **Auto-config exclusions:** `RehabstodApplication` explicitly excludes:
  `DataSourceAutoConfiguration`, `HibernateJpaAutoConfiguration`, `JpaRepositoriesAutoConfiguration`,
  `JmsAutoConfiguration`, `ActiveMQAutoConfiguration`, `RedisAutoConfiguration`,
  `RedisRepositoriesAutoConfiguration`.

---

## Step 14 — JPA Auto-configuration

### What was already done
Nothing — JPA is fully manual.

### What to do

#### 14.1 — Migrate `db.*` and `hibernate.*` properties

**Files:** `web/src/main/resources/application.properties`,
`devops/dev/config/application-dev.properties`

**`application.properties`** — replace the following block:

```properties
# Before (remove)
hibernate.dialect=org.hibernate.dialect.MySQLDialect
hibernate.hbm2ddl.auto=none
hibernate.ejb.naming_strategy=org.hibernate.cfg.DefaultNamingStrategy
hibernate.show_sql=false
hibernate.format_sql=true
hibernate.id.new_generator_mappings=false
db.driver=com.mysql.cj.jdbc.Driver
db.url=jdbc:mysql://${db.server}:${db.port}/${db.name}?useSSL=false&serverTimezone=Europe/Stockholm&allowPublicKeyRetrieval=true
db.username=
db.password=
db.server=localhost
db.port=3306
db.name=rehabstod
db.pool.maxSize=20

# After (add)
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/rehabstod?useSSL=false&serverTimezone=Europe/Stockholm&allowPublicKeyRetrieval=true
spring.datasource.username=
spring.datasource.password=
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=3
spring.datasource.hikari.connection-timeout=3000
spring.datasource.hikari.idle-timeout=15000
spring.datasource.hikari.auto-commit=false
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.id.new_generator_mappings=false
spring.jpa.properties.hibernate.enable_lazy_load_no_trans=false
spring.liquibase.change-log=classpath:changelog/changelog.xml
```

> **Note:** `hibernate.ejb.naming_strategy` is a Hibernate 4/5 property that is not valid in Hibernate 6.
> The Spring Boot default physical naming strategy is equivalent. Do not carry it forward.

**`application-dev.properties`** — replace existing db overrides:

```properties
# Before (remove)
db.server=localhost
db.port=3306
db.name=rehabstod
db.username=rehabstod
db.password=rehabstod
db.pool.maxSize=5

# After (add)
spring.datasource.url=jdbc:mysql://localhost:3306/rehabstod?useSSL=false&serverTimezone=Europe/Stockholm&allowPublicKeyRetrieval=true
spring.datasource.username=rehabstod
spring.datasource.password=rehabstod
spring.datasource.hikari.maximum-pool-size=5
```

#### 14.2 — Remove manual beans from `PersistenceConfigBase`

**File:** `persistence/src/main/java/.../persistence/config/PersistenceConfigBase.java`

Remove ALL `@Value` fields and ALL `@Bean` methods. The class becomes empty and can be deleted.
Spring Boot auto-configuration now handles `DataSource`, `EntityManagerFactory`,
`JpaTransactionManager`, and `SpringLiquibase`.

The HikariCP `minIdle`, `connectionTimeout`, and `idleTimeout` settings that were hardcoded
in `standaloneDataSource()` must be moved to `spring.datasource.hikari.*` properties (done in 14.1).

#### 14.3 — Let Spring Boot auto-configure Liquibase

The `spring.liquibase.change-log` property added in 14.1 is sufficient. Spring Boot's
`LiquibaseAutoConfiguration` will pick it up. The manual `@Bean(name = "dbUpdate") SpringLiquibase`
method is deleted as part of removing `PersistenceConfigBase`.

> **Note:** If other code references the `"dbUpdate"` bean name via `@Qualifier("dbUpdate")`, check
> first — but there are no known usages in the codebase.

#### 14.4 — Clean up `PersistenceConfigBase` and `PersistenceConfig`

**Files:**
- `persistence/src/main/java/.../persistence/config/PersistenceConfigBase.java` — **delete**
- `persistence/src/main/java/.../persistence/config/PersistenceConfig.java` — simplify or delete

`PersistenceConfig` only contributes `@EnableJpaRepositories` and `@ComponentScan`. Since
Spring Boot auto-detects repositories under `@SpringBootApplication`'s component scan base package,
`@EnableJpaRepositories` is no longer needed in a separate file.

However, `@ComponentScan(BASE_PACKAGES)` in `PersistenceConfig` is what makes the persistence
module's `@Repository` beans visible. With Spring Boot's `@SpringBootApplication`, the scan
starts from `se.inera.intyg.rehabstod` — but `persistence` classes are in
`se.inera.intyg.rehabstod.persistence`. Since that is a sub-package, they are automatically
included in the component scan. Verify this is the case, then delete `PersistenceConfig`.

If `PersistenceConfigDev` (in `src/test/java`, profile `h2`) is still used for tests: keep it or
migrate to Spring Boot test slices.

#### 14.5 — Remove JPA exclusions from `RehabstodApplication`

**File:** `web/src/main/java/.../RehabstodApplication.java`

Remove from the `exclude` array:
- `DataSourceAutoConfiguration.class`
- `HibernateJpaAutoConfiguration.class`
- `JpaRepositoriesAutoConfiguration.class`

#### 14.6 — Clean up `persistence/build.gradle`

Remove:
```groovy
implementation "com.zaxxer:HikariCP"          // provided by spring-boot-starter-data-jpa
implementation "org.hibernate.orm:hibernate-hikaricp"  // provided by spring-boot-starter-data-jpa
```

Add to `web/build.gradle` (if not already pulled transitively):
```groovy
implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
```

> Spring Boot's `spring-boot-starter-data-jpa` includes HikariCP, Hibernate, and Spring Data JPA.
> Since `web` declares `spring-data-jpa` explicitly today, switch to the full starter.
> Remove `implementation "org.springframework.data:spring-data-jpa"` from `web/build.gradle`
> once the starter is added.

#### 14.7 — Verify JPA step

```
./gradlew :rehabstod-web:spotlessApply
./gradlew build
```

Expected: BUILD SUCCESSFUL. Database connection established on startup. Liquibase changelog runs.

---

## Step 15 — JMS Auto-configuration

### What was already done
Nothing — JMS is fully manual.

### What to do

#### 15.1 — Migrate `activemq.*` properties

**Files:** `web/src/main/resources/application.properties`,
`devops/dev/config/application-dev.properties`

**`application.properties`** — replace:

```properties
# Before (remove)
activemq.broker.url=vm://localhost?broker.persistent=false
activemq.broker.username=
activemq.broker.password=

# After (add)
spring.activemq.broker-url=vm://localhost?broker.persistent=false
spring.activemq.user=
spring.activemq.password=
```

Keep `pdl.logging.queue.name=dev.logging.queue` — it is a custom property used by `jmsPDLLogTemplate`.

**`application-dev.properties`** — replace:

```properties
# Before (remove)
activemq.broker.url=tcp://localhost:61616?jms.nonBlockingRedelivery=true...
activemq.broker.username=activemqUser
activemq.broker.password=activemqPassword

# After (add)
spring.activemq.broker-url=tcp://localhost:61616?jms.nonBlockingRedelivery=true\
     &jms.redeliveryPolicy.maximumRedeliveries=3\
     &jms.redeliveryPolicy.maximumRedeliveryDelay=6000\
     &jms.redeliveryPolicy.initialRedeliveryDelay=4000\
     &jms.redeliveryPolicy.useExponentialBackOff=true\
     &jms.redeliveryPolicy.backOffMultiplier=2
spring.activemq.user=activemqUser
spring.activemq.password=activemqPassword
```

#### 15.2 — Remove manual beans from `JmsConfig`

**File:** `web/src/main/java/.../config/JmsConfig.java`

Remove:
- `@Value("${activemq.broker.url}")` field and the two credential fields
- `connectionFactory()` bean — Spring Boot auto-configures this from `spring.activemq.*`
- `jmsTransactionManager()` bean — Spring Boot auto-configures this
- `jmsTemplate(ConnectionFactory)` bean — Spring Boot auto-configures this

**Keep:**
- `@Value("${pdl.logging.queue.name}")` field
- `jmsPDLLogTemplate()` bean — it has custom destination + `sessionTransacted=true` settings
  that go beyond Spring Boot defaults. Update it to inject `ConnectionFactory` via constructor
  or `@Autowired` instead of calling `connectionFactory()` directly.

After cleanup, `JmsConfig` looks like:

```java
@Configuration
public class JmsConfig {

  @Autowired private ConnectionFactory connectionFactory;

  @Value("${pdl.logging.queue.name}")
  private String loggingQueueName;

  @Bean
  public JmsTemplate jmsPDLLogTemplate() {
    JmsTemplate jmsTemplate = new JmsTemplate();
    jmsTemplate.setDefaultDestinationName(loggingQueueName);
    jmsTemplate.setConnectionFactory(connectionFactory);
    jmsTemplate.setSessionTransacted(true);
    return jmsTemplate;
  }
}
```

#### 15.3 — Remove JMS exclusions from `RehabstodApplication`

Remove from `exclude` array:
- `JmsAutoConfiguration.class`
- `ActiveMQAutoConfiguration.class`

#### 15.4 — Clean up `web/build.gradle` JMS deps

Replace:
```groovy
// Before (remove)
implementation "org.apache.activemq:activemq-spring"
implementation "org.springframework:spring-jms"

// After (add)
implementation 'org.springframework.boot:spring-boot-starter-activemq'
```

#### 15.5 — Verify JMS step

```
./gradlew :rehabstod-web:spotlessApply
./gradlew build
```

Expected: BUILD SUCCESSFUL. On startup, JMS connection established. PDL log queue available.

---

## Step 16 — Actuator + Micrometer

### What was already done
Nothing. The app serves Prometheus metrics via a raw `MetricsServlet` at `/metrics`.
`@PrometheusTimeMethod` was previously deleted (dead code cleanup — no usages remained).

### What to do

#### 16.1 — Add Actuator dependency

**File:** `web/build.gradle`

Add:
```groovy
implementation 'org.springframework.boot:spring-boot-starter-actuator'
```

Remove:
```groovy
implementation "io.prometheus:simpleclient_servlet_jakarta"
```

> **Note:** `micrometer-registry-prometheus` is intentionally omitted — not needed for this project.

#### 16.2 — Configure Actuator endpoints

**File:** `web/src/main/resources/application.properties`

Add:
```properties
management.endpoints.web.exposure.include=health,info,prometheus
management.endpoint.health.show-details=never
```

#### 16.3 — Remove manual `MetricsServlet` from `ApplicationConfig`

**File:** `web/src/main/java/.../config/ApplicationConfig.java`

Remove the `metricsServletRegistration()` `@Bean` method entirely:

```java
// Remove this entire method:
@Bean
public ServletRegistrationBean<io.prometheus.client.servlet.jakarta.exporter.MetricsServlet>
    metricsServletRegistration() {
  return new ServletRegistrationBean<>(
      new io.prometheus.client.servlet.jakarta.exporter.MetricsServlet(), "/metrics");
}
```

Also remove the unused import for `io.prometheus.client.servlet.jakarta.exporter.MetricsServlet`.

> **Note on endpoint URL change:** The metrics endpoint moves from `/metrics` to
> `/actuator/prometheus`. If any external Prometheus scrape config targets `/metrics`,
> it must be updated. The health endpoint is at `/actuator/health`.

#### 16.4 — Verify Actuator step

```
./gradlew :rehabstod-web:spotlessApply
./gradlew build
./gradlew :rehabstod-web:bootRun
```

Smoke test:
- `GET /actuator/health` → `{"status":"UP"}`
- `GET /actuator/prometheus` → Prometheus metrics text format

---

## Step 17 — Redis Auto-configuration + ShedLock prefix fix

### What was already done
- `redis-cache` module fully deleted ✅
- `RedisConfig.java` already uses `RedisCacheManager.builder()` (no `CacheFactory`) ✅

### What to do

#### 17.1 — Fix ShedLock prefix (bug fix)

**File:** `web/src/main/java/.../config/JobConfig.java`

```java
// Before:
return new RedisLockProvider(jedisConnectionFactory, "webcert");

// After:
return new RedisLockProvider(jedisConnectionFactory, "rehabstod");
```

> **Why:** ShedLock stores lock keys in Redis under `<prefix>:<lockName>`. The prefix `"webcert"`
> is a copy-paste error from another service. Using `"rehabstod"` avoids accidental shared-lock
> collisions when both services use the same Redis instance.

#### 17.2 — Remove dead `rediscache` ComponentScan entry

**File:** `web/src/main/java/.../config/ApplicationConfig.java`

Remove `"se.inera.intyg.rehabstod.rediscache"` from the `@ComponentScan` list. The `redis-cache`
module (which provided this package) was deleted in a prior step.

```java
// Before:
@ComponentScan({
  "se.inera.intyg.rehabstod.logging",
  "se.inera.intyg.rehabstod.integration.it",
  "se.inera.intyg.rehabstod.integration.wc",
  "se.inera.intyg.rehabstod.sjukfall",
  "se.inera.intyg.rehabstod.pu.integration.api",
  "se.inera.intyg.rehabstod.integration.hsatk",
  "se.inera.intyg.rehabstod.integration.intygproxyservice",
  "se.inera.intyg.rehabstod.pu.integration.intygproxyservice",
  "se.inera.intyg.rehabstod.rediscache",      // ← remove this
  "se.inera.intyg.rehabstod.dynamiclink"
})
```

#### 17.3 — Migrate `redis.*` properties to `spring.data.redis.*`

**File:** `web/src/main/resources/application.properties`

Replace the Redis block:

```properties
# Before (remove)
redis.host=127.0.0.1
redis.port=6379
redis.password=
redis.cache.default_entry_expiry_time_in_seconds=86400
redis.sentinel.master.name=master

# After (add)
spring.data.redis.host=127.0.0.1
spring.data.redis.port=6379
spring.data.redis.password=
spring.data.redis.timeout=60s
redis.cache.default_entry_expiry_time_in_seconds=86400
```

> **Sentinel/cluster properties** (`redis.sentinel.*`, `redis.cluster.*`) are not in
> `application.properties` — they are set per-environment by Kubernetes. The ops team should
> rename them to `spring.data.redis.sentinel.*` and `spring.data.redis.cluster.*` in the
> cluster configuration.

**`application-dev.properties`** — update Redis password override:
```properties
# Before (remove)
redis.password=redis

# After (add)
spring.data.redis.password=redis
```

#### 17.4 — Remove manual `JedisConnectionFactory` bean

**File:** `web/src/main/java/.../config/RedisConfig.java`

The current `RedisCacheConfig` inner class creates a `JedisConnectionFactory` with profile-based
topology switching (standalone/sentinel/cluster). Spring Boot's auto-configuration handles all
three topologies via `spring.data.redis.*` properties and also supports Jedis when Jedis is on
the classpath.

**Remove from `RedisCacheConfig`:**
- All `@Value` fields for `redisHost`, `redisPort`, `redisPassword`, `redisSentinelMasterName`,
  `redisReadTimeout`, `redisClusterNodes`, `redisClusterPassword`, `redisClusterMaxRedirects`,
  `redisClusterReadTimeout`
- The `@Resource Environment environment` field
- The `jedisConnectionFactory()` `@Bean` method
- The private helper methods: `standAloneConnectionFactory()`, `sentinelConnectionFactory()`,
  `clusterConnectionFactory()`, `parseConnectionString()`
- The `redisTemplate()` `@Bean` method (Spring Boot auto-configures `RedisTemplate` and
  `StringRedisTemplate`)

#### 17.5 — Update `RedisCacheConfig` to inject auto-configured `RedisConnectionFactory`

After removing the manual factory, `cacheManager()` must consume the auto-configured
`RedisConnectionFactory`:

```java
@Configuration
static class RedisCacheConfig {

  @Value("${redis.cache.default_entry_expiry_time_in_seconds}")
  long defaultEntryExpiry;

  @Value("${employee.name.cache.expiry}")
  long employeeNameCacheExpiry;

  @Value("${intygsadmin.cache.expiry}")
  long iaCacheExpiry;

  @Value("${hsa.intygproxyservice.getemployee.cache.expiry:60}")
  long hsaEmployeeCacheExpiry;

  @Value("${hsa.intygproxyservice.gethealthcareunit.cache.expiry:60}")
  long hsaHealthCareUnitCacheExpiry;

  @Value("${hsa.intygproxyservice.gethealthcareunitmembers.cache.expiry:60}")
  long hsaHealthCareUnitMembersCacheExpiry;

  @Value("${hsa.intygproxyservice.getunit.cache.expiry:60}")
  long hsaUnitCacheExpiry;

  @Value("${hsa.intygproxyservice.gethealthcareprovider.cache.expiry:60}")
  long hsaHealthCareProviderCacheExpiry;

  @Bean
  public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
    final var defaultConfig =
        RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofSeconds(defaultEntryExpiry));
    return RedisCacheManager.builder(redisConnectionFactory)
        .cacheDefaults(defaultConfig)
        .withCacheConfiguration(IaCacheConstants.IA_CACHE_NAME,
            defaultConfig.entryTtl(Duration.ofSeconds(iaCacheExpiry)))
        .withCacheConfiguration(EmployeeNameServiceImpl.EMPLOYEE_NAME_CACHE_NAME,
            defaultConfig.entryTtl(Duration.ofSeconds(employeeNameCacheExpiry)))
        .withCacheConfiguration(HsaIntygProxyServiceConstants.EMPLOYEE_CACHE_NAME,
            defaultConfig.entryTtl(Duration.ofSeconds(hsaEmployeeCacheExpiry)))
        .withCacheConfiguration(HsaIntygProxyServiceConstants.HEALTH_CARE_UNIT_CACHE_NAME,
            defaultConfig.entryTtl(Duration.ofSeconds(hsaHealthCareUnitCacheExpiry)))
        .withCacheConfiguration(HsaIntygProxyServiceConstants.HEALTH_CARE_UNIT_MEMBERS_CACHE_NAME,
            defaultConfig.entryTtl(Duration.ofSeconds(hsaHealthCareUnitMembersCacheExpiry)))
        .withCacheConfiguration(HsaIntygProxyServiceConstants.UNIT_CACHE_NAME,
            defaultConfig.entryTtl(Duration.ofSeconds(hsaUnitCacheExpiry)))
        .withCacheConfiguration(HsaIntygProxyServiceConstants.HEALTH_CARE_PROVIDER_CACHE_NAME,
            defaultConfig.entryTtl(Duration.ofSeconds(hsaHealthCareProviderCacheExpiry)))
        .build();
  }
}
```

Also update `JobConfig.java` — replace `@Autowired JedisConnectionFactory` with
`@Autowired RedisConnectionFactory`:

```java
// Before:
@Autowired private JedisConnectionFactory jedisConnectionFactory;

@Bean
public LockProvider lockProvider() {
  return new RedisLockProvider(jedisConnectionFactory, "rehabstod");
}

// After:
@Autowired private RedisConnectionFactory redisConnectionFactory;

@Bean
public LockProvider lockProvider() {
  return new RedisLockProvider(redisConnectionFactory, "rehabstod");
}
```

#### 17.6 — Remove Redis exclusions from `RehabstodApplication`

Remove from `exclude` array:
- `RedisAutoConfiguration.class`
- `RedisRepositoriesAutoConfiguration.class`

> **Note:** `RedisRepositoriesAutoConfiguration` can remain excluded — rehabstod does not use
> Spring Data Redis repositories (`@Repository` annotated with Redis operations). Keeping it
> excluded prevents accidental scanning of the `persistence` JPA repositories as Redis repos.

After this step `RehabstodApplication` should have an empty (or no) `exclude` array.

#### 17.7 — Verify Redis step

```
./gradlew :rehabstod-web:spotlessApply
./gradlew build
./gradlew :rehabstod-web:bootRun
```

Expected:
- Application starts without `ConnectionRefusedException` (requires local Redis on 6379)
- Cache is populated on first HSA/IA call
- ShedLock creates keys prefixed `rehabstod:` in Redis (verify with `redis-cli keys "rehabstod:*"`)

---

## Notes

### Step 18 (Dockerfile) — Already handled
The Dockerfile was already updated as part of Step 13. The JAR is built as `app.jar` and
placed in `/deployments/`. No further changes needed.

### `application.dir` default value
The property `application.dir` is only set via `bootRun`'s `systemProperty(...)` in
`web/build.gradle` for local development. In containerised environments it is never set,
causing startup failure when properties like `features.configuration.file` reference it.

Add the following default to `application.properties`:
```properties
application.dir=/deployments
```

This is safe: `bootRun` overrides it locally, and `/deployments` is the correct container path.
