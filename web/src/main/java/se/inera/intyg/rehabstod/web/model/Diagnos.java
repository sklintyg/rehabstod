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

/**
 * Created by mango on 03/02/16.
 */
public class Diagnos {

    private static final int HASH_SEED = 31;
    private String original;
    private String grupp;
    private String kod;

    public Diagnos() {
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    public String getGrupp() {
        return grupp;
    }

    public void setGrupp(String grupp) {
        this.grupp = grupp;
    }

    public String getKod() {
        return kod;
    }

    public void setKod(String kod) {
        this.kod = kod;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Diagnos diagnos = (Diagnos) o;

        if (!original.equals(diagnos.original)) {
            return false;
        }
        if (!grupp.equals(diagnos.grupp)) {
            return false;
        }
        return kod.equals(diagnos.kod);

    }

    @Override
    public int hashCode() {
        int result = original.hashCode();
        result = HASH_SEED * result + grupp.hashCode();
        result = HASH_SEED * result + kod.hashCode();
        return result;
    }
}
