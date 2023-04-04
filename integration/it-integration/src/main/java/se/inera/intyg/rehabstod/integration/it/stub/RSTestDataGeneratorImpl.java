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
package se.inera.intyg.rehabstod.integration.it.stub;

import static java.util.Map.entry;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import se.inera.intyg.infra.certificate.dto.BaseCertificate;
import se.inera.intyg.infra.certificate.dto.DiagnosedCertificate;
import se.inera.intyg.infra.certificate.dto.SickLeaveCertificate;
import se.inera.intyg.infra.certificate.dto.SickLeaveCertificate.WorkCapacity;
import se.inera.intyg.infra.integration.pu.stub.StubResidentStore;
import se.inera.intyg.infra.monitoring.annotation.PrometheusTimeMethod;
import se.inera.intyg.infra.sjukfall.dto.DiagnosKod;
import se.inera.intyg.infra.sjukfall.dto.Lakare;
import se.inera.intyg.infra.sjukfall.dto.SjukfallEnhet;
import se.inera.intyg.infra.sjukfall.dto.Vardenhet;
import se.inera.intyg.rehabstod.integration.wc.stub.WcStubStore;
import se.inera.intyg.schemas.contract.Personnummer;
import se.riv.clinicalprocess.healthcond.certificate.types.v2.HsaId;
import se.riv.clinicalprocess.healthcond.certificate.types.v2.PersonId;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.Arbetsformaga;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.Enhet;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.Formaga;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.HosPersonal;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.IntygsData;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.Patient;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.Vardgivare;
import se.riv.strategicresourcemanagement.persons.person.v3.IIType;
import se.riv.strategicresourcemanagement.persons.person.v3.NamePartType;
import se.riv.strategicresourcemanagement.persons.person.v3.NameType;
import se.riv.strategicresourcemanagement.persons.person.v3.PersonRecordType;

/**
 * Can generate a suitable amount of intygsdata.
 *
 * Currently hard-coding the same doctor and enhet for all. (Jan Nilsson on enhet IFV1239877878-1042)
 *
 * Created by eriklupander on 2016-01-29.
 */
// CHECKSTYLE:OFF MagicNumber
@Component
@Profile({"dev", "rhs-it-stub"})
public class RSTestDataGeneratorImpl implements RSTestDataGenerator {

    public static final String VE_TSTNMT2321000156_105N = "TSTNMT2321000156-105N";
    public static final String VE_TSTNMT2321000156_105P = "TSTNMT2321000156-105P";
    public static final String VE_TSTNMT2321000156_105Q = "TSTNMT2321000156-105Q";
    public static final String VE_CENTRUM_VAST = "centrum-vast";
    public static final String VE_2A = "IFV1239877878-103H";
    public static final String VE_2B = "extern-enhet";
    public static final String UE_AKUTEN = "akuten";
    public static final String UE_DIALYS = "dialys";

    private static final Logger LOG = LoggerFactory.getLogger(RSTestDataGeneratorImpl.class);

    private LocalDateTime timeSimulator = LocalDateTime.now();

    private final Queue<Patient> seededPatients = new ArrayDeque<>();
    private Enhet enhet;
    private Enhet enhet2;
    private Enhet enhet3;
    private Enhet enhet4;
    private Enhet underenhet1;
    private Enhet underenhet2;
    private Enhet kerstinEnhet1;
    private Enhet kerstinEnhet2;
    private HosPersonal kerstin1HosPersonal;
    private HosPersonal kerstin2HosPersonal;
    private Vardgivare vg;
    private Vardgivare vg2;
    private Vardgivare vg3;
    private Vardgivare vg4;
    private HosPersonal utanInloggning;
    private int currentDiagnosIndex = 0;
    private final List<String> diagnosList = new ArrayList<>();
    private int currentSysselSattningIndex = 0;
    // Merged list of codes from fk7263/lisjp schema cv types
    private final List<String> sysselSattningList = Arrays.asList("NUVARANDE_ARBETE", "ARBETSLOSHET", "FORALDRALEDIGHET",
        "ARBETSSOKANDE", "FORALDRALEDIG", "STUDIER");
    private final List<Integer> sjukskrivningsgrader = Arrays.asList(100, 75, 50, 25);
    private int currentSjukskrivningsgraderIndex = 0;
    private int currentHosPersonIndex = 0;
    private final List<HosPersonal> hosPersonList = new ArrayList<>();

    private final List<String> femaleNames = Arrays.asList("Eva", "Britt-Marie", "Petra", "Maria", "Anna", "Margaret", "Elisabet", "Eva",
        "Kristina", "Birgitta", "Karin", "Elisabet", "Marie", "Ingrid", "Christina", "Linnéa", "Sofia", "Kerstin", "Marianne", "Lena",
        "Helena", "Emma", "Johanna", "Linnea", "Inger", "Sara", "Cecilia", "Elin");
    private int currentFemaleIndex = 0;

