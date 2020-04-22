/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
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

package se.inera.intyg.rehabstod.integration.samtyckestjanst.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;

import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import se.inera.intyg.rehabstod.integration.samtyckestjanst.client.SamtyckestjanstClientService;
import se.riv.informationsecurity.authorization.consent.CheckConsentResponder.v2.CheckConsentResponseType;
import se.riv.informationsecurity.authorization.consent.v2.CheckResultType;
import se.riv.informationsecurity.authorization.consent.v2.ResultCodeType;
import se.riv.informationsecurity.authorization.consent.v2.ResultType;

/**
 * @author Magnus Ekstrand on 2018-10-11.
 */
@RunWith(MockitoJUnitRunner.class)
public class SamtyckestjanstIntegrationServiceImplTest {

    private static final String USER_HSA_ID = UUID.randomUUID().toString();
    private static final String PATIENT_ID = "20121212-1212";
    private static final String VG_HSAID_2 = "vgHsaId-2";
    private static final String VE_HSAID_2 = "veHsaId-2";

    @Mock
    private SamtyckestjanstClientService samtyckestjanstClientService;

    @InjectMocks
    private SamtyckestjanstIntegrationServiceImpl testee = new SamtyckestjanstIntegrationServiceImpl();


    @Before
    public void setUp() {
        doAnswer(new Answer<CheckConsentResponseType>() {
            @Override
            public CheckConsentResponseType answer(InvocationOnMock invocation) {
                if (invocation.getArgument(0, String.class).equals(VG_HSAID_2)
                    && invocation.getArgument(1, String.class).equals(VE_HSAID_2)) {
                    return createCheckConsentResponseType(true);
                } else {
                    return createCheckConsentResponseType(false);
                }
            }
        }).when(samtyckestjanstClientService).checkConsent(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    public void testCheckForConsent_missing() {
        String currentVgHsaId = "vgHsaId-3";
        String currentVeHsaId = "veHsaId-3";

        boolean haveConsent = testee.checkForConsent(PATIENT_ID, USER_HSA_ID, currentVgHsaId, currentVeHsaId);

        assertFalse(haveConsent);
    }

    @Test
    public void testCheckForConsent() {
        boolean haveConsent = testee.checkForConsent(PATIENT_ID, USER_HSA_ID, VG_HSAID_2, VE_HSAID_2);

        assertTrue(haveConsent);
    }

    private CheckConsentResponseType createCheckConsentResponseType(boolean hasConsent) {
        ResultType resultType = new ResultType();
        resultType.setResultCode(ResultCodeType.OK);

        CheckResultType checkResultType = new CheckResultType();
        checkResultType.setHasConsent(hasConsent);
        checkResultType.setResult(resultType);

        CheckConsentResponseType response = new CheckConsentResponseType();
        response.setCheckResult(checkResultType);
        return response;
    }
}