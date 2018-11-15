/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import se.inera.intyg.infra.integration.hsa.model.Vardenhet;
import se.inera.intyg.infra.integration.hsa.model.Vardgivare;
import se.inera.intyg.infra.logmessages.ActivityType;
import se.inera.intyg.infra.logmessages.ResourceType;
import se.inera.intyg.infra.sjukfall.dto.IntygParametrar;
import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.auth.RehabstodUserPreferences;
import se.inera.intyg.rehabstod.auth.RehabstodUserPreferences.Preference;
import se.inera.intyg.rehabstod.auth.pdl.PDLActivityEntry;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.atLeastOnce;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

/**
 * Created by Magnus Ekstrand on 03/02/16.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(PDLActivityStore.class)
public class SjukfallControllerTest {

    private final Vardenhet DEFAULT_VARDENHET = new Vardenhet("ve123", "Vardenhet_123");
    private final Vardgivare DEFAULT_VARDGIVARE = new Vardgivare("vg987", "Vardgivare_987");

    private final List<Vardenhet> vardenheter = new ArrayList() {{
        add(DEFAULT_VARDENHET);
        add(new Vardenhet("ve234", "Vardenhet_234"));
        add(new Vardenhet("ve345", "Vardenhet_345"));
        add(new Vardenhet("ve456", "Vardenhet_456"));
        add(new Vardenhet("ve567", "Vardenhet_567"));
    }};

    private final List<Vardgivare> vardgivare = new ArrayList() {{
        add(DEFAULT_VARDGIVARE);
        add(new Vardgivare("vg876", "Vardgivare_876"));
        add(new Vardgivare("vg765", "Vardgivare_765"));
    }};

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    RehabstodUser rehabstodUserMock;

    @Mock
    UserService userServiceMock;

    @Mock
    LogService logServiceMock;

    @Mock
    PdfExportService pdfExportServiceMock;

    @Mock
    private SjukfallService sjukfallServiceMock;

    @InjectMocks
    private SjukfallController testee = new SjukfallController();

    @Before
    public void before() {
        when(userServiceMock.getUser()).thenReturn(rehabstodUserMock);
        when(rehabstodUserMock.getValdVardenhet()).thenReturn(DEFAULT_VARDENHET);
        when(rehabstodUserMock.getValdVardgivare()).thenReturn(DEFAULT_VARDGIVARE);
        when(rehabstodUserMock.getUrval()).thenReturn(Urval.ALL);

        RehabstodUserPreferences preferences = RehabstodUserPreferences.empty();
        preferences.updatePreference(Preference.MAX_ANTAL_DAGAR_MELLAN_INTYG, "5");
        preferences.updatePreference(Preference.MAX_ANTAL_DAGAR_SEDAN_SJUKFALL_AVSLUT, "0");
        when(rehabstodUserMock.getPreferences()).thenReturn(preferences);
    }

    @Test
    public void testGetSjukfallByUnit() {
        SjukfallEnhet a = createSjukFallEnhet("19121212-1212");
        SjukfallEnhet b = createSjukFallEnhet("20121212-1212");
        SjukfallEnhet c = createSjukFallEnhet("19840921-9287");

        List<SjukfallEnhet> result = Arrays.asList(a, b);
        List<SjukfallEnhet> toLog = Arrays.asList(c);

        // Given
        GetSjukfallRequest request = new GetSjukfallRequest();

        // When
        mockStatic(PDLActivityStore.class);
        when(PDLActivityStore.getActivitiesNotInStore(anyString(), any(List.class), eq(ActivityType.READ),
            eq(ResourceType.RESOURCE_TYPE_SJUKFALL), any(Map.class))).thenReturn(toLog);
        when(sjukfallServiceMock.getByUnit(anyString(), isNull(String.class), anyString(), any(Urval.class), any(IntygParametrar.class)))
                .thenReturn(new SjukfallEnhetResponse(result, false));

        // Then
        testee.getSjukfallForCareUnit(request);

        // Verify
        verifyStatic();
        PDLActivityStore.getActivitiesNotInStore(anyString(), any(List.class), eq(ActivityType.READ),
            eq(ResourceType.RESOURCE_TYPE_SJUKFALL), any(Map.class));

        verify(sjukfallServiceMock).getByUnit(anyString(), isNull(String.class), anyString(), any(Urval.class), any(IntygParametrar.class));
        verify(logServiceMock).logSjukfallData(eq(toLog), eq(ActivityType.READ), eq(ResourceType.RESOURCE_TYPE_SJUKFALL));
    }

    @Test
    public void testGetSjukfallByUnitAsPDF() {
        SjukfallEnhet a = createSjukFallEnhet("19121212-1212");
        SjukfallEnhet b = createSjukFallEnhet("20121212-1212");
        SjukfallEnhet c = createSjukFallEnhet("19840921-9287");

        List<SjukfallEnhet> allSjukFall = Arrays.asList(a, b, c);
        List<SjukfallEnhet> finalList = Arrays.asList(a, b);
        List<SjukfallEnhet> toLog = Arrays.asList(c);

        // Given
        PrintSjukfallRequest request = new PrintSjukfallRequest();
        request.setPersonnummer(Arrays.asList("19121212-1212", "20121212-1212"));

        // When
        mockStatic(PDLActivityStore.class);
        doNothing().when(PDLActivityStore.class); //This is the preferred way to mock static void methods
        PDLActivityStore.addActivitiesToStore(anyString(), eq(toLog), eq(ActivityType.PRINT),
            eq(ResourceType.RESOURCE_TYPE_SJUKFALL), any(Map.class));
        when(PDLActivityStore.getActivitiesNotInStore(anyString(), eq(finalList), eq(ActivityType.PRINT),
            eq(ResourceType.RESOURCE_TYPE_SJUKFALL), any(Map.class))).thenReturn(toLog);

        when(sjukfallServiceMock.getByUnit(anyString(), isNull(String.class), anyString(), any(Urval.class), any(IntygParametrar.class)))
                .thenReturn(new SjukfallEnhetResponse(allSjukFall, false));
        when(pdfExportServiceMock.export(eq(finalList), eq(request), eq(rehabstodUserMock), eq(allSjukFall.size()))).thenReturn(new byte[0]);

        // Then
        ResponseEntity response = testee.getSjukfallForCareUnitAsPdf(request);

        // Verify
        verifyStatic();
        PDLActivityStore.addActivitiesToStore(anyString(), eq(toLog), eq(ActivityType.PRINT),
            eq(ResourceType.RESOURCE_TYPE_SJUKFALL), any(Map.class));
        verifyStatic();
        PDLActivityStore.getActivitiesNotInStore(anyString(), eq(finalList), eq(ActivityType.PRINT),
            eq(ResourceType.RESOURCE_TYPE_SJUKFALL), any(Map.class));

        verify(sjukfallServiceMock).getByUnit(anyString(), isNull(String.class), anyString(), any(Urval.class), any(IntygParametrar.class));
        verify(logServiceMock).logSjukfallData(eq(toLog), eq(ActivityType.PRINT), eq(ResourceType.RESOURCE_TYPE_SJUKFALL));

        assertTrue(response.getStatusCode().equals(HttpStatus.OK));
    }

    @Test
    public void getSjukfallByPatient_whenIntygFromSingleUnit() {
        String patientId = "19121212-1212";

        SjukfallPatient a = createSjukFallPatient(patientId, 1, 0, DEFAULT_VARDENHET, DEFAULT_VARDGIVARE);

        List<SjukfallPatient> result = Arrays.asList(a);

        // Given
        GetSjukfallForPatientRequest request = new GetSjukfallForPatientRequest();
        request.setPatientId(patientId);

        // When
        mockStatic(PDLActivityStore.class);
        when(sjukfallServiceMock.getByPatient(anyString(), anyString(), anyString(), anyString(), any(Urval.class), any(IntygParametrar.class), anyCollectionOf(String.class)))
                .thenReturn(new SjukfallPatientResponse(result, new SjfMetaData(), false));

        // Then
        testee.getSjukfallForPatient(request);

        // Verify
        verify(sjukfallServiceMock).getByPatient(eq(DEFAULT_VARDGIVARE.getId()), eq(DEFAULT_VARDENHET.getId()), anyString(),
                eq(patientId), any(Urval.class), any(IntygParametrar.class), anyCollectionOf(String.class));

        verifyStatic();
        PDLActivityStore.isActivityInStore(anyString(), eq(patientId), eq(ActivityType.READ),
            eq(ResourceType.RESOURCE_TYPE_INTYG), any(Map.class));

        verify(logServiceMock).logSjukfallData(any(PatientData.class), eq(ActivityType.READ), eq(ResourceType.RESOURCE_TYPE_INTYG));
    }

    @Test
    public void getSjukfallByPatient_whenIntygFromMultipleUnits() {
        String patientId = "19121212-1212";

        SjukfallPatient a = createSjukFallPatient(patientId, 1, 0, DEFAULT_VARDENHET, DEFAULT_VARDGIVARE);
        SjukfallPatient b = createSjukFallPatient(patientId, 1, 0, vardenheter.get(1), DEFAULT_VARDGIVARE);
        SjukfallPatient c = createSjukFallPatient(patientId, 1, 0, vardenheter.get(2), DEFAULT_VARDGIVARE);
        SjukfallPatient d = createSjukFallPatient(patientId, 1, 0, vardenheter.get(3), vardgivare.get(1));

        List<SjukfallPatient> result = Arrays.asList(a, b, c, d);

        // Given
        GetSjukfallForPatientRequest request = new GetSjukfallForPatientRequest();
        request.setPatientId(patientId);

        // When
        mockStatic(PDLActivityStore.class);
        when(sjukfallServiceMock.getByPatient(anyString(), anyString(), anyString(), anyString(),
                any(Urval.class), any(IntygParametrar.class), anyCollectionOf(String.class)))
                .thenReturn(new SjukfallPatientResponse(result, new SjfMetaData(), false));

        // Then
        testee.getSjukfallForPatient(request);

        // Verify
        verify(sjukfallServiceMock).getByPatient(eq(DEFAULT_VARDGIVARE.getId()), eq(DEFAULT_VARDENHET.getId()), anyString(),
                eq(patientId), any(Urval.class), any(IntygParametrar.class), anyCollectionOf(String.class));

        verifyStatic(atLeastOnce());
        PDLActivityStore.isActivityInStore(anyString(), eq(patientId), eq(ActivityType.READ),
                eq(ResourceType.RESOURCE_TYPE_INTYG), any(Map.class));

        verify(logServiceMock, atLeastOnce()).logSjukfallData(
                any(PatientData.class), eq(ActivityType.READ), eq(ResourceType.RESOURCE_TYPE_INTYG));
    }

    @Test
    public void pdlLogSjukfallPatient_whenIntygFromSingleUnit() {
        String patientId = "19121212-1212";
        RehabstodUser user = buildConcreteUser();

        // Should trigger one history PDL item.
        SjukfallPatient a = createSjukFallPatient(patientId, 1, 0, DEFAULT_VARDENHET, DEFAULT_VARDGIVARE);

        List<SjukfallPatient> finalList = Arrays.asList(a);

        // Given
        GetSjukfallForPatientRequest request = new GetSjukfallForPatientRequest();
        request.setPatientId(patientId);

        // When
        when(userServiceMock.getUser()).thenReturn(user);

        when(sjukfallServiceMock
                .getByPatient(anyString(), anyString(), anyString(), anyString(), any(Urval.class), any(IntygParametrar.class), anyCollectionOf(String.class)))
                .thenReturn(new SjukfallPatientResponse(finalList, new SjfMetaData(), false));

        testee.getSjukfallForPatient(request);

        // Expect entries for ONE enhet
        assertEquals(1, user.getStoredActivities().size());
        List<PDLActivityEntry> pdlActivityEntries = user.getStoredActivities().get(DEFAULT_VARDENHET.getId());

        // Assert entry
        assertPdlActivityEntries(user, DEFAULT_VARDENHET, ResourceType.RESOURCE_TYPE_INTYG);

        verify(logServiceMock).logSjukfallData(
                any(PatientData.class), eq(ActivityType.READ), eq(ResourceType.RESOURCE_TYPE_INTYG));
    }

    @Test
    public void pdlLogSjukfallPatient_whenIntygFromMultipleUnits() {
        String patientId = "19121212-1212";
        RehabstodUser user = buildConcreteUser();

        // Should trigger one history PDL item.
        SjukfallPatient a = createSjukFallPatient(patientId, 3, 0, DEFAULT_VARDENHET, DEFAULT_VARDGIVARE);
        SjukfallPatient b = createSjukFallPatient(patientId, 1, 0, vardenheter.get(1), DEFAULT_VARDGIVARE);
        SjukfallPatient c = createSjukFallPatient(patientId, 2, 0, vardenheter.get(2), DEFAULT_VARDGIVARE);
        SjukfallPatient d = createSjukFallPatient(patientId, 1, 0, vardenheter.get(3), vardgivare.get(1));

        List<SjukfallPatient> finalList = Arrays.asList(a, b, c, d);

        // Given
        GetSjukfallForPatientRequest request = new GetSjukfallForPatientRequest();
        request.setPatientId(patientId);

        // When
        when(userServiceMock.getUser()).thenReturn(user);

        when(sjukfallServiceMock
                .getByPatient(anyString(), anyString(), anyString(), anyString(), any(Urval.class), any(IntygParametrar.class), anyCollectionOf(String.class)))
                .thenReturn(new SjukfallPatientResponse(finalList, new SjfMetaData(), false));

        testee.getSjukfallForPatient(request);

        // Expect one entry for each unit
        assertEquals(4, user.getStoredActivities().size());

        // Assert entries - one for each unit
        assertPdlActivityEntries(user, DEFAULT_VARDENHET, ResourceType.RESOURCE_TYPE_INTYG);
        assertPdlActivityEntries(user, vardenheter.get(1), ResourceType.RESOURCE_TYPE_INTYG);
        assertPdlActivityEntries(user, vardenheter.get(2), ResourceType.RESOURCE_TYPE_INTYG);
        assertPdlActivityEntries(user, vardenheter.get(3), ResourceType.RESOURCE_TYPE_INTYG);

        verify(logServiceMock, times(4)).logSjukfallData(
                any(PatientData.class), eq(ActivityType.READ), eq(ResourceType.RESOURCE_TYPE_INTYG));
    }

    @Test
    public void pdlLogSjukfallPatient_shouldTriggerSRSLogEntry() {
        String patientId = "19121212-1212";
        RehabstodUser user = buildConcreteUser();

        // Should trigger one history PDL item.
        SjukfallPatient a = createSjukFallPatient(patientId, 1, 0, DEFAULT_VARDENHET, DEFAULT_VARDGIVARE);
        // Should trigger one risk PDL item.
        SjukfallPatient b = createSjukFallPatient(patientId, 3, 2, DEFAULT_VARDENHET, DEFAULT_VARDGIVARE);

        List<SjukfallPatient> finalList = Arrays.asList(a, b);

        // Given
        GetSjukfallForPatientRequest request = new GetSjukfallForPatientRequest();
        request.setPatientId(patientId);

        // When
        when(userServiceMock.getUser()).thenReturn(user);

        when(sjukfallServiceMock
                .getByPatient(anyString(), anyString(), anyString(), anyString(), any(Urval.class), any(IntygParametrar.class), anyCollectionOf(String.class)))
                .thenReturn(new SjukfallPatientResponse(finalList, new SjfMetaData(), false));

        testee.getSjukfallForPatient(request);

        // Expect entries for ONE enhet
        assertEquals(1, user.getStoredActivities().size());
        List<PDLActivityEntry> pdlActivityEntries = user.getStoredActivities().get(DEFAULT_VARDENHET.getId());

        // Assert total number of entries
        assertEquals(2, pdlActivityEntries.size());

        // Assert entries - expect one INTYG and one SRS log entry.
        assertPdlActivityEntries(user, DEFAULT_VARDENHET, ResourceType.RESOURCE_TYPE_INTYG);
        assertPdlActivityEntries(user, DEFAULT_VARDENHET, ResourceType.RESOURCE_TYPE_PREDIKTION_SRS);

        verify(logServiceMock, times(2));
    }

    @Test
    public void testPDLLogSRSForSjukfallEnhet() {

        RehabstodUser user = buildConcreteUser();

        when(userServiceMock.getUser()).thenReturn(user);

        SjukfallEnhet a = createSjukFallEnhet("19121212-1212", true);
        SjukfallEnhet b = createSjukFallEnhet("20121212-1212", false);
        List<SjukfallEnhet> finalList = Arrays.asList(a, b);

        // Given
        GetSjukfallRequest request = new GetSjukfallRequest();

        // When
        when(sjukfallServiceMock.getByUnit(anyString(), isNull(String.class), anyString(), any(Urval.class), any(IntygParametrar.class)))
                .thenReturn(new SjukfallEnhetResponse(finalList, false));

        testee.getSjukfallForCareUnit(request);

        // Expect entries for ONE enhet
        assertEquals(1, user.getStoredActivities().size());
        List<PDLActivityEntry> pdlActivityEntries = user.getStoredActivities().get(DEFAULT_VARDENHET.getId());

        assertEquals(3, pdlActivityEntries.size());
        assertEquals(1L, pdlActivityEntries.stream()
                .filter(entry -> entry.getResourceType() == ResourceType.RESOURCE_TYPE_PREDIKTION_SRS)
                .count());
        assertEquals(2L, pdlActivityEntries.stream()
                .filter(entry -> entry.getResourceType() == ResourceType.RESOURCE_TYPE_SJUKFALL)
                .count());

        verify(logServiceMock, times(3));
    }

    private void assertPdlActivityEntries(RehabstodUser user, Vardenhet vardenhet, ResourceType resourceType) {
        List<PDLActivityEntry> pdlActivityEntries = user.getStoredActivities().get(vardenhet.getId());

        // Expect one INTYG with a certain ResourceType
        assertEquals(1L, pdlActivityEntries.stream()
                .filter(entry -> entry.getResourceType() == resourceType)
                .count());
    }

    private RehabstodUser buildConcreteUser() {
        RehabstodUser user = new RehabstodUser("user-1", "Hej Hejssansson", true);
        Vardgivare vg = DEFAULT_VARDGIVARE;
        Vardenhet ve = DEFAULT_VARDENHET;
        vg.getVardenheter().add(ve);
        user.setVardgivare(Arrays.asList(vg));
        user.setValdVardgivare(vg);
        user.setValdVardenhet(ve);
        user.getPreferences().updatePreference(Preference.MAX_ANTAL_DAGAR_MELLAN_INTYG, "5");
        user.getPreferences().updatePreference(Preference.MAX_ANTAL_DAGAR_SEDAN_SJUKFALL_AVSLUT, "0");
        return user;
    }

    private static SjukfallEnhet createSjukFallEnhet(String personNummer) {
        // CHECKSTYLE:OFF MagicNumber
        SjukfallEnhet isf = new SjukfallEnhet();

        Lakare lakare = new Lakare("123456-0987", "Hr Doktor");
        isf.setLakare(lakare);

        Patient patient = new Patient(personNummer, "patient " + personNummer);
        isf.setPatient(patient);

        // Not really interested in these properties, but the sjukfall equals /hashcode will fail without them
        Diagnos diagnos = new Diagnos("M16", "M16", "diagnosnamn");
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
        SjukfallEnhet isf = createSjukFallEnhet(personNummer);
        if (includeRisk) {
            isf.setRiskSignal(new RiskSignal("intyg-1", 3, "Descr."));
        }
        return isf;
    }

    private SjukfallPatient createSjukFallPatient(String personNummer, int numberOfIntyg, int numberOfIntygHavingRisk,
                                                  Vardenhet vardenhet, Vardgivare vardgivare) {

        SjukfallPatient sp = new SjukfallPatient();
        sp.setIntyg(new ArrayList<>());
        for (int a = 0; a < numberOfIntyg; a++) {
            PatientData pd = new PatientData();
            pd.setPatient(new Patient(personNummer, "Namnsson"));
            pd.setVardenhetId(vardenhet.getId());
            pd.setVardenhetNamn(vardenhet.getNamn());
            pd.setVardgivareId(vardgivare.getId());
            pd.setVardgivareNamn(vardgivare.getNamn());

            if (a < numberOfIntygHavingRisk) {
                pd.setRiskSignal(new RiskSignal("intyg-123", 3, "desc"));
            }
            sp.getIntyg().add(pd);
        }

        return sp;
    }

}
