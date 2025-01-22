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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.infra.integration.hsatk.model.legacy.Mottagning;
import se.inera.intyg.infra.integration.hsatk.model.legacy.SelectableVardenhet;
import se.inera.intyg.infra.integration.hsatk.model.legacy.Vardenhet;
import se.inera.intyg.infra.integration.hsatk.model.legacy.Vardgivare;
import se.inera.intyg.infra.security.common.model.Role;
import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.auth.RehabstodUserPreferences;
import se.inera.intyg.rehabstod.auth.RehabstodUserPreferences.Preference;
import se.inera.intyg.rehabstod.auth.authorities.AuthoritiesConstants;
import se.inera.intyg.rehabstod.service.diagnos.dto.DiagnosKapitel;
import se.inera.intyg.rehabstod.service.diagnos.dto.DiagnosKategori;
import se.inera.intyg.rehabstod.service.pu.PuService;
import se.inera.intyg.rehabstod.service.sjukfall.dto.SickLeaveLengthInterval;
import se.inera.intyg.rehabstod.service.user.UserService;
import se.inera.intyg.rehabstod.web.controller.api.dto.SickLeavesFilterRequestDTO;

@ExtendWith(MockitoExtension.class)
class CreateSickLeaveRequestServiceImplTest {

    @Mock
    private UserService userService;
    @Mock
    private PuService puService;

    @InjectMocks
    private CreateSickLeaveRequestServiceImpl getSickLeaveRequestService;
    private static final String HSA_ID = "hsaId";
    private static final String UNIT_ID_2 = "unitId";
    private static final String UNIT_ID_1 = "anotherUnitId";
    private static final String UNIT_NAME = "unitName";
    private static final String USERNAME = "userName";
    static final String GAP = "5";
    static final String DAYS = "10";
    static final String DIAGNOSIS_NAME = "diagnosisName";

    @Nested
    class UnitId {

        @Test
        void shallIncludeUnitId() {
            final RehabstodUser user = getRehabstodUser(true, AuthoritiesConstants.ROLE_LAKARE, GAP, DAYS);
            when(puService.shouldFilterSickLeavesOnProtectedPerson(user)).thenReturn(false);
            when(userService.getUser()).thenReturn(user);

            final var response = getSickLeaveRequestService.create(SickLeavesFilterRequestDTO.builder().build(), true);
            assertEquals(UNIT_ID_2, response.getUnitId());
        }

        @Test
        void shallExcludeUnitId() {
            final RehabstodUser user = getRehabstodUser(false, AuthoritiesConstants.ROLE_LAKARE, GAP, DAYS);
            when(puService.shouldFilterSickLeavesOnProtectedPerson(user)).thenReturn(false);
            when(userService.getUser()).thenReturn(user);

            final var response = getSickLeaveRequestService.create(SickLeavesFilterRequestDTO.builder().build(), true);
            assertNull(response.getUnitId());
        }

    }

    @Nested
    class CareUnitId {

        @Test
        void shallIncludeCareUnitIdFromSubUnit() {
            final RehabstodUser user = getRehabstodUser(true, AuthoritiesConstants.ROLE_LAKARE, GAP, DAYS);
            when(puService.shouldFilterSickLeavesOnProtectedPerson(user)).thenReturn(false);
            when(userService.getUser()).thenReturn(user);

            final var response = getSickLeaveRequestService.create(SickLeavesFilterRequestDTO.builder().build(), true);
            assertEquals(UNIT_ID_1, response.getCareUnitId());
        }

        @Test
        void shallIncludeCareUnitId() {
            final RehabstodUser user = getRehabstodUser(false, AuthoritiesConstants.ROLE_LAKARE, GAP, DAYS);
            when(puService.shouldFilterSickLeavesOnProtectedPerson(user)).thenReturn(false);
            when(userService.getUser()).thenReturn(user);

            final var response = getSickLeaveRequestService.create(SickLeavesFilterRequestDTO.builder().build(), true);
            assertEquals(UNIT_ID_2, response.getCareUnitId());
        }
    }

    @Nested
    class PatientAge {