    private final List<String> maleNames = Arrays.asList("Johan",
        "Lars", "Karl", "Anders", "Johan", "Per", "Nils", "Carl", "Nils", "Roger", "Hans-Åke", "Vidar", "Birk", "Thomas", "Mikael",
        "Jan", "Hans", "Sören", "Morgan", "Ahmad", "Herbert", "Lennart", "Olof", "Peter", "Gunnar", "Sven",
        "Fredrik", "Bengt", "Bo", "Daniel", "Gustav", "Åke", "Göran", "Alexander", "Magnus");
    private int currentMaleIndex = 0;

    private final List<String> lastNames = Arrays
        .asList("Andersson", "Ekman", "Melin", "Holmqvist", "Pålsson", "Marklund", "Krantz-HöllerBach",
            "Åkerblom", "Chen", "Westling", "Mahmoud", "Dalman", "Stolt", "Rönnberg", "Svedin", "Gran", "Hosseini", "Nordstrand",
            "Karlsson", "Weibull", "Nilsson", "Eriksson", "Larsson", "Söderlind", "Olsson", "Persson", "Pullman", "Svensson", "Sollervik",
            "Gustafsson", "Pettersson", "Lindberg", "Jonsson", "Jansson", "Hansson", "Bengtsson", "Jönsson", "von Zinken", "Carlsson",
            "Petersson", "Lindberg", "Öfverkvist", "Wahlström", "Magnusson", "Lindström", "Gustavsson", "Olofsson", "Möller", "Sjöström",
            "Lindgren", "Zaid", "Zetterström", "Öberg");
    private int currentLastNameIndex = 0;

    @Autowired
    private PersonnummerLoader personnummerLoader;
    @Autowired
    private StubResidentStore residentStore;
    @Autowired
    private WcStubStore wcStore;

    @PostConstruct
    public void init() {
        initDiagnoser();
        initHoSPerson();
    }

    /**
     * Generate intygsdata for a given number of patients, with N intyg per patient.
     *
     * @param numberOfPatients Number of patients to base intyg data on.
     * @param intygPerPatient Number of intyg to generate intyg per patient on.
     * @return List of all IntygsData
     */
    @Override
    @PrometheusTimeMethod
    public StubData generateIntygsData(Integer numberOfPatients, Integer intygPerPatient) {

        if (numberOfPatients > 13000) {
            throw new IllegalArgumentException("Cannot seed more than 13000 patients or we would have to recycle personnummer...");
        }

        // Firstly: seed a number of random test patients
        seedPatients(numberOfPatients);

        List<IntygsData> intygsDataList = new ArrayList<>();
        List<DiagnosedCertificate> diagnosedCertificateList = new ArrayList<>();
        List<SickLeaveCertificate> sickleaveCertificateList = new ArrayList<>();
        List<SjukfallEnhet> activeSickLeavesList = new ArrayList<>();

        for (int a = 0; a < numberOfPatients; a++) {
            Patient patient = nextPatient();
            HosPersonal hosPerson = nextHosPerson();
            addToIntygsData(intygPerPatient, patient, hosPerson, intygsDataList);
            addToIntygsData(intygPerPatient, patient, utanInloggning, intygsDataList);
            addToIntygsData(intygPerPatient, patient, nextHosPerson(), intygsDataList);

            addToDiagnosedCertificates(patient, hosPerson, diagnosedCertificateList);
            addToSickleaveCertificates(patient, hosPerson, sickleaveCertificateList);
            addToActiveSickleavesList(patient, hosPerson, activeSickLeavesList);

        }

        // Secondly: Always add Tolvan Tolvansson to stub data
        Patient tolvan = buildTolvanTolvansson();
        addToPuStub(tolvan);
        addToIntygsData(intygPerPatient, tolvan, hosPersonList.get(0), intygsDataList);
        addToIntygsData(intygPerPatient, tolvan, utanInloggning, intygsDataList);
        addToIntygsData(intygPerPatient, tolvan, hosPersonList.get(1), intygsDataList);
        addToDiagnosedCertificates(tolvan, hosPersonList.get(0), diagnosedCertificateList);
        addToSickleaveCertificates(tolvan, hosPersonList.get(0), sickleaveCertificateList);
        addToActiveSickleavesList(tolvan, hosPersonList.get(0), activeSickLeavesList);


        // Lilltolvan
        createSjfPatientData(intygsDataList, intygPerPatient);

        LOG.info("Generated {} intygsData, {} diagnosed certs, {} sickleave, {} active sickleaves certs items for stub",
            intygsDataList.size(), diagnosedCertificateList.size(), sickleaveCertificateList.size(), activeSickLeavesList.size());
        return new StubData(intygsDataList, diagnosedCertificateList, sickleaveCertificateList, activeSickLeavesList);
    }

    private void addToActiveSickleavesList(Patient patient, HosPersonal hosPerson, List<SjukfallEnhet> list) {
        timeSimulator = LocalDateTime.now().minusDays(ThreadLocalRandom.current().nextInt(60, 120));

        final var sickLeave = new SjukfallEnhet();
        buildActiveSickleave(sickLeave, patient, hosPerson);
        list.add(sickLeave);
    }

