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
package se.inera.intyg.rehabstod.web.controller.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableMap;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.infra.integration.hsatk.model.legacy.Vardenhet;
import se.inera.intyg.infra.integration.hsatk.model.legacy.Vardgivare;
import se.inera.intyg.infra.logmessages.ActivityType;
import se.inera.intyg.infra.logmessages.ResourceType;
import se.inera.intyg.infra.security.common.model.Feature;
import se.inera.intyg.infra.security.common.model.Role;
import se.inera.intyg.infra.sjukfall.dto.IntygParametrar;
import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.auth.RehabstodUserPreferences;
import se.inera.intyg.rehabstod.auth.RehabstodUserPreferences.Preference;
import se.inera.intyg.rehabstod.auth.authorities.AuthoritiesConstants;
import se.inera.intyg.rehabstod.auth.pdl.PDLActivityStore;
import se.inera.intyg.rehabstod.integration.srs.model.RiskSignal;
import se.inera.intyg.rehabstod.service.Urval;
import se.inera.intyg.rehabstod.service.pdl.LogService;
import se.inera.intyg.rehabstod.service.sjukfall.SjukfallService;
import se.inera.intyg.rehabstod.service.sjukfall.dto.SjfMetaData;
import se.inera.intyg.rehabstod.service.sjukfall.dto.SjukfallPatientResponse;
import se.inera.intyg.rehabstod.service.sjukfall.util.PatientIdEncryption;
import se.inera.intyg.rehabstod.service.user.UserService;
import se.inera.intyg.rehabstod.web.controller.api.dto.GetSjukfallForPatientRequest;
import se.inera.intyg.rehabstod.web.model.Diagnos;
import se.inera.intyg.rehabstod.web.model.Lakare;
import se.inera.intyg.rehabstod.web.model.Patient;
import se.inera.intyg.rehabstod.web.model.PatientData;
import se.inera.intyg.rehabstod.web.model.SjukfallEnhet;
import se.inera.intyg.rehabstod.web.model.SjukfallPatient;
import se.inera.intyg.schemas.contract.Personnummer;

/**
 * Created by Magnus Ekstrand on 03/02/16.
 */
@ExtendWith(MockitoExtension.class)
public class SjukfallControllerTest {

    public static final String PATIENT_ID_1 = "19121212-1212";
    public static final String PATIENT_ID_2 = "20121212-1212";
    public static final String PATIENT_ID_3 = "19840921-9287";
    private static final Vardenhet DEFAULT_VARDENHET = new Vardenhet("ve123", "Vardenhet_123");
    private static final Vardgivare DEFAULT_VARDGIVARE = new Vardgivare("vg987", "Vardgivare_987");

    private final List<Vardenhet> vardenheter = new ArrayList<>() {{
        add(DEFAULT_VARDENHET);
        add(new Vardenhet("ve234", "Vardenhet_234"));
        add(new Vardenhet("ve345", "Vardenhet_345"));
        add(new Vardenhet("ve456", "Vardenhet_456"));
        add(new Vardenhet("ve567", "Vardenhet_567"));
    }};

    private final List<Vardgivare> vardgivare = new ArrayList<>() {{
        add(DEFAULT_VARDGIVARE);
        add(new Vardgivare("vg876", "Vardgivare_876"));
        add(new Vardgivare("vg765", "Vardgivare_765"));
    }};

    @Mock
    RehabstodUser rehabstodUser;
    @Mock
    UserService userService;
    @Mock
    LogService logService;
    @Mock
    private SjukfallService sjukfallService;

    @InjectMocks
    private SjukfallController sjukfallController;

    @Mock
    private PatientIdEncryption patientIdEncryption;

    @Nested
    class GetSjukfallByUnitTests {

        @BeforeEach
        public void init() {
            when(userService.getUser()).thenReturn(rehabstodUser);
            when(rehabstodUser.getValdVardenhet()).thenReturn(DEFAULT_VARDENHET);
            when(rehabstodUser.getUrval()).thenReturn(Urval.ALL);

            final var preferences = RehabstodUserPreferences.empty();
            preferences.updatePreference(Preference.MAX_ANTAL_DAGAR_MELLAN_INTYG, "5");
            preferences.updatePreference(Preference.MAX_ANTAL_DAGAR_SEDAN_SJUKFALL_AVSLUT, "0");
            when(rehabstodUser.getPreferences()).thenReturn(preferences);
        }

    }

