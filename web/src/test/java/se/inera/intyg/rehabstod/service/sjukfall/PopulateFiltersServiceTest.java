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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
import se.inera.intyg.infra.sjukfall.dto.DiagnosKategori;
import se.inera.intyg.infra.sjukfall.dto.Lakare;
import se.inera.intyg.infra.sjukfall.dto.OccupationTypeDTO;
import se.inera.intyg.infra.sjukfall.dto.RekoStatusTypeDTO;
import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.auth.RehabstodUserPreferences;
import se.inera.intyg.rehabstod.auth.RehabstodUserPreferences.Preference;
import se.inera.intyg.rehabstod.auth.authorities.AuthoritiesConstants;
import se.inera.intyg.rehabstod.integration.it.dto.PopulateFiltersRequestDTO;
import se.inera.intyg.rehabstod.integration.it.dto.PopulateFiltersResponseDTO;
import se.inera.intyg.rehabstod.integration.it.service.IntygstjanstRestIntegrationService;
import se.inera.intyg.rehabstod.service.Urval;
import se.inera.intyg.rehabstod.service.certificate.CertificateService;
import se.inera.intyg.rehabstod.service.diagnos.DiagnosKapitelService;
import se.inera.intyg.rehabstod.service.diagnos.dto.DiagnosKapitel;
import se.inera.intyg.rehabstod.service.filter.PopulateFiltersServiceImpl;
import se.inera.intyg.rehabstod.service.pu.PuService;
import se.inera.intyg.rehabstod.service.sjukfall.dto.PopulateLUFilterResponseDTO;
import se.inera.intyg.rehabstod.service.sjukfall.dto.UnansweredCommunicationFilterType;
import se.inera.intyg.rehabstod.service.sjukfall.nameresolver.SjukfallEmployeeNameResolver;
import se.inera.intyg.rehabstod.service.user.FeatureService;
import se.inera.intyg.rehabstod.service.user.UserService;
import se.inera.intyg.rehabstod.web.controller.api.dto.GetDoctorsForUnitResponse;

@ExtendWith(MockitoExtension.class)
public class PopulateFiltersServiceTest {

    @Mock
    UserService userService;

    @Mock
    IntygstjanstRestIntegrationService intygstjanstRestIntegrationService;

    @Mock
    DiagnosKapitelService diagnosKapitelService;

    @Mock
    SjukfallEmployeeNameResolver sjukfallEmployeeNameResolver;

    @Mock
    PuService puService;

    @Mock
    FeatureService featureService;
    @Mock
    CertificateService certificateService;

    @InjectMocks
    PopulateFiltersServiceImpl populateActiveFilters;

    static final String HSA_ID = "HSA_ID";
    static final String HSA_ID2 = "HSA_ID2";
    static final String NAME_1 = "AAA1";
    static final String NAME_2 = "BBB2";
    static final String SUB_UNIT_ID = "SUB_UNIT_ID";
    static final String UNIT_ID = "UNIT_ID";
    static final String GAP = "5";
    static final String DAYS = "10";
    static final char LETTER_TO = 'A';
    static final char LETTER_FROM = 'B';
    static final int NUMBER_TO = 1;
    static final int NUMBER_FROM = 2;
    static final String DIAGNOSIS_CHAPTER_NAME = "Name";
    static final PopulateFiltersRequestDTO EXPECTED_REQUEST = new PopulateFiltersRequestDTO();
    static final int TOTAL_NUMBER_OF_SICK_LEAVES = 10;
    static final boolean HAS_ONGOING_SICK_LEAVES = true;

