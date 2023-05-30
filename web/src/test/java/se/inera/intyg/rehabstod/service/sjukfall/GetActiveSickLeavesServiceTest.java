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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.Assertions;
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
import se.inera.intyg.infra.logmessages.ActivityType;
import se.inera.intyg.infra.logmessages.ResourceType;
import se.inera.intyg.infra.sjukfall.dto.SjukfallEnhet;
import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.auth.RehabstodUserPreferences;
import se.inera.intyg.rehabstod.auth.RehabstodUserPreferences.Preference;
import se.inera.intyg.rehabstod.integration.it.dto.SickLeavesRequestDTO;
import se.inera.intyg.rehabstod.integration.it.dto.SickLeavesResponseDTO;
import se.inera.intyg.rehabstod.integration.it.service.IntygstjanstRestIntegrationService;
import se.inera.intyg.rehabstod.service.Urval;
import se.inera.intyg.rehabstod.service.diagnos.dto.DiagnosKapitel;
import se.inera.intyg.rehabstod.service.diagnos.dto.DiagnosKategori;
import se.inera.intyg.rehabstod.service.monitoring.MonitoringLogService;
import se.inera.intyg.rehabstod.service.pu.PuService;
import se.inera.intyg.rehabstod.service.sjukfall.dto.SickLeaveLengthInterval;
import se.inera.intyg.rehabstod.service.sjukfall.mappers.SjukfallEngineMapper;
import se.inera.intyg.rehabstod.service.sjukfall.nameresolver.SjukfallEmployeeNameResolver;
import se.inera.intyg.rehabstod.service.sjukfall.srs.RiskPredictionService;
import se.inera.intyg.rehabstod.service.sjukfall.util.PatientIdEncryption;
import se.inera.intyg.rehabstod.service.user.UserService;
import se.inera.intyg.rehabstod.web.controller.api.dto.SickLeavesFilterRequestDTO;

@ExtendWith(MockitoExtension.class)
public class GetActiveSickLeavesServiceTest {

    @Mock
    UserService userService;

    @Mock
    PuService puService;

    @Mock
    MonitoringLogService monitoringLogService;

    @Mock
    SjukfallEngineMapper sjukfallEngineMapper;

    @Mock
    IntygstjanstRestIntegrationService intygstjanstRestIntegrationService;

    @Mock
    PdlLogSickLeavesService pdlLogSickLeavesService;

    @Mock
    SjukfallEmployeeNameResolver sjukfallEmployeeNameResolver;
    @Mock
    PatientIdEncryption patientIdEncryption;

    @Mock
    RiskPredictionService riskPredictionService;

    @InjectMocks
    GetActiveSickLeavesServiceImpl getActiveSickLeavesService;

    Vardenhet unit;
    SelectableVardenhet careGiverUnit;
    Vardgivare careGiver;
    se.inera.intyg.rehabstod.web.model.SjukfallEnhet sickLeave;

    @BeforeEach
    void setup() {
        careGiverUnit = mock(SelectableVardenhet.class);
        careGiver = mock(Vardgivare.class);
        unit = mock(Vardenhet.class);
        user = mock(RehabstodUser.class);

        when(userService.getUser()).thenReturn(user);
        when(user.getHsaId()).thenReturn(HSA_ID);

        final var preferences = new HashMap<String, String>();
        preferences.put(RehabstodUserPreferences.Preference.MAX_ANTAL_DAGAR_MELLAN_INTYG.getBackendKeyName(), GAP);
        preferences.put(Preference.MAX_ANTAL_DAGAR_SEDAN_SJUKFALL_AVSLUT.getBackendKeyName(), DAYS);
        final var userPreferences = RehabstodUserPreferences.fromBackend(preferences);
        when(user.getPreferences()).thenReturn(userPreferences);
        sickLeave = new se.inera.intyg.rehabstod.web.model.SjukfallEnhet();
        final se.inera.intyg.rehabstod.web.model.Patient patient = new se.inera.intyg.rehabstod.web.model.Patient("19121212-1212",
            "Arnold");
        sickLeave.setPatient(patient);
        final var response = new SickLeavesResponseDTO(Collections.singletonList(new SjukfallEnhet()));
        when(intygstjanstRestIntegrationService.getActiveSickLeaves(any())).thenReturn(response);
        when(sjukfallEngineMapper.mapToSjukfallEnhetDto(
            any(SjukfallEnhet.class), anyInt(), any(LocalDate.class)
        )).thenReturn(sickLeave);
    }

