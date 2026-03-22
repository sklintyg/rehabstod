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

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import se.inera.intyg.rehabstod.integration.ia.constants.IaCacheConstants;
import se.inera.intyg.rehabstod.integration.intygproxyservice.constants.HsaIntygProxyServiceConstants;
import se.inera.intyg.rehabstod.service.hsa.EmployeeNameServiceImpl;

@Configuration
@EnableCaching
public class RedisConfig {

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
          .withCacheConfiguration(
              IaCacheConstants.IA_CACHE_NAME,
              defaultConfig.entryTtl(Duration.ofSeconds(iaCacheExpiry)))
          .withCacheConfiguration(
              EmployeeNameServiceImpl.EMPLOYEE_NAME_CACHE_NAME,
              defaultConfig.entryTtl(Duration.ofSeconds(employeeNameCacheExpiry)))
          .withCacheConfiguration(
              HsaIntygProxyServiceConstants.EMPLOYEE_CACHE_NAME,
              defaultConfig.entryTtl(Duration.ofSeconds(hsaEmployeeCacheExpiry)))
          .withCacheConfiguration(
              HsaIntygProxyServiceConstants.HEALTH_CARE_UNIT_CACHE_NAME,
              defaultConfig.entryTtl(Duration.ofSeconds(hsaHealthCareUnitCacheExpiry)))
          .withCacheConfiguration(
              HsaIntygProxyServiceConstants.HEALTH_CARE_UNIT_MEMBERS_CACHE_NAME,
              defaultConfig.entryTtl(Duration.ofSeconds(hsaHealthCareUnitMembersCacheExpiry)))
          .withCacheConfiguration(
              HsaIntygProxyServiceConstants.UNIT_CACHE_NAME,
              defaultConfig.entryTtl(Duration.ofSeconds(hsaUnitCacheExpiry)))
          .withCacheConfiguration(
              HsaIntygProxyServiceConstants.HEALTH_CARE_PROVIDER_CACHE_NAME,
              defaultConfig.entryTtl(Duration.ofSeconds(hsaHealthCareProviderCacheExpiry)))
          .build();
    }

    @Bean(name = "rediscache")
    public RedisTemplate<Object, Object> redisTemplate(
        RedisConnectionFactory redisConnectionFactory) {
      RedisTemplate<Object, Object> template = new RedisTemplate<>();
      template.setConnectionFactory(redisConnectionFactory);
      template.setKeySerializer(new StringRedisSerializer());
      return template;
    }
  }
}
