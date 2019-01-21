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
