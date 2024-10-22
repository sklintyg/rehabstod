package se.inera.intyg.rehabstod.auth;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.authentication.event.LogoutSuccessEvent;
import org.springframework.stereotype.Component;
import se.inera.intyg.rehabstod.service.monitoring.MonitoringLogService;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuthenticationEventListener {

    private final MonitoringLogService monitoringLogService;

    @EventListener
    public void onLoginSuccess(InteractiveAuthenticationSuccessEvent success) {
        final var rehabstodUser = getRehabstodUser(success.getAuthentication().getPrincipal());
        rehabstodUser.ifPresent(user ->
            monitoringLogService.logUserLogin(
                user.getHsaId(),
                user.getRoles() != null && user.getRoles().size() == 1 ? user.getRoles().keySet().iterator().next() : "noRole?",
                user.getRoleTypeName(),
                user.getAuthenticationScheme(),
                user.getOrigin()
            )
        );
    }

    @EventListener
    public void onLogoutSuccess(LogoutSuccessEvent success) {
        final var rehabstodUser = getRehabstodUser(success.getAuthentication().getPrincipal());
        rehabstodUser.ifPresent(user ->
            monitoringLogService.logUserLogout(
                user.getHsaId(),
                user.getAuthenticationScheme()
            )
        );
    }

    private static Optional<RehabstodUser> getRehabstodUser(Object principal) {
        if (principal instanceof RehabstodUser rehabstodUser) {
            return Optional.of(rehabstodUser);
        }
        log.warn("Invalid principal [{}]", principal.getClass().getSimpleName());
        return Optional.empty();
    }
}