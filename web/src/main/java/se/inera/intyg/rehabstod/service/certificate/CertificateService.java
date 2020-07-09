package se.inera.intyg.rehabstod.service.certificate;

import java.time.LocalDate;
import java.util.List;
import se.inera.intyg.rehabstod.web.model.AGCertificate;
import se.inera.intyg.rehabstod.web.model.LUCertificate;

public interface CertificateService {

    List<LUCertificate> getLUCertificatesForCareUnit(String unit, LocalDate fromDate, LocalDate toDate);

    List<LUCertificate> getLUCertificatesForPerson(String personId, LocalDate fromDate, LocalDate toDate, String unit);

    List<AGCertificate> getAGCertificatesForPerson(String personId, LocalDate fromDate, LocalDate toDate, String unit);
}
