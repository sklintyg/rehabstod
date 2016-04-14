/**
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
 *
 * This file is part of rehabstod (https://github.com/sklintyg/rehabstod).
 *
 * rehabstod is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * rehabstod is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.intyg.rehabstod.auth.authorities.validation;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.auth.authorities.AuthoritiesException;
import se.inera.intyg.rehabstod.auth.authorities.Role;

import com.google.common.collect.ImmutableMap;

/**
 * Created by marced on 14/04/16.
 */
public class AuthExpectationSpecImplTest {

    private static final String LAKARE = "LAKARE";
    RehabstodUser user = new RehabstodUser("11111", "Dr Doctor");

    @Before
    public void setUp() throws Exception {
        user.setRoles(ImmutableMap.of(LAKARE, new Role()));

    }

    @Test
    public void testIsVerified() throws Exception {
        AuthExpectationSpecImpl authExpectationSpec = new AuthExpectationSpecImpl(user);
        assertTrue(authExpectationSpec.roles(LAKARE).isVerified());
        assertTrue(authExpectationSpec.notRoles("ANNAN").isVerified());
    }

    @Test(expected = AuthoritiesException.class)
    public void testOrThrowRole() throws Exception {
        AuthExpectationSpecImpl authExpectationSpec = new AuthExpectationSpecImpl(user);
        authExpectationSpec.roles("ANNAN").orThrow();
    }

    @Test(expected = AuthoritiesException.class)
    public void testOrThrowRoleNot() throws Exception {
        AuthExpectationSpecImpl authExpectationSpec = new AuthExpectationSpecImpl(user);
        authExpectationSpec.notRoles(LAKARE).orThrow();
    }
}
