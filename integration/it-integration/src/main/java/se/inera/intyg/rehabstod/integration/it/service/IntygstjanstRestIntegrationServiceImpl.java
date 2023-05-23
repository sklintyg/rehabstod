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
package se.inera.intyg.rehabstod.integration.it.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import se.inera.intyg.infra.certificate.dto.DiagnosedCertificate;
import se.inera.intyg.infra.certificate.dto.SickLeaveCertificate;
import se.inera.intyg.infra.certificate.dto.TypedCertificateRequest;
import se.inera.intyg.rehabstod.integration.it.dto.*;

@Profile("!rhs-it-stub")
@Service
public class IntygstjanstRestIntegrationServiceImpl implements IntygstjanstRestIntegrationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(IntygstjanstRestIntegrationServiceImpl.class);

    private final RestTemplate restTemplate;

    @Value("${intygstjanst.host.url}")
    private String intygstjanstUrl;

    @Autowired
    public IntygstjanstRestIntegrationServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public List<DiagnosedCertificate> getDiagnosedCertificatesForCareUnit(List<String> units, List<String> certificateTypes,
        LocalDate fromDate, LocalDate toDate, List<String> doctorIds) {
        final String url = intygstjanstUrl + "/inera-certificate/internalapi/typedcertificate/diagnosed/unit";
        TypedCertificateRequest requestObject = getTypedCertificateRequest(units, certificateTypes, fromDate, toDate, null);
        requestObject.setDoctorIds(doctorIds);

        LOGGER.debug("Getting diagnosed certificates for care unit from intygstjansten");

        return buildListResponseFromArray(restTemplate.postForObject(url, requestObject, DiagnosedCertificate[].class));
    }

    @Override
    public List<DiagnosedCertificate> getDiagnosedCertificatesForPerson(String personId, List<String> certificateTypes,
        List<String> units) {
        return getDiagnosedCertificatesForPerson(personId, certificateTypes, null, null, units);
    }

    @Override
    public List<DiagnosedCertificate> getDiagnosedCertificatesForPerson(String personId, List<String> certificateTypes, LocalDate fromDate,
        LocalDate toDate, List<String> units) {
        final String url = intygstjanstUrl + "/inera-certificate/internalapi/typedcertificate/diagnosed/person";
        TypedCertificateRequest requestObject = getTypedCertificateRequest(units, certificateTypes, fromDate, toDate, personId);

        LOGGER.debug("Getting diagnosed certificates for person from intygstjansten");

        return buildListResponseFromArray(restTemplate.postForObject(url, requestObject, DiagnosedCertificate[].class));
    }

    @Override
    public List<SickLeaveCertificate> getSickLeaveCertificatesForPerson(String personId, List<String> certificateTypes,
        List<String> units) {
        return getSickLeaveCertificatesForPerson(personId, certificateTypes, null, null, units);
    }

    @Override
    public List<SickLeaveCertificate> getSickLeaveCertificatesForPerson(String personId, List<String> certificateTypes, LocalDate fromDate,
        LocalDate toDate, List<String> units) {
        final String url = intygstjanstUrl + "/inera-certificate/internalapi/typedcertificate/sickleave/person";
        TypedCertificateRequest requestObject = getTypedCertificateRequest(units, certificateTypes, fromDate, toDate, personId);

        LOGGER.debug("Getting sick leave certificates for person from intygstjansten");

        return buildListResponseFromArray(restTemplate.postForObject(url, requestObject, SickLeaveCertificate[].class));
    }

    @Override
    public List<String> getSigningDoctorsForUnit(List<String> units, List<String> certificateTypes) {
        final String url = intygstjanstUrl + "/inera-certificate/internalapi/typedcertificate/doctors";
        TypedCertificateRequest requestObject = getTypedCertificateRequest(units, certificateTypes, null, null, null);

        LOGGER.debug("Getting signing doctors for unit from intygstjansten");

        return buildListResponseFromArray(restTemplate.postForObject(url, requestObject, String[].class));
    }

    @Override
    public SickLeavesResponseDTO getActiveSickLeaves(SickLeavesRequestDTO request) {
        final String url = intygstjanstUrl + "/inera-certificate/internalapi/sickleave/active";

        LOGGER.debug("Getting active sick leaves from Intygstjansten");

        return restTemplate.postForObject(url, request, SickLeavesResponseDTO.class);
    }

    @Override
    public Response setRekoStatusForSickLeave(SetRekoStatusToSickLeaveRequestDTO request) {
        final String url = intygstjanstUrl + "/inera-certificate/internalapi/reko/set";

        LOGGER.debug("Setting reko status to sick leave");

        return restTemplate.postForObject(url, request, Response.class);
    }

    @Override
    public PopulateFiltersResponseDTO getPopulatedFiltersForActiveSickLeaves(PopulateFiltersRequestDTO request) {
        final String url = intygstjanstUrl + "/inera-certificate/internalapi/sickleave/filters";

        LOGGER.debug("Getting doctors with active sick leaves from Intygstjansten");

        return restTemplate.postForObject(url, request, PopulateFiltersResponseDTO.class);
    }

    private <E> List<E> buildListResponseFromArray(E[] array) {
        List<E> response = new ArrayList<>();
        if (array != null && array.length > 0) {
            Collections.addAll(response, array);
        }
        return response;
    }

    private TypedCertificateRequest getTypedCertificateRequest(List<String> units, List<String> certificateTypes, LocalDate fromDate,
        LocalDate toDate, String personId) {
        TypedCertificateRequest requestObject = new TypedCertificateRequest();
        requestObject.setUnitIds(units);
        requestObject.setCertificateTypes(certificateTypes);
        requestObject.setPersonId(personId);
        requestObject.setToDate(toDate);

        if (fromDate == null) {
            requestObject.setFromDate(LocalDate.now().minusYears(3));
        } else {
            requestObject.setFromDate(fromDate);
        }
        return requestObject;
    }
}
