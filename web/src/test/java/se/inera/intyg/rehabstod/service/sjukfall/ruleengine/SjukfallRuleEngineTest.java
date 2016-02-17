/**
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import se.inera.intyg.rehabstod.service.diagnos.DiagnosBeskrivningService;
import se.inera.intyg.rehabstod.service.diagnos.DiagnosKapitelService;
import se.inera.intyg.rehabstod.service.diagnos.dto.DiagnosKapitel;
import se.inera.intyg.rehabstod.web.model.Diagnos;
import se.inera.intyg.rehabstod.web.model.Gender;
import se.riv.clinicalprocess.healthcond.certificate.types.v2.PersonId;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.IntygsData;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.Patient;

/**
 * Created by martin on 11/02/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class SjukfallRuleEngineTest {

    private static final String DIAGNOS_KOD = "J1012";
    private static final String DIAGNOS_BESKRIVNING = "Beskrivning";
    private static final String DIAGNOS_KAPITEL = "J00-J10";

    @Mock
    private DiagnosBeskrivningService diagnosBeskrivningService;
    @Mock
    private DiagnosKapitelService diagnosKapitelService;

    @InjectMocks
    private SjukfallCalculatorEngineTestImpl testee = new SjukfallCalculatorEngineTestImpl();

    @Before
    public void init() {
        when(diagnosBeskrivningService.getDiagnosBeskrivning(DIAGNOS_KOD)).thenReturn(DIAGNOS_BESKRIVNING);
        DiagnosKapitel kapitel = mock(DiagnosKapitel.class);

        when(diagnosKapitelService.getDiagnosKapitel(DIAGNOS_KOD)).thenReturn(kapitel);

        when(kapitel.getId()).thenReturn(DIAGNOS_KAPITEL);
    }

    @Test
    public void testDiagnos() {
        String fornamn = "Anders";
        String efternamn = "Andersson";
        String id = "19121212-1212";

        IntygsData intyg = getIntyg(id, fornamn, efternamn, "");

        se.inera.intyg.rehabstod.web.model.Diagnos diagnos = testee.getDiagnos(intyg);

        assertEquals(DIAGNOS_KOD, diagnos.getIntygsVarde());
        assertEquals(DIAGNOS_KOD, diagnos.getKod());
        assertEquals(DIAGNOS_BESKRIVNING, diagnos.getBeskrivning());
        assertEquals(DIAGNOS_KAPITEL, diagnos.getKapitel());
    }

    @Test
    public void testPatient() {
        String fornamn = "Anders";
        String efternamn = "Andersson";
        String id = "19121212-1212";
        final int expectedYear = 103;

        IntygsData intyg = getIntyg(id, fornamn, efternamn, "");

        se.inera.intyg.rehabstod.web.model.Patient sjukfallPatient = testee.getPatient(intyg);

        assertEquals(fornamn + " " + efternamn, sjukfallPatient.getNamn());
        assertEquals(Gender.M, sjukfallPatient.getKon());
        assertEquals(expectedYear, sjukfallPatient.getAlder());
        assertEquals(id, sjukfallPatient.getId());
    }

    @Test
    public void testPatientMellanNamn() {
        String fornamn = "Anders";
        String efternamn = "Andersson";
        String id = "19121212-1212";
        String mellanNamn = "Erik";
        final int expectedYear = 103;

        IntygsData intyg = getIntyg(id, fornamn, efternamn, mellanNamn);

        se.inera.intyg.rehabstod.web.model.Patient sjukfallPatient = testee.getPatient(intyg);

        assertEquals(fornamn + " " + mellanNamn + " " + efternamn, sjukfallPatient.getNamn());
        assertEquals(Gender.M, sjukfallPatient.getKon());
        assertEquals(expectedYear, sjukfallPatient.getAlder());
        assertEquals(id, sjukfallPatient.getId());
    }

    @Test
    public void testPatientShortId() {
        String fornamn = "Anders";
        String efternamn = "Andersson";
        String id = "19121212";
        final int expectedYear = 103;

        IntygsData intyg = getIntyg(id, fornamn, efternamn, "");

        se.inera.intyg.rehabstod.web.model.Patient sjukfallPatient = testee.getPatient(intyg);

        assertEquals(fornamn + " " + efternamn, sjukfallPatient.getNamn());
        assertNull(sjukfallPatient.getKon());
        assertEquals(expectedYear, sjukfallPatient.getAlder());
        assertEquals(id, sjukfallPatient.getId());
    }

    @Test
    public void testPatientBadId() {
        String fornamn = "Anders";
        String efternamn = "Andersson";
        String id = "191212AB-ABCD";

        IntygsData intyg = getIntyg(id, fornamn, efternamn, null);

        se.inera.intyg.rehabstod.web.model.Patient sjukfallPatient = testee.getPatient(intyg);

        assertEquals(fornamn + " " + efternamn, sjukfallPatient.getNamn());
        assertNull(sjukfallPatient.getKon());
        assertEquals(0, sjukfallPatient.getAlder());
        assertEquals(id, sjukfallPatient.getId());
    }

    private IntygsData getIntyg(String patientId, String fornamn, String efternamn, String mellanNamn) {
        IntygsData intyg = new IntygsData();
        Patient patient = new Patient();
        PersonId personId = new PersonId();
        personId.setExtension(patientId);
        patient.setPersonId(personId);
        patient.setFornamn(fornamn);
        patient.setEfternamn(efternamn);
        patient.setMellannamn(mellanNamn);

        intyg.setPatient(patient);

        Diagnos diagnos = new Diagnos();
        diagnos.setKod(DIAGNOS_KOD);

        intyg.setDiagnos(diagnos);

        return intyg;
    }

    private class SjukfallCalculatorEngineTestImpl extends SjukfallRuleEngine {
        public SjukfallCalculatorEngineTestImpl() {
            super();
            // 2016-02-11
            final int date = 1455203622;
            clock = Clock.fixed(Instant.ofEpochSecond(date), ZoneId.of("Europe/Paris"));
        }
    }
}
