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

package se.inera.intyg.rehabstod.web.controller.api.dto;

import java.util.List;
import se.inera.intyg.rehabstod.service.diagnos.dto.DiagnosKapitel;
import se.inera.intyg.rehabstod.web.model.Lakare;

public class PopulateFiltersResponseDTO {
    private List<Lakare> activeDoctors;
    private List<DiagnosKapitel> allDiagnosisChapters;
    private List<DiagnosKapitel> enabledDiagnosisChapters;

    public PopulateFiltersResponseDTO(List<Lakare> activeDoctors, List<DiagnosKapitel> allDiagnosisChapters,
        List<DiagnosKapitel> enabledDiagnosisChapters) {
        this.activeDoctors = activeDoctors;
        this.allDiagnosisChapters = allDiagnosisChapters;
        this.enabledDiagnosisChapters = enabledDiagnosisChapters;
    }

    public List<Lakare> getActiveDoctors() {
        return activeDoctors;
    }

    public void setActiveDoctors(List<Lakare> activeDoctors) {
        this.activeDoctors = activeDoctors;
    }

    public List<DiagnosKapitel> getAllDiagnosisChapters() {
        return allDiagnosisChapters;
    }

    public void setAllDiagnosisChapters(List<DiagnosKapitel> allDiagnosisChapters) {
        this.allDiagnosisChapters = allDiagnosisChapters;
    }

    public List<DiagnosKapitel> getEnabledDiagnosisChapters() {
        return enabledDiagnosisChapters;
    }

    public void setEnabledDiagnosisChapters(List<DiagnosKapitel> enabledDiagnosisChapters) {
        this.enabledDiagnosisChapters = enabledDiagnosisChapters;
    }
}
