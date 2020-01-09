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

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import se.riv.itintegration.monitoring.rivtabp21.v1.PingForConfigurationResponderInterface;

/**
 * Created by eriklupander on 2016-03-30.
 */
@Configuration
@ImportResource("classpath:ntjp-services.xml")
public class NTjPPingConfig {

    @Value("${itintegration.monitoring.pingforconfiguration.url}")
    private String ntjpWsPingUrl;

    @Bean
    public PingForConfigurationResponderInterface ntjpPingWebServiceClient() {
        JaxWsProxyFactoryBean proxyFactoryBean = new JaxWsProxyFactoryBean();
        proxyFactoryBean.setAddress(ntjpWsPingUrl);
        proxyFactoryBean.setServiceClass(PingForConfigurationResponderInterface.class);
        return (PingForConfigurationResponderInterface) proxyFactoryBean.create();
    }
}
