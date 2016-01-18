/**
 * Copyright (C) 2015 Inera AB (http://www.inera.se)
 *
 * This file is part of statistik (https://github.com/sklintyg/statistik).
 *
 * statistik is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * statistik is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.privatlakarportal.service.monitoring;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;

import se.inera.privatlakarportal.common.model.RegistrationStatus;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;

@RunWith(MockitoJUnitRunner.class)
public class MonitoringLogServiceImplTest {
    
    private static final String USER_ID = "USER_ID";
    private static final String AUTHENTICATION_SCHEME = "AUTHENTICATION_SCHEME";
    private static final String HSA_ID = "HSA_ID";
    private static final Long CONSENT_VERSION = 1L;
    
    @Mock
    private Appender<ILoggingEvent> mockAppender;

    @Captor
    private ArgumentCaptor<LoggingEvent> captorLoggingEvent;
    
    MonitoringLogService logService = new MonitoringLogServiceImpl();

    @Before
    public void setup() {
        final Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        logger.addAppender(mockAppender);
    }
    
    @After
    public void teardown() {
        final Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        logger.detachAppender(mockAppender);
    }
    
    @Test
    public void shouldLogUserRegistered() {
        logService.logUserRegistered(USER_ID, CONSENT_VERSION, HSA_ID, RegistrationStatus.AUTHORIZED);
        verifyLog(Level.INFO, "USER_REGISTERED User 'e5bb97d1792ff76e360cd8e928b6b9b53bda3e4fe88b026e961c2facf963a361' registered with consent version '1' and hsaId 'HSA_ID', returned status 'AUTHORIZED'");
    }

    @Test
    public void shouldLogUserDeleted() {
        logService.logUserDeleted(USER_ID);
        verifyLog(Level.INFO, "USER_DELETED User 'e5bb97d1792ff76e360cd8e928b6b9b53bda3e4fe88b026e961c2facf963a361' deleted");
    }

    @Test
    public void shouldLogUserLogin() {
        logService.logUserLogin(USER_ID, AUTHENTICATION_SCHEME);
        verifyLog(Level.INFO, "USER_LOGIN Login user 'e5bb97d1792ff76e360cd8e928b6b9b53bda3e4fe88b026e961c2facf963a361' using scheme 'AUTHENTICATION_SCHEME'");
    }

    @Test
    public void shouldLogUserLogout() {
        logService.logUserLogout(USER_ID, AUTHENTICATION_SCHEME);
        verifyLog(Level.INFO, "USER_LOGOUT Logout user 'e5bb97d1792ff76e360cd8e928b6b9b53bda3e4fe88b026e961c2facf963a361' using scheme 'AUTHENTICATION_SCHEME'");
    }

    @Test
    public void shouldLogUserDetailsChanged() {
        logService.logUserDetailsChanged(USER_ID);
        verifyLog(Level.INFO, "USER_DETAILS_CHANGED Details for user 'e5bb97d1792ff76e360cd8e928b6b9b53bda3e4fe88b026e961c2facf963a361' changed");
    }

    private void verifyLog(Level logLevel, String logMessage) {
        // Verify and capture logging interaction
        verify(mockAppender).doAppend(captorLoggingEvent.capture());
        final LoggingEvent loggingEvent = captorLoggingEvent.getValue();

        // Verify log
        assertThat(loggingEvent.getLevel(), equalTo(logLevel));
        assertThat(loggingEvent.getFormattedMessage(), 
                equalTo(logMessage));
    }
}
