/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import se.riv.informationsecurity.authorization.consent.CheckConsent.v2.rivtabp21.CheckConsentResponderInterface;
import se.riv.informationsecurity.authorization.consent.RegisterExtendedConsent.v2.rivtabp21.RegisterExtendedConsentResponderInterface;

// CHECKSTYLE:ON LineLength

/**
 * Declares and bootstraps the Samtyckestjänst client for {@link CheckConsentResponderInterface}
 * and {@link RegisterExtendedConsentResponderInterface}
 *
 * Created by magnusekstrand on 2019-04-12.
 */
@Configuration
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


    @Bean
    public CheckConsentResponderInterface checkConsentWebServiceClient() {
        JaxWsProxyFactoryBean proxyFactoryBean = new JaxWsProxyFactoryBean();
        proxyFactoryBean.setAddress(checkConsentUrl);
        proxyFactoryBean.setServiceClass(CheckConsentResponderInterface.class);
        CheckConsentResponderInterface checkConsentResponderInterface = (CheckConsentResponderInterface) proxyFactoryBean.create();
        Client client = ClientProxy.getClient(checkConsentResponderInterface);
        applyTimeouts(client);
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
        applyTimeouts(client);
        return registerExtendedConsentResponderInterface;
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
                "Cannot apply timeouts for SamtyckestjanstClientConfiguration, unparsable String value: " + timeout
                    + ". Message: " + e.getMessage());
        }
    }
}
