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
package se.inera.intyg.rehabstod.service.sjukfall.mappers;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import se.inera.intyg.rehabstod.common.model.IntygAccessControlMetaData;
import se.inera.intyg.rehabstod.service.diagnos.DiagnosFactory;
import se.inera.intyg.rehabstod.web.model.Diagnos;
import se.inera.intyg.rehabstod.web.model.PatientData;
import se.inera.intyg.rehabstod.web.model.SjukfallEnhet;
import se.inera.intyg.rehabstod.web.model.SjukfallPatient;


/**
 * @author Magnus Ekstrand on 2017-09-22.
 */
@RunWith(MockitoJUnitRunner.class)
public class SjukfallEngineMapperTest {

    private static final int MAX_DAGAR_SEDAN_AVSLUT = 5;
    private static final String INTYGSID = "A1234-B1234-C1234-D1234-E1234";
    private static final LocalDateTime SIGNERINGSTIDPUNKT = LocalDateTime.now();
    private static final String PERSONNUMMER = "19121212-1212";
    private static final String PERSONNAMN = "Tolvan Tolvansson";
    private static final String LAKAREID = "TEST-1234509876";
    private static final String LAKARENAMN = "Erik Olsson";
    private static final String DIAGNOS = "M123   Palindrom reumatism";
    private static final String BIDIAGNOS = "S666   Skada på multipla böjmuskler och deras senor på handleds- och handnivå";
    private static final String VARDGIVAREID = "VG-1234567890";
    private static final String VARDGIVARENAMN = "Vardgivare 1";
    private static final String VARDENHETID = "VE-1234567890";
    private static final String VARDENHETNAMN = "Vardenhet 1";
    private static final Integer NEDSATTNING = 100;
    private static final LocalDate NEDSATTNINGSTARTDATUM = LocalDate.now().plusDays(1L);
    private static final LocalDate NEDSATTNINGSLUTDATUM = LocalDate.now().plusDays(7L);
    private static final long ANTALDAGARTILLSLUT = 7L;
    private static final String SYSSELSATTNING = "NUVARANDE_ARBETE";
    private static final Integer ANTALINTYG = 1;
    private static final String DIAGNOS_KOD = "M123";

    private static final LocalDate AKTIVTDATUM = LocalDate.now();

    @Mock
    private DiagnosFactory diagnosFactory;

    @InjectMocks
    private SjukfallEngineMapper testee = new SjukfallEngineMapper();

    @Before
    public void beforeEach() {
        when(diagnosFactory.getDiagnos(DIAGNOS, DIAGNOS_KOD, "Palindrom reumatism"))
            .thenReturn(createDiagnos(DIAGNOS, DIAGNOS_KOD, "Palindrom reumatism"));
        when(diagnosFactory.getDiagnos(BIDIAGNOS, "S666", "Skada på multipla böjmuskler och deras senor på handleds- och handnivå"))
            .thenReturn(createDiagnos(BIDIAGNOS, "S666", "Skada på multipla böjmuskler och deras senor på handleds- och handnivå"));
    }

    @Test
    public void testMappingOfSjukfallEnhet() {
        // given
        se.inera.intyg.infra.sjukfall.dto.SjukfallEnhet from = createSjukfallEnhet();
        LocalDate today = LocalDate.now();

        // when
        SjukfallEnhet to = testee.mapToSjukfallEnhetDto(from, 0, today);

        // then
        assertEquals(VARDGIVAREID, to.getVardGivareId());
        assertEquals(VARDGIVARENAMN, to.getVardGivareNamn());
        assertEquals(VARDENHETID, to.getVardEnhetId());
        assertEquals(VARDENHETNAMN, to.getVardEnhetNamn());
        assertEquals(LAKAREID, to.getLakare().getHsaId());
        assertEquals(LAKARENAMN, to.getLakare().getNamn());
        assertEquals(PERSONNUMMER, to.getPatient().getId());
        assertEquals(PERSONNAMN, to.getPatient().getNamn());
        assertEquals(DIAGNOS, to.getDiagnos().getIntygsVarde());
        assertEquals(DIAGNOS_KOD, to.getDiagnos().getKod());
        assertEquals("Palindrom reumatism", to.getDiagnos().getNamn());
        assertEquals(1, to.getBiDiagnoser().size());
        assertEquals(BIDIAGNOS, to.getBiDiagnoser().get(0).getIntygsVarde());
        assertEquals("S666", to.getBiDiagnoser().get(0).getKod());
        assertEquals("Skada på multipla böjmuskler och deras senor på handleds- och handnivå", to.getBiDiagnoser().get(0).getNamn());
        assertEquals(NEDSATTNING.intValue(), to.getAktivGrad());
        assertEquals(NEDSATTNINGSTARTDATUM, to.getStart());
        assertEquals(NEDSATTNINGSLUTDATUM, to.getSlut());
        assertEquals(ANTALDAGARTILLSLUT, to.getSlutOmDagar());
        assertEquals(getSjukskrivningsDagar(), to.getDagar());
        assertEquals(1, to.getGrader().size());
        assertEquals(NEDSATTNING, to.getGrader().get(0));
    }

