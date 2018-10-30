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
package se.inera.intyg.rehabstod.integration.samtyckestjanst.stub;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import se.riv.informationsecurity.authorization.consent.CheckConsent.v2.rivtabp21.CheckConsentResponderInterface;
import se.riv.informationsecurity.authorization.consent.CheckConsentResponder.v2.CheckConsentResponseType;
import se.riv.informationsecurity.authorization.consent.CheckConsentResponder.v2.CheckConsentType;
import se.riv.informationsecurity.authorization.consent.RegisterExtendedConsent.v2.rivtabp21.RegisterExtendedConsentResponderInterface;
import se.riv.informationsecurity.authorization.consent.RegisterExtendedConsentResponder.v2.RegisterExtendedConsentResponseType;
import se.riv.informationsecurity.authorization.consent.RegisterExtendedConsentResponder.v2.RegisterExtendedConsentType;
import se.riv.informationsecurity.authorization.consent.v2.AssertionTypeType;
import se.riv.informationsecurity.authorization.consent.v2.CheckResultType;
import se.riv.informationsecurity.authorization.consent.v2.ResultCodeType;
import se.riv.informationsecurity.authorization.consent.v2.ResultType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Magnus Ekstrand on 2018-10-10.
 */
public class SamtyckestjanstIntegrationStub implements CheckConsentResponderInterface, RegisterExtendedConsentResponderInterface {

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

    @Override
    public RegisterExtendedConsentResponseType registerExtendedConsent(String logicalAddress,
                                                                       RegisterExtendedConsentType registerExtendedConsentType) {

        validate(logicalAddress, registerExtendedConsentType);

        ConsentData consentData = new ConsentData.Builder(registerExtendedConsentType.getAssertionId(),
                registerExtendedConsentType.getCareProviderId(),
                registerExtendedConsentType.getCareUnitId(),
                registerExtendedConsentType.getPatientId().getExtension(),
                registerExtendedConsentType.getRegistrationAction())
                .userHsaId(registerExtendedConsentType.getEmployeeId())
                .consentFrom(registerExtendedConsentType.getStartDate())
                .consentFrom(registerExtendedConsentType.getEndDate())
                .representedBy(registerExtendedConsentType.getRepresentedBy().getExtension())
                .build();

        RegisterExtendedConsentResponseType response = new RegisterExtendedConsentResponseType();
        ResultType resultType = new ResultType();

        try {
            store.add(consentData);
            resultType.setResultCode(ResultCodeType.OK);
        } catch (Exception e) {
            resultType.setResultCode(ResultCodeType.ERROR);
            resultType.setResultText(e.getMessage());
        }

        response.setResult(resultType);
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

    private void validate(String logicalAddress, RegisterExtendedConsentType parameters) {
        List<String> messages = new ArrayList<>();
        if (logicalAddress == null || logicalAddress.length() == 0) {
            messages.add("logicalAddress can not be null or empty");
        }
        if (parameters == null) {
            messages.add("CheckConsentType can not be null");
        } else {
            if (Strings.isNullOrEmpty(parameters.getAssertionId())) {
                messages.add("Missing AssertionId");
            }
            if (parameters.getAssertionType() == null) {
                messages.add("Missing AssertionType");
            }
            if (Strings.isNullOrEmpty(parameters.getCareProviderId())) {
                messages.add("Missing CareProviderId");
            }
            if (Strings.isNullOrEmpty(parameters.getCareUnitId())) {
                messages.add("Missing CareUnitId");
            }
            if (Strings.isNullOrEmpty(parameters.getPatientId().getExtension())) {
                messages.add("Missing PatientId.Extension");
            }
            if (parameters.getRegistrationAction() == null) {
                messages.add("Missing RegistrationAction");
            }
            if (parameters.getScope() == null) {
                messages.add("Missing Scope");
            }
        }

        if (messages.size() > 0) {
            throw new IllegalArgumentException(Joiner.on(",").join(messages));
        }
    }

}
