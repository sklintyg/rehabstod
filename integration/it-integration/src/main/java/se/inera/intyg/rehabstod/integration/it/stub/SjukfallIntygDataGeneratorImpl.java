package se.inera.intyg.rehabstod.integration.it.stub;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;

import se.riv.clinicalprocess.healthcond.certificate.types.v2.HsaId;
import se.riv.clinicalprocess.healthcond.certificate.types.v2.IntygId;
import se.riv.clinicalprocess.healthcond.certificate.types.v2.PersonId;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.Diagnos;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.Enhet;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.HosPersonal;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.IntygsData;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.Patient;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.Sjukskrivningsgrad;

/**
 * Can generate a suitable amount of intygsdata.
 *
 * Created by eriklupander on 2016-01-29.
 */
@Component
@Profile({"dev", "rhs-hsa-stub"})
public class SjukfallIntygDataGeneratorImpl implements SjukfallIntygDataGenerator {

    private static final Logger log = LoggerFactory.getLogger(SjukfallIntygDataGeneratorImpl.class);

    private Queue<Patient> seededPatients = new LinkedList<>();

    private Enhet enhet;
    private HosPersonal hosPerson;

    private int currentDiagnosIndex = 0;
    private List<Diagnos> diagnosList = new ArrayList<>();

    @Autowired
    private PersonnummerLoader personnummerLoader;

    @PostConstruct
    public void init() {
        initDiagnoser();
        initEnhet();
        initHoSPerson();
    }




    public List<IntygsData> generateIntygsData(Integer numberOfPatients, Integer intygPerPatient) {

        if (numberOfPatients > 13000) {
            throw new IllegalArgumentException("Cannot seed more than 13000 patients");
        }

        seedPatients(numberOfPatients);

        List<IntygsData> intygsDataList = new ArrayList<>();
        for (int a = 0; a < numberOfPatients; a++) {
            Patient patient = nextPatient();
            for (int b = 0; b < intygPerPatient; b++) {
                IntygsData intygsData = new IntygsData();
                intygsData.setPatient(patient);
                intygsData.setIntygsId(randomIntygId());
                intygsData.setDiagnos(nextDiagnosis());
                intygsData.setSkapadAv(hosPerson);
                intygsData.setEnhet(enhet);
                intygsData.getSjukskrivningsgrader().addAll(getDefaultSjukskrivningsGrader());
                intygsDataList.add(intygsData);
            }
        }
        log.info("Generated {0} intygsData items for stub", intygsDataList.size());
        return intygsDataList;
    }

    private void initHoSPerson() {
        hosPerson = new HosPersonal();
        hosPerson.setEnhet(enhet);
        hosPerson.setFullstandigtNamn("Jan Nilsson");
        HsaId hsaId = new HsaId();
        hsaId.setExtension("IFV1239877878-1049");
        hosPerson.setPersonalId(hsaId);
    }

    private List<Sjukskrivningsgrad> getDefaultSjukskrivningsGrader() {
        List<Sjukskrivningsgrad> sjukskrivningsgradList = new ArrayList<>();

        Sjukskrivningsgrad sg1 = buildSjukskrivningsGrad(100, -2, -1);
        Sjukskrivningsgrad sg2 = buildSjukskrivningsGrad(75, -1, 2);

        sjukskrivningsgradList.add(sg1);
        sjukskrivningsgradList.add(sg2);
        return sjukskrivningsgradList;
    }

    private Sjukskrivningsgrad buildSjukskrivningsGrad(int value, Integer startOffset, Integer slutOffset) {
        Sjukskrivningsgrad sg2 = new Sjukskrivningsgrad();
        sg2.setGrad(value);
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
        d1.setKod("M16");
        d1.setText("Höftledsartros");
        d1.setGrupp("Grupp 1");

        Diagnos d2 = new Diagnos();
        d2.setKod("J21");
        d2.setText("Akut bronkiolit (katarr i de små luftvägarna)");
        d2.setGrupp("Grupp 2");

        Diagnos d3 = new Diagnos();
        d3.setKod("J11");
        d3.setText("Influensa, virus ej identifierat");
        d3.setGrupp("Grupp 2");

        Diagnos d4 = new Diagnos();
        d4.setKod("A31");
        d4.setText("Sjukdomar orsakade av andra mykobakterier");
        d4.setGrupp("Grupp 3");

        diagnosList.add(d1);
        diagnosList.add(d2);
        diagnosList.add(d3);
        diagnosList.add(d4);
    }

    private void initEnhet() {
        enhet = new Enhet();
        HsaId hsaId = new HsaId();
        hsaId.setExtension("IFV1239877878-1042");
        enhet.setEnhetsId(hsaId);
        enhet.setEnhetsnamn("WebCert-Enhet1");
    }
}