    private void addToSickleaveCertificates(Patient patient, HosPersonal hosPerson, List<SickLeaveCertificate> sickleaveCertificateList) {
        // Start by resetting time to 60-120 days back in time..
        timeSimulator = LocalDateTime.now().minusDays(ThreadLocalRandom.current().nextInt(60, 120));

        var sickLeaveCertificate = new SickLeaveCertificate();
        buildSickleaveCertificate(sickLeaveCertificate, patient, hosPerson);
        sickleaveCertificateList.add(sickLeaveCertificate);

        //For 25% of certificates, add at least 1 unanswered complement and the same for others
        int randomNumberOfUnansweredComplement = getRandomNumber();
        int randomNumberOfUnansweredOthers = getRandomNumber();
        addToWcStubStore(sickLeaveCertificate, randomNumberOfUnansweredComplement, randomNumberOfUnansweredOthers);

    }

    private void addToDiagnosedCertificates(Patient patient, HosPersonal hosPerson, List<DiagnosedCertificate> diagnosedCertificateList) {
        // Start by resetting time to 60-120 days back in time..
        timeSimulator = LocalDateTime.now().minusDays(ThreadLocalRandom.current().nextInt(60, 120));

        var diagnosedCertificate = new DiagnosedCertificate();
        buildDiagnosedCertificate(diagnosedCertificate, patient, hosPerson);
        diagnosedCertificateList.add(diagnosedCertificate);

        //For 25% of certificates, add at least 1 unanswered complement and the same for others
        int randomNumberOfUnansweredComplement = getRandomNumber();
        int randomNumberOfUnansweredOthers = getRandomNumber();
        addToWcStubStore(diagnosedCertificate, randomNumberOfUnansweredComplement, randomNumberOfUnansweredOthers);

    }

    private int getRandomNumber() {
        if (ThreadLocalRandom.current().nextInt(0, 100) < 25) {
            return ThreadLocalRandom.current().nextInt(1, 3);
        }
        return 0;
    }

    private void buildSickleaveCertificate(SickLeaveCertificate certificate, Patient patient, HosPersonal hosPerson) {

        if (ThreadLocalRandom.current().nextInt(0, 2) == 0) {
            buildDiagnosedCertificate(certificate, patient, hosPerson);
        } else {
            buildBaseCertificate(certificate, patient, hosPerson);
        }

        certificate.setOccupation(mapOccupation(getNextSysselSattning()));

        var workCapacityList = buildWorkCapacity();
        certificate.setWorkCapacityList(workCapacityList);

        var i = ThreadLocalRandom.current().nextInt(2);

        if (i == 1) {
            certificate.setCertificateType("ag114");
        } else {
            certificate.setCertificateType("ag7804");
        }

    }

    private void buildActiveSickleave(SjukfallEnhet sickleave, Patient patient, HosPersonal hosPerson) {
        sickleave.setLakare(Lakare.create(hosPerson.getPersonalId().getExtension(), hosPerson.getFullstandigtNamn()));
        sickleave.setPatient(se.inera.intyg.infra.sjukfall.dto.Patient.create(
            patient.getPersonId().getExtension(), patient.getFullstandigtNamn()
        ));
        sickleave.setVardenhet(Vardenhet.create(hosPerson.getEnhet().getEnhetsId().getExtension(), hosPerson.getEnhet().getEnhetsnamn()));
        sickleave.setVardgivare(se.inera.intyg.infra.sjukfall.dto.Vardgivare.create("id", "namn"));

        sickleave.setDiagnosKod(DiagnosKod.create(nextDiagnosis()));
        final var subDiagnosisCodes = new ArrayList<DiagnosKod>();
        for (int i = ThreadLocalRandom.current().nextInt(3); i > 0; i--) {
            subDiagnosisCodes.add(DiagnosKod.create(nextDiagnosis()));
        }
        sickleave.setBiDiagnoser(subDiagnosisCodes);

        sickleave.setGrader(getSjukskrivningsGrader());
        sickleave.setAktivGrad(sickleave.getGrader().get(0));

        sickleave.setStart(LocalDate.from(timeSimulator));
        sickleave.setSlut(LocalDate.from(timeSimulator.plusDays(60)));
        sickleave.setDagar(60);
        sickleave.setIntyg(1);
    }

    private String mapOccupation(String occupation) {
        return occupationMap().get(occupation);
    }

    private static Map<String, String> occupationMap() {
        return Map.ofEntries(
            entry("ARBETSLOSHET", "Arbetssökande"),
            entry("ARBETSSOKANDE", "Arbetssökande"),
            entry("FORALDRALEDIG", "Föräldraledighet för vård av barn"),
            entry("FORALDRALEDIGHET", "Föräldraledighet för vård av barn"),
            entry("NUVARANDE_ARBETE", "Nuvarande arbete"),
            entry("STUDIER", "Studier"));
    }

