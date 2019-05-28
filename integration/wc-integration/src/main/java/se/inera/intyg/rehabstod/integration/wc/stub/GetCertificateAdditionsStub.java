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
package se.inera.intyg.rehabstod.integration.wc.stub;

// CHECKSTYLE:OFF LineLength

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import se.inera.intyg.clinicalprocess.healthcond.certificate.getcertificateadditions.v1.GetCertificateAdditionsResponderInterface;
import se.inera.intyg.clinicalprocess.healthcond.certificate.getcertificateadditions.v1.GetCertificateAdditionsResponseType;
import se.inera.intyg.clinicalprocess.healthcond.certificate.getcertificateadditions.v1.GetCertificateAdditionsType;
import se.inera.intyg.clinicalprocess.healthcond.certificate.getcertificateadditions.v1.IntygAdditionsType;
import se.riv.clinicalprocess.healthcond.certificate.types.v3.IntygId;
import se.riv.clinicalprocess.healthcond.certificate.v3.ResultCodeType;

// CHECKSTYLE:ON LineLength

/**
 * Created by marced on 2019-05-17.
 */
@Service
@Profile({ "rhs-wc-stub" })
public class GetCertificateAdditionsStub implements GetCertificateAdditionsResponderInterface {

    @Autowired
    private WcStubStore store;

    @Override
    public GetCertificateAdditionsResponseType getCertificateAdditions(String logicalAddress,
            GetCertificateAdditionsType getCertificateAdditionsType) {
        GetCertificateAdditionsResponseType response = new GetCertificateAdditionsResponseType();
        response.setResult(ResultCodeType.OK);
        response.getAdditions().addAll(getItems(getCertificateAdditionsType));
        return response;
    }

    private Collection<IntygAdditionsType> getItems(GetCertificateAdditionsType getCertificateAdditionsType) {
        return getCertificateAdditionsType.getIntygsId().stream()
                .map(this::getResultForIntygId)
                .collect(Collectors.toList());
    }

    private IntygAdditionsType getResultForIntygId(IntygId intyg) {
        // To match the behaviour of actual WC responder implementation, we should return a result for all intygids queried for.
        final IntygAdditionsType addition = store.getAddition(intyg.getExtension());
        if (addition != null) {
            return addition;
        } else {
            IntygAdditionsType noResult = new IntygAdditionsType();
            noResult.setIntygsId(intyg);
            return noResult;
        }
    }

}
