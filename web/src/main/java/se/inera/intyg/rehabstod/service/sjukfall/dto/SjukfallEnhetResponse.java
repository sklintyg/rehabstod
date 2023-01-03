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
package se.inera.intyg.rehabstod.service.sjukfall.dto;

import java.util.List;
import se.inera.intyg.rehabstod.web.model.SjukfallEnhet;

/**
 * Encapsulates the response for sjukfall on a unit with a flag indicating whether SRS predictions could be fetched.
 */
public class SjukfallEnhetResponse {

    private List<SjukfallEnhet> sjukfallList;
    private boolean srsError = false;

    private boolean kompletteringInfoError = false;

    public SjukfallEnhetResponse(List<SjukfallEnhet> sjukfallList, boolean srsError, boolean kompletteringInfoError) {
        this.sjukfallList = sjukfallList;
        this.srsError = srsError;
        this.kompletteringInfoError = kompletteringInfoError;
    }

    public List<SjukfallEnhet> getSjukfallList() {
        return sjukfallList;
    }

    public boolean isSrsError() {
        return srsError;
    }

    public boolean isKompletteringInfoError() {
        return kompletteringInfoError;
    }
}
