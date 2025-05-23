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
package se.inera.intyg.rehabstod.service.monitoring;

import io.prometheus.client.Collector;
import io.prometheus.client.Gauge;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import jakarta.annotation.PostConstruct;
import jakarta.jms.Connection;
import jakarta.jms.ConnectionFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

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

    private static final long MILLIS_PER_SECOND = 1000L;

    private static final String PING_SQL = "SELECT 1";

    @Value("${app.name}")
    private String appName;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    @Qualifier("jmsPDLLogTemplate")
    private JmsTemplate jmsPDLLogTemplate;

    @Autowired
    private ConnectionFactory connectionFactory;

    @Value("${it.ping.url}")
    private String itMetricsUrl;

    @FunctionalInterface
    interface Tester {

        void run() throws Exception;
    }

    /**
     * Registers this class as a prometheus collector.
     */
    @PostConstruct
    public void init() {
        this.register();
    }

    @Override
    public List<MetricFamilySamples> collect() {
        long secondsSinceStart = (System.currentTimeMillis() - START_TIME) / MILLIS_PER_SECOND;
        UPTIME.set(secondsSinceStart);
        DB_ACCESSIBLE.set(checkDbConnection() ? 0 : 1);
        JMS_ACCESSIBLE.set(checkJmsConnection() ? 0 : 1);
        IT_ACCESSIBLE.set(pingIntygstjanst() ? 0 : 1);
        PDL_QUEUE_DEPTH.set(checkQueueDepth(jmsPDLLogTemplate));

        return Collections.emptyList();
    }

    private boolean checkJmsConnection() {
        return invoke(() -> {
            Connection connection = connectionFactory.createConnection();
            connection.close();
        });
    }

    private boolean checkDbConnection() {
        return invoke(() -> entityManager.createNativeQuery(PING_SQL).getSingleResult());
    }

    private boolean pingIntygstjanst() {
        return invoke(() -> {
            HttpURLConnection httpConnection = (HttpURLConnection) new URL(itMetricsUrl).openConnection();
            int respCode = httpConnection.getResponseCode();
            httpConnection.disconnect();
            if (respCode != HttpServletResponse.SC_OK) {
                throw new RuntimeException();
            }
        });
    }

    private boolean invoke(Tester tester) {
        try {
            tester.run();
        } catch (Exception e) {
            return false;
        }
        return true;
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
