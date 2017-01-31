/**
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
 *
 * This file is part of rehabstod (https://github.com/sklintyg/rehabstod).
 *
 * rehabstod is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * rehabstod is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.intyg.rehabstod.service.pdl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.integration.hsa.model.SelectableVardenhet;
import se.inera.intyg.infra.logmessages.ActivityType;
import se.inera.intyg.infra.logmessages.Enhet;
import se.inera.intyg.infra.logmessages.Patient;
import se.inera.intyg.infra.logmessages.PdlLogMessage;
import se.inera.intyg.infra.logmessages.PdlResource;
import se.inera.intyg.infra.logmessages.ResourceType;
import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.common.logging.pdl.SjukfallDataLogMessage;
import se.inera.intyg.rehabstod.common.logging.pdl.SjukfallDataPrintLogMessage;
import se.inera.intyg.rehabstod.service.pdl.dto.LogUser;
import se.inera.intyg.rehabstod.web.model.InternalSjukfall;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by eriklupander on 2016-03-03.
 */
@Service
public class PdlLogMessageFactoryImpl implements PdlLogMessageFactory {

    @Value("${pdlLogging.systemId}")
    private String systemId;

    @Value("${pdlLogging.systemName}")
    private String systemName;

    @Override
    public PdlLogMessage buildLogMessage(List<InternalSjukfall> sjukfallList, ActivityType activityType, RehabstodUser rehabstodUser) {
        LogUser user = getLogUser(rehabstodUser);

        PdlLogMessage pdlLogMessage = getLogMessageTypeForActivityType(activityType);
        pdlLogMessage.setSystemId(systemId);
        pdlLogMessage.setSystemName(systemName);
        populateWithCurrentUserAndCareUnit(pdlLogMessage, user);

        pdlLogMessage.getPdlResourceList().addAll(
                sjukfallList.stream()
                        .map(this::buildPdlLogResource)
                        .collect(Collectors.toList()));
        return pdlLogMessage;
    }

    private PdlResource buildPdlLogResource(InternalSjukfall sf) {
        PdlResource pdlResource = new PdlResource();
        Patient patient = new Patient(sf.getSjukfall().getPatient().getId().replace("-", "").replace("+", ""), sf.getSjukfall().getPatient().getNamn());
        pdlResource.setPatient(patient);

        Enhet resourceOwner = new Enhet(sf.getVardEnhetId(), sf.getVardEnhetNamn(), sf.getVardGivareId(), sf.getVardGivareNamn());
        pdlResource.setResourceOwner(resourceOwner);
        pdlResource.setResourceType(ResourceType.RESOURCE_TYPE_OVERSIKT_SJUKFALL.getResourceTypeName());
        return pdlResource;
    }

    private PdlLogMessage getLogMessageTypeForActivityType(ActivityType activityType) {
        if (activityType.equals(ActivityType.READ)) {
            return SjukfallDataLogMessage.build();
        } else if (activityType.equals(ActivityType.PRINT)) {
            return SjukfallDataPrintLogMessage.build();
        }
        throw new IllegalArgumentException("No LogMessage type for activityType " + activityType.name() + " defined");
    }

    private LogUser getLogUser(RehabstodUser user) {
        SelectableVardenhet valdVardgivare = user.getValdVardgivare();
        SelectableVardenhet valdVardenhet = user.getValdVardenhet();

        return new LogUser.Builder(user.getHsaId(), valdVardenhet.getId(), valdVardgivare.getId())
                .userName(user.getNamn())
                .userAssignment(user.getSelectedMedarbetarUppdragNamn())
                .userTitle(user.getTitel())
                .enhetsNamn(valdVardenhet.getNamn())
                .vardgivareNamn(valdVardgivare.getNamn())
                .build();
    }

    private void populateWithCurrentUserAndCareUnit(PdlLogMessage logMsg, LogUser user) {
        logMsg.setUserId(user.getUserId());
        logMsg.setUserName(user.getUserName());
        logMsg.setUserAssignment(user.getUserAssignment());
        logMsg.setUserTitle(user.getUserTitle());

        Enhet vardenhet = new Enhet(user.getEnhetsId(), user.getEnhetsNamn(), user.getVardgivareId(), user.getVardgivareNamn());
        logMsg.setUserCareUnit(vardenhet);
    }

}
