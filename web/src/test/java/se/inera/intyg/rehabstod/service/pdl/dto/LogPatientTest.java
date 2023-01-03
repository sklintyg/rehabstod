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
package se.inera.intyg.rehabstod.service.pdl.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * Created by eriklupander on 2017-01-31.
 */
public class LogPatientTest {

    @Test
    public void testBuildLogPatientWithRequiredValues() {
        LogPatient logPatient = new LogPatient.Builder("1", "2", "3").build();
        assertNotNull(logPatient);
        assertEquals("1", logPatient.getPatientId());
        assertEquals("2", logPatient.getEnhetsId());
        assertEquals("3", logPatient.getVardgivareId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuildLogPatientWithNullRequiredValues() {
        new LogUser.Builder("1", "2", null);
        fail("This assert should be unreachable!");
    }
}
