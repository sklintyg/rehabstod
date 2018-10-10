/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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

import se.inera.intyg.infra.sjukfall.dto.IntygData;
import se.inera.intyg.rehabstod.common.model.IntygAccessControlMetaData;

import java.util.List;
import java.util.Map;

/**
 * Created by Magnus Ekstrand on 2018-10-10.
 */
public interface SamtyckestjanstIntegrationService {

    /**
     * Updates the corresponding {@link IntygAccessControlMetaData} sparr attributes for each {@link IntygData} in
     * intygLista.
     *
     * @param currentVardgivarHsaId
     *            - The hsaId of the current vardgivare
     * @param currentVardenhetHsaId
     *            - The hsaId of the current varenhet
     * @param userHsaId
     *            - The hsaId of the current user
     * @param patientId
     *            - The id of the current patient
     * @param intygAccessMetaData
     *            -Map containing access control metadata
     * @param intygLista
     *            -Map containing the intyg to process
     */
    void decorateWithConsentStatus(String currentVardgivarHsaId, String currentVardenhetHsaId, String userHsaId, String patientId,
            Map<String, IntygAccessControlMetaData> intygAccessMetaData, List<IntygData> intygLista);

}
