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
package se.inera.intyg.rehabstod.web.model;

import java.util.Date;
import java.util.List;

/**
 * Created by Magnus Ekstrand on 03/02/16.
 */
public class Sjukfall {

    private static final int HASH_SEED = 31;
    private Patient patient;
    private Diagnos diagnos;

    private Date start;
    private Date slut;

    private int dagar;
    private int intyg;

    private List<Integer> grader;
    private int aktivGrad;

    private String lakare;

    public Sjukfall() {
    }


    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }


    public Diagnos getDiagnos() {
        return diagnos;
    }

    public void setDiagnos(Diagnos diagnos) {
        this.diagnos = diagnos;
    }


    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }


    public Date getSlut() {
        return slut;
    }

    public void setSlut(Date slut) {
        this.slut = slut;
    }


    public int getDagar() {
        return dagar;
    }

    public void setDagar(int dagar) {
        this.dagar = dagar;
    }


    public int getIntyg() {
        return intyg;
    }

    public void setIntyg(int intyg) {
        this.intyg = intyg;
    }


    public List<Integer> getGrader() {
        return grader;
    }

    public void setGrader(List<Integer> grader) {
        this.grader = grader;
    }


    public int getAktivGrad() {
        return aktivGrad;
    }

    public void setAktivGrad(int aktivGrad) {
        this.aktivGrad = aktivGrad;
    }


    public String getLakare() {
        return lakare;
    }

    public void setLakare(String lakare) {
        this.lakare = lakare;
    }


    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Sjukfall sjukfall = (Sjukfall) o;

        if (dagar != sjukfall.dagar) {
            return false;
        }
        if (intyg != sjukfall.intyg) {
            return false;
        }
        if (aktivGrad != sjukfall.aktivGrad) {
            return false;
        }
        if (!patient.equals(sjukfall.patient)) {
            return false;
        }
        if (!diagnos.equals(sjukfall.diagnos)) {
            return false;
        }
        if (!start.equals(sjukfall.start)) {
            return false;
        }
        if (!slut.equals(sjukfall.slut)) {
            return false;
        }
        if (!grader.equals(sjukfall.grader)) {
            return false;
        }
        return lakare.equals(sjukfall.lakare);

    }


    public int hashCode() {
        int result = patient.hashCode();
        result = HASH_SEED * result + diagnos.hashCode();
        result = HASH_SEED * result + start.hashCode();
        result = HASH_SEED * result + slut.hashCode();
        result = HASH_SEED * result + dagar;
        result = HASH_SEED * result + intyg;
        result = HASH_SEED * result + grader.hashCode();
        result = HASH_SEED * result + aktivGrad;
        result = HASH_SEED * result + lakare.hashCode();
        return result;
    }
}
