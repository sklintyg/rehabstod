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

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SickLeaveSummary {

    private int total;
    private List<GenderStat> genders;
    private List<DiagnosGruppStat> groups;
    private List<DiagnosGruppStat> maleDiagnosisGroups;
    private List<DiagnosGruppStat> femaleDiagnosisGroups;
    private List<SickLeaveDegreeStat> sickLeaveDegrees;
    private List<SickLeaveDegreeStat> maleSickLeaveDegrees;
    private List<SickLeaveDegreeStat> femaleSickLeaveDegrees;
    private List<SickLeaveDegreeStat> countSickLeaveDegrees;
    private List<SickLeaveDegreeStat> countMaleSickLeaveDegrees;
    private List<SickLeaveDegreeStat> countFemaleSickLeaveDegrees;
    private List<SickLeaveLengthStat> sickLeaveLengths;
    private List<SickLeaveLengthStat> maleSickLeaveLengths;
    private List<SickLeaveLengthStat> femaleSickLeaveLengths;
}
