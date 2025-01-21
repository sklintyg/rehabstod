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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.infra.integration.hsatk.model.legacy.SelectableVardenhet;
import se.inera.intyg.infra.logmessages.ActivityType;
import se.inera.intyg.infra.logmessages.ResourceType;
import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.auth.authorities.AuthoritiesConstants;
import se.inera.intyg.rehabstod.auth.pdl.PDLActivityEntry;
import se.inera.intyg.rehabstod.integration.srs.model.RiskSignal;
import se.inera.intyg.rehabstod.service.pdl.LogService;
import se.inera.intyg.rehabstod.service.user.UserService;
import se.inera.intyg.rehabstod.web.model.Patient;
import se.inera.intyg.rehabstod.web.model.SjukfallEnhet;

@ExtendWith(MockitoExtension.class)
public class PdlLogSickLeavesServiceTest {

    @Mock
    UserService userService;

    @Mock
    LogService logService;

    @InjectMocks
    PdlLogSickLeavesServiceImpl pdlLogSickLeavesService;

    RehabstodUser user;
    SelectableVardenhet unit;
    SjukfallEnhet loggedSickLeave = new SjukfallEnhet();
    SjukfallEnhet notLoggedSickLeave = new SjukfallEnhet();
    List<SjukfallEnhet> list = Arrays.asList(loggedSickLeave, notLoggedSickLeave);

    static final String LOGGED_PATIENT_ID = "191212121212";
    static final String NOT_LOGGED_PATIENT_ID = "201212121212";

    @BeforeEach
    void setup() {
        user = mock(RehabstodUser.class);
        unit = mock(SelectableVardenhet.class);
        when(userService.getUser()).thenReturn(user);
        when(user.getValdVardenhet()).thenReturn(unit);
        loggedSickLeave.setPatient(new Patient(LOGGED_PATIENT_ID, "name"));
        notLoggedSickLeave.setPatient(new Patient(NOT_LOGGED_PATIENT_ID, "name"));
    }

    @Nested
    class Log {

        @Test
        void shouldThrowErrorIfUnitIdIsNull() {
            assertThrows(IllegalArgumentException.class,
                () -> pdlLogSickLeavesService.log(list, ActivityType.READ, ResourceType.RESOURCE_TYPE_SJUKFALL)
            );
        }

        @Test
        void shouldOnlyLogSickLeavesWhichHaveNotBeenLogged() {
            final var activities = new HashMap<String, List<PDLActivityEntry>>();
            final var activity = new PDLActivityEntry(LOGGED_PATIENT_ID, ActivityType.READ, ResourceType.RESOURCE_TYPE_SJUKFALL);
            activities.put("UNIT_ID", Collections.singletonList(activity));
            when(user.getStoredActivities()).thenReturn(activities);
            when(unit.getId()).thenReturn("UNIT_ID");

            pdlLogSickLeavesService.log(list, ActivityType.READ, ResourceType.RESOURCE_TYPE_SJUKFALL);

            verify(logService).logSjukfallData(
                Collections.singletonList(notLoggedSickLeave), ActivityType.READ, ResourceType.RESOURCE_TYPE_SJUKFALL
            );
        }
    }

    @Nested
    class LogPrint {

        @Test
        void shouldThrowErrorIfUnitIdIsNull() {
            assertThrows(IllegalArgumentException.class,
                () -> pdlLogSickLeavesService.logPrint(list)
            );
        }

        @Test
        void shouldLogActivityPrintAndResourceTypeSjukfall() {
            when(unit.getId()).thenReturn("UNIT_ID");
            pdlLogSickLeavesService.logPrint(list);
            verify(logService).logSjukfallData(list, ActivityType.PRINT, ResourceType.RESOURCE_TYPE_SJUKFALL);
        }

        @Test
        void shouldLogActivityPrintAndResourceTypeSRSIfFeatureActive() {
            when(user.isFeatureActive(AuthoritiesConstants.FEATURE_SRS)).thenReturn(true);
            when(unit.getId()).thenReturn("UNIT_ID");
            final var sickLeave = new SjukfallEnhet();
            sickLeave.setRiskSignal(new RiskSignal("ID", 1, "description", LocalDateTime.now()));
            sickLeave.setPatient(new Patient(LOGGED_PATIENT_ID, "name"));
            final var sickLeaveWithRisk = List.of(sickLeave);
            pdlLogSickLeavesService.logPrint(sickLeaveWithRisk);
            verify(logService).logSjukfallData(Collections.singletonList(sickLeave), ActivityType.PRINT,
                ResourceType.RESOURCE_TYPE_PREDIKTION_SRS);
        }

        @Test
        void shouldOnlyLogActivityPrintAndResourceTypeSjukfallIfSRSFeatureInactive() {
            when(user.isFeatureActive(AuthoritiesConstants.FEATURE_SRS)).thenReturn(false);
            when(unit.getId()).thenReturn("UNIT_ID");
            final var sickLeave = new SjukfallEnhet();
            sickLeave.setRiskSignal(new RiskSignal("ID", 1, "description", LocalDateTime.now()));
            sickLeave.setPatient(new Patient(LOGGED_PATIENT_ID, "name"));
            final var sickLeaveWithRisk = List.of(sickLeave);
            pdlLogSickLeavesService.logPrint(sickLeaveWithRisk);
            verify(logService).logSjukfallData(Collections.singletonList(sickLeave), ActivityType.PRINT,
                ResourceType.RESOURCE_TYPE_SJUKFALL);
        }

        @Test
        void shouldHandleNullValues() {
            when(unit.getId()).thenReturn("UNIT_ID");
            pdlLogSickLeavesService.logPrint(null);
            verify(logService).logSjukfallData(Collections.emptyList(), ActivityType.PRINT,
                ResourceType.RESOURCE_TYPE_SJUKFALL);
        }
    }
}
