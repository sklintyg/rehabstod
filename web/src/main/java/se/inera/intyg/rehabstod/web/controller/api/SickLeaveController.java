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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import se.inera.intyg.rehabstod.service.sjukfall.GetActiveSickLeavesService;
import se.inera.intyg.rehabstod.service.sjukfall.GetSickLeaveSummaryService;
import se.inera.intyg.rehabstod.service.sjukfall.PopulateFiltersService;
import se.inera.intyg.rehabstod.service.sjukfall.dto.SickLeaveSummary;
import se.inera.intyg.rehabstod.web.controller.api.dto.PopulateFiltersResponseDTO;
import se.inera.intyg.rehabstod.web.controller.api.dto.SickLeavesFilterRequestDTO;
import se.inera.intyg.rehabstod.web.controller.api.dto.SickLeavesResponseDTO;

@RestController
@RequestMapping("/api/sickleaves")
public class SickLeaveController {

    @Autowired
    private GetActiveSickLeavesService getActiveSickLeavesService;
    @Autowired
    private GetSickLeaveSummaryService getSickLeaveSummaryService;

    @Autowired
    private PopulateFiltersService populateFiltersService;

    private static final boolean INCLUDE_PARAMETERS = true;

    @RequestMapping(value = "/active", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public SickLeavesResponseDTO getSickLeavesForUnit(@RequestBody SickLeavesFilterRequestDTO request) {
        return new SickLeavesResponseDTO(getActiveSickLeavesService.get(request, INCLUDE_PARAMETERS));
    }

    @RequestMapping(value = "/filters", method = RequestMethod.GET)
    public PopulateFiltersResponseDTO populateFilters() {
        return populateFiltersService.get();
    }

    @RequestMapping(value = "/summary", method = RequestMethod.GET)
    public SickLeaveSummary getSummary() {
        return getSickLeaveSummaryService.get();
    }

}
