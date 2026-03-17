/*
 * Copyright (C) 2026 Inera AB (http://www.inera.se)
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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.inera.intyg.rehabstod.security.authorities.AuthoritiesException;
import se.inera.intyg.rehabstod.security.authorities.validation.AuthExpectationSpecImpl;
import se.inera.intyg.rehabstod.security.common.model.IntygUser;
import se.inera.intyg.rehabstod.security.common.model.Role;
import se.inera.intyg.rehabstod.auth.RehabstodUser;

/** Created by marced on 14/04/16. */
class AuthExpectationSpecImplTest {

  private static final String LAKARE = "LAKARE";
  IntygUser user = new RehabstodUser("11111", "Dr Doctor", true);

  @BeforeEach
  void setUp() throws Exception {
    user.setRoles(ImmutableMap.of(LAKARE, new Role()));
  }

  @Test
  void testIsVerified() throws Exception {
    AuthExpectationSpecImpl authExpectationSpec = new AuthExpectationSpecImpl(user, null);
    assertTrue(authExpectationSpec.roles(LAKARE).isVerified());
    assertTrue(authExpectationSpec.notRoles("ANNAN").isVerified());
  }

  @Test
  void testOrThrowRole() throws Exception {
    assertThrows(
        AuthoritiesException.class,
        () -> {
          AuthExpectationSpecImpl authExpectationSpec = new AuthExpectationSpecImpl(user, null);
          authExpectationSpec.roles("ANNAN").orThrow();
        });
  }

  @Test
  void testOrThrowRoleNot() throws Exception {
    assertThrows(
        AuthoritiesException.class,
        () -> {
          AuthExpectationSpecImpl authExpectationSpec = new AuthExpectationSpecImpl(user, null);
          authExpectationSpec.notRoles(LAKARE).orThrow();
        });
  }
}
