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
package se.inera.intyg.rehabstod.integration.it.stub;

import java.time.LocalDateTime;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import se.riv.itintegration.monitoring.rivtabp21.v1.PingForConfigurationResponderInterface;
import se.riv.itintegration.monitoring.v1.PingForConfigurationResponseType;
import se.riv.itintegration.monitoring.v1.PingForConfigurationType;

/**
 * If profile rhs-it-stub is NOT active,
 * {@link se.inera.intyg.rehabstod.integration.it.config.IntygstjanstIntegrationClientConfiguration} will supply an
 * implementation
 * of the {@link PingForConfigurationResponderInterface} interface.
 *
 * Created by eriklupander on 2016-03-29.
 */
@Service("itPingForConfigurationWebServiceClient")
@Profile({"rhs-it-stub"})
public class PingForConfigurationResponderStub implements PingForConfigurationResponderInterface {

    @Override
    public PingForConfigurationResponseType pingForConfiguration(String s, PingForConfigurationType pingForConfigurationType) {
        PingForConfigurationResponseType responseType = new PingForConfigurationResponseType();
        responseType.setVersion("stubbed-version");
        responseType.setPingDateTime(LocalDateTime.now().toString());
        return responseType;
    }
}
