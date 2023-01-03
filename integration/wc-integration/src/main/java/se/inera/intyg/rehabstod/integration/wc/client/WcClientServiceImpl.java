/*
 * Copyright (C) 2023 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.integration.wc.client;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import se.inera.clinicalprocess.healthcond.certificate.types.v3.IntygId;
import se.inera.intyg.clinicalprocess.healthcond.certificate.getcertificateadditions.v1.GetCertificateAdditionsResponderInterface;
import se.inera.intyg.clinicalprocess.healthcond.certificate.getcertificateadditions.v1.GetCertificateAdditionsResponseType;
import se.inera.intyg.clinicalprocess.healthcond.certificate.getcertificateadditions.v1.GetCertificateAdditionsType;
import se.inera.intyg.infra.monitoring.annotation.PrometheusTimeMethod;

/**
 * Created by marced 2019-05-17.
 */
@Service
public class WcClientServiceImpl implements WcClientService {

    @Autowired
    private GetCertificateAdditionsResponderInterface service;

    @Value("${wc.service.logicalAddress}")
    private String logicalAddress;

    @Override
    @PrometheusTimeMethod
    public GetCertificateAdditionsResponseType getCertificateAdditions(List<String> intygsIdn) {
        GetCertificateAdditionsType parameters = new GetCertificateAdditionsType();
        // convert to JAXB params
        parameters.getIntygsId().addAll(intygsIdn.stream().map(id -> buildIntygsId(id)).collect(Collectors.toList()));
        return service.getCertificateAdditions(logicalAddress, parameters);
    }

    private IntygId buildIntygsId(String id) {
        IntygId intygId = new IntygId();
        intygId.setRoot("");
        intygId.setExtension(id);
        return intygId;
    }
}
