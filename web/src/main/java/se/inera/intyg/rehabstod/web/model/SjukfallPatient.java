/*
 * Copyright (C) 2025 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.web.model;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by eriklupander on 2016-02-19.
 */
public class SjukfallPatient {

    private Diagnos diagnos;

    private LocalDate start;
    private LocalDate slut;

    private Integer dagar;

    private List<PatientData> intyg;

    // - - - getters and setters

    public Diagnos getDiagnos() {
        return diagnos;
    }

    public void setDiagnos(Diagnos diagnos) {
        this.diagnos = diagnos;
    }

    public LocalDate getStart() {
        return start;
    }

    public void setStart(LocalDate start) {
        this.start = start;
    }

    public LocalDate getSlut() {
        return slut;
    }

    public void setSlut(LocalDate slut) {
        this.slut = slut;
    }

    public Integer getDagar() {
        return dagar;
    }

    public void setDagar(Integer dagar) {
        this.dagar = dagar;
    }

    public List<PatientData> getIntyg() {
        return intyg;
    }

    public void setIntyg(List<PatientData> intyg) {
        this.intyg = intyg;
    }
}
