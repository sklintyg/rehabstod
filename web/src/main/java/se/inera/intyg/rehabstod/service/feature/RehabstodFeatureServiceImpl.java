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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Joiner;

import se.inera.intyg.infra.security.common.service.CommonFeatureService;
import se.inera.intyg.infra.security.common.service.Feature;
import se.inera.intyg.infra.security.common.service.PilotService;

@Service
public class RehabstodFeatureServiceImpl implements CommonFeatureService {

    private static final Logger LOG = LoggerFactory.getLogger(RehabstodFeatureServiceImpl.class);
    private Map<String, Boolean> featuresMap = new HashMap<>();

    @Resource(name = "featureProperties")
    private Properties env;

    @Autowired
    private PilotService pilotService;

    /**
     * Performs initialization of the featuresMap.
     */
    @PostConstruct
    public void initFeaturesMap() {
        featuresMap = Stream.of(RehabstodFeature.values())
                .collect(Collectors.toMap(feature -> feature.getName(),
                        feature -> Optional.of(Boolean.parseBoolean(env.getProperty(feature.getName()))).orElse(Boolean.FALSE)));

        LOG.info("Active Rehabstod features is: {}", Joiner.on(", ").join(getActiveFeatures()));
    }

    @Override
    public boolean isFeatureActive(Feature feature) {
        return isFeatureActive(feature.getName());
    }

    @Override
    public boolean isFeatureActive(String featureName) {
        Boolean featureState = featuresMap.get(featureName);
        return featureState != null && featureState;
    }

    @Override
    public boolean isModuleFeatureActive(String moduleFeatureName, String moduleName) {
        throw new NotImplementedException("There are no modules in rehabstod.");
    }

    @Override
    public Set<String> getActiveFeatures(String... hsaIds) {
        return merge(featuresMap.entrySet().stream()
                .filter(entry -> entry.getValue())
                .map(Entry::getKey)
                .collect(Collectors.toSet()),
                pilotService.getFeatures(Arrays.asList(hsaIds)));
    }

    @Override
    public void setFeature(String key, String value) {
        this.featuresMap.put(key, Boolean.parseBoolean(value));
    }

    private Set<String> merge(Set<String> defaultFeatures, Map<String, Boolean> pilotFeatures) {
        // Separates the pilotfeaturesmap into two sets, one with all positives and one with all the negatives
        Map<Boolean, Set<String>> split = pilotFeatures.entrySet().stream()
                .collect(Collectors.partitioningBy(entry -> entry.getValue(),
                        Collectors.mapping(Entry::getKey, Collectors.toSet())));
        defaultFeatures.addAll(split.get(Boolean.TRUE));
        defaultFeatures.removeAll(split.get(Boolean.FALSE));
        return defaultFeatures;
    }
}
