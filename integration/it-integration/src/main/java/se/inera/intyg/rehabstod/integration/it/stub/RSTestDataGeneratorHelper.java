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
package se.inera.intyg.rehabstod.integration.it.stub;

import static se.inera.intyg.rehabstod.integration.it.stub.RSTestDataGeneratorImpl.UE_AKUTEN;
import static se.inera.intyg.rehabstod.integration.it.stub.RSTestDataGeneratorImpl.UE_DIALYS;
import static se.inera.intyg.rehabstod.integration.it.stub.RSTestDataGeneratorImpl.VE_CENTRUM_VAST;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eriklupander on 2016-02-01.
 */
public final class RSTestDataGeneratorHelper {

    private RSTestDataGeneratorHelper() {
    }

    public static List<String> getUnderenheterHsaIds(String enhetId) {
        List<String> ids = new ArrayList<>();
        // This is incredibly stupid...
        if (enhetId.equals(VE_CENTRUM_VAST)) {
            ids.add(UE_AKUTEN);
            ids.add(UE_DIALYS);
        }
        return ids;
    }

}
