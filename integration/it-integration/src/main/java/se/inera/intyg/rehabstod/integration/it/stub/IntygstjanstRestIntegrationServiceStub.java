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
