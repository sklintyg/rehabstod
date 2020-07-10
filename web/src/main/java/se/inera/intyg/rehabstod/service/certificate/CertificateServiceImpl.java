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

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.certificate.dto.DiagnosedCertificate;
import se.inera.intyg.infra.certificate.dto.SickLeaveCertificate;
import se.inera.intyg.infra.certificate.dto.SickLeaveCertificate.WorkCapacity;
import se.inera.intyg.infra.sjukfall.dto.DiagnosKod;
import se.inera.intyg.rehabstod.integration.it.service.IntygstjanstRestIntegrationService;
import se.inera.intyg.rehabstod.service.pdl.LogService;
import se.inera.intyg.rehabstod.service.sjukfall.komplettering.KompletteringInfoDecorator;
import se.inera.intyg.rehabstod.web.model.AGCertificate;
import se.inera.intyg.rehabstod.web.model.Diagnos;
import se.inera.intyg.rehabstod.web.model.LUCertificate;
import se.inera.intyg.rehabstod.web.model.Lakare;
import se.inera.intyg.rehabstod.web.model.Patient;

@Service
public class CertificateServiceImpl implements CertificateService {

    private static final Logger LOG = LoggerFactory.getLogger(CertificateServiceImpl.class); //TODO

    private final IntygstjanstRestIntegrationService restIntegrationService;

    private static final String[] LU_TYPE_LIST = {"luse", "luae-na", "luae-fs"};
    private static final String[] AG_TYPE_LIST = {"ag114", "ag7804"};

    private final KompletteringInfoDecorator kompletteringInfoDecorator;

    private final LogService logService;

    @Autowired
    public CertificateServiceImpl(
        IntygstjanstRestIntegrationService restIntegrationService, KompletteringInfoDecorator kompletteringInfoDecorator,
        LogService logService) {
        this.restIntegrationService = restIntegrationService;
        this.kompletteringInfoDecorator = kompletteringInfoDecorator;
        this.logService = logService;
    }

    @Override
    public List<LUCertificate> getLUCertificatesForCareUnit(String unit, LocalDate fromDate, LocalDate toDate) {
        var diagnosedCertificateList = restIntegrationService
            .getDiagnosedCertificatesForCareUnit(Collections.singletonList(unit), Arrays.asList(LU_TYPE_LIST), fromDate, toDate);

        var luCertificateList = transformDiagnosedCertificatesToLUCertificates(diagnosedCertificateList);

        pdlLogLUCertificatesForCareUnit(luCertificateList);

        return luCertificateList;
    }

    @Override
    public List<LUCertificate> getLUCertificatesForPerson(String personId, LocalDate fromDate, LocalDate toDate, String unit) {
        var diagnosedCertificateList = restIntegrationService
            .getDiagnosedCertificatesForPerson(personId, Arrays.asList(LU_TYPE_LIST), fromDate, toDate, Collections.singletonList(unit));

        var luCertificateList = transformDiagnosedCertificatesToLUCertificates(diagnosedCertificateList);

        pdlLogLUCertificatesForPerson(personId);

        return luCertificateList;
    }

    @Override
    public List<AGCertificate> getAGCertificatesForPerson(String personId, LocalDate fromDate, LocalDate toDate, String unit) {
        var sickLeaveCertificateList = restIntegrationService
            .getSickLeaveCertificatesForPerson(personId, Arrays.asList(AG_TYPE_LIST), fromDate, toDate, Collections.singletonList(unit));

        var agCertificates = transformSickLeaveCertificatesToLUCertificates(sickLeaveCertificateList);

        pdlLogAGCertificatesForPerson(personId);

        return agCertificates;
    }

    private void pdlLogAGCertificatesForPerson(String personId) {
    }

    private void pdlLogLUCertificatesForCareUnit(List<LUCertificate> luCertificateList) {
    }

    private void pdlLogLUCertificatesForPerson(String personId) {
    }

