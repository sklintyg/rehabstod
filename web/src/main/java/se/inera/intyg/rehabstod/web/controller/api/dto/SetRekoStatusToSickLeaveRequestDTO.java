package se.inera.intyg.rehabstod.web.controller.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SetRekoStatusToSickLeaveRequestDTO {
    private String patientId;
    private String status;
    private String sickLeaveTimestamp;
}