    private List<WorkCapacity> buildWorkCapacity() {
        var workCapacityList = new ArrayList<WorkCapacity>();

        var randomSjukskrivningsPerioder = getRandomSjukskrivningsPerioder();

        for (var sjp : randomSjukskrivningsPerioder) {
            var workCapacity = new WorkCapacity();
            workCapacity.setReduction(sjp.getNedsattning());
            workCapacity.setStartDate(sjp.getStartdatum());
            workCapacity.setEndDate(sjp.getSlutdatum());
            workCapacityList.add(workCapacity);
        }

        return workCapacityList;
    }

    private void buildDiagnosedCertificate(DiagnosedCertificate certificate, Patient patient, HosPersonal hosPerson) {
        buildBaseCertificate(certificate, patient, hosPerson);

        certificate.setDiagnoseCode(nextDiagnosis());

        if (ThreadLocalRandom.current().nextInt(2) == 0) {
            List<String> diagnoses = new ArrayList<>();
            diagnoses.add(nextDiagnosis());
            if (ThreadLocalRandom.current().nextInt(2) == 0) {
                diagnoses.add(nextDiagnosis());
            }
            certificate.setSecondaryDiagnoseCodes(diagnoses);
        }

        var i = ThreadLocalRandom.current().nextInt(3);
        if (i == 0) {
            certificate.setCertificateType("luse");
        } else if (i == 1) {
            certificate.setCertificateType("luae_na");
        } else {
            certificate.setCertificateType("luae_fs");
        }

    }

    private void buildBaseCertificate(BaseCertificate certificate, Patient patient, HosPersonal hosPerson) {

        certificate.setPatientFullName(patient.getFullstandigtNamn());
        certificate.setPersonId(patient.getPersonId().getExtension());

        certificate.setPersonalFullName(hosPerson.getFullstandigtNamn());
        certificate.setPersonalHsaId(hosPerson.getPersonalId().getExtension());

        var enhet = hosPerson.getEnhet();
        certificate.setCareProviderId(enhet.getVardgivare().getVardgivarId().getExtension());
        certificate.setCareUnitId(enhet.getEnhetsId().getExtension());
        certificate.setCareUnitName(enhet.getEnhetsnamn());

        certificate.setCertificateId(randomIntygId());
        certificate.setSigningDateTime(timeSimulator);

    }

    private void createSjfPatientData(List<IntygsData> intygsDataList, Integer intygPerPatient) {

        Patient tolvan = buildLilltolvanTolvansson();
        addToPuStub(tolvan);
        // Samma vårdgivare
        addToIntygsDataFixedDates(intygPerPatient, tolvan, kerstin1HosPersonal, intygsDataList);
        // Samma vårdgivare annan enhet
        addToIntygsDataFixedDates(2, tolvan, kerstin2HosPersonal, intygsDataList);
        addToIntygsDataFixedDates(2, tolvan, hosPersonList.get(0), intygsDataList);
        // Annan vårdgivare
        addToIntygsDataFixedDates(2, tolvan, utanInloggning, intygsDataList);
        addToIntygsDataFixedDates(2, tolvan, hosPersonList.get(3), intygsDataList);

        addToIntygsDataFixedDates(1, tolvan, hosPersonList.get(8), intygsDataList, LocalDateTime.now().minusDays(100));
    }

    private void addToIntygsDataFixedDates(Integer intygPerPatient, Patient patient, HosPersonal hosPerson,
        List<IntygsData> intygsDataList) {

        addToIntygsDataFixedDates(intygPerPatient, patient, hosPerson, intygsDataList, LocalDateTime.now());
    }

    private void addToIntygsDataFixedDates(Integer intygPerPatient, Patient patient, HosPersonal hosPerson,
        List<IntygsData> intygsDataList, LocalDateTime startDate) {
        timeSimulator = startDate;
        for (int intygsIndex = 0; intygsIndex < intygPerPatient; intygsIndex++) {
            intygsDataList.add(buildFixedIntygsData(patient, hosPerson));
            timeSimulator = LocalDateTime.now().minusDays(20L * (intygsIndex + 1L));
        }
    }

    private void addToIntygsData(Integer intygPerPatient, Patient patient, HosPersonal hosPerson, List<IntygsData> intygsDataList) {
        // Start by resetting time to 60-120 days back in time..
        timeSimulator = LocalDateTime.now().minusDays(ThreadLocalRandom.current().nextInt(60, 120));
        for (int intygsIndex = 0; intygsIndex < intygPerPatient; intygsIndex++) {

            IntygsData intygData = buildIntygsData(patient, hosPerson);
            intygsDataList.add(intygData);
            // Add random nr of days glapp between intyg
            timeSimulator = timeSimulator.plusDays(ThreadLocalRandom.current().nextInt(0, 10));

            //For 25% of certificates, add at least 1 unanswered complement and the same for others
            int randomNumberOfUnansweredComplement = getRandomNumber();
            int randomNumberOfUnansweredOthers = getRandomNumber();
            addToWcStubStore(intygData, randomNumberOfUnansweredComplement, randomNumberOfUnansweredOthers);
        }
        // Once for each patient > 50 years old, add a 2 really old intyg to get som history
        if (getAge(patient) > 50) {
            timeSimulator = timeSimulator.minusYears(ThreadLocalRandom.current().nextInt(4));
            intygsDataList.add(buildIntygsData(patient, hosPerson));
            intygsDataList.add(buildIntygsData(patient, hosPerson));
        }
    }