    @Nested
    class GetSjukfallByPatientTests {

        @BeforeEach
        public void init() {
            when(userService.getUser()).thenReturn(rehabstodUser);
            when(rehabstodUser.getValdVardenhet()).thenReturn(DEFAULT_VARDENHET);
            when(rehabstodUser.getUrval()).thenReturn(Urval.ALL);
            when(rehabstodUser.getValdVardgivare()).thenReturn(DEFAULT_VARDGIVARE);

            final var preferences = RehabstodUserPreferences.empty();
            preferences.updatePreference(Preference.MAX_ANTAL_DAGAR_MELLAN_INTYG, "5");
            preferences.updatePreference(Preference.MAX_ANTAL_DAGAR_SEDAN_SJUKFALL_AVSLUT, "0");
            when(rehabstodUser.getPreferences()).thenReturn(preferences);
        }

        @Test
        void getSjukfallByPatientWhenIntygFromSingleUnit() {
            final var patientId = PATIENT_ID_1;
            final var a = createSjukFallPatient(patientId, 1, 0, DEFAULT_VARDENHET, DEFAULT_VARDGIVARE);
            final var result = List.of(a);
            final var request = new GetSjukfallForPatientRequest();
            request.setPatientId(patientId);
            final var personnummer = Personnummer.createPersonnummer(PATIENT_ID_1).orElseThrow();

            try (MockedStatic<PDLActivityStore> pdlActivityStore = mockStatic(PDLActivityStore.class)) {
                when(sjukfallService.getByPatient(anyString(), anyString(), isNull(), anyString(),
                    any(Urval.class), any(IntygParametrar.class), anyCollection(), anyCollection()))
                    .thenReturn(new SjukfallPatientResponse(result, new SjfMetaData(), false, false));

                sjukfallController.getSjukfallForPatient(request);

                verify(sjukfallService).getByPatient(eq(DEFAULT_VARDGIVARE.getId()), eq(DEFAULT_VARDENHET.getId()), isNull(),
                    eq(personnummer.getPersonnummer()), any(Urval.class), any(IntygParametrar.class), anyCollection(), anyCollection());

                pdlActivityStore.verify(() -> PDLActivityStore.isActivityInStore(anyString(), eq(patientId), eq(ActivityType.READ),
                    eq(ResourceType.RESOURCE_TYPE_INTYG), anyMap()));

                verify(logService).logSjukfallData(any(PatientData.class), eq(ActivityType.READ), eq(ResourceType.RESOURCE_TYPE_INTYG));
            }
        }

        @Test
        void getSjukfallByPatientWhenIntygFromMultipleUnits() {
            final var patientId = PATIENT_ID_1;
            final var a = createSjukFallPatient(patientId, 1, 0, DEFAULT_VARDENHET, DEFAULT_VARDGIVARE);
            final var b = createSjukFallPatient(patientId, 1, 0, vardenheter.get(1), DEFAULT_VARDGIVARE);
            final var c = createSjukFallPatient(patientId, 1, 0, vardenheter.get(2), DEFAULT_VARDGIVARE);
            final var d = createSjukFallPatient(patientId, 1, 0, vardenheter.get(3), vardgivare.get(1));
            final var result = List.of(a, b, c, d);
            final var request = new GetSjukfallForPatientRequest();
            request.setPatientId(patientId);
            final var personnummer = Personnummer.createPersonnummer(PATIENT_ID_1).orElseThrow();

            try (MockedStatic<PDLActivityStore> pdlActivityStore = mockStatic(PDLActivityStore.class)) {
                when(sjukfallService.getByPatient(anyString(), anyString(), isNull(), anyString(),
                    any(Urval.class), any(IntygParametrar.class), anyCollection(), anyCollection()))
                    .thenReturn(new SjukfallPatientResponse(result, new SjfMetaData(), false, false));

                sjukfallController.getSjukfallForPatient(request);

                verify(sjukfallService).getByPatient(eq(DEFAULT_VARDGIVARE.getId()), eq(DEFAULT_VARDENHET.getId()), isNull(),
                    eq(personnummer.getPersonnummer()), any(Urval.class), any(IntygParametrar.class), anyCollection(), anyCollection());

                pdlActivityStore.verify(() -> PDLActivityStore.isActivityInStore(anyString(), eq(patientId), eq(ActivityType.READ),
                    eq(ResourceType.RESOURCE_TYPE_INTYG), anyMap()), times(4));

                verify(logService, times(4)).logSjukfallData(any(PatientData.class), eq(ActivityType.READ),
                    eq(ResourceType.RESOURCE_TYPE_INTYG));
            }
        }