    static RehabstodUser user;
    static final String HSA_ID = "HSA_ID";
    static final String SUB_UNIT_ID = "SUB_UNIT_ID";
    static final String UNIT_ID = "UNIT_ID";
    static final String GAP = "5";
    static final String DAYS = "10";
    static final String DOCTOR_FILTER = "DOCTOR_ID";
    static final String REKO_FILTER = "REKO_STATUS";
    static final SickLeaveLengthInterval INTERVALS_FILTER = new SickLeaveLengthInterval(1, 365);
    static final int FROM_PATIENT_AGE = 1;
    static final int TO_PATIENT_AGE = 150;
    static final char LETTER_TO = 'A';
    static final char LETTER_FROM = 'B';
    static final int NUMBER_TO = 1;
    static final int NUMBER_FROM = 2;
    static final String DIAGNOSIS_CHAPTER_NAME = "Name";
    static final LocalDate FROM_END_DATE = LocalDate.now();
    static final LocalDate TO_END_DATE = FROM_END_DATE.plusDays(10);

    static final DiagnosKategori DIAGNOSIS_CHAPTER_TO = new DiagnosKategori(LETTER_TO, NUMBER_TO);
    static final DiagnosKategori DIAGNOSIS_CHAPTER_FROM = new DiagnosKategori(LETTER_FROM, NUMBER_FROM);
    static final DiagnosKapitel CHOSEN_DIAGNOSIS_CHAPTER =
        new DiagnosKapitel(DIAGNOSIS_CHAPTER_TO, DIAGNOSIS_CHAPTER_FROM, DIAGNOSIS_CHAPTER_NAME);
    static final SickLeavesFilterRequestDTO EXPECTED_REQUEST =
        new SickLeavesFilterRequestDTO(
            Collections.singletonList(DOCTOR_FILTER),
            Collections.singletonList(INTERVALS_FILTER),
            Collections.singletonList(CHOSEN_DIAGNOSIS_CHAPTER),
            FROM_PATIENT_AGE,
            TO_PATIENT_AGE,
            FROM_END_DATE,
            TO_END_DATE,
            Collections.singletonList(REKO_FILTER)
        );
    static final SickLeavesFilterRequestDTO EXPECTED_REQUEST_DOCTOR =
        new SickLeavesFilterRequestDTO(
                Collections.emptyList(),
                Collections.singletonList(INTERVALS_FILTER),
                Collections.emptyList(),
                null,
                null,
                FROM_END_DATE,
                TO_END_DATE,
                Collections.emptyList()
        );

    @Nested
    class TestSrs {
        @BeforeEach
        void setup() {
            when(user.getValdVardenhet()).thenReturn(unit);
            when(unit.getId()).thenReturn(UNIT_ID);
            when(user.getUrval()).thenReturn(Urval.ALL);
        }

        @Test
        void shouldCallRiskPredictionService() {
            final var captor = ArgumentCaptor.forClass(List.class);
            getActiveSickLeavesService.get(EXPECTED_REQUEST, true);
            verify(riskPredictionService).updateWithRiskPredictions(captor.capture());
            assertEquals(1, captor.getValue().size());
            assertEquals(sickLeave, captor.getValue().get(0));
        }
    }


    @Nested
    class TestMonitorLogging {

        @Test
        void shouldLogUsingSubUnitIdIfChosen() {
            setupSubUnit();
            when(user.getUrval()).thenReturn(Urval.ALL);

            getActiveSickLeavesService.get(EXPECTED_REQUEST, true);

            verify(monitoringLogService).logUserViewedSjukfall(HSA_ID, 1, SUB_UNIT_ID);
        }

