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
package se.inera.intyg.rehabstod.service.certificate;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.certificate.dto.BaseCertificate;
import se.inera.intyg.infra.certificate.dto.DiagnosedCertificate;
import se.inera.intyg.infra.certificate.dto.SickLeaveCertificate;
import se.inera.intyg.infra.certificate.dto.SickLeaveCertificate.WorkCapacity;
import se.inera.intyg.infra.integration.hsatk.model.legacy.Vardenhet;
import se.inera.intyg.infra.integration.hsatk.model.legacy.Vardgivare;
import se.inera.intyg.infra.integration.hsatk.services.legacy.HsaOrganizationsService;
import se.inera.intyg.infra.logmessages.ActivityType;
import se.inera.intyg.infra.logmessages.ResourceType;
import se.inera.intyg.infra.sjukfall.dto.DiagnosKod;
import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.auth.pdl.PDLActivityEntry;
import se.inera.intyg.rehabstod.auth.pdl.PDLActivityStore;
import se.inera.intyg.rehabstod.integration.it.service.IntygstjanstRestIntegrationService;
import se.inera.intyg.rehabstod.integration.wc.exception.WcIntegrationException;
import se.inera.intyg.rehabstod.service.Urval;
import se.inera.intyg.rehabstod.service.diagnos.DiagnosFactory;
import se.inera.intyg.rehabstod.service.hsa.EmployeeNameService;
import se.inera.intyg.rehabstod.service.pdl.LogService;
import se.inera.intyg.rehabstod.service.pu.PuService;
import se.inera.intyg.rehabstod.service.communication.UnansweredCommunicationDecoratorService;
import se.inera.intyg.rehabstod.service.sjukfall.komplettering.UnansweredQAsInfoDecorator;
import se.inera.intyg.rehabstod.service.sjukfall.util.PatientIdEncryption;
import se.inera.intyg.rehabstod.service.user.UserService;
import se.inera.intyg.rehabstod.web.controller.api.dto.GetAGCertificatesForPersonResponse;
import se.inera.intyg.rehabstod.web.controller.api.dto.GetDoctorsForUnitResponse;
import se.inera.intyg.rehabstod.web.controller.api.dto.GetLUCertificatesForCareUnitRequest;
import se.inera.intyg.rehabstod.web.controller.api.dto.GetLUCertificatesForCareUnitResponse;
import se.inera.intyg.rehabstod.web.controller.api.dto.GetLUCertificatesForPersonResponse;
import se.inera.intyg.rehabstod.web.model.AGCertificate;
import se.inera.intyg.rehabstod.web.model.Diagnos;
import se.inera.intyg.rehabstod.web.model.LUCertificate;
import se.inera.intyg.rehabstod.web.model.Lakare;
import se.inera.intyg.rehabstod.web.model.Patient;
import se.inera.intyg.schemas.contract.Personnummer;

