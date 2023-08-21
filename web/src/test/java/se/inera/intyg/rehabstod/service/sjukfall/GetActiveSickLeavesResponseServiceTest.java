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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import se.inera.intyg.infra.logmessages.ActivityType;
import se.inera.intyg.infra.logmessages.ResourceType;
import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.integration.it.dto.SickLeavesRequestDTO;
import se.inera.intyg.rehabstod.service.communication.UnansweredCommunicationDecoratorService;
import se.inera.intyg.rehabstod.service.communication.UnansweredCommunicationFilterService;
import se.inera.intyg.rehabstod.service.monitoring.MonitoringLogService;
import se.inera.intyg.rehabstod.service.sjukfall.nameresolver.SjukfallEmployeeNameResolver;
import se.inera.intyg.rehabstod.service.sjukfall.srs.RiskPredictionService;
import se.inera.intyg.rehabstod.service.user.UserService;
import se.inera.intyg.rehabstod.web.controller.api.dto.SickLeavesFilterRequestDTO;

@ExtendWith(MockitoExtension.class)
public class GetActiveSickLeavesResponseServiceTest {

    @Mock
    private UserService userService;
    @Mock
    private MonitoringLogService monitoringLogService;
    @Mock
    private PdlLogSickLeavesService pdlLogSickLeavesService;

    @Mock
    private SjukfallEmployeeNameResolver sjukfallEmployeeNameResolver;

    @Mock
    private RiskPredictionService riskPredictionService;

    @Mock
    private UnansweredCommunicationDecoratorService unansweredCommunicationDecoratorService;

    @Mock
    private UnansweredCommunicationFilterService unansweredCommunicationFilterService;
    @Mock
    private CreateSickLeaveRequestService createSickLeaveRequestService;
    @Mock
    private GetActiveSickLeavesService getActiveSickLeavesService;


    @InjectMocks
    private GetActiveSickLeavesResponseServiceImpl getActiveSickLeavesResponseService;
    private se.inera.intyg.rehabstod.web.model.SjukfallEnhet sickLeave;
    static RehabstodUser user;
    static final String HSA_ID = "HSA_ID";
    static final String UNIT_ID = "UNIT_ID";

    static final SickLeavesFilterRequestDTO SICK_LEAVES_FILTER_REQUEST = new SickLeavesFilterRequestDTO();
    static final SickLeavesRequestDTO SICK_LEAVES_REQUEST = SickLeavesRequestDTO.builder()
        .unitId(UNIT_ID)
        .build();

    @BeforeEach
    void setup() {
        user = mock(RehabstodUser.class);
        when(userService.getUser()).thenReturn(user);
        when(user.getHsaId()).thenReturn(HSA_ID);
        sickLeave = new se.inera.intyg.rehabstod.web.model.SjukfallEnhet();
        when(createSickLeaveRequestService.create(eq(SICK_LEAVES_FILTER_REQUEST), anyBoolean())).thenReturn(SICK_LEAVES_REQUEST);
        when(getActiveSickLeavesService.get(SICK_LEAVES_REQUEST)).thenReturn(List.of(sickLeave));
        when(unansweredCommunicationFilterService.filter(any(), any()))
            .thenReturn(Collections.singletonList(sickLeave));
    }

    @Nested
    class GetActiveSickLeavesResponse {

        @Test
        void shallReturnContent() {
            final var result = getActiveSickLeavesResponseService.get(SICK_LEAVES_FILTER_REQUEST, true);
            assertEquals(List.of(sickLeave), result.getContent());
        }

        @Test
        void shallReturnHasDecoratedWithSrsInfo() {
            final var result = getActiveSickLeavesResponseService.get(SICK_LEAVES_FILTER_REQUEST, true);
            assertFalse(result.isSrsError());
        }

        @Test
        void shallReturnHasDecoratedWithUnansweredCommunications() {
            final var expectedResult = true;
            when(unansweredCommunicationDecoratorService.decorateSickLeaves(List.of(sickLeave))).thenReturn(false);
            final var result = getActiveSickLeavesResponseService.get(SICK_LEAVES_FILTER_REQUEST, true);
            assertEquals(expectedResult, result.isUnansweredCommunicationError());
        }
    }

    @Nested
    class CreateSickLeaveRequestServiceTest {

        @Test
        void shouldUseFilterRequest() {
            final var captor = ArgumentCaptor.forClass(SickLeavesFilterRequestDTO.class);
            getActiveSickLeavesResponseService.get(SICK_LEAVES_FILTER_REQUEST, true);
            verify(createSickLeaveRequestService).create(captor.capture(), anyBoolean());
            assertEquals(SICK_LEAVES_FILTER_REQUEST, captor.getValue());
        }

