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
package se.inera.intyg.rehabstod.integration.srs.model;

/**
 * Created by eriklupander on 2017-11-01.
 */
public class RiskSignal {

    private String intygsId;
    private int riskKategori;
    private String riskDescription;

    private RiskSignal() {
    }

    public RiskSignal(String intygsId, int riskKategori, String riskDescription) {
        this.intygsId = intygsId;
        this.riskKategori = riskKategori;
        this.riskDescription = riskDescription;
    }

    public String getIntygsId() {
        return intygsId;
    }

    public int getRiskKategori() {
        return riskKategori;
    }

    public String getRiskDescription() {
        return riskDescription;
    }
}
