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
 * Created by Magnus Ekstrand on 03/02/16.
 */
public class Patient {

    private static final int HASH_SEED = 31;
    private String id;
    private String namn;
    private Gender kon;

    private int alder;


    // getters and setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNamn() {
        return namn;
    }

    public void setNamn(String namn) {
        this.namn = namn;
    }

    public Gender getKon() {
        return kon;
    }

    public void setKon(Gender kon) {
        this.kon = kon;
    }

    public int getAlder() {
        return alder;
    }

    public void setAlder(int alder) {
        this.alder = alder;
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

        Patient patient = (Patient) o;

        if (alder != patient.alder) {
            return false;
        }
        if (!id.equals(patient.id)) {
            return false;
        }
        return namn.equals(patient.namn);

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = HASH_SEED * result + namn.hashCode();
        result = HASH_SEED * result + alder;
        return result;
    }
}
