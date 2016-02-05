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
package se.inera.intyg.rehabstod.integration.it.config;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import se.inera.intyg.clinicalprocess.healthcond.rehabilitation.listactivesickleavesforcareunit.v1.ListActiveSickLeavesForCareUnitResponderInterface;

/**
 * Created by eriklupander on 2016-02-05.
 */
@Configuration
@Profile("!rhs-it-stub")
public class IntygstjanstIntegrationClientConfiguration {

    @Value("${it.service.url}")
    private String itWsUrl;

    @Bean
    @Profile(value = {"dev", "test", "prod"})
    public ListActiveSickLeavesForCareUnitResponderInterface itIntegrationWebServiceClient() {
        JaxWsProxyFactoryBean proxyFactoryBean = new JaxWsProxyFactoryBean();
        proxyFactoryBean.setAddress(itWsUrl);
        proxyFactoryBean.setServiceClass(ListActiveSickLeavesForCareUnitResponderInterface.class);
        return (ListActiveSickLeavesForCareUnitResponderInterface) proxyFactoryBean.create();
    }
}
