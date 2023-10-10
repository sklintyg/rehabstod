/*
 * Copyright (C) 2023 Inera AB (http://www.inera.se)
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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.ExceptionMappingAuthenticationFailureHandler;
import se.inera.intyg.infra.security.exception.HsaServiceException;
import se.inera.intyg.infra.security.exception.MissingMedarbetaruppdragException;
import se.inera.intyg.rehabstod.auth.exceptions.MissingUnitWithRehabSystemRoleException;

public class RehabstodAuthenticationFailureHandler extends ExceptionMappingAuthenticationFailureHandler {

    // NOTES FOR CLEANUP - Keep the DEFAULT_FAILURE_URL, the map "failureUrls" and method "onAuthenticationFailureForReactOnly"
    // Rename method "onAuthenticationFailureForReactOnly" to "onAuthenticationFailure" and add an @Override annotation.
    // ------------------------------------------------------------------------------------------------------------------------
    private static final String DEFAULT_FAILURE_URL = "/error/login-failed";

    private final Map<String, String> failureUrls = Map.of(
        BadCredentialsException.class.getName(), DEFAULT_FAILURE_URL,
        MissingMedarbetaruppdragException.class.getName(), "/error/login-medarbetaruppdrag",
        MissingUnitWithRehabSystemRoleException.class.getName(), "/error/login-saknar-hsa-rehabroll",
        HsaServiceException.class.getName(), "/error/login-hsaerror"
    );

    private void onAuthenticationFailureForReactOnly(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException exception)
        throws IOException {
        final var exceptionName = exception.getClass().getName();

        String url;
        if (failureUrls.containsKey(exceptionName)) {
            url = failureUrls.get(exceptionName);
        } else {
            saveException(request, exception);
            url = DEFAULT_FAILURE_URL;
        }

        getRedirectStrategy().sendRedirect(request, response, url);
    }



    // NOTES FOR CLEANUP - REMOVE ALL CODE BELOW THIS COMMENT, REMOVE IMPORTS UNUSED AFTER REMOVAL
    //--------------------------------------------------------------------------------------------
    @Value("${siths.idp.url}")
    private String sithsIdpUrl;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
        throws IOException {

        if (sithsIdpUrl != null && sithsIdpUrl.contains("rs2")) {
            onAuthenticationFailureForReactAndAngular(request, response, exception);
        } else {
            onAuthenticationFailureForReactOnly(request, response, exception);
        }
    }

    private static final String BAD_CREDENTIALS = "badCredentials";
    private static final String MISSING_ASSIGNMENT = "missingAssignment";
    private static final String MISSING_UNIT_WITH_REHAB_SYSTEM_ROLE = "missingUnitWithRehabSystemRole";
    private static final String HSA_SERVICE = "hsaService";

    private final Map<String, String> authExceptions = Map.of(
        BAD_CREDENTIALS, BadCredentialsException.class.getName(),
        MISSING_ASSIGNMENT, MissingMedarbetaruppdragException.class.getName(),
        MISSING_UNIT_WITH_REHAB_SYSTEM_ROLE, MissingUnitWithRehabSystemRoleException.class.getName(),
        HSA_SERVICE, HsaServiceException.class.getName()
    );

    private static final String RS_IDENTIFIER = "rs";
    private static final String RS2_IDENTIFIER = "rs2";
    private static final String RS_DEFAULT_FAILURE_URL = "/error.jsp?reason=login.failed";
    private static final String RS2_DEFAULT_FAILURE_URL = "/error/login-failed";

    private final Map<String, String> rsFailureUrls;
    private final Map<String, String> rs2FailureUrls;
    private final Map<String, Map<String, String>> failureUrlMap = new HashMap<>();

    public RehabstodAuthenticationFailureHandler() {
        this.rsFailureUrls = getRsFailureUrls();
        this.rs2FailureUrls = getRs2FailureUrls();
    }

    @PostConstruct
    public void init() {
        mapExceptions();
    }

    private void onAuthenticationFailureForReactAndAngular(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException exception)
        throws IOException {
        final var failureUrlIdentifier = getFailureUrlIdentifier(request);
        final var exceptionName = exception.getClass().getName();

        String url;
        if (failureUrlMap.containsKey(exceptionName)) {
            url = failureUrlMap.get(exceptionName).get(failureUrlIdentifier);
        } else {
            saveException(request, exception);
            url = failureUrlIdentifier.equals(RS2_IDENTIFIER) ? RS2_DEFAULT_FAILURE_URL : RS_DEFAULT_FAILURE_URL;
        }

        getRedirectStrategy().sendRedirect(request, response, url);
    }

    private void mapExceptions() {
        this.failureUrlMap.clear();
        for (Map.Entry<String, String> entry : authExceptions.entrySet()) {
            final var rsFailureUrl = rsFailureUrls.get(entry.getKey());
            final var rs2FailureUrl = rs2FailureUrls.get(entry.getKey());
            final var failUrls = Map.of(
                RS_IDENTIFIER, rsFailureUrl != null ? rsFailureUrl : RS_DEFAULT_FAILURE_URL,
                RS2_IDENTIFIER, rs2FailureUrl != null ? rs2FailureUrl : RS2_DEFAULT_FAILURE_URL);
            this.failureUrlMap.put(entry.getValue(), failUrls);
        }
    }

    private String getFailureUrlIdentifier(HttpServletRequest request) {
        final var requestUri = request.getRequestURI();
        final var servletPath = request.getServletPath();
        final var xForwardedHost = request.getHeader("x-forwarded-host");

        if ((requestUri != null && requestUri.endsWith("-rs2"))
            || (servletPath != null && servletPath.endsWith("-rs2"))
            || (xForwardedHost != null && (xForwardedHost.startsWith("rs2.") || xForwardedHost.startsWith("rs2-")))) {
            return RS2_IDENTIFIER;
        }

        return RS_IDENTIFIER;
    }

    private Map<String, String> getRsFailureUrls() {
        return Map.of(
            BAD_CREDENTIALS, RS_DEFAULT_FAILURE_URL,
            MISSING_ASSIGNMENT, "/error.jsp?reason=login.medarbetaruppdrag",
            MISSING_UNIT_WITH_REHAB_SYSTEM_ROLE, "/error.jsp?reason=login.saknar-hsa-rehabroll",
            HSA_SERVICE, "/error.jsp?reason=login.hsaerror"
        );
    }

    private Map<String, String> getRs2FailureUrls() {
        return Map.of(
            BAD_CREDENTIALS, RS2_DEFAULT_FAILURE_URL,
            MISSING_ASSIGNMENT, "/error/login-medarbetaruppdrag",
            MISSING_UNIT_WITH_REHAB_SYSTEM_ROLE, "/error/login-saknar-hsa-rehabroll",
            HSA_SERVICE, "/error/login-hsaerror"
        );
    }
}