        @Test
        void shallIncludeFromPatientAge() {
            final RehabstodUser user = getRehabstodUser(true, AuthoritiesConstants.ROLE_LAKARE, GAP, DAYS);
            when(puService.shouldFilterSickLeavesOnProtectedPerson(user)).thenReturn(false);
            when(userService.getUser()).thenReturn(user);

            final var expectedResult = 25;
            final var response = getSickLeaveRequestService.create(
                SickLeavesFilterRequestDTO.builder()
                    .fromPatientAge(expectedResult)
                    .build(), true);
            assertEquals(expectedResult, response.getFromPatientAge());
        }

        @Test
        void shallIncludeToPatientAge() {
            final RehabstodUser user = getRehabstodUser(true, AuthoritiesConstants.ROLE_LAKARE, GAP, DAYS);
            when(puService.shouldFilterSickLeavesOnProtectedPerson(user)).thenReturn(false);
            when(userService.getUser()).thenReturn(user);

            final var expectedResult = 50;
            final var response = getSickLeaveRequestService.create(
                SickLeavesFilterRequestDTO.builder()
                    .toPatientAge(expectedResult)
                    .build(), true);
            assertEquals(expectedResult, response.getToPatientAge());
        }

        @Test
        void shallExcludePatientAge() {
            final RehabstodUser user = getRehabstodUser(true, AuthoritiesConstants.ROLE_LAKARE, GAP, DAYS);
            when(puService.shouldFilterSickLeavesOnProtectedPerson(user)).thenReturn(false);
            when(userService.getUser()).thenReturn(user);

            final var response = getSickLeaveRequestService.create(
                SickLeavesFilterRequestDTO.builder()
                    .build(), true);
            assertNull(response.getToPatientAge());
            assertNull(response.getFromPatientAge());
        }
    }

    @Nested
    class SickLeaveEndDate {

        @Test
        void shallIncludeFromSickLeaveEndDate() {
            final RehabstodUser user = getRehabstodUser(true, AuthoritiesConstants.ROLE_LAKARE, GAP, DAYS);
            when(puService.shouldFilterSickLeavesOnProtectedPerson(user)).thenReturn(false);
            when(userService.getUser()).thenReturn(user);

            final var expectedResult = LocalDate.now();
            final var response = getSickLeaveRequestService.create(
                SickLeavesFilterRequestDTO.builder()
                    .fromSickLeaveEndDate(expectedResult)
                    .build(), true);
            assertEquals(expectedResult, response.getFromSickLeaveEndDate());
        }

        @Test
        void shallIncludeToSickLeaveEndDate() {
            final RehabstodUser user = getRehabstodUser(true, AuthoritiesConstants.ROLE_LAKARE, GAP, DAYS);
            when(puService.shouldFilterSickLeavesOnProtectedPerson(user)).thenReturn(false);
            when(userService.getUser()).thenReturn(user);

            final var expectedResult = LocalDate.now();
            final var response = getSickLeaveRequestService.create(
                SickLeavesFilterRequestDTO.builder()
                    .toSickLeaveEndDate(expectedResult)
                    .build(), true);
            assertEquals(expectedResult, response.getToSickLeaveEndDate());
        }

        @Test
        void shallExcludeSickLeaveEndDate() {
            final RehabstodUser user = getRehabstodUser(true, AuthoritiesConstants.ROLE_LAKARE, GAP, DAYS);
            when(puService.shouldFilterSickLeavesOnProtectedPerson(user)).thenReturn(false);
            when(userService.getUser()).thenReturn(user);

            final var response = getSickLeaveRequestService.create(
                SickLeavesFilterRequestDTO.builder()
                    .build(), true);
            assertNull(response.getToSickLeaveEndDate());
            assertNull(response.getFromSickLeaveEndDate());
        }
    }

    @Nested
    class RekoStatusTypeIds {

        @Test
        void shallIncludeRekoStatusTypeIds() {
            final RehabstodUser user = getRehabstodUser(true, AuthoritiesConstants.ROLE_LAKARE, GAP, DAYS);
            when(puService.shouldFilterSickLeavesOnProtectedPerson(user)).thenReturn(false);
            when(userService.getUser()).thenReturn(user);

            final var expectedResult = List.of("rekoId");
            final var response = getSickLeaveRequestService.create(
                SickLeavesFilterRequestDTO.builder()
                    .rekoStatusTypeIds(expectedResult)
                    .build(), true);
            assertEquals(expectedResult, response.getRekoStatusTypeIds());
        }

