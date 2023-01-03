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
public final class Verksamhetstyper {

    public static final String VERKSAMHETSTYP_NAME = "Verksamhetskod";
    public static final String VERKSAMHETSTYP_OID = "1.2.752.129.2.2.1.3";
    public static final String VERKSAMHETSTYP_VERSION = "4.1";

    private static final Map<String, String> VERKSAMHETSTYP_MAP = Map.of(
        "10", "Barn- och ungdomsverksamhet",
        "11", "Medicinsk verksamhet",
        "12", "Laboratorieverksamht",
        "13", "Opererande verksamhet",
        "14", "Övrig medicinsk verksamhet",
        "15", "Primärvårdsverksamhet",
        "16", "Psykiatrisk verksamhet",
        "17", "Radiologisk verksamhet",
        "18", "Tandvårdsverksamhet",
        "20", "Övrig medicinsk serviceverksamhet"
    );

    private Verksamhetstyper() {
    }


    public static String getDisplayName(String code) {
        return VERKSAMHETSTYP_MAP.get(code);
    }

    public static Map<String, String> getVerksamhetstyper() {
        return Map.copyOf(VERKSAMHETSTYP_MAP);
    }
}
