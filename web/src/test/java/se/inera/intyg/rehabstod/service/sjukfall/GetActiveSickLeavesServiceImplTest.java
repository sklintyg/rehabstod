/*
 * Copyright (C) 2025 Inera AB (http://www.inera.se)
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.infra.sjukfall.dto.SjukfallEnhet;
import se.inera.intyg.rehabstod.integration.it.dto.SickLeavesRequestDTO;
import se.inera.intyg.rehabstod.integration.it.dto.SickLeavesResponseDTO;
import se.inera.intyg.rehabstod.integration.it.service.IntygstjanstRestIntegrationService;
import se.inera.intyg.rehabstod.service.sjukfall.mappers.SjukfallEngineMapper;

@ExtendWith(MockitoExtension.class)
class GetActiveSickLeavesServiceImplTest {

    @Mock
    private IntygstjanstRestIntegrationService intygstjanstRestIntegrationService;
    @Mock
    private SjukfallEngineMapper sjukfallEngineMapper;
    @InjectMocks
    private GetActiveSickLeavesServiceImpl getSickLeavesService;

    private SickLeavesRequestDTO request;
    private SickLeavesResponseDTO response;

    @BeforeEach
    void setUp() {
        response = getSickLeavesResponseDTO();
        request = getSickLeavesRequestDTO(5);
        when(intygstjanstRestIntegrationService.getActiveSickLeaves(request)).thenReturn(response);
    }

    @Test
    void shouldReturnActiveSickLeaves() {
        final var expectedResult = new se.inera.intyg.rehabstod.web.model.SjukfallEnhet();
        when(sjukfallEngineMapper.mapToSjukfallEnhetDto(
            response.getContent().get(0),
            request.getMaxDaysSinceSickLeaveCompleted(),
            LocalDate.now()))
            .thenReturn(expectedResult);
        final var result = getSickLeavesService.get(request);
        assertEquals(List.of(expectedResult), result);
    }

    @Nested
    class Request {

        @Test
        void shouldUseSickLeave() {
            final var captor = ArgumentCaptor.forClass(SjukfallEnhet.class);
            getSickLeavesService.get(request);
            verify(sjukfallEngineMapper).mapToSjukfallEnhetDto(captor.capture(), anyInt(), any(LocalDate.class));
            assertEquals(response.getContent().get(0), captor.getValue());
        }

        @Test
        void shouldUseMaxDaysFromRequest() {
            final var expectedResult = 5;
            final var captor = ArgumentCaptor.forClass(Integer.class);
            getSickLeavesService.get(request);
            verify(sjukfallEngineMapper).mapToSjukfallEnhetDto(any(SjukfallEnhet.class), captor.capture(), any(LocalDate.class));
            assertEquals(expectedResult, captor.getValue());
        }

        @Test
        void shouldUseLocalDateNow() {
            final var expectedResult = LocalDate.now();
            final var captor = ArgumentCaptor.forClass(LocalDate.class);
            getSickLeavesService.get(request);
            verify(sjukfallEngineMapper).mapToSjukfallEnhetDto(any(SjukfallEnhet.class), anyInt(), captor.capture());
            assertEquals(expectedResult, captor.getValue());
        }
    }

    private static SickLeavesResponseDTO getSickLeavesResponseDTO() {
        final var sickLeave = new SjukfallEnhet();
        return SickLeavesResponseDTO.builder()
            .content(List.of(sickLeave))
            .build();
    }

    private static SickLeavesRequestDTO getSickLeavesRequestDTO(int maxDaysSinceSickLeaveCompleted) {
        return SickLeavesRequestDTO.builder()
            .maxDaysSinceSickLeaveCompleted(maxDaysSinceSickLeaveCompleted)
            .build();
    }
}
