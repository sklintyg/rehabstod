/*
 * Copyright (C) 2026 Inera AB (http://www.inera.se)
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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import se.inera.intyg.rehabstod.application.api.SessionStatusController;
import se.inera.intyg.rehabstod.application.api.dto.GetSessionStatusResponse;
import se.inera.intyg.rehabstod.infrastructure.security.auth.RehabstodUser;

/** Created by marced on 09/03/16. */
@ExtendWith(MockitoExtension.class)
class SessionStatusControllerTest {

  @Mock HttpServletRequest request;

  @Mock SecurityContext context;

  @Mock Authentication authentication;

  @Mock HttpSession session;

  @InjectMocks private SessionStatusController controller = new SessionStatusController();

  @Test
  void testGetSessionStatusOk() {
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
  void testGetSessionStatusNoSession() {
    // Arrange
    when(request.getSession((false))).thenReturn(null);

    // Act
    final GetSessionStatusResponse sessionStatus = controller.getSessionStatus(request);

    // Assert
    assertFalse(sessionStatus.isHasSession());
    assertFalse(sessionStatus.isAuthenticated());
  }
}
