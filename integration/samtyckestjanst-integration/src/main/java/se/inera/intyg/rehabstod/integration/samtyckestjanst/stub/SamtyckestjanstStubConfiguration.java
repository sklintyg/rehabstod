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
package se.inera.intyg.rehabstod.integration.samtyckestjanst.stub;

import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@ComponentScan("se.inera.intyg.rehabstod.integration.samtyckestjanst.stub")
@Profile("rhs-samtyckestjanst-stub")
public class SamtyckestjanstStubConfiguration {

  public static final String CACHE_NAME = "samtyckestjanstStubCache";

  @Autowired private Bus bus;

  @Autowired private CheckConsentStub checkConsentStub;

  @Autowired private RegisterExtendedConsentStub registerExtendedConsentStub;

  @Bean
  public EndpointImpl checkConsentResponder() {
    EndpointImpl endpoint = new EndpointImpl(bus, checkConsentStub);
    endpoint.publish("/stubs/informationsecurity/authorization/consent/CheckConsent/2/rivtabp21");
    return endpoint;
  }

  @Bean
  public EndpointImpl registerExtendedConsentResponder() {
    EndpointImpl endpoint = new EndpointImpl(bus, registerExtendedConsentStub);
    endpoint.publish("/stubs/informationsecurity/authorization/consent/RegisterExtendedConsent/2/rivtabp21");
    return endpoint;
  }
}
