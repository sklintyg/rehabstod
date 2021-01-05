/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.integration.srs.client;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import se.inera.intyg.clinicalprocess.healthcond.srs.getdiagnosiscodes.v1.Diagnos;
import se.inera.intyg.clinicalprocess.healthcond.srs.getdiagnosiscodes.v1.GetDiagnosisCodesRequestType;
import se.inera.intyg.clinicalprocess.healthcond.srs.getdiagnosiscodes.v1.GetDiagnosisCodesResponderInterface;
import se.inera.intyg.clinicalprocess.healthcond.srs.getdiagnosiscodes.v1.GetDiagnosisCodesResponseType;
import se.inera.intyg.clinicalprocess.healthcond.srs.getriskpredictionforcertificate.v1.GetRiskPredictionForCertificateRequestType;
import se.inera.intyg.clinicalprocess.healthcond.srs.getriskpredictionforcertificate.v1.GetRiskPredictionForCertificateResponderInterface;
import se.inera.intyg.clinicalprocess.healthcond.srs.getriskpredictionforcertificate.v1.GetRiskPredictionForCertificateResponseType;
import se.inera.intyg.clinicalprocess.healthcond.srs.getriskpredictionforcertificate.v1.RiskPrediktion;
import se.riv.itintegration.monitoring.rivtabp21.v1.PingForConfigurationResponderInterface;
import se.riv.itintegration.monitoring.v1.PingForConfigurationResponseType;
import se.riv.itintegration.monitoring.v1.PingForConfigurationType;

/**
 * Created by eriklupander on 2017-10-31.
 */
@Service
public class SRSClientServiceImpl implements SRSClientService {

    @Autowired
    private GetRiskPredictionForCertificateResponderInterface service;

    @Autowired
    private GetDiagnosisCodesResponderInterface getDiagnosisCodesService;

    @Autowired
    @Qualifier("srsPingForConfigurationWebServiceClient")
    private PingForConfigurationResponderInterface pingService;

    @Value("${it.service.logicalAddress}")
    private String logicalAddress;

    @Override
    public PingForConfigurationResponseType pingForConfiguration() {
        PingForConfigurationType reqType = new PingForConfigurationType();
        reqType.setLogicalAddress(logicalAddress);
        return pingService.pingForConfiguration(logicalAddress, reqType);
    }

    @Override
    public List<RiskPrediktion> getRiskPrediktionForCertificate(List<String> intygsId) {

        GetRiskPredictionForCertificateRequestType reqType = new GetRiskPredictionForCertificateRequestType();
        reqType.getIntygsId().addAll(intygsId);
        GetRiskPredictionForCertificateResponseType responseType = service.getRiskPredictionForCertificate(reqType);
        return responseType.getRiskPrediktioner();
    }

    @Override
    public List<Diagnos> getDiagnosisList() {
        GetDiagnosisCodesRequestType reqType = new GetDiagnosisCodesRequestType();
        GetDiagnosisCodesResponseType response = getDiagnosisCodesService.getDiagnosisCodes(reqType);
        return response.getDiagnos();
    }
}
