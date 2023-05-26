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
import se.inera.intyg.rehabstod.integration.it.dto.CreateRekoStatusRequestDTO;
import se.inera.intyg.rehabstod.integration.it.service.IntygstjanstRestIntegrationService;
import se.inera.intyg.rehabstod.service.sjukfall.dto.RekoStatusDTO;
import se.inera.intyg.rehabstod.service.user.UserService;
import se.inera.intyg.rehabstod.web.controller.api.util.ControllerUtil;

import java.time.LocalDateTime;

@Service
public class CreateRekoStatusServiceImpl implements CreateRekoStatusService {

    private final IntygstjanstRestIntegrationService intygstjanstRestIntegrationService;
    private final UserService userService;

    public CreateRekoStatusServiceImpl(IntygstjanstRestIntegrationService intygstjanstRestIntegrationService,
                                       UserService userService) {
        this.intygstjanstRestIntegrationService = intygstjanstRestIntegrationService;
        this.userService = userService;
    }

    public RekoStatusDTO create(String patientId, String status, LocalDateTime sickLeaveTimestamp) {
        final var user = userService.getUser();
        final var careUnitId = ControllerUtil.getEnhetsIdForQueryingIntygstjansten(user);
        final var unitId = user.isValdVardenhetMottagning() ? user.getValdVardenhet().getId() : null;

        final var request = new CreateRekoStatusRequestDTO(
                patientId,
                status,
                user.getValdVardgivare().getId(),
                careUnitId,
                unitId,
                user.getHsaId(),
                user.getNamn(),
                sickLeaveTimestamp
        );

        return convertResponse(intygstjanstRestIntegrationService.createRekoStatus(request));
    }

    private RekoStatusDTO convertResponse(se.inera.intyg.rehabstod.integration.it.dto.RekoStatusDTO response) {
        return new RekoStatusDTO(
                response.getId(),
                response.getStatus()
        );
    }
}
