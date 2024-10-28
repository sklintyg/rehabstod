/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.service.monitoring;

import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.monitoring.annotation.PrometheusTimeMethod;
import se.inera.intyg.infra.monitoring.logging.LogMarkers;
import se.inera.intyg.rehabstod.logging.MdcCloseableMap;
import se.inera.intyg.rehabstod.logging.MdcLogConstants;

@Service("webMonitoringLogService")
public class MonitoringLogServiceImpl implements MonitoringLogService {

    private static final Object SPACE = " ";
    private static final Logger LOG = LoggerFactory.getLogger(MonitoringLogService.class);

    @Override
    @PrometheusTimeMethod
    public void logUserLogin(String userHsaId, String role, String roleTypeName, String authenticationScheme, String origin) {
        try (MdcCloseableMap mdc =
            MdcCloseableMap.builder()
                .put(MdcLogConstants.EVENT_TYPE, toEventType(MonitoringEvent.USER_LOGIN))
                .put(MdcLogConstants.USER_ID, userHsaId)
                .put(MdcLogConstants.USER_ROLES, Arrays.toString(new String[]{role}))
                .put(MdcLogConstants.EVENT_AUTHENTICATION_SCHEME, authenticationScheme)
                .build()
        ) {
            logEvent(MonitoringEvent.USER_LOGIN, userHsaId, role, roleTypeName, authenticationScheme);
        }
    }

    @Override
    @PrometheusTimeMethod
    public void logUserLogout(String userHsaId, String authenticationScheme) {
        try (MdcCloseableMap mdc =
            MdcCloseableMap.builder()
                .put(MdcLogConstants.EVENT_TYPE, toEventType(MonitoringEvent.USER_LOGOUT))
                .put(MdcLogConstants.USER_ID, userHsaId)
                .put(MdcLogConstants.EVENT_AUTHENTICATION_SCHEME, authenticationScheme)
                .build()
        ) {
            logEvent(MonitoringEvent.USER_LOGOUT, userHsaId, authenticationScheme);
        }
    }

    @Override
    @PrometheusTimeMethod
    public void logUserSessionExpired(String userHsaId, String authScheme) {
        try (MdcCloseableMap mdc =
            MdcCloseableMap.builder()
                .put(MdcLogConstants.EVENT_TYPE, toEventType(MonitoringEvent.USER_SESSION_EXPIRY))
                .put(MdcLogConstants.USER_ID, userHsaId)
                .put(MdcLogConstants.EVENT_AUTHENTICATION_SCHEME, authScheme)
                .build()
        ) {
            logEvent(MonitoringEvent.USER_SESSION_EXPIRY, userHsaId, authScheme);
        }
    }

    @Override
    @PrometheusTimeMethod
    public void logMissingMedarbetarUppdrag(String userHsaId) {
        try (MdcCloseableMap mdc =
            MdcCloseableMap.builder()
                .put(MdcLogConstants.EVENT_TYPE, toEventType(MonitoringEvent.USER_MISSING_MIU))
                .put(MdcLogConstants.USER_ID, userHsaId)
                .build()
        ) {
            logEvent(MonitoringEvent.USER_MISSING_MIU, userHsaId);
        }
    }

    @Override
    @PrometheusTimeMethod
    public void logMissingMedarbetarUppdrag(String userHsaId, String enhetsId) {
        try (MdcCloseableMap mdc =
            MdcCloseableMap.builder()
                .put(MdcLogConstants.EVENT_TYPE, toEventType(MonitoringEvent.USER_MISSING_MIU_ON_ENHET))
                .put(MdcLogConstants.USER_ID, userHsaId)
                .put(MdcLogConstants.ORGANIZATION_ID, enhetsId)
                .build()
        ) {
            logEvent(MonitoringEvent.USER_MISSING_MIU_ON_ENHET, userHsaId, enhetsId);
        }
    }

    @Override
    @PrometheusTimeMethod
    public void logUserViewedSjukfall(String userHsaId, int numberOfSjukfall, String vardEnhet) {
        try (MdcCloseableMap mdc =
            MdcCloseableMap.builder()
                .put(MdcLogConstants.EVENT_TYPE, toEventType(MonitoringEvent.USER_VIEWED_SJUKFALL))
                .put(MdcLogConstants.USER_ID, userHsaId)
                .put(MdcLogConstants.ORGANIZATION_ID, vardEnhet)
                .build()
        ) {
            logEvent(MonitoringEvent.USER_VIEWED_SJUKFALL, userHsaId, numberOfSjukfall, vardEnhet);
        }
    }

    @Override
    public void logBrowserInfo(String browserName, String browserVersion, String osFamily, String osVersion, String width, String height) {
        logEvent(MonitoringEvent.BROWSER_INFO, browserName, browserVersion, osFamily, osVersion, width, height);
    }

    @Override
    public void logSamlStatusForFailedLogin(String issuer, String samlStatus) {
        logEvent(MonitoringEvent.SAML_STATUS_LOGIN_FAIL, issuer, samlStatus);
    }

    @Override
    public void logClientError(String errorId, String errorCode, String message, String stackTrace) {
        try (MdcCloseableMap mdc =
            MdcCloseableMap.builder()
                .put(MdcLogConstants.EVENT_TYPE, toEventType(MonitoringEvent.CLIENT_ERROR))
                .put(MdcLogConstants.ERROR_ID, errorId)
                .put(MdcLogConstants.ERROR_CODE, errorCode)
                .put(MdcLogConstants.ERROR_MESSAGE, message)
                .put(MdcLogConstants.ERROR_STACK_TRACE, stackTrace)
                .build()
        ) {
            logEvent(MonitoringEvent.CLIENT_ERROR, errorId, errorCode, message, stackTrace);
        }
    }

    private void logEvent(MonitoringEvent logEvent, Object... logMsgArgs) {
        LOG.info(LogMarkers.MONITORING, buildMessage(logEvent), logMsgArgs);
    }

    private String buildMessage(MonitoringEvent logEvent) {
        final var logMsg = new StringBuilder();
        logMsg.append(logEvent.name()).append(SPACE).append(logEvent.getMessage());
        return logMsg.toString();
    }

    private String toEventType(MonitoringEvent monitoringEvent) {
        return monitoringEvent.name().toLowerCase().replace("_", "-");
    }

    private enum MonitoringEvent {
        USER_LOGIN("Login user '{}' as role '{}' roleTypeName '{}' using scheme '{}'"),
        USER_LOGOUT("Logout user '{}' using scheme '{}'"),
        USER_VIEWED_SJUKFALL("User '{}' viewed {} sjukfall on enhet '{}'"),
        USER_SESSION_EXPIRY("Session expired for user '{}' using scheme '{}'"),
        USER_MISSING_MIU("No valid MIU was found for user '{}'"),
        USER_MISSING_MIU_ON_ENHET("No valid MIU was found for user '{}' on unit '{}'"),
        BROWSER_INFO("Name '{}' Version '{}' OSFamily '{}' OSVersion '{}' Width '{}' Height '{}'"),

        SAML_STATUS_LOGIN_FAIL("Login failed at IDP '{}' with status message '{}'"),
        CLIENT_ERROR(
            "Received error from client with errorId '{}' with error code '{}', message '{}' and stacktrace '{}'");

        private final String message;

        MonitoringEvent(String msg) {
            this.message = msg;
        }

        public String getMessage() {
            return message;
        }
    }
}
