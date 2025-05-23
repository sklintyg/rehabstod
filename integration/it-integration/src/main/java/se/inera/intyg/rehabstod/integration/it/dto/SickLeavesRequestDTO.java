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

package se.inera.intyg.rehabstod.integration.it.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import se.inera.intyg.infra.sjukfall.dto.DiagnosKapitel;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SickLeavesRequestDTO {

    private String unitId;
    private String careUnitId;
    private List<String> doctorIds;
    private List<DiagnosKapitel> diagnosisChapters;
    private Integer maxCertificateGap;
    private Integer maxDaysSinceSickLeaveCompleted;
    private List<SickLeaveLengthInterval> sickLeaveLengthIntervals;
    private Integer fromPatientAge;
    private Integer toPatientAge;
    private String protectedPersonFilterId;
    private LocalDate fromSickLeaveEndDate;
    private LocalDate toSickLeaveEndDate;
    private List<String> rekoStatusTypeIds;
    private List<String> occupationTypeIds;
    private String textSearch;
}
