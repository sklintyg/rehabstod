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

package se.inera.intyg.rehabstod.web.controller.api.dto;

import java.util.ArrayList;
import java.util.List;
import se.inera.intyg.rehabstod.service.diagnos.dto.DiagnosKapitel;

public class SickLeavesFilterRequestDTO {
    private List<String> doctorIds;
    private Integer toSickLeaveLength;
    private Integer fromSickLeaveLength;
    private List<DiagnosKapitel> diagnosisChapters;

    public SickLeavesFilterRequestDTO() { }

    public SickLeavesFilterRequestDTO(
        List<String> doctorIds, int toSickLeaveLength, int fromSickLeaveLength, List<DiagnosKapitel> diagnosisChapters
    ) {
        this.doctorIds = doctorIds;
        this.toSickLeaveLength = toSickLeaveLength;
        this.fromSickLeaveLength = fromSickLeaveLength;
        this.diagnosisChapters = diagnosisChapters;
    }

    public List<String> getDoctorIds() {
        return doctorIds;
    }

    public void addDoctorId(String id) {
        if (doctorIds == null) {
            doctorIds = new ArrayList<>();
        }
        doctorIds.add(id);
    }

    public void addDiagnosisChapter(DiagnosKapitel chapter) {
        if (diagnosisChapters == null) {
            diagnosisChapters = new ArrayList<>();
        }
        diagnosisChapters.add(chapter);
    }

    public void setDoctorIds(List<String> doctorIds) {
        this.doctorIds = doctorIds;
    }

    public Integer getToSickLeaveLength() {
        return toSickLeaveLength;
    }

    public void setToSickLeaveLength(Integer toSickLeaveLength) {
        this.toSickLeaveLength = toSickLeaveLength;
    }

    public Integer getFromSickLeaveLength() {
        return fromSickLeaveLength;
    }

    public void setFromSickLeaveLength(Integer fromSickLeaveLength) {
        this.fromSickLeaveLength = fromSickLeaveLength;
    }

    public List<DiagnosKapitel> getDiagnosisChapters() {
        return diagnosisChapters;
    }

    public void setDiagnoses(List<DiagnosKapitel> diagnosisChapters) {
        this.diagnosisChapters = diagnosisChapters;
    }
}