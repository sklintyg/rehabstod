package se.inera.privatlakarportal.service.monitoring;

import se.inera.privatlakarportal.service.monitoring.dto.HealthStatus;

/**
 * Service for checking the health of the application.
 *
 * @author Erik
 *
 */
public interface HealthCheckService {

    /**
     * Check if the database responds.
     *
     * @return
     */
    HealthStatus checkDB();

    /**
     * Check if the connection to HSA is up.
     *
     * @return
     */
    HealthStatus checkHSA();

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
    /**
     * Checks the number of used HSA id.
     *
     * @return
     */
    HealthStatus checkNbrOfUsedHsaId();
}
