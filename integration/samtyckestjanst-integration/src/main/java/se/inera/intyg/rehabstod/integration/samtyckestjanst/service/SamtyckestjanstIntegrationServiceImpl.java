/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.integration.samtyckestjanst.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.sjukfall.dto.IntygData;
import se.inera.intyg.rehabstod.common.model.IntygAccessControlMetaData;
import se.inera.intyg.rehabstod.integration.samtyckestjanst.client.SamtyckestjanstClientService;

import java.util.List;
import java.util.Map;

/**
 * Created by Magnus Ekstrand on 2018-10-10.
 */
@Service
public class SamtyckestjanstIntegrationServiceImpl implements SamtyckestjanstIntegrationService {

    private static final Logger LOG = LoggerFactory.getLogger(SamtyckestjanstIntegrationServiceImpl.class);

    @Autowired
    private SamtyckestjanstClientService samtyckestjanstClientService;

    @Override
    public void decorateWithConsentStatus(String currentVardgivarHsaId, String currentVardenhetHsaId, String userHsaId, String patientId,
            Map<String, IntygAccessControlMetaData> intygAccessMetaData, List<IntygData> intygLista) {
    }
}
