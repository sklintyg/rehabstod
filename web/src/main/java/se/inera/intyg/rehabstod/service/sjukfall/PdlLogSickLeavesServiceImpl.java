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

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.logmessages.ActivityType;
import se.inera.intyg.infra.logmessages.ResourceType;
import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.auth.authorities.AuthoritiesConstants;
import se.inera.intyg.rehabstod.auth.pdl.PDLActivityStore;
import se.inera.intyg.rehabstod.service.pdl.LogService;
import se.inera.intyg.rehabstod.service.user.UserService;
import se.inera.intyg.rehabstod.web.controller.api.util.ControllerUtil;
import se.inera.intyg.rehabstod.web.model.SjukfallEnhet;

@Service
public class PdlLogSickLeavesServiceImpl implements PdlLogSickLeavesService {

    private final UserService userService;

    private final LogService logService;

    public PdlLogSickLeavesServiceImpl(UserService userService, LogService logService) {
        this.userService = userService;
        this.logService = logService;
    }

    @Override
    public void log(List<SjukfallEnhet> sickLeaves, ActivityType activityType, ResourceType resourceType) {
        final var user = userService.getUser();
        final var unitId = ControllerUtil.getEnhetsIdForQueryingIntygstjansten(user);
        if (unitId == null) {
            throw new IllegalArgumentException("Cannot create PDL log statements, unitId was null");
        }

        final var sickLeavesToLog = PDLActivityStore.getActivitiesNotInStore(
            unitId, sickLeaves, activityType, resourceType, user.getStoredActivities()
        );
        logService.logSjukfallData(sickLeavesToLog, activityType, resourceType);
        PDLActivityStore.addActivitiesToStore(unitId, sickLeavesToLog, activityType, resourceType, user.getStoredActivities());
    }

    @Override
    public void logPrint(List<SjukfallEnhet> sickLeaves) {
        final var user = userService.getUser();
        log(sickLeaves, ActivityType.PRINT, ResourceType.RESOURCE_TYPE_SJUKFALL);

        if (isActivatedForSRS(user)) {
            log(filterHavingRiskSignal(sickLeaves), ActivityType.PRINT, ResourceType.RESOURCE_TYPE_PREDIKTION_SRS);
        }
    }

    private List<SjukfallEnhet> filterHavingRiskSignal(List<SjukfallEnhet> finalList) {
        if (finalList == null) {
            return null;
        }
        return finalList.stream()
            .filter(hasRiskSignal())
            .collect(Collectors.toList());
    }

    private Predicate<SjukfallEnhet> hasRiskSignal() {
        return se -> se.getRiskSignal() != null && se.getRiskSignal().getRiskKategori() >= 1;
    }

    private boolean isActivatedForSRS(RehabstodUser user) {
        return user.isFeatureActive(AuthoritiesConstants.FEATURE_SRS);
    }
}
