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
package se.inera.intyg.rehabstod.service.pdl;

import org.junit.Test;
import se.inera.intyg.common.logmessages.ActivityPurpose;
import se.inera.intyg.common.logmessages.ActivityType;
import se.inera.intyg.common.logmessages.PdlLogMessage;
import se.inera.intyg.common.logmessages.PdlResource;
import se.inera.intyg.common.logmessages.ResourceType;
import se.inera.intyg.rehabstod.testutil.TestDataGen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by eriklupander on 2016-03-03.
 */
public class PdlLogMessageFactoryImplTest {

    PdlLogMessageFactoryImpl testee = new PdlLogMessageFactoryImpl();

    @Test
    public void testBuildPdlReadLogMessage() {

        // Then
        PdlLogMessage pdlLogMessage = testee.buildLogMessage(TestDataGen.buildSjukfallList(5), ActivityType.READ, TestDataGen.buildRehabStodUser());
        assertNotNull(pdlLogMessage);

        assertEquals(ActivityType.READ, pdlLogMessage.getActivityType());
        assertEquals(ActivityPurpose.CARE_TREATMENT, pdlLogMessage.getPurpose());
        assertEquals("careunit-1", pdlLogMessage.getUserCareUnit().getEnhetsId());
        assertEquals("Vårdenhet 1", pdlLogMessage.getUserCareUnit().getEnhetsNamn());
        assertEquals("caregiver-1", pdlLogMessage.getUserCareUnit().getVardgivareId());
        assertEquals("Vårdgivare 1", pdlLogMessage.getUserCareUnit().getVardgivareNamn());

        assertEquals(5, pdlLogMessage.getPdlResourceList().size());
        PdlResource pdlResource = pdlLogMessage.getPdlResourceList().get(0);
        assertEquals(ResourceType.RESOURCE_TYPE_OVERSIKT_SJUKFALL.getResourceTypeName(), pdlResource.getResourceType());
        assertEquals("191212121212", pdlResource.getPatient().getPatientId().getPersonnummerWithoutDash());
        assertEquals("Tolvan Tolvansson", pdlResource.getPatient().getPatientNamn());
    }

    @Test
    public void testBuildPdlPrintLogMessage() {

        // Then
        PdlLogMessage pdlLogMessage = testee.buildLogMessage(TestDataGen.buildSjukfallList(5), ActivityType.PRINT, TestDataGen.buildRehabStodUser());
        assertNotNull(pdlLogMessage);

        assertEquals(ActivityType.PRINT, pdlLogMessage.getActivityType());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuildWithUnknownType() {
        testee.buildLogMessage(TestDataGen.buildSjukfallList(1), ActivityType.EMERGENCY_ACCESS, TestDataGen.buildRehabStodUser());
    }
}
