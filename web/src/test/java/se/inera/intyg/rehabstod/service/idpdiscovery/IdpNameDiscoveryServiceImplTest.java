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
package se.inera.intyg.rehabstod.service.idpdiscovery;

import com.google.common.base.Strings;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.opensaml.saml2.metadata.provider.ResourceBackedMetadataProvider;
import org.opensaml.util.resource.ClasspathResource;
import org.opensaml.xml.parse.StaticBasicParserPool;
import org.springframework.security.saml.metadata.CachingMetadataManager;
import org.springframework.security.saml.metadata.ExtendedMetadataDelegate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Tests that our application can build a HashMap with IdP EntityID => Name key-value pairs from
 * the SAMBI trial metadata.
 */
@RunWith(MockitoJUnitRunner.class)
public class IdpNameDiscoveryServiceImplTest {

    @Mock
    private CachingMetadataManager cachingMetadataManager;

    @InjectMocks
    private IdpNameDiscoveryServiceImpl testee;

    private List<ExtendedMetadataDelegate> providers = buildProviders();

    @Before
    public void init() {
        when(cachingMetadataManager.getAvailableProviders()).thenReturn(providers);
    }

    @Test
    public void testBuildsMapOk() {

        // When
        Map<String, String> idpNameMap = testee.buildIdpNameMap();

        // Then
        assertEquals(28, idpNameMap.size());
        assertTrue(idpNameMap.entrySet().stream()
                .anyMatch(entry -> entry.getKey().equals("https://testidp.apotea.se/idp")
                && entry.getValue().equals("Apotea")));

        assertTrue(idpNameMap.values().stream().noneMatch(Strings::isNullOrEmpty));

    }

    private List<ExtendedMetadataDelegate> buildProviders() {
        try {
            List<ExtendedMetadataDelegate> providers = new ArrayList<>();
            ResourceBackedMetadataProvider provider = new ResourceBackedMetadataProvider(new Timer("test"), new ClasspathResource("/sambi/idp-sambi.xml"));

            StaticBasicParserPool parserPool = new StaticBasicParserPool();
            parserPool.initialize();

            provider.setParserPool(parserPool);
            provider.initialize();

            ExtendedMetadataDelegate delegate = new ExtendedMetadataDelegate(provider);
            providers.add(delegate);
            return providers;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
