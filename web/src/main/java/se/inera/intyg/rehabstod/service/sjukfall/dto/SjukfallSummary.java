/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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

public class SjukfallSummary {

    private int total;
    private List<GenderStat> genders;
    private List<DiagnosGruppStat> groups;
    private List<SickLeaveDegreeStat> sickLeaveDegrees;

    public SjukfallSummary(int total, List<GenderStat> genders, List<DiagnosGruppStat> groups, List<SickLeaveDegreeStat> sickLeaveDegrees) {
        this.total = total;
        this.genders = genders;
        this.groups = groups;
        this.sickLeaveDegrees = sickLeaveDegrees;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<GenderStat> getGenders() {
        return genders;
    }

    public void setGenders(List<GenderStat> genders) {
        this.genders = genders;
    }

    public List<DiagnosGruppStat> getGroups() {
        return groups;
    }

    public void setGroups(List<DiagnosGruppStat> groups) {
        this.groups = groups;
    }

    public List<SickLeaveDegreeStat> getSickLeaveDegrees() {
        return sickLeaveDegrees;
    }

    public void setSickLeaveDegrees(List<SickLeaveDegreeStat> sickLeaveDegrees) {
        this.sickLeaveDegrees = sickLeaveDegrees;
    }
}
