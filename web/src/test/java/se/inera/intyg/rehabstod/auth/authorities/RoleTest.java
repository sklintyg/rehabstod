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
package se.inera.intyg.rehabstod.auth.authorities;

import org.junit.Test;
import se.inera.intyg.common.security.common.model.Privilege;
import se.inera.intyg.common.security.common.model.Role;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by marced on 14/04/16.
 */
public class RoleTest {

    @Test
    public void testGetSetPrivileges() throws Exception {
        // Arrange
        Role role = new Role();
        role.setPrivileges(null);

        // Act / Assert
        assertNotNull(role.getPrivileges());

        List<Privilege> list = Arrays.asList(new Privilege());
        role.setPrivileges(list);

        // Act / Assert
        assertEquals(list, role.getPrivileges());
    }
}
