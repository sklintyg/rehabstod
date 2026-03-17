/*
 * Copyright (C) 2026 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.intyg.rehabstod.config;

import jakarta.annotation.Resource;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.StringUtils;
import se.inera.intyg.infra.rediscache.core.CacheFactory;
import se.inera.intyg.infra.rediscache.core.RedisCacheOptionsSetter;

/**
 * Local replacement for infra's basic-cache-config.xml + BasicCacheConfiguration. Provides
 * JedisConnectionFactory, RedisCacheManager, and RedisTemplate.
 */
@Configuration
@EnableCaching
public class BasicCacheConfig {

  @Configuration
  @Profile({"caching-enabled", "prod", "qa"})
  static class RedisCacheConfig {

    @Value("${redis.host}")
    String redisHost;

    @Value("${redis.port}")
    String redisPort;

    @Value("${redis.password}")
    String redisPassword;

    @Value("${redis.cache.default_entry_expiry_time_in_seconds}")
    long defaultEntryExpiry;

    @Value("${redis.sentinel.master.name}")
    String redisSentinelMasterName;

    @Value("${redis.read.timeout:PT1M}")
    String redisReadTimeout;

    @Value("${redis.cluster.nodes:}")
    String redisClusterNodes;

    @Value("${redis.cluster.password:}")
    String redisClusterPassword;

    @Value("${redis.cluster.max.redirects:3}")
    Integer redisClusterMaxRedirects;

    @Value("${redis.cluster.read.timeout:PT1M}")
    String redisClusterReadTimeout;

    @Value("${employee.name.cache.expiry}")
    long employeeNameCacheExpiry;

    @Value("${intygsadmin.cache.expiry}")
    long iaCacheExpiry;

    @Value("${app.name:noname}")
    String appName;

    @Resource private Environment environment;

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
      final var activeProfiles = List.of(environment.getActiveProfiles());
      if (activeProfiles.contains("redis-cluster")) {
        return clusterConnectionFactory();
      }
      if (activeProfiles.contains("redis-sentinel")) {
        return sentinelConnectionFactory();
      }
      return standAloneConnectionFactory();
    }

    @Bean
    @DependsOn("cacheManager")
    public RedisCacheOptionsSetter redisCacheOptionsSetter() {
      return new RedisCacheOptionsSetter();
    }

    @Bean
    @DependsOn("redisCacheOptionsSetter")
    public Cache iaCache() {
      return redisCacheOptionsSetter()
          .createCache("iaCache:" + appName, String.valueOf(Duration.ofSeconds(iaCacheExpiry)));
    }

    @Bean
    @DependsOn("redisCacheOptionsSetter")
    public Cache employeeCache() {
      return redisCacheOptionsSetter()
          .createCache("employeeName", String.valueOf(Duration.ofSeconds(employeeNameCacheExpiry)));
    }

    @Bean
    public RedisCacheManager cacheManager() {
      return new CacheFactory(
          RedisCacheWriter.nonLockingRedisCacheWriter(jedisConnectionFactory()),
          RedisCacheConfiguration.defaultCacheConfig()
              .entryTtl(Duration.ofSeconds(defaultEntryExpiry)));
    }

    @Bean(name = "rediscache")
    RedisTemplate<Object, Object> redisTemplate() {
      RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
      redisTemplate.setConnectionFactory(jedisConnectionFactory());
      redisTemplate.setKeySerializer(new StringRedisSerializer());
      return redisTemplate;
    }

    private JedisConnectionFactory standAloneConnectionFactory() {
      RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
      config.setHostName(redisHost);
      config.setPort(Integer.parseInt(redisPort));
      if (StringUtils.hasLength(redisPassword)) {
        config.setPassword(redisPassword);
      }
      return new JedisConnectionFactory(
          config, JedisClientConfiguration.builder().usePooling().build());
    }

    private JedisConnectionFactory sentinelConnectionFactory() {
      RedisSentinelConfiguration sentinelConfig =
          new RedisSentinelConfiguration().master(redisSentinelMasterName);
      sentinelConfig.setPassword(redisPassword);
      sentinelConfig.setSentinelPassword(redisPassword);

      if (!StringUtils.hasLength(redisHost) || !StringUtils.hasLength(redisPort)) {
        throw new IllegalStateException(
            "Cannot bootstrap RedisSentinelConfiguration, redis.host or redis.port is null or empty");
      }
      final var hosts = parseConnectionString(redisHost);
      final var ports = parseConnectionString(redisPort);

      if (hosts.isEmpty() || ports.isEmpty() || hosts.size() != ports.size()) {
        throw new IllegalStateException(
            "Cannot bootstrap RedisSentinelConfiguration, number of redis.host and/or redis.port was zero or not equal.");
      }

      for (int a = 0; a < hosts.size(); a++) {
        sentinelConfig = sentinelConfig.sentinel(hosts.get(a), Integer.parseInt(ports.get(a)));
      }

      final var clientConfig =
          JedisClientConfiguration.builder().readTimeout(Duration.parse(redisReadTimeout)).build();

      return new JedisConnectionFactory(sentinelConfig, clientConfig);
    }

    private JedisConnectionFactory clusterConnectionFactory() {
      final var clusterConfig =
          new RedisClusterConfiguration(parseConnectionString(redisClusterNodes));
      clusterConfig.setMaxRedirects(redisClusterMaxRedirects);
      clusterConfig.setPassword(redisClusterPassword);

      final var clientConfig =
          JedisClientConfiguration.builder()
              .readTimeout(Duration.parse(redisClusterReadTimeout))
              .build();

      return new JedisConnectionFactory(clusterConfig, clientConfig);
    }

    private static List<String> parseConnectionString(String value) {
      return Arrays.stream(value.split(";")).map(String::trim).filter(s -> !s.isEmpty()).toList();
    }
  }

  @Configuration
  @Conditional(NoCachingCondition.class)
  static class NoOpConfig {
    @Bean
    public CacheManager cacheManager() {
      return new NoOpCacheManager();
    }
  }
}
