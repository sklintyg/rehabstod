/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.service.pdl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.logmessages.ActivityType;
import se.inera.intyg.infra.logmessages.Enhet;
import se.inera.intyg.infra.logmessages.Patient;
import se.inera.intyg.infra.logmessages.PdlLogMessage;
import se.inera.intyg.infra.logmessages.PdlResource;
import se.inera.intyg.infra.logmessages.ResourceType;
import se.inera.intyg.rehabstod.common.logging.pdl.SjukfallDataLogMessage;
import se.inera.intyg.rehabstod.common.logging.pdl.SjukfallDataPrintLogMessage;
import se.inera.intyg.rehabstod.service.pdl.dto.LogPatient;
import se.inera.intyg.rehabstod.service.pdl.dto.LogUser;
import se.inera.intyg.rehabstod.web.model.SjukfallEnhet;

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
    public PdlLogMessage buildLogMessage(List<SjukfallEnhet> sjukfallList,
                                         LogUser logUser,
                                         ActivityType activityType,
                                         ResourceType resourceType) {

        PdlLogMessage pdlLogMessage = getLogMessage(activityType);
        populateWithCurrentUserAndCareUnit(pdlLogMessage, logUser);

        // Add resources
        pdlLogMessage.getPdlResourceList().addAll(
                sjukfallList.stream()
                        .map(sfe -> buildPdlLogResource(sfe, logUser, resourceType))
                        .collect(Collectors.toList()));

        return pdlLogMessage;
    }

    @Override
    public PdlLogMessage buildLogMessage(LogPatient logPatient,
                                         LogUser logUser,
                                         ActivityType activityType,
                                         ResourceType resourceType) {

        PdlLogMessage pdlLogMessage = getLogMessage(activityType);
        populateWithCurrentUserAndCareUnit(pdlLogMessage, logUser);

        // Add single resource
        pdlLogMessage.getPdlResourceList().add(
                buildPdlLogResource(logPatient.getPatientId(), logPatient.getEnhetsId(), logPatient.getEnhetsNamn(),
                        logPatient.getVardgivareId(), logPatient.getVardgivareNamn(), resourceType));

        return pdlLogMessage;
    }

    private PdlResource buildPdlLogResource(SjukfallEnhet sfe, LogUser user, ResourceType resourceType) {
        PdlResource pdlResource = new PdlResource();
        pdlResource.setPatient(getPatient(sfe));
        pdlResource.setResourceOwner(getEnhet(sfe, user));
        pdlResource.setResourceType(resourceType.getResourceTypeName());

        return pdlResource;
    }

    private PdlResource buildPdlLogResource(String patientId,
                                            String vardenhetId,
                                            String vardenhetNamn,
                                            String vardgivareId,
                                            String vardgivareNamn,
                                            ResourceType resourceType) {

        Patient patient = getPatient(patientId);
        Enhet enhet = getEnhet(vardenhetId, vardenhetNamn, vardgivareId, vardgivareNamn);

        PdlResource pdlResource = new PdlResource();
        pdlResource.setPatient(patient);
        pdlResource.setResourceOwner(enhet);
        pdlResource.setResourceType(resourceType.getResourceTypeName());

        return pdlResource;
    }

    private String getConditionalValue(String preferred, String alternative) {
        return preferred != null ? preferred : alternative;
    }

    private Enhet getEnhet(SjukfallEnhet sfe, LogUser user) {
        String vardenhetId = getConditionalValue(sfe.getVardEnhetId(), user.getEnhetsId());
        String vardenhetNamn = getConditionalValue(sfe.getVardEnhetNamn(), user.getEnhetsNamn());
        String vardgivareId = getConditionalValue(sfe.getVardGivareId(), user.getVardgivareId());
        String vardgivareNamn = getConditionalValue(sfe.getVardGivareNamn(), user.getVardgivareNamn());

        return getEnhet(vardenhetId, vardenhetNamn, vardgivareId, vardgivareNamn);
    }

    private Enhet getEnhet(String vardenhetId, String vardenhetNamn, String vardgivareId, String vardgivareNamn) {
        return new Enhet(vardenhetId, vardenhetNamn, vardgivareId, vardgivareNamn);
    }

    private Patient getPatient(SjukfallEnhet sfe) {
        return getPatient(sfe.getPatient().getId());
    }

    private Patient getPatient(String patientId) {
        return new Patient(patientId.replace("-", "").replace("+", ""), "");
    }

    private PdlLogMessage getLogMessage(ActivityType activityType) {
        PdlLogMessage pdlLogMessage = getLogMessageTypeForActivityType(activityType);
        pdlLogMessage.setSystemId(systemId);
        pdlLogMessage.setSystemName(systemName);
        return pdlLogMessage;
    }

    private PdlLogMessage getLogMessageTypeForActivityType(ActivityType activityType) {
        if (activityType.equals(ActivityType.READ)) {
            return SjukfallDataLogMessage.build();
        } else if (activityType.equals(ActivityType.PRINT)) {
            return SjukfallDataPrintLogMessage.build();
        }
        throw new IllegalArgumentException("No LogMessage type for activityType " + activityType.name() + " defined");
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
