package se.inera.intyg.rehabstod.integration.it.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
    LocalDateTime sickLeaveTimestamp;
}
