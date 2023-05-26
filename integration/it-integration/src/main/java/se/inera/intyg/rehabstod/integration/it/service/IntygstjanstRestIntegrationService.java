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
import java.util.List;
import se.inera.intyg.infra.certificate.dto.DiagnosedCertificate;
import se.inera.intyg.infra.certificate.dto.SickLeaveCertificate;
import se.inera.intyg.rehabstod.integration.it.dto.*;


/**
 * Connects to Intygstjanstens REST-api to get specialized certificate data
 */
public interface IntygstjanstRestIntegrationService {

    /**
     * List certificates on unit(s) with diagnosis information
     * If no from date is provided it is assumed to be 3 years back in time
     *
     * @param units List of units the certificates are bound to
     * @param certificateTypes The specific type of certificates to get
     * @param fromDate First signing date of selection
     * @param toDate Last signing date of selection
     * @return List of certificates with diagnosis information
     */
    List<DiagnosedCertificate> getDiagnosedCertificatesForCareUnit(List<String> units, List<String> certificateTypes, LocalDate fromDate,
        LocalDate toDate, List<String> doctorIds);

    /**
     * List certificates for person with diagnosis information
     * Date range is assumed to be 3 years back in time from today
     *
     * @param personId Id of the person to get certificates for
     * @param certificateTypes The specific type of certificates to get
     * @param units List of units the certificates are bound to
     * @return List of certificates with diagnosis information
     */
    List<DiagnosedCertificate> getDiagnosedCertificatesForPerson(String personId, List<String> certificateTypes, List<String> units);

    /**
     * List certificates for person with diagnosis information
     * If no from date is provided it is assumed to be 3 years back in time
     *
     * @param personId Id of the person to get certificates for
     * @param certificateTypes The specific type of certificates to get
     * @param fromDate First signing date of selection
     * @param toDate Last signing date of selection
     * @param units List of units the certificates are bound to
     * @return List of certificates with diagnosis information
     */
    List<DiagnosedCertificate> getDiagnosedCertificatesForPerson(String personId, List<String> certificateTypes, LocalDate fromDate,
        LocalDate toDate, List<String> units);

    /**
     * List certificates for person with sickleave information
     * Date range is assumed to be 3 years back in time from today
     *
     * @param personId Id of the person to get certificates for
     * @param certificateTypes The specific type of certificates to get
     * @param units List of units the certificates are bound to
     * @return List of certificates with sickleave information
     */
    List<SickLeaveCertificate> getSickLeaveCertificatesForPerson(String personId, List<String> certificateTypes, List<String> units);

    /**
     * List certificates for person with sickleave information
     * If no from date is provided it is assumed to be 3 years back in time
     *
     * @param personId Id of the person to get certificates for
     * @param certificateTypes The specific type of certificates to get
     * @param fromDate First signing date of selection
     * @param toDate Last signing date of selection
     * @param units List of units the certificates are bound to
     * @return List of certificates with sickleave information
     */
    List<SickLeaveCertificate> getSickLeaveCertificatesForPerson(String personId, List<String> certificateTypes, LocalDate fromDate,
        LocalDate toDate, List<String> units);

    /**
     * List doctors that have signed certificates on unit(s)
     * Date range is assumed to be 3 years back in time from today
     *
     * @param units List of units the certificates are bound to
     * @param certificateTypes The specific type of certificates to get
     * @return List of certificates with diagnosis information
     */
    List<String> getSigningDoctorsForUnit(List<String> units, List<String> certificateTypes);

    /**
     * List active sick leaves for unit.
     *
     * @param request Request including parameters to perform search
     */
    SickLeavesResponseDTO getActiveSickLeaves(SickLeavesRequestDTO request);

    /**
     * List doctors which have signed at least one active sick leaves for unit.
     *
     * @param request Request including parameters to perform search
     */
    PopulateFiltersResponseDTO getPopulatedFiltersForActiveSickLeaves(PopulateFiltersRequestDTO request);

    RekoStatusDTO createRekoStatus(CreateRekoStatusRequestDTO request);
}
