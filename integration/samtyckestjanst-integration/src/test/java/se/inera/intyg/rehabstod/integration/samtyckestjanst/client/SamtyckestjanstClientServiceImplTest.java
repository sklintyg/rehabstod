/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.integration.samtyckestjanst.client;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import com.google.common.base.Strings;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import se.inera.intyg.schemas.contract.Personnummer;
import se.riv.informationsecurity.authorization.consent.CheckConsent.v2.rivtabp21.CheckConsentResponderInterface;
import se.riv.informationsecurity.authorization.consent.CheckConsentResponder.v2.CheckConsentType;
import se.riv.informationsecurity.authorization.consent.RegisterExtendedConsent.v2.rivtabp21.RegisterExtendedConsentResponderInterface;
import se.riv.informationsecurity.authorization.consent.RegisterExtendedConsentResponder.v2.RegisterExtendedConsentType;
import se.riv.informationsecurity.authorization.consent.v2.ActionType;
import se.riv.informationsecurity.authorization.consent.v2.ActorType;
import se.riv.informationsecurity.authorization.consent.v2.AssertionTypeType;
import se.riv.informationsecurity.authorization.consent.v2.ScopeType;

/**
 * @author Magnus Ekstrand on 2018-10-18.
 */
@RunWith(MockitoJUnitRunner.class)
public class SamtyckestjanstClientServiceImplTest {

    private static final String REGISTERED_BY_HSA_ID = UUID.randomUUID().toString();
    private static final String USER_HSA_ID = UUID.randomUUID().toString();
    private static final String PATIENT_ID = "20121212-1212";
    private static final Personnummer PERSONNUMMER = Personnummer.createPersonnummer(PATIENT_ID).get();
    private static final String VG_HSA_ID = "vgHsaId-2";
    private static final String VE_HSA_ID = "veHsaId-2.1";
    private static final String LOGICAL_ADDRESS = "123";


    @Mock
    private CheckConsentResponderInterface checkConsentService;

    @Mock
    private RegisterExtendedConsentResponderInterface registerExtendedConsentService;

    @InjectMocks
    private SamtyckestjanstClientServiceImpl testee;

    @Before
    public void setUp() throws Exception {
        ReflectionTestUtils.setField(testee, "logicalAddress", LOGICAL_ADDRESS);
    }

    @Test
    public void checkConsent() {
        ArgumentCaptor<CheckConsentType> requestCapture = ArgumentCaptor.forClass(CheckConsentType.class);

        testee.checkConsent(VG_HSA_ID, VE_HSA_ID, USER_HSA_ID, PATIENT_ID);

        verify(checkConsentService).checkConsent(eq(LOGICAL_ADDRESS), requestCapture.capture());

        final CheckConsentType args = requestCapture.getValue();
        assertEquals(VG_HSA_ID, args.getAccessingActor().getCareProviderId());
        assertEquals(VE_HSA_ID, args.getAccessingActor().getCareUnitId());
        assertEquals(USER_HSA_ID, args.getAccessingActor().getEmployeeId());
        assertEquals("1.2.752.129.2.1.3.1", args.getPatientId().getRoot());
        assertEquals(PATIENT_ID.replace("-", ""), args.getPatientId().getExtension());
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkConsentThrowsExceptionForInvalidPatientId() throws Exception {
        testee.checkConsent(VG_HSA_ID, VE_HSA_ID, USER_HSA_ID, "123456789");
    }

    @Test
    public void registerExtendedConsent() {
        ArgumentCaptor<RegisterExtendedConsentType> requestCapture = ArgumentCaptor.forClass(RegisterExtendedConsentType.class);

        LocalDateTime registrationDate = LocalDateTime.now();
        ActionType registrationAction = createActionType(REGISTERED_BY_HSA_ID, registrationDate, REGISTERED_BY_HSA_ID, registrationDate);

        testee.registerExtendedConsent(VG_HSA_ID, VE_HSA_ID, USER_HSA_ID, PERSONNUMMER, null, null, null, registrationAction);

        verify(registerExtendedConsentService).registerExtendedConsent(eq(LOGICAL_ADDRESS), requestCapture.capture());

        final RegisterExtendedConsentType args = requestCapture.getValue();
        assertFalse(Strings.isNullOrEmpty(args.getAssertionId()));
        assertNull(args.getEndDate());
        assertNull(args.getRepresentedBy());
        assertNotNull(args.getStartDate());

        assertEquals(VG_HSA_ID, args.getCareProviderId());
        assertEquals(VE_HSA_ID, args.getCareUnitId());
        assertEquals(AssertionTypeType.CONSENT, args.getAssertionType());
        assertEquals(ScopeType.NATIONAL_LEVEL, args.getScope());
        assertEquals("1.2.752.129.2.1.3.1", args.getPatientId().getRoot());
        assertEquals(PATIENT_ID.replace("-", ""), args.getPatientId().getExtension());

        assertEquals(REGISTERED_BY_HSA_ID, args.getRegistrationAction().getRegisteredBy().getEmployeeId());
        assertEquals(REGISTERED_BY_HSA_ID, args.getRegistrationAction().getRequestedBy().getEmployeeId());
        assertEquals(registrationDate, args.getRegistrationAction().getRegistrationDate());
        assertEquals(registrationDate, args.getRegistrationAction().getRequestDate());

    }

    @Test(expected = IllegalArgumentException.class)
    public void registerExtendedConsentThrowsExceptionForInvalidRepresentedBy() throws Exception {
        LocalDateTime registrationDate = LocalDateTime.now();
        ActionType registrationAction = createActionType(REGISTERED_BY_HSA_ID, registrationDate, REGISTERED_BY_HSA_ID, registrationDate);

        testee.registerExtendedConsent(VG_HSA_ID, VE_HSA_ID, USER_HSA_ID, PERSONNUMMER, "123456789", null, null, registrationAction);
    }

    @Test
    public void registerExtendedConsentThrowsExceptionForInvalidConsentDate() throws Exception {
        LocalDateTime registrationDate = LocalDateTime.now();
        ActionType registrationAction = createActionType(REGISTERED_BY_HSA_ID, registrationDate, REGISTERED_BY_HSA_ID, registrationDate);

        LocalDateTime consentFrom = LocalDateTime.now();
        LocalDateTime consentTo = consentFrom.minusDays(1);

        try {
            testee
                .registerExtendedConsent(VG_HSA_ID, VE_HSA_ID, USER_HSA_ID, PERSONNUMMER, null, consentFrom, consentTo, registrationAction);
            fail("Date consentTo cannot be before consentFrom");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().startsWith("a consent's start date"));
        }

        try {
            testee.registerExtendedConsent(VG_HSA_ID, VE_HSA_ID, USER_HSA_ID, PERSONNUMMER, null, null, consentTo, registrationAction);
            fail("Date consentTo cannot be before consentFrom. "
                + "If consentFrom is not supplied Then "
                + "   it will be set, in client, to LocalDateTime.now() "
                + "   and consentTo must be in the future");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().startsWith("a consent's start date"));
        }

    }

    private ActionType createActionType(String registeredBy, LocalDateTime registeredDate,
        String requestedBy, LocalDateTime requestedDate) {

        ActionType registrationAction = new ActionType();
        registrationAction.setRegisteredBy(createActorType(registeredBy));
        registrationAction.setRegistrationDate(registeredDate);
        registrationAction.setRequestedBy(createActorType(requestedBy));
        registrationAction.setRequestDate(requestedDate);
        return registrationAction;
    }

    private ActorType createActorType(String userId) {
        ActorType actorType = new ActorType();
        actorType.setEmployeeId(userId);
        return actorType;
    }
}