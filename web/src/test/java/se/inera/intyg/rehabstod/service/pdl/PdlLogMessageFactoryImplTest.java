/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.service.pdl;

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
import se.inera.intyg.rehabstod.web.model.SjukfallEnhet;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by eriklupander on 2016-03-03.
 */
public class PdlLogMessageFactoryImplTest {

    private PdlLogMessageFactoryImpl testee = new PdlLogMessageFactoryImpl();

    @Test
    public void testBuildLogMessage() {

        // Then
        PdlLogMessage pdlLogMessage = testee.buildLogMessage(TestDataGen.buildSjukfallList(5),
            ActivityType.READ, ResourceType.RESOURCE_TYPE_SJUKFALL, TestDataGen.buildRehabStodUser(true));
        assertNotNull(pdlLogMessage);

        assertEquals(ActivityType.READ, pdlLogMessage.getActivityType());
        assertEquals(ActivityPurpose.CARE_TREATMENT, pdlLogMessage.getPurpose());
        assertEquals("careunit-1", pdlLogMessage.getUserCareUnit().getEnhetsId());
        assertEquals("Vårdenhet 1", pdlLogMessage.getUserCareUnit().getEnhetsNamn());
        assertEquals("caregiver-1", pdlLogMessage.getUserCareUnit().getVardgivareId());
        assertEquals("Vårdgivare 1", pdlLogMessage.getUserCareUnit().getVardgivareNamn());

        assertEquals(5, pdlLogMessage.getPdlResourceList().size());
        PdlResource pdlResource = pdlLogMessage.getPdlResourceList().get(0);
        assertEquals(ResourceType.RESOURCE_TYPE_SJUKFALL.getResourceTypeName(), pdlResource.getResourceType());
        assertEquals("191212121212", pdlResource.getPatient().getPatientId());

        // INTYG-4647: Removes patient name for PDL-logging.
        assertEquals("", pdlResource.getPatient().getPatientNamn());
        assertEquals("careunit-1", pdlResource.getResourceOwner().getEnhetsId());
        assertEquals("Vårdenhet 1", pdlResource.getResourceOwner().getEnhetsNamn());
        assertEquals("caregiver-1", pdlResource.getResourceOwner().getVardgivareId());
        assertEquals("Vårdgivare 1", pdlResource.getResourceOwner().getVardgivareNamn());
    }

    @Test
    public void testBuildLogMessageWithEmptyCareGiverName() {

        // Then
        PdlLogMessage pdlLogMessage = testee.buildLogMessage(buildSjukfallListWithEmptyCareGiverName(),
                ActivityType.READ, ResourceType.RESOURCE_TYPE_SJUKFALL, TestDataGen.buildRehabStodUser(true));

        assertNotNull(pdlLogMessage);
        assertEquals(1, pdlLogMessage.getPdlResourceList().size());
        PdlResource pdlResource = pdlLogMessage.getPdlResourceList().get(0);
        assertEquals("Vårdgivare 1", pdlResource.getResourceOwner().getVardgivareNamn());
    }

    private List<SjukfallEnhet> buildSjukfallListWithEmptyCareGiverName() {
        List<SjukfallEnhet> sjukfallList = TestDataGen.buildSjukfallList(1);
        sjukfallList.stream().forEach(sfe -> sfe.setVardGivareNamn(null));
        return sjukfallList;
    }

    @Test
    public void testBuildPrintLogMessage() {
        // Then
        PdlLogMessage pdlLogMessage = testee.buildLogMessage(TestDataGen.buildSjukfallList(5),
            ActivityType.PRINT, ResourceType.RESOURCE_TYPE_SJUKFALL, TestDataGen.buildRehabStodUser(true));
        assertNotNull(pdlLogMessage);

        assertEquals(ActivityType.PRINT, pdlLogMessage.getActivityType());
    }

    @Test
    public void testBuildLogMessageForLakare() {
        PdlLogMessage pdlLogMessage = testee.buildLogMessage(TestDataGen.buildSjukfallList(5),
            ActivityType.PRINT, ResourceType.RESOURCE_TYPE_SJUKFALL, TestDataGen.buildRehabStodUser(true));
        assertEquals("Läkare", pdlLogMessage.getUserTitle());
    }

    @Test
    public void testBuildLogMessageForLakareHavingRehabkoordinatorRole() {
        RehabstodUser rehabstodUser = TestDataGen.buildRehabStodUser(true);
        Map<String, Role> rolesMap = mock(Map.class);
        when(rolesMap.containsKey(eq("LAKARE"))).thenReturn(false);
        rehabstodUser.setRoles(rolesMap);
        PdlLogMessage pdlLogMessage = testee.buildLogMessage(TestDataGen.buildSjukfallList(5),
                ActivityType.PRINT, ResourceType.RESOURCE_TYPE_SJUKFALL, rehabstodUser);
        assertEquals("Rehabkoordinator", pdlLogMessage.getUserTitle());
    }

    @Test
    public void testBuildLogMessageForIckeLakare() {
        RehabstodUser rehabstodUser = TestDataGen.buildRehabStodUser(false);
        rehabstodUser.setRoles(ImmutableMap.of(AuthoritiesConstants.ROLE_KOORDINATOR, new Role()));
        PdlLogMessage pdlLogMessage = testee.buildLogMessage(TestDataGen.buildSjukfallList(5),
            ActivityType.PRINT, ResourceType.RESOURCE_TYPE_SJUKFALL, rehabstodUser);
        assertEquals("Rehabkoordinator", pdlLogMessage.getUserTitle());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuildWithUnknownType() {
        testee.buildLogMessage(TestDataGen.buildSjukfallList(1),
            ActivityType.EMERGENCY_ACCESS, ResourceType.RESOURCE_TYPE_SJUKFALL, TestDataGen.buildRehabStodUser(true));
    }
}
