package se.inera.intyg.rehabstod.service.user;

import se.inera.intyg.rehabstod.auth.authorities.AuthoritiesConstants;

public interface FeatureService {

    boolean isFeatureActive(String authoritiesConstants);
}
