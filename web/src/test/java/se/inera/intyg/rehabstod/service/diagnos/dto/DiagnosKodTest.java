/*
 * Copyright (C) 2023 Inera AB (http://www.inera.se)
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

import org.junit.Test;

/**
 * @author Magnus Ekstrand on 2017-02-20.
 */
public class DiagnosKodTest {

    @Test
    public void testCleanDignosKod() {
        DiagnosKod diagnosKod = new DiagnosKod("M80-   Diagnos", false);
        assertEquals("M80-", diagnosKod.getCode());
        assertEquals("Diagnos", diagnosKod.getName());

        diagnosKod = new DiagnosKod("M80-P  Diagnos", false);
        assertEquals("M80-P", diagnosKod.getCode());
        assertEquals("Diagnos", diagnosKod.getName());

        diagnosKod = new DiagnosKod("M80*\t Diagnos", false);
        assertEquals("M80", diagnosKod.getCode());
        assertEquals("Diagnos", diagnosKod.getName());

        diagnosKod = new DiagnosKod("M80†\t Diagnos", false);
        assertEquals("M80", diagnosKod.getCode());
        assertEquals("Diagnos", diagnosKod.getName());

        diagnosKod = new DiagnosKod("\uFEFFM80*\tDiagnos", false);
        assertEquals("\uFEFFM80", diagnosKod.getCode());
        assertEquals("Diagnos", diagnosKod.getName());

        diagnosKod = new DiagnosKod("\uFEFFM80*\t Diagnos", true);
        assertEquals("M80", diagnosKod.getCode());
        assertEquals("Diagnos", diagnosKod.getName());

        diagnosKod = new DiagnosKod("\uFEFFM80\t†\t Diagnos", true);
        assertEquals("M80", diagnosKod.getCode());
        assertEquals("Diagnos", diagnosKod.getName());

        diagnosKod = new DiagnosKod("M80-   Diagnos", true);
        assertEquals("M80-", diagnosKod.getCode());
        assertEquals("Diagnos", diagnosKod.getName());
    }

    @Test
    public void testSplitNormalDignosKod() {
        DiagnosKod kod = new DiagnosKod("M123   Palindrom reumatism", false);
        assertEquals("M123", kod.getCode());
        assertEquals("Palindrom reumatism", kod.getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyDiagnosKod() {
        se.inera.intyg.infra.sjukfall.dto.DiagnosKod kod = new se.inera.intyg.infra.sjukfall.dto.DiagnosKod("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullDiagnosKod() {
        se.inera.intyg.infra.sjukfall.dto.DiagnosKod kod = new se.inera.intyg.infra.sjukfall.dto.DiagnosKod(null);
    }

}
