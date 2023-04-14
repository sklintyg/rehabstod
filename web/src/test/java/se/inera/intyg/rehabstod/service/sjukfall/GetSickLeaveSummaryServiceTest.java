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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.infra.integration.hsatk.model.legacy.Mottagning;
import se.inera.intyg.infra.integration.hsatk.model.legacy.SelectableVardenhet;
import se.inera.intyg.infra.integration.hsatk.model.legacy.Vardenhet;
import se.inera.intyg.infra.integration.hsatk.model.legacy.Vardgivare;
import se.inera.intyg.infra.logmessages.ActivityType;
import se.inera.intyg.infra.logmessages.ResourceType;
import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.auth.RehabstodUserPreferences;
import se.inera.intyg.rehabstod.auth.RehabstodUserPreferences.Preference;
import se.inera.intyg.rehabstod.integration.it.dto.SickLeavesRequestDTO;
import se.inera.intyg.rehabstod.integration.it.dto.SickLeavesResponseDTO;
import se.inera.intyg.rehabstod.integration.it.service.IntygstjanstRestIntegrationService;
import se.inera.intyg.rehabstod.service.diagnos.dto.DiagnosKapitel;
import se.inera.intyg.rehabstod.service.diagnos.dto.DiagnosKategori;
import se.inera.intyg.rehabstod.service.monitoring.MonitoringLogService;
import se.inera.intyg.rehabstod.service.pu.PuService;
import se.inera.intyg.rehabstod.service.sjukfall.dto.SjukfallSummary;
import se.inera.intyg.rehabstod.service.sjukfall.mappers.SjukfallEngineMapper;
import se.inera.intyg.rehabstod.service.sjukfall.statistics.StatisticsCalculator;
import se.inera.intyg.rehabstod.service.user.UserService;
import se.inera.intyg.rehabstod.web.controller.api.dto.SickLeavesFilterRequestDTO;
import se.inera.intyg.rehabstod.web.model.SjukfallEnhet;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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
    SjukfallSummary summary = new SjukfallSummary(5, Collections.emptyList(), Collections.emptyList(), Collections.emptyList());

    @BeforeEach
    void setup() {
        doReturn(sickLeaves).when(getActiveSickLeavesService).get(any(SickLeavesFilterRequestDTO.class));
        doReturn(summary).when(statisticsCalculator).getSjukfallSummary(sickLeaves);
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
    void shouldCalculateSjukfallSummary() {
        getSickLeaveSummaryService.get();
        verify(statisticsCalculator).getSjukfallSummary(sickLeaves);
    }

    @Test
    void shouldReturnResultFromStatisticsCalculator() {
        final var response = getSickLeaveSummaryService.get();
        assertEquals(response, summary);
    }
}
