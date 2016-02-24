/**
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
 *
 * This file is part of rehabstod (https://github.com/sklintyg/rehabstod).
 *
 * rehabstod is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * rehabstod is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.intyg.rehabstod.service.sjukfall.ruleengine;

import se.inera.intyg.rehabstod.web.controller.api.dto.GetSjukfallRequest;
import se.inera.intyg.rehabstod.web.model.InternalSjukfall;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.IntygsData;

import java.util.List;

/**
 * Created by Magnus Ekstrand on 2016-02-23.
 */
public interface SjukfallEngine {

    /**
     * This method is the entry point when calculating 'sjukfall' using certificate information
     * and request parameters set by the client. Each 'sjukfall' correspomds to one patient.
     *
     * The certificate information is provided by the rehabilitation service contract
     * 'Clinicalprocess Healthcond Rehabilitation'
     *
     * @param intygsData the certificate information (base data)
     * @param requestData client request parameters
     *
     * @return a list with 'sjukfall' wrapped with an internal format.
     *
     */
    List<InternalSjukfall> calculate(List<IntygsData> intygsData, GetSjukfallRequest requestData);

}
