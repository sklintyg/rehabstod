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

package se.inera.intyg.rehabstod.web.controller.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.rehabstod.service.diagnos.dto.DiagnosKapitel;
import se.inera.intyg.rehabstod.service.filter.PopulateFiltersService;
import se.inera.intyg.rehabstod.service.sjukfall.dto.PopulateLUFilterResponseDTO;
import se.inera.intyg.rehabstod.web.model.Lakare;

@ExtendWith(MockitoExtension.class)
class LUControllerTest {

    @Mock
    private PopulateFiltersService populateFiltersService;

    @InjectMocks
    private LUController luController;

    @Test
    void shouldCallPopulateFiltersService() {
        luController.populateFilters();
        verify(populateFiltersService).populateLUFilters();
    }

    @Test
    void shouldReturnEmptyFilters() {
        final var expectedResponse = PopulateLUFilterResponseDTO.builder()
            .allDiagnosisChapters(Collections.emptyList())
            .doctors(Collections.emptyList())
            .build();
        when(populateFiltersService.populateLUFilters()).thenReturn(expectedResponse);
        final var response = luController.populateFilters();
        assertEquals(expectedResponse, response);
    }


    @Test
    void shouldReturnPopulatedFilters() {
        final var expectedResponse = PopulateLUFilterResponseDTO.builder()
            .allDiagnosisChapters(List.of(new DiagnosKapitel()))
            .doctors(List.of(new Lakare()))
            .build();
        when(populateFiltersService.populateLUFilters()).thenReturn(expectedResponse);
        final var response = luController.populateFilters();
        assertEquals(expectedResponse, response);
    }
}
