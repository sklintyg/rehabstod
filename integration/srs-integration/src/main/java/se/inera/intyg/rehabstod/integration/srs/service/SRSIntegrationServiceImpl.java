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
package se.inera.intyg.rehabstod.integration.srs.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.inera.intyg.rehabstod.integration.srs.client.SRSClientService;
import se.inera.intyg.rehabstod.integration.srs.model.RiskSignal;

/**
 * Created by eriklupander on 2016-02-01.
 */
@Service
public class SRSIntegrationServiceImpl implements SRSIntegrationService {

    @Autowired
    private SRSClientService srsClientService;

    @Override
    public List<RiskSignal> getRiskPreditionerForIntygsId(List<String> intygsIdn) {
        // Just return empty list if no intygsId's was supplied.
        if (intygsIdn == null || intygsIdn.size() == 0) {
            return new ArrayList<>();
        }

        return srsClientService.getRiskPrediktionForCertificate(intygsIdn)
            .stream()
            .map(rp -> new RiskSignal(rp.getIntygsId(), rp.getRisksignal().getRiskkategori(),
                rp.getRisksignal().getBeskrivning(), rp.getRisksignal().getBerakningstidpunkt()))
            .collect(Collectors.toList());
    }
}
