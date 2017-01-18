/**
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
 *
 * This file is part of rehabstod (https://github.com/sklintyg/rehabstod).
 *
 * rehabstod is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * rehabstod is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.intyg.rehabstod.service.sjukfall.ruleengine;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import se.inera.intyg.rehabstod.service.diagnos.DiagnosBeskrivningService;
import se.inera.intyg.rehabstod.service.diagnos.DiagnosKapitelService;
import se.inera.intyg.rehabstod.service.diagnos.dto.DiagnosKapitel;
import se.inera.intyg.rehabstod.service.sjukfall.ruleengine.testdata.IntygsDataGenerator;
import se.inera.intyg.rehabstod.web.controller.api.dto.GetSjukfallRequest;
import se.inera.intyg.rehabstod.web.model.Gender;
import se.inera.intyg.rehabstod.web.model.InternalSjukfall;
import se.inera.intyg.rehabstod.web.model.Sjukfall;
import se.riv.clinicalprocess.healthcond.certificate.types.v2.HsaId;
import se.riv.clinicalprocess.healthcond.certificate.types.v2.PersonId;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.HosPersonal;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.IntygsData;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.Patient;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by martin on 11/02/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class SjukfallEngineTest {
    private static final String LOCATION_INTYGSDATA = "classpath:SjukfallEngineTest/intygsdata-engine.csv";

    private static final String DIAGNOS_KOD = "J1012";
    private static final String DIAGNOS_BESKRIVNING = "Beskrivning";
    private static final String DIAGNOS_KAPITEL = "J00-J10";

    private static List<IntygsData> intygsDataList;
    private static List<InternalSjukfall> internalSjukfallList;

    private LocalDate activeDate = LocalDate.parse("2016-02-16");
    private LocalDate tolvanBirthdate = LocalDate.parse("1912-12-12");

    @Mock
    private DiagnosBeskrivningService diagnosBeskrivningService;

    @Mock
    private DiagnosKapitelService diagnosKapitelService;

    @Spy
    private InternalIntygsDataResolverImpl resolver;

    @InjectMocks
    private SjukfallEngineTestImpl testee = new SjukfallEngineTestImpl();

    @Before
    public void init() throws IOException {
        DiagnosKapitel kapitel = mock(DiagnosKapitel.class);

        when(diagnosBeskrivningService.getDiagnosBeskrivning(anyString())).thenReturn(DIAGNOS_BESKRIVNING);
        when(diagnosKapitelService.getDiagnosKapitel(anyString())).thenReturn(kapitel);
        when(kapitel.getId()).thenReturn(DIAGNOS_KAPITEL);

        // Load test data
        IntygsDataGenerator generator = new IntygsDataGenerator(LOCATION_INTYGSDATA);
        intygsDataList = generator.generate().get();
        assertInit(intygsDataList, 33);

        internalSjukfallList = testee.calculate(intygsDataList, getSjukfallRequest(5, activeDate));
        assertInit(internalSjukfallList, 11);
    }

    // ~ ======================================================================================================== ~
    // ~ Specification för test av sjukfall #1 - #12 finns på URL:
    // ~ https://inera-certificate.atlassian.net/wiki/pages/viewpage.action?pageId=39747618
    // ~ ======================================================================================================== ~

    @Test
    public void testCalculateSjukfall1() {
        assertSjukfall("19791110-9291", "2016-02-01", "2016-02-20", 2, 19);
    }

    @Test
    public void testCalculateSjukfall2() {
        assertSjukfall("19791123-9262", "2016-02-01", "2016-02-20", 2, 19);
    }

    @Test
    public void testCalculateSjukfall3() {
        assertSjukfall("19791212-9280", "2016-02-01", "2016-02-25", 3, 24);
    }

    @Test
    public void testCalculateSjukfall4() {
        assertSjukfall("19800113-9297", "2016-02-01", "2016-02-25", 3, 24);
    }

    @Test
    public void testCalculateSjukfall5() {
        assertSjukfall("19800124-9286", "2016-02-12", "2016-02-25", 2, 14);
    }

    @Test
    public void testCalculateSjukfall6() {
        assertSjukfall("19800207-9294", "2016-02-12", "2016-02-25", 2, 14);
    }

    @Test
    public void testCalculateSjukfall7() {
        assertSjukfall("19800228-9224", "2016-02-01", "2016-02-25", 0, 0);
    }

    @Test
    public void testCalculateSjukfall8() {
        assertSjukfall("19961110-2394", "2016-02-01", "2016-02-19", 3, 15);
    }

    @Test
    public void testCalculateSjukfall9() {
        assertSjukfall("19961111-2385", "2016-02-15", "2016-03-04", 3, 15);
    }

    @Test
    public void testCalculateSjukfall10() {
        assertSjukfall("19571109-2642", "2016-02-15", "2016-02-19", 1, 5);
    }

    @Test
    public void testCalculateSjukfall11() {
        assertSjukfall("19630206-2846", "2016-02-01", "2016-03-04", 4, 29);
    }

    @Test
    public void testCalculateSjukfall12() {
        assertSjukfall("19710301-1032", "2016-02-15", "2016-03-04", 3, 19);
    }

    @Test
    public void testDiagnos() {
        String fullstandigtNamn = "Anders Andersson";
        String id = "19121212-1212";

        IntygsData intyg = getIntygWithPatient(id, fullstandigtNamn);

        se.inera.intyg.rehabstod.web.model.Diagnos diagnos = testee.getDiagnos(intyg);

        assertEquals(DIAGNOS_KOD, diagnos.getIntygsVarde());
        assertEquals(DIAGNOS_KOD, diagnos.getKod());
        assertEquals(DIAGNOS_BESKRIVNING, diagnos.getBeskrivning());
        assertEquals(DIAGNOS_KAPITEL, diagnos.getKapitel());
    }

    @Test
    public void testLakare() {
        String fullstandigtNamn = "Jan Nilsson";
        String hsaId = " IFV1239877878-1049 ";

        IntygsData intyg = getIntygWithLakare(hsaId, fullstandigtNamn);

        se.inera.intyg.rehabstod.web.model.Lakare sjukfallLakare = testee.getLakare(intyg);

        assertEquals(fullstandigtNamn, sjukfallLakare.getNamn());
        assertEquals(hsaId.trim(), sjukfallLakare.getHsaId());
    }

    @Test
    public void testPatient() {
        String fullstandigtNamn = "Anders Andersson";
        String id = " 19121212-1212 ";
        final int expectedYear = (int) ChronoUnit.YEARS.between(tolvanBirthdate, LocalDate.now());

        IntygsData intyg = getIntygWithPatient(id, fullstandigtNamn);

        se.inera.intyg.rehabstod.web.model.Patient sjukfallPatient = testee.getPatient(intyg);

        assertEquals(fullstandigtNamn, sjukfallPatient.getNamn());
        assertEquals(Gender.M, sjukfallPatient.getKon());
        assertEquals(expectedYear, sjukfallPatient.getAlder());
        assertEquals(id.trim(), sjukfallPatient.getId());
    }

    @Test
    public void testPatientMellanNamn() {
        String fullstandigtNamn = "Anders Andersson";
        String id = " 19121212-1212 ";
        final int expectedYear =  (int) ChronoUnit.YEARS.between(tolvanBirthdate, LocalDate.now());

        IntygsData intyg = getIntygWithPatient(id, fullstandigtNamn);

        se.inera.intyg.rehabstod.web.model.Patient sjukfallPatient = testee.getPatient(intyg);

        assertEquals(fullstandigtNamn, sjukfallPatient.getNamn());
        assertEquals(Gender.M, sjukfallPatient.getKon());
        assertEquals(expectedYear, sjukfallPatient.getAlder());
        assertEquals(id.trim(), sjukfallPatient.getId());
    }

    @Test
    public void testPatientShortId() {
        String fullstandigtNamn = "Anders Andersson";
        String id = " 19121212 ";
        final int expectedYear =  (int) ChronoUnit.YEARS.between(tolvanBirthdate, LocalDate.now());

        IntygsData intyg = getIntygWithPatient(id, fullstandigtNamn);

        se.inera.intyg.rehabstod.web.model.Patient sjukfallPatient = testee.getPatient(intyg);

        assertEquals(fullstandigtNamn, sjukfallPatient.getNamn());
        assertEquals(Gender.UNKNOWN, sjukfallPatient.getKon());
        assertEquals(expectedYear, sjukfallPatient.getAlder());
        assertEquals(id.trim(), sjukfallPatient.getId());
    }

    @Test
    public void testPatientEvenShorterId() {
        String fullstandigtNamn = "Anders Andersson";
        String id = " 121212 ";
        final int expectedYear = 0;

        IntygsData intyg = getIntygWithPatient(id, fullstandigtNamn);

        se.inera.intyg.rehabstod.web.model.Patient sjukfallPatient = testee.getPatient(intyg);

        assertEquals(fullstandigtNamn, sjukfallPatient.getNamn());
        assertEquals(Gender.UNKNOWN, sjukfallPatient.getKon());
        assertEquals(expectedYear, sjukfallPatient.getAlder());
        assertEquals(id.trim(), sjukfallPatient.getId());
    }

    @Test
    public void testPatientBadId() {
        String fullstandigtNamn = "Anders Andersson";
        String id = " 191212AB-ABCD ";

        IntygsData intyg = getIntygWithPatient(id, fullstandigtNamn);

        se.inera.intyg.rehabstod.web.model.Patient sjukfallPatient = testee.getPatient(intyg);

        assertEquals(fullstandigtNamn, sjukfallPatient.getNamn());
        assertEquals(Gender.UNKNOWN, sjukfallPatient.getKon());
        assertEquals(0, sjukfallPatient.getAlder());
        assertEquals(id.trim(), sjukfallPatient.getId());
    }

    @Test
    public void testSamordningnummer() {
        String fullstandigtNamn = "Anders Andersson";
        String id = "19701063-2391";

        IntygsData intyg = getIntygWithPatient(id, fullstandigtNamn);

        se.inera.intyg.rehabstod.web.model.Patient sjukfallPatient = testee.getPatient(intyg);

        assertEquals(fullstandigtNamn, sjukfallPatient.getNamn());
        assertEquals(Gender.M, sjukfallPatient.getKon());
        assertEquals(46, sjukfallPatient.getAlder());
        assertEquals(id.trim(), sjukfallPatient.getId());
    }

    @Test
    public void testShortSamordningnummer() {
        String fullstandigtNamn = "Anders Andersson";
        String id = "701063-2391 ";

        IntygsData intyg = getIntygWithPatient(id, fullstandigtNamn);

        se.inera.intyg.rehabstod.web.model.Patient sjukfallPatient = testee.getPatient(intyg);

        assertEquals(fullstandigtNamn, sjukfallPatient.getNamn());
        assertEquals(Gender.M, sjukfallPatient.getKon());
        assertEquals(46, sjukfallPatient.getAlder());
        assertEquals(id.trim(), sjukfallPatient.getId());
    }

    @Test
    public void testWhitespaceTrimming() {
        String fullstandigtNamn = "Anders Andersson";
        String id = " 19121212-1212 ";
        IntygsData intyg = getIntygWithPatient(id, fullstandigtNamn);

        se.inera.intyg.rehabstod.web.model.Patient sjukfallPatient = testee.getPatient(intyg);
        assertEquals(id.trim(), sjukfallPatient.getId());
    }

    // - - -  Private scope  - - -

    private static void assertInit(List<?> list, int expectedListSize) {
        assertTrue("Expected " + expectedListSize + " but was + " + list.size(), list.size() == expectedListSize);
    }

    private static void assertSjukfall(String patientId, String startDatum, String slutDatum, int antalIntyg, int effektivSjukskrivningslangd) {
        InternalSjukfall internalSjukfall = internalSjukfallList.stream().
                filter(o -> o.getSjukfall().getPatient().getId().equals(patientId)).findFirst().orElse(null);

        if (antalIntyg == 0) {
            assertNull(internalSjukfall);
            return;
        }

        Sjukfall sjukfall = internalSjukfall.getSjukfall();

        assertTrue(sjukfall.getStart().isEqual(LocalDate.parse(startDatum)));
        assertTrue(sjukfall.getSlut().isEqual(LocalDate.parse(slutDatum)));
        assertTrue(sjukfall.getIntyg() == antalIntyg);
        assertTrue(sjukfall.getDagar() == effektivSjukskrivningslangd);
    }

    private GetSjukfallRequest getSjukfallRequest(int maxIntygsGlapp, LocalDate aktivtDatum) {
        GetSjukfallRequest request = new GetSjukfallRequest();
        request.setMaxIntygsGlapp(maxIntygsGlapp);
        request.setAktivtDatum(aktivtDatum);
        return request;
    }

    private IntygsData getIntygWithLakare(String lakareId, String fullstandigtNamn) {
        HsaId hsaId = new HsaId();
        hsaId.setExtension(lakareId);

        HosPersonal hosPersonal = new HosPersonal();
        hosPersonal.setPersonalId(hsaId);
        hosPersonal.setFullstandigtNamn(fullstandigtNamn);

        IntygsData intyg = new IntygsData();
        intyg.setSkapadAv(hosPersonal);

        return intyg;
    }

    private IntygsData getIntygWithPatient(String patientId, String fullstandigtNamn) {
        IntygsData intyg = new IntygsData();

        PersonId personId = new PersonId();
        personId.setExtension(patientId);

        Patient patient = new Patient();
        patient.setPersonId(personId);
        patient.setFullstandigtNamn(fullstandigtNamn);

        intyg.setPatient(patient);
        intyg.setDiagnoskod(DIAGNOS_KOD);

        return intyg;
    }

    private class SjukfallEngineTestImpl extends SjukfallEngineImpl {
        public SjukfallEngineTestImpl() {
            super();
            // 2016-02-11
            final int date = 1455203622;
            clock = Clock.fixed(Instant.ofEpochSecond(date), ZoneId.of("Europe/Paris"));
        }

        @Override
        InternalSjukfall buildInternalSjukfall(Sjukfall sjukfall, InternalIntygsData aktivtIntyg) {
            InternalSjukfall internalSjukfall = new InternalSjukfall();
            internalSjukfall.setSjukfall(sjukfall);
            internalSjukfall.setVardGivareId(" IFV1239877878-0000 ");
            internalSjukfall.setVardGivareNamn("Webcert-Vårdgivare1");
            internalSjukfall.setVardEnhetId(" IFV1239877878-1045 ");
            internalSjukfall.setVardEnhetNamn("Webcert-Enhet2");

            return internalSjukfall;
        }
    }
}
