/*
 * Copyright (C) 2023 Inera AB (http://www.inera.se)
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

package se.inera.intyg.rehabstod.service.sjukfall;

import org.springframework.stereotype.Service;
import se.inera.intyg.rehabstod.integration.it.dto.SetRekoStatusToSickLeaveRequestDTO;
import se.inera.intyg.rehabstod.integration.it.service.IntygstjanstRestIntegrationService;
import se.inera.intyg.rehabstod.service.user.UserService;
import se.inera.intyg.rehabstod.web.controller.api.util.ControllerUtil;

import java.time.LocalDateTime;

@Service
public class SetRekoStatusToSickLeaveServiceImpl implements SetRekoStatusToSickLeaveService {

    private final IntygstjanstRestIntegrationService intygstjanstRestIntegrationService;
    private final UserService userService;

    public SetRekoStatusToSickLeaveServiceImpl(IntygstjanstRestIntegrationService intygstjanstRestIntegrationService,
                                               UserService userService) {
        this.intygstjanstRestIntegrationService = intygstjanstRestIntegrationService;
        this.userService = userService;
    }

    public void set(String patientId, String status, LocalDateTime sickLeaveTimestamp) {
        final var user = userService.getUser();
        final var careUnitId = ControllerUtil.getEnhetsIdForQueryingIntygstjansten(user);
        final var unitId = user.isValdVardenhetMottagning() ? user.getValdVardenhet().getId() : null;

        final var request = new SetRekoStatusToSickLeaveRequestDTO(
                patientId,
                status,
                user.getValdVardgivare().getId(),
                careUnitId,
                unitId,
                user.getHsaId(),
                user.getNamn(),
                sickLeaveTimestamp
        );

        intygstjanstRestIntegrationService.setRekoStatusForSickLeave(request);
    }
}
