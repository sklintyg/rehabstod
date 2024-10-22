/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
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