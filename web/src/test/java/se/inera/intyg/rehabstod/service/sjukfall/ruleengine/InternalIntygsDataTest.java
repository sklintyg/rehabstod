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
package se.inera.intyg.rehabstod.service.sjukfall.ruleengine;

import static org.junit.Assert.assertTrue;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import se.inera.intyg.rehabstod.service.sjukfall.ruleengine.testdata.IntygsDataGenerator;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.IntygsData;

import java.io.IOException;
import java.util.List;

/**
 * Created by Magnus Ekstrand on 2016-02-16.
 */
@RunWith(MockitoJUnitRunner.class)
public class InternalIntygsDataTest {

    private static final String LOCATION_INTYGSDATA = "classpath:SortableIntygsDataTest/intygsdata.csv";

    private static List<IntygsData> intygsDataList;

    private LocalDate activeDate = LocalDate.parse("2016-02-16");

    @BeforeClass
    public static void initTestData() throws IOException {
        IntygsDataGenerator generator = new IntygsDataGenerator(LOCATION_INTYGSDATA);
        intygsDataList = generator.generate().get();

        // CHECKSTYLE:OFF MagicNumber
        assertTrue("Expected 6 but was " + intygsDataList.size(), intygsDataList.size() == 6);
        // CHECKSTYLE:ON MagicNumber
    }

    @Before
    public void setup() {
    }

    @Test
    public void testIntyg1() {
        IntygsData intygsData = getIntygsData("intyg-1");
        InternalIntygsData testee = new InternalIntygsData.SortableIntygsDataBuilder(intygsData, activeDate).build();

        assertIntygsData(testee, "2016-02-01", "2016-02-10", false);
    }

    @Test
    public void testIntyg2() {
        IntygsData intygsData = getIntygsData("intyg-2");
        InternalIntygsData testee = new InternalIntygsData.SortableIntygsDataBuilder(intygsData, activeDate).build();

        assertIntygsData(testee, "2016-02-12", "2016-02-20", true);
    }

    @Test
    public void testIntyg3() {
        IntygsData intygsData = getIntygsData("intyg-3");
        InternalIntygsData testee = new InternalIntygsData.SortableIntygsDataBuilder(intygsData, activeDate).build();

        assertIntygsData(testee, "2016-02-01", "2016-02-20", true);
    }

    @Test
    public void testIntyg4() {
        IntygsData intygsData = getIntygsData("intyg-4");
        InternalIntygsData testee = new InternalIntygsData.SortableIntygsDataBuilder(intygsData, activeDate).build();

        assertIntygsData(testee, "2016-02-01", "2016-02-25", false);
    }

    @Test
    public void testIntyg5() {
        IntygsData intygsData = getIntygsData("intyg-5");
        InternalIntygsData testee = new InternalIntygsData.SortableIntygsDataBuilder(intygsData, activeDate).build();

        assertIntygsData(testee, "2016-02-01", "2016-02-28", true);
    }

    @Test
    public void testIntyg6() {
        IntygsData intygsData = getIntygsData("intyg-6");
        InternalIntygsData testee1 = new InternalIntygsData.SortableIntygsDataBuilder(intygsData, activeDate).build();
        InternalIntygsData testee2 = new InternalIntygsData.SortableIntygsDataBuilder(intygsData, LocalDate.parse("2016-02-22")).build();
        InternalIntygsData testee3 = new InternalIntygsData.SortableIntygsDataBuilder(intygsData, LocalDate.parse("2016-02-23")).build();

        assertIntygsData(testee1, "2016-02-11", "2016-02-28", false);
        assertIntygsData(testee2, "2016-02-11", "2016-02-28", true);
        assertIntygsData(testee3, "2016-02-11", "2016-02-28", true);
    }

    private IntygsData getIntygsData(String intygsId) {
        return intygsDataList.stream()
                .filter(e -> e.getIntygsId().equalsIgnoreCase(intygsId))
                .findAny()
                .get();
    }

    private static void assertIntygsData(InternalIntygsData obj, String startDatum, String slutDatum, boolean aktivtIntyg) {
        assertTrue(obj.getStartDatum().equals(LocalDate.parse(startDatum)));
        assertTrue(obj.getSlutDatum().equals(LocalDate.parse(slutDatum)));
        assertTrue(obj.isAktivtIntyg() == aktivtIntyg);
    }

}
