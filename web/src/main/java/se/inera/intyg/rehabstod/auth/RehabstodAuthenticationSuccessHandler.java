/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * Customized authentication success handler that sets a Cookie with the EntityID of the IdP used for this login.
 *
 * The cookie is set only if the selected IdP is NOT the defaultIDP.
 *
 * @author eriklupander
 */
public class RehabstodAuthenticationSuccessHandler extends
        SimpleUrlAuthenticationSuccessHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(RehabstodAuthenticationSuccessHandler.class);
    private static final int MAX_AGE = 999999999;
    static final String SELECTED_SAMBI_IDP = "selectedSambiIdp";

    @Value("${sakerhetstjanst.saml.idp.metadata.url}")
    private String defaultIdpEntityId;

    public RehabstodAuthenticationSuccessHandler(String defaultTargetUrl) {
        super(defaultTargetUrl);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
            HttpServletResponse response, Authentication authentication)
            throws ServletException, IOException {
        // If the IdP used was OTHER than our default IDP, stuff a Set-Cookie with the EntityID of the IdP
        // into the response.
        // This MUST be done prior to calling super.
        if (authentication.isAuthenticated() && authentication.getCredentials() instanceof SAMLCredential) {
            String remoteEntityId = ((SAMLCredential) authentication.getCredentials()).getRemoteEntityID();

            if (remoteEntityId != null) {

                if (!defaultIdpEntityId.equals(remoteEntityId)) {
                    LOGGER.info("User logged in using SAMBI, setting cookie: selectedSambiIdp={}", remoteEntityId);
                    Cookie cookie = new Cookie(SELECTED_SAMBI_IDP, URLEncoder.encode(remoteEntityId, "UTF-8"));
                    cookie.setVersion(0);
                    cookie.setHttpOnly(false);
                    cookie.setMaxAge(MAX_AGE);
                    cookie.setPath("/");
                    response.addCookie(cookie);
                }
            } else {
                LOGGER.warn("Unable to set Cookie for selectedSambiIdp, could not extract remoteEntityID from the SAML credential.");
            }
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
