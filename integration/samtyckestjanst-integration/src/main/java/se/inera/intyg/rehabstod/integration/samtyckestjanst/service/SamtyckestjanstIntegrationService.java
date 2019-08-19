/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.integration.samtyckestjanst.service;

import java.time.LocalDateTime;
import se.inera.intyg.rehabstod.common.model.IntygAccessControlMetaData;
import se.inera.intyg.schemas.contract.Personnummer;
import se.riv.informationsecurity.authorization.consent.v2.ActionType;

/**
 * Created by Magnus Ekstrand on 2018-10-10.
 */
public interface SamtyckestjanstIntegrationService {

    /**
     * Updates the 'has consent' attribute for each {@link IntygAccessControlMetaData} in
     * intygAccessMetaData map.
     *
     * @param patientId - The id of the current patient
     * @param userHsaId - The hsaId of the current user
     * @param currentVardgivarHsaId - The hsaId of the current vardgivare
     * @param currentEnhetsId - The hsaId of the current enehet
     */
    boolean checkForConsent(String patientId,
        String userHsaId,
        String currentVardgivarHsaId,
        String currentEnhetsId);

    /**
     * Service that registers an extended consent for a particular patient, and thus, providing direct
     * access to the patient's information from other healthcare providers according to PDL.
     */
    // CHECKSTYLE:OFF ParameterNumber
    void registerConsent(String vgHsaId,
        String veHsaId,
        Personnummer patientId,
        String userHsaId,
        String representedBy,
        LocalDateTime consentFrom,
        LocalDateTime consentTo,
        ActionType registrationAction);
    // CHECKSTYLE:ON ParameterNumber

}