        @Test
        void shallExcludeRekoStatusTypeIds() {
            final RehabstodUser user = getRehabstodUser(true, AuthoritiesConstants.ROLE_LAKARE, GAP, DAYS);
            when(puService.shouldFilterSickLeavesOnProtectedPerson(user)).thenReturn(false);
            when(userService.getUser()).thenReturn(user);

            final var response = getSickLeaveRequestService.create(
                SickLeavesFilterRequestDTO.builder()
                    .build(), true);
            assertNull(response.getRekoStatusTypeIds());
        }
    }

    @Nested
    class OccupationTypeIds {

        @Test
        void shallIncludeOccupationTypeIds() {
            final RehabstodUser user = getRehabstodUser(true, AuthoritiesConstants.ROLE_LAKARE, GAP, DAYS);
            when(puService.shouldFilterSickLeavesOnProtectedPerson(user)).thenReturn(false);
            when(userService.getUser()).thenReturn(user);

            final var expectedResult = List.of("occupationTypeId");
            final var response = getSickLeaveRequestService.create(
                SickLeavesFilterRequestDTO.builder()
                    .occupationTypeIds(expectedResult)
                    .build(), true);
            assertEquals(expectedResult, response.getOccupationTypeIds());
        }

        @Test
        void shallExcludeOccupationTypeIds() {
            final RehabstodUser user = getRehabstodUser(true, AuthoritiesConstants.ROLE_LAKARE, GAP, DAYS);
            when(puService.shouldFilterSickLeavesOnProtectedPerson(user)).thenReturn(false);
            when(userService.getUser()).thenReturn(user);

            final var response = getSickLeaveRequestService.create(
                SickLeavesFilterRequestDTO.builder()
                    .build(), true);
            assertNull(response.getOccupationTypeIds());
        }
    }

    @Nested
    class TextSearch {


        @Test
        void shallIncludeTextSearch() {
            final RehabstodUser user = getRehabstodUser(true, AuthoritiesConstants.ROLE_LAKARE, GAP, DAYS);
            when(puService.shouldFilterSickLeavesOnProtectedPerson(user)).thenReturn(false);
            when(userService.getUser()).thenReturn(user);

            final var expectedResult = "textSearch";
            final var response = getSickLeaveRequestService.create(
                SickLeavesFilterRequestDTO.builder()
                    .textSearch(expectedResult)
                    .build(), true);
            assertEquals(expectedResult, response.getTextSearch());
        }

        @Test
        void shallExcludeTextSearch() {
            final RehabstodUser user = getRehabstodUser(true, AuthoritiesConstants.ROLE_LAKARE, GAP, DAYS);
            when(puService.shouldFilterSickLeavesOnProtectedPerson(user)).thenReturn(false);
            when(userService.getUser()).thenReturn(user);

            final var response = getSickLeaveRequestService.create(
                SickLeavesFilterRequestDTO.builder()
                    .build(), true);
            assertNull(response.getTextSearch());
        }
    }

    @Nested
    class DoctorIds {


        @Test
        void shallIncludeDoctorIds() {
            final RehabstodUser user = getRehabstodUser(true, AuthoritiesConstants.ROLE_KOORDINATOR, GAP, DAYS);
            when(puService.shouldFilterSickLeavesOnProtectedPerson(user)).thenReturn(false);
            when(userService.getUser()).thenReturn(user);

            final var expectedResult = List.of("doctorId");
            final var response = getSickLeaveRequestService.create(
                SickLeavesFilterRequestDTO.builder()
                    .doctorIds(expectedResult)
                    .build(), true);
            assertEquals(expectedResult, response.getDoctorIds());
        }

        @Test
        void shallIncludeDoctorIdFromUser() {
            final RehabstodUser user = getRehabstodUser(true, AuthoritiesConstants.ROLE_LAKARE, GAP, DAYS);
            when(puService.shouldFilterSickLeavesOnProtectedPerson(user)).thenReturn(false);
            when(userService.getUser()).thenReturn(user);

            final var expectedResult = List.of("doctorId", HSA_ID);
            final var response = getSickLeaveRequestService.create(
                SickLeavesFilterRequestDTO.builder()
                    .doctorIds(List.of("doctorId"))
                    .build(), true);
            assertEquals(expectedResult, response.getDoctorIds());
        }

