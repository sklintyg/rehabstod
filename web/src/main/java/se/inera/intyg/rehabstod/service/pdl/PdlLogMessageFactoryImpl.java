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
package se.inera.intyg.rehabstod.service.pdl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.logmessages.ActivityType;
import se.inera.intyg.infra.logmessages.Enhet;
import se.inera.intyg.infra.logmessages.Patient;
import se.inera.intyg.infra.logmessages.PdlLogMessage;
import se.inera.intyg.infra.logmessages.PdlResource;
import se.inera.intyg.infra.logmessages.ResourceType;
import se.inera.intyg.rehabstod.auth.pdl.PDLActivityEntry;
import se.inera.intyg.rehabstod.common.logging.pdl.SjukfallDataLogMessage;
import se.inera.intyg.rehabstod.service.pdl.dto.LogPatient;
import se.inera.intyg.rehabstod.service.pdl.dto.LogUser;
import se.inera.intyg.rehabstod.web.model.LUCertificate;
import se.inera.intyg.rehabstod.web.model.SjukfallEnhet;

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

        unsetValuesdueToRegulations(pdlLogMessage);

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
            buildPdlLogResource(logPatient.getPatientId(), logPatient.getPatientNamn(), logPatient.getEnhetsId(),
                logPatient.getEnhetsNamn(), logPatient.getVardgivareId(), logPatient.getVardgivareNamn(), resourceType));

        unsetValuesdueToRegulations(pdlLogMessage);

        return pdlLogMessage;
    }

    @Override
    public PdlLogMessage buildLogMessage(List<LUCertificate> luCertificateList, LogUser logUser, ActivityType activityType,
        ResourceType resourceType, Map<String, List<PDLActivityEntry>> storedActivities) {
        PdlLogMessage pdlLogMessage = getLogMessage(activityType);
        populateWithCurrentUserAndCareUnit(pdlLogMessage, logUser);

        // Add resources
        pdlLogMessage.getPdlResourceList().addAll(
            luCertificateList.stream().filter(distinctByKey(c -> c.getPatient().getId()))
                .map(c -> buildPdlLogResource(c, logUser, resourceType))
                .collect(Collectors.toList()));

        unsetValuesdueToRegulations(pdlLogMessage);

        return pdlLogMessage;
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    private PdlResource buildPdlLogResource(LUCertificate certificate, LogUser user, ResourceType resourceType) {
        PdlResource pdlResource = new PdlResource();
        pdlResource.setPatient(getPatient(certificate));
        pdlResource.setResourceOwner(getEnhet(certificate, user));
        pdlResource.setResourceType(resourceType.getResourceTypeName());

        return pdlResource;
    }

    private Patient getPatient(LUCertificate certificate) {
        return createPatient(certificate.getPatient().getId(), certificate.getPatient().getNamn());
    }

    private Enhet getEnhet(LUCertificate certificate, LogUser user) {
        String vardenhetId = getConditionalValue(certificate.getCareUnitId(), user.getEnhetsId());
        String vardenhetNamn = getConditionalValue(certificate.getCareUnitName(), user.getEnhetsNamn());
        String vardgivareId = getConditionalValue(certificate.getCareProviderId(), user.getVardgivareId());
        String vardgivareNamn = getConditionalValue(certificate.getCareProviderName(), user.getVardgivareNamn());

        return createEnhet(vardenhetId, vardenhetNamn, vardgivareId, vardgivareNamn);
    }

    private PdlResource buildPdlLogResource(SjukfallEnhet sfe, LogUser user, ResourceType resourceType) {
        PdlResource pdlResource = new PdlResource();
        pdlResource.setPatient(getPatient(sfe));
        pdlResource.setResourceOwner(getEnhet(sfe, user));
        pdlResource.setResourceType(resourceType.getResourceTypeName());

        return pdlResource;
    }

    private PdlResource buildPdlLogResource(String patientId,
        String patientName,
        String vardenhetId,
        String vardenhetNamn,
        String vardgivareId,
        String vardgivareNamn,
        ResourceType resourceType) {

        Patient patient = createPatient(patientId, patientName);
        Enhet enhet = createEnhet(vardenhetId, vardenhetNamn, vardgivareId, vardgivareNamn);

        PdlResource pdlResource = new PdlResource();
        pdlResource.setPatient(patient);
        pdlResource.setResourceOwner(enhet);
        pdlResource.setResourceType(resourceType.getResourceTypeName());

        return pdlResource;
    }

    private Enhet createEnhet(String vardenhetId, String vardenhetNamn, String vardgivareId, String vardgivareNamn) {
        return new Enhet(vardenhetId, vardenhetNamn, vardgivareId, vardgivareNamn);
    }

    private Patient createPatient(String patientId, String patientName) {
        return new Patient(patientId.replace("-", "").replace("+", ""), patientName);
    }

    private String getConditionalValue(String preferred, String alternative) {
        return preferred != null ? preferred : alternative;
    }

    private Enhet getEnhet(SjukfallEnhet sfe, LogUser user) {
        String vardenhetId = getConditionalValue(sfe.getVardEnhetId(), user.getEnhetsId());
        String vardenhetNamn = getConditionalValue(sfe.getVardEnhetNamn(), user.getEnhetsNamn());
        String vardgivareId = getConditionalValue(sfe.getVardGivareId(), user.getVardgivareId());
        String vardgivareNamn = getConditionalValue(sfe.getVardGivareNamn(), user.getVardgivareNamn());

        return createEnhet(vardenhetId, vardenhetNamn, vardgivareId, vardgivareNamn);
    }

    private Patient getPatient(SjukfallEnhet sfe) {
        return createPatient(sfe.getPatient().getId(), sfe.getPatient().getNamn());
    }

    private PdlLogMessage getLogMessage(ActivityType activityType) {
        PdlLogMessage pdlLogMessage = getLogMessageTypeForActivityType(activityType);
        pdlLogMessage.setSystemId(systemId);
        pdlLogMessage.setSystemName(systemName);
        return pdlLogMessage;
    }

    private PdlLogMessage getLogMessageTypeForActivityType(ActivityType activityType) {
        switch (activityType) {
            case READ:
                return SjukfallDataLogMessage.build();
            case CREATE:
                return SjukfallDataLogMessage.build(ActivityType.CREATE);
            case PRINT:
                return SjukfallDataLogMessage.build(ActivityType.PRINT);
            default:
                throw new IllegalArgumentException("No LogMessage type for activityType " + activityType.name() + " defined");
        }
    }

    private void populateWithCurrentUserAndCareUnit(PdlLogMessage logMsg, LogUser user) {
        logMsg.setUserId(user.getUserId());
        logMsg.setUserName(user.getUserName());
        logMsg.setUserAssignment(user.getUserAssignment());
        logMsg.setUserTitle(user.getUserTitle());

        Enhet vardenhet = new Enhet(user.getEnhetsId(), user.getEnhetsNamn(), user.getVardgivareId(), user.getVardgivareNamn());
        logMsg.setUserCareUnit(vardenhet);
    }

    private void unsetValuesdueToRegulations(final PdlLogMessage logMessage) {
        // INTYG-8349: Inget användarnamn vid PDL-logging
        logMessage.setUserName("");

        // INTYG-4647: Inget patientnamn vid PDL-logging
        logMessage.getPdlResourceList().forEach(pdlResource ->
            pdlResource.setPatient(createPatient(pdlResource.getPatient().getPatientId(), ""))
        );
    }

}
