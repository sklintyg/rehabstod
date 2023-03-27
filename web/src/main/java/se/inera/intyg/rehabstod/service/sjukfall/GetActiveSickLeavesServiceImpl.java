/* Copyright (C) 2023 Inera AB (http://www.inera.se)
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.logmessages.ActivityType;
import se.inera.intyg.infra.logmessages.ResourceType;
import se.inera.intyg.infra.sjukfall.dto.IntygParametrar;
import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.integration.it.dto.SickLeavesRequestDTO;
import se.inera.intyg.rehabstod.integration.it.service.IntygstjanstRestIntegrationService;
import se.inera.intyg.rehabstod.service.monitoring.MonitoringLogService;
import se.inera.intyg.rehabstod.service.pu.PuService;
import se.inera.intyg.rehabstod.service.sjukfall.mappers.SjukfallEngineMapper;
import se.inera.intyg.rehabstod.service.user.UserService;
import se.inera.intyg.rehabstod.web.controller.api.util.ControllerUtil;
import se.inera.intyg.rehabstod.web.model.SjukfallEnhet;

@Service
public class GetActiveSickLeavesServiceImpl implements GetActiveSickLeavesService {

    private final UserService userService;
    private final PuService puService;
    private final MonitoringLogService monitoringLogService;
    private final SjukfallEngineMapper sjukfallEngineMapper;
    private final PdlLogSickLeavesService pdlLogSickLeavesService;
    private final IntygstjanstRestIntegrationService intygstjanstRestIntegrationService;

    private static final Logger LOG = LoggerFactory.getLogger(GetActiveSickLeavesServiceImpl.class);

    @Autowired
    public GetActiveSickLeavesServiceImpl(UserService userService, PuService puService, MonitoringLogService monitoringLogService,
        SjukfallEngineMapper sjukfallEngineMapper, PdlLogSickLeavesService pdlLogSickLeavesService,
        IntygstjanstRestIntegrationService intygstjanstRestIntegrationService) {
        this.userService = userService;
        this.puService = puService;
        this.monitoringLogService = monitoringLogService;
        this.sjukfallEngineMapper = sjukfallEngineMapper;
        this.pdlLogSickLeavesService = pdlLogSickLeavesService;
        this.intygstjanstRestIntegrationService = intygstjanstRestIntegrationService;
    }

    @Override
    public List<SjukfallEnhet> get() {
        final var user = userService.getUser();
        final var unitId = ControllerUtil.getEnhetsIdForQueryingIntygstjansten(user);
        final var careUnitId = user.isValdVardenhetMottagning() ? user.getValdVardenhet().getId() : null;
        final var certificateParameters = getCertificateParameters(user);
        final var request = getRequest(user, unitId, careUnitId);

        LOG.debug("Getting sick leaves for unit {}", unitId);
        final var response = intygstjanstRestIntegrationService.getActiveSickLeaves(request);
        final var convertedSickLeaves = convertSickLeaves(response.getContent(), certificateParameters);

        LOG.debug("Add patient names and filter on protected person for sick leaves");
        puService.enrichSjukfallWithPatientNamesAndFilterSekretess(convertedSickLeaves);

        LOG.debug("Logging that sick leaves have been fetched");
        performMonitorLogging(convertedSickLeaves, user.getHsaId(), careUnitId != null ? careUnitId : unitId);
        pdlLogSickLeavesService.log(convertedSickLeaves, ActivityType.READ, ResourceType.RESOURCE_TYPE_SJUKFALL);

        return convertedSickLeaves;
    }

    private SickLeavesRequestDTO getRequest(RehabstodUser user, String unitId, String careUnitId) {
        final var request = new SickLeavesRequestDTO();
        request.setMaxCertificateGap(ControllerUtil.getMaxGlapp(user));
        request.setMaxDaysSinceSickLeaveCompleted(ControllerUtil.getMaxDagarSedanSjukfallAvslut(user));
        request.setUnitId(unitId);
        request.setCareUnitId(careUnitId);
        request.setDoctorId(user.getHsaId());
        return request;

    }

    private List<SjukfallEnhet> convertSickLeaves(
        List<se.inera.intyg.infra.sjukfall.dto.SjukfallEnhet> sickLeavesFromIT, IntygParametrar certificateParameters
    ) {
        return sickLeavesFromIT.stream()
            .map(sickLeave -> sjukfallEngineMapper.mapToSjukfallEnhetDto(
                    sickLeave,
                    certificateParameters.getMaxAntalDagarSedanSjukfallAvslut(),
                    certificateParameters.getAktivtDatum()
                )
            )
            .collect(Collectors.toList());
    }

    private IntygParametrar getCertificateParameters(RehabstodUser user) {
        return new IntygParametrar(
            ControllerUtil.getMaxGlapp(user),
            ControllerUtil.getMaxDagarSedanSjukfallAvslut(user),
            LocalDate.now()
        );
    }

    private void performMonitorLogging(List<SjukfallEnhet> sickLeaves, String hsaId, String unitId) {
        if (sickLeaves == null) {
            return;
        }

        monitoringLogService.logUserViewedSjukfall(
            hsaId,
            sickLeaves.size(),
            unitId
        );
    }
}
