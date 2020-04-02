/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.integration.srs.stub;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Created by eriklupander on 2017-10-31.
 */
@RunWith(MockitoJUnitRunner.class)
public class SRSStubTest {

    private SRSStub testee = new SRSStub();

    @Test
    public void generateRiskInt() {
        assertEquals(1, testee.getRiskInt(0).intValue());
        assertEquals(2, testee.getRiskInt(1).intValue());
        assertEquals(3, testee.getRiskInt(2).intValue());
        assertEquals(1, testee.getRiskInt(3).intValue());
    }
}
