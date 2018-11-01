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
package se.inera.intyg.rehabstod.service.sjukfall.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * Created by marced on 2018-10-02.
 */
public class SjfMetaData {

    private Collection<String> vardenheterInomVGMedSparr = new HashSet<>();
    private Collection<String> andraVardgivareMedSparr = new HashSet<>();
    private Collection<SjfSamtyckeFinnsMetaData> samtyckeFinns = new ArrayList<>();
    private Collection<String> samtyckeSaknas = new ArrayList<>();

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

    public Collection<SjfSamtyckeFinnsMetaData> getSamtyckeFinns() {
        return samtyckeFinns;
    }

    public void setSamtyckeFinns(Collection<SjfSamtyckeFinnsMetaData> samtyckeFinns) {
        this.samtyckeFinns = samtyckeFinns;
    }

    public Collection<String> getSamtyckeSaknas() {
        return samtyckeSaknas;
    }

    public void setSamtyckeSaknas(Collection<String> samtyckeSaknas) {
        this.samtyckeSaknas = samtyckeSaknas;
    }
}
