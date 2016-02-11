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
package se.inera.intyg.rehabstod.service.sjukfall.ruleengine;

import org.springframework.beans.factory.annotation.Autowired;
import se.inera.intyg.rehabstod.service.diagnos.DiagnosBeskrivningService;
import se.inera.intyg.rehabstod.service.diagnos.DiagnosKapitelService;
import se.inera.intyg.rehabstod.service.diagnos.dto.DiagnosKapitel;
import se.inera.intyg.rehabstod.service.diagnos.dto.DiagnosKod;
import se.inera.intyg.rehabstod.web.controller.api.dto.GetSjukfallRequest;
import se.inera.intyg.rehabstod.web.model.Diagnos;
import se.inera.intyg.rehabstod.web.model.Sjukfall;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.IntygsData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Magnus Ekstrand on 03/02/16.
 */
public class SjukfallCalculatorEngine {

    @Autowired
    private DiagnosBeskrivningService diagnosBeskrivningService;

    @Autowired
    private DiagnosKapitelService diagnosKapitelService;


    public SjukfallCalculatorEngine() {
    }

    public List<Sjukfall> calculate(List<IntygsData> intygsData, GetSjukfallRequest requestData) {
        return new ArrayList();
    }

    protected Diagnos getDiagnos(IntygsData intyg) {
        String cleanedDiagnosKod = DiagnosKod.cleanKod(intyg.getDiagnos().getKod());
        String description = diagnosBeskrivningService.getDiagnosBeskrivning(cleanedDiagnosKod);
        DiagnosKapitel diagnosKaptiel = diagnosKapitelService.getDiagnosKapitel(cleanedDiagnosKod);

        Diagnos diagnos = new Diagnos();
        diagnos.setIntygsVarde(intyg.getDiagnos().getKod());
        diagnos.setKapitel(diagnosKaptiel.getId());
        diagnos.setKod(cleanedDiagnosKod);
        diagnos.setBeskrivning(description);

        return diagnos;
    }
}
