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

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import se.inera.intyg.rehabstod.service.filter.PopulateFiltersService;
import se.inera.intyg.rehabstod.service.sjukfall.dto.PopulateLUFilterResponseDTO;

@RestController
@RequestMapping("/api/lu")
public class LUController {

    private final PopulateFiltersService populateFiltersService;

    public LUController(PopulateFiltersService populateFiltersService) {
        this.populateFiltersService = populateFiltersService;
    }

    @RequestMapping(value = "/filters", method = RequestMethod.GET)
    public PopulateLUFilterResponseDTO populateFilters() {
        return populateFiltersService.populateLUFilters();
    }
}
