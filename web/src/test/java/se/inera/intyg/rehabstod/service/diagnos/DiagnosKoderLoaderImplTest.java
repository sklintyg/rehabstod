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
package se.inera.intyg.rehabstod.service.diagnos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Created by eriklupander on 2016-04-14.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {DiagnosKoderLoaderImpl.class, IcdCodeConverter.class})
public class DiagnosKoderLoaderImplTest {

    @Autowired
    private DiagnosKoderLoaderImpl diagnosKoderLoader;

    @Test
    public void testLoadDiagnosKoder() throws IOException {
        specifyDiagnosKodFiler();
        Map<String, String> diagnosKoder = diagnosKoderLoader.loadDiagnosKoder();
        assertNotNull(diagnosKoder);
        assertEquals(39350, diagnosKoder.size());
    }

    private void specifyDiagnosKodFiler() {
        ReflectionTestUtils.setField(diagnosKoderLoader, "diagnosisCodeIcd10SeFile",
            loadDiagnosKodFile("icd10se/icd-10-se.tsv"));
        ReflectionTestUtils.setField(diagnosKoderLoader, "diagnosKodKS97PKodFile",
            loadDiagnosKodFile("KSH97P_KOD.ANS"));
    }

    private String loadDiagnosKodFile(String file) {
        return "classpath:DiagnosKoderLoaderTest/" + file;
    }
}
