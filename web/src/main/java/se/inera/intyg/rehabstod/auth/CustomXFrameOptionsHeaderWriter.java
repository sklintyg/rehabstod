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

import static org.springframework.security.web.server.header.XFrameOptionsServerHttpHeadersWriter.X_FRAME_OPTIONS;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.web.header.HeaderWriter;

public class CustomXFrameOptionsHeaderWriter implements HeaderWriter {

    private static final String PDF_API_IDENTIFIER = "/pdf";
    private static final String IE_USER_AGENT_REGEX = ".*Trident/\\d+.*|.*MSIE \\d+.*";

    @Override
    public void writeHeaders(HttpServletRequest request, HttpServletResponse response) {
        if (isPdfRequestFromNonIEBrowser(request)) {
            response.setHeader(X_FRAME_OPTIONS, "SAMEORIGIN");
        } else {
            response.setHeader(X_FRAME_OPTIONS, "DENY");
        }
    }

    private boolean isPdfRequestFromNonIEBrowser(HttpServletRequest request) {
        final var isPdfRequest = request.getRequestURI().contains(PDF_API_IDENTIFIER);
        if (isPdfRequest) {
            return isNonIEBrowser(request);
        }
        return false;
    }

    private boolean isNonIEBrowser(HttpServletRequest request) {
        final var userAgent = request.getHeader(HttpHeaders.USER_AGENT);
        return !userAgent.matches(IE_USER_AGENT_REGEX);
    }
}
