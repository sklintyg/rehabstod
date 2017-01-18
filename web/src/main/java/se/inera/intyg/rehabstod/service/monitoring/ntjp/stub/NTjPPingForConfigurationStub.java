/**
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.service.monitoring.ntjp.stub;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import se.riv.itintegration.monitoring.rivtabp21.v1.PingForConfigurationResponderInterface;
import se.riv.itintegration.monitoring.v1.ConfigurationType;
import se.riv.itintegration.monitoring.v1.PingForConfigurationResponseType;
import se.riv.itintegration.monitoring.v1.PingForConfigurationType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by eriklupander on 2016-03-30.
 */
@Service
@Profile(value = {"dev", "wc-hsa-stub"})
public class NTjPPingForConfigurationStub implements PingForConfigurationResponderInterface {

    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Override
    public PingForConfigurationResponseType pingForConfiguration(String s, PingForConfigurationType pingForConfigurationType) {
        PingForConfigurationResponseType responseType = new PingForConfigurationResponseType();

        responseType.setPingDateTime(LocalDateTime.now().format(dateTimeFormatter));
        responseType.setVersion("ntjp-stub");
        ConfigurationType cfgType = new ConfigurationType();
        cfgType.setName("stub-info");
        cfgType.setValue("This stub pretends it is NTjP itinfrastructure:monitoring:PingForConfiguration.");
        responseType.getConfiguration().add(cfgType);
        return responseType;
    }
}
