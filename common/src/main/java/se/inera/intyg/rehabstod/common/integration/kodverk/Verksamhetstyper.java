/*
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pebe on 2015-08-19.
 */
public final class Verksamhetstyper {

    public static final String VERKSAMHETSTYP_NAME = "Verksamhetskod";
    public static final String VERKSAMHETSTYP_OID = "1.2.752.129.2.2.1.3";
    public static final String VERKSAMHETSTYP_VERSION = "4.1";

    private static final Map<String, String> VERKSAMHETSTYP_MAP;

    private Verksamhetstyper() {
    }

    static {
        Map<String, String> map = new HashMap();
        map.put("10", "Barn- och ungdomsverksamhet");
        map.put("11", "Medicinsk verksamhet");
        map.put("12", "Laboratorieverksamhet");
        map.put("13", "Opererande verksamhet");
        map.put("14", "Övrig medicinsk verksamhet");
        map.put("15", "Primärvårdsverksamhet");
        map.put("16", "Psykiatrisk verksamhet");
        map.put("17", "Radiologisk verksamhet");
        map.put("18", "Tandvårdsverksamhet");
        map.put("20", "Övrig medicinsk serviceverksamhet");
        VERKSAMHETSTYP_MAP = Collections.unmodifiableMap(map);
    }

    public static String getDisplayName(String code) {
        return VERKSAMHETSTYP_MAP.get(code);
    }

    public static Map<String, String> getVerksamhetstyper() {
        return VERKSAMHETSTYP_MAP;
    }
}
