/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.google.common.collect.ImmutableMap;
import java.security.Principal;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import se.inera.intyg.infra.security.common.model.Role;
import se.inera.intyg.rehabstod.service.monitoring.MonitoringLogService;

/**
 * Created by marced on 13/04/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class LoggingSessionRegistryImplTest {

    private static final String SESSION_ID = "sessionId";
    private static final String ROLE_TYPE_NAME = "Läkare - AT";
    @Mock
    private MonitoringLogService monitoringService;

    @InjectMocks
    private LoggingSessionRegistryImpl testee;

    RehabstodUser user;

    private Principal customPrincipal = (Principal) () -> "I'm not a real Principal";

    private Map<String, Role> roles = ImmutableMap.of("LAKARE", new Role());
    @Before
    public void before() {
        user = new RehabstodUser("hsaId", "En Användare", false);
        user.setRoles(roles);
        user.setRoleTypeName(ROLE_TYPE_NAME);
        user.setAuthenticationScheme("my:auth");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterNewSessionNullPrincipal() throws Exception {
        testee.registerNewSession(SESSION_ID, null);
    }

    @Test
    public void testRegisterNewSessionUnknownPrincipalType() throws Exception {
        testee.registerNewSession(SESSION_ID, customPrincipal);
        verifyNoMoreInteractions(monitoringService);
    }

    @Test
    public void testRegisterNewSession() throws Exception {
        testee.registerNewSession(SESSION_ID, user);
        verify(monitoringService).logUserLogin(user.getHsaId(), user.getRoles().keySet().iterator().next(), user.getRoleTypeName(),
            user.getAuthenticationScheme(), user.getOrigin());
    }

    @Test
    public void testRemoveSessionInformation() throws Exception {
        testee.registerNewSession(SESSION_ID, user);
        testee.removeSessionInformation(SESSION_ID);
        verify(monitoringService).logUserLogout(user.getHsaId(), user.getAuthenticationScheme());
    }

    @Test
    public void testRemoveSessionInformationNoSessionExist() throws Exception {
        testee.removeSessionInformation("unknownsessionid");
        verifyNoMoreInteractions(monitoringService);
    }

    @Test
    public void testRemoveSessionInformationUnknownPrincipalType() throws Exception {
        testee.registerNewSession(SESSION_ID, customPrincipal);
        testee.removeSessionInformation(SESSION_ID);
        verifyNoMoreInteractions(monitoringService);
    }
}
