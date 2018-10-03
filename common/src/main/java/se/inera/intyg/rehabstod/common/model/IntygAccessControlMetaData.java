/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.common.model;

import se.inera.intyg.infra.sjukfall.dto.IntygData;

/**
 * Created by marced on 2018-09-27.
 */
public class IntygAccessControlMetaData {

    private IntygData intygData;
    private boolean inomVardgivare;
    private boolean sparr;
    private boolean harSamtycke;
    private boolean kraverSamtycke;
    private boolean bidrarTillAktivtSjukfall;

    public IntygAccessControlMetaData() {
    }

    public IntygAccessControlMetaData(IntygData intygData, boolean inomVardgivare) {
        this.intygData = intygData;
        this.inomVardgivare = inomVardgivare;
    }
    public IntygData getIntygData() {
        return intygData;
    }

    public void setIntygData(IntygData intygData) {
        this.intygData = intygData;
    }

    public boolean isInomVardgivare() {
        return inomVardgivare;
    }

    public void setInomVardgivare(boolean inomVardgivare) {
        this.inomVardgivare = inomVardgivare;
    }

    public boolean isSparr() {
        return sparr;
    }

    public void setSparr(boolean sparr) {
        this.sparr = sparr;
    }

    public boolean isHarSamtycke() {
        return harSamtycke;
    }

    public void setHarSamtycke(boolean harSamtycke) {
        this.harSamtycke = harSamtycke;
    }

    public boolean isKraverSamtycke() {
        return kraverSamtycke;
    }

    public void setKraverSamtycke(boolean kraverSamtycke) {
        this.kraverSamtycke = kraverSamtycke;
    }

    public boolean isBidrarTillAktivtSjukfall() {
        return bidrarTillAktivtSjukfall;
    }

    public void setBidrarTillAktivtSjukfall(boolean bidrarTillAktivtSjukfall) {
        this.bidrarTillAktivtSjukfall = bidrarTillAktivtSjukfall;
    }

    // Convenience methods
    public boolean inreSparr() {
        return this.sparr && this.inomVardgivare;
    }

    public boolean yttreSparr() {
        return this.sparr && !this.inomVardgivare;
    }

}