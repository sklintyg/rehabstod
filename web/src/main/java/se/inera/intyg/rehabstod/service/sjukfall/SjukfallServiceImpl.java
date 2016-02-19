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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.inera.intyg.rehabstod.integration.it.service.IntygstjanstIntegrationService;
import se.inera.intyg.rehabstod.service.Urval;
import se.inera.intyg.rehabstod.service.sjukfall.dto.SjukfallSummary;
import se.inera.intyg.rehabstod.service.sjukfall.ruleengine.SjukfallEngine;
import se.inera.intyg.rehabstod.web.controller.api.dto.GetSjukfallRequest;
import se.inera.intyg.rehabstod.web.model.Sjukfall;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.IntygsData;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by eriklupander on 2016-02-01.
 */

@Service
public class SjukfallServiceImpl implements SjukfallService {

    @Autowired
    private IntygstjanstIntegrationService intygstjanstIntegrationService;

    @Autowired
    private SjukfallEngine sjukfallEngine;

    @Override
    public List<Sjukfall> getSjukfall(String enhetsId, String hsaId, Urval urval, GetSjukfallRequest request) {

        // 1; check the cache for data

        // 2; fetch data from backend if cache was invalidated
        List<IntygsData> intygsData = intygstjanstIntegrationService.getIntygsDataForCareUnit(enhetsId);


        // 2.1; Calculate sjukfall
        List<Sjukfall> sjukfall = sjukfallEngine.calculate(intygsData, hsaId, urval, request);

        // 2.2; update cache if necessary


        return sjukfall;
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
