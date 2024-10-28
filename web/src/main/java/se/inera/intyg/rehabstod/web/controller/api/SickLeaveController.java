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
package se.inera.intyg.rehabstod.web.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import se.inera.intyg.rehabstod.logging.MdcLogConstants;
import se.inera.intyg.rehabstod.logging.PerformanceLogging;
import se.inera.intyg.rehabstod.service.filter.PopulateFiltersService;
import se.inera.intyg.rehabstod.service.sjukfall.GetActiveSickLeavesResponseService;
import se.inera.intyg.rehabstod.service.sjukfall.GetSickLeaveSummaryService;
import se.inera.intyg.rehabstod.service.sjukfall.PdlLogSickLeavesService;
import se.inera.intyg.rehabstod.service.sjukfall.dto.SickLeaveSummary;
import se.inera.intyg.rehabstod.web.controller.api.dto.PopulateFiltersResponseDTO;
import se.inera.intyg.rehabstod.web.controller.api.dto.SickLeavePrintRequestDTO;
import se.inera.intyg.rehabstod.web.controller.api.dto.SickLeavesFilterRequestDTO;
import se.inera.intyg.rehabstod.web.controller.api.dto.SickLeavesResponseDTO;

@RestController
@RequestMapping("/api/sickleaves")
public class SickLeaveController {

    @Autowired
    private GetActiveSickLeavesResponseService getActiveSickLeavesResponseService;
    @Autowired
    private GetSickLeaveSummaryService getSickLeaveSummaryService;
    @Autowired
    private PopulateFiltersService populateFiltersService;
    @Autowired
    private PdlLogSickLeavesService pdlLogSickLeavesService;

    private static final boolean INCLUDE_PARAMETERS = true;
    private static final boolean SHOULD_PDL_LOG = true;

    @RequestMapping(value = "/active", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PerformanceLogging(eventAction = "get-sick-leaves-for-unit", eventType = MdcLogConstants.EVENT_TYPE_ACCESSED)
    public SickLeavesResponseDTO getSickLeavesForUnit(@RequestBody SickLeavesFilterRequestDTO request) {
        final var response = getActiveSickLeavesResponseService.get(request, INCLUDE_PARAMETERS, SHOULD_PDL_LOG);
        return new SickLeavesResponseDTO(
            response.getContent(),
            response.isSrsError(),
            response.isUnansweredCommunicationError()
        );
    }

    @RequestMapping(value = "/filters", method = RequestMethod.GET)
    @PerformanceLogging(eventAction = "populate-sick-leave-filters", eventType = MdcLogConstants.EVENT_TYPE_CHANGE)
    public PopulateFiltersResponseDTO populateFilters() {
        final var response = populateFiltersService.populateSickLeaveFilters();
        return new PopulateFiltersResponseDTO(
            response.getActiveDoctors(),
            response.getAllDiagnosisChapters(),
            response.getEnabledDiagnosisChapters(),
            response.getNbrOfSickLeaves(),
            response.isHasOngoingSickLeaves(),
            response.getRekoStatusTypes(),
            response.getOccupationTypes(),
            response.getUnansweredCommunicationFilterTypes(),
            response.isSrsActivated()
        );
    }

    @RequestMapping(value = "/summary", method = RequestMethod.GET)
    @PerformanceLogging(eventAction = "get-sick-leaves-summary", eventType = MdcLogConstants.EVENT_TYPE_ACCESSED)
    public SickLeaveSummary getSummary() {
        return getSickLeaveSummaryService.get();
    }

    @PostMapping(value = "/print", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PerformanceLogging(eventAction = "print-sick-leaves", eventType = MdcLogConstants.EVENT_TYPE_ACCESSED)
    public void print(@RequestBody SickLeavePrintRequestDTO sickLeavesPrintRequest) {
        pdlLogSickLeavesService.logPrint(sickLeavesPrintRequest.getSickLeaves());
    }
}
