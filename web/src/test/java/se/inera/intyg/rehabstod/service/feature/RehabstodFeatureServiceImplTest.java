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
package se.inera.intyg.rehabstod.service.feature;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.NotImplementedException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import se.inera.intyg.infra.security.common.service.PilotService;

@RunWith(MockitoJUnitRunner.class)
public class RehabstodFeatureServiceImplTest {

    @Mock
    private PilotService pilotService;

    @Mock
    private Properties env;

    @InjectMocks
    private RehabstodFeatureServiceImpl featureService;

    @Test
    public void testSetup() {

        when(env.getProperty(RehabstodFeature.SRS.getName())).thenReturn("true");

        featureService.initFeaturesMap();
        Set<String> res = featureService.getActiveFeatures();

        assertNotNull(res);
        assertEquals(1, res.size());
        assertTrue(featureService.isFeatureActive(RehabstodFeature.SRS));
        assertTrue(featureService.isFeatureActive(RehabstodFeature.SRS.getName()));
        assertFalse(featureService.isFeatureActive("otherFeature"));
        verify(env).getProperty(RehabstodFeature.SRS.getName());
        // Logs the active features once
        verify(pilotService, times(2)).getFeatures(eq(Collections.emptyList()));
        verifyNoMoreInteractions(env);
        verifyNoMoreInteractions(pilotService);
    }

    @Test
    public void testActiveFeaturesPilot() {

        when(env.getProperty(RehabstodFeature.SRS.getName())).thenReturn("true");
        final String hsaId1 = "hsaId1";
        final String hsaId2 = "hsaId2";
        Map<String, Boolean> pilotMap = new HashMap<>();
        final String prop1 = "prop1";
        final String prop2 = "prop2";
        pilotMap.put(prop1, true);
        pilotMap.put(prop2, false);
        pilotMap.put(RehabstodFeature.SRS.getName(), false);
        when(pilotService.getFeatures(eq(Arrays.asList(hsaId1, hsaId2)))).thenReturn(pilotMap);

        featureService.initFeaturesMap();
        Set<String> res = featureService.getActiveFeatures(hsaId1, hsaId2);

        assertNotNull(res);
        assertEquals(1, res.size());
        assertEquals(prop1, res.stream().collect(Collectors.joining()));
        verify(env).getProperty(RehabstodFeature.SRS.getName());
        verify(pilotService).getFeatures(eq(Arrays.asList(hsaId1, hsaId2)));
        // Logs the active features once
        verify(pilotService).getFeatures(eq(Collections.emptyList()));
        verifyNoMoreInteractions(env);
        verifyNoMoreInteractions(pilotService);
    }

    @Test(expected = NotImplementedException.class)
    public void testModuleFeatureNotImplemented() {
        featureService.isModuleFeatureActive("", "");
    }
}