    private void addToWcStubStore(IntygsData intygsData, int nrUnansweredComplement, int nrUnansweredOthers) {
        wcStore.addAddition(intygsData.getIntygsId(), intygsData.getSigneringsTidpunkt(), nrUnansweredComplement, nrUnansweredOthers);
    }

    private void addToWcStubStore(SickLeaveCertificate sickLeaveCertificate, int nrUnansweredComplement, int nrUnansweredOthers) {
        wcStore.addAddition(sickLeaveCertificate.getCertificateId(), sickLeaveCertificate.getSigningDateTime(), nrUnansweredComplement,
            nrUnansweredOthers);
    }

    private void addToWcStubStore(DiagnosedCertificate diagnosedCertificate, int nrUnansweredComplement, int nrUnansweredOthers) {
        wcStore.addAddition(diagnosedCertificate.getCertificateId(), diagnosedCertificate.getSigningDateTime(), nrUnansweredComplement,
            nrUnansweredOthers);
    }

    private int getAge(Patient patient) {
        int yearBorn = Integer.parseInt(patient.getPersonId().getExtension().substring(0, 4));
        return LocalDate.now().getYear() - yearBorn;
    }

    private IntygsData buildFixedIntygsData(Patient patient, HosPersonal hosPerson) {
        IntygsData intygsData = buildIntygsData(patient, hosPerson);

        Arbetsformaga arbetsformaga = new Arbetsformaga();
        arbetsformaga.getFormaga().addAll(getSjukskrivningsPerioder());
        intygsData.setArbetsformaga(arbetsformaga);

        return intygsData;
    }

    private IntygsData buildIntygsData(Patient patient, HosPersonal hosPerson) {
        IntygsData intygsData = new IntygsData();
        intygsData.setPatient(patient);
        intygsData.setIntygsId(randomIntygId());
        intygsData.setDiagnoskod(nextDiagnosis());

        // Randomly add 0 <-> 2 bidiagnoses
        for (int i = ThreadLocalRandom.current().nextInt(3); i > 0; i--) {
            intygsData.getBidiagnoser().add(nextDiagnosis());
        }

        // Randomly add 1 <-> 3 sysselsattningar
        for (int i = ThreadLocalRandom.current().nextInt(1, 4); i > 0; i--) {
            intygsData.getSysselsattning().add(getNextSysselSattning());
        }

        intygsData.setSkapadAv(hosPerson);

        // Set signdate to basedate - sjukskrivningsperiods are distributed randomly into the future
        intygsData.setSigneringsTidpunkt(timeSimulator);

        Arbetsformaga arbetsformaga = new Arbetsformaga();
        arbetsformaga.getFormaga().addAll(getRandomSjukskrivningsPerioder());
        intygsData.setArbetsformaga(arbetsformaga);

        return intygsData;
    }

    private List<Integer> getSjukskrivningsGrader() {
        List<Integer> sjukskrivningsgradList = new ArrayList<>();

        int antalPerioder = 2;

        for (int i = 0; i < antalPerioder; i++) {
            sjukskrivningsgradList.add(getNextSjukskrivningsgrad());
        }
        return sjukskrivningsgradList;
    }

    private List<Formaga> getSjukskrivningsPerioder() {
        List<Formaga> sjukskrivningsgradList = new ArrayList<>();

        // Add 1 .. 4 perioder
        int antalPerioder = 2;

        for (int i = 0; i < antalPerioder; i++) {
            int periodLength = 8;
            sjukskrivningsgradList.add(buildSjukskrivningsGrad(getNextSjukskrivningsgrad(), periodLength));
        }
        return sjukskrivningsgradList;
    }

    private List<Formaga> getRandomSjukskrivningsPerioder() {
        List<Formaga> sjukskrivningsgradList = new ArrayList<>();

        // Basedate == signdate. Let's distribute the periods x days into the future

        // Add 1 .. 4 perioder
        int antalPerioder = ThreadLocalRandom.current().nextInt(1, 5);

        for (int i = 0; i < antalPerioder; i++) {
            int periodLength = ThreadLocalRandom.current().nextInt(7, 14);
            sjukskrivningsgradList.add(buildSjukskrivningsGrad(getNextSjukskrivningsgrad(), periodLength));
        }
        return sjukskrivningsgradList;
    }

    private Formaga buildSjukskrivningsGrad(int value, int length) {
        Formaga sg2 = new Formaga();
        sg2.setNedsattning(value);
        sg2.setStartdatum(timeSimulator.toLocalDate());
        timeSimulator = timeSimulator.plusDays(length);
        sg2.setSlutdatum(timeSimulator.toLocalDate());
        return sg2;
    }