    private List<AGCertificate> transformSickLeaveCertificatesToLUCertificates(List<SickLeaveCertificate> sickLeaveCertificateList) {
        List<AGCertificate> agCertificateList = sickLeaveCertificateList.stream().map(this::convertSickLeaveCertificateToLUCertificate)
            .collect(Collectors.toList());
        populateAGCertificatesWithNotificationData(agCertificateList);

        return agCertificateList;
    }

    private void populateAGCertificatesWithNotificationData(List<AGCertificate> agCertificateList) {
        kompletteringInfoDecorator.updateAGCertificatesWithKompletteringar(agCertificateList);
    }

    private AGCertificate convertSickLeaveCertificateToLUCertificate(SickLeaveCertificate sickLeaveCertificate) {

        var workCapacityList = sickLeaveCertificate.getWorkCapacityList();
        var startDate = workCapacityList.stream().min(Comparator.comparing(WorkCapacity::getStartDate)).get().getStartDate();
        var endDate = workCapacityList.stream().max(Comparator.comparing(WorkCapacity::getEndDate)).get().getEndDate();

        return AGCertificate.builder().certificateId(sickLeaveCertificate.getCertificateId())
            .signingTimeStamp(sickLeaveCertificate.getSigningDateTime())
            .patient(new Patient(sickLeaveCertificate.getPersonId(), sickLeaveCertificate.getPatientFullName()))
            .doctor(new Lakare(sickLeaveCertificate.getPersonalHsaId(), sickLeaveCertificate.getPersonalFullName()))
            .diagnose(getDiagnose(sickLeaveCertificate.getDiagnoseCode()))
            .biDiagnosis(getDiagnoseList(sickLeaveCertificate.getSecondaryDiagnoseCodes()))
            .start(startDate)
            .end(endDate)
            .days((int) ChronoUnit.DAYS.between(startDate, endDate))
            .degree(workCapacityList.stream().sorted(Comparator.comparing(WorkCapacity::getStartDate)).map(WorkCapacity::getReduction)
                .collect(Collectors.toList()))
            .occupation(Arrays.asList(sickLeaveCertificate.getOccupation().split(","))).build();
    }

    private List<LUCertificate> transformDiagnosedCertificatesToLUCertificates(
        List<DiagnosedCertificate> diagnosedCertificateList) {

        List<LUCertificate> luCertificateList = diagnosedCertificateList.stream().map(this::convertDiagnosedCertificateToLUCertificate)
            .collect(Collectors.toList());
        populateLUCertificatesWithNotificationData(luCertificateList);

        return luCertificateList;
    }

    private void populateLUCertificatesWithNotificationData(List<LUCertificate> luCertificateList) {
        kompletteringInfoDecorator.updateLUCertificatesWithKompletteringar(luCertificateList);
    }

    private LUCertificate convertDiagnosedCertificateToLUCertificate(DiagnosedCertificate diagnosedCertificate) {
        return LUCertificate.builder().certificateId(diagnosedCertificate.getCertificateId())
            .signingTimeStamp(diagnosedCertificate.getSigningDateTime())
            .patient(new Patient(diagnosedCertificate.getPersonId(), diagnosedCertificate.getPatientFullName()))
            .doctor(new Lakare(diagnosedCertificate.getPersonalHsaId(), diagnosedCertificate.getPersonalFullName()))
            .diagnose(getDiagnose(diagnosedCertificate.getDiagnoseCode()))
            .biDiagnosis(getDiagnoseList(diagnosedCertificate.getSecondaryDiagnoseCodes())).build();
    }

    private List<Diagnos> getDiagnoseList(List<String> secondaryDiagnoseCodes) {
        return secondaryDiagnoseCodes.stream().map(this::getDiagnose).collect(Collectors.toList());
    }

    private Diagnos getDiagnose(String code) {
        var diagnoseCode = new DiagnosKod(code);
        return new Diagnos(diagnoseCode.getOriginalCode(), diagnoseCode.getCleanedCode(), diagnoseCode.getName());
    }
}
