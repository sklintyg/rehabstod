package se.inera.intyg.rehabstod.service.user;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import se.inera.intyg.rehabstod.auth.RehabstodUser;

/**
 * Created by eriklupander on 2016-01-19.
 */
@Service
public class UserServiceImpl implements UserService {
    @Override
    public RehabstodUser getUser() {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            return null;
        }
        RehabstodUser user = (RehabstodUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return user;
    }
}
