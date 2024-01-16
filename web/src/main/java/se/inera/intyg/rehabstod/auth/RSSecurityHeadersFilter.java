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

import static org.springframework.security.web.server.header.StrictTransportSecurityServerHttpHeadersWriter.STRICT_TRANSPORT_SECURITY;
import static org.springframework.security.web.server.header.XFrameOptionsServerHttpHeadersWriter.X_FRAME_OPTIONS;
import static org.springframework.security.web.server.header.XXssProtectionServerHttpHeadersWriter.X_XSS_PROTECTION;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import se.inera.intyg.infra.security.filter.SecurityHeadersFilter;

public class RSSecurityHeadersFilter extends SecurityHeadersFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        response.setHeader(STRICT_TRANSPORT_SECURITY, "max-age=31536000 ; includeSubDomains");
        response.setHeader(X_XSS_PROTECTION, "1; mode=block");
        response.setHeader(X_FRAME_OPTIONS, "DENY");

        super.doFilterInternal(request, response, filterChain);
    }
}
