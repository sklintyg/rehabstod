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
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import se.riv.informationsecurity.authorization.consent.RegisterExtendedConsent.v2.rivtabp21.RegisterExtendedConsentResponderInterface;
import se.riv.informationsecurity.authorization.consent.RegisterExtendedConsentResponder.v2.RegisterExtendedConsentResponseType;
import se.riv.informationsecurity.authorization.consent.RegisterExtendedConsentResponder.v2.RegisterExtendedConsentType;
import se.riv.informationsecurity.authorization.consent.v2.IIType;
import se.riv.informationsecurity.authorization.consent.v2.ResultCodeType;
import se.riv.informationsecurity.authorization.consent.v2.ResultType;

/**
 * Created by Magnus Ekstrand on 2018-10-10.
 */
public class RegisterExtendedConsentStub implements RegisterExtendedConsentResponderInterface {

    @Autowired
    private SamtyckestjanstStubStore store;

    @Override
    public RegisterExtendedConsentResponseType registerExtendedConsent(String logicalAddress,
        RegisterExtendedConsentType registerExtendedConsentType) {

        validate(logicalAddress, registerExtendedConsentType);

        ConsentData consentData = new ConsentData.Builder(registerExtendedConsentType.getAssertionId(),
            registerExtendedConsentType.getCareProviderId(),
            registerExtendedConsentType.getCareUnitId(),
            registerExtendedConsentType.getPatientId().getExtension(),
            registerExtendedConsentType.getRegistrationAction())
            .employeeId(registerExtendedConsentType.getEmployeeId())
            .consentFrom(registerExtendedConsentType.getStartDate())
            .consentTo(registerExtendedConsentType.getEndDate())
            .representedBy(getRegisteredBy(registerExtendedConsentType.getRepresentedBy()))
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

    private String getRegisteredBy(IIType representedBy) {
        return representedBy == null ? null : representedBy.getExtension();
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
