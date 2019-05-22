/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.service.export.xlsx;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.ImmutableMap;

import se.inera.intyg.infra.integration.hsa.model.SelectableVardenhet;
import se.inera.intyg.infra.security.common.model.Feature;
import se.inera.intyg.infra.security.common.model.Role;
import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.auth.authorities.AuthoritiesConstants;
import se.inera.intyg.rehabstod.service.diagnos.DiagnosKapitelService;
import se.inera.intyg.rehabstod.service.diagnos.dto.DiagnosKapitel;
import se.inera.intyg.rehabstod.testutil.TestDataGen;

/**
 * Created by eriklupander on 2016-02-24.
 */
@RunWith(MockitoJUnitRunner.class)
public class XlsxExportServiceImplTest {

    RehabstodUser user;

    @Mock
    private DiagnosKapitelService diagnosKapitelService;

    @InjectMocks
    private XlsxExportServiceImpl testee = new XlsxExportServiceImpl();

    @Before
    public void setup() {
        user = new RehabstodUser("HSA1111", "Johannes Nielsen-Kornbach", false);
        user.setValdVardenhet(new SelectableVardenhet() {
            @Override
            public String getId() {
                return "1111";
            }

            @Override
            public String getNamn() {
                return "Gläntans vårdcentral";
            }

            @Override
            public List<String> getHsaIds() {
                return null;
            }
        });
        user.setValdVardgivare(new SelectableVardenhet() {
            @Override
            public String getId() {
                return "VG1";
            }

            @Override
            public String getNamn() {
                return "Vardgivare1";
            }

            @Override
            public List<String> getHsaIds() {
                return null;
            }
        });
        Feature f = new Feature();
        f.setGlobal(true);
        f.setName(AuthoritiesConstants.FEATURE_SRS);
        user.setFeatures(Collections.singletonMap(AuthoritiesConstants.FEATURE_SRS, f));

        DiagnosKapitel diagnosKapitel = mock(DiagnosKapitel.class);
        when(diagnosKapitel.getName()).thenReturn("Diagnoskapitlets namn");
        when(diagnosKapitelService.getDiagnosKapitel(anyString())).thenReturn(diagnosKapitel);
    }

    @Test
    public void testBuildXlsxForAll() throws IOException {
        user.setRoles(ImmutableMap.of(AuthoritiesConstants.ROLE_KOORDINATOR, new Role()));
        byte[] data = testee.export(TestDataGen.buildSjukfallList(2), TestDataGen.buildPrintRequest(), user, 2);
        assertNotNull(data);
        assertTrue(data.length > 0);
        //IOUtils.write(data, new FileOutputStream(new File("./dev.xlsx")));
    }

    @Test
    public void testBuildXlsxForIssuedByMe() throws IOException {
        user.setRoles(ImmutableMap.of(AuthoritiesConstants.ROLE_LAKARE, new Role()));
        byte[] data = testee.export(TestDataGen.buildSjukfallList(2), TestDataGen.buildPrintRequest(), user, 2);
        assertNotNull(data);
        assertTrue(data.length > 0);
        // IOUtils.write(data, new FileOutputStream(new File("/Users/eriklupander/intyg/dev2.xlsx")));
    }

    @Test
    public void testBuildXlsxForIssuedByMeWithoutSrs() throws IOException {
        user.setFeatures(Collections.emptyMap());
        user.setRoles(ImmutableMap.of(AuthoritiesConstants.ROLE_LAKARE, new Role()));
        byte[] data = testee.export(TestDataGen.buildSjukfallList(2), TestDataGen.buildPrintRequest(), user, 2);
        assertNotNull(data);
        assertTrue(data.length > 0);
        // IOUtils.write(data, new FileOutputStream(new File("/Users/eriklupander/intyg/dev2.xlsx")));
    }

}
