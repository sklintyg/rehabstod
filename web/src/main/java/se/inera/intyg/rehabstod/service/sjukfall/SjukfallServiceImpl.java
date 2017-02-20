/**
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.service.sjukfall;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.inera.intyg.rehabstod.integration.it.service.IntygstjanstIntegrationService;
import se.inera.intyg.rehabstod.service.Urval;
import se.inera.intyg.rehabstod.service.monitoring.MonitoringLogService;
import se.inera.intyg.rehabstod.service.sjukfall.dto.SjukfallSummary;
import se.inera.intyg.rehabstod.service.sjukfall.ruleengine.SjukfallEngineImpl;
import se.inera.intyg.rehabstod.service.sjukfall.ruleengine.statistics.StatisticsCalculator;
import se.inera.intyg.rehabstod.web.controller.api.dto.GetSjukfallRequest;
import se.inera.intyg.rehabstod.web.model.InternalSjukfall;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.IntygsData;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by eriklupander on 2016-02-01.
 */

@Service
public class SjukfallServiceImpl implements SjukfallService {

    private static final Logger LOG = LoggerFactory.getLogger(SjukfallServiceImpl.class);

    @Autowired
    private IntygstjanstIntegrationService intygstjanstIntegrationService;

    @Autowired
    private SjukfallEngineImpl sjukfallEngine;

    @Autowired
    private StatisticsCalculator statisticsCalculator;

    @Autowired
    private MonitoringLogService monitoringLogService;

    @Override
    public List<InternalSjukfall> getSjukfall(String enhetsId, String mottagningsId, String hsaId, Urval urval,
            GetSjukfallRequest request) {
        List<InternalSjukfall> sjukfallList = getFilteredSjukfallList(enhetsId, mottagningsId, hsaId, urval, request);
        if (sjukfallList != null) {
            monitoringLogService.logUserViewedSjukfall(hsaId, sjukfallList.size(), resolveIdOfActualUnit(enhetsId, mottagningsId));
        }
        return sjukfallList;
    }

    /**
     * Since we always use the vardenhetsId to query intygstjänsten (even when the
     * selected unit is a mottagning), we must take care to use the mottagningsId
     * when logging when present). Note that this does NOT apply to PDL logging, only monitoring logging.
     *
     * For PDL logging we always specify the enhetsId, never a mottagningsId.
     */
    private String resolveIdOfActualUnit(String enhetsId, String mottagningsId) {
        return mottagningsId != null ? mottagningsId : enhetsId;
    }

    @Override
    public SjukfallSummary getSummary(String enhetsId, String mottagningsId, String hsaId, Urval urval, GetSjukfallRequest request) {
        return statisticsCalculator.getSjukfallSummary(getFilteredSjukfallList(enhetsId, mottagningsId, hsaId, urval, request));
    }

    private List<InternalSjukfall> getFilteredSjukfallList(String enhetsId, String mottagningsId, String hsaId, Urval urval,
            GetSjukfallRequest request) {

        if (urval == null) {
            throw new IllegalArgumentException("Urval must be given to be able to get sjukfall");
        }

        LOG.debug("Calling Intygstjänsten - fetching certificate information.");
        List<IntygsData> intygsData = intygstjanstIntegrationService.getIntygsDataForCareUnit(enhetsId);

        LOG.debug("Calling the calculation engine - calculating and assembling 'sjukfall'.");
        List<InternalSjukfall> internalSjukfall = sjukfallEngine.calculate(intygsData, request);

        if (urval.equals(Urval.ISSUED_BY_ME)) {
            LOG.debug("Filtering response - a doctor shall only see patients 'sjukfall' he/she has issued certificates.");
            internalSjukfall = internalSjukfall.stream()
                    .filter(o -> o.getSjukfall().getLakare().getHsaId().equals(hsaId))
                    .collect(Collectors.toList());
        }

        if (mottagningsId != null) {
            LOG.debug("Filtering response - query for mottagning, only including 'sjukfall' with active intyg on specified mottagning");
            internalSjukfall = internalSjukfall.stream()
                    .filter(o -> o.getVardEnhetId().equals(mottagningsId))
                    .collect(Collectors.toList());
        }
        return internalSjukfall;
    }
}
