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
package se.inera.intyg.rehabstod.infrastructure.integration.intygproxyservice.client.employee;

import static se.inera.intyg.rehabstod.infrastructure.integration.intygproxyservice.configuration.HsaRestClientConfig.LOG_SESSION_ID_HEADER;
import static se.inera.intyg.rehabstod.infrastructure.integration.intygproxyservice.configuration.HsaRestClientConfig.LOG_TRACE_ID_HEADER;
import static se.inera.intyg.rehabstod.infrastructure.integration.intygproxyservice.configuration.HsaRestClientConfig.SESSION_ID_KEY;
import static se.inera.intyg.rehabstod.infrastructure.integration.intygproxyservice.configuration.HsaRestClientConfig.TRACE_ID_KEY;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import se.inera.intyg.rehabstod.infrastructure.integration.intygproxyservice.config.properties.HsaIntygProxyServiceProperties;
import se.inera.intyg.rehabstod.infrastructure.integration.intygproxyservice.dto.employee.GetEmployeeRequestDTO;
import se.inera.intyg.rehabstod.infrastructure.integration.intygproxyservice.dto.employee.GetEmployeeResponseDTO;
import se.inera.intyg.rehabstod.logging.MdcLogConstants;
import se.inera.intyg.rehabstod.logging.PerformanceLogging;

@Service
public class HsaIntygProxyServiceEmployeeClient {

  private final RestClient ipsRestClient;
  private final String employeeEndpoint;

  public HsaIntygProxyServiceEmployeeClient(
      @Qualifier("hsaIntygProxyServiceRestClient") RestClient ipsRestClient,
      HsaIntygProxyServiceProperties props) {
    this.ipsRestClient = ipsRestClient;
    this.employeeEndpoint = props.employeeEndpoint();
  }

  @PerformanceLogging(eventAction = "get-employee", eventType = MdcLogConstants.EVENT_TYPE_ACCESSED)
  public GetEmployeeResponseDTO getEmployee(GetEmployeeRequestDTO getEmployeeRequestDTO) {
    return ipsRestClient
        .post()
        .uri(employeeEndpoint)
        .body(getEmployeeRequestDTO)
        .header(LOG_TRACE_ID_HEADER, MDC.get(TRACE_ID_KEY))
        .header(LOG_SESSION_ID_HEADER, MDC.get(SESSION_ID_KEY))
        .contentType(MediaType.APPLICATION_JSON)
        .retrieve()
        .body(GetEmployeeResponseDTO.class);
  }
}