    @Test
    public void testMappingOfSjukfallEnhetEndDateToday() {
        // given
        se.inera.intyg.infra.sjukfall.dto.SjukfallEnhet from = createSjukfallEnhet();
        LocalDate today = LocalDate.now().plusDays(7L);

        // when
        SjukfallEnhet to = testee.mapToSjukfallEnhetDto(from, 0, today);

        // then
        assertEquals(0, to.getSlutOmDagar());
    }

    @Test
    public void testMappingOfSjukfallPatient() {
        // given
        se.inera.intyg.infra.sjukfall.dto.SjukfallPatient from = createSjukfallPatient();
        Map<String, IntygAccessControlMetaData> intygAccessMetaData =
            createMockIAMD(from.getSjukfallIntygList().get(0).getIntygId(), true, true);
        // when
        SjukfallPatient to = testee.mapToSjukfallPatientDto(from, intygAccessMetaData);

        // then
        assertEquals(NEDSATTNINGSTARTDATUM, to.getStart());
        assertEquals(NEDSATTNINGSLUTDATUM, to.getSlut());
        assertEquals(getSjukskrivningsDagar(), to.getDagar().intValue());
        assertEquals(DIAGNOS, to.getDiagnos().getIntygsVarde());
        assertEquals(DIAGNOS_KOD, to.getDiagnos().getKod());
        assertEquals("Palindrom reumatism", to.getDiagnos().getNamn());
        assertEquals(ANTALINTYG.intValue(), to.getIntyg().size());

        PatientData patientData = to.getIntyg().get(0);

        assertEquals(DIAGNOS_KOD, patientData.getDiagnos().getKod());
        assertEquals(1, patientData.getBidiagnoser().size());
        assertEquals(1, patientData.getGrader().size());
        assertEquals(1, patientData.getSysselsattning().size());
    }

    private Map<String, IntygAccessControlMetaData> createMockIAMD(String intygId, boolean inomVG, boolean inomVE) {
        Map<String, IntygAccessControlMetaData> iamd = new HashMap<>();
        IntygAccessControlMetaData item = new IntygAccessControlMetaData(null, inomVG, inomVE, true);
        iamd.put(intygId, item);
        return iamd;
    }

    @Test
    public void testMappingOfSjukfallPatientClearDataVardgivare() {
        // given
        se.inera.intyg.infra.sjukfall.dto.SjukfallPatient from = createSjukfallPatient();
        Map<String, IntygAccessControlMetaData> intygAccessMetaData =
            createMockIAMD(from.getSjukfallIntygList().get(0).getIntygId(), false, true);
        // when
        SjukfallPatient to = testee.mapToSjukfallPatientDto(from, intygAccessMetaData);

        // then
        assertEquals(NEDSATTNINGSTARTDATUM, to.getStart());
        assertEquals(NEDSATTNINGSLUTDATUM, to.getSlut());
        assertEquals(getSjukskrivningsDagar(), to.getDagar().intValue());
        assertNull(to.getDiagnos());
        assertEquals(ANTALINTYG.intValue(), to.getIntyg().size());

        PatientData patientData = to.getIntyg().get(0);

        assertTrue(patientData.isOtherVardgivare());
        assertNull(patientData.getDiagnos());
        assertNull(patientData.getLakare());
        assertEquals(0, patientData.getBidiagnoser().size());
        assertEquals(0, patientData.getGrader().size());
        assertEquals(0, patientData.getSysselsattning().size());
    }

