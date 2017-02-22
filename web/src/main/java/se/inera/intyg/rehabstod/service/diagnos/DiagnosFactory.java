/**
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
 * <p>
 * This file is part of sklintyg (https://github.com/sklintyg).
 * <p>
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.intyg.rehabstod.service.diagnos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import se.inera.intyg.rehabstod.web.model.Diagnos;

/**
 * @author Magnus Ekstrand on 2017-02-17.
 */
@Component
public final class DiagnosFactory {

    @Autowired
    private DiagnosBeskrivningService diagnosBeskrivningService;

    @Autowired
    private DiagnosKapitelService diagnosKapitelService;

    public Diagnos getDiagnos(String orginalVarde, String diagnosKod, String diagnosNamn) {
        Diagnos diagnos = new Diagnos(orginalVarde, diagnosKod, diagnosNamn);

        // populate beskrivning and kapitel
        diagnos.setBeskrivning(diagnosBeskrivningService.getDiagnosBeskrivning(diagnosKod));
        diagnos.setKapitel(diagnosKapitelService.getDiagnosKapitel(diagnosKod).getId());

        return diagnos;
    }

}