        @Test
        void shouldLogUsingUnitIdIfNotSubUnitIsChosen() {
            when(user.getValdVardenhet()).thenReturn(unit);
            when(unit.getId()).thenReturn(UNIT_ID);
            when(user.getUrval()).thenReturn(Urval.ALL);

            getActiveSickLeavesService.get(EXPECTED_REQUEST, true);

            verify(monitoringLogService).logUserViewedSjukfall(HSA_ID, 1, UNIT_ID);
        }
    }

    @Nested
    class TestPdlLogging {

        @BeforeEach
        void setupPdl() {
            when(user.getValdVardenhet()).thenReturn(unit);
            when(unit.getId()).thenReturn(UNIT_ID);
            when(user.getUrval()).thenReturn(Urval.ALL);
        }

        @Test
        void shouldPerformPdlLog() {
            getActiveSickLeavesService.get(EXPECTED_REQUEST, true);

            verify(pdlLogSickLeavesService)
                .log(Collections.singletonList(sickLeave), ActivityType.READ, ResourceType.RESOURCE_TYPE_SJUKFALL);
        }
    }

    @Nested
    class TestPU {

        @BeforeEach
        void setup() {
            when(user.getValdVardenhet()).thenReturn(unit);
            when(unit.getId()).thenReturn(UNIT_ID);
            when(user.getUrval()).thenReturn(Urval.ALL);
        }

        @Test
        void shouldMakeCallToPUToGetFilterOnProtectedPerson() {
            getActiveSickLeavesService.get(EXPECTED_REQUEST, true);

            verify(puService).shouldFilterSickLeavesOnProtectedPerson(user);
        }
    }

    @Nested
    class TestUpdateHsaNames {

        @BeforeEach
        void setup() {
            when(user.getValdVardenhet()).thenReturn(unit);
            when(unit.getId()).thenReturn(UNIT_ID);
            when(user.getUrval()).thenReturn(Urval.ALL);
        }

        @Test
        void shouldEnrichWithHsaEmployeeNames() {
            getActiveSickLeavesService.get(EXPECTED_REQUEST, true);

            verify(sjukfallEmployeeNameResolver, times(1))
                .enrichWithHsaEmployeeNames(Collections.singletonList(sickLeave));
        }

        @Test
        void shouldUpdateDuplicateDoctorNamesWithHsaId() {
            getActiveSickLeavesService.get(EXPECTED_REQUEST, true);

            verify(sjukfallEmployeeNameResolver, times(1))
                .updateDuplicateDoctorNamesWithHsaId(Collections.singletonList(sickLeave));
        }
    }

    @Nested
    class TestITRequest {

        @Test
        void shouldCreateRequestWithCorrectValuesWhenChosenUnit() {
            when(user.getValdVardenhet()).thenReturn(unit);
            when(unit.getId()).thenReturn(UNIT_ID);
            when(user.getUrval()).thenReturn(Urval.ALL);

            final var captor = ArgumentCaptor.forClass(SickLeavesRequestDTO.class);
            getActiveSickLeavesService.get(EXPECTED_REQUEST, true);

            verify(intygstjanstRestIntegrationService).getActiveSickLeaves(captor.capture());
            Assertions.assertNull(captor.getValue().getUnitId());
            assertEquals(UNIT_ID, captor.getValue().getCareUnitId());
            assertEquals(Integer.parseInt(GAP), captor.getValue().getMaxCertificateGap());
            assertEquals(Integer.parseInt(DAYS), captor.getValue().getMaxDaysSinceSickLeaveCompleted());
            assertEquals(DOCTOR_FILTER, captor.getValue().getDoctorIds().get(0));
            assertEquals(INTERVALS_FILTER.getTo(), captor.getValue().getSickLeaveLengthIntervals().get(0).getTo());
            assertEquals(INTERVALS_FILTER.getFrom(), captor.getValue().getSickLeaveLengthIntervals().get(0).getFrom());
            assertEquals(FROM_PATIENT_AGE, captor.getValue().getFromPatientAge());
            assertEquals(TO_PATIENT_AGE, captor.getValue().getToPatientAge());
            assertEquals(FROM_END_DATE, captor.getValue().getFromSickLeaveEndDate());
            assertEquals(TO_END_DATE, captor.getValue().getToSickLeaveEndDate());
            assertEquals(REKO_FILTER, captor.getValue().getRekoStatusTypeIds().get(0));
        }

