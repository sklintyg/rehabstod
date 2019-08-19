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
package se.inera.intyg.rehabstod.integration.sparrtjanst.service;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.sjukfall.dto.IntygData;
import se.inera.intyg.rehabstod.common.model.IntygAccessControlMetaData;
import se.inera.intyg.rehabstod.integration.sparrtjanst.client.SparrtjanstClientService;
import se.inera.intyg.rehabstod.integration.sparrtjanst.exception.SparrtjanstIntegrationException;
import se.riv.informationsecurity.authorization.blocking.CheckBlocksResponder.v4.CheckBlocksResponseType;
import se.riv.informationsecurity.authorization.blocking.v4.CheckBlocksResultType;
import se.riv.informationsecurity.authorization.blocking.v4.CheckResultType;
import se.riv.informationsecurity.authorization.blocking.v4.CheckStatusType;
import se.riv.informationsecurity.authorization.blocking.v4.ResultCodeType;

/**
 * Created by marced on 2018-09-28.
 */
@Service
public class SparrtjanstIntegrationServiceImpl implements SparrtjanstIntegrationService {

    private static final Logger LOG = LoggerFactory.getLogger(SparrtjanstIntegrationServiceImpl.class);

    @Autowired
    private SparrtjanstClientService sparrtjanstClientService;

    @Override
    public void decorateWithBlockStatus(String currentVardgivarHsaId, String currentVardenhetHsaId, String userHsaId, String patientId,
        Map<String, IntygAccessControlMetaData> intygAccessMetaData, List<IntygData> intygOnOtherUnitsOnly) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(currentVardgivarHsaId), "vgHsaId may not be null or empty");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(currentVardenhetHsaId), "veHsaId may not be null or empty");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(userHsaId), "userHsaId may not be null or empty");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(patientId), "patientId may not be null or empty");

        // Nothing to do - just return
        if (intygOnOtherUnitsOnly.isEmpty()) {
            return;
        }

        final CheckBlocksResponseType checkBlocksForPersonResponse = sparrtjanstClientService.getCheckBlocks(currentVardgivarHsaId,
            currentVardenhetHsaId, userHsaId, patientId, intygOnOtherUnitsOnly);

        final CheckBlocksResultType response = checkBlocksForPersonResponse.getCheckBlocksResult();
        // OK   = Response is good
        // INFO = Some of the requested information has a validation error,
        //        but a response should still have been provided
        if (response.getResult().getResultCode() != ResultCodeType.OK) {
            if (response.getResult().getResultCode() == ResultCodeType.INFO) {
                LOG.warn("Blocking service responded with result code INFO and resultText '{}' - "
                    + "a partially valid response is still expected.", response.getResult().getResultText());
            } else {
                throw new SparrtjanstIntegrationException(
                    String.format("Blocking service failed with resultCode '%s' and resultText '%s'",
                        response.getResult().getResultCode(),
                        response.getResult().getResultText()));
            }
        }

        updateBlockStatuses(intygAccessMetaData, intygOnOtherUnitsOnly, response.getCheckResults());
    }

    private void updateBlockStatuses(Map<String, IntygAccessControlMetaData> intygAccessMetaData, List<IntygData> queryList,
        List<CheckResultType> responseList) {

        // Sanity check before processing result - we must have gotten exactly the same number of responses as in list.
        if (responseList.size() != queryList.size()) {
            throw new SparrtjanstIntegrationException(
                "Fatal error - expected " + queryList.size() + " results - got " + responseList.size());
        }

        for (int i = 0; i < queryList.size(); i++) {
            final IntygData intygData = queryList.get(i);

            // get the corresponding access metadata item
            final IntygAccessControlMetaData metadata = intygAccessMetaData.get(intygData.getIntygId());

            // Contract doesn't specify that order of element in xml is sequential,
            // so we use rowNumber to match request/response.
            final int rowNumber = i;
            final CheckResultType checkResultType = responseList
                .stream()
                .filter(cr -> cr.getRowNumber() == rowNumber).findFirst()
                .orElseThrow(() -> new SparrtjanstIntegrationException("Found no result for source row " + rowNumber));

            // We consider all CheckStatusType's but CheckStatusType.OK as blocked.
            metadata.setSparr(!checkResultType.getStatus().equals(CheckStatusType.OK));
        }
    }

}
