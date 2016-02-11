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
package se.inera.intyg.rehabstod.web.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by martin on 11/02/16.
 */
public class GenderTest {


    @Test
    public void testGetMale() {
        assertEquals(Gender.M, Gender.getGenderFromString("1"));
        assertEquals(Gender.M, Gender.getGenderFromString("3"));
        assertEquals(Gender.M, Gender.getGenderFromString("5"));
        assertEquals(Gender.M, Gender.getGenderFromString("7"));
        assertEquals(Gender.M, Gender.getGenderFromString("9"));
    }

    @Test
    public void testGetFemale() {
        assertEquals(Gender.F, Gender.getGenderFromString("0"));
        assertEquals(Gender.F, Gender.getGenderFromString("2"));
        assertEquals(Gender.F, Gender.getGenderFromString("4"));
        assertEquals(Gender.F, Gender.getGenderFromString("6"));
        assertEquals(Gender.F, Gender.getGenderFromString("8"));
    }

    @Test
    public void testGetNull() {
        assertNull(Gender.getGenderFromString("A"));
        assertNull(Gender.getGenderFromString(""));
        assertNull(Gender.getGenderFromString(null));
        assertNull(Gender.getGenderFromString("+"));
        assertNull(Gender.getGenderFromString("12"));
    }
}