    @Test
    public void testMappingOfSjukfallPatientClearDataVardenhet() {
        // given
        se.inera.intyg.infra.sjukfall.dto.SjukfallPatient from = createSjukfallPatient();
        Map<String, IntygAccessControlMetaData> intygAccessMetaData =
            createMockIAMD(from.getSjukfallIntygList().get(0).getIntygId(), true, false);
        // when
        SjukfallPatient to = testee.mapToSjukfallPatientDto(from, intygAccessMetaData);

        // then
        assertEquals(NEDSATTNINGSTARTDATUM, to.getStart());
        assertEquals(NEDSATTNINGSLUTDATUM, to.getSlut());
        assertEquals(getSjukskrivningsDagar(), to.getDagar().intValue());
        assertNull(to.getDiagnos());
        assertEquals(ANTALINTYG.intValue(), to.getIntyg().size());

        PatientData patientData = to.getIntyg().get(0);

        assertFalse(patientData.isOtherVardgivare());
        assertTrue(patientData.isOtherVardenhet());
        assertNull(patientData.getDiagnos());
        assertNull(patientData.getLakare());
        assertEquals(0, patientData.getBidiagnoser().size());
        assertEquals(0, patientData.getGrader().size());
        assertEquals(0, patientData.getSysselsattning().size());
    }

    @Test
    public void testMappingOfSjukfallIntyg() {
        // given
        se.inera.intyg.infra.sjukfall.dto.SjukfallIntyg from = createSjukfallIntyg();
        Map<String, IntygAccessControlMetaData> intygAccessMetaData =
            createMockIAMD(from.getIntygId(), true, false);
        // when
        PatientData to = testee.mapSjukfallIntygToPatientData(from, intygAccessMetaData.get(from.getIntygId()));

        // then
        assertEquals(SIGNERINGSTIDPUNKT, to.getSigneringsTidpunkt());
        assertEquals(VARDGIVAREID, to.getVardgivareId());
        assertEquals(VARDGIVARENAMN, to.getVardgivareNamn());
        assertEquals(VARDENHETID, to.getVardenhetId());
        assertEquals(VARDENHETNAMN, to.getVardenhetNamn());
        assertEquals(LAKAREID, to.getLakare().getHsaId());
        assertEquals(LAKARENAMN, to.getLakare().getNamn());
        assertEquals(NEDSATTNINGSTARTDATUM, to.getStart());
        assertEquals(NEDSATTNINGSLUTDATUM, to.getSlut());
        assertEquals(NEDSATTNING, to.getGrader().get(0));
        assertEquals(PERSONNUMMER, to.getPatient().getId());
        assertEquals(PERSONNAMN, to.getPatient().getNamn());
        assertEquals(DIAGNOS, to.getDiagnos().getIntygsVarde());
        assertEquals(DIAGNOS_KOD, to.getDiagnos().getKod());
        assertEquals("Palindrom reumatism", to.getDiagnos().getNamn());
        assertEquals(BIDIAGNOS, to.getBidiagnoser().get(0).getIntygsVarde());
        assertEquals("S666", to.getBidiagnoser().get(0).getKod());
        assertEquals("Skada på multipla böjmuskler och deras senor på handleds- och handnivå", to.getBidiagnoser().get(0).getNamn());
        assertEquals("Nuvarande arbete", to.getSysselsattning().get(0));
        assertEquals(getSjukskrivningsDagar(), to.getDagar());
        assertFalse(to.isOtherVardgivare());
        assertTrue(to.isOtherVardenhet());
    }

    private Diagnos createDiagnos(String orginalVarde, String diagnosKod, String diagnosNamn) {
        Diagnos diagnos = new Diagnos(orginalVarde, diagnosKod, diagnosNamn);

        // populate beskrivning and kapitel
        diagnos.setBeskrivning("en beskrivning");
        diagnos.setKapitel("M00-M45");

        return diagnos;
    }

    private se.inera.intyg.infra.sjukfall.dto.SjukfallEnhet createSjukfallEnhet() {

        se.inera.intyg.infra.sjukfall.dto.SjukfallEnhet enhet =
            new se.inera.intyg.infra.sjukfall.dto.SjukfallEnhet();

        enhet.setVardgivare(createVardivare());
        enhet.setVardenhet(createVardenhet());
        enhet.setPatient(createPatient());
        enhet.setLakare(createLakare());
        enhet.setStart(NEDSATTNINGSTARTDATUM);
        enhet.setSlut(NEDSATTNINGSLUTDATUM);
        enhet.setDiagnosKod(createDiagnosKod(DIAGNOS));
        enhet.setBiDiagnoser(createBiDiagnoser(BIDIAGNOS));
        enhet.setAktivGrad(NEDSATTNING);
        enhet.setGrader(Arrays.asList(NEDSATTNING));
        enhet.setDagar(getSjukskrivningsDagar());
        enhet.setIntyg(ANTALINTYG);

        return enhet;
    }

