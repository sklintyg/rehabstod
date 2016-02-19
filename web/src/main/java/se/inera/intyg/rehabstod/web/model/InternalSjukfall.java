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
 * Created by eriklupander on 2016-02-19.
 */
public class InternalSjukfall {

    private Sjukfall sjukfall;

    private String vardEnhetId;
    private String vardEnhetNamn;
    private String vardGivareId;
    private String vardGivareNamn;

    public InternalSjukfall() {}

    public String getVardEnhetId() {
        return vardEnhetId;
    }

    public void setVardEnhetId(String vardEnhetId) {
        this.vardEnhetId = vardEnhetId;
    }

    public String getVardEnhetNamn() {
        return vardEnhetNamn;
    }

    public void setVardEnhetNamn(String vardEnhetNamn) {
        this.vardEnhetNamn = vardEnhetNamn;
    }

    public String getVardGivareId() {
        return vardGivareId;
    }

    public void setVardGivareId(String vardGivareId) {
        this.vardGivareId = vardGivareId;
    }

    public String getVardGivareNamn() {
        return vardGivareNamn;
    }

    public void setVardGivareNamn(String vardGivareNamn) {
        this.vardGivareNamn = vardGivareNamn;
    }

    public Sjukfall getSjukfall() {
        return sjukfall;
    }

    public void setSjukfall(Sjukfall sjukfall) {
        this.sjukfall = sjukfall;
    }
}
