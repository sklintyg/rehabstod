package se.inera.intyg.rehabstod.web.controller.api.dto;

import java.time.LocalDate;
import lombok.Data;

@Data
public class GetLUCertificatesForCareUnitRequest {

    String unit;
    LocalDate fromDate;
    LocalDate toDate;
}
