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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.rehabstod.service.sjukfall.GetActiveSickLeavesService;
import se.inera.intyg.rehabstod.service.sjukfall.GetSickLeaveSummaryService;
import se.inera.intyg.rehabstod.service.sjukfall.PopulateFiltersService;
import se.inera.intyg.rehabstod.service.sjukfall.dto.GetActiveSickLeavesResponseDTO;
import se.inera.intyg.rehabstod.service.sjukfall.dto.PopulateFiltersResponseDTO;
import se.inera.intyg.rehabstod.web.controller.api.dto.SickLeavesFilterRequestDTO;

@ExtendWith(MockitoExtension.class)
public class SickLeaveControllerTest {

    @Mock
    private GetActiveSickLeavesService getActiveSickLeavesService;
    @Mock
    private PopulateFiltersService populateFiltersService;
    @Mock
    private GetSickLeaveSummaryService getSickLeaveSummaryService;

    @InjectMocks
    private SickLeaveController sickLeaveController = new SickLeaveController();

    @Test
    void shouldCallGetActiveSickLeavesService() {
        final var expectedRequest =
            new SickLeavesFilterRequestDTO(
                Collections.singletonList("doctorId"),
                Collections.emptyList(),
                Collections.emptyList(),
                1,
                150,
                LocalDate.now(),
                LocalDate.now(),
                Collections.emptyList(),
                Collections.emptyList()
            );
        when(getActiveSickLeavesService.get(any(), anyBoolean())).thenReturn(
                new GetActiveSickLeavesResponseDTO(Collections.emptyList(), true)
        );
        sickLeaveController.getSickLeavesForUnit(expectedRequest);
        verify(getActiveSickLeavesService).get(expectedRequest, true);
    }

    @Test
    void shouldConvertContentInResponse() {
        final var expectedRequest =
                new SickLeavesFilterRequestDTO(
                        Collections.singletonList("doctorId"),
                        Collections.emptyList(),
                        Collections.emptyList(),
                        1,
                        150,
                        LocalDate.now(),
                        LocalDate.now(),
                        Collections.emptyList(),
                        Collections.emptyList()
                );

        final var expectedResponse = new GetActiveSickLeavesResponseDTO(Collections.emptyList(), true);
        when(getActiveSickLeavesService.get(any(), anyBoolean())).thenReturn(expectedResponse);

        final var response = sickLeaveController.getSickLeavesForUnit(expectedRequest);

        assertEquals(expectedResponse.getContent(), response.getContent());
    }

    @Test
    void shouldConvertSRSErrorInResponse() {
        final var expectedRequest =
                new SickLeavesFilterRequestDTO(
                        Collections.singletonList("doctorId"),
                        Collections.emptyList(),
                        Collections.emptyList(),
                        1,
                        150,
                        LocalDate.now(),
                        LocalDate.now(),
                        Collections.emptyList(),
                        Collections.emptyList()
                );

        final var expectedResponse = new GetActiveSickLeavesResponseDTO(Collections.emptyList(), true);
        when(getActiveSickLeavesService.get(any(), anyBoolean())).thenReturn(expectedResponse);

        final var response = sickLeaveController.getSickLeavesForUnit(expectedRequest);

        assertEquals(expectedResponse.isSrsError(), response.isSrsError());
    }

    @Nested
    class GetPopulatedFiltersService {

        PopulateFiltersResponseDTO expectedResponse;

        @BeforeEach
        void setup() {
            expectedResponse = new PopulateFiltersResponseDTO(
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                10,
                Collections.emptyList(),
                Collections.emptyList()
            );

            when(populateFiltersService.get()).thenReturn(expectedResponse);
        }

        @Test
        void shouldCallPopulateFiltersService() {
            sickLeaveController.populateFilters();
            verify(populateFiltersService).get();
        }

        @Test
        void shouldTransformResponseDoctors() {
            final var response = sickLeaveController.populateFilters();
            assertEquals(expectedResponse.getActiveDoctors(), response.getActiveDoctors());
        }

        @Test
        void shouldTransformResponseAllDiagnoses() {
            final var response = sickLeaveController.populateFilters();
            assertEquals(expectedResponse.getAllDiagnosisChapters(), response.getAllDiagnosisChapters());
        }

        @Test
        void shouldTransformResponseEnabledDiagnoses() {
            final var response = sickLeaveController.populateFilters();
            assertEquals(expectedResponse.getEnabledDiagnosisChapters(), response.getEnabledDiagnosisChapters());
        }

        @Test
        void shouldTransformResponseTotalNbrOfSickLeaves() {
            final var response = sickLeaveController.populateFilters();
            assertEquals(expectedResponse.getNbrOfSickLeaves(), response.getNbrOfSickLeaves());
        }

        @Test
        void shouldTransformResponseRekoStatusTypes() {
            final var response = sickLeaveController.populateFilters();
            assertEquals(expectedResponse.getRekoStatusTypes(), response.getRekoStatusTypes());
        }

        @Test
        void shouldTransformResponseOccupationsStatusTypes() {
            final var response = sickLeaveController.populateFilters();
            assertEquals(expectedResponse.getOccupationTypes(), response.getOccupationTypes());
        }
    }

    @Test
    void shouldTransformPopulateFiltersResponse() {
        final var expectedResponse = new PopulateFiltersResponseDTO(
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.emptyList(),
            10,
            Collections.emptyList(),
            Collections.emptyList()
        );
        when(populateFiltersService.get()).thenReturn(expectedResponse);

        final var actualResponse = sickLeaveController.populateFilters();

        assertEquals(expectedResponse.getActiveDoctors(), actualResponse.getActiveDoctors());
        assertEquals(expectedResponse.getAllDiagnosisChapters(), actualResponse.getAllDiagnosisChapters());
        assertEquals(expectedResponse.getEnabledDiagnosisChapters(), actualResponse.getEnabledDiagnosisChapters());
        assertEquals(expectedResponse.getNbrOfSickLeaves(), actualResponse.getNbrOfSickLeaves());
        assertEquals(expectedResponse.getRekoStatusTypes(), actualResponse.getRekoStatusTypes());
        assertEquals(expectedResponse.getOccupationTypes(), actualResponse.getOccupationTypes());
    }

    @Test
    void shouldCallGetSickLeaveSummaryService() {
        sickLeaveController.getSummary();
        verify(getSickLeaveSummaryService).get();
    }
}