        @Test
        void shallNotDecryptAnNonEncryptedPatientId() {
            final var expectedPatientId = "191212121212";
            final var patientIdCaptor = ArgumentCaptor.forClass(String.class);
            final var request = new GetSjukfallForPatientRequest();
            request.setPatientId(expectedPatientId);

            when(sjukfallService.getByPatient(any(), any(), any(), patientIdCaptor.capture(), any(), any(), any(),
                any())).thenReturn(mock(SjukfallPatientResponse.class));

            sjukfallController.getSjukfallForPatient(request);

            assertEquals(expectedPatientId, patientIdCaptor.getValue());
        }

        @Test
        void shallDecryptAnEncryptedPatientId() {
            final var expectedPatientId = "191212121212";
            final var encryptedPatientId = "HjdOIDIjREBrfko";
            final var patientIdCaptor = ArgumentCaptor.forClass(String.class);
            final var request = new GetSjukfallForPatientRequest();
            request.setEncryptedPatientId(encryptedPatientId);

            when(patientIdEncryption.decrypt(encryptedPatientId)).thenReturn(expectedPatientId);

            when(sjukfallService.getByPatient(any(), any(), any(), patientIdCaptor.capture(), any(), any(), any(),
                any())).thenReturn(mock(SjukfallPatientResponse.class));

            sjukfallController.getSjukfallForPatient(request);

            assertEquals(expectedPatientId, patientIdCaptor.getValue());
        }
    }

    @Nested
    class PdlLogTests {

        @BeforeEach
        public void init() {
            when(userService.getUser()).thenReturn(rehabstodUser);
        }

        @Test
        void pdlLogSjukfallPatientWhenIntygFromSingleUnit() {
            final var patientId = PATIENT_ID_1;
            final var user = buildConcreteUser();
            final var a = createSjukFallPatient(patientId, 1, 0, DEFAULT_VARDENHET, DEFAULT_VARDGIVARE);
            final var finalList = List.of(a);

            final var request = new GetSjukfallForPatientRequest();
            request.setPatientId(patientId);

            when(userService.getUser()).thenReturn(user);
            when(sjukfallService.getByPatient(anyString(), anyString(), anyString(), anyString(), any(Urval.class),
                any(IntygParametrar.class), anyCollection(), anyCollection()))
                .thenReturn(new SjukfallPatientResponse(finalList, new SjfMetaData(), false, false));

            sjukfallController.getSjukfallForPatient(request);

            assertEquals(1, user.getStoredActivities().size());
            assertPdlActivityEntries(user, DEFAULT_VARDENHET, ResourceType.RESOURCE_TYPE_INTYG);
            verify(logService).logSjukfallData(any(PatientData.class), eq(ActivityType.READ), eq(ResourceType.RESOURCE_TYPE_INTYG));
        }

        @Test
        void pdlLogSjukfallPatientWhenIntygFromMultipleUnits() {
            final var patientId = PATIENT_ID_1;
            final var user = buildConcreteUser();
            final var a = createSjukFallPatient(patientId, 3, 0, DEFAULT_VARDENHET, DEFAULT_VARDGIVARE);
            final var b = createSjukFallPatient(patientId, 1, 0, vardenheter.get(1), DEFAULT_VARDGIVARE);
            final var c = createSjukFallPatient(patientId, 2, 0, vardenheter.get(2), DEFAULT_VARDGIVARE);
            final var d = createSjukFallPatient(patientId, 1, 0, vardenheter.get(3), vardgivare.get(1));
            final var finalList = List.of(a, b, c, d);

            final var request = new GetSjukfallForPatientRequest();
            request.setPatientId(patientId);

            when(userService.getUser()).thenReturn(user);

            when(sjukfallService.getByPatient(anyString(), anyString(), anyString(), anyString(), any(Urval.class),
                any(IntygParametrar.class), anyCollection(), anyCollection()))
                .thenReturn(new SjukfallPatientResponse(finalList, new SjfMetaData(), false, false));

            sjukfallController.getSjukfallForPatient(request);

            assertEquals(4, user.getStoredActivities().size());

            // Assert entries - one for each unit
            assertPdlActivityEntries(user, DEFAULT_VARDENHET, ResourceType.RESOURCE_TYPE_INTYG);
            assertPdlActivityEntries(user, vardenheter.get(1), ResourceType.RESOURCE_TYPE_INTYG);
            assertPdlActivityEntries(user, vardenheter.get(2), ResourceType.RESOURCE_TYPE_INTYG);
            assertPdlActivityEntries(user, vardenheter.get(3), ResourceType.RESOURCE_TYPE_INTYG);

            verify(logService, times(4)).logSjukfallData(
                any(PatientData.class), eq(ActivityType.READ), eq(ResourceType.RESOURCE_TYPE_INTYG));
        }

