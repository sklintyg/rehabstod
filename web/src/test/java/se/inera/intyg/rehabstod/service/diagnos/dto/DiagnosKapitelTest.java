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
package se.inera.intyg.rehabstod.service.diagnos.dto;

import java.util.Optional;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by marced on 08/02/16.
 */
//CHECKSTYLE:OFF MagicNumber
public class DiagnosKapitelTest {

    @Test
    public void testConstructor() {
        DiagnosKapitel interval = new DiagnosKapitel("A00-D88En grupp av diagnoser");
        assertEquals("En grupp av diagnoser", interval.getName());
        assertEquals("A", String.valueOf(interval.getFrom().getLetter()));
        final int expectedFrom = 0;
        assertEquals(expectedFrom, interval.getFrom().getNumber());
        assertEquals("D", String.valueOf(interval.getTo().getLetter()));
        final int expectedTo = 88;
        assertEquals(expectedTo, interval.getTo().getNumber());
        assertEquals("A00-D88", interval.getId());

    }
    @Test
    public void testConstructorWithoutName() {
        DiagnosKapitel interval = new DiagnosKapitel("A00-D88");
        assertTrue(interval.getName().isEmpty());
        assertEquals("A", String.valueOf(interval.getFrom().getLetter()));
        final int expectedFrom = 0;
        assertEquals(expectedFrom, interval.getFrom().getNumber());
        assertEquals("D", String.valueOf(interval.getTo().getLetter()));
        final int expectedTo = 88;
        assertEquals(expectedTo, interval.getTo().getNumber());
        assertEquals("A00-D88", interval.getId());

    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadFormatConstructor() {
        DiagnosKapitel interval = new DiagnosKapitel("A00-D8En grupp av diagnoser");
    }

    @Test
    public void testIncludes() {
        DiagnosKapitel kapitel = new DiagnosKapitel("B00-D88En grupp av diagnoser");

        assertTrue(kapitel.includes(Optional.of(new DiagnosKategori('B', 0))));
        assertTrue(kapitel.includes(Optional.of(new DiagnosKategori('B', 1))));
        assertTrue(kapitel.includes(Optional.of(new DiagnosKategori('C', 12))));
        assertTrue(kapitel.includes(Optional.of(new DiagnosKategori('D', 88))));

        assertFalse(kapitel.includes(Optional.of(new DiagnosKategori('A', 0))));
        assertFalse(kapitel.includes(Optional.of(new DiagnosKategori('D', 89))));
        assertFalse(kapitel.includes(Optional.of(new DiagnosKategori('E', 12))));
        assertFalse(kapitel.includes(Optional.empty()));

    }
}
