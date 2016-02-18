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
import java.util.Map;

/**
 * Created by Magnus Ekstrand on 10/02/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class SjukfallResolverImplTest {
    // CHECKSTYLE:OFF MagicNumber

    private static final String LOCATION_INTYGSDATA = "classpath:SjukfallResolverTest/intygsdata-resolver.csv";

    private static List<IntygsData> intygsDataList;

    private SjukfallResolverImpl resolver;
    private SjukfallMapper mapper;

    private Map<String, List<SortableIntygsData>> intygsDataMap;

    @BeforeClass
    public static void initTestData() throws IOException {
        IntygsDataGenerator generator = new IntygsDataGenerator(LOCATION_INTYGSDATA);
        intygsDataList = generator.generate().get();

        assertTrue(intygsDataList.size() == 16);
    }

    @Before
    public void setup() {
        mapper = new SjukfallMapperImpl();
        resolver = new SjukfallResolverImpl(mapper);

        intygsDataMap = resolver.toMap(intygsDataList, LocalDate.now());

        //assertTrue("Expected 9 but was " + intygsDataMap.size(), intygsDataMap.size() == 9);
    }

    @Test
    public void test() {

    }

    // CHECKSTYLE:ON MagicNumber
}
