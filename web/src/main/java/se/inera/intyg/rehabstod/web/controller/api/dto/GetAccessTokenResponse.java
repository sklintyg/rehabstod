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
package se.inera.intyg.rehabstod.web.controller.api.dto;

import se.inera.intyg.rehabstod.auth.RehabstodUserTokens;

/**
 * Reponse dto for the getAccessToken api.
 */
public class GetAccessTokenResponse {

    private String accessToken;

    public GetAccessTokenResponse(RehabstodUserTokens tokens) {
        this.accessToken = tokens != null ? tokens.getAccessToken() : null;
    }

    public String getAccessToken() {
        return this.accessToken;
    }
}