    private se.inera.intyg.infra.sjukfall.dto.SjukfallPatient createSjukfallPatient() {

        se.inera.intyg.infra.sjukfall.dto.SjukfallPatient patient =
            new se.inera.intyg.infra.sjukfall.dto.SjukfallPatient();

        patient.setStart(NEDSATTNINGSTARTDATUM);
        patient.setSlut(NEDSATTNINGSLUTDATUM);
        patient.setDagar(getSjukskrivningsDagar());
        patient.setDiagnosKod(createDiagnosKod(DIAGNOS));
        patient.setSjukfallIntygList(createSjukfallIntygList());
        patient.setPatient(createPatient());

        return patient;
    }

    private List<se.inera.intyg.infra.sjukfall.dto.SjukfallIntyg> createSjukfallIntygList() {
        return Arrays.asList(new se.inera.intyg.infra.sjukfall.dto.SjukfallIntyg(
            new se.inera.intyg.infra.sjukfall.dto.SjukfallIntyg.SjukfallIntygBuilder(
                createIntygData(), AKTIVTDATUM, MAX_DAGAR_SEDAN_AVSLUT)));
    }

    private se.inera.intyg.infra.sjukfall.dto.SjukfallIntyg createSjukfallIntyg() {
        return new se.inera.intyg.infra.sjukfall.dto.SjukfallIntyg(
            new se.inera.intyg.infra.sjukfall.dto.SjukfallIntyg.SjukfallIntygBuilder(
                createIntygData(), AKTIVTDATUM, MAX_DAGAR_SEDAN_AVSLUT));
    }

    private se.inera.intyg.infra.sjukfall.dto.IntygData createIntygData() {
        se.inera.intyg.infra.sjukfall.dto.IntygData intygData =
            new se.inera.intyg.infra.sjukfall.dto.IntygData();

        intygData.setIntygId(INTYGSID);
        intygData.setSigneringsTidpunkt(SIGNERINGSTIDPUNKT);
        intygData.setVardgivareId(VARDGIVAREID);
        intygData.setVardgivareNamn(VARDGIVARENAMN);
        intygData.setVardenhetId(VARDENHETID);
        intygData.setVardenhetNamn(VARDENHETNAMN);
        intygData.setLakareId(LAKAREID);
        intygData.setLakareNamn(LAKARENAMN);
        intygData.setPatientId(PERSONNUMMER);
        intygData.setPatientNamn(PERSONNAMN);
        intygData.setDiagnosKod(createDiagnosKod(DIAGNOS));
        intygData.setBiDiagnoser(createBiDiagnoser(BIDIAGNOS));
        intygData.setFormagor(createFormagor());
        intygData.setSysselsattning(createSysselsattningar());

        return intygData;
    }

    private se.inera.intyg.infra.sjukfall.dto.Lakare createLakare() {
        return se.inera.intyg.infra.sjukfall.dto.Lakare.create(LAKAREID, LAKARENAMN);
    }

    private se.inera.intyg.infra.sjukfall.dto.Patient createPatient() {
        return se.inera.intyg.infra.sjukfall.dto.Patient.create(PERSONNUMMER, PERSONNAMN);
    }

    private se.inera.intyg.infra.sjukfall.dto.DiagnosKod createDiagnosKod(String diagnos) {
        return se.inera.intyg.infra.sjukfall.dto.DiagnosKod.create(diagnos);
    }

    private List<se.inera.intyg.infra.sjukfall.dto.DiagnosKod> createBiDiagnoser(String diagnos) {
        return Arrays.asList(createDiagnosKod(diagnos));
    }

    private se.inera.intyg.infra.sjukfall.dto.Formaga createFormaga() {
        return new se.inera.intyg.infra.sjukfall.dto.Formaga(
            NEDSATTNINGSTARTDATUM, NEDSATTNINGSLUTDATUM, NEDSATTNING);
    }

    private List<se.inera.intyg.infra.sjukfall.dto.Formaga> createFormagor() {
        return Arrays.asList(createFormaga());
    }

    private List<String> createSysselsattningar() {
        return Arrays.asList(SYSSELSATTNING);
    }

    private int getSjukskrivningsDagar() {
        return Long.valueOf(DAYS.between(NEDSATTNINGSTARTDATUM, NEDSATTNINGSLUTDATUM)).intValue() + 1;
    }

    private se.inera.intyg.infra.sjukfall.dto.Vardgivare createVardivare() {
        return se.inera.intyg.infra.sjukfall.dto.Vardgivare.create(VARDGIVAREID, VARDGIVARENAMN);
    }

    private se.inera.intyg.infra.sjukfall.dto.Vardenhet createVardenhet() {
        return se.inera.intyg.infra.sjukfall.dto.Vardenhet.create(VARDENHETID, VARDENHETNAMN);
    }

}
