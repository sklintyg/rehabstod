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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cglib.core.Local;
import se.inera.intyg.rehabstod.service.sjukfall.GetActiveSickLeavesService;
import se.inera.intyg.rehabstod.service.sjukfall.GetSickLeaveSummaryService;
import se.inera.intyg.rehabstod.service.sjukfall.PopulateFiltersService;
import se.inera.intyg.rehabstod.service.sjukfall.SetRekoStatusToSickLeaveService;
import se.inera.intyg.rehabstod.service.sjukfall.dto.PopulateFiltersResponseDTO;
import se.inera.intyg.rehabstod.web.controller.api.dto.SetRekoStatusToSickLeaveRequestDTO;
import se.inera.intyg.rehabstod.web.controller.api.dto.SickLeavesFilterRequestDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RekoControllerTest {

    @Mock
    private SetRekoStatusToSickLeaveService setRekoStatusToSickLeaveService;

    @InjectMocks
    private RekoController rekoController;

    private static final String PATIENT_ID = "PATIENT_ID";
    private static final String STATUS = "STATUS";
    private static final LocalDate TIMESTAMP = LocalDate.now();


    @Test
    void shouldCallSetRekoStatusServiceWithCorrectPatientId() {
        final var expectedRequest = new SetRekoStatusToSickLeaveRequestDTO(PATIENT_ID, STATUS, TIMESTAMP.toString());
        final var captor = ArgumentCaptor.forClass(String.class);

        rekoController.getSickLeavesForUnit(expectedRequest);
        verify(setRekoStatusToSickLeaveService).set(captor.capture(), anyString(), any(LocalDateTime.class));

        assertEquals(PATIENT_ID, captor.getValue());
    }

    @Test
    void shouldCallSetRekoStatusServiceWithCorrectStatus() {
        final var expectedRequest =
                new SetRekoStatusToSickLeaveRequestDTO(PATIENT_ID, STATUS, TIMESTAMP.toString());
        final var captor = ArgumentCaptor.forClass(String.class);

        rekoController.getSickLeavesForUnit(expectedRequest);
        verify(setRekoStatusToSickLeaveService).set(anyString(), captor.capture(), any(LocalDateTime.class));

        assertEquals(STATUS, captor.getValue());
    }

    @Test
    void shouldCallSetRekoStatusServiceWithCorrectTimestamp() {
        final var expectedRequest =
                new SetRekoStatusToSickLeaveRequestDTO(PATIENT_ID, STATUS, TIMESTAMP.toString());
        final var captor = ArgumentCaptor.forClass(LocalDateTime.class);

        rekoController.getSickLeavesForUnit(expectedRequest);
        verify(setRekoStatusToSickLeaveService).set(anyString(), anyString(), captor.capture());

        assertEquals(TIMESTAMP.atStartOfDay(), captor.getValue());
    }

}
