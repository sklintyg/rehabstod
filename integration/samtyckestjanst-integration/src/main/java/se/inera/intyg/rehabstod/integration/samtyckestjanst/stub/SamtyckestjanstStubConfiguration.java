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
package se.inera.intyg.rehabstod.integration.samtyckestjanst.stub;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;

import com.fasterxml.jackson.databind.ObjectMapper;
import se.inera.intyg.infra.rediscache.core.RedisCacheOptionsSetter;

@Configuration
@ComponentScan("se.inera.intyg.rehabstod.integration.samtyckestjanst.stub")
@ImportResource("classpath:samtyckestjanst-stub-context.xml")
@Profile("rhs-samtyckestjanst-stub")
public class SamtyckestjanstStubConfiguration {

    public static final String CACHE_NAME = "samtyckestjanstStubCache";

    private String cacheExpirySeconds = "3600";

    @Autowired
    private RedisCacheOptionsSetter redisCacheOptionsSetter;

    @PostConstruct
    public void init() {
        redisCacheOptionsSetter.createCache(CACHE_NAME, cacheExpirySeconds);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

}