        @Test
        void shallExcludeDoctorIds() {
            final RehabstodUser user = getRehabstodUser(true, AuthoritiesConstants.ROLE_KOORDINATOR, GAP, DAYS);
            when(puService.shouldFilterSickLeavesOnProtectedPerson(user)).thenReturn(false);
            when(userService.getUser()).thenReturn(user);

            final var response = getSickLeaveRequestService.create(
                SickLeavesFilterRequestDTO.builder()
                    .build(), true);
            assertTrue(response.getDoctorIds().isEmpty());
        }
    }

    @Nested
    class MaxCertificateGap {

        @Test
        void shallIncludeMaxCertificateGap() {
            final RehabstodUser user = getRehabstodUser(true, AuthoritiesConstants.ROLE_LAKARE, GAP, DAYS);
            when(puService.shouldFilterSickLeavesOnProtectedPerson(user)).thenReturn(false);
            when(userService.getUser()).thenReturn(user);

            final var expectedResult = 5;
            final var response = getSickLeaveRequestService.create(SickLeavesFilterRequestDTO.builder().build(), true);
            assertEquals(expectedResult, response.getMaxCertificateGap());
        }

        @Test
        void shallUseDefaultValueIfIncludeParametersFalse() {
            final RehabstodUser user = getRehabstodUser(true, AuthoritiesConstants.ROLE_LAKARE, GAP, DAYS);
            when(puService.shouldFilterSickLeavesOnProtectedPerson(user)).thenReturn(false);
            when(userService.getUser()).thenReturn(user);

            final var expectedResult = 0;
            final var response = getSickLeaveRequestService.create(SickLeavesFilterRequestDTO.builder().build(), false);
            assertEquals(expectedResult, response.getMaxCertificateGap());
        }
    }

    @Nested
    class MaxDaysSinceSickLeaveCompleted {

        @Test
        void shallIncludeMaxDaysSinceSickLeaveCompleted() {
            final RehabstodUser user = getRehabstodUser(true, AuthoritiesConstants.ROLE_LAKARE, GAP, DAYS);
            when(puService.shouldFilterSickLeavesOnProtectedPerson(user)).thenReturn(false);
            when(userService.getUser()).thenReturn(user);

            final var expectedResult = 10;
            final var response = getSickLeaveRequestService.create(SickLeavesFilterRequestDTO.builder().build(), true);
            assertEquals(expectedResult, response.getMaxDaysSinceSickLeaveCompleted());
        }

        @Test
        void shallUseDefaultValueIfIncludeParametersFalse() {
            final RehabstodUser user = getRehabstodUser(true, AuthoritiesConstants.ROLE_LAKARE, GAP, DAYS);
            when(puService.shouldFilterSickLeavesOnProtectedPerson(user)).thenReturn(false);
            when(userService.getUser()).thenReturn(user);

            final var expectedResult = 0;
            final var response = getSickLeaveRequestService.create(SickLeavesFilterRequestDTO.builder().build(), false);
            assertEquals(expectedResult, response.getMaxDaysSinceSickLeaveCompleted());
        }
    }

    @Nested
    class SickLeaveLengthIntervals {

        @Test
        void shallIncludeSickLeaveLengthIntervals() {
            final RehabstodUser user = getRehabstodUser(true, AuthoritiesConstants.ROLE_LAKARE, GAP, DAYS);
            when(puService.shouldFilterSickLeavesOnProtectedPerson(user)).thenReturn(false);
            when(userService.getUser()).thenReturn(user);

            final var expectedResult = List.of(new se.inera.intyg.rehabstod.integration.it.dto.SickLeaveLengthInterval(5, 5));
            final var response = getSickLeaveRequestService.create(SickLeavesFilterRequestDTO.builder()
                .sickLeaveLengthIntervals(List.of(new SickLeaveLengthInterval(5, 5)))
                .build(), true);
            assertEquals(expectedResult, response.getSickLeaveLengthIntervals());
        }

        @Test
        void shallExcludeSickLeaveLengthIntervals() {
            final RehabstodUser user = getRehabstodUser(true, AuthoritiesConstants.ROLE_LAKARE, GAP, DAYS);
            when(puService.shouldFilterSickLeavesOnProtectedPerson(user)).thenReturn(false);
            when(userService.getUser()).thenReturn(user);

            final var response = getSickLeaveRequestService.create(SickLeavesFilterRequestDTO.builder()
                .build(), true);
            assertTrue(response.getSickLeaveLengthIntervals().isEmpty());
        }
    }

    @Nested
    class DiagnosisChapters {