        @Test
        void shouldCreateRequestWithCorrectValuesWhenChosenSubUnit() {
            setupSubUnit();
            when(user.getUrval()).thenReturn(Urval.ALL);

            final var captor = ArgumentCaptor.forClass(SickLeavesRequestDTO.class);
            getActiveSickLeavesService.get(EXPECTED_REQUEST, true);

            verify(intygstjanstRestIntegrationService).getActiveSickLeaves(captor.capture());
            assertEquals(SUB_UNIT_ID, captor.getValue().getUnitId());
            assertEquals(UNIT_ID, captor.getValue().getCareUnitId());
            assertEquals(Integer.parseInt(GAP), captor.getValue().getMaxCertificateGap());
            assertEquals(Integer.parseInt(DAYS), captor.getValue().getMaxDaysSinceSickLeaveCompleted());
            assertEquals(DOCTOR_FILTER, captor.getValue().getDoctorIds().get(0));
            assertEquals(INTERVALS_FILTER.getTo(), captor.getValue().getSickLeaveLengthIntervals().get(0).getTo());
            assertEquals(INTERVALS_FILTER.getFrom(), captor.getValue().getSickLeaveLengthIntervals().get(0).getFrom());
            assertEquals(FROM_PATIENT_AGE, captor.getValue().getFromPatientAge());
            assertEquals(TO_PATIENT_AGE, captor.getValue().getToPatientAge());
            assertEquals(FROM_END_DATE, captor.getValue().getFromSickLeaveEndDate());
            assertEquals(TO_END_DATE, captor.getValue().getToSickLeaveEndDate());
            assertEquals(REKO_FILTER, captor.getValue().getRekoStatusTypeIds().get(0));
        }

        @Test
        void shouldCreateRequestWithNullSickLeaveLengthFilter() {
            when(user.getValdVardenhet()).thenReturn(unit);
            when(unit.getId()).thenReturn(UNIT_ID);
            when(user.getUrval()).thenReturn(Urval.ALL);

            final var captor = ArgumentCaptor.forClass(SickLeavesRequestDTO.class);
            getActiveSickLeavesService.get(
                new SickLeavesFilterRequestDTO(
                    Collections.singletonList(DOCTOR_FILTER),
                    Collections.singletonList(new SickLeaveLengthInterval(null, null)),
                    Collections.singletonList(CHOSEN_DIAGNOSIS_CHAPTER),
                    null,
                    null,
                    FROM_END_DATE,
                    TO_END_DATE,
                    Collections.singletonList(REKO_FILTER)
                ), true
            );

            verify(intygstjanstRestIntegrationService).getActiveSickLeaves(captor.capture());
            assertNull(captor.getValue().getSickLeaveLengthIntervals().get(0).getTo());
            assertNull(captor.getValue().getSickLeaveLengthIntervals().get(0).getFrom());
        }

        @Test
        void shouldCreateRequestWithNullSickLeaveEndDateFilter() {
            when(user.getValdVardenhet()).thenReturn(unit);
            when(unit.getId()).thenReturn(UNIT_ID);
            when(user.getUrval()).thenReturn(Urval.ALL);

            final var captor = ArgumentCaptor.forClass(SickLeavesRequestDTO.class);
            getActiveSickLeavesService.get(
                    new SickLeavesFilterRequestDTO(
                            Collections.singletonList(DOCTOR_FILTER),
                            Collections.singletonList(new SickLeaveLengthInterval(null, null)),
                            Collections.singletonList(CHOSEN_DIAGNOSIS_CHAPTER),
                            null,
                            null,
                            null,
                            null,
                            Collections.singletonList(REKO_FILTER)
                    ), true
            );

            verify(intygstjanstRestIntegrationService).getActiveSickLeaves(captor.capture());
            assertNull(captor.getValue().getFromSickLeaveEndDate());
            assertNull(captor.getValue().getToSickLeaveEndDate());
        }

