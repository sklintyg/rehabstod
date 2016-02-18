/**
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
 *
 * This file is part of rehabstod (https://github.com/sklintyg/rehabstod).
 *
 * rehabstod is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * rehabstod is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.intyg.rehabstod.config;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

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
import se.inera.intyg.rehabstod.integration.it.config.IntygstjanstIntegrationClientConfiguration;
import se.inera.intyg.rehabstod.integration.it.config.IntygstjanstIntegrationConfiguration;
import se.inera.intyg.rehabstod.integration.it.stub.IntygstjanstIntegrationStubConfiguration;

// import se.inera.intyg.rehabstod.hsa.config.HsaConfiguration;

public class ApplicationInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(javax.servlet.ServletContext servletContext) throws ServletException {
        AnnotationConfigWebApplicationContext appContext = new AnnotationConfigWebApplicationContext();

        appContext.register(ApplicationConfig.class, HsaConfiguration.class,
                ServiceConfig.class, IntygstjanstIntegrationConfiguration.class,
                IntygstjanstIntegrationClientConfiguration.class,
                IntygstjanstIntegrationStubConfiguration.class,
                JmsConfig.class);

        servletContext.addListener(new ContextLoaderListener(appContext));

        AnnotationConfigWebApplicationContext webConfig = new AnnotationConfigWebApplicationContext();
        webConfig.register(WebConfig.class);
        ServletRegistration.Dynamic servlet = servletContext.addServlet("dispatcher", new DispatcherServlet(webConfig));
        servlet.setLoadOnStartup(1);
        servlet.addMapping("/");

        // Spring security filter
        FilterRegistration.Dynamic springSecurityFilterChain = servletContext.addFilter("springSecurityFilterChain", DelegatingFilterProxy.class);
        springSecurityFilterChain.addMappingForUrlPatterns(null, false, "/*");

        FilterRegistration.Dynamic hiddenHttpMethodFilter = servletContext.addFilter("hiddenHttpMethodFilter", HiddenHttpMethodFilter.class);
        hiddenHttpMethodFilter.addMappingForUrlPatterns(null, false, "/*");

        registerCharachterEncodingFilter(servletContext);

        // CXF services filter
        ServletRegistration.Dynamic cxfServlet = servletContext.addServlet("services", new CXFServlet());
        cxfServlet.setLoadOnStartup(1);
        cxfServlet.addMapping("/services/*");

        // Listeners for session audit logging
        servletContext.addListener(new HttpSessionEventPublisher());
        servletContext.addListener(new RequestContextListener());
    }

    private void registerCharachterEncodingFilter(ServletContext aContext) {
        CharacterEncodingFilter cef = new CharacterEncodingFilter();
        cef.setForceEncoding(true);
        cef.setEncoding("UTF-8");
        aContext.addFilter("characterEncodingFilter", cef).addMappingForUrlPatterns(null, true, "/*");
    }
}