    RehabstodUser user;
    Vardenhet unit;
    SelectableVardenhet careGiverUnit;
    Vardgivare careGiver;
    DiagnosKategori diagnosisChapterTo = new DiagnosKategori(LETTER_TO, NUMBER_TO);
    DiagnosKategori diagnosisChapterFrom = new DiagnosKategori(LETTER_FROM, NUMBER_FROM);
    RekoStatusTypeDTO rekoStatus = new RekoStatusTypeDTO("REKO_1", "Ingen");
    OccupationTypeDTO occupationTypeDTO = new OccupationTypeDTO("OCCUPATION_STUDIER", "Studier");
    se.inera.intyg.infra.sjukfall.dto.DiagnosKapitel enabledDiagnosisChapter =
        new se.inera.intyg.infra.sjukfall.dto.DiagnosKapitel(diagnosisChapterTo, diagnosisChapterFrom, DIAGNOSIS_CHAPTER_NAME);
    List<DiagnosKapitel> allDiagnosisChapters = Collections.singletonList(new DiagnosKapitel());

    @Nested
    class PopulateSickLeaveFilter {

        @BeforeEach
        void setup() {
            user = mock(RehabstodUser.class);
            careGiverUnit = mock(SelectableVardenhet.class);
            careGiver = mock(Vardgivare.class);
            unit = mock(Vardenhet.class);

            when(userService.getUser()).thenReturn(user);

            final var preferences = new HashMap<String, String>();
            preferences.put(Preference.MAX_ANTAL_DAGAR_MELLAN_INTYG.getBackendKeyName(), GAP);
            preferences.put(Preference.MAX_ANTAL_DAGAR_SEDAN_SJUKFALL_AVSLUT.getBackendKeyName(), DAYS);
            final var userPreferences = RehabstodUserPreferences.fromBackend(preferences);
            when(user.getPreferences()).thenReturn(userPreferences);

            final var response = new PopulateFiltersResponseDTO(
                List.of(Lakare.create(HSA_ID2, HSA_ID2), Lakare.create(HSA_ID, HSA_ID)),
                Collections.singletonList(enabledDiagnosisChapter),
                TOTAL_NUMBER_OF_SICK_LEAVES,
                HAS_ONGOING_SICK_LEAVES,
                Collections.singletonList(rekoStatus),
                Collections.singletonList(occupationTypeDTO)
            );
            when(intygstjanstRestIntegrationService.getPopulatedFiltersForActiveSickLeaves(any())).thenReturn(response);

            EXPECTED_REQUEST.setMaxDaysSinceSickLeaveCompleted(Integer.parseInt(DAYS));
            EXPECTED_REQUEST.setUnitId(UNIT_ID);

            when(diagnosKapitelService.getDiagnosKapitelList()).thenReturn(allDiagnosisChapters);
        }

        @Nested
        class UnansweredCommunication {

            @BeforeEach
            void setup() {
                when(user.getValdVardenhet()).thenReturn(unit);
                when(unit.getId()).thenReturn(UNIT_ID);

                when(sjukfallEmployeeNameResolver.getEmployeeName(HSA_ID)).thenReturn(NAME_1);
                when(sjukfallEmployeeNameResolver.getEmployeeName(HSA_ID2)).thenReturn(NAME_2);
            }

            @Test
            void shouldSetUnansweredCommunicationFilterTypeIds() {
                final var response = populateActiveFilters.populateSickLeaveFilters();

                assertEquals(4, response.getUnansweredCommunicationFilterTypes().size());
            }

            @Test
            void shouldSetUnansweredCommunicationFilterTypeId1() {
                final var response = populateActiveFilters.populateSickLeaveFilters();

                assertEquals(
                    UnansweredCommunicationFilterType.UNANSWERED_COMMUNICATION_FILTER_TYPE_1.toString(),
                    response.getUnansweredCommunicationFilterTypes().get(0).getId()
                );

                assertEquals(
                    UnansweredCommunicationFilterType.UNANSWERED_COMMUNICATION_FILTER_TYPE_1.getName(),
                    response.getUnansweredCommunicationFilterTypes().get(0).getName()
                );
            }

