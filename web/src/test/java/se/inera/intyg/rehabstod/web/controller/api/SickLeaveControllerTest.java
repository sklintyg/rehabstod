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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.rehabstod.service.filter.PopulateFiltersService;
import se.inera.intyg.rehabstod.service.sjukfall.GetActiveSickLeavesResponseService;
import se.inera.intyg.rehabstod.service.sjukfall.GetSickLeaveSummaryService;
import se.inera.intyg.rehabstod.service.sjukfall.PdlLogSickLeavesService;
import se.inera.intyg.rehabstod.service.sjukfall.dto.GetActiveSickLeavesResponseDTO;
import se.inera.intyg.rehabstod.service.sjukfall.dto.PopulateSickLeaveFilterResponseDTO;
import se.inera.intyg.rehabstod.web.controller.api.dto.SickLeavePrintRequestDTO;
import se.inera.intyg.rehabstod.web.controller.api.dto.SickLeavesFilterRequestDTO;
import se.inera.intyg.rehabstod.web.model.SjukfallEnhet;

@ExtendWith(MockitoExtension.class)
public class SickLeaveControllerTest {

    @Mock
    private GetActiveSickLeavesResponseService getActiveSickLeavesResponseService;
    @Mock
    private PopulateFiltersService populateFiltersService;
    @Mock
    private GetSickLeaveSummaryService getSickLeaveSummaryService;
    @Mock
    private PdlLogSickLeavesService pdlLogSickLeavesService;

    @InjectMocks
    private SickLeaveController sickLeaveController = new SickLeaveController();
    private static final String TEXT_SEARCH = "textSearch";
    private static final String UNANSWERED_COMMUNICATION = "uac";

    @Test
    void shouldCallGetActiveSickLeavesService() {
        final var expectedRequest =
            getExpectedRequest();
        when(getActiveSickLeavesResponseService.get(any(), anyBoolean(), anyBoolean())).thenReturn(
            new GetActiveSickLeavesResponseDTO(Collections.emptyList(), true, true)
        );
        sickLeaveController.getSickLeavesForUnit(expectedRequest);
        verify(getActiveSickLeavesResponseService).get(expectedRequest, true, true);
    }

    @Test
    void shouldConvertContentInResponse() {
        final var expectedRequest = getExpectedRequest();

        final var expectedResponse = new GetActiveSickLeavesResponseDTO(Collections.emptyList(), true, true);
        when(getActiveSickLeavesResponseService.get(any(), anyBoolean(), anyBoolean())).thenReturn(expectedResponse);

        final var response = sickLeaveController.getSickLeavesForUnit(expectedRequest);

        assertEquals(expectedResponse.getContent(), response.getContent());
    }

    @Test
    void shouldConvertSRSErrorInResponse() {
        final var expectedRequest = getExpectedRequest();

        final var expectedResponse = new GetActiveSickLeavesResponseDTO(Collections.emptyList(), true, false);
        when(getActiveSickLeavesResponseService.get(any(), anyBoolean(), anyBoolean())).thenReturn(expectedResponse);

        final var response = sickLeaveController.getSickLeavesForUnit(expectedRequest);

        assertEquals(expectedResponse.isSrsError(), response.isSrsError());
    }

    @Test
    void shouldSetIncludeParametersToFalse() {
        final var includeParametersCaptur = ArgumentCaptor.forClass(boolean.class);
        final var expectedResponse = new GetActiveSickLeavesResponseDTO(Collections.emptyList(), true, false);
        when(getActiveSickLeavesResponseService.get(any(), anyBoolean(), anyBoolean())).thenReturn(expectedResponse);
        sickLeaveController.getSickLeavesForUnit(getExpectedRequest());
        verify(getActiveSickLeavesResponseService).get(any(SickLeavesFilterRequestDTO.class), includeParametersCaptur.capture(),
            any(boolean.class));
        assertTrue(includeParametersCaptur.getValue());
    }

    @Test
    void shouldSetShouldPdlLogToFalse() {
        final var pdlLogArgumentCaptor = ArgumentCaptor.forClass(boolean.class);
        final var expectedResponse = new GetActiveSickLeavesResponseDTO(Collections.emptyList(), true, false);
        when(getActiveSickLeavesResponseService.get(any(), anyBoolean(), anyBoolean())).thenReturn(expectedResponse);
        sickLeaveController.getSickLeavesForUnit(getExpectedRequest());
        verify(getActiveSickLeavesResponseService).get(any(SickLeavesFilterRequestDTO.class), any(boolean.class),
            pdlLogArgumentCaptor.capture());
        assertTrue(pdlLogArgumentCaptor.getValue());
    }

