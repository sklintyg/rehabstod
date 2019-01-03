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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.web.controller.api.dto.GetSessionStatusResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Created by marced on 09/03/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class SessionStatusControllerTest {

    @Mock
    HttpServletRequest request;

    @Mock
    SecurityContext context;

    @Mock
    Authentication authentication;

    @Mock
    HttpSession session;

    @InjectMocks
    private SessionStatusController controller = new SessionStatusController();

    @Test
    public void testGetSessionStatusOk() throws Exception {
        // Arrange
        when(request.getSession((false))).thenReturn(session);
        when(session.getAttribute(anyString())).thenReturn(context);
        when(context.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(new RehabstodUser("test", "test", true));

        // Act
        final GetSessionStatusResponse sessionStatus = controller.getSessionStatus(request);

        // Assert
        assertTrue(sessionStatus.isHasSession());
        assertTrue(sessionStatus.isAuthenticated());
    }

    @Test
    public void testGetSessionStatusNoSession() throws Exception {
        // Arrange
        when(request.getSession((false))).thenReturn(null);

        // Act
        final GetSessionStatusResponse sessionStatus = controller.getSessionStatus(request);

        // Assert
        assertFalse(sessionStatus.isHasSession());
        assertFalse(sessionStatus.isAuthenticated());
    }
}
