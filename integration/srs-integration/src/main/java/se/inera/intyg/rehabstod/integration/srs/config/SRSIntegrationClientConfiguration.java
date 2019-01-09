/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.integration.srs.config;

// CHECKSTYLE:OFF LineLength

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import se.inera.intyg.clinicalprocess.healthcond.srs.getriskpredictionforcertificate.v1.GetRiskPredictionForCertificateResponderInterface;
import se.riv.itintegration.monitoring.rivtabp21.v1.PingForConfigurationResponderInterface;

// CHECKSTYLE:ON LineLength

/**
 * Declares and bootstraps the Intygstjänst client for {@link GetRiskPredictionForCertificateResponderInterface}
 *
 * Somewhat "hackish" use of profiles:
 *
 * Bean is only active when rhs-it-stub is NOT active. The underlying getRiskPredictionForCertificateWebServiceClient() @Bean and (PfC) is
 * active for either of dev,test,prod (which should be possible to replace with no @Profile at all)
 *
 * Created by eriklupander on 2016-02-05.
 */
@Configuration
@Profile("!rhs-srs-stub")
public class SRSIntegrationClientConfiguration {

    private static final String DEFAULT_RECEIVE_TIMEOUT = "60000";
    private static final String DEFAULT_CONNECTION_TIMEOUT = "15000";

    @Value("${srs.service.receive.timeout}")
    private String receiveTimeout = DEFAULT_RECEIVE_TIMEOUT;

    @Value("${srs.service.connection.timeout}")
    private String connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;

    @Value("${srs.service.url}")
    private String srsWsUrl;

    @Value("${srs.ping.url}")
    private String srsWsPingUrl;

    @Bean
    public GetRiskPredictionForCertificateResponderInterface getRiskPredictionForCertificateWebServiceClient() {
        // CHECKSTYLE:OFF LineLength
        JaxWsProxyFactoryBean proxyFactoryBean = new JaxWsProxyFactoryBean();
        proxyFactoryBean.setAddress(srsWsUrl);
        proxyFactoryBean.setServiceClass(GetRiskPredictionForCertificateResponderInterface.class);
        GetRiskPredictionForCertificateResponderInterface getRiskPredictionForCertificateResponderInterface = (GetRiskPredictionForCertificateResponderInterface) proxyFactoryBean
                .create();
        Client client = ClientProxy.getClient(getRiskPredictionForCertificateResponderInterface);
        applyTimeouts(client);
        return getRiskPredictionForCertificateResponderInterface;
        // CHECKSTYLE:ON LineLength
    }

    @Bean
    public PingForConfigurationResponderInterface srsPingForConfigurationWebServiceClient() {
        // CHECKSTYLE:OFF LineLength
        JaxWsProxyFactoryBean proxyFactoryBean = new JaxWsProxyFactoryBean();
        proxyFactoryBean.setAddress(srsWsPingUrl);
        proxyFactoryBean.setServiceClass(PingForConfigurationResponderInterface.class);

        PingForConfigurationResponderInterface pingForConfigurationResponderInterface = (PingForConfigurationResponderInterface) proxyFactoryBean
                .create();
        Client client = ClientProxy.getClient(pingForConfigurationResponderInterface);
        applyTimeouts(client);
        return pingForConfigurationResponderInterface;
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
                    "Cannot apply timeouts for SRSIntegrationClientConfiguration, unparsable String value: " + timeout
                            + ". Message: " + e.getMessage());
        }
    }
}