        @Test
        void shouldCreateRequestWithCorrectValuesWhenNotIncludingParameters() {
            when(user.getValdVardenhet()).thenReturn(unit);
            when(unit.getId()).thenReturn(UNIT_ID);
            when(user.getUrval()).thenReturn(Urval.ALL);

            final var captor = ArgumentCaptor.forClass(SickLeavesRequestDTO.class);
            getActiveSickLeavesService.get(EXPECTED_REQUEST, false);

            verify(intygstjanstRestIntegrationService).getActiveSickLeaves(captor.capture());
            Assertions.assertNull(captor.getValue().getUnitId());
            assertEquals(UNIT_ID, captor.getValue().getCareUnitId());
            assertEquals(0, captor.getValue().getMaxCertificateGap());
            assertEquals(0, captor.getValue().getMaxDaysSinceSickLeaveCompleted());
            assertEquals(DOCTOR_FILTER, captor.getValue().getDoctorIds().get(0));
            assertEquals(FROM_PATIENT_AGE, captor.getValue().getFromPatientAge());
            assertEquals(TO_PATIENT_AGE, captor.getValue().getToPatientAge());
            assertEquals(REKO_FILTER, captor.getValue().getRekoStatusTypeIds().get(0));
        }

        @Test
        void shouldCreateRequestWithHsaIdOfDoctorIfUserIsDoctor() {
            when(user.getValdVardenhet()).thenReturn(unit);
            when(unit.getId()).thenReturn(UNIT_ID);
            when(user.getUrval()).thenReturn(Urval.ISSUED_BY_ME);

            final var captor = ArgumentCaptor.forClass(SickLeavesRequestDTO.class);
            getActiveSickLeavesService.get(EXPECTED_REQUEST_DOCTOR, true);

            verify(intygstjanstRestIntegrationService).getActiveSickLeaves(captor.capture());
            assertEquals(HSA_ID, captor.getValue().getDoctorIds().get(0));
        }

        @Test
        void shouldCreateRequestWithHsaIdIfDoctorAndMergedWithFilteringDoctorIds() {
            when(user.getValdVardenhet()).thenReturn(unit);
            when(unit.getId()).thenReturn(UNIT_ID);
            when(user.getUrval()).thenReturn(Urval.ISSUED_BY_ME);

            final var captor = ArgumentCaptor.forClass(SickLeavesRequestDTO.class);
            getActiveSickLeavesService.get(EXPECTED_REQUEST, true);

            verify(intygstjanstRestIntegrationService).getActiveSickLeaves(captor.capture());
            assertEquals(EXPECTED_REQUEST.getDoctorIds().size() + 1, captor.getValue().getDoctorIds().size());
        }

        @Test
        void shouldCreateRequestWithProtectedPersonFilterIdNull() {
            when(user.getValdVardenhet()).thenReturn(unit);
            when(unit.getId()).thenReturn(UNIT_ID);
            when(user.getUrval()).thenReturn(Urval.ISSUED_BY_ME);
            when(puService.shouldFilterSickLeavesOnProtectedPerson(any())).thenReturn(true);

            final var captor = ArgumentCaptor.forClass(SickLeavesRequestDTO.class);
            getActiveSickLeavesService.get(EXPECTED_REQUEST_DOCTOR, true);

            verify(intygstjanstRestIntegrationService).getActiveSickLeaves(captor.capture());
            assertNull(captor.getValue().getProtectedPersonFilterId());
        }

        @Test
        void shouldCreateRequestWithProtectedPersonFilterIdAsHsaId() {
            when(user.getValdVardenhet()).thenReturn(unit);
            when(unit.getId()).thenReturn(UNIT_ID);
            when(user.getUrval()).thenReturn(Urval.ISSUED_BY_ME);
            when(puService.shouldFilterSickLeavesOnProtectedPerson(any())).thenReturn(false);

            final var captor = ArgumentCaptor.forClass(SickLeavesRequestDTO.class);
            getActiveSickLeavesService.get(EXPECTED_REQUEST_DOCTOR, true);

            verify(intygstjanstRestIntegrationService).getActiveSickLeaves(captor.capture());
            assertEquals(HSA_ID, captor.getValue().getProtectedPersonFilterId());
        }

        @Nested
        class DiagnosisChapters {

