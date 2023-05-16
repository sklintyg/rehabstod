/*
 * Copyright (C) 2023 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.inera.intyg.rehabstod.integration.it.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import se.inera.intyg.rehabstod.integration.it.testability.model.CareProvider;
import se.inera.intyg.rehabstod.integration.it.testability.model.CareUnit;
import se.inera.intyg.rehabstod.integration.it.testability.model.Doctor;
import se.inera.intyg.rehabstod.integration.it.testability.model.Occupation;
import se.inera.intyg.rehabstod.integration.it.testability.model.Patient;
import se.inera.intyg.rehabstod.integration.it.testability.model.Relation;
import se.inera.intyg.rehabstod.integration.it.testability.model.WorkCapacity;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestDataOptionsDTO {

    private List<CareProvider> careProviderIds;
    private List<CareUnit> careUnitIds;
    private List<Doctor> doctorIds;
    private List<Patient> patientIds;
    private List<Relation> relationCodes;
    private List<String> diagnosisCodes;
    private List<Occupation> occupations;
    private List<WorkCapacity> workCapacity;

}
