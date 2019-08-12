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
import static org.junit.Assert.assertTrue;

import java.util.EnumSet;
import java.util.List;
import org.junit.Test;

public class ExportFieldsTest {

  @Test
  public void fromJson() {
    assertEquals(ExportFields.fromJson(""), EnumSet.allOf(ExportFields.class));
    assertEquals(ExportFields.fromJson(null), EnumSet.allOf(ExportFields.class));
    assertTrue(ExportFields.fromJson("unknown").isEmpty());

    final List<ExportFields> exportFields = ExportFields.fromJson(
        ExportFields.KOMPLETTERINGAR.getJsonId() + "|" + ExportFields.DAYS.getJsonId() + "|apa|" + ExportFields.GRADER.getJsonId());
    assertEquals(3, exportFields.size());
    assertEquals((ExportFields.KOMPLETTERINGAR), exportFields.get(0));
    assertEquals((ExportFields.DAYS), exportFields.get(1));
    assertEquals((ExportFields.GRADER), exportFields.get(2));


  }
}