        @Test
        void shallIncludeDiagnosisChapters() {
            final RehabstodUser user = getRehabstodUser(true, AuthoritiesConstants.ROLE_LAKARE, GAP, DAYS);
            when(puService.shouldFilterSickLeavesOnProtectedPerson(user)).thenReturn(false);
            when(userService.getUser()).thenReturn(user);

            final var expectedResult = List.of(
                new se.inera.intyg.infra.sjukfall.dto.DiagnosKapitel(new se.inera.intyg.infra.sjukfall.dto.DiagnosKategori('A', 5),
                    new se.inera.intyg.infra.sjukfall.dto.DiagnosKategori('B', 5), DIAGNOSIS_NAME));
            final var response = getSickLeaveRequestService.create(SickLeavesFilterRequestDTO.builder()
                .diagnosisChapters(List.of(new DiagnosKapitel(new DiagnosKategori('A', 5), new DiagnosKategori('B', 5), DIAGNOSIS_NAME)))
                .build(), true);
            assertEquals(expectedResult, response.getDiagnosisChapters());
        }

        @Test
        void shallExcludeDiagnosisChapters() {
            final RehabstodUser user = getRehabstodUser(true, AuthoritiesConstants.ROLE_LAKARE, GAP, DAYS);
            when(puService.shouldFilterSickLeavesOnProtectedPerson(user)).thenReturn(false);
            when(userService.getUser()).thenReturn(user);

            final var response = getSickLeaveRequestService.create(SickLeavesFilterRequestDTO.builder()
                .build(), true);
            assertTrue(response.getDiagnosisChapters().isEmpty());
        }
    }

    @Nested
    class GetProtectedPersonFilter {

        @Test
        void shallIncludeHsaId() {
            final RehabstodUser user = getRehabstodUser(true, AuthoritiesConstants.ROLE_LAKARE, GAP, DAYS);
            when(puService.shouldFilterSickLeavesOnProtectedPerson(user)).thenReturn(false);
            when(userService.getUser()).thenReturn(user);

            final var response = getSickLeaveRequestService.create(SickLeavesFilterRequestDTO.builder().build(), true);
            assertEquals(HSA_ID, response.getProtectedPersonFilterId());
        }

        @Test
        void shallNotIncludeHsaId() {
            final RehabstodUser user = getRehabstodUser(true, AuthoritiesConstants.ROLE_LAKARE, GAP, DAYS);
            when(puService.shouldFilterSickLeavesOnProtectedPerson(user)).thenReturn(true);
            when(userService.getUser()).thenReturn(user);

            final var response = getSickLeaveRequestService.create(SickLeavesFilterRequestDTO.builder().build(), true);
            assertNull(response.getProtectedPersonFilterId());
        }
    }

    private static RehabstodUser getRehabstodUser(boolean useSubUnit, String role, String gap, String days) {
        final var user = new RehabstodUser(HSA_ID, USERNAME, false);
        user.setValdVardenhet(getValdVardenhet());
        user.setVardgivare(List.of(setupUnitsForUser(useSubUnit)));
        user.setPreferences(getUserPreferences(gap, days));
        user.setRoles(Map.of(role, new Role()));
        return user;
    }

    private static Vardgivare setupUnitsForUser(boolean useSubUnit) {
        final var careGiver = new Vardgivare();
        final var careUnit = new Vardenhet(useSubUnit ? UNIT_ID_1 : UNIT_ID_2, UNIT_NAME);
        final var unit = new Mottagning(UNIT_ID_2, UNIT_NAME);
        careGiver.setVardenheter(List.of(careUnit));
        careUnit.setMottagningar(List.of(unit));
        return careGiver;
    }

    private static RehabstodUserPreferences getUserPreferences(String gap, String days) {
        final var preferences = new HashMap<String, String>();
        preferences.put(Preference.MAX_ANTAL_DAGAR_MELLAN_INTYG.getBackendKeyName(), gap);
        preferences.put(Preference.MAX_ANTAL_DAGAR_SEDAN_SJUKFALL_AVSLUT.getBackendKeyName(), days);
        return RehabstodUserPreferences.fromBackend(preferences);
    }

    private static SelectableVardenhet getValdVardenhet() {
        return new SelectableVardenhet() {
            @Override
            public String getId() {
                return UNIT_ID_2;
            }

            @Override
            public String getNamn() {
                return null;
            }

            @Override
            public List<String> getHsaIds() {
                return null;
            }
        };
    }
}
