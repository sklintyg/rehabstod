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
package se.inera.intyg.rehabstod.web.controller.api.dto;

public class GetUnitCertificateSummaryResponse {
    private int total;
    private int men;
    private int women;


    public GetUnitCertificateSummaryResponse(int total, int men, int women) {
        this.total = total;
        this.men = men;
        this.women = women;
    }

    public int getTotal() {
        return total;
    }

    public int getMen() {
        return men;
    }

    public int getWomen() {
        return women;
    }
}
