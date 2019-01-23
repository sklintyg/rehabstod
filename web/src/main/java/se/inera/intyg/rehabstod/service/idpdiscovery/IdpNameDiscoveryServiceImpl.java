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

import org.opensaml.saml2.metadata.Organization;
import org.opensaml.saml2.metadata.impl.EntityDescriptorImpl;
import org.opensaml.xml.XMLObject;
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
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class parses the loaded SAML metadata and produces a HashMap with EntityID => Name of the
 * IdP's contained within the metadata.
 *
 * @author eriklupander
 */
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
            return availableProviders.stream()
                    .flatMap(filterNullsAndProduceStream())
                    .filter(child -> child instanceof EntityDescriptorImpl)
                    .map(mapToPair())
                    .filter(Objects::nonNull)
                    .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));

        } catch (Exception e) {
            LOG.error("Error building map for IdP EntityID => Display name: " + e.getMessage() + ". Returning empty map.");
            return new HashMap<>();
        }
    }

    private Function<XMLObject, Pair<String, String>> mapToPair() {
        return child -> {
            EntityDescriptorImpl edi = (EntityDescriptorImpl) child;
            if (edi.getIDPSSODescriptor("urn:oasis:names:tc:SAML:2.0:protocol") != null) {
                Organization organization = edi.getOrganization();
                if (organization != null && organization.getDisplayNames() != null) {
                    return Pair.of(edi.getEntityID(), resolveDisplayName(edi));
                } else {
                    return Pair.of(edi.getEntityID(), edi.getEntityID());
                }
            }
            return null;
        };
    }

    private String resolveDisplayName(EntityDescriptorImpl edi) {
        Organization organization = edi.getOrganization();
        if (organization != null) {

            // Try displaynames first
            if (organization.getDisplayNames() != null && organization.getDisplayNames().stream().findFirst().isPresent()) {
                return organization.getDisplayNames().stream()
                        .findFirst().get().getName().getLocalString();
            }
            // Then try organization name
            if (organization.getOrganizationNames() != null && organization.getOrganizationNames().stream().findFirst().isPresent()) {
                return organization.getOrganizationNames().stream().findFirst().get().getName().getLocalString();
            }
        }
        // Finally, just use the EntityID.
        return edi.getEntityID();

    }

    private Function<ExtendedMetadataDelegate, Stream<? extends XMLObject>> filterNullsAndProduceStream() {
        return provider -> {
            try {
                XMLObject metadata = provider.getMetadata();
                if (metadata != null && metadata.getOrderedChildren() != null) {
                    return metadata.getOrderedChildren().stream();
                }
            } catch (Exception ignored) {
                // Ignore exception, empty stream is returned below.
            }
            return Stream.empty();
        };
    }
}
