package se.inera.intyg.rehabstod.integration.it.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SetRekoStatusToSickLeaveRequestDTO {
    String patientId;
    String status;
    String careProviderId;
    String careUnitId;
    String unitId;
    String staffId;
    String staffName;
    int maxCertificateGap;
    int maxDaysSinceSickLeaveCompleted;
}
