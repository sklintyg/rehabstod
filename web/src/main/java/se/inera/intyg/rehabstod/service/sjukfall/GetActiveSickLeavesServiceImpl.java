/*
 * Copyright (C) 2025 Inera AB (http://www.inera.se)
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
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import se.inera.intyg.rehabstod.integration.it.dto.SickLeavesRequestDTO;
import se.inera.intyg.rehabstod.integration.it.service.IntygstjanstRestIntegrationService;
import se.inera.intyg.rehabstod.logging.SickLeaveLogMessageFactory;
import se.inera.intyg.rehabstod.service.sjukfall.mappers.SjukfallEngineMapper;
import se.inera.intyg.rehabstod.web.model.SjukfallEnhet;

@Service
public class GetActiveSickLeavesServiceImpl implements GetActiveSickLeavesService {

    private static final Logger LOG = LoggerFactory.getLogger(GetActiveSickLeavesServiceImpl.class);
    private final IntygstjanstRestIntegrationService intygstjanstRestIntegrationService;
    private final SjukfallEngineMapper sjukfallEngineMapper;


    public GetActiveSickLeavesServiceImpl(IntygstjanstRestIntegrationService intygstjanstRestIntegrationService,
        SjukfallEngineMapper sjukfallEngineMapper) {
        this.intygstjanstRestIntegrationService = intygstjanstRestIntegrationService;
        this.sjukfallEngineMapper = sjukfallEngineMapper;
    }

    @Override
    public List<SjukfallEnhet> get(SickLeavesRequestDTO request) {
        final var logFactory = new SickLeaveLogMessageFactory(System.currentTimeMillis());
        final var response = intygstjanstRestIntegrationService.getActiveSickLeaves(request);
        final var sickLeaves = response.getContent().stream()
            .map(sickLeave -> sjukfallEngineMapper.mapToSjukfallEnhetDto(
                    sickLeave,
                    request.getMaxDaysSinceSickLeaveCompleted(),
                    LocalDate.now()
                )
            )
            .collect(Collectors.toList());
        LOG.info(logFactory.message(SickLeaveLogMessageFactory.GET_ACTIVE_SICK_LEAVES, response.getContent().size()));
        return sickLeaves;
    }
}
