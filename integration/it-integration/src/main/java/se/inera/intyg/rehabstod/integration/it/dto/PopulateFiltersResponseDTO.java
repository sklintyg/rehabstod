/*
 * Copyright (C) 2023 Inera AB (http://www.inera.se)
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

package se.inera.intyg.rehabstod.integration.it.dto;

import java.util.List;
import se.inera.intyg.infra.sjukfall.dto.DiagnosKod;
import se.inera.intyg.infra.sjukfall.dto.Lakare;

public class PopulateFiltersResponseDTO {
    private List<Lakare> activeDoctors;
    private List<DiagnosKod> diagnoses;

    public PopulateFiltersResponseDTO(List<Lakare> activeDoctors, List<DiagnosKod> diagnoses) {
        this.activeDoctors = activeDoctors;
        this.diagnoses = diagnoses;
    }

    public List<Lakare> getActiveDoctors() {
        return activeDoctors;
    }

    public void setActiveDoctors(List<Lakare> activeDoctors) {
        this.activeDoctors = activeDoctors;
    }

    public List<DiagnosKod> getDiagnoses() {
        return diagnoses;
    }

    public void setDiagnoses(List<DiagnosKod> diagnoses) {
        this.diagnoses = diagnoses;
    }
}
