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
 * @author Magnus Ekstrand on 2017-02-17.
 */
public class Lakare {

    private String hsaId;
    private String namn;

    public Lakare() {
        // When we try to deserialize a JSON String to Lakare an Exception
        // “JsonMappingException: No suitable constructor found” will be thrown
        // in absence of a default constructor
    }

    public Lakare(String hsaId, String namn) {
        this.hsaId = hsaId;
        this.namn = namn;
    }

    // - - - getters - - -

    public String getHsaId() {
        return hsaId;
    }

    public String getNamn() {
        return namn;
    }

    // name is mutable since we sometimes need to post-process the doctor name
    public void setNamn(String namn) {
        this.namn = namn;
    }

    // - - - api - - -

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Lakare)) {
            return false;
        }

        Lakare lakare = (Lakare) o;

        return hsaId.equals(lakare.hsaId);
    }

    @Override
    public int hashCode() {
        return hsaId.hashCode();
    }

}
