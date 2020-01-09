/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
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

import java.io.Serializable;
import java.time.LocalDateTime;
import se.riv.informationsecurity.authorization.consent.v2.ActionType;
import se.riv.informationsecurity.authorization.consent.v2.AssertionTypeType;
import se.riv.informationsecurity.authorization.consent.v2.ScopeType;

/**
 * @author Magnus Ekstrand on 2018-10-17.
 */
public class ConsentData implements Serializable {

    private static final long serialVersionUID = 1L;

    private String assertionId;
    private String vardgivareId;
    private String vardenhetId;
    private String patientId;
    private String employeeId;
    private String representedBy;

    private LocalDateTime consentFrom;
    private LocalDateTime consentTo;

    private ActionType registrationAction;

    private final ScopeType scopeType = ScopeType.NATIONAL_LEVEL;
    private final AssertionTypeType assertionType = AssertionTypeType.CONSENT;

    ConsentData(Builder builder) {
        this.assertionId = builder.assertionId;
        this.vardgivareId = builder.vardgivareId;
        this.vardenhetId = builder.vardenhetId;
        this.patientId = builder.patientId;
        this.employeeId = builder.employeeId;
        this.representedBy = builder.representedBy;
        this.consentFrom = builder.consentFrom;
        this.consentTo = builder.consentTo;
        this.registrationAction = builder.registrationAction;
    }

    public String getAssertionId() {
        return assertionId;
    }

    public String getVardgivareId() {
        return vardgivareId;
    }

    public String getVardenhetId() {
        return vardenhetId;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getEmployeeId() {
        return employeeId;
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

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
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

    public static final class Builder {

        private String assertionId;
        private String vardgivareId;
        private String vardenhetId;
        private String patientId;
        private String employeeId;
        private String representedBy;
        private LocalDateTime consentFrom;
        private LocalDateTime consentTo;
        private ActionType registrationAction;

        public Builder(String assertionId, String vgHsaId, String veHsaId, String patientId, ActionType registrationAction) {
            this.assertionId = assertionId;
            this.vardgivareId = vgHsaId;
            this.vardenhetId = veHsaId;
            this.patientId = patientId;
            this.registrationAction = registrationAction;
        }

        public Builder employeeId(String userHsaId) {
            this.employeeId = userHsaId;
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