        @Test
        void shouldUseIncludeParameters() {
            final var captor = ArgumentCaptor.forClass(Boolean.class);
            getActiveSickLeavesResponseService.get(SICK_LEAVES_FILTER_REQUEST, true);
            verify(createSickLeaveRequestService).create(eq(SICK_LEAVES_FILTER_REQUEST), captor.capture());
            assertEquals(true, captor.getValue());
        }
    }

    @Nested
    class GetActiveSickLeaveServiceTest {

        @Test
        void shouldUseRequestFromCreateRequest() {
            final var captor = ArgumentCaptor.forClass(SickLeavesRequestDTO.class);
            getActiveSickLeavesResponseService.get(SICK_LEAVES_FILTER_REQUEST, true);
            verify(getActiveSickLeavesService).get(captor.capture());
            assertEquals(SICK_LEAVES_REQUEST, captor.getValue());
        }
    }


    @Nested
    class TestDecoratingServices {

        @Test
        void shouldCallRiskPredictionService() {
            final var captor = ArgumentCaptor.forClass(List.class);
            getActiveSickLeavesResponseService.get(SICK_LEAVES_FILTER_REQUEST, true);
            verify(riskPredictionService).updateWithRiskPredictions(captor.capture());
            assertEquals(1, captor.getValue().size());
            assertEquals(sickLeave, captor.getValue().get(0));
        }

        @Test
        void shouldCallUnansweredCommunicationDecorator() {
            final var captor = ArgumentCaptor.forClass(List.class);
            getActiveSickLeavesResponseService.get(SICK_LEAVES_FILTER_REQUEST, true);
            verify(unansweredCommunicationDecoratorService).decorateSickLeaves(captor.capture());
            assertEquals(1, captor.getValue().size());
            assertEquals(sickLeave, captor.getValue().get(0));
        }
    }

    @Nested
    class TestUnansweredCommunicationFilteringService {

        @Test
        void shouldCallUnansweredCommunicationFilteringServiceWithSickLeave() {
            final var captor = ArgumentCaptor.forClass(List.class);
            getActiveSickLeavesResponseService.get(SICK_LEAVES_FILTER_REQUEST, true);

            verify(unansweredCommunicationFilterService).filter(captor.capture(), any());
            assertEquals(1, captor.getValue().size());
        }

        @Test
        void shouldCallUnansweredCommunicationFilteringServiceWithFilterId() {
            final var captor = ArgumentCaptor.forClass(String.class);
            getActiveSickLeavesResponseService.get(SICK_LEAVES_FILTER_REQUEST, true);

            verify(unansweredCommunicationFilterService).filter(any(), captor.capture());
            assertEquals(SICK_LEAVES_FILTER_REQUEST.getUnansweredCommunicationFilterTypeId(), captor.getValue());
        }
    }

    @Nested
    class TestMonitorLogging {

        @Test
        void shouldLogUsingUnitIdFromRequest() {
            getActiveSickLeavesResponseService.get(SICK_LEAVES_FILTER_REQUEST, true);

            verify(monitoringLogService).logUserViewedSjukfall(HSA_ID, 1, UNIT_ID);
        }
    }

    @Nested
    class TestPdlLogging {

        @Test
        void shouldPerformPdlLog() {
            getActiveSickLeavesResponseService.get(SICK_LEAVES_FILTER_REQUEST, true);

            verify(pdlLogSickLeavesService)
                .log(Collections.singletonList(sickLeave), ActivityType.READ, ResourceType.RESOURCE_TYPE_SJUKFALL);
        }
    }

    @Nested
    class TestUpdateHsaNames {

        @Test
        void shouldEnrichWithHsaEmployeeNames() {
            getActiveSickLeavesResponseService.get(SICK_LEAVES_FILTER_REQUEST, true);

            verify(sjukfallEmployeeNameResolver, times(1))
                .enrichWithHsaEmployeeNames(Collections.singletonList(sickLeave));
        }

        @Test
        void shouldUpdateDuplicateDoctorNamesWithHsaId() {
            getActiveSickLeavesResponseService.get(SICK_LEAVES_FILTER_REQUEST, true);

            verify(sjukfallEmployeeNameResolver, times(1))
                .updateDuplicateDoctorNamesWithHsaId(Collections.singletonList(sickLeave));
        }
    }
}