    private String getNextSysselSattning() {
        if (currentSysselSattningIndex > sysselSattningList.size() - 1) {
            currentSysselSattningIndex = 0;
        }
        return sysselSattningList.get(currentSysselSattningIndex++);
    }

    private int getNextSjukskrivningsgrad() {
        if (currentSjukskrivningsgraderIndex > sjukskrivningsgrader.size() - 1) {
            currentSjukskrivningsgraderIndex = 0;
        }
        return sjukskrivningsgrader.get(currentSjukskrivningsgraderIndex++);
    }

    private HosPersonal nextHosPerson() {
        if (currentHosPersonIndex > hosPersonList.size() - 1) {
            currentHosPersonIndex = 0;
        }
        return hosPersonList.get(currentHosPersonIndex++);
    }

    private String nextDiagnosis() {
        if (currentDiagnosIndex > diagnosList.size() - 1) {
            currentDiagnosIndex = 0;
        }
        return diagnosList.get(currentDiagnosIndex++);
    }

    private String randomIntygId() {
        return UUID.randomUUID().toString();
    }

    private Patient nextPatient() {
        return seededPatients.poll();
    }

    private void seedPatients(Integer numberOfPatients) {
        try {
            List<String> personNummer = personnummerLoader.readTestPersonnummer();
            int personNummerSize = personNummer.size();
            int step = personNummerSize / numberOfPatients;

            for (int a = 0, i = 0; a < personNummerSize && i < numberOfPatients; i++, a += step) {
                final Patient patient = buildPerson(personNummer.get(a));
                seededPatients.add(patient);
                addToPuStub(patient);
            }

        } catch (IOException e) {
            throw new IllegalArgumentException("Could not bootstrap IntygsData: " + e.getMessage());
        }
    }

    private void addToPuStub(Patient patient) {
        PersonRecordType resident = new PersonRecordType();

        resident.setProtectedPersonIndicator(false);

        Personnummer pnr = Personnummer.createPersonnummer(patient.getPersonId().getExtension())
            .orElseThrow(() -> new IllegalStateException("Invalid personnummer!"));
        resident.setPersonalIdentity(buildIIType(pnr.getPersonnummer()));

        NameType namn = new NameType();
        namn.setGivenName(buildNamePartType(""));
        namn.setSurname(buildNamePartType(patient.getFullstandigtNamn()));
        resident.setName(namn);

        residentStore.addResident(resident);
    }

    private NamePartType buildNamePartType(String val) {
        NamePartType namePartType = new NamePartType();
        namePartType.setName(val);
        return namePartType;
    }

    private IIType buildIIType(String extension) {
        IIType iiType = new IIType();
        iiType.setExtension(extension);
        return iiType;
    }

    private Patient buildTolvanTolvansson() {
        Patient patient = new Patient();
        PersonId personId = new PersonId();
        personId.setExtension("19121212-1212");
        patient.setPersonId(personId);
        patient.setFullstandigtNamn("Tolvan Tolvansson");
        return patient;
    }

    private Patient buildLilltolvanTolvansson() {
        Patient patient = new Patient();
        PersonId personId = new PersonId();
        personId.setExtension("20121212-1212");
        patient.setPersonId(personId);
        patient.setFullstandigtNamn("Lilltolvan Tolvansson");
        return patient;
    }

    private Patient buildPerson(String pnr) {
        Patient patient = new Patient();
        PersonId personId = new PersonId();
        personId.setExtension(pnr);
        patient.setPersonId(personId);
        if (isFemale(pnr)) {
            patient.setFullstandigtNamn(getNextFirstName(true) + " " + getNextLastName());
        } else {
            patient.setFullstandigtNamn(getNextFirstName(false) + " " + getNextLastName());
        }

        return patient;
    }

    private String getNextLastName() {
        if (currentLastNameIndex > lastNames.size() - 1) {
            currentLastNameIndex = 0;
        }
        return lastNames.get(currentLastNameIndex++);
    }

    private String getNextFirstName(boolean female) {

        if (female) {
            if (currentFemaleIndex > femaleNames.size() - 1) {
                currentFemaleIndex = 0;
            }
            return femaleNames.get(currentFemaleIndex++);
        } else {
            if (currentMaleIndex > maleNames.size() - 1) {
                currentMaleIndex = 0;
            }
            return maleNames.get(currentMaleIndex++);
        }

    }

    private boolean isFemale(String personnr) {
        String withoutDash = personnr.replace("-", "").replace("+", "");
        return withoutDash.substring(10, 11).matches("^\\d*[02468]$");
    }

    private void initDiagnoser() {
        diagnosList.add("M16.0");
        diagnosList.add("J21");
        diagnosList.add("J-110");
        diagnosList.add("F198");
        diagnosList.add("H_01");
        diagnosList.add("M46");
        diagnosList.add("A25");
        diagnosList.add("A048");
        diagnosList.add("A165");
        diagnosList.add("J168");
        diagnosList.add("B09");
        diagnosList.add("D21");
        diagnosList.add("Z88");
        diagnosList.add("Y65");
        diagnosList.add("M259");
        diagnosList.add("M478");
        diagnosList.add("N342W");
        diagnosList.add("R072");
        diagnosList.add("R119");
        diagnosList.add("R227");
        diagnosList.add("S004");
        diagnosList.add("S0230");
        diagnosList.add("T055");
        diagnosList.add("T171");
        diagnosList.add("Z610");
        diagnosList.add("Z723");
        diagnosList.add("Z870");
    }

