/*
 * Copyright (C) 2023 Inera AB (http://www.inera.se)
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

package se.inera.intyg.rehabstod.service.sjukfall;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.infra.security.common.model.Feature;
import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.auth.authorities.AuthoritiesConstants;
import se.inera.intyg.rehabstod.service.user.FeatureServiceImpl;
import se.inera.intyg.rehabstod.service.user.UserService;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FeatureServiceTest {

    @Mock
    UserService userService;

    @InjectMocks
    FeatureServiceImpl featureService;

    RehabstodUser user;

    @Nested
    class SRS {

        @BeforeEach
        void setup() {
            user = mock(RehabstodUser.class);
            when(userService.getUser()).thenReturn(user);
        }

        @Test
        void shouldReturnTrueIfFeatureIsAvailable() {
            final var features = new HashMap<String, Feature>();
            final var feature = new Feature();
            feature.setGlobal(true);
            features.put(AuthoritiesConstants.FEATURE_SRS, feature);
            when(user.getFeatures()).thenReturn(features);

            final var response = featureService.isFeatureActive(AuthoritiesConstants.FEATURE_SRS);

            assertTrue(response);
        }

        @Test
        void shouldReturnFalseIfFeatureIsNotGlobal() {
            final var features = new HashMap<String, Feature>();
            final var feature = new Feature();
            feature.setGlobal(false);
            features.put(AuthoritiesConstants.FEATURE_SRS, feature);
            when(user.getFeatures()).thenReturn(features);

            final var response = featureService.isFeatureActive(AuthoritiesConstants.FEATURE_SRS);

            assertFalse(response);
        }

        @Test
        void shouldReturnFalseIfFeatureIsNotAvailable() {
            final var response = featureService.isFeatureActive(AuthoritiesConstants.FEATURE_SRS);

            assertFalse(response);
        }
    }
}