            @Test
            void shouldSetUnansweredCommunicationFilterTypeId2() {
                final var response = populateActiveFilters.populateSickLeaveFilters();

                assertEquals(
                    UnansweredCommunicationFilterType.UNANSWERED_COMMUNICATION_FILTER_TYPE_2.toString(),
                    response.getUnansweredCommunicationFilterTypes().get(1).getId()
                );

                assertEquals(
                    UnansweredCommunicationFilterType.UNANSWERED_COMMUNICATION_FILTER_TYPE_2.getName(),
                    response.getUnansweredCommunicationFilterTypes().get(1).getName()
                );
            }

            @Test
            void shouldSetUnansweredCommunicationFilterTypeId3() {
                final var response = populateActiveFilters.populateSickLeaveFilters();

                assertEquals(
                    UnansweredCommunicationFilterType.UNANSWERED_COMMUNICATION_FILTER_TYPE_3.toString(),
                    response.getUnansweredCommunicationFilterTypes().get(2).getId()
                );

                assertEquals(
                    UnansweredCommunicationFilterType.UNANSWERED_COMMUNICATION_FILTER_TYPE_3.getName(),
                    response.getUnansweredCommunicationFilterTypes().get(2).getName()
                );
            }

            @Test
            void shouldSetUnansweredCommunicationFilterTypeId4() {
                final var response = populateActiveFilters.populateSickLeaveFilters();

                assertEquals(
                    UnansweredCommunicationFilterType.UNANSWERED_COMMUNICATION_FILTER_TYPE_4.toString(),
                    response.getUnansweredCommunicationFilterTypes().get(3).getId()
                );

                assertEquals(
                    UnansweredCommunicationFilterType.UNANSWERED_COMMUNICATION_FILTER_TYPE_4.getName(),
                    response.getUnansweredCommunicationFilterTypes().get(3).getName()
                );
            }
        }

        @Nested
        class SRS {

            @BeforeEach
            void setup() {
                when(user.getValdVardenhet()).thenReturn(unit);
                when(unit.getId()).thenReturn(UNIT_ID);

                when(sjukfallEmployeeNameResolver.getEmployeeName(HSA_ID)).thenReturn(NAME_1);
                when(sjukfallEmployeeNameResolver.getEmployeeName(HSA_ID2)).thenReturn(NAME_2);
            }

            @Test
            void shouldSetSrsActivatedIfFeatureIsAvailable() {
                when(featureService.isFeatureActive(anyString())).thenReturn(true);

                final var response = populateActiveFilters.populateSickLeaveFilters();

                assertTrue(response.isSrsActivated());
            }

            @Test
            void shouldSetSrsNotActivatedIfFeatureIsNotAvailable() {
                when(featureService.isFeatureActive(anyString())).thenReturn(false);

                final var response = populateActiveFilters.populateSickLeaveFilters();

                assertFalse(response.isSrsActivated());
            }

            @Test
            void shouldCallFeatureServiceWithSrsFeatureString() {
                final var captor = ArgumentCaptor.forClass(String.class);
                populateActiveFilters.populateSickLeaveFilters();
                verify(featureService).isFeatureActive(captor.capture());

                assertEquals(AuthoritiesConstants.FEATURE_SRS, captor.getValue());
            }
        }

        @Nested
        class TestITRequest {

            @BeforeEach
            void setup() {
                when(sjukfallEmployeeNameResolver.getEmployeeName(HSA_ID)).thenReturn(NAME_1);
                when(sjukfallEmployeeNameResolver.getEmployeeName(HSA_ID2)).thenReturn(NAME_2);
            }

            @Nested
            class CareUnit {

                @BeforeEach
                void setup() {
                    when(user.getValdVardenhet()).thenReturn(unit);
                    when(unit.getId()).thenReturn(UNIT_ID);
                }

                @Test
                void shouldCreateRequestWithCorrectValuesWhenChosenUnit() {
                    final var captor = ArgumentCaptor.forClass(PopulateFiltersRequestDTO.class);
                    populateActiveFilters.populateSickLeaveFilters();

                    verify(intygstjanstRestIntegrationService).getPopulatedFiltersForActiveSickLeaves(captor.capture());
                    assertEquals(UNIT_ID, captor.getValue().getCareUnitId());
                    assertNull(captor.getValue().getUnitId());
                    assertEquals(Integer.parseInt(DAYS), captor.getValue().getMaxDaysSinceSickLeaveCompleted());
                }

