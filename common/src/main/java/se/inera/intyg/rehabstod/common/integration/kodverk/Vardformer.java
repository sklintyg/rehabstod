/*
 * Copyright (C) 2022 Inera AB (http://www.inera.se)
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
public final class Vardformer {

    public static final String VARDFORM_NAME = "Vårdform";
    public static final String VARDFORM_OID = "1.2.752.129.2.2.1.13";
    public static final String VARDFORM_VERSION = "3.0";

    private static final Map<String, String> VARDFORMS_MAP = Map.of(
        "01", "Öppenvård",
        "02", "Slutenvård",
        "03", "Hemsjukvård"
    );

    private Vardformer() {
    }

    public static String getDisplayName(String code) {
        return VARDFORMS_MAP.get(code);
    }

    public static Map<String, String> getVardformer() {
        return Map.copyOf(VARDFORMS_MAP);
    }
}
