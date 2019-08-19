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
package se.inera.intyg.rehabstod.service.user;

import org.springframework.security.saml.SAMLCredential;
import se.inera.intyg.rehabstod.auth.RehabstodUserTokens;

public interface TokenExchangeService {

    /**
     * Given a SAMLCredential, exchange the underlying assertion for a JWT token.
     *
     * @param samlCredential SAML Credential
     * @return RehabstodUserTokens with access and refresh token.
     */
    RehabstodUserTokens exchange(SAMLCredential samlCredential);

    /**
     * Request new access token for a users given refresh token.
     *
     * @param tokens RehabstodUserTokens that includes a refresh token
     * @return A new RehabUserTokens with updated access token and expire time
     */
    RehabstodUserTokens refresh(RehabstodUserTokens tokens);
}
