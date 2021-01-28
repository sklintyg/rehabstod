/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
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
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Created by martin on 10/02/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class DiagnosBeskrivningServiceImplTest {

    private static final String KOD_1 = "M123";
    private static final String KOD_2 = "A123B";
    private static final String KOD_3 = "J123";
    private static final String KOD_4 = "M123A";
    private static final String BESKRIVNING_1 = "Test beskrivning";
    private static final String BESKRIVNING_2 = "En annan beskrivning";

    @Mock
    private DiagnosKoderLoader diagnosKoderLoader;

    @InjectMocks
    private DiagnosBeskrivningServiceImpl testee;

    @Before
    public void init() throws IOException {
        when(diagnosKoderLoader.loadDiagnosKoder()).thenReturn(buildDiagnosKoderMap());
        testee.init();
    }

    @Test
    public void testGetDiagnosBeskrivning() {

        assertEquals(BESKRIVNING_1, testee.getDiagnosBeskrivning(KOD_1));
        assertEquals(BESKRIVNING_2, testee.getDiagnosBeskrivning(KOD_2));
        assertNull(testee.getDiagnosBeskrivning(KOD_3));
        assertNull(testee.getDiagnosBeskrivning(KOD_4));
    }

    @Test(expected = DiagnosServiceException.class)
    public void testDiagnosServiceExceptionOnFailedInit() throws IOException {
        when(diagnosKoderLoader.loadDiagnosKoder()).thenThrow(new IOException("Faked IO Exception"));
        testee.init();
    }


    private Map<String, String> buildDiagnosKoderMap() {
        Map<String, String> map = new HashMap<>();

        map.put(KOD_1, BESKRIVNING_1);
        map.put(KOD_2, BESKRIVNING_2);

        return map;

    }
}
