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
package se.inera.intyg.rehabstod.common.integration.kodverk;

import java.util.Map;

/**
 * Created by pebe on 2015-08-19.
 */
public final class Befattningar {

    public static final String BEFATTNING_NAME = "Befattning HSA";
    public static final String BEFATTNING_OID = "1.2.752.129.2.2.1.4";
    public static final String BEFATTNING_VERSION = "3.1";

    private static final Map<String, String> BEFATTNINGS_MAP = Map.of(
        "201011", "Distriktsläkare/Specialist allmänmedicin",
        "201012", "Skolläkare",
        "201013", "Företagsläkare",
        "202010", "Specialistläkare",
        "203010", "Läkare legitimerad, specialiseringstjänstgöring",
        "203090", "Läkare legitimerad, annan"
    );

    private Befattningar() {
    }

    public static String getDisplayName(String code) {
        return BEFATTNINGS_MAP.get(code);
    }

    public static Map<String, String> getBefattningar() {
        return Map.copyOf(BEFATTNINGS_MAP);
    }
}
