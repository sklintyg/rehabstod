package se.inera.privatlakarportal.hsa.monitoring;

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

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;

@RunWith(MockitoJUnitRunner.class)
public class MonitoringLogServiceImplTest {
    
    private static final String USER_ID = "USER_ID";
    
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
    public void shouldLogHospWaiting() {
        logService.logHospWaiting(USER_ID);
        verifyLog(Level.INFO, "HOSP_WAITING User 'e5bb97d1792ff76e360cd8e928b6b9b53bda3e4fe88b026e961c2facf963a361' is waiting for HOSP");
    }

    @Test
    public void shouldLogUserAuthorizedInHosp() {
        logService.logUserAuthorizedInHosp(USER_ID);
        verifyLog(Level.INFO, "HOSP_AUTHORIZED User 'e5bb97d1792ff76e360cd8e928b6b9b53bda3e4fe88b026e961c2facf963a361' is authorized doctor in HOSP");
    }

    @Test
    public void shouldLogUserNotAuthorizedInHosp() {
        logService.logUserNotAuthorizedInHosp(USER_ID);
        verifyLog(Level.INFO, "HOSP_NOT_AUTHORIZED User 'e5bb97d1792ff76e360cd8e928b6b9b53bda3e4fe88b026e961c2facf963a361' is not authorized doctor in HOSP");
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
