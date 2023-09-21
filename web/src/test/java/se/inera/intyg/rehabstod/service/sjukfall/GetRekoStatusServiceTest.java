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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.infra.integration.hsatk.model.legacy.SelectableVardenhet;
import se.inera.intyg.infra.sjukfall.dto.RekoStatusTypeDTO;
import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.integration.it.dto.GetRekoStatusRequestDTO;
import se.inera.intyg.rehabstod.integration.it.dto.RekoStatusDTO;
import se.inera.intyg.rehabstod.integration.it.service.IntygstjanstRestIntegrationService;

import java.time.LocalDate;
import se.inera.intyg.rehabstod.service.user.UserService;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GetRekoStatusServiceTest {

    @Mock
    IntygstjanstRestIntegrationService intygstjanstRestIntegrationService;
    @Mock
    UserService userService;
    @InjectMocks
    GetRekoStatusServiceImpl getRekoStatusService;

    private static final String PATIENT_ID = "191212121212";
    private static final String REKO_ID = "REKO_1";
    private static final String REKO_NAME = "Reko name";
    private static final String UNIT_ID = "Unit id";
    private static final LocalDate END_DATE = LocalDate.now();
    private static final LocalDate START_DATE = LocalDate.now();

    @BeforeEach
    void setup() {
        final var unit = mock(SelectableVardenhet.class);
        final var user = mock(RehabstodUser.class);

        when(unit.getId()).thenReturn(UNIT_ID);
        when(userService.getUser()).thenReturn(user);
        when(user.getValdVardenhet()).thenReturn(unit);
    }

     @Nested
     class HasRekoStatus {

         @BeforeEach
         void setup() {
             when(intygstjanstRestIntegrationService.getRekoStatus(any())).thenReturn(
                 new RekoStatusDTO(
                     new RekoStatusTypeDTO(REKO_ID, REKO_NAME)
                 )
             );
         }

         @Nested
         class TestItResponse {

             @Test
             void shouldReturnRekoStatusId() {
                 final var response = getRekoStatusService.get(PATIENT_ID, END_DATE, START_DATE);
                 assertEquals(REKO_ID, response.getStatus().getId());
             }

             @Test
             void shouldReturnRekoStatusName() {
                 final var response = getRekoStatusService.get(PATIENT_ID, END_DATE, START_DATE);
                 assertEquals(REKO_NAME, response.getStatus().getName());
             }
         }

         @Nested
         class TestITRequest {

             @Test
             void shouldSetPatientId() {
                 final var captor = ArgumentCaptor.forClass(GetRekoStatusRequestDTO.class);
                 getRekoStatusService.get(PATIENT_ID, END_DATE, START_DATE);
                 verify(intygstjanstRestIntegrationService).getRekoStatus(captor.capture());
                 assertEquals(PATIENT_ID, captor.getValue().getPatientId());
             }

             @Test
             void shouldSetEndDate() {
                 final var captor = ArgumentCaptor.forClass(GetRekoStatusRequestDTO.class);
                 getRekoStatusService.get(PATIENT_ID, END_DATE, START_DATE);
                 verify(intygstjanstRestIntegrationService).getRekoStatus(captor.capture());
                 assertEquals(END_DATE, captor.getValue().getEndDate());
             }

             @Test
             void shouldSetStartDate() {
                 final var captor = ArgumentCaptor.forClass(GetRekoStatusRequestDTO.class);
                 getRekoStatusService.get(PATIENT_ID, END_DATE, START_DATE);
                 verify(intygstjanstRestIntegrationService).getRekoStatus(captor.capture());
                 assertEquals(START_DATE, captor.getValue().getEndDate());
             }

             @Test
             void shouldSetCareUnitId() {
                 final var captor = ArgumentCaptor.forClass(GetRekoStatusRequestDTO.class);
                 getRekoStatusService.get(PATIENT_ID, END_DATE, START_DATE);
                 verify(intygstjanstRestIntegrationService).getRekoStatus(captor.capture());
                 assertEquals(UNIT_ID, captor.getValue().getCareUnitId());
             }
         }
     }

     @Nested
    class HasNoRekoStatus {
         @Test
         void shouldNotThrowError() {
            assertDoesNotThrow(() -> getRekoStatusService.get(PATIENT_ID, END_DATE, START_DATE));
         }

         @Test
         void shouldReturnEmptyRekoStatus() {
             final var response = getRekoStatusService.get(PATIENT_ID, END_DATE, START_DATE);

             assertNull(response.getStatus());
         }
     }
}
