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
package se.inera.intyg.rehabstod.service.diagnos.dto;

import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Created by marced on 08/02/16.
 */
public class DiagnosKapitelTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

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
    public void testBadFormatConstructor() {
        thrown.expect(IllegalArgumentException.class);
        DiagnosKapitel interval = new DiagnosKapitel("A00-D8En grupp av diagnoser");

    }
}
