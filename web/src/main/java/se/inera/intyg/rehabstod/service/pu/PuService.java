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
package se.inera.intyg.rehabstod.service.pu;

import java.util.List;
import se.inera.intyg.infra.certificate.dto.DiagnosedCertificate;
import se.inera.intyg.infra.pu.integration.api.model.PersonSvar;
import se.inera.intyg.infra.sjukfall.dto.IntygData;
import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.web.model.SjukfallEnhet;
import se.inera.intyg.rehabstod.web.model.SjukfallPatient;

public interface PuService {

    String SEKRETESS_SKYDDAD_NAME_PLACEHOLDER = "Skyddad personuppgift";
    String SEKRETESS_SKYDDAD_NAME_UNKNOWN = "Namn okänt";

    /**
     * Removes sjukfall belonging to patients with sekretessmarkering. Ignores PU errors, will not exclude such
     * sjukfall. Removes patient name rather than enriching with PU info since the purpose of this method is for
     * enabling stats aggregation.
     */
    void filterSekretessForSummary(List<SjukfallEnhet> sjukfallList);

    /**
     * Removes intyg from other careUnits belonging to the patient with sekretessmarkering.
     */
    List<IntygData> filterSekretessForPatientHistory(List<IntygData> intygsData);

    boolean shouldFilterSickLeavesOnProtectedPerson(RehabstodUser user);

    /**
     * Filters out sjukfall if the patient has sekretessmarkering and the user doesn't have the requisite privilege.
     *
     * If the PU-service cannot be reached, an Exception must be thrown.
     */
    void enrichSjukfallWithPatientNamesAndFilterSekretess(List<SjukfallEnhet> sjukfallList);

    /**
     * Filters out certificates if the patient has sekretessmarkering and the user doesn't have the requisite privilege.
     *
     * If the PU-service cannot be reached, an Exception must be thrown.
     */
    void enrichDiagnosedCertificateWithPatientNamesAndFilterSekretess(List<DiagnosedCertificate> diagnosedCertificateList);

    /**
     * Filters out sjukfall if the patient has sekretessmarkering and the user doesn't have the requisite privilege.
     *
     * If the PU-service cannot be reached, an Exception must be thrown.
     */
    void enrichSjukfallWithPatientNameAndFilterSekretess(List<SjukfallPatient> patientSjukfall);

    PersonSvar getPersonSvar(String pnr);
}
