/**
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.service.export;

import java.util.List;

/**
 * Created by eriklupander on 2016-02-23.
 */
public class SjukfallFilterState {

    private List<String> selectedDoctors;
    private List<String> selectedDiagnoseGroups;
    private Integer sickLeaveLengthLowerBoundDays;
    private Integer sickLeaveLengthUpperBoundDays;

    private Integer gapDays;

    private String freetext;
    private String sortColumn;
    private String sortOrder;

    public List<String> getSelectedDoctors() {
        return selectedDoctors;
    }

    public void setSelectedDoctors(List<String> selectedDoctors) {
        this.selectedDoctors = selectedDoctors;
    }

    public List<String> getSelectedDiagnoseGroups() {
        return selectedDiagnoseGroups;
    }

    public void setSelectedDiagnoseGroups(List<String> selectedDiagnoseGroups) {
        this.selectedDiagnoseGroups = selectedDiagnoseGroups;
    }

    public Integer getSickLeaveLengthLowerBoundDays() {
        return sickLeaveLengthLowerBoundDays;
    }

    public void setSickLeaveLengthLowerBoundDays(Integer sickLeaveLengthLowerBoundDays) {
        this.sickLeaveLengthLowerBoundDays = sickLeaveLengthLowerBoundDays;
    }

    public Integer getSickLeaveLengthUpperBoundDays() {
        return sickLeaveLengthUpperBoundDays;
    }

    public void setSickLeaveLengthUpperBoundDays(Integer sickLeaveLengthUpperBoundDays) {
        this.sickLeaveLengthUpperBoundDays = sickLeaveLengthUpperBoundDays;
    }

    public Integer getGapDays() {
        return gapDays;
    }

    public void setGapDays(Integer gapDays) {
        this.gapDays = gapDays;
    }

    public String getFreetext() {
        return freetext;
    }

    public void setFreetext(String freetext) {
        this.freetext = freetext;
    }

    public String getSortColumn() {
        return sortColumn;
    }

    public void setSortColumn(String sortColumn) {
        this.sortColumn = sortColumn;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }
}
