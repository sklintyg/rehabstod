/*
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.service.diagnos.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Created by marced on 14/03/16.
 */
public class DiagnosGruppTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testConstructor() {
        DiagnosKapitel expected = new DiagnosKapitel("A00-D88");

        DiagnosGrupp grupp = new DiagnosGrupp(expected.getId() + ":En grupp av diagnoser");
        assertEquals("En grupp av diagnoser", grupp.getName());
        assertEquals("A00-D88", grupp.getId());

        assertTrue(grupp.getKapitelList().contains(expected));
    }

    @Test
    public void testConstructorMultipleKapitels() {
        DiagnosKapitel expected1 = new DiagnosKapitel("A00-D88");
        DiagnosKapitel expected2 = new DiagnosKapitel("B00-B67");
        DiagnosKapitel expected3 = new DiagnosKapitel("C00-C99");

        DiagnosGrupp grupp = new DiagnosGrupp("A00-D88,B00-B67,C00-C99:En grupp av diagnoser");
        assertEquals("En grupp av diagnoser", grupp.getName());
        assertEquals("A00-D88,B00-B67,C00-C99", String.valueOf(grupp.getId()));
        assertEquals(3, grupp.getKapitelList().size());
        assertTrue(grupp.getKapitelList().containsAll(Arrays.asList(expected1, expected2, expected3)));
    }

    @Test
    public void testBadFormatConstructor() {
        thrown.expect(IllegalArgumentException.class);
        DiagnosKapitel grupp = new DiagnosKapitel("A00 D00En grupp av diagnoser");

    }

    @Test
    public void testInclude() {
        DiagnosGrupp grupp = new DiagnosGrupp("A00-D88,F00-V67:En saling blandning av diagnoser");

        assertTrue(grupp.includes("A22"));
        assertTrue(grupp.includes("B22"));
        assertTrue(grupp.includes("F22"));
        assertTrue(grupp.includes("F6765"));
        assertTrue(grupp.includes("O999999"));
        assertTrue(grupp.includes("V67"));

        assertFalse(grupp.includes("E22"));
        assertFalse(grupp.includes("V68"));
        assertFalse(grupp.includes("Z2222"));

    }

}
