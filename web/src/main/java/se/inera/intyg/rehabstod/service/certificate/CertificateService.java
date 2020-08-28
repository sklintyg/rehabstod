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

import java.util.List;
import se.inera.intyg.rehabstod.web.controller.api.dto.GetAGCertificatesForPersonResponse;
import se.inera.intyg.rehabstod.web.controller.api.dto.GetLUCertificatesForCareUnitRequest;
import se.inera.intyg.rehabstod.web.controller.api.dto.GetLUCertificatesForCareUnitResponse;
import se.inera.intyg.rehabstod.web.controller.api.dto.GetLUCertificatesForPersonResponse;

/**
 * Provides front end with specialized certificate data.
 * Data is collected from intygstjansten
 */
public interface CertificateService {

    /**
     * Gets LU certificates for the active unit
     *
     * @param request GetLUCertificatesForCareUnitRequest with internal parameters
     * @return GetLUCertificatesForCareUnitResponse with LU certificates for unit
     */
    GetLUCertificatesForCareUnitResponse getLUCertificatesForCareUnit(GetLUCertificatesForCareUnitRequest request);

    /**
     * Gets LU certificates for the selected person
     *
     * @param personId Id of the person the get certificates for
     * @return GetLUCertificatesForCareUnitResponse with LU certificates for person
     */
    GetLUCertificatesForPersonResponse getLUCertificatesForPerson(String personId);

    /**
     * Gets AG certificates for the selected person
     *
     * @param personId Id of the person the get certificates for
     * @return GetAGCertificatesForCareUnitResponse with LU certificates for person
     */
    GetAGCertificatesForPersonResponse getAGCertificatesForPerson(String personId);

    /**
     * Gets names of the doctors on active unit that has signed certificates
     */
    List<String> getDoctorsForUnit();
}
