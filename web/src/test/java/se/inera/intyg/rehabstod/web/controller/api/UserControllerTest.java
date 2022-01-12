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
package se.inera.intyg.rehabstod.web.controller.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.env.Environment;
import se.inera.intyg.infra.integration.hsatk.model.legacy.Vardenhet;
import se.inera.intyg.infra.security.authorities.AuthoritiesException;
import se.inera.intyg.infra.security.authorities.CommonAuthoritiesResolver;
import se.inera.intyg.infra.security.common.model.AuthenticationMethod;
import se.inera.intyg.rehabstod.auth.RehabstodUnitChangeService;
import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.auth.RehabstodUserPreferences;
import se.inera.intyg.rehabstod.auth.RehabstodUserTokens;
import se.inera.intyg.rehabstod.persistence.repository.AnvandarPreferenceRepository;
import se.inera.intyg.rehabstod.service.user.TokenExchangeService;
import se.inera.intyg.rehabstod.service.user.UserPreferencesService;
import se.inera.intyg.rehabstod.service.user.UserService;
import se.inera.intyg.rehabstod.web.controller.api.dto.ChangeSelectedUnitRequest;
import se.inera.intyg.rehabstod.web.controller.api.dto.GetAccessTokenResponse;

/**
 * Created by marced on 01/02/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {

    private static final String HSA_ID = "abcdefghijkl";

    @Mock
    RehabstodUser rehabUserMock;

    @Mock
    UserService userService;

    @Mock
    RehabstodUnitChangeService rehabstodUnitChangeService;

    @Mock
    private CommonAuthoritiesResolver commonAuthoritiesResolver;

    @Mock
    private AnvandarPreferenceRepository anvandarPreferenceRepository;

    @Mock
    private UserPreferencesService userPreferencesService;

    @Mock
    TokenExchangeService tokenExchangeService;

    @Mock
    private Environment environment;

    @InjectMocks
    private UserController userController = new UserController();

    @Before
    public void before() {
        when(commonAuthoritiesResolver.getFeatures(any())).thenReturn(Collections.emptyMap());
        when(rehabUserMock.getValdVardenhet()).thenReturn(new Vardenhet("123", "enhet"));
        when(rehabUserMock.getValdVardgivare()).thenReturn(new Vardenhet("456", "vardgivare"));
        when(rehabUserMock.getHsaId()).thenReturn(HSA_ID);
        when(rehabUserMock.getPreferences()).thenReturn(RehabstodUserPreferences.empty());
        when(userService.getUser()).thenReturn(rehabUserMock);
        when(environment.getActiveProfiles()).thenReturn(new String[0]);
    }

    @Test
    public void testCreateGet() {
        userController.getUser();

        verify(userService).getUser();
    }

    @Test
    public void testChangeEnhetSuccess() {
        ChangeSelectedUnitRequest req = new ChangeSelectedUnitRequest("123");
        when(rehabstodUnitChangeService.changeValdVardenhet(eq(req.getId()), eq(rehabUserMock))).thenReturn(true);

        userController.changeSelectedUnitOnUser(req);

        verify(userService).getUser();
        verify(rehabstodUnitChangeService).changeValdVardenhet(eq(req.getId()), eq(rehabUserMock));
    }

    @Test(expected = AuthoritiesException.class)
    public void testChangeEnhetFails() {
        ChangeSelectedUnitRequest req = new ChangeSelectedUnitRequest("123");
        when(rehabstodUnitChangeService.changeValdVardenhet(eq(req.getId()), eq(rehabUserMock))).thenReturn(false);

        userController.changeSelectedUnitOnUser(req);

        verify(userService).getUser();
        verify(rehabstodUnitChangeService).changeValdVardenhet(eq(req.getId()), eq(rehabUserMock));
    }

    @Test
    public void clearAddedVardgivareAndVardeneheterWhenPreferencesHasChanged() {
        when(userPreferencesService.getAllPreferences()).thenReturn(defaultPreferences());

        RehabstodUserPreferences preferences = RehabstodUserPreferences.empty();
        preferences.updatePreference(RehabstodUserPreferences.Preference.MAX_ANTAL_DAGAR_MELLAN_INTYG, "6");
        preferences.updatePreference(RehabstodUserPreferences.Preference.MAX_ANTAL_DAGAR_SEDAN_SJUKFALL_AVSLUT, "14");

        userController.updatePref(preferences.toFrontendMap());

        verify(rehabUserMock).clearSjfData();
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateInvalidMaxDagarMellanIntygPreference() {
        RehabstodUserPreferences preferences = RehabstodUserPreferences.empty();
        preferences.updatePreference(RehabstodUserPreferences.Preference.MAX_ANTAL_DAGAR_MELLAN_INTYG, "91");
        preferences.validate();
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateInvalidMaxDagarSedanSjukfallPreference() {
        RehabstodUserPreferences preferences = RehabstodUserPreferences.empty();
        preferences.updatePreference(RehabstodUserPreferences.Preference.MAX_ANTAL_DAGAR_SEDAN_SJUKFALL_AVSLUT, "-1");
        preferences.validate();
    }

    @Test
    public void testGetAccessTokenWithResponseNoRefresh() {
        String oldAccessToken = "abc123";

        // Previous access token expires in 1800 seconds
        when(rehabUserMock.getTokens())
            .thenReturn(new RehabstodUserTokens(oldAccessToken, "cba321", LocalDateTime.now().plusSeconds(1800)));

        GetAccessTokenResponse accessTokenResponse = userController.getAccessToken();
        verify(tokenExchangeService, never())
            .refresh(any(RehabstodUserTokens.class));
        assertEquals(oldAccessToken, accessTokenResponse.getAccessToken());

    }

    @Test
    public void testGetAccessTokenNoResponse() {
        when(rehabUserMock.getTokens()).thenReturn(null);

        GetAccessTokenResponse accessTokenResponse = userController.getAccessToken();
        verify(tokenExchangeService, never())
            .refresh(any(RehabstodUserTokens.class));
        assertNull(accessTokenResponse.getAccessToken());
    }

    @Test
    public void testGetAccessTokenFakeUserResponse() {
        when(rehabUserMock.getTokens()).thenReturn(null);

        when(rehabUserMock.getAuthenticationMethod()).thenReturn(AuthenticationMethod.FAKE);

        GetAccessTokenResponse accessTokenResponse = userController.getAccessToken();
        verify(tokenExchangeService, never()).refresh(any(RehabstodUserTokens.class));
        assertEquals("fakeToken-" + HSA_ID, accessTokenResponse.getAccessToken());
    }

    @Test
    public void testGetAccessTokenFakeUserNotProdResponse() {
        when(rehabUserMock.getTokens()).thenReturn(null);

        when(environment.getActiveProfiles()).thenReturn(new String[] {"prod"});

        GetAccessTokenResponse accessTokenResponse = userController.getAccessToken();
        verify(tokenExchangeService, never()).refresh(any(RehabstodUserTokens.class));
        assertNull(accessTokenResponse.getAccessToken());
    }

    @Test
    public void testGetAccessTokenWithResponseWithRefresh() {
        String refreshToken = "cba321";
        String oldAccessToken = "abc123";
        String newAccessToken = "abc456";
        RehabstodUserTokens oldRehabstodUserTokens = new RehabstodUserTokens(oldAccessToken, refreshToken,
            LocalDateTime.now().plusSeconds(30));
        RehabstodUserTokens newRehabstodUserTokens = new RehabstodUserTokens(newAccessToken, refreshToken,
            LocalDateTime.now().plusSeconds(3600));

        // Previous access token expires in 30 seconds
        when(rehabUserMock.getTokens()).thenReturn(oldRehabstodUserTokens);
        when(tokenExchangeService.refresh(any(RehabstodUserTokens.class)))
            .thenReturn(newRehabstodUserTokens);

        GetAccessTokenResponse accessTokenResponse = userController.getAccessToken();
        verify(tokenExchangeService, times(1))
            .refresh(any(RehabstodUserTokens.class));
        assertEquals(newAccessToken, accessTokenResponse.getAccessToken());
    }

    private RehabstodUserPreferences defaultPreferences() {
        RehabstodUserPreferences preferences = RehabstodUserPreferences.empty();
        preferences.updatePreference(RehabstodUserPreferences.Preference.MAX_ANTAL_DAGAR_MELLAN_INTYG, "5");
        preferences.updatePreference(RehabstodUserPreferences.Preference.MAX_ANTAL_DAGAR_SEDAN_SJUKFALL_AVSLUT, "14");
        return preferences;
    }

}
