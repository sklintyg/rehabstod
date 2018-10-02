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
import java.util.List;

/**
 * Created by marced on 2018-10-02.
 */
public class SjfMetaData {

    private List<String> vardenheterInomVGMedSparr = new ArrayList<>();
    private List<String> andraVardgivareMedSparr = new ArrayList<>();

    public SjfMetaData() {

    }

    public SjfMetaData(List<String> vardenheterInomVGMedSparr, List<String> andraVardgivareMedSparr) {
        this.vardenheterInomVGMedSparr = vardenheterInomVGMedSparr;
        this.andraVardgivareMedSparr = andraVardgivareMedSparr;
    }

    public List<String> getVardenheterInomVGMedSparr() {
        return vardenheterInomVGMedSparr;
    }

    public void setVardenheterInomVGMedSparr(List<String> vardenheterInomVGMedSparr) {
        this.vardenheterInomVGMedSparr = vardenheterInomVGMedSparr;
    }

    public List<String> getAndraVardgivareMedSparr() {
        return andraVardgivareMedSparr;
    }

    public void setAndraVardgivareMedSparr(List<String> andraVardgivareMedSparr) {
        this.andraVardgivareMedSparr = andraVardgivareMedSparr;
    }

}
