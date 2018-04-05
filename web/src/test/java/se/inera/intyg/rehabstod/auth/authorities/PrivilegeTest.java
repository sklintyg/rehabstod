/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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

import org.junit.Before;
import org.junit.Test;
import se.inera.intyg.infra.security.common.model.Privilege;
import se.inera.intyg.infra.security.common.model.RequestOrigin;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by marced on 13/04/16.
 */
public class PrivilegeTest {

    Privilege privilege;

    @Before
    public void before() {
        privilege = new Privilege();
    }

    @Test
    public void testGetSetIntygstyper() throws Exception {
        // Arrange
        privilege.setIntygstyper(null);

        // Act / Assert
        assertNotNull(privilege.getIntygstyper());

        List<String> list = Arrays.asList("1", "2");
        privilege.setIntygstyper(list);

        // Act / Assert
        assertEquals(list, privilege.getIntygstyper());
    }

    @Test
    public void testGetSetRequestOrigins() throws Exception {
        // Arrange
        privilege.setRequestOrigins(null);

        // Act / Assert
        assertNotNull(privilege.getRequestOrigins());

        List<RequestOrigin> list = Arrays.asList(new RequestOrigin());
        privilege.setRequestOrigins(list);

        // Act / Assert
        assertEquals(list, privilege.getRequestOrigins());
    }

}
