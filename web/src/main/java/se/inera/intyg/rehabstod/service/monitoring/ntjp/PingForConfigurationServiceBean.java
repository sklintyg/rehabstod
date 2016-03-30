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
package se.inera.intyg.rehabstod.service.monitoring.ntjp;

import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import se.inera.intyg.rehabstod.service.monitoring.dto.HealthStatus;
import se.riv.itintegration.monitoring.rivtabp21.v1.PingForConfigurationResponderInterface;
import se.riv.itintegration.monitoring.v1.PingForConfigurationType;

import javax.xml.ws.soap.SOAPFaultException;

/**
 * Created by eriklupander on 2016-03-30.
 */
@Service
public class PingForConfigurationServiceBean implements PingForConfigurationService {

    /**
     * NTjP QA PfC always responds with "VP004 No Logical Adress found for serviceNamespace:urn:riv:itintegration:monitoring:PingForConfigurationResponder:1, receiverId:SExxxxxxxxxx-xxxx"
     * for the typical request for the logicalAddress ${infrastructure.directory.logicalAddress}.
     */
    private static final String NO_LOGICAL_ADDRESS_ERR_PREFIX = "VP004";

    @Value("${infrastructure.directory.logicalAddress}")
    private String logicalAddress;

    @Autowired
    @Qualifier("ntjpPingWebServiceClient")
    private PingForConfigurationResponderInterface pfcService;

    @Override
    public HealthStatus pingNtjp(String withLogicalAddress) {
        PingForConfigurationType reqType = new PingForConfigurationType();
        reqType.setLogicalAddress(logicalAddress);
        reqType.setServiceContractNamespace("");
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            pfcService.pingForConfiguration(logicalAddress, reqType);
            stopWatch.stop();
            return new HealthStatus(stopWatch.getTime(), true);
        } catch (SOAPFaultException e) {

            stopWatch.stop();
            if (e.getMessage().startsWith(NO_LOGICAL_ADDRESS_ERR_PREFIX)) {
                return new HealthStatus(stopWatch.getTime(), true);
            } else {
                return new HealthStatus(stopWatch.getTime(), false);
            }
        }
    }

}
