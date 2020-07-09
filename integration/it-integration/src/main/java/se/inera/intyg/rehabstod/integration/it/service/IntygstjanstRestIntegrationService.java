package se.inera.intyg.rehabstod.integration.it.service;

import java.time.LocalDate;
import java.util.List;
import se.inera.intyg.infra.certificate.dto.DiagnosedCertificate;
import se.inera.intyg.infra.certificate.dto.SickLeaveCertificate;


public interface IntygstjanstRestIntegrationService {

    List<DiagnosedCertificate> getDiagnosedCertificatesForCareUnit(List<String> units, List<String> certificateTypes, LocalDate fromDate,
        LocalDate toDate);

    List<DiagnosedCertificate> getDiagnosedCertificatesForPerson(String personId, List<String> certificateTypes, LocalDate fromDate,
        LocalDate toDate, List<String> units);

    List<SickLeaveCertificate> getSickLeaveCertificatesForPerson(String personId, List<String> certificateTypes, LocalDate fromDate,
        LocalDate toDate, List<String> units);

}
