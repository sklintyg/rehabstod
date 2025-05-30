/*
 * Copyright (C) 2025 Inera AB (http://www.inera.se)
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

import se.inera.intyg.infra.security.common.service.AuthenticationLogger;

/**
 * Interface used when logging to monitoring file. Used to ensure that the log entries are uniform and easy to parse.
 */
public interface MonitoringLogService extends AuthenticationLogger {

    @Override
    void logUserLogin(String id, String role, String roleTypeName, String authenticationScheme, String origin);

    @Override
    void logUserLogout(String id, String authenticationScheme);

    void logUserViewedSjukfall(String userId, int numberOfSjukfall, String vardEnhet);

    void logBrowserInfo(String browserName, String browserVersion, String osFamily, String osVersion, String width, String height);

    // Saml
    void logSamlStatusForFailedLogin(String issuer, String samlStatus);

    void logClientError(String errorId, String errorCode, String message, String stackTrace);
}
