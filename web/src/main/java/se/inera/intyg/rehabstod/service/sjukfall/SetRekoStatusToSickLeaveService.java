package se.inera.intyg.rehabstod.service.sjukfall;

import java.time.LocalDateTime;

public interface SetRekoStatusToSickLeaveService {
    void set(String patientId, String status, LocalDateTime sickLeaveTimestamp);
}
