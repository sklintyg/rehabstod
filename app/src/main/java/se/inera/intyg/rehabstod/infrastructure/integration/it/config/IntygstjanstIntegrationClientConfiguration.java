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
package se.inera.intyg.rehabstod.infrastructure.integration.it.config;

// CHECKSTYLE:OFF LineLength

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import se.inera.intyg.clinicalprocess.healthcond.rehabilitation.listactivesickleavesforcareunit.v1.ListActiveSickLeavesForCareUnitResponderInterface;
import se.inera.intyg.clinicalprocess.healthcond.rehabilitation.listsickleavesforperson.v1.ListSickLeavesForPersonResponderInterface;
import se.inera.intyg.rehabstod.infrastructure.config.properties.IntygstjanstProperties;

// CHECKSTYLE:ON LineLength

/**
 * Declares and bootstraps the Intygstjänst client for
 * {@link ListActiveSickLeavesForCareUnitResponderInterface}
 *
 * <p>Somewhat "hackish" use of profiles:
 *
 * <p>Bean is only active when rhs-it-stub is NOT active. The underlying beans is active for either
 * of dev,test,prod (which should be possible to replace with no @Profile at all)
 *
 * <p>Created by eriklupander on 2016-02-05.
 */
@Configuration
@Profile("!rhs-it-stub")
public class IntygstjanstIntegrationClientConfiguration {

  private final String receiveTimeout;
  private final String connectionTimeout;
  private final String listSickleavesForPersonUrl;

  public IntygstjanstIntegrationClientConfiguration(IntygstjanstProperties props) {
    this.receiveTimeout = String.valueOf(props.service().receiveTimeout());
    this.connectionTimeout = String.valueOf(props.service().connectionTimeout());
    this.listSickleavesForPersonUrl = props.listSickLeavesForPersonUrl();
  }

  @Bean
  public ListSickLeavesForPersonResponderInterface listSickLeavesForPersonWebServiceClient() {
    // CHECKSTYLE:OFF LineLength
    JaxWsProxyFactoryBean proxyFactoryBean = new JaxWsProxyFactoryBean();
    proxyFactoryBean.setAddress(listSickleavesForPersonUrl);
    proxyFactoryBean.setServiceClass(ListSickLeavesForPersonResponderInterface.class);
    ListSickLeavesForPersonResponderInterface listSickLeavesForPersonResponderInterface =
        (ListSickLeavesForPersonResponderInterface) proxyFactoryBean.create();
    Client client = ClientProxy.getClient(listSickLeavesForPersonResponderInterface);
    applyTimeouts(client);
    return listSickLeavesForPersonResponderInterface;
    // CHECKSTYLE:ON LineLength
  }

  private void applyTimeouts(Client client) {
    Long connTimeout = parseTimeout(connectionTimeout);
    Long recTimeout = parseTimeout(receiveTimeout);

    if (client != null) {
      HTTPConduit conduit = (HTTPConduit) client.getConduit();
      HTTPClientPolicy policy = new HTTPClientPolicy();
      policy.setConnectionTimeout(connTimeout);
      policy.setReceiveTimeout(recTimeout);
      conduit.setClient(policy);
    }
  }

  private Long parseTimeout(String timeout) {
    try {
      return Long.parseLong(timeout);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException(
          "Cannot apply timeouts for IntygstjanstIntegrationClientConfiguration, unparsable String value: "
              + timeout
              + ". Message: "
              + e.getMessage());
    }
  }
}