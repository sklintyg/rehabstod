/*
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.integration.srs.stub;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import se.inera.intyg.clinicalprocess.healthcond.srs.getriskpredictionforcertificate.v1.GetRiskPredictionForCertificateRequestType;
import se.inera.intyg.clinicalprocess.healthcond.srs.getriskpredictionforcertificate.v1.GetRiskPredictionForCertificateResponderInterface;
import se.inera.intyg.clinicalprocess.healthcond.srs.getriskpredictionforcertificate.v1.GetRiskPredictionForCertificateResponseType;
import se.inera.intyg.clinicalprocess.healthcond.srs.getriskpredictionforcertificate.v1.RiskPrediktion;
import se.inera.intyg.clinicalprocess.healthcond.srs.getriskpredictionforcertificate.v1.Risksignal;

import java.math.BigInteger;

/**
 * Stub for SRS. Will round-robin risk categories so first intygsId queried gets 1, the next 2...3...4...1...2...3...
 *
 * Created by eriklupander on 2017-10-31.
 */
@Service
@Profile({ "rhs-srs-stub" })
public class SRSStub implements GetRiskPredictionForCertificateResponderInterface {

    private static final int ONE = 1;
    private static final int TWO = 2;
    private static final int THREE = 3;
    private static final int FOUR = 4;

    @Override
    public GetRiskPredictionForCertificateResponseType getRiskPredictionForCertificate(GetRiskPredictionForCertificateRequestType reqType) {
        GetRiskPredictionForCertificateResponseType resp = new GetRiskPredictionForCertificateResponseType();
        for (int a = 0; a < reqType.getIntygsId().size(); a++) {
            RiskPrediktion riskPred = new RiskPrediktion();
            riskPred.setIntygsId(reqType.getIntygsId().get(a));
            riskPred.setRisksignal(buildRiskSignal(a));
            resp.getRiskPrediktioner().add(riskPred);
        }
        return resp;
    }

    private Risksignal buildRiskSignal(int index) {
        Risksignal riskSignal = new Risksignal();
        riskSignal.setRiskkategori(getRiskInt(index));
        riskSignal.setBeskrivning(getRiskBeskrivning(riskSignal.getRiskkategori().intValue()));
        return riskSignal;
    }

    private String getRiskBeskrivning(int risk) {
        switch (risk) {
        case ONE:
            return "Ingen risk";
        case TWO:
            return "Låg risk";
        case THREE:
            return "Medel risk";
        case FOUR:
            return "Hög risk";
        default:
            throw new IllegalArgumentException("Only risks 1,2,3 and 4 are possible");
        }
    }

    // Package public for unit-tests
    BigInteger getRiskInt(int index) {
        return BigInteger.valueOf(index % FOUR + ONE);
    }
}
