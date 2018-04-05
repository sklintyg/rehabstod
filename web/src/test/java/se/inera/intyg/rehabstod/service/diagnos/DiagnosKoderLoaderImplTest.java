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

import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by eriklupander on 2016-04-14.
 */
public class DiagnosKoderLoaderImplTest {

    private DiagnosKoderLoaderImpl testee = new DiagnosKoderLoaderImpl();

    @Test
    public void testLoadDiagnosKoder() throws IOException {
        specifyDiagnosKodFiler();
        Map<String, String> diagnosKoder = testee.loadDiagnosKoder();
        assertNotNull(diagnosKoder);
        assertEquals(36323, diagnosKoder.size());
    }

    private void specifyDiagnosKodFiler() {
        ReflectionTestUtils.setField(testee, "diagnosKodKS97KatFile", loadDiagnosKodFile("KSH97_KAT.ANS"));
        ReflectionTestUtils.setField(testee, "diagnosKodKS97KodFile", loadDiagnosKodFile("KSH97_KOD.ANS"));
        ReflectionTestUtils.setField(testee, "diagnosKodKS97KxxFile", loadDiagnosKodFile("KSH97_KXX.ANS"));
        ReflectionTestUtils.setField(testee, "diagnosKodKS97PKodFile", loadDiagnosKodFile("KSH97P_KOD.ANS"));
    }

    private Resource loadDiagnosKodFile(String file) {
        return new ClassPathResource("DiagnosKoderLoaderTest/" + file);
    }
}
