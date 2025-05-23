/*
 * Copyright (C) 2025 Inera AB (http://www.inera.se)
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

package se.inera.intyg.rehabstod.service.sjukfall.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import se.inera.intyg.rehabstod.service.diagnos.dto.DiagnosKapitel;
import se.inera.intyg.rehabstod.web.model.Lakare;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PopulateSickLeaveFilterResponseDTO {

    private List<Lakare> activeDoctors;
    private List<DiagnosKapitel> allDiagnosisChapters;
    private List<DiagnosKapitel> enabledDiagnosisChapters;
    private int nbrOfSickLeaves;
    private boolean hasOngoingSickLeaves;
    private List<RekoStatusTypeDTO> rekoStatusTypes;
    private List<OccupationTypeDTO> occupationTypes;
    private List<UnansweredCommunicationFilterTypeDTO> unansweredCommunicationFilterTypes;
    private boolean srsActivated;
}