        @Test
        void pdlLogSjukfallPatientShouldTriggerSRSLogEntryIfFeatureIsActive() {
            final var patientId = PATIENT_ID_1;
            final var user = buildConcreteUser();

            final var feature = new Feature();
            feature.setGlobal(true);
            user.setFeatures(Map.of(AuthoritiesConstants.FEATURE_SRS, feature));

            // Should trigger one history PDL item.
            final var a = createSjukFallPatient(patientId, 1, 0, DEFAULT_VARDENHET, DEFAULT_VARDGIVARE);
            // Should trigger one risk PDL item.
            final var b = createSjukFallPatient(patientId, 3, 2, DEFAULT_VARDENHET, DEFAULT_VARDGIVARE);

            final var finalList = List.of(a, b);
            final var request = new GetSjukfallForPatientRequest();
            request.setPatientId(patientId);

            when(userService.getUser()).thenReturn(user);
            when(sjukfallService.getByPatient(anyString(), anyString(), anyString(), anyString(), any(Urval.class),
                any(IntygParametrar.class), anyCollection(), anyCollection()))
                .thenReturn(new SjukfallPatientResponse(finalList, new SjfMetaData(), false, false));

            sjukfallController.getSjukfallForPatient(request);

            // Expect entries for ONE enhet
            assertEquals(1, user.getStoredActivities().size());

            final var pdlActivityEntries = user.getStoredActivities().get(DEFAULT_VARDENHET.getId());
            assertEquals(2, pdlActivityEntries.size());

            // Assert entries - expect one INTYG and one SRS log entry.
            assertPdlActivityEntries(user, DEFAULT_VARDENHET, ResourceType.RESOURCE_TYPE_INTYG);
            assertPdlActivityEntries(user, DEFAULT_VARDENHET, ResourceType.RESOURCE_TYPE_PREDIKTION_SRS);

            verify(logService, times(1)).logSjukfallData(any(PatientData.class),
                eq(ActivityType.READ), eq(ResourceType.RESOURCE_TYPE_INTYG));
            verify(logService, times(1)).logSjukfallData(any(PatientData.class),
                eq(ActivityType.READ), eq(ResourceType.RESOURCE_TYPE_PREDIKTION_SRS));
        }

        @Test
        void pdlLogSjukfallPatientShouldNotTriggerSRSLogEntryIfFeatureISInactive() {
            final var patientId = PATIENT_ID_1;
            final var user = buildConcreteUser();

            final var feature = new Feature();
            feature.setGlobal(true);
            user.setFeatures(Map.of(AuthoritiesConstants.ROLE_LAKARE, feature));

            final var sjukfallPatient1 = createSjukFallPatient(patientId, 1, 0, DEFAULT_VARDENHET, DEFAULT_VARDGIVARE);
            final var sjukfallPatient2 = createSjukFallPatient(patientId, 3, 2, DEFAULT_VARDENHET, DEFAULT_VARDGIVARE);

            final var listOfSjukfallPatient = List.of(sjukfallPatient1, sjukfallPatient2);
            final var request = new GetSjukfallForPatientRequest();
            request.setPatientId(patientId);

            when(userService.getUser()).thenReturn(user);
            when(sjukfallService.getByPatient(anyString(), anyString(), anyString(), anyString(), any(Urval.class),
                any(IntygParametrar.class), anyCollection(), anyCollection()))
                .thenReturn(new SjukfallPatientResponse(listOfSjukfallPatient, new SjfMetaData(), false, false));

            sjukfallController.getSjukfallForPatient(request);

            assertEquals(1, user.getStoredActivities().size());

            final var pdlActivityEntries = user.getStoredActivities().get(DEFAULT_VARDENHET.getId());
            assertEquals(1, pdlActivityEntries.size());

            assertPdlActivityEntries(user, DEFAULT_VARDENHET, ResourceType.RESOURCE_TYPE_INTYG);

            verify(logService, times(1)).logSjukfallData(any(PatientData.class),
                eq(ActivityType.READ), eq(ResourceType.RESOURCE_TYPE_INTYG));
            verify(logService, times(0)).logSjukfallData(any(PatientData.class),
                eq(ActivityType.READ), eq(ResourceType.RESOURCE_TYPE_PREDIKTION_SRS));
        }
    }

