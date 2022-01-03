/*
 * Copyright (C) 2022 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.integration.samtyckestjanst.stub;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import se.riv.informationsecurity.authorization.consent.CheckConsent.v2.rivtabp21.CheckConsentResponderInterface;
import se.riv.informationsecurity.authorization.consent.CheckConsentResponder.v2.CheckConsentResponseType;
import se.riv.informationsecurity.authorization.consent.CheckConsentResponder.v2.CheckConsentType;
import se.riv.informationsecurity.authorization.consent.v2.AssertionTypeType;
import se.riv.informationsecurity.authorization.consent.v2.CheckResultType;
import se.riv.informationsecurity.authorization.consent.v2.ResultCodeType;
import se.riv.informationsecurity.authorization.consent.v2.ResultType;

/**
 * Created by Magnus Ekstrand on 2018-10-10.
 */
public class CheckConsentStub implements CheckConsentResponderInterface {

    @Autowired
    private SamtyckestjanstStubStore store;

    @Override
    public CheckConsentResponseType checkConsent(String logicalAddress,
        CheckConsentType parameters) {

        validate(logicalAddress, parameters);

        CheckResultType result = new CheckResultType();
        result.setAssertionType(AssertionTypeType.CONSENT); // fast värde enl. krav
        result.setHasConsent(store.hasConsent(
            parameters.getAccessingActor().getCareProviderId(),
            parameters.getAccessingActor().getCareUnitId(),
            parameters.getPatientId().getExtension(),
            LocalDate.now()));

        ResultType resultType = new ResultType();
        resultType.setResultCode(ResultCodeType.OK);
        result.setResult(resultType);

        // Create a response
        CheckConsentResponseType response = new CheckConsentResponseType();
        response.setCheckResult(result);
        return response;
    }

    private void validate(String logicalAddress, CheckConsentType parameters) {
        List<String> messages = new ArrayList<>();
        if (logicalAddress == null || logicalAddress.length() == 0) {
            messages.add("logicalAddress can not be null or empty");
        }
        if (parameters == null) {
            messages.add("CheckConsentType can not be null");
        } else {
            if (Strings.isNullOrEmpty(parameters.getAccessingActor().getCareProviderId())) {
                messages.add("Missing AccessingActor.CareProviderId");
            }
            if (Strings.isNullOrEmpty(parameters.getAccessingActor().getCareUnitId())) {
                messages.add("Missing AccessingActor.CareUnitId");
            }
            if (Strings.isNullOrEmpty(parameters.getPatientId().getExtension())) {
                messages.add("Missing PatientId.Extension");
            }
        }

        if (messages.size() > 0) {
            throw new IllegalArgumentException(Joiner.on(",").join(messages));
        }
    }

}
