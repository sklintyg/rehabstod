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
package se.inera.intyg.rehabstod.service.pdl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import se.inera.intyg.infra.logmessages.ActivityPurpose;
import se.inera.intyg.infra.logmessages.ActivityType;
import se.inera.intyg.infra.logmessages.PdlLogMessage;
import se.inera.intyg.infra.logmessages.PdlResource;
import se.inera.intyg.infra.logmessages.ResourceType;
import se.inera.intyg.infra.security.common.model.Role;
import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.auth.authorities.AuthoritiesConstants;
import se.inera.intyg.rehabstod.testutil.TestDataGen;

/**
 * Created by eriklupander on 2016-03-03.
 */
public class PdlLogMessageFactoryImplTest {

    private PdlLogMessageFactoryImpl testee = new PdlLogMessageFactoryImpl();

    @Test
    public void testBuildPdlReadLogMessage() {

        // Then
        PdlLogMessage pdlLogMessage = testee.buildLogMessage(TestDataGen.buildSjukfallList(5),
            ActivityType.READ, ResourceType.RESOURCE_TYPE_OVERSIKT_SJUKFALL, TestDataGen.buildRehabStodUser());
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
        assertEquals("191212121212", pdlResource.getPatient().getPatientId());
        assertEquals("Tolvan Tolvansson", pdlResource.getPatient().getPatientNamn());
    }

    @Test
    public void testBuildPdlPrintLogMessage() {

        // Then
        PdlLogMessage pdlLogMessage = testee.buildLogMessage(TestDataGen.buildSjukfallList(5),
            ActivityType.PRINT, ResourceType.RESOURCE_TYPE_OVERSIKT_SJUKFALL, TestDataGen.buildRehabStodUser());
        assertNotNull(pdlLogMessage);

        assertEquals(ActivityType.PRINT, pdlLogMessage.getActivityType());
    }

    @Test
    public void testBuildLogMessageForLakare() {
        PdlLogMessage pdlLogMessage = testee.buildLogMessage(TestDataGen.buildSjukfallList(5),
            ActivityType.PRINT, ResourceType.RESOURCE_TYPE_OVERSIKT_SJUKFALL, TestDataGen.buildRehabStodUser());
        assertEquals("Läkare", pdlLogMessage.getUserTitle());
    }

    @Test
    public void testBuildLogMessageForIckeLakare() {
        RehabstodUser rehabstodUser = TestDataGen.buildRehabStodUser();
        rehabstodUser.setRoles(ImmutableMap.of(AuthoritiesConstants.ROLE_KOORDINATOR, new Role()));
        PdlLogMessage pdlLogMessage = testee.buildLogMessage(TestDataGen.buildSjukfallList(5),
            ActivityType.PRINT, ResourceType.RESOURCE_TYPE_OVERSIKT_SJUKFALL, rehabstodUser);
        assertEquals("Rehabkoordinator", pdlLogMessage.getUserTitle());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuildWithUnknownType() {
        testee.buildLogMessage(TestDataGen.buildSjukfallList(1),
            ActivityType.EMERGENCY_ACCESS, ResourceType.RESOURCE_TYPE_OVERSIKT_SJUKFALL, TestDataGen.buildRehabStodUser());
    }
}
