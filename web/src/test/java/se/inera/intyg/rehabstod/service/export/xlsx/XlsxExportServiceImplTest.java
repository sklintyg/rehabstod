/**
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.service.Urval;
import se.inera.intyg.rehabstod.service.diagnos.DiagnosKapitelService;
import se.inera.intyg.rehabstod.service.diagnos.dto.DiagnosKapitel;
import se.inera.intyg.rehabstod.service.user.UserService;
import se.inera.intyg.rehabstod.testutil.TestDataGen;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by eriklupander on 2016-02-24.
 */
@RunWith(MockitoJUnitRunner.class)
public class XlsxExportServiceImplTest {

    @Mock
    private DiagnosKapitelService diagnosKapitelService;

    @Mock
    private UserService userService;

    @InjectMocks
    private XlsxExportServiceImpl testee = new XlsxExportServiceImpl();

    @Before
    public void setup() {
        DiagnosKapitel diagnosKapitel = mock(DiagnosKapitel.class);
        when(diagnosKapitel.getName()).thenReturn("Diagnoskapitlets namn");
        when(diagnosKapitelService.getDiagnosKapitel(anyString())).thenReturn(diagnosKapitel);

        RehabstodUser user = mock(RehabstodUser.class);
        when(user.getNamn()).thenReturn("Läkare Läkarsson");
        when(userService.getUser()).thenReturn(user);
    }

    @Test
    public void testBuildXlsxForAll() throws IOException {
        byte[] data = testee.export(TestDataGen.buildSjukfallList(2), TestDataGen.buildPrintRequest(), Urval.ALL, 2);
        assertNotNull(data);
        assertTrue(data.length > 0);
        //IOUtils.write(data, new FileOutputStream(new File("/Users/eriklupander/intyg/dev.xlsx")));
    }

    @Test
    public void testBuildXlsxForIssuedByMe() throws IOException {
        byte[] data = testee.export(TestDataGen.buildSjukfallList(2), TestDataGen.buildPrintRequest(), Urval.ISSUED_BY_ME, 2);
        assertNotNull(data);
        assertTrue(data.length > 0);
     //   IOUtils.write(data, new FileOutputStream(new File("/Users/eriklupander/intyg/dev2.xlsx")));
    }

}
