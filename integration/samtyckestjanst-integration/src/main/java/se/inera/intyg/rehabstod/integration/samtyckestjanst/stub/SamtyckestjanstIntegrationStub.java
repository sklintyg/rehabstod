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

// CHECKSTYLE:OFF LineLength

import com.google.common.base.Joiner;
import org.springframework.beans.factory.annotation.Autowired;
import se.riv.informationsecurity.authorization.consent.CheckConsent.v2.rivtabp21.CheckConsentResponderInterface;
import se.riv.informationsecurity.authorization.consent.CheckConsentResponder.v2.CheckConsentResponseType;
import se.riv.informationsecurity.authorization.consent.CheckConsentResponder.v2.CheckConsentType;

import java.util.ArrayList;
import java.util.List;


// CHECKSTYLE:ON LineLength

/**
 * Created by Magnus Ekstrand on 2018-10-10
 */
public class SamtyckestjanstIntegrationStub implements CheckConsentResponderInterface {

    @Autowired
    private SamtyckestjanstStubStore store;

    @Override
    public CheckConsentResponseType checkConsent(String logicalAddress, CheckConsentType parameters) {
        validate(logicalAddress, parameters);

        CheckConsentResponseType response = new CheckConsentResponseType();

        // TODO: ....

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
            // TODO: check parameters...

        }

        if (messages.size() > 0) {
            throw new IllegalArgumentException(Joiner.on(",").join(messages));
        }
    }
}
