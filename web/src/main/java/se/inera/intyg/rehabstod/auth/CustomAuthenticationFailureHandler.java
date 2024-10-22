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
package se.inera.intyg.rehabstod.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.ExceptionMappingAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import se.inera.intyg.infra.security.exception.HsaServiceException;
import se.inera.intyg.infra.security.exception.MissingMedarbetaruppdragException;
import se.inera.intyg.rehabstod.auth.exceptions.MissingUnitWithRehabSystemRoleException;

@Component
@Slf4j
public class CustomAuthenticationFailureHandler extends ExceptionMappingAuthenticationFailureHandler {

    private static final String DEFAULT_FAILURE_URL = "/error/login-failed";

    private final Map<String, String> failureUrls = Map.of(
        BadCredentialsException.class.getName(), DEFAULT_FAILURE_URL,
        MissingMedarbetaruppdragException.class.getName(), "/error/login-medarbetaruppdrag",
        MissingUnitWithRehabSystemRoleException.class.getName(), "/error/login-saknar-hsa-rehabroll",
        HsaServiceException.class.getName(), "/error/login-hsaerror"
    );

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
        throws IOException {
        final var exceptionName = exception.getClass().getName();

        String url;
        if (failureUrls.containsKey(exceptionName)) {
            url = failureUrls.get(exceptionName);
        } else {
            saveException(request, exception);
            url = DEFAULT_FAILURE_URL;
        }

        log.error("Authentication failed!", exception);

        getRedirectStrategy().sendRedirect(request, response, url);
    }
}
