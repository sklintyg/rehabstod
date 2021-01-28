/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.service.pdl.dto;


/**
 * Immutable representation of a Rehabstöd user for PDL logging purposes.
 *
 * @author mekstrand
 */
public final class LogPatient {

    private final String patientId;
    private final String patientNamn;
    private final String enhetsId;
    private final String enhetsNamn;
    private final String vardgivareId;
    private final String vardgivareNamn;

    private LogPatient(Builder builder) {
        this.patientId = builder.patientId;
        this.patientNamn = builder.patientNamn;
        this.enhetsId = builder.enhetsId;
        this.enhetsNamn = builder.enhetsNamn;
        this.vardgivareId = builder.vardgivareId;
        this.vardgivareNamn = builder.vardgivareNamn;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getPatientNamn() {
        return patientNamn;
    }

    public String getEnhetsId() {
        return enhetsId;
    }

    public String getEnhetsNamn() {
        return enhetsNamn;
    }

    public String getVardgivareId() {
        return vardgivareId;
    }

    public String getVardgivareNamn() {
        return vardgivareNamn;
    }


    public static class Builder {

        private String patientId;
        private String patientNamn;
        private String enhetsId;
        private String enhetsNamn;
        private String vardgivareId;
        private String vardgivareNamn;

        /**
         * Enligt tjänstekontraktsbeskrivningen ska det i anrop till tjänsten "StoreLog"
         * komma information om användaren som är upphov till loggposten. De fält som är
         * obligatoriska är användarens id, vårdenhetens id och vårdgivarens id.
         * <p>
         * Se https://bitbucket.org/rivta-domains/riv.ehr.log/raw/master/docs/TKB_ehr_log.docx
         *
         * @param patientId Id of the patient.
         * @param enhetsId HsaId of the unit owning the information.
         * @param vardgivareId HsaId of the caregiver owning the information.
         */
        public Builder(String patientId, String enhetsId, String vardgivareId) {
            if (patientId == null || enhetsId == null || vardgivareId == null) {
                throw new IllegalArgumentException("LogPatient builder requires all constructor arguments to be non-null");
            }

            this.patientId = patientId;
            this.enhetsId = enhetsId;
            this.vardgivareId = vardgivareId;
        }

        public Builder patientNamn(String patientNamn) {
            this.patientNamn = patientNamn;
            return this;
        }

        public Builder enhetsNamn(String enhetsNamn) {
            this.enhetsNamn = enhetsNamn;
            return this;
        }

        public Builder vardgivareNamn(String vardgivareNamn) {
            this.vardgivareNamn = vardgivareNamn;
            return this;
        }

        public LogPatient build() {
            return new LogPatient(this);
        }
    }
}
