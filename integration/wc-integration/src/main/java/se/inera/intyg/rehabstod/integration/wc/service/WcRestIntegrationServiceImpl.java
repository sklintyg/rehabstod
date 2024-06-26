/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
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

package se.inera.intyg.rehabstod.integration.wc.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import se.inera.intyg.rehabstod.integration.wc.service.dto.UnansweredCommunicationRequest;
import se.inera.intyg.rehabstod.integration.wc.service.dto.UnansweredCommunicationResponse;

@Service
public class WcRestIntegrationServiceImpl implements WcRestIntegrationService {


    @Value("${webcert.internal.host.url}")
    private String wcUrl;

    private static final Logger LOG = LoggerFactory.getLogger(WcRestIntegrationServiceImpl.class);

    private final RestTemplate restTemplate;

    public WcRestIntegrationServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public UnansweredCommunicationResponse getUnansweredCommunicationForPatients(UnansweredCommunicationRequest request) {
        final var url = wcUrl + "/internalapi/unanswered-communication";

        try {
            return restTemplate.postForObject(url, request, UnansweredCommunicationResponse.class);
        } catch (Exception e) {
            LOG.error("Error getting unanswered communication from Webcert", e);
            return new UnansweredCommunicationResponse(null, true);
        }
    }
}
