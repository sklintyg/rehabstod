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
package se.inera.intyg.rehabstod.auth.fake;

import java.io.Serial;
import java.util.Collections;
import java.util.Objects;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import se.inera.intyg.rehabstod.auth.RehabstodUser;

public class FakeAuthenticationToken extends AbstractAuthenticationToken {

    @Serial
    private static final long serialVersionUID = 1L;

    private final RehabstodUser rehabstodUser;

    public FakeAuthenticationToken(RehabstodUser rehabstodUser) {
        super(Collections.emptyList());
        this.rehabstodUser = rehabstodUser;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return rehabstodUser.getPersonId();
    }

    @Override
    public Object getPrincipal() {
        return rehabstodUser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        final var that = (FakeAuthenticationToken) o;
        return Objects.equals(rehabstodUser, that.rehabstodUser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), rehabstodUser);
    }
}
