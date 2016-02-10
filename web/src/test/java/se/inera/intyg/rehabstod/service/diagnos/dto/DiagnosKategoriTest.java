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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Optional;

/**
 * Created by marced on 09/02/16.
 */
public class DiagnosKategoriTest {

    private static final Optional<DiagnosKategori> EXPECTED_A = Optional.of(new DiagnosKategori('A', 22));

    @Test
    public void testExtractFromStringSuccesses() throws Exception {
        assertTrue(DiagnosKategori.extractFromString("A22").isPresent());
        assertTrue(DiagnosKategori.extractFromString("A22").equals(EXPECTED_A));
        assertTrue(DiagnosKategori.extractFromString("A2230.5").equals(EXPECTED_A));
    }

    @Test
    public void testExtractFromStringFailures() throws Exception {
        assertFalse(DiagnosKategori.extractFromString(null).isPresent());
        assertFalse(DiagnosKategori.extractFromString("").isPresent());
        assertFalse(DiagnosKategori.extractFromString("A").isPresent());
        assertFalse(DiagnosKategori.extractFromString("A2").isPresent());
        assertFalse(DiagnosKategori.extractFromString("AB22").isPresent());
        assertFalse(DiagnosKategori.extractFromString("2F46").isPresent());
        assertFalse(DiagnosKategori.extractFromString("A 22").isPresent());
    }
}
