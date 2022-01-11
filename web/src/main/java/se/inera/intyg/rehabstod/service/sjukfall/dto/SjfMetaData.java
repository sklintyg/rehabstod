/*
 * Copyright (C) 2022 Inera AB (http://www.inera.se)
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

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by marced on 2018-10-02.
 */
public class SjfMetaData {

    private Collection<String> vardenheterInomVGMedSparr = new HashSet<>();
    private Collection<String> andraVardgivareMedSparr = new HashSet<>();
    private Collection<SjfMetaDataItem> kraverSamtycke = new HashSet<>();
    private Collection<SjfMetaDataItem> kraverInteSamtycke = new HashSet<>();

    private boolean samtyckeFinns;
    private boolean blockingServiceError;
    private boolean consentServiceError;
    private boolean haveSekretess;

    public SjfMetaData() {

    }

    public SjfMetaData(Collection<String> vardenheterInomVGMedSparr, Collection<String> andraVardgivareMedSparr) {
        this.vardenheterInomVGMedSparr = vardenheterInomVGMedSparr;
        this.andraVardgivareMedSparr = andraVardgivareMedSparr;
    }

    public Collection<String> getVardenheterInomVGMedSparr() {
        return vardenheterInomVGMedSparr;
    }

    public void setVardenheterInomVGMedSparr(Collection<String> vardenheterInomVGMedSparr) {
        this.vardenheterInomVGMedSparr = vardenheterInomVGMedSparr;
    }

    public Collection<String> getAndraVardgivareMedSparr() {
        return andraVardgivareMedSparr;
    }

    public void setAndraVardgivareMedSparr(Collection<String> andraVardgivareMedSparr) {
        this.andraVardgivareMedSparr = andraVardgivareMedSparr;
    }

    public Collection<SjfMetaDataItem> getKraverSamtycke() {
        return kraverSamtycke;
    }

    public void setKraverSamtycke(Collection<SjfMetaDataItem> kraverSamtycke) {
        this.kraverSamtycke = kraverSamtycke;
    }

    public Collection<SjfMetaDataItem> getKraverInteSamtycke() {
        return kraverInteSamtycke;
    }

    public void setKraverInteSamtycke(Collection<SjfMetaDataItem> kraverInteSamtycke) {
        this.kraverInteSamtycke = kraverInteSamtycke;
    }

    public boolean isSamtyckeFinns() {
        return samtyckeFinns;
    }

    public void setSamtyckeFinns(boolean samtyckeFinns) {
        this.samtyckeFinns = samtyckeFinns;
    }

    public boolean isBlockingServiceError() {
        return blockingServiceError;
    }

    public void setBlockingServiceError(boolean blockingServiceError) {
        this.blockingServiceError = blockingServiceError;
    }

    public boolean isConsentServiceError() {
        return consentServiceError;
    }

    public void setConsentServiceError(boolean consentServiceError) {
        this.consentServiceError = consentServiceError;
    }

    public boolean isHaveSekretess() {
        return haveSekretess;
    }

    public void setHaveSekretess(boolean haveSekretess) {
        this.haveSekretess = haveSekretess;
    }
}
