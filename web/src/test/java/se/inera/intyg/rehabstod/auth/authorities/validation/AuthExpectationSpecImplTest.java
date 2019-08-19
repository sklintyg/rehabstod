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
package se.inera.intyg.rehabstod.auth.authorities.validation;

import static org.junit.Assert.assertTrue;

import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import se.inera.intyg.infra.security.authorities.AuthoritiesException;
import se.inera.intyg.infra.security.authorities.validation.AuthExpectationSpecImpl;
import se.inera.intyg.infra.security.common.model.IntygUser;
import se.inera.intyg.infra.security.common.model.Role;
import se.inera.intyg.rehabstod.auth.RehabstodUser;

/**
 * Created by marced on 14/04/16.
 */
public class AuthExpectationSpecImplTest {

    private static final String LAKARE = "LAKARE";
    IntygUser user = new RehabstodUser("11111", "Dr Doctor", true);

    @Before
    public void setUp() throws Exception {
        user.setRoles(ImmutableMap.of(LAKARE, new Role()));

    }

    @Test
    public void testIsVerified() throws Exception {
        AuthExpectationSpecImpl authExpectationSpec = new AuthExpectationSpecImpl(user, null);
        assertTrue(authExpectationSpec.roles(LAKARE).isVerified());
        assertTrue(authExpectationSpec.notRoles("ANNAN").isVerified());
    }

    @Test(expected = AuthoritiesException.class)
    public void testOrThrowRole() throws Exception {
        AuthExpectationSpecImpl authExpectationSpec = new AuthExpectationSpecImpl(user, null);
        authExpectationSpec.roles("ANNAN").orThrow();
    }

    @Test(expected = AuthoritiesException.class)
    public void testOrThrowRoleNot() throws Exception {
        AuthExpectationSpecImpl authExpectationSpec = new AuthExpectationSpecImpl(user, null);
        authExpectationSpec.notRoles(LAKARE).orThrow();
    }
}
