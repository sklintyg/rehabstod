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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableMap;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import se.inera.intyg.infra.integration.hsatk.model.legacy.Vardenhet;
import se.inera.intyg.infra.integration.hsatk.model.legacy.Vardgivare;
import se.inera.intyg.infra.logmessages.ActivityType;
import se.inera.intyg.infra.logmessages.ResourceType;
import se.inera.intyg.infra.security.common.model.Role;
import se.inera.intyg.infra.sjukfall.dto.IntygParametrar;
import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.auth.RehabstodUserPreferences;
import se.inera.intyg.rehabstod.auth.RehabstodUserPreferences.Preference;
import se.inera.intyg.rehabstod.auth.authorities.AuthoritiesConstants;
import se.inera.intyg.rehabstod.auth.pdl.PDLActivityStore;
import se.inera.intyg.rehabstod.integration.srs.model.RiskSignal;
import se.inera.intyg.rehabstod.service.Urval;
import se.inera.intyg.rehabstod.service.export.pdf.PdfExportService;
import se.inera.intyg.rehabstod.service.pdl.LogService;
import se.inera.intyg.rehabstod.service.sjukfall.SjukfallService;
import se.inera.intyg.rehabstod.service.sjukfall.dto.SjfMetaData;
import se.inera.intyg.rehabstod.service.sjukfall.dto.SjukfallEnhetResponse;
import se.inera.intyg.rehabstod.service.sjukfall.dto.SjukfallPatientResponse;
import se.inera.intyg.rehabstod.service.user.UserService;
import se.inera.intyg.rehabstod.web.controller.api.dto.GetSjukfallForPatientRequest;
import se.inera.intyg.rehabstod.web.controller.api.dto.GetSjukfallRequest;
import se.inera.intyg.rehabstod.web.controller.api.dto.PrintSjukfallRequest;
import se.inera.intyg.rehabstod.web.model.Diagnos;
import se.inera.intyg.rehabstod.web.model.Lakare;
import se.inera.intyg.rehabstod.web.model.Patient;
import se.inera.intyg.rehabstod.web.model.PatientData;
import se.inera.intyg.rehabstod.web.model.SjukfallEnhet;
import se.inera.intyg.rehabstod.web.model.SjukfallPatient;

/**
 * Created by Magnus Ekstrand on 03/02/16.
 */
@ExtendWith(MockitoExtension.class)
public class SjukfallControllerTest {

    public static final String PATIENT_ID_1 = "19121212-1212";
    public static final String PATIENT_ID_2 = "20121212-1212";
    public static final String PATIENT_ID_3 = "19840921-9287";
    private final Vardenhet DEFAULT_VARDENHET = new Vardenhet("ve123", "Vardenhet_123");
    private final Vardgivare DEFAULT_VARDGIVARE = new Vardgivare("vg987", "Vardgivare_987");

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
    PdfExportService pdfExportService;
    @Mock
    private SjukfallService sjukfallService;

    @InjectMocks
    private SjukfallController sjukfallController = new SjukfallController();

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

        @Test
        public void testGetSjukfallByUnit() {
            final var a = createSjukFallEnhet(PATIENT_ID_1);
            final var b = createSjukFallEnhet(PATIENT_ID_2);
            final var c = createSjukFallEnhet(PATIENT_ID_3);
            final var result = List.of(a, b);
            final var toLog = List.of(c);
            final var request = new GetSjukfallRequest();

            try (MockedStatic<PDLActivityStore> pdlActivityStore = mockStatic(PDLActivityStore.class)) {
                pdlActivityStore.when(() -> PDLActivityStore.getActivitiesNotInStore(anyString(), anyList(), eq(ActivityType.READ),
                    eq(ResourceType.RESOURCE_TYPE_SJUKFALL), anyMap())).thenReturn(toLog);
                when(sjukfallService.getByUnit(anyString(), isNull(), isNull(), any(Urval.class), any(IntygParametrar.class)))
                    .thenReturn(new SjukfallEnhetResponse(result, false, false));

                sjukfallController.getSjukfallForCareUnit(request);

                pdlActivityStore.verify(() -> PDLActivityStore.getActivitiesNotInStore(anyString(), anyList(), eq(ActivityType.READ),
                    eq(ResourceType.RESOURCE_TYPE_SJUKFALL), anyMap()));

                verify(sjukfallService).getByUnit(anyString(), isNull(), isNull(), any(Urval.class), any(IntygParametrar.class));
                verify(logService).logSjukfallData(eq(toLog), eq(ActivityType.READ), eq(ResourceType.RESOURCE_TYPE_SJUKFALL));
            }
        }

