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
package se.inera.intyg.rehabstod.service.sjukfall;

import java.util.Collection;
import se.inera.intyg.infra.sjukfall.dto.IntygParametrar;
import se.inera.intyg.rehabstod.service.Urval;
import se.inera.intyg.rehabstod.service.sjukfall.dto.SjukfallPatientResponse;

/**
 * Created by eriklupander on 2016-02-01.
 */
public interface SjukfallService {

    /**
     * @param currentVardgivarId The identifier of the current care giver
     * @param enhetsId The care unit identifier
     * @param lakareId The physician's identifier
     * @param patientId The patient's identifier
     * @param vgHsaIds Identifiers of care givers that shall be included in the calculation of the active 'sjukfall'
     * @param veHsaIds Identifiers of care units that shall be included in the calculation of the active 'sjukfall'
     */
    // CHECKSTYLE:OFF ParameterNumber
    SjukfallPatientResponse getByPatient(String currentVardgivarId, String enhetsId, String lakareId,
        String patientId, Urval urval, IntygParametrar parameters,
        Collection<String> vgHsaIds, Collection<String> veHsaIds);

}
