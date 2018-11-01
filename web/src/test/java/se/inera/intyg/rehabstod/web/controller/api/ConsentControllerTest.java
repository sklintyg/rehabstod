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
package se.inera.intyg.rehabstod.web.controller.api;

import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import se.inera.intyg.infra.integration.hsa.model.Vardenhet;
import se.inera.intyg.infra.integration.hsa.model.Vardgivare;
import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.auth.RehabstodUserPreferences;
import se.inera.intyg.rehabstod.common.integration.json.CustomObjectMapper;
import se.inera.intyg.rehabstod.service.Urval;
import se.inera.intyg.rehabstod.service.sjukfall.ConsentService;
import se.inera.intyg.rehabstod.service.user.UserService;
import se.inera.intyg.rehabstod.web.controller.api.dto.RegisterExtendedConsentRequest;
import se.inera.intyg.rehabstod.web.controller.api.dto.RegisterExtendedConsentResponse;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Magnus Ekstrand on 03/02/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class ConsentControllerTest {

    private static final String PERSON_ID = "19121212-1212";
    private static final String VARDGIVARE_ID = "VG123";
    private static final String VARDENHETS_ID = "VEA";
    private static final int DAYS = 30;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    RehabstodUser rehabstodUserMock;

    @Mock
    private UserService userServiceMock;

    @Mock
    private ConsentService consentServiceMock;

    @InjectMocks
    ConsentController testee = new ConsentController();

    @Before
    public void before() {
        when(userServiceMock.getUser()).thenReturn(rehabstodUserMock);
        when(rehabstodUserMock.getValdVardgivare()).thenReturn(new Vardgivare(VARDGIVARE_ID, "vårdgivare"));
        when(rehabstodUserMock.getValdVardenhet()).thenReturn(new Vardenhet(VARDENHETS_ID, "enhet"));
        when(rehabstodUserMock.getUrval()).thenReturn(Urval.ALL);
        RehabstodUserPreferences preferences = RehabstodUserPreferences.empty();
        preferences.updatePreference(RehabstodUserPreferences.Preference.MAX_ANTAL_DAGAR_MELLAN_INTYG, "5");
        preferences.updatePreference(RehabstodUserPreferences.Preference.MAX_ANTAL_DAGAR_SEDAN_SJUKFALL_AVSLUT, "0");
        when(rehabstodUserMock.getPreferences()).thenReturn(preferences);
    }

    @Test
    public void testRegisterExtendedConsent() {
        LocalDateTime result = LocalDateTime.now();

        when(consentServiceMock.giveConsent(anyString(), anyBoolean(), anyString(), any(), any(), any()))
                .thenReturn(result);

        RegisterExtendedConsentResponse response = testee.registerConsent(createRequest(PERSON_ID));

        LocalDateTime consentFrom = LocalDate.now().atStartOfDay();
        LocalDateTime consentTo = LocalDate.now().plusDays(DAYS).atTime(23, 59, 59);

        assertEquals(rehabstodUserMock.getHsaId(), response.getRegisteredBy());
        assertEquals(RegisterExtendedConsentResponse.ResponseCode.OK, response.getResponseCode());
        verify(consentServiceMock).giveConsent(eq(PERSON_ID), eq(false), eq(null), eq(consentFrom), eq(consentTo), eq(rehabstodUserMock));
    }

    @Test
    public void testRegisterExtendedConsent_error() {
        when(consentServiceMock.giveConsent(anyString(), anyBoolean(), anyString(), any(), any(), any()))
                .thenThrow(new RuntimeException("error"));

        RegisterExtendedConsentResponse response = testee.registerConsent(createRequest(PERSON_ID));

        assertEquals(rehabstodUserMock.getHsaId() ,response.getRegisteredBy());
        assertEquals(RegisterExtendedConsentResponse.ResponseCode.ERROR, response.getResponseCode());
        verify(consentServiceMock).giveConsent(anyString(), anyBoolean(), anyString(), any(), any(), any());
    }

    @Test
    public void convertToJson() throws IOException {
        RegisterExtendedConsentRequest request = createRequest(PERSON_ID);
        StringWriter jsonWriter = new StringWriter();
        CustomObjectMapper objectMapper = new CustomObjectMapper();
        objectMapper.writeValue(jsonWriter, request);
        System.out.println(jsonWriter);
    }

    private RegisterExtendedConsentRequest createRequest(String personId) {
        RegisterExtendedConsentRequest request = new RegisterExtendedConsentRequest();
        request.setPatientId(personId);
        request.setDays(DAYS);
        return request;
    }

}