            @BeforeEach
            void setup() {
                when(user.getValdVardenhet()).thenReturn(unit);
                when(unit.getId()).thenReturn(UNIT_ID);
                when(user.getUrval()).thenReturn(Urval.ALL);
            }

            @Test
            void shouldConvertEnabledDiagnosisChapters() {
                final var captor = ArgumentCaptor.forClass(SickLeavesRequestDTO.class);
                getActiveSickLeavesService.get(EXPECTED_REQUEST, true);

                verify(intygstjanstRestIntegrationService).getActiveSickLeaves(captor.capture());

                assertEquals(1, captor.getValue().getDiagnosisChapters().size());
            }

            @Test
            void shouldConvertEnabledDiagnosisChapterTo() {
                final var captor = ArgumentCaptor.forClass(SickLeavesRequestDTO.class);
                getActiveSickLeavesService.get(EXPECTED_REQUEST, true);

                verify(intygstjanstRestIntegrationService).getActiveSickLeaves(captor.capture());

                assertEquals(
                    CHOSEN_DIAGNOSIS_CHAPTER.getTo().getLetter(), captor.getValue().getDiagnosisChapters().get(0).getTo().getLetter()
                );
                assertEquals(
                    CHOSEN_DIAGNOSIS_CHAPTER.getTo().getNumber(), captor.getValue().getDiagnosisChapters().get(0).getTo().getNumber()
                );
            }

            @Test
            void shouldConvertEnabledDiagnosisChapterFrom() {
                final var captor = ArgumentCaptor.forClass(SickLeavesRequestDTO.class);
                getActiveSickLeavesService.get(EXPECTED_REQUEST, true);

                verify(intygstjanstRestIntegrationService).getActiveSickLeaves(captor.capture());

                assertEquals(
                    CHOSEN_DIAGNOSIS_CHAPTER.getFrom().getLetter(), captor.getValue().getDiagnosisChapters().get(0).getFrom().getLetter()
                );
                assertEquals(
                    CHOSEN_DIAGNOSIS_CHAPTER.getFrom().getNumber(), captor.getValue().getDiagnosisChapters().get(0).getFrom().getNumber()
                );
            }

            @Test
            void shouldConvertEnabledDiagnosisChapterId() {
                final var captor = ArgumentCaptor.forClass(SickLeavesRequestDTO.class);
                getActiveSickLeavesService.get(EXPECTED_REQUEST, true);

                verify(intygstjanstRestIntegrationService).getActiveSickLeaves(captor.capture());

                assertEquals(CHOSEN_DIAGNOSIS_CHAPTER.getId(), captor.getValue().getDiagnosisChapters().get(0).getId());
                assertEquals(CHOSEN_DIAGNOSIS_CHAPTER.getId(), captor.getValue().getDiagnosisChapters().get(0).getId());
            }

            @Test
            void shouldConvertEnabledDiagnosisChapterName() {
                final var captor = ArgumentCaptor.forClass(SickLeavesRequestDTO.class);
                getActiveSickLeavesService.get(EXPECTED_REQUEST, true);

                verify(intygstjanstRestIntegrationService).getActiveSickLeaves(captor.capture());

                assertEquals(CHOSEN_DIAGNOSIS_CHAPTER.getName(), captor.getValue().getDiagnosisChapters().get(0).getName());
                assertEquals(CHOSEN_DIAGNOSIS_CHAPTER.getName(), captor.getValue().getDiagnosisChapters().get(0).getName());
            }
        }
    }

    private void setupSubUnit() {
        when(user.isValdVardenhetMottagning()).thenReturn(true);

        final var subUnit = mock(SelectableVardenhet.class);
        final var mottagning = mock(Mottagning.class);
        when(mottagning.getId()).thenReturn(SUB_UNIT_ID);
        when(subUnit.getId()).thenReturn(SUB_UNIT_ID);
        when(user.getValdVardenhet()).thenReturn(subUnit);

        when(careGiver.getVardenheter()).thenReturn(Collections.singletonList(unit));
        when(unit.getMottagningar()).thenReturn(Collections.singletonList(mottagning));
        when(unit.getId()).thenReturn(UNIT_ID);
        when(user.getVardgivare()).thenReturn(Collections.singletonList(careGiver));
    }
}
