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
package se.inera.intyg.rehabstod.service.export;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import se.inera.intyg.rehabstod.web.controller.api.dto.PrintSjukfallRequest;
import se.inera.intyg.rehabstod.web.model.Diagnos;
import se.inera.intyg.rehabstod.web.model.LangdIntervall;
import se.inera.intyg.rehabstod.web.model.Sortering;

public class BaseExportServiceTest {

    private BaseExportService baseExportService = new BaseExportService() {
    };


    @Test
    public void testGetFilterDate() {
        LangdIntervall interval = new LangdIntervall();
        interval.setMin("2017-12-01");
        interval.setMax("2017-12-12");

        String date = baseExportService.getFilterDate(interval);

        String expected = "2017-12-01 - 2017-12-12";

        assertEquals(expected, date);
    }

    @Test
    public void testGetFilterDateEmpty() {
        LangdIntervall interval = new LangdIntervall();

        String date = baseExportService.getFilterDate(interval);

        String expected = "-";

        assertEquals(expected, date);
    }

    @Test
    public void testGetFilterDateSame() {
        LangdIntervall interval = new LangdIntervall();
        interval.setMin("2017-12-01");
        interval.setMax("2017-12-01");

        String date = baseExportService.getFilterDate(interval);

        String expected = "2017-12-01";

        assertEquals(expected, date);
    }

    @Test
    public void testDiagnoseListToStringNull() {
        String diagnoseString = baseExportService.diagnoseListToString(null);

        String expected = "";

        assertEquals(expected, diagnoseString);
    }

    @Test
    public void testDiagnoseListToStringEmpty() {
        List<Diagnos> diagnoses = new ArrayList<>();
        String diagnoseString = baseExportService.diagnoseListToString(diagnoses);

        String expected = "";

        assertEquals(expected, diagnoseString);
    }

    @Test
    public void testDiagnoseListToStringOne() {
        List<Diagnos> diagnoses = new ArrayList<>();
        diagnoses.add(new Diagnos("J20V", "J20", "Test"));
        String diagnoseString = baseExportService.diagnoseListToString(diagnoses);

        String expected = ", J20V";

        assertEquals(expected, diagnoseString);
    }

    @Test
    public void testDiagnoseListToStringList() {
        List<Diagnos> diagnoses = new ArrayList<>();
        diagnoses.add(new Diagnos("J20V", "J20", "Test"));
        diagnoses.add(new Diagnos("J21V", "J21", "Test2"));
        String diagnoseString = baseExportService.diagnoseListToString(diagnoses);

        String expected = ", J20V, J21V";

        assertEquals(expected, diagnoseString);
    }

    @Test
    public void testNotEmpty() {
        PrintSjukfallRequest request = new PrintSjukfallRequest();
        request.setFritext("Search");

        boolean notEmpty = baseExportService.notEmpty(request);

        assertTrue(notEmpty);
    }

    @Test
    public void testNotEmptyBlank() {
        PrintSjukfallRequest request = new PrintSjukfallRequest();
        request.setFritext("");

        boolean notEmpty = baseExportService.notEmpty(request);

        assertFalse(notEmpty);
    }

    @Test
    public void testNotEmptyNull() {
        PrintSjukfallRequest request = new PrintSjukfallRequest();

        boolean notEmpty = baseExportService.notEmpty(request);

        assertFalse(notEmpty);
    }

    @Test
    public void testShouldShowSort() {
        PrintSjukfallRequest request = new PrintSjukfallRequest();
        List<ExportField> fields = Arrays.asList(ExportField.DAYS, ExportField.DIAGNOSE);

        assertFalse(baseExportService.shouldShowSortering(request, fields));

        Sortering sortering = new Sortering();
        sortering.setKolumn(null);
        request.setSortering(sortering);

        assertFalse(baseExportService.shouldShowSortering(request, fields));
        sortering.setKolumn("");
        assertFalse(baseExportService.shouldShowSortering(request, fields));

        sortering.setKolumn(ExportField.ENDDATE.getJsonId());
        assertFalse(baseExportService.shouldShowSortering(request, fields));

        sortering.setKolumn(ExportField.DIAGNOSE.getJsonId());
        assertTrue(baseExportService.shouldShowSortering(request, fields));

    }

}
