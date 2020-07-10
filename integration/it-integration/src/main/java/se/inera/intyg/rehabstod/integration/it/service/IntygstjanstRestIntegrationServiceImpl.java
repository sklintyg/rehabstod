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
package se.inera.intyg.rehabstod.integration.it.service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import se.inera.intyg.infra.certificate.dto.DiagnosedCertificate;
import se.inera.intyg.infra.certificate.dto.SickLeaveCertificate;
import se.inera.intyg.infra.certificate.dto.TypedCertificateRequest;

@Service
public class IntygstjanstRestIntegrationServiceImpl implements IntygstjanstRestIntegrationService {

    @Bean("itRestTemplate")
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Autowired
    @Qualifier("itRestTemplate")
    private RestTemplate restTemplate;

    @Value("${intygstjanst.host.url}")
    private String intygstjanstUrl;


    @Override
    public List<DiagnosedCertificate> getDiagnosedCertificatesForCareUnit(List<String> units, List<String> certificateTypes,
        LocalDate fromDate, LocalDate toDate) {
        final String url = intygstjanstUrl + "/inera-certificate/internalapi/typedcertificate/diagnosed/unit";
        TypedCertificateRequest requestObject = getTypedCertificateRequest(units, certificateTypes, fromDate, toDate, null);

        var diagnosedCertificates = restTemplate.postForObject(url, requestObject, DiagnosedCertificate[].class);
        return Arrays.asList(diagnosedCertificates); //TODO felhantering
    }

    @Override
    public List<DiagnosedCertificate> getDiagnosedCertificatesForPerson(String personId, List<String> certificateTypes, LocalDate fromDate,
        LocalDate toDate, List<String> units) {
        final String url = intygstjanstUrl + "/inera-certificate/internalapi/typedcertificate/diagnosed/person";
        TypedCertificateRequest requestObject = getTypedCertificateRequest(units, certificateTypes, fromDate, toDate, personId);

        var diagnosedCertificates = restTemplate.postForObject(url, requestObject, DiagnosedCertificate[].class);
        return Arrays.asList(diagnosedCertificates);
    }

    @Override
    public List<SickLeaveCertificate> getSickLeaveCertificatesForPerson(String personId, List<String> certificateTypes, LocalDate fromDate,
        LocalDate toDate, List<String> units) {
        final String url = intygstjanstUrl + "/inera-certificate/internalapi/typedcertificate/sickleave/unit";
        TypedCertificateRequest requestObject = getTypedCertificateRequest(units, certificateTypes, fromDate, toDate, personId);

        var sickLeaveCertificates = restTemplate.postForObject(url, requestObject, SickLeaveCertificate[].class);
        return Arrays.asList(sickLeaveCertificates);
    }

    private TypedCertificateRequest getTypedCertificateRequest(List<String> units, List<String> certificateTypes, LocalDate fromDate,
        LocalDate toDate, String personId) {
        TypedCertificateRequest requestObject = new TypedCertificateRequest();
        requestObject.setUnitIds(units);
        requestObject.setCertificateTypes(certificateTypes);
        requestObject.setPersonId(personId);
        requestObject.setFromDate(fromDate);
        requestObject.setToDate(toDate);
        return requestObject;
    }
}
