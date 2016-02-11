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
package se.inera.intyg.rehabstod.integration.it.stub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import se.riv.clinicalprocess.healthcond.certificate.types.v2.HsaId;
import se.riv.clinicalprocess.healthcond.certificate.types.v2.IntygId;
import se.riv.clinicalprocess.healthcond.certificate.types.v2.PersonId;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.Arbetsformaga;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.Diagnos;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.Enhet;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.Formaga;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.HosPersonal;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.IntygsData;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.Patient;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;

/**
 * Can generate a suitable amount of intygsdata.
 *
 * Currently hard-coding the same doctor and enhet for all. (Jan Nilsson on enhet IFV1239877878-1042)
 *
 * Created by eriklupander on 2016-01-29.
 */
//CHECKSTYLE:OFF MagicNumber
@Component
@Profile({"dev", "rhs-it-stub"})
public class SjukfallIntygDataGeneratorImpl implements SjukfallIntygDataGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(SjukfallIntygDataGeneratorImpl.class);

    private Queue<Patient> seededPatients = new LinkedList<>();

    private Enhet enhet;

    private int currentDiagnosIndex = 0;
    private List<Diagnos> diagnosList = new ArrayList<>();

    private int currentHosPersonIndex = 0;
    private List<HosPersonal> hosPersonList = new ArrayList<>();

    @Autowired
    private PersonnummerLoader personnummerLoader;

    @PostConstruct
    public void init() {
        initDiagnoser();
        initEnhet();
        initHoSPerson();
    }


    /**
     * Generate intygsdata for a given number of patients, with N intyg per patient.
     *
     * @param numberOfPatients
     *                  Number of patients to base intyg data on.
     * @param intygPerPatient
     *                  Number of intyg to generate intyg per patient on.
     * @return
     *        List of all IntygsData
     */
    public List<IntygsData> generateIntygsData(Integer numberOfPatients, Integer intygPerPatient) {

        if (numberOfPatients > 13000) {
            throw new IllegalArgumentException("Cannot seed more than 13000 patients or we would have to recycle personnummer...");
        }

        seedPatients(numberOfPatients);

        List<IntygsData> intygsDataList = new ArrayList<>();
        for (int a = 0; a < numberOfPatients; a++) {
            Patient patient = nextPatient();
            HosPersonal hosPerson = nextHosPerson();
            for (int b = 0; b < intygPerPatient; b++) {
                IntygsData intygsData = new IntygsData();
                intygsData.setPatient(patient);
                intygsData.setIntygsId(randomIntygId());
                intygsData.setDiagnos(nextDiagnosis());
                intygsData.setSkapadAv(hosPerson);
                intygsData.setEnhet(enhet);

                Arbetsformaga arbetsformaga = new Arbetsformaga();
                arbetsformaga.getFormaga().addAll(getDefaultSjukskrivningsGrader());
                intygsData.setArbetsformaga(arbetsformaga);

                intygsDataList.add(intygsData);
            }
        }
        LOG.info("Generated {0} intygsData items for stub", intygsDataList.size());
        return intygsDataList;
    }

    private HosPersonal nextHosPerson() {
        if (currentHosPersonIndex > hosPersonList.size() - 1) {
            currentHosPersonIndex = 0;
        }
        return hosPersonList.get(currentHosPersonIndex++);
    }


    private List<Formaga> getDefaultSjukskrivningsGrader() {
        List<Formaga> sjukskrivningsgradList = new ArrayList<>();

        Formaga sg1 = buildSjukskrivningsGrad(100, -2, -1);
        Formaga sg2 = buildSjukskrivningsGrad(75, -1, 2);

        sjukskrivningsgradList.add(sg1);
        sjukskrivningsgradList.add(sg2);
        return sjukskrivningsgradList;
    }

    private Formaga buildSjukskrivningsGrad(int value, Integer startOffset, Integer slutOffset) {
        Formaga sg2 = new Formaga();
        sg2.setNedsattning(value);
        sg2.setStartdatum(org.joda.time.LocalDate.now().plusWeeks(startOffset));
        sg2.setSlutdatum(org.joda.time.LocalDate.now().plusWeeks(slutOffset));
        return sg2;
    }

    private Diagnos nextDiagnosis() {
        if (currentDiagnosIndex > diagnosList.size() - 1) {
            currentDiagnosIndex = 0;
        }
        return diagnosList.get(currentDiagnosIndex++);
    }

    private IntygId randomIntygId() {
        IntygId id = new IntygId();
        id.setExtension(UUID.randomUUID().toString());
        return id;
    }

    private Patient nextPatient() {
        return seededPatients.poll();
    }

    private void seedPatients(Integer numberOfPatients) {
        try {
            List<String> personNummer = personnummerLoader.readTestPersonnummer();
            for (int a = 0; a < personNummer.size() && a < numberOfPatients; a++) {
                seededPatients.add(buildPerson(personNummer.get(a)));
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not bootstrap IntygsData: " + e.getMessage());
        }
    }

    private Patient buildPerson(String pnr) {
        Patient patient = new Patient();
        PersonId personId = new PersonId();
        personId.setExtension(pnr);
        patient.setPersonId(personId);
        patient.setFornamn("Förnamn-" + pnr.substring(2, 6));
        patient.setEfternamn("Efternamn-" + pnr.substring(6));
        return patient;
    }

    private void initDiagnoser() {
        Diagnos d1 = new Diagnos();
        d1.setKod("M160");
        d1.setText("Höftledsartros");
        d1.setGrupp("M00-M99");

        Diagnos d2 = new Diagnos();
        d2.setKod("J210");
        d2.setText("Akut bronkiolit (katarr i de små luftvägarna)");
        d2.setGrupp("J00-J99");

        Diagnos d3 = new Diagnos();
        d3.setKod("J110");
        d3.setText("Influensa, virus ej identifierat");
        d3.setGrupp("J00-J99");

        diagnosList.add(d1);
        diagnosList.add(d2);
        diagnosList.add(d3);

    }

    private void initEnhet() {
        enhet = new Enhet();
        HsaId hsaId = new HsaId();
        hsaId.setExtension("IFV1239877878-1042");
        enhet.setEnhetsId(hsaId);
        enhet.setEnhetsnamn("WebCert-Enhet1");
    }

    private void initHoSPerson() {

        HosPersonal hosPerson1 = new HosPersonal();
        hosPerson1.setEnhet(enhet);
        hosPerson1.setFullstandigtNamn("Jan Nilsson");
        HsaId hsaId1 = new HsaId();
        hsaId1.setExtension("IFV1239877878-1049");
        hosPerson1.setPersonalId(hsaId1);

        HosPersonal hosPerson2 = new HosPersonal();
        hosPerson2.setEnhet(enhet);
        hosPerson2.setFullstandigtNamn("Per Karlsson");
        HsaId hsaId2 = new HsaId();
        hsaId2.setExtension("IFV1239877878-1050");
        hosPerson2.setPersonalId(hsaId2);

        HosPersonal hosPerson3 = new HosPersonal();
        hosPerson3.setEnhet(enhet);
        hosPerson3.setFullstandigtNamn("Nina von Döbel");
        HsaId hsaId3 = new HsaId();
        hsaId3.setExtension("IFV1239877878-1050");
        hosPerson3.setPersonalId(hsaId3);

        hosPersonList.add(hosPerson1);
        hosPersonList.add(hosPerson2);
        hosPersonList.add(hosPerson3);
    }
}
