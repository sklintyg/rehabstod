package se.inera.intyg.rehabstod.web.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AGCertificate {

    private String certificateId;

    private Lakare doctor;

    private Patient patient;

    private Diagnos diagnose;
    private List<Diagnos> biDiagnosis;

    private LocalDateTime signingTimeStamp;

    private int notifications;

    private LocalDate start;
    private LocalDate end;
    private int days;

    private List<Integer> degree;

    private List<String> occupation;

}