                @Test
                void shouldSetDoctorIdIfUserIsDoctor() {
                    when(user.getHsaId()).thenReturn(HSA_ID);
                    when(user.getUrval()).thenReturn(Urval.ISSUED_BY_ME);

                    final var captor = ArgumentCaptor.forClass(PopulateFiltersRequestDTO.class);
                    populateActiveFilters.populateSickLeaveFilters();

                    verify(intygstjanstRestIntegrationService).getPopulatedFiltersForActiveSickLeaves(captor.capture());
                    assertEquals(HSA_ID, captor.getValue().getDoctorId());
                }

                @Test
                void shouldNotSetDoctorIdIfUserIsNotDoctor() {
                    when(user.getUrval()).thenReturn(Urval.ALL);

                    final var captor = ArgumentCaptor.forClass(PopulateFiltersRequestDTO.class);
                    populateActiveFilters.populateSickLeaveFilters();

                    verify(intygstjanstRestIntegrationService).getPopulatedFiltersForActiveSickLeaves(captor.capture());
                    assertNull(captor.getValue().getDoctorId());
                }

                @Test
                void shouldCreateRequestWithProtectedPersonFilterIdNull() {
                    when(puService.shouldFilterSickLeavesOnProtectedPerson(any())).thenReturn(true);

                    final var captor = ArgumentCaptor.forClass(PopulateFiltersRequestDTO.class);
                    populateActiveFilters.populateSickLeaveFilters();

                    verify(intygstjanstRestIntegrationService).getPopulatedFiltersForActiveSickLeaves(captor.capture());
                    assertNull(captor.getValue().getProtectedPersonFilterId());
                }

                @Test
                void shouldCreateRequestWithProtectedPersonFilterIdAsHsaId() {
                    when(puService.shouldFilterSickLeavesOnProtectedPerson(any())).thenReturn(false);
                    when(user.getHsaId()).thenReturn(HSA_ID);

                    final var captor = ArgumentCaptor.forClass(PopulateFiltersRequestDTO.class);
                    populateActiveFilters.populateSickLeaveFilters();

                    verify(intygstjanstRestIntegrationService).getPopulatedFiltersForActiveSickLeaves(captor.capture());
                    assertEquals(HSA_ID, captor.getValue().getProtectedPersonFilterId());
                }
            }

            @Nested
            class SubUnit {

                @Test
                void shouldCreateRequestWithCorrectValuesWhenChosenSubUnit() {
                    setupSubUnit();

                    final var captor = ArgumentCaptor.forClass(PopulateFiltersRequestDTO.class);
                    populateActiveFilters.populateSickLeaveFilters();

                    verify(intygstjanstRestIntegrationService).getPopulatedFiltersForActiveSickLeaves(captor.capture());
                    assertEquals(UNIT_ID, captor.getValue().getCareUnitId());
                    assertEquals(SUB_UNIT_ID, captor.getValue().getUnitId());
                    assertEquals(Integer.parseInt(DAYS), captor.getValue().getMaxDaysSinceSickLeaveCompleted());
                }
            }
        }

        @Nested
        class TestITResponse {

            @BeforeEach
            void setUp() {
                when(user.getValdVardenhet()).thenReturn(unit);
                when(unit.getId()).thenReturn(UNIT_ID);
                when(user.getUrval()).thenReturn(Urval.ALL);
            }

            @Test
            void shouldHandleNullValuesFromResponse() {
                final var responeDTO = new PopulateFiltersResponseDTO(
                    null,
                    null,
                    0,
                    false,
                    null,
                    null
                );
                when(intygstjanstRestIntegrationService.getPopulatedFiltersForActiveSickLeaves(any())).thenReturn(responeDTO);
                final var response = populateActiveFilters.populateSickLeaveFilters();
                assertEquals(0, response.getActiveDoctors().size());
            }

