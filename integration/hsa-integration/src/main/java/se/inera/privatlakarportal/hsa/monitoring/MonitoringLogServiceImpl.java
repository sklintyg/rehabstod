package se.inera.privatlakarportal.hsa.monitoring;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import se.inera.privatlakarportal.common.monitoring.util.HashUtility;
import se.inera.privatlakarportal.common.monitoring.util.LogMarkers;


@Service("hsaMonitoringLogService")
public class MonitoringLogServiceImpl implements MonitoringLogService {

    private static final Object SPACE = " ";
    private static final Logger LOG = LoggerFactory.getLogger(MonitoringLogService.class);

    @Override
    public void logHospWaiting(String id) {
        logEvent(MonitoringEvent.HOSP_WAITING, HashUtility.hash(id));
    }

    @Override
    public void logUserAuthorizedInHosp(String id) {
        logEvent(MonitoringEvent.HOSP_AUTHORIZED, HashUtility.hash(id));
    }

    @Override
    public void logUserNotAuthorizedInHosp(String id) {
        logEvent(MonitoringEvent.HOSP_NOT_AUTHORIZED, HashUtility.hash(id));
    }

    private void logEvent(MonitoringEvent logEvent, Object... logMsgArgs) {
        LOG.info(LogMarkers.MONITORING, buildMessage(logEvent), logMsgArgs);
    }

    private String buildMessage(MonitoringEvent logEvent) {
        StringBuilder logMsg = new StringBuilder();
        logMsg.append(logEvent.name()).append(SPACE).append(logEvent.getMessage());
        return logMsg.toString();
    }

    private enum MonitoringEvent {
        HOSP_WAITING("User '{}' is waiting for HOSP"),
        HOSP_AUTHORIZED("User '{}' is authorized doctor in HOSP"),
        HOSP_NOT_AUTHORIZED("User '{}' is not authorized doctor in HOSP");

        private String message;

        private MonitoringEvent(String msg) {
            this.message = msg;
        }

        public String getMessage() {
            return message;
        }
    }
}