@Service
public class CertificateServiceImpl implements CertificateService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CertificateServiceImpl.class);

    private final IntygstjanstRestIntegrationService restIntegrationService;

    private static final String[] LU_TYPE_LIST = {"luse", "luae_na", "luae_fs"};
    private static final String[] AG_TYPE_LIST = {"ag114", "ag7804"};

    private static final int AGE_LOWER_LIMIT = 0;
    private static final int AGE_UPPER_LIMIT = 150;

    private final UnansweredQAsInfoDecorator unansweredQAsInfoDecorator;

    private final LogService logService;

    private final UserService userService;

    private final DiagnosFactory diagnosFactory;

    private final HsaOrganizationsService hsaOrganizationsService;

    private final PuService puService;

    private final EmployeeNameService employeeNameService;

    private final UnansweredCommunicationDecoratorService unansweredCommunicationDecoratorService;

    private final PatientIdEncryption patientIdEncryption;

    @Autowired
    public CertificateServiceImpl(
            IntygstjanstRestIntegrationService restIntegrationService, UnansweredQAsInfoDecorator unansweredQAsInfoDecorator,
            LogService logService, UserService userService, DiagnosFactory diagnosFactory, HsaOrganizationsService hsaOrganizationsService,
            PuService puService, EmployeeNameService employeeNameService,
            UnansweredCommunicationDecoratorService unansweredCommunicationDecoratorService,
            PatientIdEncryption patientIdEncryption) {
        this.restIntegrationService = restIntegrationService;
        this.unansweredQAsInfoDecorator = unansweredQAsInfoDecorator;
        this.logService = logService;
        this.userService = userService;
        this.diagnosFactory = diagnosFactory;
        this.hsaOrganizationsService = hsaOrganizationsService;
        this.puService = puService;
        this.employeeNameService = employeeNameService;
        this.unansweredCommunicationDecoratorService = unansweredCommunicationDecoratorService;
        this.patientIdEncryption = patientIdEncryption;
    }

    @Override
    public GetLUCertificatesForCareUnitResponse getLUCertificatesForCareUnit(GetLUCertificatesForCareUnitRequest request) {
        final var user = userService.getUser();
        final var unitIds = user.getValdVardenhet().getHsaIds();

        final var certTypesToQuery = getCertificateTypesToQuery(request.getCertTypes());
        final var doctorIds = getDoctorIds(user, request.getDoctors());

        var diagnosedCertificateList = restIntegrationService
            .getDiagnosedCertificatesForCareUnit(unitIds, certTypesToQuery, request.getFromDate(), request.getToDate(), doctorIds);

        diagnosedCertificateList = filterOnDiagnoses(diagnosedCertificateList, request.getDiagnoses());
        diagnosedCertificateList = filterOnAge(diagnosedCertificateList, request.getFromAge(), request.getToAge());

        puService.enrichDiagnosedCertificateWithPatientNamesAndFilterSekretess(diagnosedCertificateList);

        var luCertificateList = transformCertificatesBasedOnMetaDataQuery(diagnosedCertificateList);

        final var hasDecoratedWithUnansweredCommunication =
                unansweredCommunicationDecoratorService.decorateLuCertificates(luCertificateList);

        luCertificateList = filterOnQuestionAndAnswers(request, luCertificateList);

        luCertificateList = filterOnText(luCertificateList, request.getSearchText());

        pdlLogLUCertificatesForCareUnit(luCertificateList);

        luCertificateList.forEach(
                lu -> lu.setEncryptedPatientId(patientIdEncryption.encrypt(lu.getPatient().getId()))
        );

        LOGGER.debug("Returning LU Certificates for Care Unit");
        return new GetLUCertificatesForCareUnitResponse(luCertificateList, !hasDecoratedWithUnansweredCommunication);
    }

    private List<DiagnosedCertificate> filterOnDiagnoses(List<DiagnosedCertificate> diagnosedCertificates, List<String> diagnoses) {
        if (diagnoses == null || diagnoses.isEmpty()) {
            return diagnosedCertificates;
        }

        return diagnosedCertificates.stream()
            .filter(c -> filterOnDiagnoses(c, diagnoses))
            .collect(Collectors.toList());
    }

    private List<DiagnosedCertificate> filterOnAge(List<DiagnosedCertificate> diagnosedCertificates, int fromAge, int toAge) {
        if (fromAge > AGE_LOWER_LIMIT || (toAge > AGE_LOWER_LIMIT && toAge <= AGE_UPPER_LIMIT)) {
            return diagnosedCertificates.stream()
                .filter(c -> filterOnAge(c, fromAge, toAge))
                .collect(Collectors.toList());
        }

        return diagnosedCertificates;
    }

    private List<LUCertificate> filterOnText(List<LUCertificate> luCertificates, String searchText) {
        if (searchText == null || searchText.isBlank() || searchText.isEmpty()) {
            return luCertificates;
        }

        return luCertificates.stream()
            .filter(c -> filterOnText(c, searchText))
            .collect(Collectors.toList());
    }

    private List<String> getDoctorIds(RehabstodUser user, List<String> doctorIds) {
        if (user.getUrval() == Urval.ISSUED_BY_ME) {
            return Collections.singletonList(user.getHsaId());
        }

        return doctorIds;
    }

    private List<String> getCertificateTypesToQuery(List<String> certificateTypeNames) {
        if (certificateTypeNames != null && certificateTypeNames.size() > 0) {
            return certificateTypeNames.stream()
                .map(this::translateCertificateType)
                .collect(Collectors.toList());
        }

        return Arrays.asList(LU_TYPE_LIST);
    }

    private List<LUCertificate> filterOnQuestionAndAnswers(GetLUCertificatesForCareUnitRequest request,
        List<LUCertificate> luCertificateList) {
        var qas = request.getQuestionAndAnswers();
        if (qas > 0) {
            switch (qas) {
                case 1: // Only show certificates without unanswered complement requests and questions
                    luCertificateList = luCertificateList.stream().filter(c -> c.getUnAnsweredComplement() == 0
                        && c.getUnAnsweredOther() == 0)
                        .collect(Collectors.toList());
                    break;
                case 2: // Only show certificates with unanswered complement requests or questions
                    luCertificateList = luCertificateList.stream().filter(c -> c.getUnAnsweredComplement() > 0
                        || c.getUnAnsweredOther() > 0)
                        .collect(Collectors.toList());
                    break;
                case 3: // Only show certificates with unanswered complement requests
                    luCertificateList = luCertificateList.stream().filter(c -> c.getUnAnsweredComplement() > 0)
                        .collect(Collectors.toList());
                    break;
                case 4: // Only show certificates with unanswered questions
                    luCertificateList = luCertificateList.stream().filter(c -> c.getUnAnsweredOther() > 0)
                        .collect(Collectors.toList());
                    break;
                default:
                    break;
            }
        }
        return luCertificateList;
    }

    private boolean containsIgnoreCase(String one, String other) {
        if (one == null) {
            return other == null;
        }
        return other != null && one.toLowerCase().contains(other.toLowerCase());
    }

    private boolean filterOnText(LUCertificate c, String searchText) {

        var patient = c.getPatient();
        if (patient != null) {
            if (patient.getId() != null && containsIgnoreCase(patient.getId(), searchText)) {
                return true;
            }
            if (patient.getNamn() != null && containsIgnoreCase(patient.getNamn(), searchText)) {
                return true;
            }
            if (patient.getKon() != null && containsIgnoreCase(patient.getKon().getDescription(), searchText)) {
                return true;
            }
            if (patient.getAlder() > -1 && containsIgnoreCase(String.format("%d år", patient.getAlder()), searchText)) {
                return true;
            }
        }

        if (c.getDiagnosis() != null) {
            if (c.getDiagnosis().getIntygsVarde() != null && containsIgnoreCase(c.getDiagnosis().getIntygsVarde(), searchText)) {
                return true;
            }
            if (c.getDiagnosis().getBeskrivning() != null && containsIgnoreCase(c.getDiagnosis().getBeskrivning(), searchText)) {
                return true;
            }
        }

        var biDiagnoses = c.getBiDiagnoses();
        if (biDiagnoses != null && !biDiagnoses.isEmpty()) {
            for (var diagnosis : biDiagnoses) {
                if (diagnosis.getIntygsVarde() != null && containsIgnoreCase(diagnosis.getIntygsVarde(), searchText)) {
                    return true;
                }
                if (diagnosis.getBeskrivning() != null && containsIgnoreCase(diagnosis.getBeskrivning(), searchText)) {
                    return true;
                }
            }
        }

        if (c.getCertificateType() != null && containsIgnoreCase(c.getCertificateType(), searchText)) {
            return true;
        }

        if (c.getSigningTimeStamp() != null && containsIgnoreCase(c.getSigningTimeStamp().toLocalDate().toString(), searchText)) {
            return true;
        }

        if (c.getUnAnsweredComplement() > 0 && containsIgnoreCase(String.format("Komplettering (%d)", c.getUnAnsweredComplement()),
            searchText)) {
            return true;
        }

        if (c.getUnAnsweredOther() > 0 && containsIgnoreCase(String.format("Administrativ fråga (%d)", c.getUnAnsweredOther()),
            searchText)) {
            return true;
        }

        return c.getDoctor() != null && c.getDoctor().getNamn() != null && containsIgnoreCase(c.getDoctor().getNamn(), searchText);
    }

    private boolean filterOnAge(DiagnosedCertificate c, int fromAge, int toAge) {
        var patient = new Patient(c.getPersonId(), c.getPatientFullName());

        if (fromAge > AGE_LOWER_LIMIT && patient.getAlder() < fromAge) {
            return false;
        }
        return toAge <= AGE_LOWER_LIMIT || toAge > AGE_UPPER_LIMIT || patient.getAlder() <= toAge;
    }

    private boolean filterOnDiagnoses(DiagnosedCertificate diagnosedCertificate, List<String> diagnoseGroupList) {
        List<String> diagnoseList = Collections.singletonList(diagnosedCertificate.getDiagnoseCode());

        for (String d : diagnoseList) {
            for (String dg : diagnoseGroupList) {
                if (belongsToDiagnoseGroup(d, dg)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Creates a numeric representation of diagnose and the selected diagnoseGroup and checks
     * if the diagnose is within the range of the diagnoseGroup.
     *
     * The diagnoseGroup should be in the format of this example: "A00-B99".
     *
     * @return true if diagnose is within the range of diagnoseGroup
     */
    private boolean belongsToDiagnoseGroup(String diagnose, String diagnoseGroup) {
        if (diagnose == null || diagnoseGroup == null) {
            return false;
        }

        diagnose = diagnose.toUpperCase();
        diagnoseGroup = diagnoseGroup.toUpperCase();

        var splits = diagnoseGroup.split("-");
        if (splits.length != 2 || splits[0].length() != 3 || splits[1].length() != 3) {
            return false;
        }

        try {
            // get int representation of diagnoseGroup
            int startValue = (Character.getNumericValue(splits[0].toCharArray()[0]) * 100) + Integer.parseInt(splits[0].substring(1, 3));
            int endValue = (Character.getNumericValue(splits[1].toCharArray()[0]) * 100) + Integer.parseInt(splits[1].substring(1, 3));

            // get int representation of diagnose code (first 3 characters only)
            int diagnoseValue =
                (Character.getNumericValue(diagnose.toCharArray()[0]) * 100) + Integer.parseInt(diagnose.substring(1, 3));

            return diagnoseValue >= startValue && diagnoseValue <= endValue;
        } catch (NumberFormatException exception) {
            // Likely due to an invalid diagnose code containing other values than a starting char followed by numbers. i.e. "B65" or "B651"
            return false;
        }

    }

    @Override
    public GetDoctorsForUnitResponse getDoctorsForUnit() {
        final var unitIds = userService.getUser().getValdVardenhet().getHsaIds();

        final var doctorIds = restIntegrationService.getSigningDoctorsForUnit(unitIds, Arrays.asList(LU_TYPE_LIST));

        final var doctors = doctorIds.stream()
            .map(this::getDoctor)
            .sorted(Comparator.comparing(Lakare::getNamn))
            .collect(Collectors.toList());

        return new GetDoctorsForUnitResponse(doctors);
    }

    private Lakare getDoctor(String doctorId) {
        final var employeeHsaName = getEmployeeHsaName(doctorId);
        return new Lakare(doctorId, employeeHsaName);
    }

    private String getEmployeeHsaName(String doctorId) {
        final var employeeHsaName = employeeNameService.getEmployeeHsaName(doctorId);
        if (employeeHsaName == null) {
            return doctorId;
        }
        return employeeHsaName;
    }

    @Override
    public GetLUCertificatesForPersonResponse getLUCertificatesForPerson(String personId) {
        final var unitIds = userService.getUser().getValdVardenhet().getHsaIds();
        final var loggedInUnitId = userService.getUser().getValdVardenhet().getId();

        var diagnosedCertificateList = restIntegrationService
            .getDiagnosedCertificatesForPerson(personId, Arrays.asList(LU_TYPE_LIST), unitIds);

        var luCertificateList = transformDiagnosedCertificatesToLUCertificates(diagnosedCertificateList);
        final var hasDecoratedWithUnansweredCommunication =
                unansweredCommunicationDecoratorService.decorateLuCertificates(luCertificateList);

        LOGGER.debug("Adding PDL log for certificate read");
        var rehabstodUser = userService.getUser();
        var storedActivities = rehabstodUser.getStoredActivities();
        pdlLogCertificatesForPerson(personId, loggedInUnitId, storedActivities);

        luCertificateList.forEach(
                lu -> lu.setEncryptedPatientId(patientIdEncryption.encrypt(lu.getPatient().getId()))
        );

        LOGGER.debug("Returning LU Certificates for Person");
        return new GetLUCertificatesForPersonResponse(luCertificateList, !hasDecoratedWithUnansweredCommunication);
    }

    @Override
    public GetAGCertificatesForPersonResponse getAGCertificatesForPerson(String personId) {
        final var unitIds = userService.getUser().getValdVardenhet().getHsaIds();
        final var loggedInUnitId = userService.getUser().getValdVardenhet().getId();
        var sickLeaveCertificateList = restIntegrationService
            .getSickLeaveCertificatesForPerson(personId, Arrays.asList(AG_TYPE_LIST), unitIds);

        var agCertificateList = transformSickLeaveCertificatesToAGCertificates(sickLeaveCertificateList);
        boolean qaInfoError = false;
        try {
            populateAGCertificatesWithNotificationData(agCertificateList);
        } catch (WcIntegrationException e) {
            qaInfoError = true;
        }

        LOGGER.debug("Adding PDL log for certificate read");
        var rehabstodUser = userService.getUser();
        var storedActivities = rehabstodUser.getStoredActivities();
        pdlLogCertificatesForPerson(personId, loggedInUnitId, storedActivities);

        LOGGER.debug("Returning AG Certificates for Person");
        return new GetAGCertificatesForPersonResponse(agCertificateList, qaInfoError);
    }

    private void pdlLogCertificatesForPerson(String personId, String unitId,
        Map<String, List<PDLActivityEntry>> storedActivities) {
        var patientId = Personnummer.createPersonnummer(personId)
            .orElseThrow(() -> new IllegalArgumentException("Could not parse passed personId: " + personId));

        var readActivityType = ActivityType.READ;
        var resourceTypeCertificate = ResourceType.RESOURCE_TYPE_INTYG;

        var isInStore = PDLActivityStore
            .isActivityInStore(unitId, personId, readActivityType, resourceTypeCertificate, storedActivities);

        if (!isInStore) {
            logService.logCertificate(patientId, readActivityType, resourceTypeCertificate);
            PDLActivityStore
                .addActivityToStore(unitId, personId, readActivityType, resourceTypeCertificate, storedActivities);
        }
    }

    private void pdlLogLUCertificatesForCareUnit(List<LUCertificate> luCertificateList) {
        LOGGER.debug("Adding PDL logs for certificate read");
        var rehabstodUser = userService.getUser();
        var enhetsId = rehabstodUser.getValdVardenhet().getId();
        var storedActivities = rehabstodUser.getStoredActivities();

        var readActivityType = ActivityType.READ;
        var resourceTypeCertificate = ResourceType.RESOURCE_TYPE_INTYG;

        List<LUCertificate> luCertificateToLog = PDLActivityStore
            .getActivitiesNotInStore(luCertificateList, enhetsId, readActivityType, resourceTypeCertificate,
                rehabstodUser.getStoredActivities());
        if (luCertificateToLog.isEmpty()) {
            LOGGER.info("Everything in list already in PDL log");
        } else {
            LOGGER.info("Adding new entries to PDL log");
            logService.logCertificate(luCertificateToLog, readActivityType, resourceTypeCertificate, storedActivities);
            PDLActivityStore.addActivitiesToStore(luCertificateToLog, enhetsId, readActivityType, resourceTypeCertificate,
                rehabstodUser.getStoredActivities());
        }

    }

    private List<AGCertificate> transformSickLeaveCertificatesToAGCertificates(List<SickLeaveCertificate> sickLeaveCertificateList) {

        return sickLeaveCertificateList.stream().filter(this::commonFilter).map(this::convertSickLeaveCertificateToLUCertificate)
            .sorted(Comparator.comparing(AGCertificate::getStart).reversed()).collect(Collectors.toList());
    }

    private void populateAGCertificatesWithNotificationData(List<AGCertificate> agCertificateList) {
        unansweredQAsInfoDecorator.updateAGCertificatesWithQAs(agCertificateList);
    }

    private AGCertificate convertSickLeaveCertificateToLUCertificate(SickLeaveCertificate sickLeaveCertificate) {

        var careUnit = hsaOrganizationsService.getVardenhet(sickLeaveCertificate.getCareUnitId());
        var careProvider = hsaOrganizationsService.getVardgivareInfo(sickLeaveCertificate.getCareProviderId());
        var workCapacityList = sickLeaveCertificate.getWorkCapacityList();
        var startDate = workCapacityList.stream().min(Comparator.comparing(WorkCapacity::getStartDate)).get().getStartDate();
        var endDate = workCapacityList.stream().max(Comparator.comparing(WorkCapacity::getEndDate)).get().getEndDate();

        return AGCertificate.builder().certificateId(sickLeaveCertificate.getCertificateId())
            .certificateType(translateCertificateTypeName(sickLeaveCertificate.getCertificateType()))
            .careProviderId(sickLeaveCertificate.getCareProviderId())
            .careProviderName(careProvider.getNamn())
            .careUnitId(sickLeaveCertificate.getCareUnitId())
            .careUnitName(careUnit.getNamn())
            .signingTimeStamp(sickLeaveCertificate.getSigningDateTime())
            .patient(new Patient(sickLeaveCertificate.getPersonId(), sickLeaveCertificate.getPatientFullName()))
            .doctor(new Lakare(sickLeaveCertificate.getPersonalHsaId(), sickLeaveCertificate.getPersonalFullName()))
            .diagnosis(getDiagnosis(sickLeaveCertificate.getDiagnoseCode()))
            .biDiagnoses(getDiagnosisList(sickLeaveCertificate.getSecondaryDiagnoseCodes()))
            .start(startDate)
            .end(endDate)
            .days((int) ChronoUnit.DAYS.between(startDate, endDate) + 1)
            .degree(workCapacityList.stream().sorted(Comparator.comparing(WorkCapacity::getStartDate)).map(WorkCapacity::getReduction)
                .collect(Collectors.toList()))
            .occupation(Arrays.asList(sickLeaveCertificate.getOccupation().split(","))).build();
    }

    private List<LUCertificate> transformCertificatesBasedOnMetaDataQuery(
        List<DiagnosedCertificate> diagnosedCertificateList) {

        final var unitMap = getUnitMap(diagnosedCertificateList);
        final var careProviderMap = getCareProviderMap(diagnosedCertificateList);
        final var doctorMap = getDoctorMap(diagnosedCertificateList);

        return diagnosedCertificateList.stream()
            .filter(this::commonFilter).map(diagnosedCertificate ->
                LUCertificate.builder().certificateId(diagnosedCertificate.getCertificateId())
                    .certificateType(translateCertificateTypeName(diagnosedCertificate.getCertificateType()))
                    .careProviderId(diagnosedCertificate.getCareProviderId())
                    .careProviderName(careProviderMap.get(diagnosedCertificate.getCareProviderId()).getNamn())
                    .careUnitId(diagnosedCertificate.getCareUnitId())
                    .careUnitName(unitMap.get(diagnosedCertificate.getCareUnitId()).getNamn())
                    .signingTimeStamp(diagnosedCertificate.getSigningDateTime())
                    .patient(new Patient(diagnosedCertificate.getPersonId(), diagnosedCertificate.getPatientFullName()))
                    .doctor(doctorMap.get(diagnosedCertificate.getPersonalHsaId()))
                    .diagnosis(getDiagnosis(diagnosedCertificate.getDiagnoseCode()))
                    .biDiagnoses(getDiagnosisList(diagnosedCertificate.getSecondaryDiagnoseCodes()))
                    .build()
            ).collect(Collectors.toList());
    }

    private Map<String, Lakare> getDoctorMap(List<DiagnosedCertificate> diagnosedCertificateList) {
        final var doctorIds = new ArrayList<String>();

        diagnosedCertificateList.forEach(diagnosedCertificate -> {
            if (!doctorIds.contains(diagnosedCertificate.getPersonalHsaId())) {
                doctorIds.add(diagnosedCertificate.getPersonalHsaId());
            }
        });

        final var doctorMap = new HashMap<String, Lakare>(doctorIds.size());

        for (String doctorId : doctorIds) {
            final var doctor = getDoctor(doctorId);
            doctorMap.put(doctorId, doctor);
        }

        return doctorMap;
    }

    private Map<String, Vardenhet> getUnitMap(List<DiagnosedCertificate> diagnosedCertificateList) {
        final var unitIds = new ArrayList<String>();

        diagnosedCertificateList.forEach(diagnosedCertificate -> {
            if (!unitIds.contains(diagnosedCertificate.getCareUnitId())) {
                unitIds.add(diagnosedCertificate.getCareUnitId());
            }
        });

        final var unitMap = new HashMap<String, Vardenhet>(unitIds.size());

        for (String unitId : unitIds) {
            final var careUnit = hsaOrganizationsService.getVardenhet(unitId);
            unitMap.put(unitId, careUnit);
        }

        return unitMap;
    }

    private Map<String, Vardgivare> getCareProviderMap(List<DiagnosedCertificate> diagnosedCertificateList) {
        final var careProviderIds = new ArrayList<String>();

        diagnosedCertificateList.forEach(diagnosedCertificate -> {
            if (!careProviderIds.contains(diagnosedCertificate.getCareProviderId())) {
                careProviderIds.add(diagnosedCertificate.getCareProviderId());
            }
        });

        final var careProviderMap = new HashMap<String, Vardgivare>(careProviderIds.size());

        for (String careProviderId : careProviderIds) {
            final var careProvider = hsaOrganizationsService.getVardgivareInfo(careProviderId);
            careProviderMap.put(careProviderId, careProvider);
        }

        return careProviderMap;
    }

    private List<LUCertificate> transformDiagnosedCertificatesToLUCertificates(
        List<DiagnosedCertificate> diagnosedCertificateList) {

        return diagnosedCertificateList.stream().filter(this::commonFilter).map(this::convertDiagnosedCertificateToLUCertificate)
            .collect(Collectors.toList());
    }

    private boolean commonFilter(BaseCertificate certificate) {
        return !certificate.isDeleted() && !certificate.isTestCertificate();
    }

    private boolean populateLUCertificatesWithNotificationData(List<LUCertificate> luCertificateList) {
        try {
            unansweredQAsInfoDecorator.updateLUCertificatesWithQAs(luCertificateList);
            return false;
        } catch (WcIntegrationException e) {
            return true;
        }
    }

    private LUCertificate convertDiagnosedCertificateToLUCertificate(DiagnosedCertificate diagnosedCertificate) {
        var careUnit = hsaOrganizationsService.getVardenhet(diagnosedCertificate.getCareUnitId());
        var careProvider = hsaOrganizationsService.getVardgivareInfo(diagnosedCertificate.getCareProviderId());

        return LUCertificate.builder().certificateId(diagnosedCertificate.getCertificateId())
            .certificateType(translateCertificateTypeName(diagnosedCertificate.getCertificateType()))
            .careProviderId(diagnosedCertificate.getCareProviderId())
            .careProviderName(careProvider.getNamn())
            .careUnitId(diagnosedCertificate.getCareUnitId())
            .careUnitName(careUnit.getNamn())
            .signingTimeStamp(diagnosedCertificate.getSigningDateTime())
            .patient(new Patient(diagnosedCertificate.getPersonId(), diagnosedCertificate.getPatientFullName()))
            .doctor(new Lakare(diagnosedCertificate.getPersonalHsaId(), diagnosedCertificate.getPersonalFullName()))
            .diagnosis(getDiagnosis(diagnosedCertificate.getDiagnoseCode()))
            .biDiagnoses(getDiagnosisList(diagnosedCertificate.getSecondaryDiagnoseCodes())).build();
    }

    private List<Diagnos> getDiagnosisList(List<String> secondaryDiagnoseCodes) {
        if (secondaryDiagnoseCodes != null) {
            return secondaryDiagnoseCodes.stream().map(this::getDiagnosis).collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    private Diagnos getDiagnosis(String code) {
        if (code != null) {
            var diagnoseCode = DiagnosKod.create(code);
            return diagnosFactory.getDiagnos(diagnoseCode.getOriginalCode(), diagnoseCode.getCleanedCode(), diagnoseCode.getName());
        } else {
            return null;
        }
    }

    private String translateCertificateTypeName(String type) {
        String name = type;

        switch (type) {
            case "luse":
                name = "FK7800";
                break;
            case "luae_na":
                name = "FK7801";
                break;
            case "luae_fs":
                name = "FK7802";
                break;
            case "ag7804":
                name = "AG7804";
                break;
            case "ag114":
                name = "AG1-14";
                break;
        }

        return name;
    }

    private String translateCertificateType(String name) {
        var type = name;

        switch (name) {
            case "FK7800":
                type = "luse";
                break;
            case "FK7801":
                type = "luae_na";
                break;
            case "FK7802":
                type = "luae_fs";
                break;
            case "AG7804":
                type = "ag7804";
                break;
            case "AG1-14":
                type = "ag114";
                break;
        }

        return type;
    }


}
