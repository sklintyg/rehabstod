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

import org.opensaml.saml2.metadata.impl.EntityDescriptorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.security.saml.metadata.CachingMetadataManager;
import org.springframework.security.saml.metadata.ExtendedMetadataDelegate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class IdpNameDiscoveryServiceImpl implements IdpNameDiscoveryService {

    private static final Logger LOG = LoggerFactory.getLogger(IdpNameDiscoveryServiceImpl.class);

    @Autowired(required = false)
    private CachingMetadataManager cachingMetadataManager;

    @Override
    public Map<String, String> buildIdpNameMap() {
        if (cachingMetadataManager == null) {
            LOG.warn(
                    "No SAML CachingMetadataManager configured, no name lookup of SAMBI IdP's possible. (Expected if running dev profile)");
            return new HashMap<>();
        }
        List<ExtendedMetadataDelegate> availableProviders = cachingMetadataManager.getAvailableProviders();

        try {
            List<Pair<String, String>> listOfPairs = availableProviders.stream().flatMap(provider -> {
                try {
                    if (provider.getMetadata() != null && provider.getMetadata().getOrderedChildren() != null) {
                        return provider.getMetadata().getOrderedChildren().stream();
                    }
                } catch (Exception ignored) {

                }
                return Stream.empty();
            })
                    .filter(child -> child instanceof EntityDescriptorImpl)
                    .map(child -> {
                        EntityDescriptorImpl edi = (EntityDescriptorImpl) child;
                        if (edi.getIDPSSODescriptor("urn:oasis:names:tc:SAML:2.0:protocol") != null) {
                            if (edi.getOrganization() != null && edi.getOrganization().getDisplayNames() != null) {
                                return Pair.of(edi.getEntityID(), edi.getOrganization().getDisplayNames().stream()
                                        .findFirst().get().getName().getLocalString());
                            } else {
                                return Pair.of(edi.getEntityID(), edi.getEntityID());
                            }
                        }
                        return null;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            Map<String, String> idps = new HashMap<>();
            listOfPairs.stream().forEach(pair -> {
                idps.put(pair.getFirst(), pair.getSecond());
            });

            return idps;
        } catch (Exception e) {
            return new HashMap<>();
        }
    }
}
