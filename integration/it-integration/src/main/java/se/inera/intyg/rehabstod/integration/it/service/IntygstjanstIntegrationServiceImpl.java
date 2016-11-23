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
package se.inera.intyg.rehabstod.integration.it.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import se.inera.intyg.clinicalprocess.healthcond.rehabilitation.listactivesickleavesforcareunit.v1.ListActiveSickLeavesForCareUnitResponseType;
import se.inera.intyg.clinicalprocess.healthcond.rehabilitation.listactivesickleavesforcareunit.v1.ResultCodeEnum;
import se.inera.intyg.rehabstod.integration.it.client.IntygstjanstClientService;
import se.inera.intyg.rehabstod.integration.it.exception.IntygstjanstIntegrationException;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.IntygsData;

/**
 * Created by eriklupander on 2016-02-01.
 */
@Service
public class IntygstjanstIntegrationServiceImpl implements IntygstjanstIntegrationService {

    private static final Logger LOG = LoggerFactory.getLogger(IntygstjanstIntegrationServiceImpl.class);

    @Autowired
    private IntygstjanstClientService intygstjanstClientService;

    @Override
    public List<IntygsData> getIntygsDataForCareUnit(String hsaId) {
        ListActiveSickLeavesForCareUnitResponseType responseType = intygstjanstClientService.getSjukfall(hsaId);
        if (responseType.getResultCode() == ResultCodeEnum.OK) {
            return responseType.getIntygsLista().getIntygsData();
        } else {
            LOG.error("An error occured fetching sick leave certificates. Error type: {}. Error msg: {}", responseType.getResultCode(), responseType.getComment());
            throw new IntygstjanstIntegrationException();
        }

    }
}
