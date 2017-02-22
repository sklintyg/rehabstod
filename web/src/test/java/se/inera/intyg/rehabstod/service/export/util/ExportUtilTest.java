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

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import se.inera.intyg.rehabstod.testutil.TestDataGen;
import se.inera.intyg.rehabstod.web.model.InternalSjukfall;
import se.inera.intyg.rehabstod.web.model.Patient;

import java.util.Arrays;
import java.util.List;

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
        assertEquals("pnr1", sjukfallList.get(0).getPatient().getId());
        assertEquals("pnr2", sjukfallList.get(1).getPatient().getId());
        assertEquals("pnr3", sjukfallList.get(2).getPatient().getId());
    }

    @Test
    public void testListReturnedInExpectedOrderAndOneIsFilteredAway() {
        List<InternalSjukfall> sjukfallList = ExportUtil.sortForExport(Arrays.asList("pnr1", "pnr3"), buildMatchingInternalSjukfallInOtherOrder());
        assertEquals(2, sjukfallList.size());
        assertEquals("pnr1", sjukfallList.get(0).getPatient().getId());
        assertEquals("pnr3", sjukfallList.get(1).getPatient().getId());
    }

    private List<InternalSjukfall> buildMatchingInternalSjukfallInOtherOrder() {
        List<InternalSjukfall> list = TestDataGen.buildSjukfallList(3);

        list.get(0).setPatient(buildPatient("pnr2", list.get(0).getPatient()));
        list.get(1).setPatient(buildPatient("pnr3", list.get(1).getPatient()));
        list.get(2).setPatient(buildPatient("pnr1", list.get(2).getPatient()));

        return list;
    }

    private List<InternalSjukfall> buildNoneMatchingInternalSjukfall() {

        List<InternalSjukfall> sjukfallList = TestDataGen.buildSjukfallList(2);
        for (InternalSjukfall internalSjukfall : sjukfallList) {
            internalSjukfall.setPatient(buildPatient("other-pnr", internalSjukfall.getPatient()));
        }
        return sjukfallList;
    }

    private Patient buildPatient(String newPatientId, Patient oldPatient) {
        Patient newPatient = new Patient(newPatientId, oldPatient.getNamn());
        newPatient.setAlder(oldPatient.getAlder());
        newPatient.setKon(oldPatient.getKon());
        return newPatient;
    }

    // CHECKSTYLE:ON MagicNumber
}
