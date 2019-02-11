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

import io.prometheus.client.Collector;
import io.prometheus.client.Gauge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import se.inera.intyg.rehabstod.integration.it.client.IntygstjanstClientService;
import se.riv.itintegration.monitoring.v1.PingForConfigurationResponseType;

import javax.annotation.PostConstruct;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Time;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * Exposes health metrics as Prometheus values. To simplify any 3rd party scraping applications, all metrics produced
 * by this component uses the following conventions:
 *
 * All metrics are prefixed with "health_"
 * All metrics are suffixed with their type, either "_normal" that indicates a boolean value 0 or 1 OR
 * "_value" that indiciates a numeric metric of some kind.
 *
 * Note that NORMAL values uses 0 to indicate OK state and 1 to indicate a problem.
 *
 * The implementation is somewhat quirky, registering an instace of this class as a Collector, so the
 * {@link Collector#collect()} method is invoked by the Prometheus registry on-demand. That makes it possible for us
 * to update the Gauges defined and registered in this collector with new values as part of the normal collect()
 * lifecycle.
 *
 * @author eriklupander
 */
@Component
public class HealthMonitor extends Collector {

    private static final long BAD_HEALTHSTATUS_MEASUREMNET = -1L;

    private static final String PREFIX = "health_";
    private static final String NORMAL = "_normal";
    private static final String VALUE = "_value";

    private static final long START_TIME = System.currentTimeMillis();

    private static final Gauge UPTIME = Gauge.build()
            .name(PREFIX + "uptime" + VALUE)
            .help("Current uptime in seconds")
            .register();

    private static final Gauge LOGGED_IN_USERS = Gauge.build()
            .name(PREFIX + "logged_in_users" + VALUE)
            .help("Current number of logged in users")
            .register();

    private static final Gauge DB_ACCESSIBLE = Gauge.build()
            .name(PREFIX + "db_accessible" + NORMAL)
            .help("0 == OK 1 == NOT OK")
            .register();

    private static final Gauge JMS_ACCESSIBLE = Gauge.build()
            .name(PREFIX + "jms_accessible" + NORMAL)
            .help("0 == OK 1 == NOT OK")
            .register();

    private static final Gauge IT_ACCESSIBLE = Gauge.build()
            .name(PREFIX + "intygstjanst_accessible" + NORMAL)
            .help("0 == OK 1 == NOT OK")
            .register();

    private static final Gauge PDL_QUEUE_DEPTH = Gauge.build()
            .name(PREFIX + "pdl_queue_depth" + VALUE)
            .help("Number of waiting pdl log messages")
            .register();

    private static final Gauge PDL_AGGREGATED_QUEUE_DEPTH = Gauge.build()
            .name(PREFIX + "pdl_aggregated_queue_depth" + VALUE)
            .help("Number of waiting aggregated pdl log messages")
            .register();
    private static final long MILLIS_PER_SECOND = 1000L;

    private static final String CURR_TIME_SQL = "SELECT CURRENT_TIME()";

    @Value("${app.name}")
    private String appName;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    @Qualifier("jmsPDLLogTemplate")
    private JmsTemplate jmsPDLLogTemplate;

    @Autowired
    @Qualifier("jmsAggregatedPDLLogTemplate")
    private JmsTemplate jmsAggregatedPDLLogTemplate;

    @Autowired
    private ConnectionFactory connectionFactory;

    @Autowired
    @Qualifier("rediscache")
    private RedisTemplate<Object, Object> redisTemplate;

    @Value("${it.ping.url}")
    private String itMetricsUrl;

    // Runs a lua script to count number of keys matching our session keys.
    private RedisScript<Long> redisScript;

    @PostConstruct
    public void init() {
        redisScript = new DefaultRedisScript<>(
                "return #redis.call('keys','spring:session:" + appName + ":index:*')", Long.class);
        this.register();
    }

    @Override
    public List<MetricFamilySamples> collect() {
        long secondsSinceStart = (System.currentTimeMillis() - START_TIME) / MILLIS_PER_SECOND;
        UPTIME.set(secondsSinceStart);
        LOGGED_IN_USERS.set(countSessions());
        DB_ACCESSIBLE.set(checkTimeFromDb() ? 0 : 1);
        JMS_ACCESSIBLE.set(checkJmsConnection() ? 0 : 1);
        IT_ACCESSIBLE.set(pingIntygstjanst() ? 0 : 1);
        PDL_QUEUE_DEPTH.set(checkQueueDepth(jmsPDLLogTemplate));
        PDL_AGGREGATED_QUEUE_DEPTH.set(checkQueueDepth(jmsAggregatedPDLLogTemplate));

        return Collections.emptyList();
    }

    private int countSessions() {
        Long numberOfUsers = redisTemplate.execute(redisScript, Collections.emptyList());
        return numberOfUsers.intValue();
    }

    private boolean checkJmsConnection() {
        try {
            Connection connection = connectionFactory.createConnection();
            connection.close();
        } catch (JMSException e) {
            return false;
        }
        return true;
    }

    private boolean checkTimeFromDb() {
        Time timestamp;
        try {
            Query query = entityManager.createNativeQuery(CURR_TIME_SQL);
            timestamp = (Time) query.getSingleResult();
        } catch (Exception e) {
            return false;
        }
        return timestamp != null;
    }

    private boolean pingIntygstjanst() {

        return doHttpLookup(itMetricsUrl) == HttpServletResponse.SC_OK;
    }

    private int doHttpLookup(String url) {
        try {
            HttpURLConnection httpConnection = (HttpURLConnection) new URL(url).openConnection();
            int respCode = httpConnection.getResponseCode();
            httpConnection.disconnect();
            return respCode;
        } catch (IOException e) {
            return 0;
        }
    }

    private long checkQueueDepth(JmsTemplate tpl) {
        long queueDepth = 0;

        try {
            return tpl.browse((session, browser) -> {
                Enumeration<?> enumeration = browser.getEnumeration();
                long qd = 0;
                while (enumeration.hasMoreElements()) {
                    enumeration.nextElement();
                    qd++;
                }
                return qd;
            });
        } catch (Exception e) {
            queueDepth = BAD_HEALTHSTATUS_MEASUREMNET;
        }

        return queueDepth;
    }
}
