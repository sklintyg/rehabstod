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
package se.inera.intyg.rehabstod.integration.it.service;

// CHECKSTYLE:OFF LineLength

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.inera.intyg.clinicalprocess.healthcond.rehabilitation.listactivesickleavesforcareunit.v1.ListActiveSickLeavesForCareUnitResponseType;
import se.inera.intyg.clinicalprocess.healthcond.rehabilitation.listsickleavesforperson.v1.ListSickLeavesForPersonResponseType;
import se.inera.intyg.infra.monitoring.annotation.PrometheusTimeMethod;
import se.inera.intyg.rehabstod.common.util.StringUtil;
import se.inera.intyg.rehabstod.integration.it.client.IntygstjanstClientService;
import se.inera.intyg.rehabstod.integration.it.exception.IntygstjanstIntegrationException;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.IntygsData;

// CHECKSTYLE:ON LineLength

/**
 * Created by eriklupander on 2016-02-01.
 */
@Service
public class IntygstjanstIntegrationServiceImpl implements IntygstjanstIntegrationService {

    private static final Logger LOG = LoggerFactory.getLogger(IntygstjanstIntegrationServiceImpl.class);

    @Autowired
    private IntygstjanstClientService intygstjanstClientService;

    @Override
    @PrometheusTimeMethod
    public List<IntygsData> getIntygsDataForCareUnit(String unitId, int maxAntalDagarSedanSjukfallAvslut) {
        verifyMandatoryParameter("unitId", unitId);
        String errorMessage = "An error occured fetching sick leave certificates for healthcare unit. Error type: {}. Error msg: {}";
        return getIntygsData(intygstjanstClientService.getSjukfallForUnit(unitId, maxAntalDagarSedanSjukfallAvslut), errorMessage);
    }

    @Override
    @PrometheusTimeMethod
    public List<IntygsData> getIntygsDataForCareUnitAndPatient(String unitId, String patientId, int maxAntalDagarSedanSjukfallAvslut) {
        verifyMandatoryParameter("unitId", unitId);
        verifyMandatoryParameter("patientId", patientId);

        String errorMessage = "An error occured fetching sick leave certificates for patient on a care unit. Error type: {}. Error msg: {}";
        return getIntygsData(intygstjanstClientService.getSjukfallForUnitAndPatient(unitId, patientId, maxAntalDagarSedanSjukfallAvslut),
            errorMessage);
    }

    @Override
    @PrometheusTimeMethod
    public List<IntygsData> getAllIntygsDataForPatient(String patientId) {
        verifyMandatoryParameter("patientId", patientId);

        String errorMessage = "An error occured fetching all sick leave certificates for a patient. Error type: {}. Error msg: {}";
        return getIntygsData(intygstjanstClientService.getAllSjukfallForPatient(patientId), errorMessage);
    }

    private List<IntygsData> getIntygsData(ListActiveSickLeavesForCareUnitResponseType responseType, String errorMessage) {
        if (responseType == null) {
            LOG.error(errorMessage);
            throw new IntygstjanstIntegrationException();
        }

        if (responseType.getResultCode()
            != se.inera.intyg.clinicalprocess.healthcond.rehabilitation.listactivesickleavesforcareunit.v1.ResultCodeEnum.OK) {
            LOG.error(errorMessage, responseType.getResultCode(), responseType.getComment());
            throw new IntygstjanstIntegrationException();
        }

        return responseType.getIntygsLista().getIntygsData();
    }

    private List<IntygsData> getIntygsData(ListSickLeavesForPersonResponseType responseType, String errorMessage) {
        if (responseType == null) {
            LOG.error(errorMessage);
            throw new IntygstjanstIntegrationException();
        }

        if (responseType.getResult().getResultCode()
            != se.inera.intyg.clinicalprocess.healthcond.rehabilitation.listsickleavesforperson.v1.ResultCodeEnum.OK) {
            LOG.error(errorMessage, responseType.getResult().getResultCode(), responseType.getResult().getResultMessage());
            throw new IntygstjanstIntegrationException();
        }

        return responseType.getIntygsLista().getIntygsData();
    }

    private void verifyMandatoryParameter(String name, String value) {
        if (StringUtil.isNullOrEmpty(value)) {
            throw new IllegalArgumentException("Parameter '" + name + "' must be non-empty string");
        }
    }
}
