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

import java.util.ArrayList;
import org.springframework.stereotype.Service;
import se.inera.intyg.rehabstod.service.pu.PuService;
import se.inera.intyg.rehabstod.service.sjukfall.dto.SjukfallSummary;
import se.inera.intyg.rehabstod.service.sjukfall.statistics.StatisticsCalculator;
import se.inera.intyg.rehabstod.web.controller.api.dto.SickLeavesFilterRequestDTO;

@Service
public class GetSickLeaveSummaryServiceImpl implements GetSickLeaveSummaryService {

    private final PuService puService;
    private final StatisticsCalculator statisticsCalculator;
    private final GetActiveSickLeavesService getActiveSickLeavesService;

    public GetSickLeaveSummaryServiceImpl(PuService puService, StatisticsCalculator statisticsCalculator,
        GetActiveSickLeavesService getActiveSickLeavesService) {
        this.puService = puService;
        this.statisticsCalculator = statisticsCalculator;
        this.getActiveSickLeavesService = getActiveSickLeavesService;
    }

    @Override
    public SjukfallSummary get() {
        final var sickLeaves = getActiveSickLeavesService.get(
            new SickLeavesFilterRequestDTO(new ArrayList<>(), 365, 1, new ArrayList<>())
        );
        puService.filterSekretessForSummary(sickLeaves);
        return statisticsCalculator.getSjukfallSummary(sickLeaves);
    }
}
