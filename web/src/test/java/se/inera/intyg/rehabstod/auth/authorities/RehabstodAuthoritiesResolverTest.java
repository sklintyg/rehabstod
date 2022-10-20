/*
 * Copyright (C) 2022 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.auth.authorities;

import static org.junit.Assert.assertEquals;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import se.inera.intyg.infra.integration.hsatk.services.legacy.HsaPersonService;
import se.inera.intyg.infra.security.authorities.CommonAuthoritiesResolver;
import se.inera.intyg.infra.security.authorities.bootstrap.SecurityConfigurationLoader;
import se.inera.intyg.infra.security.common.model.Privilege;
import se.inera.intyg.infra.security.common.model.RequestOrigin;
import se.inera.intyg.infra.security.common.model.Title;

/**
 * Should _only_ contain asserts specific to the Rehabstöd authorities-test.yaml file.
 * <p>
 * Created by eriklupander on 2016-05-17.
 */
@RunWith(MockitoJUnitRunner.class)
public class RehabstodAuthoritiesResolverTest {

    private final String authoritiesConfigurationFile = "classpath:AuthoritiesConfigurationLoaderTest/authorities-test.yaml";
    private final String featuresConfigurationsFile = "classpath:AuthoritiesConfigurationLoaderTest/features-test.yaml";
    private final Integer defaultMaxAliasesForCollections = 300;

    @Mock
    private HsaPersonService hsaPersonService;

    @Spy
    private SecurityConfigurationLoader configurationLoader = new SecurityConfigurationLoader(authoritiesConfigurationFile,
        featuresConfigurationsFile, defaultMaxAliasesForCollections);

    @InjectMocks
    private CommonAuthoritiesResolver authoritiesResolver = new CommonAuthoritiesResolver();

    @Before
    public void setup() throws Exception {
        configurationLoader.afterPropertiesSet();
    }

    @Test
    public void testGetIntygsTyper() {
        // Arrange

        // Act
        final List<String> intygstyper = authoritiesResolver.getIntygstyper();

        // Verify
        // (We don't have any in rehabstod)
        assertEquals(0, intygstyper.size());
    }

    @Test
    public void testGetPrivileges() {
        // Arrange

        // Act
        final List<Privilege> privileges = authoritiesResolver.getPrivileges();

        // Verify
        assertEquals(1, privileges.size());
        assertEquals("VISA_SJUKFALL", privileges.get(0).getName());
    }

    @Test
    public void testGetRequestOrigins() {
        // Arrange

        // Act
        final List<RequestOrigin> requestOrigins = authoritiesResolver.getRequestOrigins();

        // Verify
        assertEquals(1, requestOrigins.size());
        assertEquals("NORMAL", requestOrigins.get(0).getName());
    }

    @Test
    public void testGetTitles() {
        // Arrange

        // Act
        final List<Title> titles = authoritiesResolver.getTitles();

        // Verify
        assertEquals(2, titles.size());

    }

}