    private void initEnhet() {
        initFakedVardgivare1();
        initFakedVardgivare2();
        initFakedVardgivare3();
        initFakedVardgivare4();

        enhet = new Enhet();
        HsaId hsaId = new HsaId();
        hsaId.setExtension(VE_TSTNMT2321000156_105N);
        enhet.setEnhetsId(hsaId);
        enhet.setEnhetsnamn("Rehabstöd Enhet 1");
        enhet.setVardgivare(vg);

        enhet2 = new Enhet();
        HsaId hsaId2 = new HsaId();
        hsaId2.setExtension(VE_CENTRUM_VAST);
        enhet2.setEnhetsId(hsaId2);
        enhet2.setEnhetsnamn("Centrum Väst");
        enhet2.setVardgivare(vg2);

        enhet3 = new Enhet();
        HsaId hsaId2a = new HsaId();
        hsaId2a.setExtension(VE_2A);
        enhet3.setEnhetsId(hsaId2a);
        enhet3.setEnhetsnamn("Vårdenhet 2A");
        enhet3.setVardgivare(vg3);

        enhet4 = new Enhet();
        HsaId hsaId2b = new HsaId();
        hsaId2b.setExtension(VE_2B);
        enhet4.setEnhetsId(hsaId2b);
        enhet4.setEnhetsnamn("Vårdenhet 2b");
        enhet4.setVardgivare(vg4);

        underenhet1 = new Enhet();
        HsaId hsaId3 = new HsaId();
        hsaId3.setExtension(UE_DIALYS);
        underenhet1.setEnhetsId(hsaId3);
        underenhet1.setEnhetsnamn("Dialys");
        underenhet1.setVardgivare(vg2);

        underenhet2 = new Enhet();
        HsaId hsaId4 = new HsaId();
        hsaId4.setExtension(UE_AKUTEN);
        underenhet2.setEnhetsId(hsaId4);
        underenhet2.setEnhetsnamn("Akuten");
        underenhet2.setVardgivare(vg2);

        kerstinEnhet1 = new Enhet();
        HsaId kerstinEnhetsId1 = new HsaId();
        kerstinEnhetsId1.setExtension(VE_TSTNMT2321000156_105P);
        kerstinEnhet1.setEnhetsId(kerstinEnhetsId1);
        kerstinEnhet1.setEnhetsnamn("Rehabstöd Enhet 2");
        kerstinEnhet1.setVardgivare(vg);

        kerstinEnhet2 = new Enhet();
        HsaId kerstinEnhetsId2 = new HsaId();
        kerstinEnhetsId2.setExtension(VE_TSTNMT2321000156_105Q);
        kerstinEnhet2.setEnhetsId(kerstinEnhetsId2);
        kerstinEnhet2.setEnhetsnamn("Rehabstöd Enhet 3");
        kerstinEnhet2.setVardgivare(vg);
    }

