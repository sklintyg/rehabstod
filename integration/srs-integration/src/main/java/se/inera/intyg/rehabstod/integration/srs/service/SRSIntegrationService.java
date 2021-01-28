/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.integration.srs.service;

import java.util.List;
import se.inera.intyg.rehabstod.integration.srs.model.RiskSignal;

/**
 * Created by eriklupander on 2016-02-01.
 */
public interface SRSIntegrationService {

    /**
     * Basically acts as proxy for {@link se.inera.intyg.rehabstod.integration.srs.client.SRSClientService} where
     * the native response type {@link se.inera.intyg.clinicalprocess.healthcond.srs.getriskpredictionforcertificate.v1.RiskPrediktion}
     * is converted into the more user-friendly {@link RiskSignal} type.
     *
     * @param intygsIdn 1..n intygIds to query SRS for risks.
     * @return 0..n RiskSignals.
     */
    List<RiskSignal> getRiskPrediktionerForIntygsId(List<String> intygsIdn);

    /**
     * Fetch the list of diagnosis codes that has SRS support
     * @return a list of ICD-10 diagnosis codes
     */
    List<String> getDiagnosisList();

}