    private void assertPdlActivityEntries(RehabstodUser user, Vardenhet vardenhet, ResourceType resourceType) {
        final var pdlActivityEntries = user.getStoredActivities().get(vardenhet.getId());

        // Expect one INTYG with a certain ResourceType
        assertEquals(1L, pdlActivityEntries.stream()
            .filter(entry -> entry.getResourceType() == resourceType)
            .count());
    }

    private RehabstodUser buildConcreteUser() {
        final var user = new RehabstodUser("user-1", "Hej Hejssansson", true);
        final var vg = DEFAULT_VARDGIVARE;
        final var ve = DEFAULT_VARDENHET;
        vg.getVardenheter().add(ve);
        user.setVardgivare(List.of(vg));
        user.setValdVardgivare(vg);
        user.setValdVardenhet(ve);
        user.setRoles(ImmutableMap.of(AuthoritiesConstants.ROLE_LAKARE, new Role()));
        user.getPreferences().updatePreference(Preference.MAX_ANTAL_DAGAR_MELLAN_INTYG, "5");
        user.getPreferences().updatePreference(Preference.MAX_ANTAL_DAGAR_SEDAN_SJUKFALL_AVSLUT, "0");
        return user;
    }

    private static SjukfallEnhet createSjukFallEnhet(String personNummer) {
        // CHECKSTYLE:OFF MagicNumber
        final var isf = new SjukfallEnhet();

        final var lakare = new Lakare("123456-0987", "Hr Doktor");
        isf.setLakare(lakare);

        final var patient = new Patient(personNummer, "patient " + personNummer);
        isf.setPatient(patient);

        // Not really interested in these properties, but the sjukfall equals /hashcode will fail without them
        final var diagnos = new Diagnos("M16", "M16", "diagnosnamn");
        diagnos.setKapitel("M00-M99");
        isf.setDiagnos(diagnos);

        isf.setDiagnos(diagnos);
        isf.setStart(LocalDate.now());
        isf.setSlut(LocalDate.now());
        isf.setDagar(1);
        isf.setIntyg(1);
        isf.setGrader(new ArrayList<>());
        isf.setAktivGrad(50);

        return isf;
        // CHECKSTYLE:ON MagicNumber
    }

    private static SjukfallEnhet createSjukFallEnhet(String personNummer, boolean includeRisk) {
        final var isf = createSjukFallEnhet(personNummer);
        if (includeRisk) {
            isf.setRiskSignal(new RiskSignal("intyg-1", 3, "Descr.", isf.getStart().atTime(12, 0)));
        }
        return isf;
    }

    private SjukfallPatient createSjukFallPatient(String personNummer, int numberOfIntyg, int numberOfIntygHavingRisk,
        Vardenhet vardenhet, Vardgivare vardgivare) {

        final var sp = new SjukfallPatient();
        sp.setIntyg(new ArrayList<>());
        for (int a = 0; a < numberOfIntyg; a++) {
            PatientData pd = new PatientData();
            pd.setPatient(new Patient(personNummer, "Namnsson"));
            pd.setVardenhetId(vardenhet.getId());
            pd.setVardenhetNamn(vardenhet.getNamn());
            pd.setVardgivareId(vardgivare.getId());
            pd.setVardgivareNamn(vardgivare.getNamn());

            if (a < numberOfIntygHavingRisk) {
                pd.setRiskSignal(new RiskSignal("intyg-123", 3, "desc", LocalDateTime.now()));
            }
            sp.getIntyg().add(pd);
        }

        return sp;
    }
}
