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
package se.inera.intyg.rehabstod.auth.authorities;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.inera.intyg.rehabstod.infrastructure.security.authorities.AuthoritiesResolverUtil;
import se.inera.intyg.rehabstod.infrastructure.security.common.model.Privilege;
import se.inera.intyg.rehabstod.infrastructure.security.common.model.Role;

/** Created by marced on 13/04/16. */
class AuthoritiesResolverUtilTest {

  private static final String KEY = "1";
  private static final String VALUE = "2";
  Role role = new Role();

  @BeforeEach
  void before() {

    role.setName("ROLENAME");
    List<Privilege> privileges = new ArrayList<>();
    Privilege p = new Privilege();
    p.setName("PRIVILEGE");
    privileges.add(p);
    role.setPrivileges(privileges);
  }

  @Test
  void testRoleToMap() {
    // Act
    final Map<String, Role> stringRoleMap = AuthoritiesResolverUtil.toMap(role);

    // Assert
    assertEquals(1, stringRoleMap.size());
    assertEquals(stringRoleMap.get(role.getName()), role);
  }

  @Test
  void testPrivilegesToMapWithNullElementInList() {
    // Act
    final Map<String, Privilege> stringPrivilegeMap =
        AuthoritiesResolverUtil.toMap(Arrays.asList(new Privilege(), null), Privilege::getName);

    // Assert
    assertEquals(1, stringPrivilegeMap.size());
  }

  @Test
  void testPrivilegesToMap() {
    // Act
    final Map<String, Privilege> stringPrivilegeMap =
        AuthoritiesResolverUtil.toMap(role.getPrivileges(), Privilege::getName);

    // Assert
    assertEquals(1, stringPrivilegeMap.size());
    assertEquals(
        stringPrivilegeMap.get(role.getPrivileges().get(0).getName()), role.getPrivileges().get(0));
  }

  @Test
  void testToList() {
    // Arrange
    Map<String, Object> map = new HashMap<>();
    map.put(KEY, VALUE);

    // Act
    final List<Object> objects = AuthoritiesResolverUtil.toList(map);

    // Assert
    assertEquals(1, objects.size());
    assertEquals(VALUE, objects.get(0));
  }

  @Test
  void testToArray() {
    // Arrange
    Map<String, Object> map = new HashMap<>();
    map.put(KEY, VALUE);

    // Act
    final String[] strings = AuthoritiesResolverUtil.toArray(map);

    // Assert
    assertEquals(1, strings.length);
    assertEquals(KEY, strings[0]);
  }
}
