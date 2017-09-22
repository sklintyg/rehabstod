/*
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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

import se.inera.intyg.rehabstod.service.Urval;
import se.inera.intyg.rehabstod.service.sjukfall.dto.SjukfallSummary;
import se.inera.intyg.rehabstod.web.controller.api.dto.GetSjukfallRequest;
import se.inera.intyg.rehabstod.web.model.SjukfallEnhet;
import se.inera.intyg.rehabstod.web.model.SjukfallPatient;

import java.util.List;

/**
 * Created by eriklupander on 2016-02-01.
 */
public interface SjukfallService {

    /**
     * @see  List<   SjukfallEnhet   > se.inera.intyg.rehabstod.service.sjukfall.SjukfallService.getByUnit
     */
    List<SjukfallEnhet> getSjukfall(String enhetsId, String mottagningsId, String lakareId, Urval urval, GetSjukfallRequest request);

    /**
     * The 'enhetsId' is _always_ the ID of the Vardenhet we want to query IT with regardless of whether the currently
     * selected RehabstodUser#valdVardenhet is a Vardenhet or a Mottagning. 'mottagningsId' is always null if the selected
     * RehabstodUser#valdVardenhet is a Vardenhet.
     *
     * This method will always fetch all ongoing sjukfall for the specified Vardenhet from IT, but if there is a mottagningsId
     * specified, we'll perform filtering on our side so only Sjukfall originating from the specified mottagningsId are
     * included in the response.
     */
    List<SjukfallEnhet> getByUnit(String enhetsId, String mottagningsId, String lakareId, Urval urval, GetSjukfallRequest request);

    List<SjukfallPatient> getByPatient(String enhetsId, String lakareId, Urval urval, GetSjukfallRequest request);

    SjukfallSummary getSummary(String enhetsId, String mottagningsId, String lakareId, Urval urval, GetSjukfallRequest request);

}
