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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.rehabstod.service.sjukfall.CreateRekoStatusService;
import se.inera.intyg.rehabstod.service.sjukfall.GetRekoStatusService;
import se.inera.intyg.rehabstod.web.controller.api.dto.CreateRekoStatusRequestDTO;
import se.inera.intyg.rehabstod.web.controller.api.dto.GetRekoStatusRequestDTO;

@ExtendWith(MockitoExtension.class)
public class RekoControllerTest {

    @Mock
    private CreateRekoStatusService createRekoStatusService;

    @Mock
    private GetRekoStatusService getRekoStatusService;

    @InjectMocks
    private RekoController rekoController;

    private static final String PATIENT_ID = "PATIENT_ID";
    private static final String STATUS = "STATUS";
    private static final LocalDate TIMESTAMP = LocalDate.now();


    @Test
    void shouldCallSetRekoStatusServiceWithCorrectPatientId() {
        final var expectedRequest = new CreateRekoStatusRequestDTO(PATIENT_ID, STATUS, TIMESTAMP.toString());
        final var captor = ArgumentCaptor.forClass(String.class);

        rekoController.createRekoStatus(expectedRequest);
        verify(createRekoStatusService).create(captor.capture(), anyString(), any(LocalDateTime.class));

        assertEquals(PATIENT_ID, captor.getValue());
    }

    @Test
    void shouldCallSetRekoStatusServiceWithCorrectStatus() {
        final var expectedRequest =
            new CreateRekoStatusRequestDTO(PATIENT_ID, STATUS, TIMESTAMP.toString());
        final var captor = ArgumentCaptor.forClass(String.class);

        rekoController.createRekoStatus(expectedRequest);
        verify(createRekoStatusService).create(anyString(), captor.capture(), any(LocalDateTime.class));

        assertEquals(STATUS, captor.getValue());
    }

    @Test
    void shouldCallSetRekoStatusServiceWithCorrectTimestamp() {
        final var expectedRequest =
            new CreateRekoStatusRequestDTO(PATIENT_ID, STATUS, TIMESTAMP.toString());
        final var captor = ArgumentCaptor.forClass(LocalDateTime.class);

        rekoController.createRekoStatus(expectedRequest);
        verify(createRekoStatusService).create(anyString(), anyString(), captor.capture());

        assertEquals(TIMESTAMP.atStartOfDay(), captor.getValue());
    }

    @Test
    void shouldCallGetRekoStatusServiceWithCorrectPatientId() {
        final var expectedRequest = new GetRekoStatusRequestDTO(PATIENT_ID, TIMESTAMP, TIMESTAMP.minusDays(2));
        final var captor = ArgumentCaptor.forClass(String.class);

        rekoController.getRekoStatus(expectedRequest);
        verify(getRekoStatusService).get(captor.capture(), any(LocalDate.class), any(LocalDate.class));

        assertEquals(PATIENT_ID, captor.getValue());
    }

    @Test
    void shouldCallGetRekoStatusServiceWithCorrectEndDate() {
        final var expectedRequest = new GetRekoStatusRequestDTO(PATIENT_ID, TIMESTAMP, TIMESTAMP.minusDays(2));
        final var captor = ArgumentCaptor.forClass(LocalDate.class);

        rekoController.getRekoStatus(expectedRequest);
        verify(getRekoStatusService).get(anyString(), captor.capture(), any(LocalDate.class));

        assertEquals(TIMESTAMP, captor.getValue());
    }

    @Test
    void shouldCallGetRekoStatusServiceWithCorrectStartDate() {
        final var expectedRequest = new GetRekoStatusRequestDTO(PATIENT_ID, TIMESTAMP, TIMESTAMP.minusDays(2));
        final var captor = ArgumentCaptor.forClass(LocalDate.class);

        rekoController.getRekoStatus(expectedRequest);
        verify(getRekoStatusService).get(anyString(), any(LocalDate.class), captor.capture());

        assertEquals(TIMESTAMP.minusDays(2), captor.getValue());
    }
}
