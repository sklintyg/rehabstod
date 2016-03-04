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
package se.inera.intyg.rehabstod.service.sjukfall.ruleengine.statistics;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import se.inera.intyg.rehabstod.service.sjukfall.dto.SjukfallSummary;
import se.inera.intyg.rehabstod.web.model.Gender;
import se.inera.intyg.rehabstod.web.model.InternalSjukfall;
import se.inera.intyg.rehabstod.web.model.Lakare;
import se.inera.intyg.rehabstod.web.model.Patient;
import se.inera.intyg.rehabstod.web.model.Sjukfall;

/**
 * Created by marced on 04/03/16.
 */
public class StatisticsCalculatorImplTest {

    private String lakareId1 = "hsaid1";
    private String lakareNamn1 = "Läkare1";
    private String lakareId2 = "hsaid2";
    private String lakareNamn2 = "Läkare2";

    private StatisticsCalculatorImpl testee = new StatisticsCalculatorImpl();

    @Test
    public void testGetSjukfallSummaryNoInput() throws Exception {
        List<InternalSjukfall> internalSjukfallList = new ArrayList<>();

        final SjukfallSummary summary = testee.getSjukfallSummary(internalSjukfallList);
        assertEquals(0, summary.getTotal());
        assertEquals(0.0, summary.getWomen(), 0);
        assertEquals(0, summary.getMen(), 0);
    }

    @Test
    public void testGetSjukfallSummary() throws Exception {
        List<InternalSjukfall> internalSjukfallList = new ArrayList<>();

        internalSjukfallList.add(createInternalSjukfall(lakareId1, lakareNamn1, Gender.F));
        internalSjukfallList.add(createInternalSjukfall(lakareId1, lakareNamn1, Gender.F));

        internalSjukfallList.add(createInternalSjukfall(lakareId2, lakareNamn2, Gender.F));
        internalSjukfallList.add(createInternalSjukfall(lakareId2, lakareNamn2, Gender.M));

        final SjukfallSummary summary = testee.getSjukfallSummary(internalSjukfallList);
        assertEquals(4, summary.getTotal());
        assertEquals(75.0, summary.getWomen(), 0);
        assertEquals(25.0, summary.getMen(), 0);

    }

    @Test
    public void testGetSjukfallAllOneGender() throws Exception {
        List<InternalSjukfall> internalSjukfallList = new ArrayList<>();

        internalSjukfallList.add(createInternalSjukfall(lakareId1, lakareNamn1, Gender.F));
        internalSjukfallList.add(createInternalSjukfall(lakareId1, lakareNamn1, Gender.F));

        internalSjukfallList.add(createInternalSjukfall(lakareId2, lakareNamn2, Gender.F));
        internalSjukfallList.add(createInternalSjukfall(lakareId2, lakareNamn2, Gender.F));

        final SjukfallSummary summary = testee.getSjukfallSummary(internalSjukfallList);
        assertEquals(4, summary.getTotal());
        assertEquals(100.0, summary.getWomen(), 0);
        assertEquals(0.0, summary.getMen(), 0);

    }

    private InternalSjukfall createInternalSjukfall(String lakareId, String lakareNamn, Gender patientKon) {
        Lakare lakare = new Lakare();
        lakare.setHsaId(lakareId);
        lakare.setNamn(lakareNamn);

        Sjukfall sjukfall = new Sjukfall();
        sjukfall.setLakare(lakare);
        Patient patient = new Patient();
        patient.setKon(patientKon);
        sjukfall.setPatient(patient);

        InternalSjukfall internalSjukfall = new InternalSjukfall();

        internalSjukfall.setSjukfall(sjukfall);

        return internalSjukfall;
    }
}
