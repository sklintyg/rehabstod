package se.inera.intyg.rehabstod.web.controller.api.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import se.inera.intyg.rehabstod.web.model.Lakare;

@Data
@AllArgsConstructor
public class GetDoctorsForUnitResponse {

    List<Lakare> doctors;
}
