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
package se.inera.intyg.rehabstod.integration.srs.stub;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import se.inera.intyg.clinicalprocess.healthcond.srs.getdiagnosiscodes.v1.Diagnos;
import se.inera.intyg.clinicalprocess.healthcond.srs.getdiagnosiscodes.v1.GetDiagnosisCodesRequestType;
import se.inera.intyg.clinicalprocess.healthcond.srs.getdiagnosiscodes.v1.GetDiagnosisCodesResponderInterface;
import se.inera.intyg.clinicalprocess.healthcond.srs.getdiagnosiscodes.v1.GetDiagnosisCodesResponseType;
import se.inera.intyg.clinicalprocess.healthcond.srs.getriskpredictionforcertificate.v1.GetRiskPredictionForCertificateRequestType;
import se.inera.intyg.clinicalprocess.healthcond.srs.getriskpredictionforcertificate.v1.GetRiskPredictionForCertificateResponderInterface;
import se.inera.intyg.clinicalprocess.healthcond.srs.getriskpredictionforcertificate.v1.GetRiskPredictionForCertificateResponseType;
import se.inera.intyg.clinicalprocess.healthcond.srs.getriskpredictionforcertificate.v1.RiskPrediktion;
import se.inera.intyg.clinicalprocess.healthcond.srs.getriskpredictionforcertificate.v1.Risksignal;

/**
 * Stub for SRS. Will round-robin risk categories so first intygsId queried gets 1, the next 2...3...4...1...2...3...
 *
 * Created by eriklupander on 2017-10-31.
 */
@Service
@Profile({"rhs-srs-stub"})
public class SRSStub implements GetRiskPredictionForCertificateResponderInterface, GetDiagnosisCodesResponderInterface {

    private static final int ONE = 1;
    private static final int TWO = 2;
    private static final int THREE = 3;
    private static final int FOUR = 4;
    private static final int FIVE = 5;

    private boolean active = true;

    @Override
    public GetRiskPredictionForCertificateResponseType getRiskPredictionForCertificate(GetRiskPredictionForCertificateRequestType reqType) {
        if (!active) {
            throw new RuntimeException("Faking an error in the stub!");
        }

        GetRiskPredictionForCertificateResponseType resp = new GetRiskPredictionForCertificateResponseType();
        for (int a = 0; a < reqType.getIntygsId().size(); a++) {

            // Skip adding every 5th result to mimic items having no prediction in SRS.
            if (a % FIVE != 0) {
                RiskPrediktion riskPred = new RiskPrediktion();
                riskPred.setIntygsId(reqType.getIntygsId().get(a));
                riskPred.setRisksignal(buildRiskSignal(a));
                resp.getRiskPrediktioner().add(riskPred);
            }
        }
        return resp;
    }

    @Override
    public GetDiagnosisCodesResponseType getDiagnosisCodes(GetDiagnosisCodesRequestType getDiagnosisCodesRequestType) {
        GetDiagnosisCodesResponseType response = new GetDiagnosisCodesResponseType();
        response.setPrediktionsmodellversion("2.2");
        List<Diagnos> dList = Arrays.asList("F43","M79","S52")
            .stream()
            .map(code -> {
                Diagnos d = new Diagnos();
                d.setCode(code);
                d.setCodeSystem("1.2.752.116.1.1.1.1.3");
                return d;
            })
            .collect(Collectors.toList());
        response.getDiagnos().addAll(dList);
        return response;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    private Risksignal buildRiskSignal(int index) {
        Risksignal riskSignal = new Risksignal();
        riskSignal.setRiskkategori(getRiskInt(index));
        riskSignal.setBeskrivning(getRiskBeskrivning(riskSignal.getRiskkategori()));
        riskSignal.setBerakningstidpunkt(LocalDateTime.now());
        return riskSignal;
    }

    private String getRiskBeskrivning(int risk) {
        switch (risk) {
            case ONE:
                return "Måttlig risk att sjukfallet varar i mer än 90 dagar";
            case TWO:
                return "Hög risk att sjukfallet varar i mer än 90 dagar";
            case THREE:
                return "Mycket hög risk att sjukfallet varar i mer än 90 dagar";
            default:
                throw new IllegalArgumentException("Only risks 1,2 and 3 are possible");
        }
    }

    // Package public for unit-tests
    Integer getRiskInt(int index) {
        return index % THREE + ONE;
    }
}
