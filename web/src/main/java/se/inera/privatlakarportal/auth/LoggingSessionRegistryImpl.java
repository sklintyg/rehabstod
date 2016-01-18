package se.inera.privatlakarportal.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistryImpl;

import se.inera.privatlakarportal.service.monitoring.MonitoringLogService;

/**
 * Implementation of SessionRegistry that performs audit logging of login and logout.
 */
public class LoggingSessionRegistryImpl extends SessionRegistryImpl {
    @Autowired
    @Qualifier("webMonitoringLogService")
    private MonitoringLogService monitoringService;

    @Override
    public void registerNewSession(String sessionId, Object principal) {
        if (principal != null && principal instanceof PrivatlakarUser) {
            PrivatlakarUser user = (PrivatlakarUser) principal;
            monitoringService.logUserLogin(user.getPersonalIdentityNumber(), user.getAuthenticationScheme());
        }
        super.registerNewSession(sessionId, principal);
    }

    @Override
    public void removeSessionInformation(String sessionId) {
        SessionInformation sessionInformation = getSessionInformation(sessionId);
        if (sessionInformation != null) {
            Object principal = sessionInformation.getPrincipal();

            if (principal instanceof PrivatlakarUser) {
                // TODO: We could log specifically that a session has expired. Is this something we want to do?
                //       sessionInformation.isExpired()
                PrivatlakarUser user = (PrivatlakarUser) principal;
                monitoringService.logUserLogout(user.getPersonalIdentityNumber(), user.getAuthenticationScheme());
            }
        }
        super.removeSessionInformation(sessionId);
    }
}
