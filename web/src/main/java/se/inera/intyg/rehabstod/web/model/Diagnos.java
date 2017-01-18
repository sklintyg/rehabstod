/**
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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

/**
 * Created by mango on 03/02/16.
 */
public class Diagnos {

    private static final int HASH_SEED = 31;

    private String intygsVarde;
    private String kapitel;
    private String kod;
    private String beskrivning;


    // - - - Getters and setters - - -

    public String getIntygsVarde() {
        return intygsVarde;
    }

    public void setIntygsVarde(String intygsVarde) {
        this.intygsVarde = intygsVarde;
    }

    public String getKapitel() {
        return kapitel;
    }

    public void setKapitel(String kapitel) {
        this.kapitel = kapitel;
    }

    public String getKod() {
        return kod;
    }

    public void setKod(String kod) {
        this.kod = kod;
    }

    public String getBeskrivning() {
        return beskrivning;
    }

    public void setBeskrivning(String beskrivning) {
        this.beskrivning = beskrivning;
    }


    // - - - API - - -

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Diagnos diagnos = (Diagnos) o;

        if (!intygsVarde.equals(diagnos.intygsVarde)) {
            return false;
        }
        if (!kapitel.equals(diagnos.kapitel)) {
            return false;
        }
        return kod.equals(diagnos.kod);

    }

    @Override
    public int hashCode() {
        int result = intygsVarde.hashCode();
        result = HASH_SEED * result + kapitel.hashCode();
        result = HASH_SEED * result + kod.hashCode();
        return result;
    }
}
