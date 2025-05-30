/*
 * Copyright (C) 2025 Inera AB (http://www.inera.se)
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

import static se.inera.intyg.rehabstod.web.controller.api.SessionStatusController.SESSION_STATUS_CHECK_URI;
import static se.inera.intyg.rehabstod.web.controller.api.SessionStatusController.SESSION_STATUS_EXTEND;
import static se.inera.intyg.rehabstod.web.controller.api.SessionStatusController.SESSION_STATUS_REQUEST_MAPPING;

import jakarta.servlet.FilterRegistration;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRegistration;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.DispatcherServlet;
import se.inera.intyg.infra.monitoring.MonitoringConfiguration;
import se.inera.intyg.infra.monitoring.logging.LogbackConfiguratorContextListener;
import se.inera.intyg.infra.security.filter.RequestContextHolderUpdateFilter;
import se.inera.intyg.infra.security.filter.SessionTimeoutFilter;
import se.inera.intyg.rehabstod.auth.RSSecurityHeadersFilter;
import se.inera.intyg.rehabstod.integration.it.config.IntygstjanstIntegrationClientConfiguration;
import se.inera.intyg.rehabstod.integration.it.config.IntygstjanstIntegrationConfiguration;
import se.inera.intyg.rehabstod.integration.it.config.IntygstjanstRestIntegrationConfiguration;
import se.inera.intyg.rehabstod.integration.it.stub.IntygstjanstIntegrationStubConfiguration;
import se.inera.intyg.rehabstod.integration.samtyckestjanst.config.SamtyckestjanstClientConfiguration;
import se.inera.intyg.rehabstod.integration.samtyckestjanst.config.SamtyckestjanstConfiguration;
import se.inera.intyg.rehabstod.integration.samtyckestjanst.stub.SamtyckestjanstStubConfiguration;
import se.inera.intyg.rehabstod.integration.sparrtjanst.config.SparrtjanstClientConfiguration;
import se.inera.intyg.rehabstod.integration.sparrtjanst.config.SparrtjanstConfiguration;
import se.inera.intyg.rehabstod.integration.sparrtjanst.stub.SparrtjanstStubConfiguration;
import se.inera.intyg.rehabstod.integration.srs.config.SRSIntegrationClientConfiguration;
import se.inera.intyg.rehabstod.integration.srs.config.SRSIntegrationConfiguration;
import se.inera.intyg.rehabstod.integration.srs.stub.SRSIntegrationStubConfiguration;
import se.inera.intyg.rehabstod.integration.wc.config.WcClientConfiguration;
import se.inera.intyg.rehabstod.integration.wc.config.WcIntegrationConfiguration;
import se.inera.intyg.rehabstod.integration.wc.stub.WcIntegrationStubConfiguration;
import se.inera.intyg.rehabstod.persistence.config.PersistenceConfig;
import se.inera.intyg.rehabstod.persistence.config.PersistenceConfigDev;

public class ApplicationInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(jakarta.servlet.ServletContext servletContext) {
        servletContext.setInitParameter("logbackConfigParameter", "logback.file");
        servletContext.addListener(new LogbackConfiguratorContextListener());

        AnnotationConfigWebApplicationContext appContext = new AnnotationConfigWebApplicationContext();

        appContext.register(WebSecurityConfig.class, ApplicationConfig.class, CacheConfigurationFromInfra.class,
            PuConfiguration.class, ServiceConfig.class,
            IaConfiguration.class, JobConfig.class,
            IntygstjanstIntegrationConfiguration.class,
            IntygstjanstRestIntegrationConfiguration.class,
            IntygstjanstIntegrationClientConfiguration.class,
            IntygstjanstIntegrationStubConfiguration.class,
            WcClientConfiguration.class,
            WcIntegrationConfiguration.class,
            WcIntegrationStubConfiguration.class,
            SamtyckestjanstConfiguration.class,
            SamtyckestjanstClientConfiguration.class,
            SamtyckestjanstStubConfiguration.class,
            SparrtjanstConfiguration.class,
            SparrtjanstClientConfiguration.class,
            SparrtjanstStubConfiguration.class,
            SRSIntegrationConfiguration.class,
            SRSIntegrationClientConfiguration.class,
            SRSIntegrationStubConfiguration.class,
            JmsConfig.class, NTjPPingConfig.class, SecurityConfig.class,
            SjukfallConfig.class, EmployeeNameCacheConfig.class, InfraConfig.class, PersistenceConfig.class,
            PersistenceConfigDev.class, MonitoringConfiguration.class);

        servletContext.addListener(new ContextLoaderListener(appContext));

        AnnotationConfigWebApplicationContext webConfig = new AnnotationConfigWebApplicationContext();
        webConfig.register(WebConfig.class);
        ServletRegistration.Dynamic servlet = servletContext.addServlet("dispatcher", new DispatcherServlet(webConfig));
        servlet.setLoadOnStartup(1);
        servlet.addMapping("/");

        // NOTE: The characterEncoding filter must run before any invocation to request.getParameter()
        FilterRegistration.Dynamic characterEncodingFilter = servletContext.addFilter("characterEncodingFilter",
            CharacterEncodingFilter.class);
        characterEncodingFilter.addMappingForUrlPatterns(null, false, "/*");
        characterEncodingFilter.setInitParameter("encoding", "UTF-8");
        characterEncodingFilter.setInitParameter("forceEncoding", "true");

        // Spring session filter
        FilterRegistration.Dynamic springSessionRepositoryFilter = servletContext.addFilter("springSessionRepositoryFilter",
            DelegatingFilterProxy.class);
        springSessionRepositoryFilter.addMappingForUrlPatterns(null, false, "/*");

        // Update RequestContext with spring session
        FilterRegistration.Dynamic requestContextHolderUpdateFilter = servletContext.addFilter("requestContextHolderUpdateFilter",
            RequestContextHolderUpdateFilter.class);
        requestContextHolderUpdateFilter.addMappingForUrlPatterns(null, false, "/*");

        // LogMDCServletFilter
        FilterRegistration.Dynamic logMdcFilter = servletContext.addFilter("mdcServletFilter",
            DelegatingFilterProxy.class);
        logMdcFilter.addMappingForUrlPatterns(null, false, "/*");

        // Session Timeout filter
        FilterRegistration.Dynamic sessionTimeoutFilter = servletContext.addFilter("sessionTimeoutFilter", SessionTimeoutFilter.class);
        sessionTimeoutFilter.addMappingForUrlPatterns(null, false, "/*");
        sessionTimeoutFilter.setInitParameter("skipRenewSessionUrls", SESSION_STATUS_CHECK_URI);

        // Spring security filter
        FilterRegistration.Dynamic springSecurityFilterChain = servletContext.addFilter("springSecurityFilterChain",
            DelegatingFilterProxy.class);
        springSecurityFilterChain.addMappingForUrlPatterns(null, false, "/*");

        // LogMDCUserServletFilter
        FilterRegistration.Dynamic logMdcUserFilter = servletContext.addFilter("mdcUserServletFilter",
            DelegatingFilterProxy.class);
        logMdcUserFilter.addMappingForUrlPatterns(null, false, "/*");

        // principalUpdatedFilter filter
        FilterRegistration.Dynamic principalUpdatedFilter = servletContext.addFilter("principalUpdatedFilter",
            DelegatingFilterProxy.class);
        principalUpdatedFilter.setInitParameter("targetFilterLifecycle", "true");
        principalUpdatedFilter.addMappingForUrlPatterns(null, false, "/*");

        // unitSelectedAssurance filter
        FilterRegistration.Dynamic unitSelectedAssuranceFilter = servletContext.addFilter("unitSelectedAssuranceFilter",
            DelegatingFilterProxy.class);
        unitSelectedAssuranceFilter.setInitParameter("targetFilterLifecycle", "true");
        unitSelectedAssuranceFilter.addMappingForUrlPatterns(null, false, "/api/*");
        unitSelectedAssuranceFilter.setInitParameter("ignoredUrls",
            SESSION_STATUS_CHECK_URI + ",/api/config,/api/user,/api/user/andraenhet");

        // pdlConsentGiven filter
        FilterRegistration.Dynamic pdlConsentGivenAssuranceFilter = servletContext.addFilter("pdlConsentGivenAssuranceFilter",
            DelegatingFilterProxy.class);
        pdlConsentGivenAssuranceFilter.setInitParameter("targetFilterLifecycle", "true");
        pdlConsentGivenAssuranceFilter.addMappingForUrlPatterns(null, false, "/api/*");
        pdlConsentGivenAssuranceFilter.setInitParameter("ignoredUrls",
            SESSION_STATUS_CHECK_URI + "," + SESSION_STATUS_REQUEST_MAPPING + SESSION_STATUS_EXTEND
                + ",/api/config,/api/user,/api/user/giveconsent,/api/sjukfall/summary,/api/stub,/api/sickleaves,/api/lu,/api/testability,"
                + "/api/log/error");

        FilterRegistration.Dynamic hiddenHttpMethodFilter = servletContext.addFilter("hiddenHttpMethodFilter",
            HiddenHttpMethodFilter.class);
        hiddenHttpMethodFilter.addMappingForUrlPatterns(null, false, "/*");

        registerSecurityHeadersFilter(servletContext);

        // CXF services filter
        ServletRegistration.Dynamic cxfServlet = servletContext.addServlet("services", new CXFServlet());
        cxfServlet.setLoadOnStartup(1);
        cxfServlet.addMapping("/services/*");

        // Listeners for session audit logging
        servletContext.addListener(new HttpSessionEventPublisher());
        servletContext.addListener(new RequestContextListener());
    }

    private void registerSecurityHeadersFilter(ServletContext servletContext) {
        RSSecurityHeadersFilter filter = new RSSecurityHeadersFilter();
        servletContext.addFilter("securityHeadersFilter", filter).addMappingForUrlPatterns(null, true, "/*");
    }
}
