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
package se.inera.intyg.rehabstod.service.monitoring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import se.inera.intyg.rehabstod.common.monitoring.util.LogMarkers;

@Service("webMonitoringLogService")
public class MonitoringLogServiceImpl implements MonitoringLogService {

    private static final Object SPACE = " ";
    private static final Logger LOG = LoggerFactory.getLogger(MonitoringLogService.class);

    @Override
    public void logUserLogin(String userHsaId, String authenticationScheme) {
        logEvent(MonitoringEvent.USER_LOGIN, userHsaId, authenticationScheme);
    }

    @Override
    public void logUserLogout(String userHsaId, String authenticationScheme) {
        logEvent(MonitoringEvent.USER_LOGOUT, userHsaId, authenticationScheme);
    }

    @Override
    public void logUserSessionExpired(String userHsaId, String authScheme) {
        logEvent(MonitoringEvent.USER_SESSION_EXPIRY, userHsaId, authScheme);
    }

    @Override
    public void logMissingMedarbetarUppdrag(String userHsaId) {
        logEvent(MonitoringEvent.USER_MISSING_MIU, userHsaId);
    }

    @Override
    public void logMissingMedarbetarUppdrag(String userHsaId, String enhetsId) {
        logEvent(MonitoringEvent.USER_MISSING_MIU_ON_ENHET, userHsaId, enhetsId);
    }

    @Override
    public void logUserViewedSjukfall(String userHsaId, int numberOfSjukfall, String vardEnhet) {
        logEvent(MonitoringEvent.USER_VIEWED_SJUKFALL, userHsaId, numberOfSjukfall, vardEnhet);
    }

    private void logEvent(MonitoringEvent logEvent, Object... logMsgArgs) {
        LOG.info(LogMarkers.MONITORING, buildMessage(logEvent), logMsgArgs);
    }

    private String buildMessage(MonitoringEvent logEvent) {
        StringBuilder logMsg = new StringBuilder();
        logMsg.append(logEvent.name()).append(SPACE).append(logEvent.getMessage());
        return logMsg.toString();
    }

    private enum MonitoringEvent {
        USER_LOGIN("Login user '{}' using scheme '{}'"),
        USER_LOGOUT("Logout user '{}' using scheme '{}'"),
        USER_VIEWED_SJUKFALL("User '{}' viewed {} sjukfall on enhet '{}'"),
        USER_SESSION_EXPIRY("Session expired for user '{}' using scheme '{}'"),
        USER_MISSING_MIU("No valid MIU was found for user '{}'"),
        USER_MISSING_MIU_ON_ENHET("No valid MIU was found for user '{}' on unit '{}'");

        private String message;

        MonitoringEvent(String msg) {
            this.message = msg;
        }

        public String getMessage() {
            return message;
        }
    }
}