            @Nested
            class WithDoctorName {

                @BeforeEach
                void setup() {
                    when(sjukfallEmployeeNameResolver.getEmployeeName(HSA_ID)).thenReturn(NAME_1);
                    when(sjukfallEmployeeNameResolver.getEmployeeName(HSA_ID2)).thenReturn(NAME_2);
                }

                @Test
                void shouldConvertActiveDoctors() {
                    final var response = populateActiveFilters.populateSickLeaveFilters();
                    assertEquals(2, response.getActiveDoctors().size());
                }

                @Test
                void shouldConvertActiveDoctorsSortedByName() {
                    final var response = populateActiveFilters.populateSickLeaveFilters();
                    assertEquals(HSA_ID, response.getActiveDoctors().get(0).getHsaId());
                    assertEquals(HSA_ID2, response.getActiveDoctors().get(1).getHsaId());
                }

                @Test
                void shouldConvertHsaIdForDoctor() {
                    final var response = populateActiveFilters.populateSickLeaveFilters();
                    assertEquals(HSA_ID, response.getActiveDoctors().get(0).getHsaId());
                }

                @Test
                void shouldConvertDoctorName() {
                    final var response = populateActiveFilters.populateSickLeaveFilters();
                    assertEquals(NAME_1, response.getActiveDoctors().get(0).getNamn());
                }

                @Test
                void shouldDecorateDuplicateDoctorNamesWithHsaId() {
                    populateActiveFilters.populateSickLeaveFilters();
                    verify(sjukfallEmployeeNameResolver, times(1))
                        .decorateAnyDuplicateNamesWithHsaId(anyList());
                }

                @Test
                void shouldConvertAllDiagnosisChapters() {
                    final var response = populateActiveFilters.populateSickLeaveFilters();
                    assertEquals(allDiagnosisChapters, response.getAllDiagnosisChapters());
                }

                @Test
                void shouldConvertNbrOfSickLeaves() {
                    final var response = populateActiveFilters.populateSickLeaveFilters();
                    assertEquals(TOTAL_NUMBER_OF_SICK_LEAVES, response.getNbrOfSickLeaves());
                }

                @Test
                void shouldConvertHasOngoingSickLeaves() {
                    final var response = populateActiveFilters.populateSickLeaveFilters();
                    assertEquals(HAS_ONGOING_SICK_LEAVES, response.isHasOngoingSickLeaves());
                }

                @Test
                void shouldConvertRekoStatusId() {
                    final var response = populateActiveFilters.populateSickLeaveFilters();
                    assertEquals(rekoStatus.getId(), response.getRekoStatusTypes().get(0).getId());
                }

                @Test
                void shouldConvertRekoStatusName() {
                    final var response = populateActiveFilters.populateSickLeaveFilters();
                    assertEquals(rekoStatus.getName(), response.getRekoStatusTypes().get(0).getName());
                }

                @Test
                void shouldConvertOccupationTypeId() {
                    final var response = populateActiveFilters.populateSickLeaveFilters();
                    assertEquals(occupationTypeDTO.getId(), response.getOccupationTypes().get(0).getId());
                }

                @Test
                void shouldConvertOccupationTypeName() {
                    final var response = populateActiveFilters.populateSickLeaveFilters();
                    assertEquals(occupationTypeDTO.getName(), response.getOccupationTypes().get(0).getName());
                }

                @Nested
                class DiagnosisChapters {

                    @Test
                    void shouldConvertEnabledDiagnosisChapters() {
                        when(user.getValdVardenhet()).thenReturn(unit);
                        when(unit.getId()).thenReturn(UNIT_ID);

                        final var response = populateActiveFilters.populateSickLeaveFilters();
                        assertEquals(1, response.getEnabledDiagnosisChapters().size());
                    }

