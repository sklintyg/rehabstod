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
package se.inera.intyg.rehabstod.auth.util;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static se.inera.intyg.rehabstod.auth.util.SystemRolesParser.HSA_SYSTEMROLE_REHAB_UNIT_PREFIX;

public class SystemRolesParserTest {

    @Test
    public void testParseNullList() {
        List<String> enhetIds = SystemRolesParser.parseEnhetsIdsFromSystemRoles(null);
        assertEquals(0, enhetIds.size());
    }

    @Test
    public void testParseEmptyList() {
        List<String> enhetIds = SystemRolesParser.parseEnhetsIdsFromSystemRoles(new ArrayList<>());
        assertEquals(0, enhetIds.size());
    }

    @Test
    public void testParseSingleValidRole() {
        List<String> enhetIds = SystemRolesParser.parseEnhetsIdsFromSystemRoles(Arrays.asList(HSA_SYSTEMROLE_REHAB_UNIT_PREFIX + "TSTNMT-ABC-123"));
        assertEquals(1, enhetIds.size());
        assertEquals("TSTNMT-ABC-123", enhetIds.get(0));
    }

    @Test
    public void testParseTwoValidRoles() {
        List<String> enhetIds = SystemRolesParser.parseEnhetsIdsFromSystemRoles(Arrays.asList(HSA_SYSTEMROLE_REHAB_UNIT_PREFIX + "TSTNMT-ABC-123", HSA_SYSTEMROLE_REHAB_UNIT_PREFIX + "TSTNMT-ABC-124"));
        assertEquals(2, enhetIds.size());
        assertEquals("TSTNMT-ABC-123", enhetIds.get(0));
        assertEquals("TSTNMT-ABC-124", enhetIds.get(1));
    }
}
