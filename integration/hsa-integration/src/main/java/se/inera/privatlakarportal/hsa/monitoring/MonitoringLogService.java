package se.inera.privatlakarportal.hsa.monitoring;


/**
 * Interface used when logging to monitoring file. Used to ensure that the log entries are uniform and easy to parse.
 */
public interface MonitoringLogService {

    void logHospWaiting(String id);

    void logUserAuthorizedInHosp(String id);

    void logUserNotAuthorizedInHosp(String id);
}