                    @Test
                    void shouldConvertEnabledDiagnosisChapterTo() {
                        when(user.getValdVardenhet()).thenReturn(unit);
                        when(unit.getId()).thenReturn(UNIT_ID);

                        final var response = populateActiveFilters.populateSickLeaveFilters();
                        final var diagnosisChapter = response.getEnabledDiagnosisChapters().get(0);
                        assertEquals(enabledDiagnosisChapter.getTo().getLetter(), diagnosisChapter.getTo().getLetter());
                        assertEquals(enabledDiagnosisChapter.getTo().getNumber(), diagnosisChapter.getTo().getNumber());
                    }

                    @Test
                    void shouldConvertEnabledDiagnosisChapterFrom() {
                        when(user.getValdVardenhet()).thenReturn(unit);
                        when(unit.getId()).thenReturn(UNIT_ID);

                        final var response = populateActiveFilters.populateSickLeaveFilters();
                        final var diagnosisChapter = response.getEnabledDiagnosisChapters().get(0);
                        assertEquals(enabledDiagnosisChapter.getFrom().getLetter(), diagnosisChapter.getFrom().getLetter());
                        assertEquals(enabledDiagnosisChapter.getFrom().getNumber(), diagnosisChapter.getFrom().getNumber());
                    }

                    @Test
                    void shouldConvertEnabledDiagnosisChapterId() {
                        when(user.getValdVardenhet()).thenReturn(unit);
                        when(unit.getId()).thenReturn(UNIT_ID);

                        final var response = populateActiveFilters.populateSickLeaveFilters();
                        final var diagnosisChapter = response.getEnabledDiagnosisChapters().get(0);
                        assertEquals(enabledDiagnosisChapter.getId(), diagnosisChapter.getId());
                    }

                    @Test
                    void shouldConvertEnabledDiagnosisChapterName() {
                        when(user.getValdVardenhet()).thenReturn(unit);
                        when(unit.getId()).thenReturn(UNIT_ID);

                        final var response = populateActiveFilters.populateSickLeaveFilters();
                        final var diagnosisChapter = response.getEnabledDiagnosisChapters().get(0);
                        assertEquals(enabledDiagnosisChapter.getName(), diagnosisChapter.getName());
                    }
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

    @Nested
    class PopulateLUFilter {

        @Test
        void shouldReturnEmptyResponse() {
            when(diagnosKapitelService.getDiagnosKapitelList()).thenReturn(Collections.emptyList());
            when(certificateService.getDoctorsForUnit()).thenReturn(new GetDoctorsForUnitResponse(Collections.emptyList()));
            final var expectedResponse = PopulateLUFilterResponseDTO.builder()
                .doctors(Collections.emptyList())
                .allDiagnosisChapters(Collections.emptyList())
                .build();
            final var response = populateActiveFilters.populateLUFilters();
            assertEquals(expectedResponse, response);
        }

        @Test
        void shouldReturnAllDiagnosisChapters() {
            when(diagnosKapitelService.getDiagnosKapitelList()).thenReturn(allDiagnosisChapters);
            when(certificateService.getDoctorsForUnit()).thenReturn(new GetDoctorsForUnitResponse(Collections.emptyList()));
            final var expectedResponse = PopulateLUFilterResponseDTO.builder()
                .doctors(Collections.emptyList())
                .allDiagnosisChapters(allDiagnosisChapters)
                .build();
            final var response = populateActiveFilters.populateLUFilters();
            assertEquals(expectedResponse, response);
        }

        @Test
        void shouldReturnAllDoctorsForUnit() {
            when(diagnosKapitelService.getDiagnosKapitelList()).thenReturn(Collections.emptyList());
            final var doctor = new se.inera.intyg.rehabstod.web.model.Lakare();
            when(certificateService.getDoctorsForUnit()).thenReturn(new GetDoctorsForUnitResponse(List.of(doctor)));
            final var expectedResponse = PopulateLUFilterResponseDTO.builder()
                .doctors(List.of(doctor))
                .allDiagnosisChapters(Collections.emptyList())
                .build();
            final var response = populateActiveFilters.populateLUFilters();
            assertEquals(expectedResponse, response);
        }
    }
}
