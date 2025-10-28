/*
 * Copyright (C) 2025 Inera AB (http://www.inera.se)
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

import static se.inera.intyg.rehabstod.logging.MdcHelper.LOG_SESSION_ID_HEADER;
import static se.inera.intyg.rehabstod.logging.MdcHelper.LOG_TRACE_ID_HEADER;
import static se.inera.intyg.rehabstod.logging.MdcLogConstants.SESSION_ID_KEY;
import static se.inera.intyg.rehabstod.logging.MdcLogConstants.TRACE_ID_KEY;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import se.inera.intyg.infra.certificate.dto.DiagnosedCertificate;
import se.inera.intyg.infra.certificate.dto.SickLeaveCertificate;
import se.inera.intyg.infra.certificate.dto.TypedCertificateRequest;
import se.inera.intyg.rehabstod.integration.it.dto.CreateRekoStatusRequestDTO;
import se.inera.intyg.rehabstod.integration.it.dto.GetRekoStatusRequestDTO;
import se.inera.intyg.rehabstod.integration.it.dto.PopulateFiltersRequestDTO;
import se.inera.intyg.rehabstod.integration.it.dto.PopulateFiltersResponseDTO;
import se.inera.intyg.rehabstod.integration.it.dto.RekoStatusDTO;
import se.inera.intyg.rehabstod.integration.it.dto.SickLeavesRequestDTO;
import se.inera.intyg.rehabstod.integration.it.dto.SickLeavesResponseDTO;
import se.inera.intyg.rehabstod.logging.MdcLogConstants;
import se.inera.intyg.rehabstod.logging.PerformanceLogging;

@Profile("!rhs-it-stub")
@Service
@RequiredArgsConstructor
public class IntygstjanstRestIntegrationServiceImpl implements IntygstjanstRestIntegrationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(IntygstjanstRestIntegrationServiceImpl.class);

    private final RestClient itRestClient;

    @Value("${integration.intygstjanst.scheme}")
    private String scheme;
    @Value("${integration.intygstjanst.baseurl}")
    private String baseUrl;
    @Value("${integration.intygstjanst.port}")
    private int port;

    @Override
    @PerformanceLogging(eventAction = "get-diagnosed-certificates-for-care-unit", eventType = MdcLogConstants.EVENT_TYPE_ACCESSED)
    public List<DiagnosedCertificate> getDiagnosedCertificatesForCareUnit(List<String> units, List<String> certificateTypes,
        LocalDate fromDate, LocalDate toDate, List<String> doctorIds) {
        final var url = "/inera-certificate/internalapi/typedcertificate/diagnosed/unit";
        final var requestObject = getTypedCertificateRequest(units, certificateTypes, fromDate, toDate, null);
        requestObject.setDoctorIds(doctorIds);

        LOGGER.debug("Getting diagnosed certificates for care unit from intygstjansten");

        return buildListResponseFromArray(
            itRestClient
                .post()
                .uri(uriBuilder -> uriBuilder
                    .scheme(scheme)
                    .host(baseUrl)
                    .port(port)
                    .path(url)
                    .build()
                )
                .body(requestObject)
                .header(LOG_TRACE_ID_HEADER, MDC.get(TRACE_ID_KEY))
                .header(LOG_SESSION_ID_HEADER, MDC.get(SESSION_ID_KEY))
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(DiagnosedCertificate[].class)
        );
    }

    @Override
    @PerformanceLogging(eventAction = "get-diagnosed-certificates-for-person", eventType = MdcLogConstants.EVENT_TYPE_ACCESSED)
    public List<DiagnosedCertificate> getDiagnosedCertificatesForPerson(String personId, List<String> certificateTypes,
        List<String> units) {
        return getDiagnosedCertificatesForPerson(personId, certificateTypes, null, null, units);
    }

    @Override
    @PerformanceLogging(eventAction = "get-diagnosed-certificates-for-person", eventType = MdcLogConstants.EVENT_TYPE_ACCESSED)
    public List<DiagnosedCertificate> getDiagnosedCertificatesForPerson(String personId, List<String> certificateTypes, LocalDate fromDate,
        LocalDate toDate, List<String> units) {
        final var url = "/inera-certificate/internalapi/typedcertificate/diagnosed/person";
        final var requestObject = getTypedCertificateRequest(units, certificateTypes, fromDate, toDate, personId);

        LOGGER.debug("Getting diagnosed certificates for person from intygstjansten");

        return buildListResponseFromArray(
            itRestClient
                .post()
                .uri(uriBuilder -> uriBuilder
                    .scheme(scheme)
                    .host(baseUrl)
                    .port(port)
                    .path(url)
                    .build()
                )
                .body(requestObject)
                .header(LOG_TRACE_ID_HEADER, MDC.get(TRACE_ID_KEY))
                .header(LOG_SESSION_ID_HEADER, MDC.get(SESSION_ID_KEY))
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(DiagnosedCertificate[].class)
        );
    }

    @Override
    @PerformanceLogging(eventAction = "get-sick-leave-certificates-for-person", eventType = MdcLogConstants.EVENT_TYPE_ACCESSED)
    public List<SickLeaveCertificate> getSickLeaveCertificatesForPerson(String personId, List<String> certificateTypes,
        List<String> units, List<String> doctorIds) {
        return getSickLeaveCertificatesForPerson(personId, certificateTypes, null, null, units, doctorIds);
    }

    @Override
    @PerformanceLogging(eventAction = "get-sick-leave-certificates-for-person", eventType = MdcLogConstants.EVENT_TYPE_ACCESSED)
    public List<SickLeaveCertificate> getSickLeaveCertificatesForPerson(String personId, List<String> certificateTypes, LocalDate fromDate,
        LocalDate toDate, List<String> units, List<String> doctorIds) {
        final var url = "/inera-certificate/internalapi/typedcertificate/sickleave/person";
        final var requestObject = getTypedCertificateRequest(units, certificateTypes, fromDate, toDate, personId);
        requestObject.setDoctorIds(doctorIds);

        LOGGER.debug("Getting sick leave certificates for person from intygstjansten");

        return buildListResponseFromArray(
            itRestClient
                .post()
                .uri(uriBuilder -> uriBuilder
                    .scheme(scheme)
                    .host(baseUrl)
                    .port(port)
                    .path(url)
                    .build()
                )
                .body(requestObject)
                .header(LOG_TRACE_ID_HEADER, MDC.get(TRACE_ID_KEY))
                .header(LOG_SESSION_ID_HEADER, MDC.get(SESSION_ID_KEY))
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(SickLeaveCertificate[].class)
        );
    }

    @Override
    @PerformanceLogging(eventAction = "get-signing-doctors-for-unit", eventType = MdcLogConstants.EVENT_TYPE_ACCESSED)
    public List<String> getSigningDoctorsForUnit(List<String> units, List<String> certificateTypes) {
        final var url = "/inera-certificate/internalapi/typedcertificate/doctors";
        final var requestObject = getTypedCertificateRequest(units, certificateTypes, null, null, null);

        LOGGER.debug("Getting signing doctors for unit from intygstjansten");

        return buildListResponseFromArray(
            itRestClient
                .post()
                .uri(uriBuilder -> uriBuilder
                    .scheme(scheme)
                    .host(baseUrl)
                    .port(port)
                    .path(url)
                    .build()
                )
                .body(requestObject)
                .header(LOG_TRACE_ID_HEADER, MDC.get(TRACE_ID_KEY))
                .header(LOG_SESSION_ID_HEADER, MDC.get(SESSION_ID_KEY))
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(String[].class)
        );
    }

    @Override
    @PerformanceLogging(eventAction = "get-active-sick-leaves", eventType = MdcLogConstants.EVENT_TYPE_ACCESSED)
    public SickLeavesResponseDTO getActiveSickLeaves(SickLeavesRequestDTO request) {
        final var url = "/inera-certificate/internalapi/sickleave/active";

        LOGGER.debug("Getting active sick leaves from Intygstjansten");

        return itRestClient
            .post()
            .uri(uriBuilder -> uriBuilder
                .scheme(scheme)
                .host(baseUrl)
                .port(port)
                .path(url)
                .build()
            )
            .body(request)
            .header(LOG_TRACE_ID_HEADER, MDC.get(TRACE_ID_KEY))
            .header(LOG_SESSION_ID_HEADER, MDC.get(SESSION_ID_KEY))
            .contentType(MediaType.APPLICATION_JSON)
            .retrieve()
            .body(SickLeavesResponseDTO.class);
    }

    @Override
    @PerformanceLogging(eventAction = "create-reko-status", eventType = MdcLogConstants.EVENT_TYPE_CREATION)
    public RekoStatusDTO createRekoStatus(CreateRekoStatusRequestDTO request) {
        final var url = "/inera-certificate/internalapi/reko";

        LOGGER.debug("Setting reko status to sick leave");

        return itRestClient
            .post()
            .uri(uriBuilder -> uriBuilder
                .scheme(scheme)
                .host(baseUrl)
                .port(port)
                .path(url)
                .build()
            )
            .body(request)
            .header(LOG_TRACE_ID_HEADER, MDC.get(TRACE_ID_KEY))
            .header(LOG_SESSION_ID_HEADER, MDC.get(SESSION_ID_KEY))
            .contentType(MediaType.APPLICATION_JSON)
            .retrieve()
            .body(RekoStatusDTO.class);
    }

    @Override
    @PerformanceLogging(eventAction = "get-reko-status", eventType = MdcLogConstants.EVENT_TYPE_ACCESSED)
    public RekoStatusDTO getRekoStatus(GetRekoStatusRequestDTO request) {
        final var url = "/inera-certificate/internalapi/reko/patient";

        LOGGER.debug("Getting reko status for patient");

        return itRestClient
            .post()
            .uri(uriBuilder -> uriBuilder
                .scheme(scheme)
                .host(baseUrl)
                .port(port)
                .path(url)
                .build()
            )
            .body(request)
            .header(LOG_TRACE_ID_HEADER, MDC.get(TRACE_ID_KEY))
            .header(LOG_SESSION_ID_HEADER, MDC.get(SESSION_ID_KEY))
            .contentType(MediaType.APPLICATION_JSON)
            .retrieve()
            .body(RekoStatusDTO.class);
    }

    @Override
    @PerformanceLogging(eventAction = "get-populated-filers-for-active-sick-leaves", eventType = MdcLogConstants.EVENT_TYPE_ACCESSED)
    public PopulateFiltersResponseDTO getPopulatedFiltersForActiveSickLeaves(PopulateFiltersRequestDTO request) {
        final var url = "/inera-certificate/internalapi/sickleave/filters";

        LOGGER.debug("Getting doctors with active sick leaves from Intygstjansten");

        return itRestClient
            .post()
            .uri(uriBuilder -> uriBuilder
                .scheme(scheme)
                .host(baseUrl)
                .port(port)
                .path(url)
                .build()
            )
            .body(request)
            .header(LOG_TRACE_ID_HEADER, MDC.get(TRACE_ID_KEY))
            .header(LOG_SESSION_ID_HEADER, MDC.get(SESSION_ID_KEY))
            .contentType(MediaType.APPLICATION_JSON)
            .retrieve()
            .body(PopulateFiltersResponseDTO.class);
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
