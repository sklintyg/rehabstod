/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.certificate.dto.BaseCertificate;
import se.inera.intyg.infra.certificate.dto.DiagnosedCertificate;
import se.inera.intyg.infra.certificate.dto.SickLeaveCertificate;
import se.inera.intyg.infra.certificate.dto.SickLeaveCertificate.WorkCapacity;
import se.inera.intyg.infra.integration.hsa.services.HsaOrganizationsService;
import se.inera.intyg.infra.logmessages.ActivityType;
import se.inera.intyg.infra.logmessages.ResourceType;
import se.inera.intyg.infra.sjukfall.dto.DiagnosKod;
import se.inera.intyg.rehabstod.auth.pdl.PDLActivityEntry;
import se.inera.intyg.rehabstod.auth.pdl.PDLActivityStore;
import se.inera.intyg.rehabstod.integration.it.service.IntygstjanstRestIntegrationService;
import se.inera.intyg.rehabstod.integration.wc.exception.WcIntegrationException;
import se.inera.intyg.rehabstod.service.Urval;
import se.inera.intyg.rehabstod.service.diagnos.DiagnosFactory;
import se.inera.intyg.rehabstod.service.pdl.LogService;
import se.inera.intyg.rehabstod.service.sjukfall.komplettering.UnansweredQAsInfoDecorator;
import se.inera.intyg.rehabstod.service.user.UserService;
import se.inera.intyg.rehabstod.web.controller.api.dto.GetAGCertificatesForPersonResponse;
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

    private final UnansweredQAsInfoDecorator unansweredQAsInfoDecorator;

    private final LogService logService;

    private final UserService userService;

    private final DiagnosFactory diagnosFactory;

    private final HsaOrganizationsService hsaOrganizationsService;

    @Autowired
    public CertificateServiceImpl(
        IntygstjanstRestIntegrationService restIntegrationService, UnansweredQAsInfoDecorator unansweredQAsInfoDecorator,
        LogService logService, UserService userService, DiagnosFactory diagnosFactory, HsaOrganizationsService hsaOrganizationsService) {
        this.restIntegrationService = restIntegrationService;
        this.unansweredQAsInfoDecorator = unansweredQAsInfoDecorator;
        this.logService = logService;
        this.userService = userService;
        this.diagnosFactory = diagnosFactory;
        this.hsaOrganizationsService = hsaOrganizationsService;
    }

    @Override
    public GetLUCertificatesForCareUnitResponse getLUCertificatesForCareUnit(GetLUCertificatesForCareUnitRequest request) {
        var user = userService.getUser();
        var unitId = user.getValdVardenhet().getId();
        var urval = user.getUrval();

        var diagnosedCertificateList = restIntegrationService
            .getDiagnosedCertificatesForCareUnit(Collections.singletonList(unitId), Arrays.asList(LU_TYPE_LIST), request.getFromDate(),
                request.getToDate());

        var certTypes = request.getCertTypes();
        var diagnoses = request.getDiagnoses();
        var fromAge = request.getFromAge();
        var toAge = request.getToAge();
        var doctors = request.getDoctors();

        if (certTypes != null && !certTypes.isEmpty()) {
            diagnosedCertificateList = diagnosedCertificateList.stream()
                .filter(c -> certTypes.contains(translateCertificateTypeName(c.getCertificateType()))).collect(Collectors.toList());
        }

        if (diagnoses != null && !diagnoses.isEmpty()) {
            diagnosedCertificateList = diagnosedCertificateList.stream()
                .filter(c -> filterOnDiagnoses(c, diagnoses)).collect(Collectors.toList());
        }

        if (fromAge > 0 || (toAge > 0 && toAge <= 100)) {
            diagnosedCertificateList = diagnosedCertificateList.stream().filter(c -> filterOnAge(c, fromAge, toAge))
                .collect(Collectors.toList());
        }

        if (urval == Urval.ISSUED_BY_ME) {
            diagnosedCertificateList = diagnosedCertificateList.stream().filter(c -> user.getHsaId().equals(c.getPersonalHsaId()))
                .collect(Collectors.toList());
        } else if (doctors != null && !doctors.isEmpty()) {
            diagnosedCertificateList = diagnosedCertificateList.stream().filter(c -> doctors.contains(c.getPersonalFullName()))
                .collect(Collectors.toList());
        }

        var luCertificateList = transformDiagnosedCertificatesToLUCertificates(diagnosedCertificateList);
        boolean qaInfoError = false;
        try {
            populateLUCertificatesWithNotificationData(luCertificateList);
        } catch (WcIntegrationException e) {
            qaInfoError = true;
        }

        var qas = request.getQas();
        if (qas > 0) {
            switch (qas) {
                case 1:
                    luCertificateList = luCertificateList.stream().filter(c -> c.getUnAnsweredComplement() == 0)
                        .collect(Collectors.toList());
                    break;
                case 2:
                    luCertificateList = luCertificateList.stream().filter(c -> c.getUnAnsweredComplement() > 0)
                        .collect(Collectors.toList());
                    break;
                case 3:
                    luCertificateList = luCertificateList.stream().filter(c -> c.getUnAnsweredOther() == 0)
                        .collect(Collectors.toList());
                    break;
                case 4:
                    luCertificateList = luCertificateList.stream().filter(c -> c.getUnAnsweredOther() > 0)
                        .collect(Collectors.toList());
                    break;
                default:
                    break;
            }
        }

        var searchText = request.getSearchText();
        if (searchText != null && !searchText.isBlank() && !searchText.isEmpty()) {
            luCertificateList = luCertificateList.stream().filter(c -> filterOnText(c, searchText)).collect(Collectors.toList());
        }

        pdlLogLUCertificatesForCareUnit(luCertificateList);

        LOGGER.debug("Returning LU Certificates for Care Unit");
        return new GetLUCertificatesForCareUnitResponse(luCertificateList, qaInfoError);
    }

    private boolean filterOnText(LUCertificate c, String searchText) {

        var patient = c.getPatient();
        if (patient != null) {
            if (patient.getId() != null && patient.getId().contains(searchText)) {
                return true;
            }
            if (patient.getNamn() != null && patient.getNamn().contains(searchText)) {
                return true;
            }
            if (patient.getKon() != null && patient.getKon().getDescription().contains(searchText)) {
                return true;
            }
            if (patient.getAlder() > -1 && String.format("%d år", patient.getAlder()).contains(searchText)) {
                return true;
            }
        }

        if (c.getDiagnosis() != null) {
            if (c.getDiagnosis().getIntygsVarde() != null && c.getDiagnosis().getIntygsVarde().contains(searchText)) {
                return true;
            }
            if (c.getDiagnosis().getBeskrivning() != null && c.getDiagnosis().getBeskrivning().contains(searchText)) {
                return true;
            }
        }

        var biDiagnoses = c.getBiDiagnoses();
        if (biDiagnoses != null && !biDiagnoses.isEmpty()) {
            for (var diagnosis : biDiagnoses) {
                if (diagnosis.getIntygsVarde() != null && diagnosis.getIntygsVarde().contains(searchText)) {
                    return true;
                }
                if (diagnosis.getBeskrivning() != null && diagnosis.getBeskrivning().contains(searchText)) {
                    return true;
                }
            }
        }

        if (c.getCertificateType() != null && c.getCertificateType().contains(searchText)) {
            return true;
        }

        if (c.getSigningTimeStamp() != null && c.getSigningTimeStamp().toLocalDate().toString().contains(searchText)) {
            return true;
        }

        return c.getDoctor() != null && c.getDoctor().getNamn() != null && c.getDoctor().getNamn().contains(searchText);
    }

    private boolean filterOnAge(DiagnosedCertificate c, int fromAge, int toAge) {
        var patient = new Patient(c.getPersonId(), c.getPatientFullName());

        if (fromAge > 0 && patient.getAlder() < fromAge) {
            return false;
        }
        return toAge <= 0 || toAge > 100 || patient.getAlder() <= toAge;
    }

    private boolean filterOnDiagnoses(DiagnosedCertificate diagnosedCertificate, List<String> diagnoseGroupList) {
        List<String> diagnoseList;

        if (diagnosedCertificate.getSecondaryDiagnoseCodes() != null) {
            diagnoseList = Stream
                .concat(Stream.of(diagnosedCertificate.getDiagnoseCode()), diagnosedCertificate.getSecondaryDiagnoseCodes().stream())
                .collect(Collectors.toList());
        } else {
            diagnoseList = Collections.singletonList(diagnosedCertificate.getDiagnoseCode());
        }

        for (String d : diagnoseList) {
            for (String dg : diagnoseGroupList) {
                if (belongsToDiagnoseGroup(d, dg)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean belongsToDiagnoseGroup(String diagnose, String diagnoseGroup) {
        if (!diagnose.startsWith(diagnoseGroup.substring(0, 1))) {
            return false;
        }

        var diagnoseNumber = Integer.parseInt(diagnose.substring(1, 3));
        var splits = diagnoseGroup.split("-");
        return diagnoseNumber >= Integer.parseInt(splits[0].substring(1, 3))
            && diagnoseNumber <= Integer.parseInt(splits[1].substring(1, 3));
    }

    @Override
    public List<String> getDoctorsForUnit() {
        var unitId = userService.getUser().getValdVardenhet().getId();
        return restIntegrationService.getSigningDoctorsForUnit(Collections.singletonList(unitId), Arrays.asList(LU_TYPE_LIST));
    }

    @Override
    public GetLUCertificatesForPersonResponse getLUCertificatesForPerson(String personId) {
        var unitId = userService.getUser().getValdVardenhet().getId();
        var diagnosedCertificateList = restIntegrationService
            .getDiagnosedCertificatesForPerson(personId, Arrays.asList(LU_TYPE_LIST), Collections.singletonList(unitId));

        var luCertificateList = transformDiagnosedCertificatesToLUCertificates(diagnosedCertificateList);
        boolean qaInfoError = false;
        try {
            populateLUCertificatesWithNotificationData(luCertificateList);
        } catch (WcIntegrationException e) {
            qaInfoError = true;
        }

        LOGGER.debug("Adding PDL log for certificate read");
        var rehabstodUser = userService.getUser();
        var storedActivities = rehabstodUser.getStoredActivities();
        pdlLogCertificatesForPerson(personId, unitId, storedActivities);

        LOGGER.debug("Returning LU Certificates for Person");
        return new GetLUCertificatesForPersonResponse(luCertificateList, qaInfoError);
    }

    @Override
    public GetAGCertificatesForPersonResponse getAGCertificatesForPerson(String personId) {
        var unitId = userService.getUser().getValdVardenhet().getId();
        var sickLeaveCertificateList = restIntegrationService
            .getSickLeaveCertificatesForPerson(personId, Arrays.asList(AG_TYPE_LIST), Collections.singletonList(unitId));

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
        pdlLogCertificatesForPerson(personId, unitId, storedActivities);

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
        logService.logCertificate(luCertificateList, readActivityType, resourceTypeCertificate, storedActivities);
        PDLActivityStore.addActivitiesToStore(luCertificateToLog, enhetsId, readActivityType, resourceTypeCertificate,
            rehabstodUser.getStoredActivities());

    }

    private List<AGCertificate> transformSickLeaveCertificatesToAGCertificates(List<SickLeaveCertificate> sickLeaveCertificateList) {

        return sickLeaveCertificateList.stream().filter(this::commonFilter).map(this::convertSickLeaveCertificateToLUCertificate)
            .sorted(Comparator.comparing(AGCertificate::getSigningTimeStamp).reversed()).collect(Collectors.toList());
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

    private List<LUCertificate> transformDiagnosedCertificatesToLUCertificates(
        List<DiagnosedCertificate> diagnosedCertificateList) {

        return diagnosedCertificateList.stream().filter(this::commonFilter).map(this::convertDiagnosedCertificateToLUCertificate)
            .collect(Collectors.toList());
    }

    private boolean commonFilter(BaseCertificate certificate) {
        return !certificate.isDeleted() && !certificate.isTestCertificate();
    }

    private void populateLUCertificatesWithNotificationData(List<LUCertificate> luCertificateList) {
        unansweredQAsInfoDecorator.updateLUCertificatesWithQAs(luCertificateList);
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
            var diagnoseCode = new DiagnosKod(code);
            return diagnosFactory.getDiagnos(diagnoseCode.getOriginalCode(), diagnoseCode.getCleanedCode(), diagnoseCode.getName());
        } else  {
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
}
