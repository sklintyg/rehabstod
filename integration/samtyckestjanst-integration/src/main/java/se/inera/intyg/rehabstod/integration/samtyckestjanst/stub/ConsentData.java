/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.integration.samtyckestjanst.stub;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Created by Magnus Ekstrand on 2018-10-10.
 */
public class ConsentData {

    String personId;
    LocalDate consentFrom;
    LocalDate consentTo;

    public ConsentData(String personId, LocalDate consentFrom, LocalDate consentTo) {
        this.personId = personId;
        this.consentFrom = consentFrom;
        this.consentTo = consentTo;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public LocalDate getConsentFrom() {
        return consentFrom;
    }

    public void setConsentFrom(LocalDate consentFrom) {
        this.consentFrom = consentFrom;
    }

    public LocalDate getConsentTo() {
        return consentTo;
    }

    public void setConsentTo(LocalDate consentTo) {
        this.consentTo = consentTo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ConsentData consentData = (ConsentData) o;
        return Objects.equals(personId, consentData.personId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(personId);
    }
}
