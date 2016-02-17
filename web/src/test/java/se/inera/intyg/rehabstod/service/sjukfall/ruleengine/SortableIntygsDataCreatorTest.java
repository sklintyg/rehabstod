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
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import se.inera.intyg.rehabstod.service.sjukfall.ruleengine.testdata.IntygsDataGenerator;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.IntygsData;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by Magnus Ekstrand on 2016-02-16.
 */
@RunWith(MockitoJUnitRunner.class)
public class SortableIntygsDataCreatorTest {
    // CHECKSTYLE:OFF MagicNumber

    private static final String LOCATION_INTYGSDATA = "classpath:SortableIntygsDataCreatorTest/intygsdata-creator.csv";

    private static List<IntygsData> intygsDataList;

    private SortableIntygsDataCreator creator;

    private LocalDate activeDate = LocalDate.parse("2016-02-16");

    @BeforeClass
    public static void initTestData() throws IOException {
        IntygsDataGenerator generator = new IntygsDataGenerator(LOCATION_INTYGSDATA);
        intygsDataList = generator.generate().get();

        assertTrue("Expected 16 but was " + intygsDataList.size(), intygsDataList.size() == 16);
    }

    @Before
    public void setup() {
        creator = new SortableIntygsDataCreator();
    }

    @Test
    public void testCreatingMap() {
        Map<String, List<SortableIntygsData>> map = creator.createMap(intygsDataList, activeDate);
        assertTrue("Expected 7 but was " + map.size(), map.size() == 7);
    }

    @Test
    public void testSortedMap() {
        Map<String, List<SortableIntygsData>> map = creator.createMap(intygsDataList, activeDate);
        Map<String, List<SortableIntygsData>> sortedMap = creator.sortValues(map);

        for (Map.Entry<String, List<SortableIntygsData>> entry : sortedMap.entrySet()) {
            if (entry.getValue().size() > 1) {
                SortableIntygsData[] arr = entry.getValue().toArray(new SortableIntygsData[entry.getValue().size()]);
                // Check sort order when list size is greater than one
                for (int i = 0; i < arr.length - 1; i++) {
                    assertTrue(arr[i].getSlutDatum().isBefore(arr[i + 1].getSlutDatum()));
                }
            }
        }
    }

    /*
     * Test methods below refers to specified cases in Sjukfall.xlsx.
     * https://inera-certificate.atlassian.net/wiki/pages/viewpage.action?pageId=39747618&preview=/39747618/39747617/Sjukfall.xlsx
     */

    @Ignore
    @Test
    public void testFall1() {
        String key = "19791110-9291";
        Map<String, List<SortableIntygsData>> map = creator.create(intygsDataList, activeDate);

        List<SortableIntygsData> list = map.get(key);

        assertTrue("Expected 2 but was " + list.size(), list.size() == 2);
        assertStartDate(list.get(0), "2016-02-01");
        assertEndDate(list.get(1), "2016-02-20");
        assertTrue(list.get(1).isAktivtIntyg());
    }

    static void assertStartDate(SortableIntygsData intygsData, String datum) {
        assertTrue(intygsData.getStartDatum().equals(LocalDate.parse(datum)));
    }

    static void assertEndDate(SortableIntygsData intygsData, String datum) {
        assertTrue(intygsData.getSlutDatum().equals(LocalDate.parse(datum)));
    }

    // CHECKSTYLE:ON MagicNumber
}
