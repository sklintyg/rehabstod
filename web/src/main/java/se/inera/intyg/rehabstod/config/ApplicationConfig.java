/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
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
import java.util.ArrayList;
import java.util.Arrays;
import javax.annotation.PostConstruct;
import org.apache.cxf.Bus;
import org.apache.cxf.ext.logging.LoggingFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;
import se.inera.intyg.infra.monitoring.MonitoringConfiguration;
import se.inera.intyg.infra.security.common.cookie.IneraCookieSerializer;
import se.inera.intyg.infra.security.filter.PrincipalUpdatedFilter;
import se.inera.intyg.rehabstod.service.diagnos.DiagnosFactory;
import se.inera.intyg.rehabstod.web.filters.PdlConsentGivenAssuranceFilter;
import se.inera.intyg.rehabstod.web.filters.UnitSelectedAssuranceFilter;

@Configuration
@EnableTransactionManagement
@PropertySource(ignoreResourceNotFound = true, value = {"classpath:application.properties",
    "file:${dev.config.file}",
    "classpath:version.properties"})
@Import(MonitoringConfiguration.class)
public class ApplicationConfig implements TransactionManagementConfigurer {

    @Autowired
    private Bus bus;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Bean
    public CookieSerializer cookieSerializer() {
        /*
        This is needed to make IdP functionality work.
        This will not satisfy all browsers, but it works for IE, Chrome and Edge.
        Reference: https://auth0.com/blog/browser-behavior-changes-what-developers-need-to-know/
         */
        return new IneraCookieSerializer();
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @PostConstruct
    public Bus init() {
        bus.setFeatures(new ArrayList<>(Arrays.asList(loggingFeature())));
        return bus;
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

    @Bean
    public PrincipalUpdatedFilter principalUpdatedFilter() {
        return new PrincipalUpdatedFilter();
    }

    @Bean(name = "jacksonJsonProvider")
    public JacksonJaxbJsonProvider jacksonJsonProvider() {

        return new JacksonJaxbJsonProvider();
    }


    @Bean
    public DiagnosFactory diagnosFactory() {
        return new DiagnosFactory();
    }

    @Bean
    public LoggingFeature loggingFeature() {
        LoggingFeature loggingFeature = new LoggingFeature();
        loggingFeature.setPrettyLogging(true);
        return loggingFeature;
    }

    @Override
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return transactionManager;
    }
}
