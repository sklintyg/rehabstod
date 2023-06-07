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

package se.inera.intyg.rehabstod.service.sjukfall;

import java.util.Collections;
import org.springframework.stereotype.Service;
import se.inera.intyg.rehabstod.service.pu.PuService;
import se.inera.intyg.rehabstod.service.sjukfall.dto.SickLeaveSummary;
import se.inera.intyg.rehabstod.service.sjukfall.statistics.StatisticsCalculator;
import se.inera.intyg.rehabstod.web.controller.api.dto.SickLeavesFilterRequestDTO;

@Service
public class GetSickLeaveSummaryServiceImpl implements GetSickLeaveSummaryService {

    private final PuService puService;
    private final StatisticsCalculator statisticsCalculator;
    private final GetActiveSickLeavesService getActiveSickLeavesService;
    private static final boolean INCLUDE_PARAMETERS = false;
    private static final String TEXT_SEARCH = "textSearch";

    public GetSickLeaveSummaryServiceImpl(PuService puService, StatisticsCalculator statisticsCalculator,
        GetActiveSickLeavesService getActiveSickLeavesService) {
        this.puService = puService;
        this.statisticsCalculator = statisticsCalculator;
        this.getActiveSickLeavesService = getActiveSickLeavesService;
    }

    @Override
    public SickLeaveSummary get() {
        final var sickLeaves = getActiveSickLeavesService.get(
            new SickLeavesFilterRequestDTO(
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                null,
                null,
                null,
                null,
                Collections.emptyList(),
                Collections.emptyList(),
                null
            ),
            INCLUDE_PARAMETERS
        ).getContent();
        puService.filterSekretessForSummary(sickLeaves);
        return statisticsCalculator.getSickLeaveSummary(sickLeaves);
    }
}
