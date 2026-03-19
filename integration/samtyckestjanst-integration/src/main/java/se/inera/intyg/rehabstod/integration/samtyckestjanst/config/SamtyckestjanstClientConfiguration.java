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
package se.inera.intyg.rehabstod.integration.samtyckestjanst.config;

// CHECKSTYLE:OFF LineLength

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import se.riv.informationsecurity.authorization.consent.CheckConsent.v2.rivtabp21.CheckConsentResponderInterface;
import se.riv.informationsecurity.authorization.consent.RegisterExtendedConsent.v2.rivtabp21.RegisterExtendedConsentResponderInterface;

// CHECKSTYLE:ON LineLength

@Configuration
@Profile("!rhs-samtyckestjanst-stub")
public class SamtyckestjanstClientConfiguration {

  private static final String DEFAULT_RECEIVE_TIMEOUT = "30000";
  private static final String DEFAULT_CONNECTION_TIMEOUT = "10000";

  @Value("${samtyckestjanst.service.receive.timeout}")
  private String receiveTimeout = DEFAULT_RECEIVE_TIMEOUT;

  @Value("${samtyckestjanst.service.connection.timeout}")
  private String connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;

  @Value("${samtyckestjanst.checkconsent.endpoint.url}")
  private String checkConsentUrl;

  @Value("${samtyckestjanst.registerextendedconsent.endpoint.url}")
  private String registerConsentUrl;

  @Autowired private SamtyckestjanstTlsConfig tlsConfig;

  @Bean
  public CheckConsentResponderInterface checkConsentWebServiceClient() {
    JaxWsProxyFactoryBean proxyFactoryBean = new JaxWsProxyFactoryBean();
    proxyFactoryBean.setAddress(checkConsentUrl);
    proxyFactoryBean.setServiceClass(CheckConsentResponderInterface.class);
    CheckConsentResponderInterface checkConsentResponderInterface =
        (CheckConsentResponderInterface) proxyFactoryBean.create();
    Client client = ClientProxy.getClient(checkConsentResponderInterface);
    applyConduitConfig(client);
    return checkConsentResponderInterface;
  }

  @Bean
  public RegisterExtendedConsentResponderInterface registerExtendedConsentWebServiceClient() {
    JaxWsProxyFactoryBean proxyFactoryBean = new JaxWsProxyFactoryBean();
    proxyFactoryBean.setAddress(registerConsentUrl);
    proxyFactoryBean.setServiceClass(RegisterExtendedConsentResponderInterface.class);
    RegisterExtendedConsentResponderInterface registerExtendedConsentResponderInterface =
        (RegisterExtendedConsentResponderInterface) proxyFactoryBean.create();
    Client client = ClientProxy.getClient(registerExtendedConsentResponderInterface);
    applyConduitConfig(client);
    return registerExtendedConsentResponderInterface;
  }

  private void applyConduitConfig(Client client) {
    if (client == null) {
      return;
    }
    HTTPConduit conduit = (HTTPConduit) client.getConduit();
    applyTimeouts(conduit);
    applyTls(conduit);
    applyHttpClientPolicy(conduit);
  }

  private void applyTimeouts(HTTPConduit conduit) {
    Long connTimeout = parseTimeout(connectionTimeout);
    Long recTimeout = parseTimeout(receiveTimeout);
    HTTPClientPolicy policy =
        conduit.getClient() != null ? conduit.getClient() : new HTTPClientPolicy();
    policy.setConnectionTimeout(connTimeout);
    policy.setReceiveTimeout(recTimeout);
    conduit.setClient(policy);
  }

  private void applyHttpClientPolicy(HTTPConduit conduit) {
    HTTPClientPolicy policy =
        conduit.getClient() != null ? conduit.getClient() : new HTTPClientPolicy();
    policy.setAllowChunking(false);
    policy.setAutoRedirect(true);
    conduit.setClient(policy);
  }

  private void applyTls(HTTPConduit conduit) {
    tlsConfig.configure(conduit);
  }

  private Long parseTimeout(String timeout) {
    try {
      return Long.parseLong(timeout);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException(
          "Cannot apply timeouts for SamtyckestjanstClientConfiguration, unparsable String value: "
              + timeout
              + ". Message: "
              + e.getMessage());
    }
  }
}
