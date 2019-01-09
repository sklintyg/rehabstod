/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.service.user;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.auth.RehabstodUserPreferences;
import se.inera.intyg.rehabstod.persistence.model.AnvandarPreference;
import se.inera.intyg.rehabstod.persistence.repository.AnvandarPreferenceRepository;

@RunWith(MockitoJUnitRunner.class)
public class UserPreferencesServiceTest {
   
    private static final String KEY = "maxAntalDagarMellanIntyg";
    private static final String HSA_ID = "abcdefghijkl";

    @InjectMocks
    private UserPreferencesService userPreferencesService = new UserPreferencesServiceImpl();

    @Mock
    private AnvandarPreferenceRepository anvandarPreferenceRepository;

    @Mock
    private UserService userService;

    @Before
    public void setup() {
        when(userService.getUser()).thenReturn(new RehabstodUser(HSA_ID, "rehabstod-username", true));
    }

    @Test
    public void testUpdatingExistingPreference() throws Exception {
        AnvandarPreference anvPref = new AnvandarPreference(HSA_ID, KEY, "old value");
        when(anvandarPreferenceRepository.findByHsaIdAndKey(eq(HSA_ID), eq(KEY))).thenReturn(anvPref);

        Map<String, String> req = new HashMap<>();
        req.put(KEY, "new value");
        userPreferencesService.updatePreferences(RehabstodUserPreferences.fromFrontend(req));

        assertEquals("new value", anvPref.getValue());
    }

}
