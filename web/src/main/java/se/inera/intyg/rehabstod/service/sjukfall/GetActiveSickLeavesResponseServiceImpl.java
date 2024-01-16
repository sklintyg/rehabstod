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

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.logmessages.ActivityType;
import se.inera.intyg.infra.logmessages.ResourceType;
import se.inera.intyg.rehabstod.integration.it.dto.SickLeavesRequestDTO;
import se.inera.intyg.rehabstod.logging.SickLeaveLogMessageFactory;
import se.inera.intyg.rehabstod.service.communication.UnansweredCommunicationDecoratorService;
import se.inera.intyg.rehabstod.service.communication.UnansweredCommunicationFilterService;
import se.inera.intyg.rehabstod.service.exceptions.SRSServiceException;
import se.inera.intyg.rehabstod.service.monitoring.MonitoringLogService;
import se.inera.intyg.rehabstod.service.sjukfall.dto.GetActiveSickLeavesResponseDTO;
import se.inera.intyg.rehabstod.service.sjukfall.nameresolver.SjukfallEmployeeNameResolver;
import se.inera.intyg.rehabstod.service.sjukfall.srs.RiskPredictionService;
import se.inera.intyg.rehabstod.service.user.UserService;
import se.inera.intyg.rehabstod.web.controller.api.dto.SickLeavesFilterRequestDTO;
import se.inera.intyg.rehabstod.web.model.SjukfallEnhet;

@Service
public class GetActiveSickLeavesResponseServiceImpl implements GetActiveSickLeavesResponseService {

    private final SjukfallEmployeeNameResolver sjukfallEmployeeNameResolver;
    private final RiskPredictionService riskPredictionService;
    private final UnansweredCommunicationDecoratorService unansweredCommunicationDecoratorService;
    private final UnansweredCommunicationFilterService unansweredCommunicationFilterService;
    private final CreateSickLeaveRequestService createSickLeaveRequestService;
    private final MonitoringLogService monitoringLogService;

    private final GetActiveSickLeavesService getActiveSickLeavesService;
    private final UserService userService;
    private final PdlLogSickLeavesService pdlLogSickLeavesService;

    private static final Logger LOG = LoggerFactory.getLogger(GetActiveSickLeavesResponseServiceImpl.class);

    @Autowired
    public GetActiveSickLeavesResponseServiceImpl(
        SjukfallEmployeeNameResolver sjukfallEmployeeNameResolver,
        RiskPredictionService riskPredictionService,
        UnansweredCommunicationDecoratorService unansweredCommunicationDecoratorService,
        UnansweredCommunicationFilterService unansweredCommunicationFilterService,
        CreateSickLeaveRequestService createSickLeaveRequestService, MonitoringLogService monitoringLogService,
        GetActiveSickLeavesService getActiveSickLeavesService, UserService userService, PdlLogSickLeavesService pdlLogSickLeavesService) {
        this.sjukfallEmployeeNameResolver = sjukfallEmployeeNameResolver;
        this.riskPredictionService = riskPredictionService;
        this.unansweredCommunicationDecoratorService = unansweredCommunicationDecoratorService;
        this.unansweredCommunicationFilterService = unansweredCommunicationFilterService;
        this.createSickLeaveRequestService = createSickLeaveRequestService;
        this.monitoringLogService = monitoringLogService;
        this.getActiveSickLeavesService = getActiveSickLeavesService;
        this.userService = userService;
        this.pdlLogSickLeavesService = pdlLogSickLeavesService;
    }

    @Override
    public GetActiveSickLeavesResponseDTO get(SickLeavesFilterRequestDTO filterRequest, boolean includeParameters, boolean shouldPdlLog) {
        final var request = createSickLeaveRequestService.create(filterRequest, includeParameters);
        final var sickLeaves = getActiveSickLeavesService.get(request);
        sjukfallEmployeeNameResolver.enrichWithHsaEmployeeNames(sickLeaves);
        sjukfallEmployeeNameResolver.updateDuplicateDoctorNamesWithHsaId(sickLeaves);

        final var hasDecoratedWithUnansweredCommunications = unansweredCommunicationDecoratorService.decorateSickLeaves(sickLeaves);
        final var filteredSickLeaves = unansweredCommunicationFilterService.filter(
            sickLeaves,
            filterRequest.getUnansweredCommunicationFilterTypeId()
        );
        final var hasDecoratedWithSRSInfo = decorateWithSRSInfo(filteredSickLeaves);
        performMonitorLogging(filteredSickLeaves, getUnitForLogging(request));
        if (shouldPdlLog) {
            pdlLogSickLeavesService.log(filteredSickLeaves, ActivityType.READ, ResourceType.RESOURCE_TYPE_SJUKFALL);
        }
        return new GetActiveSickLeavesResponseDTO(
            filteredSickLeaves,
            !hasDecoratedWithSRSInfo,
            !hasDecoratedWithUnansweredCommunications
        );
    }

    private static String getUnitForLogging(SickLeavesRequestDTO request) {
        return request.getUnitId() != null ? request.getUnitId() : request.getCareUnitId();
    }

    private void performMonitorLogging(List<SjukfallEnhet> filteredSickLeaves, String unitId) {
        if (filteredSickLeaves == null || filteredSickLeaves.isEmpty()) {
            return;
        }
        final var user = userService.getUser();
        monitoringLogService.logUserViewedSjukfall(
            user.getHsaId(),
            filteredSickLeaves.size(),
            unitId
        );
    }

    private boolean decorateWithSRSInfo(List<SjukfallEnhet> sickLeaves) {
        final var logFactory = new SickLeaveLogMessageFactory(System.currentTimeMillis());
        logFactory.setStartTimer(System.currentTimeMillis());
        try {
            riskPredictionService.updateWithRiskPredictions(sickLeaves);
        } catch (SRSServiceException e) {
            LOG.info("Unable to update sick leaves with risk prediction cause of SRSServiceException: {}", e.getMessage());
            return false;
        }
        LOG.info(logFactory.message(SickLeaveLogMessageFactory.ADD_SRS_RISK, sickLeaves.size()));
        return true;
    }
}
