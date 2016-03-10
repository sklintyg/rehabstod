/**
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
 *
 * This file is part of rehabstod (https://github.com/sklintyg/rehabstod).
 *
 * rehabstod is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * rehabstod is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.intyg.rehabstod.web.controller.api;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.web.context.SecurityContextRepository;

import se.inera.intyg.rehabstod.web.controller.api.dto.GetSessionStatusResponse;

/**
 * Created by marced on 09/03/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class SessionStatusControllerTest {

    @Mock
    SecurityContextRepository repository;

    @Mock
    HttpServletRequest request;

    @InjectMocks
    private SessionStatusController controller = new SessionStatusController();

    @Test
    public void testGetSessionStatus() throws Exception {
        // Arrange
        when(repository.containsContext(eq(request))).thenReturn(true);

        // Act
        final GetSessionStatusResponse sessionStatus = controller.getSessionStatus(request);

        // Assert
        assertFalse(sessionStatus.isHasSession());
        assertTrue(sessionStatus.isAuthenticated());
    }
}
