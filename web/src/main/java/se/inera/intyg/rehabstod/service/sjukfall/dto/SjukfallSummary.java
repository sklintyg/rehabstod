/**
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
 *
 * This file is part of rehabstod (https://github.com/sklintyg/rehabstod).
 *
 * rehabstod is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * rehabstod is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
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

    public SjukfallSummary(int total, List<GenderStat> genders, List<DiagnosGruppStat> groups) {
        this.total = total;
        this.genders = genders;
        this.groups = groups;
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
}
