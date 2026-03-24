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
package se.inera.intyg.rehabstod.infrastructure.config;

import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import se.inera.intyg.rehabstod.application.diagnos.DiagnosFactory;
import se.inera.intyg.rehabstod.infrastructure.security.auth.PdlConsentGivenAssuranceFilter;
import se.inera.intyg.rehabstod.infrastructure.security.auth.UnitSelectedAssuranceFilter;
import se.inera.intyg.rehabstod.infrastructure.security.filter.PrincipalUpdatedFilter;

@Configuration
@EnableTransactionManagement
@ComponentScan({
  "se.inera.intyg.rehabstod.logging",
  "se.inera.intyg.rehabstod.integration.it",
  "se.inera.intyg.rehabstod.integration.wc",
  "se.inera.intyg.rehabstod.sjukfall",
  "se.inera.intyg.rehabstod.pu.integration.api",
  "se.inera.intyg.rehabstod.integration.hsatk",
  "se.inera.intyg.rehabstod.integration.intygproxyservice",
  "se.inera.intyg.rehabstod.pu.integration.intygproxyservice",
  "se.inera.intyg.rehabstod.dynamiclink"
})
public class ApplicationConfig {

  @Bean(name = Bus.DEFAULT_BUS_ID)
  public SpringBus springBus() {
    return new SpringBus();
  }

  @Bean
  public ServletRegistrationBean<CXFServlet> cxfServletRegistration() {
    ServletRegistrationBean<CXFServlet> registration =
        new ServletRegistrationBean<>(new CXFServlet(), "/services/*");
    registration.setName("services");
    registration.setLoadOnStartup(1);
    return registration;
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

  @Bean
  public DiagnosFactory diagnosFactory() {
    return new DiagnosFactory();
  }
}
