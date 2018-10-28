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
package se.inera.intyg.rehabstod.service.sjukfall;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.sjukfall.dto.IntygData;
import se.inera.intyg.infra.sjukfall.dto.IntygParametrar;
import se.inera.intyg.infra.sjukfall.services.SjukfallEngineService;
import se.inera.intyg.rehabstod.common.model.IntygAccessControlMetaData;
import se.inera.intyg.rehabstod.integration.it.service.IntygstjanstIntegrationService;
import se.inera.intyg.rehabstod.integration.samtyckestjanst.service.SamtyckestjanstIntegrationService;
import se.inera.intyg.rehabstod.integration.sparrtjanst.service.SparrtjanstIntegrationService;
import se.inera.intyg.rehabstod.service.Urval;
import se.inera.intyg.rehabstod.service.sjukfall.mappers.IntygstjanstMapper;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.IntygsData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Magnus Ekstrand on 2018-10-25.
 */
@Service("consentService")
public class ConsentServiceImpl implements ConsentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsentServiceImpl.class);

    @Autowired
    private IntygstjanstIntegrationService intygstjanstIntegrationService;

    @Autowired
    private SjukfallEngineService sjukfallEngine;

    @Autowired
    private IntygstjanstMapper intygstjanstMapper;

    @Autowired
    private SparrtjanstIntegrationService sparrtjanstIntegrationService;

    @Autowired
    private SamtyckestjanstIntegrationService samtyckestjanstIntegrationService;

    @Override
    public List<IntygData> getIntygDataForPatient(String currentVardgivarHsaId,
                                                  String enhetsId,
                                                  String lakareId,
                                                  String patientId,
                                                  Urval urval,
                                                  IntygParametrar parameters) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(currentVardgivarHsaId), "currentVardgivarHsaId may not be null or empty");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(enhetsId), "enhetsId may not be null or empty");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(lakareId), "lakareId may not be null or empty");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(patientId), "patientId may not be null or empty");
        Preconditions.checkArgument(urval != null, "urval may not be null");
        Preconditions.checkArgument(parameters != null, "parameters may not be null");

        Map<String, IntygAccessControlMetaData> intygAccessMetaData = new HashMap<>();

        LOGGER.debug("Calling Intygstjänsten - fetching certificate information by patient.");

        List<IntygsData> intygsData =
                intygstjanstIntegrationService.getAllIntygsDataForPatient(patientId, parameters.getMaxAntalDagarSedanSjukfallAvslut());

        List<IntygData> data = intygsData.stream()
                .map(o -> intygstjanstMapper.map(o))
                .collect(Collectors.toList());

        // Create initial map linked to each intyg by intygsId
        data.forEach(intygData -> intygAccessMetaData.put(intygData.getIntygId(),
                new IntygAccessControlMetaData(intygData, currentVardgivarHsaId.equals(intygData.getVardgivareId()))));

        // Decorate intygAccessMetaData with "spärr" info
        sparrtjanstIntegrationService.decorateWithBlockStatus(currentVardgivarHsaId, enhetsId, lakareId, patientId, intygAccessMetaData,
                data);

        // Make a call to the check for consent service
        // Decorate intygAccessMetaData with the consent info
        samtyckestjanstIntegrationService.checkForConsent(patientId, lakareId, intygAccessMetaData);

        return filterByAcessMetaData(data, intygAccessMetaData);
    }

    private List<IntygData> filterByAcessMetaData(List<IntygData> data, Map<String, IntygAccessControlMetaData> intygAccessMetaData) {
        return data.stream()
                .filter(intygData -> shouldInclude(intygAccessMetaData.get(intygData.getIntygId())))
                .collect(Collectors.toList());
    }

    private boolean shouldInclude(IntygAccessControlMetaData intygAccessControlMetaData) {
        // 1. Får inte har spärr
        if (intygAccessControlMetaData.isSparr()) {
            return false;
        }
        // 2. Om Samtycke krävs - inkludera inte (filtrera bort) de som redan har samtycke
        if (intygAccessControlMetaData.isKraverSamtycke() && intygAccessControlMetaData.isHarSamtycke()) {
            return false;
        }
        return true;
    }

}
