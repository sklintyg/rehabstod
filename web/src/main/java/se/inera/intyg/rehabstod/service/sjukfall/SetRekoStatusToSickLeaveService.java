package se.inera.intyg.rehabstod.service.sjukfall;

public interface SetRekoStatusToSickLeaveService {
    void set(String patientId, String status);
}
