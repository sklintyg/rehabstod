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

import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import se.inera.intyg.common.integration.hsa.model.SelectableVardenhet;
import se.inera.intyg.common.logmessages.AbstractLogMessage;
import se.inera.intyg.common.logmessages.Enhet;
import se.inera.intyg.common.logmessages.IntygCreateMessage;

import se.inera.intyg.common.logmessages.Patient;
import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.service.pdl.dto.LogRequest;
import se.inera.intyg.rehabstod.service.pdl.dto.LogUser;
import se.inera.intyg.rehabstod.service.user.UserService;


/**
 * Implementation of service for logging user actions according to PDL requirements.
 *
 * @author nikpet
 */
@Service
public class LogServiceImpl implements LogService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogServiceImpl.class);

    private static final String PRINTED_AS_PDF = "Intyget utskrivet som PDF";
    private static final String PRINTED_AS_DRAFT = "Intyget utskrivet som utkast";

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
            LOGGER.error("PDL logging is disabled!");
        }
    }

    @Override
    public void logCreateIntyg(LogRequest logRequest) {
        logCreateIntyg(logRequest, getLogUser(userService.getUser()));
    }

    @Override
    public void logCreateIntyg(LogRequest logRequest, LogUser user) {
        send(populateLogMessage(logRequest, new IntygCreateMessage(logRequest.getIntygId()), user));
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

    private void send(AbstractLogMessage logMsg) {

        if (jmsTemplate == null) {
            LOGGER.warn("Can not log {} of Intyg '{}' since PDL logging is disabled!", logMsg.getActivityType(), logMsg.getActivityLevel());
            return;
        }

        LOGGER.debug("Logging {} of Intyg {}", logMsg.getActivityType(), logMsg.getActivityLevel());

        jmsTemplate.send(new MC(logMsg));
    }

    private static final class MC implements MessageCreator {
        private final AbstractLogMessage logMsg;

        private MC(AbstractLogMessage log) {
            this.logMsg = log;
        }

        @Override
        public Message createMessage(Session session) throws JMSException {
            return session.createObjectMessage(logMsg);
        }
    }
}
