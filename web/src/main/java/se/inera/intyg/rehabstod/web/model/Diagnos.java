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
package se.inera.intyg.rehabstod.web.model;

/**
 * Created by Magnus Ekstrand on 03/02/16.
 */
public class Diagnos {

    private static final int HASH_SEED = 31;

    private String intygsVarde;
    private String kod;
    private String namn;
    private String beskrivning;
    private String kapitel;

    public Diagnos() {
        // When we try to deserialize a JSON String to Diagnos an Exception
        // “JsonMappingException: No suitable constructor found” will be thrown
        // in absence of a default constructor
    }

    public Diagnos(String intygsVarde, String kod, String namn) {
        this.intygsVarde = intygsVarde;
        this.kod = kod;
        this.namn = namn;
    }


    // getters and setters

    public String getIntygsVarde() {
        return intygsVarde;
    }

    public String getKod() {
        return kod;
    }

    public String getNamn() {
        return namn;
    }

    public String getBeskrivning() {
        return beskrivning;
    }

    public void setBeskrivning(String beskrivning) {
        this.beskrivning = beskrivning;
    }

    public String getKapitel() {
        return kapitel;
    }

    public void setKapitel(String kapitel) {
        this.kapitel = kapitel;
    }


    // api

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
        if (!kod.equals(diagnos.kod)) {
            return false;
        }
        if (!namn.equals(diagnos.namn)) {
            return false;
        }
        if (beskrivning != null ? !beskrivning.equals(diagnos.beskrivning) : diagnos.beskrivning != null) {
            return false;
        }

        return !(kapitel != null ? !kapitel.equals(diagnos.kapitel) : diagnos.kapitel != null);
    }

    @Override
    public int hashCode() {
        int result = intygsVarde.hashCode();
        result = HASH_SEED * result + kod.hashCode();
        result = HASH_SEED * result + namn.hashCode();
        result = HASH_SEED * result + (beskrivning != null ? beskrivning.hashCode() : 0);
        result = HASH_SEED * result + (kapitel != null ? kapitel.hashCode() : 0);
        return result;
    }

}
