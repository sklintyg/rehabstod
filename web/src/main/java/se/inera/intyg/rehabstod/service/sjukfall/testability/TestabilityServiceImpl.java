/*
 * Copyright (C) 2023 Inera AB (http://www.inera.se)
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

package se.inera.intyg.rehabstod.service.sjukfall.testability;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import se.inera.intyg.rehabstod.integration.it.dto.CreateSickLeaveRequestDTO;
import se.inera.intyg.rehabstod.integration.it.dto.CreateSickLeaveResponseDTO;
import se.inera.intyg.rehabstod.integration.it.dto.TestDataOptionsDTO;

@Service
public class TestabilityServiceImpl implements TestabilityService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestabilityServiceImpl.class);
    private final RestTemplate restTemplate;
    @Value("${intygstjanst.host.url}")
    private String intygstjanstUrl;

    public TestabilityServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String getDefaultTestData() {
        final String url = intygstjanstUrl + "/inera-certificate/resources/testability/createDefault";
        LOGGER.debug("Getting default test data from Intygstjansten");
        return restTemplate.postForObject(url, null, String.class);
    }

    @Override
    public String createSickleave(CreateSickLeaveRequestDTO request) {
        final String url = intygstjanstUrl + "/inera-certificate/resources/testability/createSickLeave";
        LOGGER.debug("Creating sick leave test data");
        final var createSickLeaveResponseDTO = restTemplate.postForObject(url, request, CreateSickLeaveResponseDTO.class);
        return createSickLeaveResponseDTO.getCertificateId();
    }

    @Override
    public TestDataOptionsDTO getTestDataOptions() {
        final String url = intygstjanstUrl + "/inera-certificate/resources/testability/testDataOptions";
        LOGGER.debug("Getting sick leave test data");
        return restTemplate.getForObject(url, TestDataOptionsDTO.class);
    }
}