        @Test
        public void testGetSjukfallByUnitAsPDF() {
            final var a = createSjukFallEnhet(PATIENT_ID_1);
            final var b = createSjukFallEnhet(PATIENT_ID_2);
            final var c = createSjukFallEnhet(PATIENT_ID_3);
            final var allSjukFall = List.of(a, b, c);
            final var finalList =  List.of(a, b);
            final var toLog = List.of(c);

            final var servletRequest = new MockHttpServletRequest();
            servletRequest.addHeader(HttpHeaders.USER_AGENT,
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/102.0.0.0 Safari/537.36");

            final var request = new PrintSjukfallRequest();
            request.setPersonnummer(List.of(PATIENT_ID_1, PATIENT_ID_2));

            try (MockedStatic<PDLActivityStore> pdlActivityStore = mockStatic(PDLActivityStore.class)) {
                pdlActivityStore.when(() -> PDLActivityStore.getActivitiesNotInStore(anyString(), eq(finalList), eq(ActivityType.PRINT),
                    eq(ResourceType.RESOURCE_TYPE_SJUKFALL), anyMap())).thenReturn(toLog);
                when(sjukfallService.getByUnit(anyString(), isNull(), isNull(), any(Urval.class), any(IntygParametrar.class)))
                    .thenReturn(new SjukfallEnhetResponse(allSjukFall, false, false));
                when(pdfExportService.export(eq(finalList), eq(request), eq(rehabstodUser), eq(allSjukFall.size())))
                    .thenReturn(new byte[0]);

                final var response = sjukfallController.getSjukfallForCareUnitAsPdf(request, servletRequest);

                pdlActivityStore.verify(() -> PDLActivityStore.addActivitiesToStore(anyString(), eq(toLog), eq(ActivityType.PRINT),
                    eq(ResourceType.RESOURCE_TYPE_SJUKFALL), anyMap()));

                pdlActivityStore.verify(() -> PDLActivityStore.getActivitiesNotInStore(anyString(), eq(finalList), eq(ActivityType.PRINT),
                    eq(ResourceType.RESOURCE_TYPE_SJUKFALL), anyMap()));

                verify(sjukfallService).getByUnit(anyString(), isNull(), isNull(), any(Urval.class), any(IntygParametrar.class));
                verify(logService).logSjukfallData(eq(toLog), eq(ActivityType.PRINT), eq(ResourceType.RESOURCE_TYPE_SJUKFALL));
                assertEquals(response.getStatusCode(), HttpStatus.OK);
                assertTrue(Objects.requireNonNull(response.getHeaders().get(HttpHeaders.CONTENT_DISPOSITION)).get(0)
                    .startsWith("inline; filename=\"sjukfall-" + DEFAULT_VARDENHET.getNamn() + "-"));
            }
        }

        @Test
        public void testGetSjukfallByUnitAsPDFFromInternetExplorer() {
            final var a = createSjukFallEnhet(PATIENT_ID_1);
            final var b = createSjukFallEnhet(PATIENT_ID_2);
            final var c = createSjukFallEnhet(PATIENT_ID_3);

            final var allSjukFall =  List.of(a, b, c);
            final var finalList =  List.of(a, b);
            final var toLog = List.of(c);

            final var servletRequest = new MockHttpServletRequest();
            servletRequest.addHeader(HttpHeaders.USER_AGENT,
                "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; Touch; rv:11.0) like Gecko");

            final var request = new PrintSjukfallRequest();
            request.setPersonnummer(List.of(PATIENT_ID_1, PATIENT_ID_2));

            try (MockedStatic<PDLActivityStore> pdlActivityStore = mockStatic(PDLActivityStore.class)) {
                pdlActivityStore.when(() -> PDLActivityStore.getActivitiesNotInStore(anyString(), eq(finalList), eq(ActivityType.PRINT),
                    eq(ResourceType.RESOURCE_TYPE_SJUKFALL), anyMap())).thenReturn(toLog);

                when(sjukfallService.getByUnit(anyString(), isNull(), isNull(), any(Urval.class), any(IntygParametrar.class)))
                    .thenReturn(new SjukfallEnhetResponse(allSjukFall, false, false));
                when(pdfExportService.export(eq(finalList), eq(request), eq(rehabstodUser), eq(allSjukFall.size())))
                    .thenReturn(new byte[0]);

                final var response = sjukfallController.getSjukfallForCareUnitAsPdf(request, servletRequest);

                pdlActivityStore.verify(() -> PDLActivityStore.addActivitiesToStore(anyString(), eq(toLog), eq(ActivityType.PRINT),
                    eq(ResourceType.RESOURCE_TYPE_SJUKFALL), anyMap()));
                pdlActivityStore.verify(() -> PDLActivityStore.getActivitiesNotInStore(anyString(), eq(finalList), eq(ActivityType.PRINT),
                    eq(ResourceType.RESOURCE_TYPE_SJUKFALL), anyMap()));

                verify(sjukfallService).getByUnit(anyString(), isNull(), isNull(), any(Urval.class), any(IntygParametrar.class));
                verify(logService).logSjukfallData(eq(toLog), eq(ActivityType.PRINT), eq(ResourceType.RESOURCE_TYPE_SJUKFALL));

                assertEquals(response.getStatusCode(), HttpStatus.OK);
                assertTrue(Objects.requireNonNull(response.getHeaders().get(HttpHeaders.CONTENT_DISPOSITION)).get(0)
                    .startsWith("attachment; filename=\"sjukfall-" + DEFAULT_VARDENHET.getNamn() + "-"));
            }
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
        public void getSjukfallByPatientWhenIntygFromSingleUnit() {
            final var patientId = PATIENT_ID_1;
            final var a = createSjukFallPatient(patientId, 1, 0, DEFAULT_VARDENHET, DEFAULT_VARDGIVARE);
            final var result = List.of(a);
            final var request = new GetSjukfallForPatientRequest();
            request.setPatientId(patientId);

            try (MockedStatic<PDLActivityStore> pdlActivityStore = mockStatic(PDLActivityStore.class)) {
                when(sjukfallService.getByPatient(anyString(), anyString(), isNull(), anyString(),
                    any(Urval.class), any(IntygParametrar.class), anyCollection(), anyCollection()))
                    .thenReturn(new SjukfallPatientResponse(result, new SjfMetaData(), false, false));

                sjukfallController.getSjukfallForPatient(request);

                verify(sjukfallService).getByPatient(eq(DEFAULT_VARDGIVARE.getId()), eq(DEFAULT_VARDENHET.getId()), isNull(),
                    eq(patientId), any(Urval.class), any(IntygParametrar.class), anyCollection(), anyCollection());

                pdlActivityStore.verify(() -> PDLActivityStore.isActivityInStore(anyString(), eq(patientId), eq(ActivityType.READ),
                    eq(ResourceType.RESOURCE_TYPE_INTYG), anyMap()));

                verify(logService).logSjukfallData(any(PatientData.class), eq(ActivityType.READ), eq(ResourceType.RESOURCE_TYPE_INTYG));
            }
        }

        @Test
        public void getSjukfallByPatientWhenIntygFromMultipleUnits() {
            final var patientId = PATIENT_ID_1;
            final var a = createSjukFallPatient(patientId, 1, 0, DEFAULT_VARDENHET, DEFAULT_VARDGIVARE);
            final var b = createSjukFallPatient(patientId, 1, 0, vardenheter.get(1), DEFAULT_VARDGIVARE);
            final var c = createSjukFallPatient(patientId, 1, 0, vardenheter.get(2), DEFAULT_VARDGIVARE);
            final var d = createSjukFallPatient(patientId, 1, 0, vardenheter.get(3), vardgivare.get(1));
            final var result = List.of(a, b, c, d);
            final var request = new GetSjukfallForPatientRequest();
            request.setPatientId(patientId);

            try (MockedStatic<PDLActivityStore> pdlActivityStore = mockStatic(PDLActivityStore.class)) {
                when(sjukfallService.getByPatient(anyString(), anyString(), isNull(), anyString(),
                    any(Urval.class), any(IntygParametrar.class), anyCollection(), anyCollection()))
                    .thenReturn(new SjukfallPatientResponse(result, new SjfMetaData(), false, false));

                sjukfallController.getSjukfallForPatient(request);

                verify(sjukfallService).getByPatient(eq(DEFAULT_VARDGIVARE.getId()), eq(DEFAULT_VARDENHET.getId()), isNull(),
                    eq(patientId), any(Urval.class), any(IntygParametrar.class), anyCollection(), anyCollection());

                pdlActivityStore.verify(() -> PDLActivityStore.isActivityInStore(anyString(), eq(patientId), eq(ActivityType.READ),
                    eq(ResourceType.RESOURCE_TYPE_INTYG), anyMap()), times(4));

                verify(logService, times(4)).logSjukfallData(any(PatientData.class), eq(ActivityType.READ),
                    eq(ResourceType.RESOURCE_TYPE_INTYG));
            }
        }
    }

    @Nested
    class PdlLogTests {

        @BeforeEach
        public void init() {
            when(userService.getUser()).thenReturn(rehabstodUser);
        }

        @Test
        public void pdlLogSjukfallPatientWhenIntygFromSingleUnit() {
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
        public void pdlLogSjukfallPatientWhenIntygFromMultipleUnits() {
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
        public void pdlLogSjukfallPatientShouldTriggerSRSLogEntry() {
            final var patientId = PATIENT_ID_1;
            final var  user = buildConcreteUser();

            // Should trigger one history PDL item.
            final var  a = createSjukFallPatient(patientId, 1, 0, DEFAULT_VARDENHET, DEFAULT_VARDGIVARE);
            // Should trigger one risk PDL item.
            final var  b = createSjukFallPatient(patientId, 3, 2, DEFAULT_VARDENHET, DEFAULT_VARDGIVARE);

            final var  finalList = List.of(a, b);
            final var  request = new GetSjukfallForPatientRequest();
            request.setPatientId(patientId);

            when(userService.getUser()).thenReturn(user);
            when(sjukfallService.getByPatient(anyString(), anyString(), anyString(), anyString(), any(Urval.class),
                    any(IntygParametrar.class), anyCollection(), anyCollection()))
                .thenReturn(new SjukfallPatientResponse(finalList, new SjfMetaData(), false, false));

            sjukfallController.getSjukfallForPatient(request);

            // Expect entries for ONE enhet
            assertEquals(1, user.getStoredActivities().size());

            final var  pdlActivityEntries = user.getStoredActivities().get(DEFAULT_VARDENHET.getId());
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
        public void testPDLLogSRSForSjukfallEnhet() {
            final var  user = buildConcreteUser();
            final var  a = createSjukFallEnhet(PATIENT_ID_1, true);
            final var  b = createSjukFallEnhet(PATIENT_ID_2, false);
            final var  finalList = List.of(a, b);
            final var  request = new GetSjukfallRequest();

            when(userService.getUser()).thenReturn(user);
            when(sjukfallService.getByUnit(anyString(), isNull(), anyString(), any(Urval.class), any(IntygParametrar.class)))
                .thenReturn(new SjukfallEnhetResponse(finalList, false, false));

            sjukfallController.getSjukfallForCareUnit(request);

            // Expect entries for ONE enhet
            assertEquals(1, user.getStoredActivities().size());

            final var  pdlActivityEntries = user.getStoredActivities().get(DEFAULT_VARDENHET.getId());
            assertEquals(3, pdlActivityEntries.size());
            assertEquals(1L, pdlActivityEntries.stream()
                .filter(entry -> entry.getResourceType() == ResourceType.RESOURCE_TYPE_PREDIKTION_SRS)
                .count());
            assertEquals(2L, pdlActivityEntries.stream()
                .filter(entry -> entry.getResourceType() == ResourceType.RESOURCE_TYPE_SJUKFALL)
                .count());

            verify(logService, times(1)).logSjukfallData(anyList(), eq(ActivityType.READ),
                eq(ResourceType.RESOURCE_TYPE_SJUKFALL));
            verify(logService, times(1)).logSjukfallData(anyList(),
                eq(ActivityType.READ), eq(ResourceType.RESOURCE_TYPE_PREDIKTION_SRS));
        }
    }

    private void assertPdlActivityEntries(RehabstodUser user, Vardenhet vardenhet, ResourceType resourceType) {
        final var  pdlActivityEntries = user.getStoredActivities().get(vardenhet.getId());

        // Expect one INTYG with a certain ResourceType
        assertEquals(1L, pdlActivityEntries.stream()
            .filter(entry -> entry.getResourceType() == resourceType)
            .count());
    }

    private RehabstodUser buildConcreteUser() {
        final var  user = new RehabstodUser("user-1", "Hej Hejssansson", true);
        final var  vg = DEFAULT_VARDGIVARE;
        final var  ve = DEFAULT_VARDENHET;
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
        final var  isf = new SjukfallEnhet();

        final var  lakare = new Lakare("123456-0987", "Hr Doktor");
        isf.setLakare(lakare);

        final var  patient = new Patient(personNummer, "patient " + personNummer);
        isf.setPatient(patient);

        // Not really interested in these properties, but the sjukfall equals /hashcode will fail without them
        final var  diagnos = new Diagnos("M16", "M16", "diagnosnamn");
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
        final var  isf = createSjukFallEnhet(personNummer);
        if (includeRisk) {
            isf.setRiskSignal(new RiskSignal("intyg-1", 3, "Descr.", isf.getStart().atTime(12, 0)));
        }
        return isf;
    }

    private SjukfallPatient createSjukFallPatient(String personNummer, int numberOfIntyg, int numberOfIntygHavingRisk,
        Vardenhet vardenhet, Vardgivare vardgivare) {

        final var  sp = new SjukfallPatient();
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
