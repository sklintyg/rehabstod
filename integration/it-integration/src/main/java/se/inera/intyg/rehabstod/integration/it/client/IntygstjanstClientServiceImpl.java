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
package se.inera.intyg.rehabstod.integration.it.client;

// CHECKSTYLE:OFF LineLength

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import se.inera.intyg.clinicalprocess.healthcond.rehabilitation.listactivesickleavesforcareunit.v1.ListActiveSickLeavesForCareUnitResponderInterface;
import se.inera.intyg.clinicalprocess.healthcond.rehabilitation.listactivesickleavesforcareunit.v1.ListActiveSickLeavesForCareUnitResponseType;
import se.inera.intyg.clinicalprocess.healthcond.rehabilitation.listactivesickleavesforcareunit.v1.ListActiveSickLeavesForCareUnitType;
import se.inera.intyg.infra.monitoring.annotation.PrometheusTimeMethod;
import se.riv.clinicalprocess.healthcond.certificate.types.v2.HsaId;
import se.riv.clinicalprocess.healthcond.certificate.types.v2.PersonId;
import se.riv.itintegration.monitoring.rivtabp21.v1.PingForConfigurationResponderInterface;
import se.riv.itintegration.monitoring.v1.PingForConfigurationResponseType;
import se.riv.itintegration.monitoring.v1.PingForConfigurationType;

// CHECKSTYLE:ON LineLength

/**
 * Created by eriklupander on 2016-01-29.
 */
@Service
public class IntygstjanstClientServiceImpl implements IntygstjanstClientService {

    @Autowired
    private ListActiveSickLeavesForCareUnitResponderInterface service;

    @Autowired
    @Qualifier("itPingForConfigurationWebServiceClient")
    private PingForConfigurationResponderInterface pingService;

    @Value("${it.service.logicalAddress}")
    private String logicalAddress;

    @Override
    @PrometheusTimeMethod
    public ListActiveSickLeavesForCareUnitResponseType getSjukfallForUnit(String unitId, int maxAntalDagarSedanSjukfallAvslut) {
        ListActiveSickLeavesForCareUnitType params = new ListActiveSickLeavesForCareUnitType();

        HsaId hsaId = new HsaId();
        hsaId.setExtension(unitId);
        params.setEnhetsId(hsaId);
        params.setMaxDagarSedanAvslut(maxAntalDagarSedanSjukfallAvslut);

        return service.listActiveSickLeavesForCareUnit(logicalAddress, params);
    }

    @Override
    @PrometheusTimeMethod
    public ListActiveSickLeavesForCareUnitResponseType getSjukfallForPatient(String unitId, String patientId,
            int maxAntalDagarSedanSjukfallAvslut) {
        ListActiveSickLeavesForCareUnitType params = new ListActiveSickLeavesForCareUnitType();

        PersonId pId = new PersonId();
        pId.setExtension(patientId);
        params.setPersonId(pId);

        HsaId hsaId = new HsaId();
        hsaId.setExtension(unitId);
        params.setEnhetsId(hsaId);
        params.setMaxDagarSedanAvslut(maxAntalDagarSedanSjukfallAvslut);

        return service.listActiveSickLeavesForCareUnit(logicalAddress, params);
    }

    @Override
    @PrometheusTimeMethod
    public PingForConfigurationResponseType pingForConfiguration() {
        PingForConfigurationType reqType = new PingForConfigurationType();
        reqType.setLogicalAddress(logicalAddress);
        return pingService.pingForConfiguration(logicalAddress, reqType);
    }

}
