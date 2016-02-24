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
package se.inera.intyg.rehabstod.service.export.xlsx;

import org.junit.Test;
import se.inera.intyg.rehabstod.service.export.BaseExportTest;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by eriklupander on 2016-02-24.
 */
public class XlsxExportServiceImplTest extends BaseExportTest {

    private XlsxExportServiceImpl testee = new XlsxExportServiceImpl();

    @Test
    public void testBuildXlsx() throws IOException {
        byte[] data = testee.export(buildSjukfallList(2), buildPrintRequest());
        assertNotNull(data);
        assertTrue(data.length > 0);
     //   IOUtils.write(data, new FileOutputStream(new File("/Users/eriklupander/intyg/dev.xlsx")));
    }

}
