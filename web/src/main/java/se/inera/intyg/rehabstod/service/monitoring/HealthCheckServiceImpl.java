/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.service.monitoring;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.monitoring.annotation.PrometheusTimeMethod;
import se.inera.intyg.rehabstod.integration.it.client.IntygstjanstClientService;
import se.inera.intyg.rehabstod.service.monitoring.dto.HealthStatus;
import se.riv.itintegration.monitoring.v1.PingForConfigurationResponseType;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import java.util.Enumeration;
import java.util.List;

/**
 * Service for getting the health status of the application.
 */
@Service("healthCheckService")
public class HealthCheckServiceImpl implements HealthCheckService {

    private static final Logger LOG = LoggerFactory.getLogger(HealthCheckServiceImpl.class);

    private static final long START_TIME = System.currentTimeMillis();
    private static final long BAD_HEALTHSTATUS_MEASUREMNET = -1L;

    // JMS
    @Autowired
    @Qualifier("jmsPDLLogTemplate")
    private JmsTemplate jmsPDLLogTemplate;

    // JMS
    @Autowired
    @Qualifier("jmsAggregatedPDLLogTemplate")
    private JmsTemplate jmsAggregatedPDLLogTemplate;

    // User count
    @Autowired
    private SessionRegistry sessionRegistry;

    // Pings
    @Autowired
    private IntygstjanstClientService intygstjanstClientService;

    @Override
    public HealthStatus checkPdlLogQueue() {
        HealthStatus healthStatus = checkQueueDepth(jmsPDLLogTemplate);
        LOG.info("Operation checkPdlLogQueue completed with queue size {}", healthStatus.getMeasurement());
        return healthStatus;
    }

    @Override
    public HealthStatus checkPdlAggregatedLogQueue() {
        HealthStatus healthStatus = checkQueueDepth(jmsAggregatedPDLLogTemplate);
        LOG.info("Operation checkPdlAggregatedLogQueue completed with queue size {}", healthStatus.getMeasurement());
        return healthStatus;
    }

    @Override
    @PrometheusTimeMethod
    public HealthStatus checkActiveMQ() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        boolean ok = checkJmsConnection(jmsPDLLogTemplate.getConnectionFactory());
        stopWatch.stop();
        HealthStatus status = createStatusWithTiming(ok, stopWatch);
        logStatus("checkActiveMQ", status);
        return status;
    }

    @Override
    public HealthStatus checkNbrOfUsers() {
        boolean ok;
        long size = -1;
        try {
            List<Object> allPrincipals = sessionRegistry.getAllPrincipals();
            size = allPrincipals.size();
            ok = true;
        } catch (Exception e) {
            LOG.warn("Operation checkNbrOfUsers failed", e);
            ok = false;
        }

        String result = ok ? "OK" : "FAIL";
        LOG.info("Operation checkNbrOfUsers completed with result {}, nbr of users is {}", result, size);

        return new HealthStatus(size, ok);
    }

    @Override
    public HealthStatus checkHSA() {
        // INTYG-2352: Not possible to ping NTjP PROD, disabling this check for now.
        return new HealthStatus(-1, true);
        // return pingForConfigurationService.pingNtjp("");
    }

    @Override
    public HealthStatus checkIntygstjansten() {
        boolean ok;

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            PingForConfigurationResponseType pingResponse = intygstjanstClientService.pingForConfiguration();
            ok = pingResponse != null && pingResponse.getPingDateTime() != null;
        } catch (Exception e) {
            LOG.warn("Operation checkIntygstjansten failed", e);
            ok = false;
        }

        stopWatch.stop();
        HealthStatus status = createStatusWithTiming(ok, stopWatch);
        logStatus("checkIntygstjansten", status);
        return status;
    }

    @Override
    @PrometheusTimeMethod
    public HealthStatus checkUptime() {
        long uptime = System.currentTimeMillis() - START_TIME;
        LOG.info("Current system uptime is {}", DurationFormatUtils.formatDurationWords(uptime, true, true));
        return new HealthStatus(uptime, true);
    }

    @Override
    public String checkUptimeAsString() {
        HealthStatus uptime = checkUptime();
        return DurationFormatUtils.formatDurationWords(uptime.getMeasurement(), true, true);
    }

    private void logStatus(String operation, HealthStatus status) {
        String result = status.isOk() ? "OK" : "FAIL";
        LOG.info("Operation {} completed with result {} in {} ms", operation, result, status.getMeasurement());
    }

    private HealthStatus createStatusWithTiming(boolean ok, StopWatch stopWatch) {
        return new HealthStatus(stopWatch.getTime(), ok);
    }

    private boolean checkJmsConnection(ConnectionFactory connectionFactory) {
        try {
            Connection connection = connectionFactory.createConnection();
            connection.close();
        } catch (JMSException e) {
            LOG.error("checkJmsConnection failed with JMSException: {}", e);
            return false;
        } catch (Exception e) {
            LOG.error(String.format("checkJmsConnection failed with exception of class: %s. Message: %s", e.getClass().getName(),
                    e.getMessage()), e);
            return false;
        }
        return true;
    }

    private HealthStatus checkQueueDepth(JmsTemplate tpl) {
        long queueDepth = 0;
        boolean status = false;

        try {
            queueDepth = tpl.browse((session, browser) -> {
                Enumeration<?> enumeration = browser.getEnumeration();
                long qd = 0;
                while (enumeration.hasMoreElements()) {
                    enumeration.nextElement();
                    qd++;
                }
                return qd;
            });

            status = true;

        } catch (Exception e) {
            LOG.warn("Error when checking queue depth", e);
            queueDepth = BAD_HEALTHSTATUS_MEASUREMNET;
        }

        return new HealthStatus(queueDepth, status);
    }

}
