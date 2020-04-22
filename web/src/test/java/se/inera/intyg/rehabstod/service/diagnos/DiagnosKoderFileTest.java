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
package se.inera.intyg.rehabstod.service.diagnos;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.junit.Ignore;
import org.junit.Test;
import se.inera.intyg.rehabstod.service.diagnos.dto.DiagnosKod;

/**
 * Created by martin on 11/02/16.
 */
public class DiagnosKoderFileTest {

    private static final String KS97_KAT_FILE = "src/main/resources/diagnoskoder/KSH97_KAT.ANS";
    private static final String KS97_KOD_FILE = "src/main/resources/diagnoskoder/KSH97_KOD.ANS";
    private static final String KS97_KXX_FILE = "src/main/resources/diagnoskoder/KSH97_KXX.ANS";
    private static final String KS97P_KOD_FILE = "src/main/resources/diagnoskoder/KSH97P_KOD.ANS";


    /**
     * Tests that the codes in the provided files only contains A-Z 0-9 and -.
     */
    @Test
    @Ignore
    public void testKodFormat() throws IOException {

        testFile(loadDiagnosFile(KS97P_KOD_FILE));

        testFile(loadDiagnosFile(KS97_KAT_FILE));

        testFile(loadDiagnosFile(KS97_KOD_FILE));

        testFile(loadDiagnosFile(KS97_KXX_FILE));
    }

    private void testFile(List<String> koder) {

        for (String key : koder) {
            assertEquals(DiagnosKod.cleanKod(key), key);
        }
    }


    private List<String> loadDiagnosFile(String fileName) throws IOException {
        String filePath = getClass().getClassLoader().getResource(fileName).getFile();

        List<String> koder = new ArrayList<>();
        try (LineIterator it = FileUtils.lineIterator(new File(filePath), "ISO-8859-1")) {

            while (it.hasNext()) {
                String line = it.nextLine();

                String kod = line.substring(0, DiagnosKod.KOD_LENGTH).trim();

                koder.add(kod);
            }
        }
        return koder;
    }

}
