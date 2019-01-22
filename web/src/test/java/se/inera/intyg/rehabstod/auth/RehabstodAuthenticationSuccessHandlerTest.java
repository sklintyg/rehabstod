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
package se.inera.intyg.rehabstod.auth;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.test.util.ReflectionTestUtils;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static se.inera.intyg.rehabstod.auth.RehabstodAuthenticationSuccessHandler.SELECTED_SAMBI_IDP;

@RunWith(MockitoJUnitRunner.class)
public class RehabstodAuthenticationSuccessHandlerTest {

    private RehabstodAuthenticationSuccessHandler testee = new RehabstodAuthenticationSuccessHandler("/");

    private HttpServletRequest req = mock(HttpServletRequest.class);
    private HttpServletResponse resp = mock(HttpServletResponse.class);
    private Authentication auth = mock(Authentication.class);
    private SAMLCredential credential = mock(SAMLCredential.class);

    @Before
    public void init() {
        ReflectionTestUtils.setField(testee, "defaultIdpEntityId", "default-idp");
    }

    @Test
    public void testSetsCookie() throws ServletException, IOException {
        when(auth.isAuthenticated()).thenReturn(true);
        when(credential.getRemoteEntityID()).thenReturn("other-idp");
        when(auth.getCredentials()).thenReturn(credential);
        testee.onAuthenticationSuccess(req, resp, auth);
        ArgumentCaptor<Cookie> captor = ArgumentCaptor.forClass(Cookie.class);

        verify(resp, times(1)).addCookie(captor.capture());
        assertEquals(SELECTED_SAMBI_IDP, captor.getValue().getName());
        assertEquals("/", captor.getValue().getPath());
    }
}
