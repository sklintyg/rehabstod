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
package se.inera.intyg.rehabstod.service.export.util;

import org.junit.Test;
import se.inera.intyg.rehabstod.testutil.TestDataGen;
import se.inera.intyg.rehabstod.web.model.InternalSjukfall;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by eriklupander on 2016-02-24.
 */
public class ExportUtilTest {
    // CHECKSTYLE:OFF MagicNumber
    @Test
    public void testEmptyListReturnedForNoneMatching() {
        List<InternalSjukfall> sjukfallList = ExportUtil.sortForExport(Arrays.asList("pnr1", "pnr2", "pnr3"), buildNoneMatchingInternalSjukfall());
        assertEquals(0, sjukfallList.size());
    }

    @Test
    public void testListReturnedInExpectedOrder() {
        List<InternalSjukfall> sjukfallList = ExportUtil.sortForExport(Arrays.asList("pnr1", "pnr2", "pnr3"), buildMatchingInternalSjukfallInOtherOrder());
        assertEquals(3, sjukfallList.size());
        assertEquals("pnr1", sjukfallList.get(0).getSjukfall().getPatient().getId());
        assertEquals("pnr2", sjukfallList.get(1).getSjukfall().getPatient().getId());
        assertEquals("pnr3", sjukfallList.get(2).getSjukfall().getPatient().getId());
    }

    @Test
    public void testListReturnedInExpectedOrderAndOneIsFilteredAway() {
        List<InternalSjukfall> sjukfallList = ExportUtil.sortForExport(Arrays.asList("pnr1", "pnr3"), buildMatchingInternalSjukfallInOtherOrder());
        assertEquals(2, sjukfallList.size());
        assertEquals("pnr1", sjukfallList.get(0).getSjukfall().getPatient().getId());
        assertEquals("pnr3", sjukfallList.get(1).getSjukfall().getPatient().getId());
    }

    private List<InternalSjukfall> buildMatchingInternalSjukfallInOtherOrder() {
        List<InternalSjukfall> sjukfallList = TestDataGen.buildSjukfallList(3);

        sjukfallList.get(0).getSjukfall().getPatient().setId("pnr2");
        sjukfallList.get(1).getSjukfall().getPatient().setId("pnr3");
        sjukfallList.get(2).getSjukfall().getPatient().setId("pnr1");

        return sjukfallList;
    }


    private List<InternalSjukfall> buildNoneMatchingInternalSjukfall() {

        List<InternalSjukfall> sjukfallList = TestDataGen.buildSjukfallList(2);
        for (InternalSjukfall internalSjukfall : sjukfallList) {
            internalSjukfall.getSjukfall().getPatient().setId("other-pnr");
        }
        return sjukfallList;
    }
    // CHECKSTYLE:ON MagicNumber
}
