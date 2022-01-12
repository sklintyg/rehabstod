/*
 * Copyright (C) 2022 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.integration.it.stub;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@ComponentScan({"se.inera.intyg.rehabstod.integration.it.stub"})
@Profile({"rhs-it-stub"})
public class IntygstjanstIntegrationStubConfiguration {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ListActiveSickLeavesForCareUnitStub listActiveSickLeavesForCareUnitStub;

    @Autowired
    private ListSickLeavesForPersonStub listSickLeavesForPersonStub;

    @Autowired
    private Bus bus;

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public EndpointImpl listActiveSickLeavesForCareUnitResponder() {
        Object implementor = listActiveSickLeavesForCareUnitStub;
        EndpointImpl endpoint = new EndpointImpl(bus, implementor);
        endpoint.publish("/get-active-sickleaves-for-careunit/v1.0");
        return endpoint;
    }

    @Bean
    public EndpointImpl listSickLeavesForPersonResponder() {
        Object implementor = listSickLeavesForPersonStub;
        EndpointImpl endpoint = new EndpointImpl(bus, implementor);
        endpoint.publish("/get-active-sickleaves-for-person/v1.0");
        return endpoint;
    }

}
