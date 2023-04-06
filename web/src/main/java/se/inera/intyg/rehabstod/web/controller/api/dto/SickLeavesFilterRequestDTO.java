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

public class SickLeavesFilterRequestDTO {
    private List<String> doctorIds;
    private int toSickLeaveLength;
    private int fromSickLeaveLength;

    public SickLeavesFilterRequestDTO() { }

    public SickLeavesFilterRequestDTO(List<String> doctorIds, int toSickLeaveLength, int fromSickLeaveLength) {
        this.doctorIds = doctorIds;
        this.toSickLeaveLength = toSickLeaveLength;
        this.fromSickLeaveLength = fromSickLeaveLength;
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

    public void setDoctorIds(List<String> doctorIds) {
        this.doctorIds = doctorIds;
    }

    public int getToSickLeaveLength() {
        return toSickLeaveLength;
    }

    public void setToSickLeaveLength(int toSickLeaveLength) {
        this.toSickLeaveLength = toSickLeaveLength;
    }

    public int getFromSickLeaveLength() {
        return fromSickLeaveLength;
    }

    public void setFromSickLeaveLength(int fromSickLeaveLength) {
        this.fromSickLeaveLength = fromSickLeaveLength;
    }
}
