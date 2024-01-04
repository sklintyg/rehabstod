/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
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
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import se.inera.intyg.rehabstod.service.diagnos.dto.DiagnosKapitel;

/**
 * Created by marced on 10/02/16.
 */
//CHECKSTYLE:OFF MagicNumber
@RunWith(MockitoJUnitRunner.class)
public class DiagnosKapitelServiceImplTest {

    private static final DiagnosKapitel DEFAULT_KAPITEL = new DiagnosKapitel("A00-C00En ganska bred diagnos");
    private static final DiagnosKapitel SLIM_KAPITEL = new DiagnosKapitel("S00-S01En smal diagnos");
    @Mock
    private DiagnosKapitelLoader diagnosKapitelLoader;

    @InjectMocks
    private DiagnosKapitelServiceImpl testee;

    @Before
    public void init() throws IOException {
        when(diagnosKapitelLoader.loadDiagnosKapitel()).thenReturn(buildDiagnosKapitelList());
        testee.init();
    }

    @Test
    public void testGetDiagnosKapitelList() throws Exception {

        final List<DiagnosKapitel> diagnosKapitelList = testee.getDiagnosKapitelList();
        assertEquals(4, diagnosKapitelList.size());
        assertTrue(diagnosKapitelList.contains(DiagnosKapitelServiceImpl.OGILTIGA_DIAGNOSKODER_KAPITEL));

    }

    @Test
    public void testGetDiagnosKapitel() throws Exception {
        assertEquals(DEFAULT_KAPITEL, testee.getDiagnosKapitel("A00"));
        assertEquals(DEFAULT_KAPITEL, testee.getDiagnosKapitel("B01"));
        assertEquals(DEFAULT_KAPITEL, testee.getDiagnosKapitel("C00"));

        assertEquals(DiagnosKapitelServiceImpl.OGILTIGA_DIAGNOSKODER_KAPITEL, testee.getDiagnosKapitel("R99"));
        assertEquals(SLIM_KAPITEL, testee.getDiagnosKapitel("S00201"));
        assertEquals(SLIM_KAPITEL, testee.getDiagnosKapitel("S01201"));
        assertEquals(DiagnosKapitelServiceImpl.OGILTIGA_DIAGNOSKODER_KAPITEL, testee.getDiagnosKapitel("S02"));

        assertEquals(DiagnosKapitelServiceImpl.OGILTIGA_DIAGNOSKODER_KAPITEL, testee.getDiagnosKapitel("X0102"));

    }

    private List<DiagnosKapitel> buildDiagnosKapitelList() {
        List<DiagnosKapitel> list = new ArrayList<>();
        list.add(DEFAULT_KAPITEL);
        list.add(SLIM_KAPITEL);

        list.add(new DiagnosKapitel("D00-D00Dummy"));
        return list;

    }

}
