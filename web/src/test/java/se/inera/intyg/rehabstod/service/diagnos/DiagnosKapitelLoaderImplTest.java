/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.service.diagnos;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.util.ReflectionTestUtils;
import se.inera.intyg.rehabstod.service.diagnos.dto.DiagnosKapitel;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by eriklupander on 2016-04-14.
 */
public class DiagnosKapitelLoaderImplTest {

    private DiagnosKapitelLoaderImpl testee = new DiagnosKapitelLoaderImpl();

    @Test
    public void testLoadDiagnosKapitel() throws IOException {
        ReflectionTestUtils.setField(testee, "diagnosKapitelFile", loadTestResource());
        List<DiagnosKapitel> diagnosKapitel = testee.loadDiagnosKapitel();
        assertNotNull(diagnosKapitel);
        assertEquals(22, diagnosKapitel.size());
    }

    @Test
    public void testLoadDiagnosKapitelEmptyFile() throws IOException {
        ReflectionTestUtils.setField(testee, "diagnosKapitelFile", new ClassPathResource("DiagnosKapitelLoaderTest/diagnoskapitel_tom.txt"));
        List<DiagnosKapitel> diagnosKapitel = testee.loadDiagnosKapitel();
        assertNotNull(diagnosKapitel);
        assertEquals(0, diagnosKapitel.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLoadDiagnosKapitelInvaludFileThrowsException() throws IOException {
        ReflectionTestUtils.setField(testee, "diagnosKapitelFile", new ClassPathResource("DiagnosKapitelLoaderTest/diagnoskapitel_invalid.txt"));
        testee.loadDiagnosKapitel();
    }

    private Resource loadTestResource() {
        Resource res = new ClassPathResource("DiagnosKapitelLoaderTest/diagnoskapitel.txt");
        return res;
    }
}
