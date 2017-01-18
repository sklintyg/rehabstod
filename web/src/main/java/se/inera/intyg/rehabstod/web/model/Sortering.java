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
public class Sortering {

    private static final int HASH_SEED = 31;

    private String kolumn;
    private String order;


    // getters and setters

    public String getKolumn() {
        return kolumn;
    }

    public void setKolumn(String kolumn) {
        this.kolumn = kolumn;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
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

        Sortering sortering = (Sortering) o;

        if (!kolumn.equals(sortering.kolumn)) {
            return false;
        }
        return order.equals(sortering.order);

    }

    @Override
    public int hashCode() {
        int result = kolumn.hashCode();
        result = HASH_SEED * result + order.hashCode();
        return result;
    }
}
