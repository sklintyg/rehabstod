/**
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;
import se.inera.intyg.common.integration.hsa.model.SelectableVardenhet;
import se.inera.intyg.common.logmessages.*;
import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.service.pdl.dto.LogRequest;
import se.inera.intyg.rehabstod.service.pdl.dto.LogUser;
import se.inera.intyg.rehabstod.service.user.UserService;
import se.inera.intyg.rehabstod.web.model.InternalSjukfall;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

//import se.inera.intyg.common.logmessages.IntygDataPrintLogMessage;

/**
 * Implementation of service for logging user actions according to PDL requirements.
 *
 * @author eriklupander
 */
@Service
public class LogServiceImpl implements LogService {

    private static final Logger LOG = LoggerFactory.getLogger(LogServiceImpl.class);

    @Autowired(required = false)
    @Qualifier("jmsPDLLogTemplate")
    private JmsTemplate jmsTemplate;

    @Value("${pdlLogging.systemId}")
    private String systemId;

    @Value("${pdlLogging.systemName}")
    private String systemName;

    @Autowired
    private UserService userService;

    @PostConstruct
    public void checkJmsTemplate() {
        if (jmsTemplate == null) {
            LOG.error("PDL logging is disabled!");
        }
    }

    @Override
    public void logSjukfallData(List<InternalSjukfall> sjukfallList, ActivityType activityType) {
        LogUser user = getLogUser(userService.getUser());

        List<AbstractLogMessage> logRequestList = sjukfallList.stream()
                .map(LogRequestFactory::createLogRequestFromSjukfall)
                .map(logRequest -> populateLogMessage(logRequest, getLogMessageTypeForActivityType(logRequest.getIntygId(), activityType), user))
                .collect(Collectors.toList());

        send(logRequestList, activityType);
    }

    private AbstractLogMessage getLogMessageTypeForActivityType(String intygId, ActivityType activityType) {
        if (activityType.equals(ActivityType.READ)) {
            return new IntygDataLogMessage(intygId);
        } else if (activityType.equals(ActivityType.PRINT)) {
            return new IntygDataLogMessage(intygId);
            //return new IntygDataPrintLogMessage(intygId);
        }

        throw new IllegalArgumentException("No LogMessage type for activityType " + activityType.name() + " defined");

    }

    @Override
    public LogUser getLogUser(RehabstodUser user) {

        LogUser logUser = new LogUser();

        logUser.setUserId(user.getHsaId());
        logUser.setUserName(user.getNamn());

        SelectableVardenhet valdVardenhet = user.getValdVardenhet();
        logUser.setEnhetsId(valdVardenhet.getId());
        logUser.setEnhetsNamn(valdVardenhet.getNamn());

        SelectableVardenhet valdVardgivare = user.getValdVardgivare();
        logUser.setVardgivareId(valdVardgivare.getId());
        logUser.setVardgivareNamn(valdVardgivare.getNamn());

        return logUser;
    }

    private AbstractLogMessage populateLogMessage(LogRequest logRequest, AbstractLogMessage logMsg, LogUser user) {

        populateWithCurrentUserAndCareUnit(logMsg, user);

        Patient patient = new Patient(logRequest.getPatientId(), logRequest.getPatientName());
        logMsg.setPatient(patient);

        String careUnitId = logRequest.getIntygCareUnitId();
        String careUnitName = logRequest.getIntygCareUnitName();

        String careGiverId = logRequest.getIntygCareGiverId();
        String careGiverName = logRequest.getIntygCareGiverName();

        Enhet resourceOwner = new Enhet(careUnitId, careUnitName, careGiverId, careGiverName);
        logMsg.setResourceOwner(resourceOwner);

        logMsg.setSystemId(systemId);
        logMsg.setSystemName(systemName);

        return logMsg;
    }

    private void populateWithCurrentUserAndCareUnit(AbstractLogMessage logMsg, LogUser user) {
        logMsg.setUserId(user.getUserId());
        logMsg.setUserName(user.getUserName());

        Enhet vardenhet = new Enhet(user.getEnhetsId(), user.getEnhetsNamn(), user.getVardgivareId(), user.getVardgivareNamn());
        logMsg.setUserCareUnit(vardenhet);
    }

    private void send(List<AbstractLogMessage> logMsgs, ActivityType activityType) {

        if (jmsTemplate == null) {
            LOG.error("Could not log list of IntygsData, PDL logging is disabled!");
            // LOG.warn("Can not log {} of Intyg '{}' since PDL logging is disabled!", logMsg.getActivityType(),
            // logMsg.getActivityLevel());
            return;
        }

        LOG.info("Logging {} of IntygsData items for activityType {}", logMsgs.size(), activityType.name());
        if (logMsgs.size() > 0) {
            jmsTemplate.send(new MC(logMsgs));
        }
    }

    private static final class MC implements MessageCreator {
        private final List<AbstractLogMessage> logMsg;

        private MC(List<AbstractLogMessage> log) {
            this.logMsg = log;
        }

        @Override
        public Message createMessage(Session session) throws JMSException {
            return session.createObjectMessage((Serializable) logMsg);
        }
    }
}
