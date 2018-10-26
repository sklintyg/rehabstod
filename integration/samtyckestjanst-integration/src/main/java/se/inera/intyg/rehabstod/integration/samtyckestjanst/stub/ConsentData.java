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

import se.riv.informationsecurity.authorization.consent.v2.ActionType;
import se.riv.informationsecurity.authorization.consent.v2.AssertionTypeType;
import se.riv.informationsecurity.authorization.consent.v2.ScopeType;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author Magnus Ekstrand on 2018-10-17.
 */
public class ConsentData {

    private String assertionId;
    private String vgHsaId;
    private String veHsaId;
    private String patientId;
    private String userHsaId;
    private String representedBy;

    private LocalDateTime consentFrom;
    private LocalDateTime consentTo;

    private ActionType registrationAction;

    private final ScopeType  scopeType = ScopeType.NATIONAL_LEVEL;
    private final AssertionTypeType assertionType = AssertionTypeType.CONSENT;

    ConsentData(Builder builder) {
        this.assertionId = builder.assertionId;
        this.vgHsaId = builder.vgHsaId;
        this.veHsaId = builder.veHsaId;
        this.patientId = builder.patientId;
        this.userHsaId = builder.userHsaId;
        this.representedBy = builder.representedBy;
        this.consentFrom = builder.consentFrom;
        this.consentTo = builder.consentTo;
        this.registrationAction = builder.registrationAction;
    }

    public String getAssertionId() {
        return assertionId;
    }

    public String getVgHsaId() {
        return vgHsaId;
    }

    public String getVeHsaId() {
        return veHsaId;
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

    public LocalDateTime getConsentFrom() {
        return consentFrom;
    }

    public LocalDateTime getConsentTo() {
        return consentTo;
    }

    public ActionType getRegistrationAction() {
        return registrationAction;
    }

    public ScopeType getScopeType() {
        return scopeType;
    }

    public AssertionTypeType getAssertionType() {
        return assertionType;
    }

    public void setUserHsaId(String userHsaId) {
        this.userHsaId = userHsaId;
    }

    public void setRepresentedBy(String representedBy) {
        this.representedBy = representedBy;
    }

    public void setConsentFrom(LocalDateTime consentFrom) {
        this.consentFrom = consentFrom;
    }

    public void setConsentTo(LocalDateTime consentTo) {
        this.consentTo = consentTo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConsentData)) return false;
        ConsentData that = (ConsentData) o;
        return Objects.equals(assertionId, that.assertionId) &&
                Objects.equals(vgHsaId, that.vgHsaId) &&
                Objects.equals(veHsaId, that.veHsaId) &&
                Objects.equals(patientId, that.patientId) &&
                Objects.equals(registrationAction, that.registrationAction);
    }

    @Override
    public int hashCode() {

        return Objects.hash(assertionId, vgHsaId, veHsaId, patientId, registrationAction);
    }


    public static final class Builder {

        private String assertionId;
        private String vgHsaId;
        private String veHsaId;
        private String patientId;
        private String userHsaId;
        private String representedBy;
        private LocalDateTime consentFrom;
        private LocalDateTime consentTo;
        private ActionType registrationAction;

        public Builder(String assertionId, String vgHsaId, String veHsaId, String patientId, ActionType registrationAction) {
            this.assertionId = assertionId;
            this.vgHsaId = vgHsaId;
            this.veHsaId = veHsaId;
            this.patientId = patientId;
            this.registrationAction = registrationAction;
        }

        public Builder userHsaId(String userHsaId) {
            this.userHsaId = userHsaId;
            return this;
        }

        public Builder representedBy(String representedBy) {
            this.representedBy = representedBy;
            return this;
        }

        public Builder consentFrom(LocalDateTime consentFrom) {
            this.consentFrom = consentFrom;
            return this;
        }

        public Builder consentTo(LocalDateTime consentTo) {
            this.consentTo = consentTo;
            return this;
        }

        public ConsentData build() {
            return new ConsentData(this);
        }
    }
}
