/*
 * Copyright (C) 2025 Inera AB (http://www.inera.se)
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

package se.inera.intyg.rehabstod.web.controller.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static se.inera.intyg.rehabstod.web.controller.api.ConfigController.PROJECT_VERSION_PROPERTY;
import static se.inera.intyg.rehabstod.web.controller.api.ConfigController.SITHS_IDP_URL;
import static se.inera.intyg.rehabstod.web.controller.api.ConfigController.WEBCERT_LAUNCH_URL_TEMPLATE;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import se.inera.intyg.infra.dynamiclink.service.DynamicLinkService;
import se.inera.intyg.infra.integration.ia.services.IABannerService;
import se.inera.intyg.rehabstod.service.diagnos.DiagnosKapitelService;

@ExtendWith(MockitoExtension.class)
class ConfigControllerTest {

    @Mock
    private DiagnosKapitelService diagnosKapitelService;

    @Mock
    private DynamicLinkService dynamicLinkService;

    @Mock
    private IABannerService iaBannerService;

    @Mock
    private Environment env;

    @InjectMocks
    private ConfigController configController;

    @Nested
    class GetConfigTest {

        private static final String EXPECTED_WEBCERT_LAUNCH_URL_TEMPLATE = "webcert-launch-url-template";
        private static final String EXPECTED_PROJECT_VERSION_PROPERTY = "project-version";
        private static final String EXPECTED_SITHS_IDP_URL = "/saml/login";

        @BeforeEach
        void setUp() {
            doReturn(EXPECTED_WEBCERT_LAUNCH_URL_TEMPLATE).when(env).getProperty(WEBCERT_LAUNCH_URL_TEMPLATE);
            doReturn(EXPECTED_SITHS_IDP_URL).when(env).getProperty(SITHS_IDP_URL);
            doReturn(EXPECTED_PROJECT_VERSION_PROPERTY).when(env).getProperty(PROJECT_VERSION_PROPERTY);
        }

        @Test
        void shallIncludeWebcertLaunchUrlTemplate() {
            assertEquals(EXPECTED_WEBCERT_LAUNCH_URL_TEMPLATE,
                configController.getConfig().getWebcertLaunchUrlTemplate()
            );
        }

        @Test
        void shallIncludeProjectVersion() {
            assertEquals(EXPECTED_PROJECT_VERSION_PROPERTY,
                configController.getConfig().getVersion()
            );
        }

        @Test
        void shallIncludeSithsIdpUrl() {
            assertEquals(EXPECTED_SITHS_IDP_URL,
                configController.getConfig().getSithsIdpUrl()
            );
        }
    }
}
