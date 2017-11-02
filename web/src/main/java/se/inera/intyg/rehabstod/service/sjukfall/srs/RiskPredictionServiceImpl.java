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
package se.inera.intyg.rehabstod.service.sjukfall.srs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.inera.intyg.rehabstod.integration.srs.model.RiskSignal;
import se.inera.intyg.rehabstod.integration.srs.service.SRSIntegrationService;
import se.inera.intyg.rehabstod.service.exceptions.SRSServiceException;
import se.inera.intyg.rehabstod.service.feature.RehabstodFeature;
import se.inera.intyg.rehabstod.service.feature.RehabstodFeatureServiceImpl;
import se.inera.intyg.rehabstod.web.model.PatientData;
import se.inera.intyg.rehabstod.web.model.SjukfallEnhet;
import se.inera.intyg.rehabstod.web.model.SjukfallPatient;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by eriklupander on 2017-11-01.
 */
@Service
public class RiskPredictionServiceImpl implements RiskPredictionService {

    private static final Logger LOG = LoggerFactory.getLogger(RiskPredictionServiceImpl.class);

    @Autowired
    private RehabstodFeatureServiceImpl featureService;

    @Autowired
    private SRSIntegrationService srsIntegrationService;

    @Override
    public void updateWithRiskPredictions(List<SjukfallEnhet> rehabstodSjukfall) {
        if (!featureService.isFeatureActive(RehabstodFeature.SRS)) {
            return;
        }

        if (rehabstodSjukfall == null || rehabstodSjukfall.size() == 0) {
            return;
        }

        List<String> intygIds = rehabstodSjukfall.stream().map(SjukfallEnhet::getAktivIntygsId).collect(Collectors.toList());
        List<RiskSignal> prediktioner = getRiskSignals(intygIds);

        LOG.info("Successfully queried SRS for risk signals for {} sjukfall, got {} results.", intygIds.size(), prediktioner.size());

        for (RiskSignal riskSignal : prediktioner) {
            for (SjukfallEnhet sjukfallEnhet : rehabstodSjukfall) {
                if (riskSignal.getIntygsId().equals(sjukfallEnhet.getAktivIntygsId())) {
                    sjukfallEnhet.setRiskSignal(riskSignal);
                }
            }
        }
    }

    @Override
    public void updateSjukfallPatientListWithRiskPredictions(List<SjukfallPatient> rehabstodSjukfall) {
        if (!featureService.isFeatureActive(RehabstodFeature.SRS)) {
            return;
        }

        if (rehabstodSjukfall == null || rehabstodSjukfall.size() == 0) {
            return;
        }

        List<String> intygIds = rehabstodSjukfall.stream()
                .flatMap(patientData -> patientData.getIntyg().stream())
                .map(intyg -> intyg.getIntygsId())
                .collect(Collectors.toList());
        List<RiskSignal> prediktioner = getRiskSignals(intygIds);

        LOG.info("Successfully queried SRS for risk signals for {} sjukfall, got {} results.", intygIds.size(), prediktioner.size());

        // Ugly iteration over result and patientdata to apply risk signals on original datastructure.
        for (RiskSignal riskSignal : prediktioner) {
            for (SjukfallPatient sjukfallPatient : rehabstodSjukfall) {
                for (PatientData patientData : sjukfallPatient.getIntyg()) {
                    if (patientData.getIntygsId().equals(riskSignal.getIntygsId())) {
                        patientData.setRiskSignal(riskSignal);
                    }
                }
            }
        }
    }

    private List<RiskSignal> getRiskSignals(List<String> intygIds) {
        List<RiskSignal> prediktioner;
        try {
            prediktioner = srsIntegrationService.getRiskPreditionerForIntygsId(intygIds);
        } catch (Exception e) {
            throw new SRSServiceException(e.getMessage());
        }
        return prediktioner;
    }
}
