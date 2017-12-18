/*
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ResourceBundleMessageSource;
import se.inera.intyg.infra.cache.metrics.CacheStatisticsService;
import se.inera.intyg.infra.cache.metrics.CacheStatisticsServiceImpl;
import se.inera.intyg.rehabstod.service.diagnos.DiagnosFactory;
import se.inera.intyg.rehabstod.web.filters.PdlConsentGivenAssuranceFilter;
import se.inera.intyg.rehabstod.web.filters.UnitSelectedAssuranceFilter;

@Configuration
@PropertySource({ "file:${rehabstod.config.file}", "file:${credentials.file}", "classpath:version.properties" })
@ImportResource({ "classpath:META-INF/cxf/cxf.xml", "classpath:securityContext.xml" })
public class ApplicationConfig {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public CacheStatisticsService cacheStatisticsService() {
        return new CacheStatisticsServiceImpl();
    }

    @Bean
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        source.setBasename("version");
        source.setUseCodeAsDefaultMessage(true);
        return source;
    }

    @Bean
    public UnitSelectedAssuranceFilter unitSelectedAssuranceFilter() {
        return new UnitSelectedAssuranceFilter();
    }

    @Bean
    public PdlConsentGivenAssuranceFilter pdlConsentGivenAssuranceFilter() {
        return new PdlConsentGivenAssuranceFilter();
    }

    @Bean(name = "jacksonJsonProvider")
    public JacksonJaxbJsonProvider jacksonJsonProvider() {
        return new JacksonJaxbJsonProvider();
    }

    @Bean
    public DiagnosFactory diagnosFactory() {
        return new DiagnosFactory();
    }
}
