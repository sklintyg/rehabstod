package se.inera.intyg.rehabstod.logging;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.common.monitoring.util.HashUtility;
import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * Logback converter that returns information about the current user.
 * User info is retrieved from the Spring Security context. If no context
 * is available a NO USER is returned.
 *
 * @author nikpet
 */
public class UserConverter extends ClassicConverter {

    private static final String NO_USER = "NO USER";

    @Override
    public String convert(ILoggingEvent event) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null) {
            return NO_USER;
        }

        Object principal = auth.getPrincipal();

        if (principal instanceof RehabstodUser) {
            RehabstodUser user = (RehabstodUser) auth.getPrincipal();
            return HashUtility.hash(user.getPersonalIdentityNumber());
        }

        return NO_USER;
    }

}