    private void initHoSPerson() {
        initEnhet();

        HosPersonal hosPerson1 = new HosPersonal();
        hosPerson1.setEnhet(enhet);
        hosPerson1.setFullstandigtNamn("Emma Nilsson");
        HsaId hsaId1 = new HsaId();
        hsaId1.setExtension("TSTNMT2321000156-105R");
        hosPerson1.setPersonalId(hsaId1);

        HosPersonal hosPerson2 = new HosPersonal();
        hosPerson2.setEnhet(enhet);
        hosPerson2.setFullstandigtNamn("Anders Karlsson");
        HsaId hsaId2 = new HsaId();
        hsaId2.setExtension("TSTNMT2321000156-105S");
        hosPerson2.setPersonalId(hsaId2);

        HosPersonal hosPerson3 = new HosPersonal();
        hosPerson3.setEnhet(enhet);
        hosPerson3.setFullstandigtNamn("Ingrid Nilsson Olsson");
        HsaId hsaId3 = new HsaId();
        hsaId3.setExtension("TSTNMT2321000156-105T");
        hosPerson3.setPersonalId(hsaId3);

        HosPersonal hosPerson4 = new HosPersonal();
        hosPerson4.setEnhet(enhet2);
        hosPerson4.setFullstandigtNamn("Eva Holgersson");
        HsaId hsaId4 = new HsaId();
        hsaId4.setExtension("eva");
        hosPerson4.setPersonalId(hsaId4);

        HosPersonal hosPerson5 = new HosPersonal();
        hosPerson5.setEnhet(underenhet1);
        hosPerson5.setFullstandigtNamn("Eva Holgersson");
        HsaId hsaId5 = new HsaId();
        hsaId5.setExtension("eva");
        hosPerson5.setPersonalId(hsaId5);

        HosPersonal hosPerson6 = new HosPersonal();
        hosPerson6.setEnhet(underenhet2);
        hosPerson6.setFullstandigtNamn("Eva Holgersson");
        HsaId hsaId6 = new HsaId();
        hsaId6.setExtension("eva");
        hosPerson6.setPersonalId(hsaId6);

        kerstin1HosPersonal = new HosPersonal();
        kerstin1HosPersonal.setEnhet(kerstinEnhet1);
        kerstin1HosPersonal.setFullstandigtNamn("Kerstin Johansson");
        HsaId kerstinId1 = new HsaId();
        kerstinId1.setExtension("TSTNMT2321000156-105W");
        kerstin1HosPersonal.setPersonalId(kerstinId1);

        kerstin2HosPersonal = new HosPersonal();
        kerstin2HosPersonal.setEnhet(kerstinEnhet2);
        kerstin2HosPersonal.setFullstandigtNamn("Kerstin Johansson");
        HsaId kerstinId2 = new HsaId();
        kerstinId2.setExtension("TSTNMT2321000156-105W");
        kerstin2HosPersonal.setPersonalId(kerstinId2);

        HosPersonal peterEnkel = new HosPersonal();
        peterEnkel.setEnhet(enhet3);
        peterEnkel.setFullstandigtNamn("Peter Enkel");
        HsaId peterEnkelId = new HsaId();
        peterEnkelId.setExtension("peter-enkel");
        peterEnkel.setPersonalId(peterEnkelId);

        HosPersonal tothGergo = new HosPersonal();
        tothGergo.setEnhet(enhet);
        tothGergo.setFullstandigtNamn("Tóth Gergő Mészáros");
        HsaId tothGergoId = new HsaId();
        tothGergoId.setExtension("toth-gergo-1");
        tothGergo.setPersonalId(tothGergoId);

        hosPersonList.add(hosPerson1);
        hosPersonList.add(hosPerson2);
        hosPersonList.add(hosPerson3);
        hosPersonList.add(hosPerson4);
        hosPersonList.add(hosPerson5);
        hosPersonList.add(hosPerson6);
        hosPersonList.add(kerstin1HosPersonal);
        hosPersonList.add(kerstin2HosPersonal);
        hosPersonList.add(peterEnkel);
        hosPersonList.add(tothGergo);

        utanInloggning = new HosPersonal();
        utanInloggning.setEnhet(enhet4);
        utanInloggning.setFullstandigtNamn("Ny läkare");
        HsaId utanInloggningHSA = new HsaId();
        utanInloggningHSA.setExtension("ny-lakare-hsa-id");
        utanInloggning.setPersonalId(utanInloggningHSA);
    }

    private void initFakedVardgivare1() {
        vg = new Vardgivare();
        HsaId hsaId = new HsaId();
        hsaId.setExtension("TSTNMT2321000156-105M");
        vg.setVardgivarId(hsaId);
        vg.setVardgivarnamn("Rehabstöd Vårdgivare 1");
    }

    private void initFakedVardgivare2() {
        vg2 = new Vardgivare();
        HsaId hsaId2 = new HsaId();
        hsaId2.setExtension("vastmanland");
        vg2.setVardgivarId(hsaId2);
        vg2.setVardgivarnamn("Landstinget Västmanland");
    }

    private void initFakedVardgivare3() {
        vg3 = new Vardgivare();
        HsaId hsaId3 = new HsaId();
        hsaId3.setExtension("ifv-testdata");
        vg3.setVardgivarId(hsaId3);
        vg3.setVardgivarnamn("IFV Testdata Vårdgivare");
    }

    private void initFakedVardgivare4() {
        vg4 = new Vardgivare();
        HsaId hsaId = new HsaId();
        hsaId.setExtension("TSTNMT2321000156-1061");
        vg4.setVardgivarId(hsaId);
        vg4.setVardgivarnamn("VårdgivareCambio");
    }

    public static class StubData {

        List<IntygsData> intygsDataList;
        List<DiagnosedCertificate> diagnosedCertificateList;
        List<SickLeaveCertificate> sickleaveCertificateList;
        List<SjukfallEnhet> activeSickleaveList;

        public StubData(List<IntygsData> intygsDataList, List<DiagnosedCertificate> diagnosedCertificateList,
            List<SickLeaveCertificate> sickleaveCertificateList, List<SjukfallEnhet> activeSickleaveList) {
            this.intygsDataList = intygsDataList;
            this.diagnosedCertificateList = diagnosedCertificateList;
            this.sickleaveCertificateList = sickleaveCertificateList;
            this.activeSickleaveList = activeSickleaveList;
        }

        public List<IntygsData> getIntygsData() {
            return intygsDataList;
        }

        public List<DiagnosedCertificate> getDiagnosedCertificates() {
            return diagnosedCertificateList;
        }

        public List<SickLeaveCertificate> getSickLeaveCertificates() {
            return sickleaveCertificateList;
        }

        public List<SjukfallEnhet> getActiveSickleaveList() {
            return activeSickleaveList;
        }

    }
}
