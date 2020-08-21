package se.inera.intyg.rehabstod.integration.it.stub;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.certificate.dto.BaseCertificate;
import se.inera.intyg.infra.certificate.dto.DiagnosedCertificate;
import se.inera.intyg.infra.certificate.dto.SickLeaveCertificate;
import se.inera.intyg.rehabstod.integration.it.service.IntygstjanstRestIntegrationService;

@Profile("rhs-it-stub")
@Service
public class IntygstjanstRestIntegrationServiceStub implements IntygstjanstRestIntegrationService {

    @Autowired
    private RSTestIntygStub rsTestIntygStub;

    @Override
    public List<DiagnosedCertificate> getDiagnosedCertificatesForCareUnit(List<String> units, List<String> certificateTypes,
        LocalDate fromDate, LocalDate toDate) {
        return rsTestIntygStub.getLUCertificateData();
    }

    @Override
    public List<DiagnosedCertificate> getDiagnosedCertificatesForPerson(String personId, List<String> certificateTypes,
        List<String> units) {
        return getDiagnosedCertificatesForPerson(personId, certificateTypes, null, null, units);
    }

    @Override
    public List<DiagnosedCertificate> getDiagnosedCertificatesForPerson(String personId, List<String> certificateTypes, LocalDate fromDate,
        LocalDate toDate, List<String> units) {

        var luCertificateData = rsTestIntygStub.getLUCertificateData();
        return luCertificateData.stream().filter(c -> personId.equals(c.getPersonId())).collect(Collectors.toList());
    }

    @Override
    public List<SickLeaveCertificate> getSickLeaveCertificatesForPerson(String personId, List<String> certificateTypes,
        List<String> units) {
        return getSickLeaveCertificatesForPerson(personId, certificateTypes, null, null, units);
    }

    @Override
    public List<SickLeaveCertificate> getSickLeaveCertificatesForPerson(String personId, List<String> certificateTypes, LocalDate fromDate,
        LocalDate toDate, List<String> units) {
        var agCertificateData = rsTestIntygStub.getAGCertificateData();
        return agCertificateData.stream().filter(c -> personId.equals(c.getPersonId())).collect(Collectors.toList());
    }

    @Override
    public List<String> getSigningDoctorsForUnit(List<String> units, List<String> certificateTypes) {
        return rsTestIntygStub.getLUCertificateData().stream().map(BaseCertificate::getPersonalFullName).distinct()
            .collect(Collectors.toList());
    }
}
