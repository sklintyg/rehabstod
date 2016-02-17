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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by martin on 10/02/16.
 */
public class DiagnosKodTest {


    @Test
    public void testSplitNormalDignosKod() {
        String line = "M123   Palindrom reumatism";

        DiagnosKod kod = new DiagnosKod(line);

        assertEquals("M123", kod.getId());
        assertEquals("Palindrom reumatism", kod.getName());
    }

    @Test
    public void testSplitToShortDiagnosKod() {
        String line = "M123 ";

        DiagnosKod kod = new DiagnosKod(line);

        assertNull(kod.getId());
        assertNull(kod.getName());
    }

    @Test
    public void testCleanDignosKod() {
        assertEquals("M80-", DiagnosKod.cleanKod("M80-   "));
        assertEquals("M80-P", DiagnosKod.cleanKod("M80-P  "));
        assertEquals("M80", DiagnosKod.cleanKod("M80.   "));
        assertEquals("M801", DiagnosKod.cleanKod("M80.1   "));
        assertEquals("M8001", DiagnosKod.cleanKod("M.80.01   "));
        assertEquals("A", DiagnosKod.cleanKod("aÅÄÖ)(/"));
    }
}
