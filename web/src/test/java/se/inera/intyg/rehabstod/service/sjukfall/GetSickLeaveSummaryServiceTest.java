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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.rehabstod.service.pu.PuService;
import se.inera.intyg.rehabstod.service.sjukfall.dto.SickLeaveSummary;
import se.inera.intyg.rehabstod.service.sjukfall.statistics.StatisticsCalculator;
import se.inera.intyg.rehabstod.web.controller.api.dto.SickLeavesFilterRequestDTO;
import se.inera.intyg.rehabstod.web.model.SjukfallEnhet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GetSickLeaveSummaryServiceTest {

    @Mock
    PuService puService;
    @Mock
    StatisticsCalculator statisticsCalculator;
    @Mock
    GetActiveSickLeavesService getActiveSickLeavesService;
    @InjectMocks
    GetSickLeaveSummaryServiceImpl getSickLeaveSummaryService;

    List<SjukfallEnhet> sickLeaves = new ArrayList<>();
    SickLeaveSummary summary =
            new SickLeaveSummary(
                    5,
                    Collections.emptyList(),
                    Collections.emptyList(),
                    Collections.emptyList(),
                    Collections.emptyList(),
                    Collections.emptyList()
            );

    @BeforeEach
    void setup() {
        doReturn(sickLeaves).when(getActiveSickLeavesService).get(any(SickLeavesFilterRequestDTO.class));
        doReturn(summary).when(statisticsCalculator).getSickLeaveSummary(sickLeaves);
    }

    @Test
    void shouldGetActiveSickLeaves() {
        getSickLeaveSummaryService.get();
        verify(getActiveSickLeavesService).get(any(SickLeavesFilterRequestDTO.class));
    }

    @Test
    void shouldFilterForSekretess() {
        getSickLeaveSummaryService.get();
        verify(puService).filterSekretessForSummary(sickLeaves);
    }

    @Test
    void shouldCalculateSickLeaveSummary() {
        getSickLeaveSummaryService.get();
        verify(statisticsCalculator).getSickLeaveSummary(sickLeaves);
    }

    @Test
    void shouldReturnResultFromStatisticsCalculator() {
        final var response = getSickLeaveSummaryService.get();
        assertEquals(response, summary);
    }
}