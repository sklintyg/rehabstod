package se.inera.privatlakarportal.logging;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import se.inera.privatlakarportal.auth.PrivatlakarUser;
import se.inera.privatlakarportal.common.monitoring.util.HashUtility;
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

        if (principal instanceof PrivatlakarUser) {
            PrivatlakarUser user = (PrivatlakarUser) auth.getPrincipal();
            return HashUtility.hash(user.getPersonalIdentityNumber());
        }

        return NO_USER;
    }

}