    @Test
    void shouldConvertUnansweredCommunicationErrorInResponse() {
        final var expectedRequest = getExpectedRequest();
        final var expectedResponse = new GetActiveSickLeavesResponseDTO(Collections.emptyList(), false, true);
        when(getActiveSickLeavesResponseService.get(any(), anyBoolean(), anyBoolean())).thenReturn(expectedResponse);

        final var response = sickLeaveController.getSickLeavesForUnit(expectedRequest);

        assertEquals(expectedResponse.isUnansweredCommunicationError(), response.isUnansweredCommunicationError());
    }

    private static SickLeavesFilterRequestDTO getExpectedRequest() {
        return new SickLeavesFilterRequestDTO(
            Collections.singletonList("doctorId"),
            Collections.emptyList(),
            Collections.emptyList(),
            1,
            150,
            LocalDate.now(),
            LocalDate.now(),
            Collections.emptyList(),
            Collections.emptyList(),
            TEXT_SEARCH,
            UNANSWERED_COMMUNICATION
        );
    }

    @Nested
    class GetPopulatedFiltersService {

        PopulateSickLeaveFilterResponseDTO expectedResponse;

        @BeforeEach
        void setup() {
            expectedResponse = new PopulateSickLeaveFilterResponseDTO(
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                10,
                true,
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                true
            );

            when(populateFiltersService.populateSickLeaveFilters()).thenReturn(expectedResponse);
        }

        @Test
        void shouldCallPopulateFiltersService() {
            sickLeaveController.populateFilters();
            verify(populateFiltersService).populateSickLeaveFilters();
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
        void shouldTransformResponseHasOngoingSickLeaves() {
            final var response = sickLeaveController.populateFilters();
            assertEquals(expectedResponse.isHasOngoingSickLeaves(), response.isHasOngoingSickLeaves());
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

        @Test
        void shouldTransformResponseUnansweredCommunicationFilterTypes() {
            final var response = sickLeaveController.populateFilters();
            assertEquals(expectedResponse.getUnansweredCommunicationFilterTypes(),
                response.getUnansweredCommunicationFilterTypes());
        }
    }

    @Nested
    class Print {

        @Test
        void shouldLogPrintWithSickLeavesFromRequest() {
            final var request = SickLeavePrintRequestDTO.builder()
                .sickLeaves(List.of(new SjukfallEnhet()))
                .build();
            sickLeaveController.print(request);
            verify(pdlLogSickLeavesService).logPrint(request.getSickLeaves());
        }
    }

    @Test
    void shouldTransformPopulateFiltersResponse() {
        final var expectedResponse = new PopulateSickLeaveFilterResponseDTO(
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.emptyList(),
            10,
            true,
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.emptyList(),
            true
        );
        when(populateFiltersService.populateSickLeaveFilters()).thenReturn(expectedResponse);

        final var actualResponse = sickLeaveController.populateFilters();

        assertEquals(expectedResponse.getActiveDoctors(), actualResponse.getActiveDoctors());
        assertEquals(expectedResponse.getAllDiagnosisChapters(), actualResponse.getAllDiagnosisChapters());
        assertEquals(expectedResponse.getEnabledDiagnosisChapters(), actualResponse.getEnabledDiagnosisChapters());
        assertEquals(expectedResponse.getNbrOfSickLeaves(), actualResponse.getNbrOfSickLeaves());
        assertEquals(expectedResponse.getRekoStatusTypes(), actualResponse.getRekoStatusTypes());
        assertEquals(expectedResponse.getOccupationTypes(), actualResponse.getOccupationTypes());
        assertEquals(expectedResponse.getUnansweredCommunicationFilterTypes(),
            actualResponse.getUnansweredCommunicationFilterTypes());
        assertEquals(expectedResponse.isSrsActivated(), actualResponse.isSrsActivated());
        assertEquals(expectedResponse.isHasOngoingSickLeaves(), actualResponse.isHasOngoingSickLeaves());
    }

    @Test
    void shouldCallGetSickLeaveSummaryService() {
        sickLeaveController.getSummary();
        verify(getSickLeaveSummaryService).get();
    }
}
