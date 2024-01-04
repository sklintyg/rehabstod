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

package se.inera.intyg.rehabstod.service.sjukfall;

import java.time.LocalDate;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.sjukfall.dto.RekoStatusTypeDTO;
import se.inera.intyg.rehabstod.integration.it.dto.GetRekoStatusRequestDTO;
import se.inera.intyg.rehabstod.integration.it.service.IntygstjanstRestIntegrationService;
import se.inera.intyg.rehabstod.service.sjukfall.dto.RekoStatusDTO;
import se.inera.intyg.rehabstod.service.user.UserService;
import se.inera.intyg.rehabstod.web.controller.api.util.ControllerUtil;

@Service
public class GetRekoStatusServiceImpl implements GetRekoStatusService {

    private final IntygstjanstRestIntegrationService intygstjanstRestIntegrationService;
    private final UserService userService;

    public GetRekoStatusServiceImpl(IntygstjanstRestIntegrationService intygstjanstRestIntegrationService,
        UserService userService) {
        this.intygstjanstRestIntegrationService = intygstjanstRestIntegrationService;
        this.userService = userService;
    }

    @Override
    public RekoStatusDTO get(String patientId, LocalDate endDate, LocalDate startDate) {
        final var user = userService.getUser();
        final var careUnitId = ControllerUtil.getEnhetsIdForQueryingIntygstjansten(user);
        final var request = new GetRekoStatusRequestDTO(patientId, endDate, startDate, careUnitId);

        return convertResponse(intygstjanstRestIntegrationService.getRekoStatus(request));
    }

    private RekoStatusDTO convertResponse(se.inera.intyg.rehabstod.integration.it.dto.RekoStatusDTO response) {
        if (response == null) {
            return new RekoStatusDTO();
        }

        return new RekoStatusDTO(
            new RekoStatusTypeDTO(
                response.getStatus().getId(),
                response.getStatus().getName()
            )
        );
    }
}
