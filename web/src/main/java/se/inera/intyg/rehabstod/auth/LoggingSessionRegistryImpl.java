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
package se.inera.intyg.rehabstod.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistryImpl;

import se.inera.intyg.rehabstod.service.monitoring.MonitoringLogService;

/**
 * Implementation of SessionRegistry that performs audit logging of login and logout.
 */
public class LoggingSessionRegistryImpl extends SessionRegistryImpl {
    @Autowired
    @Qualifier("webMonitoringLogService")
    private MonitoringLogService monitoringService;

    @Override
    public void registerNewSession(String sessionId, Object principal) {
        if (principal != null && principal instanceof RehabstodUser) {
            RehabstodUser user = (RehabstodUser) principal;
            monitoringService.logUserLogin(user.getHsaId(), user.getAuthenticationScheme(), user.getOrigin());
        }
        super.registerNewSession(sessionId, principal);
    }

    @Override
    public void removeSessionInformation(String sessionId) {
        SessionInformation sessionInformation = getSessionInformation(sessionId);
        if (sessionInformation != null) {
            Object principal = sessionInformation.getPrincipal();

            if (principal instanceof RehabstodUser) {
                RehabstodUser user = (RehabstodUser) principal;
                if (sessionInformation.isExpired()) {
                    monitoringService.logUserSessionExpired(user.getHsaId(), user.getAuthenticationScheme());
                } else {
                    monitoringService.logUserLogout(user.getHsaId(), user.getAuthenticationScheme());
                }
            }
        }
        super.removeSessionInformation(sessionId);
    }
}
