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
package se.inera.intyg.rehabstod.auth.authorities;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import se.inera.intyg.common.integration.hsa.services.HsaPersonService;
import se.inera.intyg.common.security.authorities.CommonAuthoritiesResolver;
import se.inera.intyg.common.security.authorities.bootstrap.AuthoritiesConfigurationLoader;
import se.inera.intyg.common.security.common.model.Privilege;
import se.inera.intyg.common.security.common.model.RequestOrigin;
import se.inera.intyg.common.security.common.model.Title;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Should _only_ contain asserts specific to the Rehabstöd authorities-test.yaml file.
 *
 * Created by eriklupander on 2016-05-17.
 */
@RunWith(MockitoJUnitRunner.class)
public class RehabstodAuthoritiesResolverTest {

    private String configurationLocation = "AuthoritiesConfigurationLoaderTest/authorities-test.yaml";

    @Mock
    private HsaPersonService hsaPersonService;

    @Spy
    private AuthoritiesConfigurationLoader configurationLoader = new AuthoritiesConfigurationLoader(configurationLocation);

    @InjectMocks
    private CommonAuthoritiesResolver authoritiesResolver = new CommonAuthoritiesResolver();

    @Before
    public void setup() throws Exception {
        configurationLoader.afterPropertiesSet();
    }

    @Test
    public void testGetIntygsTyper() throws Exception {
        // Arrange

        // Act
        final List<String> intygstyper = authoritiesResolver.getIntygstyper();

        // Verify
        // (We don't have any in rehabstod)
        assertEquals(0, intygstyper.size());
    }

    @Test
    public void testGetPrivileges() throws Exception {
        // Arrange

        // Act
        final List<Privilege> privileges = authoritiesResolver.getPrivileges();

        // Verify
        assertEquals(1, privileges.size());
        assertEquals("VISA_SJUKFALL", privileges.get(0).getName());
    }

    @Test
    public void testGetRequestOrigins() throws Exception {
        // Arrange

        // Act
        final List<RequestOrigin> requestOrigins = authoritiesResolver.getRequestOrigins();

        // Verify
        assertEquals(1, requestOrigins.size());
        assertEquals("NORMAL", requestOrigins.get(0).getName());
    }

    @Test
    public void testGetTitles() throws Exception {
        // Arrange

        // Act
        final List<Title> titles = authoritiesResolver.getTitles();

        // Verify
        assertEquals(2, titles.size());

    }

}
