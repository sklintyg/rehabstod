/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import se.inera.intyg.infra.rediscache.core.RedisCacheOptionsSetter;

import javax.annotation.PostConstruct;

/**
 * Created by eriklupander on 2017-02-23.
 */
@Configuration
@Profile("caching-enabled")
public class EmployeeNameCacheConfig {
    public static final String EMPLOYEE_NAME_CACHE_NAME = "employeeName";
    private static final String EMPLOYEE_NAME_CACHE_EXPIRY = "employee.name.cache.expiry";

    @Value("${" + EMPLOYEE_NAME_CACHE_EXPIRY + "}")
    private String employeeNameCacheExpirySeconds;

    @Autowired
    private RedisCacheOptionsSetter redisCacheOptionsSetter;

    @PostConstruct
    public void init() {
        redisCacheOptionsSetter.createCache(EMPLOYEE_NAME_CACHE_NAME, EMPLOYEE_NAME_CACHE_EXPIRY);
    }
}
