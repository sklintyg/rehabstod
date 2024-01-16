/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
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

import java.time.LocalDate;

/**
 * Created by Magnus Ekstrand on 03/02/16.
 */
public class GetSjukfallForPatientRequest {

    private LocalDate aktivtDatum;
    private String patientId;
    private String encryptedPatientId;

    /**
     * The sole constructor.
     **/
    public GetSjukfallForPatientRequest() {
        aktivtDatum = LocalDate.now();
    }


    public LocalDate getAktivtDatum() {
        return aktivtDatum;
    }

    public void setAktivtDatum(LocalDate aktivtDatum) {
        this.aktivtDatum = aktivtDatum;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getEncryptedPatientId() {
        return encryptedPatientId;
    }

    public void setEncryptedPatientId(String encryptedPatientId) {
        this.encryptedPatientId = encryptedPatientId;
    }
}
