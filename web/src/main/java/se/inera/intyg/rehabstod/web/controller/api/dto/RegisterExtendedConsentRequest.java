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
package se.inera.intyg.rehabstod.web.controller.api.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * @author Magnus Ekstrand on 2018-10-19.
 */
public class RegisterExtendedConsentRequest implements Serializable {

    private static final long serialVersionUID = 6854285992002662050L;

    private String patientId;
    private String userHsaId;
    private String representedBy;

    private LocalDate consentFrom;
    private LocalDate consentTo;

    public RegisterExtendedConsentRequest(String patientId) {
        this.patientId = patientId;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getUserHsaId() {
        return userHsaId;
    }

    public String getRepresentedBy() {
        return representedBy;
    }

    public LocalDate getConsentFrom() {
        return consentFrom;
    }

    public LocalDate getConsentTo() {
        return consentTo;
    }

    public void setUserHsaId(String userHsaId) {
        this.userHsaId = userHsaId;
    }

    public void setRepresentedBy(String representedBy) {
        this.representedBy = representedBy;
    }

    public void setConsentFrom(LocalDate consentFrom) {
        this.consentFrom = consentFrom;
    }

    public void setConsentTo(LocalDate consentTo) {
        this.consentTo = consentTo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RegisterExtendedConsentRequest)) return false;
        RegisterExtendedConsentRequest that = (RegisterExtendedConsentRequest) o;
        return Objects.equals(patientId, that.patientId) &&
                Objects.equals(userHsaId, that.userHsaId) &&
                Objects.equals(representedBy, that.representedBy) &&
                Objects.equals(consentFrom, that.consentFrom) &&
                Objects.equals(consentTo, that.consentTo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(patientId, userHsaId, representedBy, consentFrom, consentTo);
    }

}
