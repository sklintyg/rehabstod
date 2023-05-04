package se.inera.intyg.rehabstod.service.sjukfall.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SickLeaveLengthInterval {
    Integer from;
    Integer to;
}
