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

public class PopulateFiltersRequestDTO {
    private String unitId;
    private String careUnitId;
    private int maxCertificateGap;
    private int maxDaysSinceSickLeaveCompleted;

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    public String getCareUnitId() {
        return careUnitId;
    }

    public void setCareUnitId(String careUnitId) {
        this.careUnitId = careUnitId;
    }


    public int getMaxCertificateGap() {
        return maxCertificateGap;
    }

    public void setMaxCertificateGap(int maxCertificateGap) {
        this.maxCertificateGap = maxCertificateGap;
    }

    public int getMaxDaysSinceSickLeaveCompleted() {
        return maxDaysSinceSickLeaveCompleted;
    }

    public void setMaxDaysSinceSickLeaveCompleted(int maxDaysSinceSickLeaveCompleted) {
        this.maxDaysSinceSickLeaveCompleted = maxDaysSinceSickLeaveCompleted;
    }
}