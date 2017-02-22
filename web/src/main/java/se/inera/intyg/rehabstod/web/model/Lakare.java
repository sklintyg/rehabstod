/**
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
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

    private String lakareId;
    private String lakareNamn;

    public Lakare() {
        // When we try to deserialize a JSON String to Lakare an Exception
        // “JsonMappingException: No suitable constructor found” will be thrown
        // in absence of a default constructor
    }

    public Lakare(String lakareId, String lakareNamn) {
        this.lakareId = lakareId;
        this.lakareNamn = lakareNamn;
    }


    // - - - getters - - -

    public String getLakareId() {
        return lakareId;
    }

    public String getLakareNamn() {
        return lakareNamn;
    }


    // - - - api - - -

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Lakare lakare = (Lakare) o;

        return lakareId.equals(lakare.lakareId);
    }

    @Override
    public int hashCode() {
        return lakareId.hashCode();
    }

}
