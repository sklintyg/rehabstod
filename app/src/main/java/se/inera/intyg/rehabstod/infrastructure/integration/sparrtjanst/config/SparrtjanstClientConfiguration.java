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
package se.inera.intyg.rehabstod.infrastructure.integration.sparrtjanst.config;

// CHECKSTYLE:OFF LineLength

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import se.inera.intyg.rehabstod.infrastructure.integration.sparrtjanst.config.properties.SparrtjanstProperties;
import se.riv.informationsecurity.authorization.blocking.CheckBlocks.v4.rivtabp21.CheckBlocksResponderInterface;

// CHECKSTYLE:ON LineLength

@Configuration
@Profile("!rhs-sparrtjanst-stub")
public class SparrtjanstClientConfiguration {

  private final SparrtjanstTlsConfig tlsConfig;
  private final String receiveTimeout;
  private final String connectionTimeout;
  private final String checkBlocksUrl;

  public SparrtjanstClientConfiguration(
      SparrtjanstTlsConfig tlsConfig, SparrtjanstProperties props) {
    this.tlsConfig = tlsConfig;
    this.receiveTimeout = String.valueOf(props.receiveTimeout());
    this.connectionTimeout = String.valueOf(props.connectionTimeout());
    this.checkBlocksUrl = props.checkBlocksUrl();
  }

  @Bean
  public CheckBlocksResponderInterface checkBlocksWebServiceClient() {
    JaxWsProxyFactoryBean proxyFactoryBean = new JaxWsProxyFactoryBean();
    proxyFactoryBean.setAddress(checkBlocksUrl);
    proxyFactoryBean.setServiceClass(CheckBlocksResponderInterface.class);
    CheckBlocksResponderInterface checkBlocksResponderInterface =
        (CheckBlocksResponderInterface) proxyFactoryBean.create();
    Client client = ClientProxy.getClient(checkBlocksResponderInterface);
    applyConduitConfig(client);
    return checkBlocksResponderInterface;
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
          "Cannot apply timeouts for SparrtjanstClientConfiguration, unparsable String value: "
              + timeout
              + ". Message: "
              + e.getMessage());
    }
  }
}
