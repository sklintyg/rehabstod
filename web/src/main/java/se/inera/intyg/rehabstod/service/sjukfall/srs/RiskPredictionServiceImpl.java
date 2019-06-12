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
package se.inera.intyg.rehabstod.service.sjukfall.srs;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.security.common.model.Feature;
import se.inera.intyg.rehabstod.auth.authorities.AuthoritiesConstants;
import se.inera.intyg.rehabstod.integration.srs.model.RiskSignal;
import se.inera.intyg.rehabstod.integration.srs.service.SRSIntegrationService;
import se.inera.intyg.rehabstod.service.exceptions.SRSServiceException;
import se.inera.intyg.rehabstod.service.user.UserService;
import se.inera.intyg.rehabstod.web.model.PatientData;
import se.inera.intyg.rehabstod.web.model.SjukfallEnhet;
import se.inera.intyg.rehabstod.web.model.SjukfallPatient;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by eriklupander on 2017-11-01.
 */
@Service
public class RiskPredictionServiceImpl implements RiskPredictionService {

    private static final Logger LOG = LoggerFactory.getLogger(RiskPredictionServiceImpl.class);
    protected static final int MAX_AGE_DAYS = 90;

    @Autowired
    private UserService userService;

    @Autowired
    private SRSIntegrationService srsIntegrationService;

    @Override
    public void updateWithRiskPredictions(List<SjukfallEnhet> rehabstodSjukfall) {
        if (!isSrsFeatureActive()) {
            return;
        }

        if (rehabstodSjukfall == null || rehabstodSjukfall.size() == 0) {
            return;
        }
        List<String> intygIds = rehabstodSjukfall
                .stream()
                .flatMap((sfe) -> CollectionUtils.emptyIfNull(sfe.getIntygLista()).stream())
                .collect(Collectors.toList());

        // Använd endast prediktioner/risksignaler som är mindre än 90 dagar gamla och har ett relevant värde
        List<RiskSignal> prediktioner = CollectionUtils.emptyIfNull(getRiskSignals(intygIds)).stream()
                .filter((p) -> p.getBerakningstidpunkt().isAfter(LocalDateTime.now().minus(MAX_AGE_DAYS, ChronoUnit.DAYS)))
                .filter((p) -> p.getRiskKategori() >= 1)
                .collect(Collectors.toList());

        LOG.info("Successfully queried SRS for risk signals for {} sjukfall, got {} results.", intygIds.size(), prediktioner.size());

        // För varje sjukfall på enheten
        for (SjukfallEnhet sjukfallEnhet : rehabstodSjukfall) {
            // Kolla om det finns risksignaler som ingår i sjukfallet
            // och använd i så fall den senaste
            Optional<RiskSignal> riskSignal = prediktioner.stream()
                    .filter((p) -> sjukfallEnhet.getIntygLista().stream()
                            .filter((sjukfallsIntyg) -> sjukfallsIntyg.equals(p.getIntygsId())).findAny().isPresent()
                    )
                    .max(Comparator.comparing(RiskSignal::getBerakningstidpunkt));
            if (riskSignal.isPresent()) {
                sjukfallEnhet.setRiskSignal(riskSignal.get());
            }
        }
    }

    @Override
    public void updateSjukfallPatientListWithRiskPredictions(List<SjukfallPatient> rehabstodSjukfall) {
        if (!isSrsFeatureActive()) {
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

            // Do not add risk signals if response was 1, that means SRS had no prediction at all for the intygsId.
            if (riskSignal.getRiskKategori() < 1
                    || riskSignal.getBerakningstidpunkt().isBefore(LocalDateTime.now().minus(MAX_AGE_DAYS, ChronoUnit.DAYS))) {
                continue;
            }

            for (SjukfallPatient sjukfallPatient : rehabstodSjukfall) {
                for (PatientData patientData : sjukfallPatient.getIntyg()) {
                    if (patientData.getIntygsId().equals(riskSignal.getIntygsId())) {
                        patientData.setRiskSignal(riskSignal);
                    }
                }
            }
        }
    }

    /**
     * Either the global feature "srs" or pilot feature needs to be active.
     */
    private boolean isSrsFeatureActive() {
        return Optional.ofNullable(userService.getUser().getFeatures())
                .map(features -> features.get(AuthoritiesConstants.FEATURE_SRS))
                .map(Feature::getGlobal)
                .orElse(false);
    }

    private List<RiskSignal> getRiskSignals(List<String> intygIds) {
        try {
            return srsIntegrationService.getRiskPreditionerForIntygsId(intygIds);
        } catch (Exception e) {
            throw new SRSServiceException(e.getMessage());
        }
    }
}
