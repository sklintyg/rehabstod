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
package se.inera.intyg.rehabstod.service.sjukfall;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import se.inera.intyg.rehabstod.integration.it.service.IntygstjanstIntegrationService;
import se.inera.intyg.rehabstod.service.Urval;
import se.inera.intyg.rehabstod.service.sjukfall.dto.SjukfallSummary;
import se.inera.intyg.rehabstod.service.sjukfall.ruleengine.SjukfallEngineImpl;
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

    @Override
    public List<InternalSjukfall> getSjukfall(String enhetsId, String hsaId, Urval urval, GetSjukfallRequest request) {
        // 0. Sanity check
        if (urval == null) {
            throw new IllegalArgumentException("Urval must be given to be able to get sjukfall");
        }

        // 1; check the cache for data

        // 2; fetch data from backend if cache was invalidated
        LOG.debug("Calling Intygstjänsten - fetching certificate information.");
        List<IntygsData> intygsData = intygstjanstIntegrationService.getIntygsDataForCareUnit(enhetsId);

        // 3; Calculate sjukfall
        LOG.debug("Calling the calculation engine - calculating and assembling 'sjukfall'.");
        List<InternalSjukfall> internalSjukfall = sjukfallEngine.calculate(intygsData, request);

        // 4; update cache if necessary

        // 5; filter response
        LOG.debug("Filtering response - a doctor shall only see patients 'sjukfall' he/she has issued certificates.");
        if (urval.equals(Urval.ISSUED_BY_ME)) {
            internalSjukfall = internalSjukfall.stream()
                    .filter(o -> o.getSjukfall().getLakare().getHsaId().equals(hsaId))
                    .collect(Collectors.toList());
        }

        return internalSjukfall;
    }

    @Override
    public SjukfallSummary getSummary(String enhetsId) {

        List<IntygsData> intygsData = intygstjanstIntegrationService.getIntygsDataForCareUnit(enhetsId);

        List<String> personNummer = intygsData.stream().map(e -> e.getPatient().getPersonId().getExtension()).distinct()
                .collect(Collectors.toList());

        int total = personNummer.size();
        // CHECKSTYLE:OFF MagicNumber
        int menTotal = (int) personNummer.stream().filter(p -> p.substring(11, 12).matches("^\\d*[13579]$")).count();
        int womenTotal = total - menTotal;

        double men = 0;
        double women = 0;

        if (total > 0) {
            men = (menTotal * 1.0 / total) * 100;
            women = (womenTotal * 1.0 / total) * 100;
        }
        // CHECKSTYLE:ON MagicNumber

        return new SjukfallSummary(total, men, women);
    }
}
