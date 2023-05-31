package se.inera.intyg.rehabstod.service.user;

import se.inera.intyg.infra.security.common.model.Feature;
import se.inera.intyg.rehabstod.auth.authorities.AuthoritiesConstants;

import java.util.Optional;

public class FeatureServiceImpl implements FeatureService {
    private final UserService userService;

    public FeatureServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean isFeatureActive(String authoritiesConstants) {
        return Optional.ofNullable(userService.getUser().getFeatures())
                .map(features -> features.get(authoritiesConstants))
                .map(Feature::getGlobal)
                .orElse(false);
    }
}
