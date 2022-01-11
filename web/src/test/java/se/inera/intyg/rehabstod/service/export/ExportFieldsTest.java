/*
 * Copyright (C) 2022 Inera AB (http://www.inera.se)
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
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;

public class ExportFieldsTest {

    @Test
    public void fromJson() {
        assertEquals(ExportField.fromJson(""), Arrays.asList(ExportField.values()));
        assertEquals(ExportField.fromJson(null), Arrays.asList(ExportField.values()));
        assertTrue(ExportField.fromJson("unknown:1").isEmpty());
        assertTrue(ExportField.fromJson(ExportField.DAYS.getJsonId() + ":0").isEmpty());
        assertEquals(ExportField.fromJson(ExportField.DAYS.getJsonId()), Arrays.asList(ExportField.DAYS));

        final List<ExportField> exportFields = ExportField.fromJson(
            ExportField.ARENDEN.getJsonId() + ":1|" + ExportField.DAYS.getJsonId() + ":1|apa:1|" + ExportField.GRADER.getJsonId() + ":1");
        assertEquals(3, exportFields.size());
        assertEquals((ExportField.ARENDEN), exportFields.get(0));
        assertEquals((ExportField.DAYS), exportFields.get(1));
        assertEquals((ExportField.GRADER), exportFields.get(2));


    }
}