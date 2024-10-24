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
package se.inera.intyg.rehabstod.web.controller.api;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import se.inera.intyg.infra.security.filter.SessionTimeoutFilter;
import se.inera.intyg.rehabstod.web.controller.api.dto.GetSessionStatusResponse;

/**
 * Reports basic information about the current session status.
 * This controller works in cooperation with SessionTimeoutFilter that makes sure that requests to:
 * <ul>
 * <li>getSessionStatus does NOT extend the session</li>
 * <li>getExtendSession does extend the session.</li>
 * </ul>
 *
 * @see SessionTimeoutFilter
 * @see org.springframework.security.web.context.SecurityContextRepository SecurityContextRepository
 * @see org.springframework.security.web.context.HttpSessionSecurityContextRepository
 * HttpSessionSecurityContextRepository
 */

@RestController
@RequestMapping(SessionStatusController.SESSION_STATUS_REQUEST_MAPPING)
public class SessionStatusController {

    public static final String SESSION_STATUS_REQUEST_MAPPING = "/api/session-auth-check";
    public static final String SESSION_STATUS_PING = "/ping";
    public static final String SESSION_STATUS_EXTEND = "/extend";

    public static final String SESSION_STATUS_CHECK_URI = SESSION_STATUS_REQUEST_MAPPING + SESSION_STATUS_PING;

    @RequestMapping(value = SessionStatusController.SESSION_STATUS_PING, method = RequestMethod.GET)
    public GetSessionStatusResponse getSessionStatus(HttpServletRequest request) {
        return createStatusResponse(request);
    }

    private GetSessionStatusResponse createStatusResponse(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        // The sessionTimeoutFilter should have put a secondsLeft attribute in the request for us to use.
        Long secondsLeft = (Long) request.getAttribute(SessionTimeoutFilter.SECONDS_UNTIL_SESSIONEXPIRE_ATTRIBUTE_KEY);

        return new GetSessionStatusResponse(session != null, hasAuthenticatedPrincipalSession(session),
            secondsLeft == null ? 0 : secondsLeft);
    }

    private boolean hasAuthenticatedPrincipalSession(HttpSession session) {
        if (session != null) {
            final Object context = session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
            if (context != null && context instanceof SecurityContext) {
                SecurityContext securityContext = (SecurityContext) context;
                return securityContext.getAuthentication() != null && securityContext.getAuthentication().getPrincipal() != null;
            }

        }
        return false;
    }

}
