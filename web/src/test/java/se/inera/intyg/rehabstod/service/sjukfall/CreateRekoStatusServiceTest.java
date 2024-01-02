/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Collections;
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
import se.inera.intyg.infra.sjukfall.dto.RekoStatusTypeDTO;
import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.integration.it.dto.CreateRekoStatusRequestDTO;
import se.inera.intyg.rehabstod.integration.it.dto.RekoStatusDTO;
import se.inera.intyg.rehabstod.integration.it.service.IntygstjanstRestIntegrationService;
import se.inera.intyg.rehabstod.service.user.UserService;

@ExtendWith(MockitoExtension.class)
public class CreateRekoStatusServiceTest {

    @Mock
    IntygstjanstRestIntegrationService intygstjanstRestIntegrationService;
    @Mock
    UserService userService;
    @InjectMocks
    CreateRekoStatusServiceImpl createRekoStatusService;

    private static final String PATIENT_ID = "191212121212";
    private static final String REKO_ID = "REKO_1";
    private static final String CARE_PROVIDER_ID = "CareProviderId";
    private static final String CARE_UNIT_ID = "CareUnitId";
    private static final String UNIT_ID = "UnitId";
    private static final String STAFF_ID = "StaffId";
    private static final String STAFF_NAME = "StaffName";
    private static final LocalDateTime SICK_LEAVE_TIMESTAMP = LocalDateTime.now().plusDays(1);
    private static final String REKO_NAME = "Reko name";
    private static RehabstodUser user;
    private static SelectableVardenhet careProvider;
    private static SelectableVardenhet careUnit;
    private static Vardgivare careGiver;
    private static Vardenhet unit;

    @BeforeEach
    void setup() {
        user = mock(RehabstodUser.class);
        careProvider = mock(SelectableVardenhet.class);
        careUnit = mock(SelectableVardenhet.class);
        when(careProvider.getId()).thenReturn(CARE_PROVIDER_ID);
        when(user.getValdVardgivare()).thenReturn(careProvider);
        when(user.getHsaId()).thenReturn(STAFF_ID);
        when(user.getNamn()).thenReturn(STAFF_NAME);
        when(userService.getUser()).thenReturn(user);
        when(intygstjanstRestIntegrationService.createRekoStatus(any())).thenReturn(
            new RekoStatusDTO(
                new RekoStatusTypeDTO(REKO_ID, REKO_NAME)
            )
        );
    }

    @Nested
    class TestItResponse {

        @BeforeEach
        void setup() {
            when(user.isValdVardenhetMottagning()).thenReturn(false);
            when(user.getValdVardenhet()).thenReturn(careUnit);
            when(careUnit.getId()).thenReturn(CARE_UNIT_ID);
        }

        @Test
        void shouldReturnRekoStatusId() {
            final var response = createRekoStatusService.create(PATIENT_ID, REKO_ID, SICK_LEAVE_TIMESTAMP);
            assertEquals(REKO_ID, response.getStatus().getId());
        }

        @Test
        void shouldReturnRekoStatusName() {
            final var response = createRekoStatusService.create(PATIENT_ID, REKO_ID, SICK_LEAVE_TIMESTAMP);
            assertEquals(REKO_NAME, response.getStatus().getName());
        }
    }

    @Nested
    class TestITRequest {

        @Nested
        class CareUnit {

            @BeforeEach
            void setup() {
                when(user.isValdVardenhetMottagning()).thenReturn(false);
                when(user.getValdVardenhet()).thenReturn(careUnit);
                when(careUnit.getId()).thenReturn(CARE_UNIT_ID);
            }

            @Test
            void shouldSetStaffId() {
                final var captor = ArgumentCaptor.forClass(CreateRekoStatusRequestDTO.class);
                createRekoStatusService.create(PATIENT_ID, REKO_ID, SICK_LEAVE_TIMESTAMP);
                verify(intygstjanstRestIntegrationService).createRekoStatus(captor.capture());
                assertEquals(STAFF_ID, captor.getValue().getStaffId());
            }

            @Test
            void shouldSetStaffName() {
                final var captor = ArgumentCaptor.forClass(CreateRekoStatusRequestDTO.class);
                createRekoStatusService.create(PATIENT_ID, REKO_ID, SICK_LEAVE_TIMESTAMP);
                verify(intygstjanstRestIntegrationService).createRekoStatus(captor.capture());
                assertEquals(STAFF_NAME, captor.getValue().getStaffName());
            }

            @Test
            void shouldSetPatientId() {
                final var captor = ArgumentCaptor.forClass(CreateRekoStatusRequestDTO.class);
                createRekoStatusService.create(PATIENT_ID, REKO_ID, SICK_LEAVE_TIMESTAMP);
                verify(intygstjanstRestIntegrationService).createRekoStatus(captor.capture());
                assertEquals(PATIENT_ID, captor.getValue().getPatientId());
            }

            @Test
            void shouldSetSickLeaveTimestamp() {
                final var captor = ArgumentCaptor.forClass(CreateRekoStatusRequestDTO.class);
                createRekoStatusService.create(PATIENT_ID, REKO_ID, SICK_LEAVE_TIMESTAMP);
                verify(intygstjanstRestIntegrationService).createRekoStatus(captor.capture());
                assertEquals(SICK_LEAVE_TIMESTAMP, captor.getValue().getSickLeaveTimestamp());
            }

