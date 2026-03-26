/*
 * Copyright (C) 2026 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.infrastructure.integration.pu.intygproxyservice.client;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import se.inera.intyg.rehabstod.infrastructure.config.properties.PuIntygProxyServiceProperties;
import se.inera.intyg.rehabstod.infrastructure.integration.pu.intygproxyservice.configuration.PURestClientConfig;
import se.inera.intyg.rehabstod.infrastructure.integration.pu.intygproxyservice.dto.PersonsRequestDTO;
import se.inera.intyg.rehabstod.infrastructure.integration.pu.intygproxyservice.dto.PersonsResponseDTO;
import se.inera.intyg.rehabstod.logging.MdcLogConstants;
import se.inera.intyg.rehabstod.logging.PerformanceLogging;

@Service
public class GetPersonsIntygProxyServiceClient {

  private final RestClient ipsRestClient;
  private final String personsEndpoint;

  public GetPersonsIntygProxyServiceClient(
      @Qualifier("puIntygProxyServiceRestClient") RestClient ipsRestClient,
      PuIntygProxyServiceProperties props) {
    this.ipsRestClient = ipsRestClient;
    this.personsEndpoint = props.personsEndpoint();
  }

  @PerformanceLogging(eventAction = "get-persons", eventType = MdcLogConstants.EVENT_TYPE_ACCESSED)
  public PersonsResponseDTO get(PersonsRequestDTO request) {
    return ipsRestClient
        .post()
        .uri(personsEndpoint)
        .body(request)
        .header(PURestClientConfig.LOG_TRACE_ID_HEADER, MDC.get(PURestClientConfig.TRACE_ID_KEY))
        .header(
            PURestClientConfig.LOG_SESSION_ID_HEADER, MDC.get(PURestClientConfig.SESSION_ID_KEY))
        .contentType(MediaType.APPLICATION_JSON)
        .retrieve()
        .body(PersonsResponseDTO.class);
  }
}