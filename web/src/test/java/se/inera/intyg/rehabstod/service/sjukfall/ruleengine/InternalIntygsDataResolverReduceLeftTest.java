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
package se.inera.intyg.rehabstod.service.sjukfall.ruleengine;

import java.time.LocalDate;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import se.inera.intyg.rehabstod.service.sjukfall.ruleengine.testdata.IntygsDataGenerator;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.IntygsData;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Magnus Ekstrand on 10/02/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class InternalIntygsDataResolverReduceLeftTest {

    private static final String LOCATION_INTYGSDATA = "classpath:InternalIntygsDataResolverTest/intygsdata-resolver-left.csv";

    private static List<IntygsData> intygsDataList;

    private InternalIntygsDataResolverImpl resolver;


    @BeforeClass
    public static void initTestData() throws IOException {
        IntygsDataGenerator generator = new IntygsDataGenerator(LOCATION_INTYGSDATA);
        intygsDataList = generator.generate().get();
    }

    @Before
    public void setup() {
        resolver = new InternalIntygsDataResolverImpl();
    }

    @Test
    public void testFall1() {
        List<InternalIntygsData> result = getTestData("fall-1-left", "2016-01-27", 5, "2016-02-08");
        assertTrue("Expected 3 but was " + result.size(), result.size() == 3);
    }

    @Test
    public void testFall2() {
        List<InternalIntygsData> result = getTestData("fall-2-left", "2016-01-27", 5, "2016-02-08");
        assertTrue("Expected 2 but was " + result.size(), result.size() == 2);
        assertEquals("fall-2-intyg-2", result.get(0).getIntygsId());
        assertEquals("fall-2-intyg-3", result.get(1).getIntygsId());
    }

    @Test
    public void testFall3() {
        List<InternalIntygsData> result = getTestData("fall-3-left", "2016-01-27", 5, "2016-02-08");
        assertTrue("Expected 0 but was " + result.size(), result.size() == 0);
    }

    @Test
    public void testFall4() {
        List<InternalIntygsData> result = getTestData("fall-4-left", "2016-01-27", 5, "2016-02-08");
        assertTrue("Expected 3 but was " + result.size(), result.size() == 3);
        assertEquals("fall-4-intyg-1", result.get(0).getIntygsId());
        assertEquals("fall-4-intyg-3", result.get(1).getIntygsId());
        assertEquals("fall-4-intyg-2", result.get(2).getIntygsId());
    }

    @Test
    public void testFall5() {
        List<InternalIntygsData> result = getTestData("fall-5-left", "2016-01-27", 5, "2016-02-08");
        assertTrue("Expected 4 but was " + result.size(), result.size() == 4);
        assertEquals("fall-5-intyg-1", result.get(0).getIntygsId());
        assertEquals("fall-5-intyg-2", result.get(1).getIntygsId());
        assertEquals("fall-5-intyg-3", result.get(2).getIntygsId());
        assertEquals("fall-5-intyg-4", result.get(3).getIntygsId());
    }

    @Test
    public void testFall6() {
        List<InternalIntygsData> result = getTestData("fall-6-left", "2016-01-27", 5, "2016-02-08");
        assertTrue("Expected 2 but was " + result.size(), result.size() == 2);
        assertEquals("fall-6-intyg-2", result.get(0).getIntygsId());
        assertEquals("fall-6-intyg-1", result.get(1).getIntygsId());
    }

    @Test
    public void testFall7() {
        List<InternalIntygsData> result = getTestData("fall-7-left", "2016-01-27", 5, "2016-02-08");
        assertTrue("Expected 5 but was " + result.size(), result.size() == 5);
        assertEquals("fall-7-intyg-1", result.get(0).getIntygsId());
        assertEquals("fall-7-intyg-2", result.get(1).getIntygsId());
        assertEquals("fall-7-intyg-3", result.get(2).getIntygsId());
        assertEquals("fall-7-intyg-5", result.get(3).getIntygsId());
        assertEquals("fall-7-intyg-4", result.get(4).getIntygsId());
    }

    private List<InternalIntygsData> getTestData(String key, String aktivtDatum , int maxIntygsGlapp, String initialtDatum) {
        Map<String, List<InternalIntygsData>> data = getTestData(aktivtDatum);
        return resolver.reduceLeft(data.get(key), maxIntygsGlapp, LocalDate.parse(initialtDatum));
    }

    private Map<String, List<InternalIntygsData>> getTestData(String aktivtDatum) {
        return resolver.toMap(intygsDataList, LocalDate.parse(aktivtDatum));
    }

    private Map<String, List<InternalIntygsData>> getTestData(LocalDate aktivtDatum) {
        return resolver.toMap(intygsDataList, aktivtDatum);
    }
}
