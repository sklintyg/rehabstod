package se.inera.intyg.rehabstod.service.monitoring;

import se.inera.intyg.rehabstod.service.monitoring.dto.HealthStatus;

/**
 * Service for checking the health of the application.
 *
 * @author Erik
 *
 */
public interface HealthCheckService {




    /**
     * Returns the applications uptime.
     *
     * @return
     */
    HealthStatus checkUptime();

    /**
     * Returns the applications uptime in human readable format.
     *
     * @return
     */
    String checkUptimeAsString();

    /**
     * Checks the number of logged in users.
     *
     * @return
     */
    HealthStatus checkNbrOfUsers();

}