            @Test
            void shouldSetCareProviderId() {
                final var captor = ArgumentCaptor.forClass(CreateRekoStatusRequestDTO.class);
                createRekoStatusService.create(PATIENT_ID, REKO_ID, SICK_LEAVE_TIMESTAMP);
                verify(intygstjanstRestIntegrationService).createRekoStatus(captor.capture());
                assertEquals(CARE_PROVIDER_ID, captor.getValue().getCareProviderId());
            }

            @Test
            void shouldSetCareUnitId() {
                final var captor = ArgumentCaptor.forClass(CreateRekoStatusRequestDTO.class);
                createRekoStatusService.create(PATIENT_ID, REKO_ID, SICK_LEAVE_TIMESTAMP);
                verify(intygstjanstRestIntegrationService).createRekoStatus(captor.capture());
                assertEquals(CARE_UNIT_ID, captor.getValue().getCareUnitId());
            }

            @Test
            void shouldSetUnitIdToNullIfNotSubUnit() {
                final var captor = ArgumentCaptor.forClass(CreateRekoStatusRequestDTO.class);
                createRekoStatusService.create(PATIENT_ID, REKO_ID, SICK_LEAVE_TIMESTAMP);
                verify(intygstjanstRestIntegrationService).createRekoStatus(captor.capture());
                assertNull(captor.getValue().getUnitId());
            }
        }

        @Nested
        class SubUnit {

            @BeforeEach
            void setup() {
                final var subUnit = mock(SelectableVardenhet.class);
                final var mottagning = mock(Mottagning.class);
                careGiver = mock(Vardgivare.class);
                unit = mock(Vardenhet.class);

                when(user.isValdVardenhetMottagning()).thenReturn(true);
                when(mottagning.getId()).thenReturn(UNIT_ID);
                when(subUnit.getId()).thenReturn(UNIT_ID);
                when(user.getValdVardenhet()).thenReturn(subUnit);

                when(careGiver.getVardenheter()).thenReturn(Collections.singletonList(unit));
                when(unit.getMottagningar()).thenReturn(Collections.singletonList(mottagning));
                when(unit.getId()).thenReturn(CARE_UNIT_ID);
                when(user.getVardgivare()).thenReturn(Collections.singletonList(careGiver));
            }

            @Test
            void shouldSetStaffId() {
                final var captor = ArgumentCaptor.forClass(CreateRekoStatusRequestDTO.class);
                createRekoStatusService.create(PATIENT_ID, REKO_ID, SICK_LEAVE_TIMESTAMP);
                verify(intygstjanstRestIntegrationService).createRekoStatus(captor.capture());
                assertEquals(STAFF_ID, captor.getValue().getStaffId());
            }

            @Test
            void shouldSetStaffName() {
                final var captor = ArgumentCaptor.forClass(CreateRekoStatusRequestDTO.class);
                createRekoStatusService.create(PATIENT_ID, REKO_ID, SICK_LEAVE_TIMESTAMP);
                verify(intygstjanstRestIntegrationService).createRekoStatus(captor.capture());
                assertEquals(STAFF_NAME, captor.getValue().getStaffName());
            }

            @Test
            void shouldSetPatientId() {
                final var captor = ArgumentCaptor.forClass(CreateRekoStatusRequestDTO.class);
                createRekoStatusService.create(PATIENT_ID, REKO_ID, SICK_LEAVE_TIMESTAMP);
                verify(intygstjanstRestIntegrationService).createRekoStatus(captor.capture());
                assertEquals(PATIENT_ID, captor.getValue().getPatientId());
            }

            @Test
            void shouldSetSickLeaveTimestamp() {
                final var captor = ArgumentCaptor.forClass(CreateRekoStatusRequestDTO.class);
                createRekoStatusService.create(PATIENT_ID, REKO_ID, SICK_LEAVE_TIMESTAMP);
                verify(intygstjanstRestIntegrationService).createRekoStatus(captor.capture());
                assertEquals(SICK_LEAVE_TIMESTAMP, captor.getValue().getSickLeaveTimestamp());
            }

            @Test
            void shouldSetCareProviderId() {
                final var captor = ArgumentCaptor.forClass(CreateRekoStatusRequestDTO.class);
                createRekoStatusService.create(PATIENT_ID, REKO_ID, SICK_LEAVE_TIMESTAMP);
                verify(intygstjanstRestIntegrationService).createRekoStatus(captor.capture());
                assertEquals(CARE_PROVIDER_ID, captor.getValue().getCareProviderId());
            }

            @Test
            void shouldSetCareUnitId() {
                final var captor = ArgumentCaptor.forClass(CreateRekoStatusRequestDTO.class);
                createRekoStatusService.create(PATIENT_ID, REKO_ID, SICK_LEAVE_TIMESTAMP);
                verify(intygstjanstRestIntegrationService).createRekoStatus(captor.capture());
                assertEquals(CARE_UNIT_ID, captor.getValue().getCareUnitId());
            }

            @Test
            void shouldSetUnitIdToNullIfNotSubUnit() {
                final var captor = ArgumentCaptor.forClass(CreateRekoStatusRequestDTO.class);
                createRekoStatusService.create(PATIENT_ID, REKO_ID, SICK_LEAVE_TIMESTAMP);
                verify(intygstjanstRestIntegrationService).createRekoStatus(captor.capture());
                assertEquals(UNIT_ID, captor.getValue().getUnitId());
            }
        }
    }
}
