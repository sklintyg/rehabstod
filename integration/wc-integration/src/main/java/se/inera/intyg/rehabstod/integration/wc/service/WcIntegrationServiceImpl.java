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
package se.inera.intyg.rehabstod.integration.wc.service;

import static se.inera.intyg.clinicalprocess.healthcond.certificate.getcertificateadditions.v1.StatusType.OBESVARAD;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import se.inera.intyg.clinicalprocess.healthcond.certificate.getcertificateadditions.v1.AdditionType;
import se.inera.intyg.clinicalprocess.healthcond.certificate.getcertificateadditions.v1.GetCertificateAdditionsResponseType;
import se.inera.intyg.clinicalprocess.healthcond.certificate.getcertificateadditions.v1.IntygAdditionsType;
import se.inera.intyg.rehabstod.integration.wc.client.WcClientService;
import se.inera.intyg.rehabstod.integration.wc.exception.WcIntegrationException;
import se.riv.clinicalprocess.healthcond.certificate.v3.ResultCodeType;

/**
 * Created by marced on 2019-05-16.
 */
@Service
public class WcIntegrationServiceImpl implements WcIntegrationService {

    private static final Logger LOG = LoggerFactory.getLogger(WcIntegrationServiceImpl.class);

    @Value("${wc.getadditions.max.age.days:90}")
    private int maxDaysOld;

    @Autowired
    private WcClientService wcClientService;

    @Override
    public Map<String, Integer> getCertificateAdditionsForIntyg(List<String> intygId) {
        try {
            // Nothing to do?
            if (intygId == null || intygId.isEmpty()) {
                return new HashMap<>();
            }

            final GetCertificateAdditionsResponseType response = wcClientService.getCertificateAdditions(intygId);
            if (response.getResult() != ResultCodeType.OK) {
                throw new WcIntegrationException(
                        String.format("getCertificateAdditionsForIntyg service failed with resultCode '%s'",
                                response.getResult().name()));

            }

            final Map<String, Integer> result = convertResult(response.getAdditions(), LocalDateTime.now().minusDays(maxDaysOld));
            LOG.debug(
                    "Queried getCertificateAdditionsForIntyg for {} intygids, got {} items back (after converting using no older "
                            + "than {} days, {} are left)",
                    intygId.size(),
                    response.getAdditions().size(), maxDaysOld, result.size());
            return result;
        } catch (Exception e) {
            LOG.error("Error in getCertificateAdditionsForIntyg", e);
            throw new WcIntegrationException("Error in getCertificateAdditionsForIntyg: " + e.getMessage());
        }
    }

    private Map<String, Integer> convertResult(List<IntygAdditionsType> additions, LocalDateTime earliestValidDate) {
        Map<String, Integer> result = new HashMap<>();
        for (IntygAdditionsType intyg : additions) {
            int antalObesvarade = 0;
            for (AdditionType item : intyg.getAddition()) {
                if (OBESVARAD.equals(item.getStatus()) && item.getSkapad().isAfter(earliestValidDate)) {
                    antalObesvarade++;
                }
            }
            result.put(intyg.getIntygsId().getExtension(), antalObesvarade);
        }

        return result;
    }

}
