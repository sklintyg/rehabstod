/*
 * Copyright (C) 2025 Inera AB (http://www.inera.se)
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

import static se.inera.intyg.rehabstod.logging.MdcHelper.LOG_SESSION_ID_HEADER;
import static se.inera.intyg.rehabstod.logging.MdcHelper.LOG_TRACE_ID_HEADER;
import static se.inera.intyg.rehabstod.logging.MdcLogConstants.EVENT_TYPE_ACCESSED;
import static se.inera.intyg.rehabstod.logging.MdcLogConstants.SESSION_ID_KEY;
import static se.inera.intyg.rehabstod.logging.MdcLogConstants.TRACE_ID_KEY;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import se.inera.intyg.rehabstod.integration.wc.service.dto.UnansweredCommunicationRequest;
import se.inera.intyg.rehabstod.integration.wc.service.dto.UnansweredCommunicationResponse;
import se.inera.intyg.rehabstod.logging.PerformanceLogging;

@Service
@RequiredArgsConstructor
public class WcRestIntegrationServiceImpl implements WcRestIntegrationService {

    private static final Logger LOG = LoggerFactory.getLogger(WcRestIntegrationServiceImpl.class);

    private final RestClient wcRestClient;

    @Value("${integration.webcert.scheme}")
    private String scheme;
    @Value("${integration.webcert.baseurl}")
    private String baseUrl;
    @Value("${integration.webcert.port}")
    private int port;

    @Override
    @PerformanceLogging(eventAction = "get-unanswered-communication-for-patients", eventType = EVENT_TYPE_ACCESSED)
    public UnansweredCommunicationResponse getUnansweredCommunicationForPatients(UnansweredCommunicationRequest request) {
        final var url = "/internalapi/unanswered-communication";

        try {
            return wcRestClient
                .post()
                .uri(uriBuilder -> uriBuilder
                    .scheme(scheme)
                    .host(baseUrl)
                    .port(port)
                    .path(url)
                    .build()
                )
                .body(request)
                .header(LOG_TRACE_ID_HEADER, MDC.get(TRACE_ID_KEY))
                .header(LOG_SESSION_ID_HEADER, MDC.get(SESSION_ID_KEY))
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(UnansweredCommunicationResponse.class);
        } catch (Exception e) {
            LOG.error("Error getting unanswered communication from Webcert", e);
            return new UnansweredCommunicationResponse(null, true);
        }
    }
}
