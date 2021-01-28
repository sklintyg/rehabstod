/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.Test;
import se.inera.intyg.infra.sjukfall.dto.IntygData;
import se.riv.clinicalprocess.healthcond.certificate.types.v2.Befattning;
import se.riv.clinicalprocess.healthcond.certificate.types.v2.HsaId;
import se.riv.clinicalprocess.healthcond.certificate.types.v2.PersonId;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.Arbetsformaga;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.Befattningar;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.Enhet;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.Formaga;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.HosPersonal;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.IntygsData;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.Patient;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.Vardgivare;

/**
 * @author Magnus Ekstrand on 2017-09-22.
 */
public class IntygstjanstMapperTest {

    private static final String INTYGSID = "A1234-B1234-C1234-D1234-E1234";
    private static final LocalDateTime SIGNERINGSTIDPUNKT = LocalDateTime.now();
    private static final String PERSONNUMMER = "19121212-1212";
    private static final String PERSONNAMN = "Tolvan Tolvansson";
    private static final String LAKAREID = "TEST-1234509876";
    private static final String LAKARENAMN = "Erik Olsson";
    private static final String LAKAREBEFATTNING = "Läkare";
    private static final String DIAGNOS = "M123   Palindrom reumatism";
    private static final String BIDIAGNOS = "S666   Skada på multipla böjmuskler och deras senor på handleds- och handnivå";
    private static final String VARDGIVAREID = "VG-1234567890";
    private static final String VARDGIVARENAMN = "Vardgivare 1";
    private static final String VARDENHETID = "VE-1234567890";
    private static final String VARDENHETNAMN = "Vardenhet 1";
    private static final Integer NEDSATTNING = 100;
    private static final LocalDate NEDSATTNINGSTARTDATUM = LocalDate.now().plusDays(1L);
    private static final LocalDate NEDSATTNINGSLUTDATUM = LocalDate.now().plusDays(7L);
    private static final String SYSSELSATTNING = "NUVARANDE_ARBETE";

    private IntygstjanstMapper testee = new IntygstjanstMapper();

    @Test
    public void testMapppingOfIntygsData() {
        // given
        IntygsData from = createIntygsData();

        // when
        IntygData to = testee.map(from);

        // then
        assertEquals(INTYGSID, to.getIntygId());
        assertEquals(SIGNERINGSTIDPUNKT, to.getSigneringsTidpunkt());
        assertEquals(VARDGIVAREID, to.getVardgivareId());
        assertEquals(VARDGIVARENAMN, to.getVardgivareNamn());
        assertEquals(VARDENHETID, to.getVardenhetId());
        assertEquals(VARDENHETNAMN, to.getVardenhetNamn());
        assertEquals(LAKAREID, to.getLakareId());
        assertEquals(LAKARENAMN, to.getLakareNamn());
        assertEquals(PERSONNUMMER, to.getPatientId());
        assertEquals(PERSONNAMN, to.getPatientNamn());
        assertEquals("M123", to.getDiagnosKod().getCleanedCode());
        assertEquals("Palindrom reumatism", to.getDiagnosKod().getName());
        assertEquals(DIAGNOS, to.getDiagnosKod().getOriginalCode());
        assertEquals(1, to.getBiDiagnoser().size());
        assertEquals("S666", to.getBiDiagnoser().get(0).getCleanedCode());
        assertEquals("Skada på multipla böjmuskler och deras senor på handleds- och handnivå",
            to.getBiDiagnoser().get(0).getName());
        assertEquals(BIDIAGNOS, to.getBiDiagnoser().get(0).getOriginalCode());
        assertEquals(1, to.getFormagor().size());
        assertEquals(NEDSATTNING.intValue(), to.getFormagor().get(0).getNedsattning());
        assertEquals(NEDSATTNINGSTARTDATUM, to.getFormagor().get(0).getStartdatum());
        assertEquals(NEDSATTNINGSLUTDATUM, to.getFormagor().get(0).getSlutdatum());
        assertEquals(1, to.getSysselsattning().size());
        assertEquals(SYSSELSATTNING, to.getSysselsattning().get(0));
        assertTrue(!to.isEnkeltIntyg());
    }

    ;

    private IntygsData createIntygsData() {
        IntygsData intygsData = new IntygsData();

        intygsData.setIntygsId(INTYGSID);
        intygsData.setSigneringsTidpunkt(SIGNERINGSTIDPUNKT);
        intygsData.setPatient(createPatient());
        intygsData.setSkapadAv(createSkapadAv());
        intygsData.setDiagnoskod(DIAGNOS);
        intygsData.getBidiagnoser().add(BIDIAGNOS);
        intygsData.setArbetsformaga(createArbetsformaga());
        intygsData.getSysselsattning().add(SYSSELSATTNING);
        intygsData.setEnkeltIntyg(false);

        return intygsData;
    }

    private Patient createPatient() {
        PersonId personId = new PersonId();
        personId.setRoot("root");
        personId.setExtension(PERSONNUMMER);

        Patient patient = new Patient();
        patient.setPersonId(personId);
        patient.setFullstandigtNamn(PERSONNAMN);

        return patient;
    }

    private HosPersonal createSkapadAv() {
        Vardgivare vardgivare = new Vardgivare();
        vardgivare.setVardgivarId(createHsaId(VARDGIVAREID));
        vardgivare.setVardgivarnamn(VARDGIVARENAMN);

        Enhet enhet = new Enhet();
        enhet.setEnhetsId(createHsaId(VARDENHETID));
        enhet.setEnhetsnamn(VARDENHETNAMN);
        enhet.setVardgivare(vardgivare);

        Befattning befattning = new Befattning();
        befattning.setCode("kod");
        befattning.setCodeSystem("kodsystem");
        befattning.setCodeSystemName("kodsystemnamn");
        befattning.setDisplayName(LAKAREBEFATTNING);
        befattning.setOriginalText(LAKAREBEFATTNING);

        Befattningar befattningar = new Befattningar();
        befattningar.getBefattning().add(befattning);

        HosPersonal hosPersonal = new HosPersonal();
        hosPersonal.setPersonalId(createHsaId(LAKAREID));
        hosPersonal.setFullstandigtNamn(LAKARENAMN);
        hosPersonal.setEnhet(enhet);
        hosPersonal.setBefattningar(befattningar);

        return hosPersonal;
    }

    private HsaId createHsaId(String id) {
        HsaId hsaId = new HsaId();
        hsaId.setRoot("root");
        hsaId.setExtension(id);

        return hsaId;
    }

    private Arbetsformaga createArbetsformaga() {
        Formaga formaga = new Formaga();
        formaga.setNedsattning(NEDSATTNING);
        formaga.setStartdatum(NEDSATTNINGSTARTDATUM);
        formaga.setSlutdatum(NEDSATTNINGSLUTDATUM);

        Arbetsformaga arbetsformaga = new Arbetsformaga();
        arbetsformaga.getFormaga().add(formaga);

        return arbetsformaga;
    }

}
