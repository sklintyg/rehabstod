package se.inera.intyg.rehabstod.web.controller.api.dto;

import java.time.LocalDate;
import lombok.Data;

@Data
public class GetLUCertificatesForPersonRequest {

    String personId;
    String unit;
    LocalDate fromDate;
    LocalDate toDate;
}
