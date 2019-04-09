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
package se.inera.intyg.rehabstod.web.controller.api;

import com.google.common.base.Strings;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import se.inera.intyg.infra.integration.hsa.model.Vardenhet;
import se.inera.intyg.infra.integration.hsa.model.Vardgivare;
import se.inera.intyg.infra.logmessages.ActivityType;
import se.inera.intyg.infra.logmessages.ResourceType;
import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.auth.RehabstodUserPreferences;
import se.inera.intyg.rehabstod.service.pdl.LogService;
import se.inera.intyg.rehabstod.service.sjukfall.ConsentService;
import se.inera.intyg.rehabstod.service.user.UserService;
import se.inera.intyg.rehabstod.web.controller.api.dto.RegisterExtendedConsentRequest;
import se.inera.intyg.rehabstod.web.controller.api.dto.RegisterExtendedConsentResponse;
import se.inera.intyg.schemas.contract.Personnummer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * Created by Magnus Ekstrand on 03/02/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class ConsentControllerTest {

    private static final String PERSON_ID = "19121212-1212";
    private static final String LAKARE_ID = "L999";
    private static final String LAKARE_NAMN = "L. Lakarsson";
    private static final String VARDGIVARE_ID = "VG123";
    private static final String VARDENHETS_ID = "VEA";

    private static final int DAYS = 30;

    @Mock
    LogService logServiceMock;

    @Mock
    private UserService userServiceMock;

    @Mock
    private ConsentService consentServiceMock;

    @InjectMocks
    ConsentController testee = new ConsentController();

    @Before
    public void before() {
        when(userServiceMock.getUser()).thenReturn(buildUser());
    }

    @Test
    public void testRegisterExtendedConsent() {
        LocalDateTime result = LocalDateTime.now();

        when(consentServiceMock.giveConsent(any(), anyBoolean(), anyString(), any(), any(), any()))
                .thenReturn(result);

        RegisterExtendedConsentResponse response = testee.registerConsent(buildRequest(PERSON_ID));

        LocalDateTime consentFrom = LocalDate.now().atStartOfDay();
        LocalDateTime consentTo = LocalDate.now().plusDays(DAYS).atTime(23, 59, 59);

        assertEquals(LAKARE_ID, response.getRegisteredBy());
        assertEquals(RegisterExtendedConsentResponse.ResponseCode.OK, response.getResponseCode());

        Optional<Personnummer> personnummer = Personnummer.createPersonnummer(PERSON_ID);

        verify(consentServiceMock).giveConsent(eq(personnummer.get()), eq(false),
                eq(null), eq(consentFrom), eq(consentTo), any(RehabstodUser.class));
        verify(logServiceMock).logConsent(eq(personnummer.get()), eq(ActivityType.CREATE), eq(ResourceType.RESOURCE_TYPE_SAMTYCKE));
    }

    @Test
    public void testRegisterExtendedConsentMaxDays() {
        LocalDateTime result = LocalDateTime.now();

        when(consentServiceMock.giveConsent(any(), anyBoolean(), anyString(), any(), any(), any()))
                .thenReturn(result);

        RegisterExtendedConsentResponse response = testee.registerConsent(buildRequest(PERSON_ID, ConsentController.MAX_DAYS_FOR_CONSENT));

        LocalDateTime consentFrom = LocalDate.now().atStartOfDay();
        LocalDateTime consentTo = LocalDate.now().plusDays(ConsentController.MAX_DAYS_FOR_CONSENT).atTime(23, 59, 59);

        assertEquals(LAKARE_ID, response.getRegisteredBy());
        assertEquals(RegisterExtendedConsentResponse.ResponseCode.OK, response.getResponseCode());

        Optional<Personnummer> personnummer = Personnummer.createPersonnummer(PERSON_ID);

        verify(consentServiceMock).giveConsent(eq(personnummer.get()), eq(false),
                eq(null), eq(consentFrom), eq(consentTo), any(RehabstodUser.class));
        verify(logServiceMock).logConsent(eq(personnummer.get()), eq(ActivityType.CREATE), eq(ResourceType.RESOURCE_TYPE_SAMTYCKE));
    }

    @Test
    public void testRegisterExtendedConsent_invalidPersonnummer() {
        RegisterExtendedConsentResponse response = testee.registerConsent(buildRequest("21121212-1212"));

        assertEquals(LAKARE_ID, response.getRegisteredBy());
        assertEquals(RegisterExtendedConsentResponse.ResponseCode.ERROR, response.getResponseCode());
        assertTrue(!Strings.isNullOrEmpty(response.getResponseMessage()));

        verifyZeroInteractions(consentServiceMock);
    }

    @Test
    public void testRegisterExtendedConsent_invalidConsentBoundary() {
        RegisterExtendedConsentResponse response = testee.registerConsent(
                buildRequest(PERSON_ID, ConsentController.MAX_DAYS_FOR_CONSENT + 1));

        assertEquals(LAKARE_ID, response.getRegisteredBy());
        assertEquals(RegisterExtendedConsentResponse.ResponseCode.ERROR, response.getResponseCode());
        assertTrue(!Strings.isNullOrEmpty(response.getResponseMessage()));

        verifyZeroInteractions(consentServiceMock);
    }

    @Test
    public void testRegisterExtendedConsent_error() {
        when(consentServiceMock.giveConsent(any(), anyBoolean(), anyString(), any(), any(), any()))
                .thenReturn(null);

        RegisterExtendedConsentResponse response = testee.registerConsent(buildRequest(PERSON_ID));

        assertEquals(LAKARE_ID, response.getRegisteredBy());
        assertEquals(RegisterExtendedConsentResponse.ResponseCode.ERROR, response.getResponseCode());
        assertTrue(!Strings.isNullOrEmpty(response.getResponseMessage()));

        verify(consentServiceMock).giveConsent(any(), anyBoolean(), anyString(), any(), any(), any());
    }

    private RehabstodUser buildUser() {
        RehabstodUserPreferences preferences = RehabstodUserPreferences.empty();
        preferences.updatePreference(RehabstodUserPreferences.Preference.MAX_ANTAL_DAGAR_MELLAN_INTYG, "5");
        preferences.updatePreference(RehabstodUserPreferences.Preference.MAX_ANTAL_DAGAR_SEDAN_SJUKFALL_AVSLUT, "0");

        RehabstodUser user = new RehabstodUser(LAKARE_ID, LAKARE_NAMN, true);
        user.setPreferences(preferences);
        user.setValdVardgivare(new Vardgivare(VARDGIVARE_ID, "vårdgivare"));
        user.setValdVardenhet(new Vardenhet(VARDENHETS_ID, "enhet"));

        return user;
    }

    private RegisterExtendedConsentRequest buildRequest(String personId) {
        return buildRequest(personId, DAYS);
    }

    private RegisterExtendedConsentRequest buildRequest(String personId, int days) {
        RegisterExtendedConsentRequest request = new RegisterExtendedConsentRequest();
        request.setPatientId(personId);
        request.setDays(days);
        return request;
    }

}
