/*
 * Copyright (C) 2025 Inera AB (http://www.inera.se)
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

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LUCertificate {

    private String certificateId;
    private String certificateType;

    private String careProviderId;
    private String careProviderName;
    private String careUnitId;
    private String careUnitName;

    private Lakare doctor;

    private Patient patient;
    private String encryptedPatientId;

    private Diagnos diagnosis;
    private List<Diagnos> biDiagnoses;

    private LocalDateTime signingTimeStamp;

    private int unAnsweredComplement;
    private int unAnsweredOther;

}
