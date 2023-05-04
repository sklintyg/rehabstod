package se.inera.intyg.rehabstod.integration.it.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SickLeaveLengthInterval {
    Integer from;
    Integer to;
}
