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

package se.inera.intyg.rehabstod.web.controller.api;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import se.inera.intyg.rehabstod.integration.it.dto.CreateSickLeaveRequestDTO;
import se.inera.intyg.rehabstod.integration.it.dto.TestDataOptionsDTO;
import se.inera.intyg.rehabstod.service.sjukfall.testability.TestabilityService;
import se.inera.intyg.rehabstod.web.controller.api.dto.TestabilityResponseDTO;

@RestController
@RequestMapping("/api/testability")
public class TestabilityController {

    private final TestabilityService testabilityService;

    public TestabilityController(TestabilityService testabilityService) {
        this.testabilityService = testabilityService;
    }

    @RequestMapping(value = "/createDefault", method = RequestMethod.POST)
    public TestabilityResponseDTO createDefaultTestData() {
        return new TestabilityResponseDTO(testabilityService.getDefaultTestData());
    }

    @RequestMapping(value = "/createSickLeave", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public TestabilityResponseDTO createSickLeave(@RequestBody CreateSickLeaveRequestDTO request) {
        return new TestabilityResponseDTO(testabilityService.createSickleave(request));
    }

    @RequestMapping(value = "/testDataOptions", method = RequestMethod.GET)
    public TestDataOptionsDTO getTestDataOptions() {
        return testabilityService.getTestDataOptions();
    }
}
