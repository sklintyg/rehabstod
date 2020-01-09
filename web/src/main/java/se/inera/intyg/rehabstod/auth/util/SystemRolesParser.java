/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.auth.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides static parsing of systemRole strings.
 *
 * @author eriklupander
 */
public final class SystemRolesParser {

    public static final String HSA_SYSTEMROLE_REHAB_UNIT_PREFIX = "INTYG;Rehab-";

    // The part after prefix is assumed to be a hsa-enhetsid, this will be extracted and compared.
    private static final Pattern HSA_SYSTEMROLE_REHAB_UNIT_PATTERN = Pattern.compile("^" + HSA_SYSTEMROLE_REHAB_UNIT_PREFIX + "(.*)");

    private SystemRolesParser() {

    }

    /**
     * Parses supplied systemRoles into careUnitId's.
     *
     * @param systemRoles List of systemRoles, e.g. "Rehab-[careUnitId]"
     * @return List of careUnitIds.
     */
    public static List<String> parseEnhetsIdsFromSystemRoles(List<String> systemRoles) {
        List<String> idList = new ArrayList<>();
        if (systemRoles == null) {
            return idList;
        }
        for (String s : systemRoles) {
            Matcher matcher = HSA_SYSTEMROLE_REHAB_UNIT_PATTERN.matcher(s);
            if (matcher.find()) {
                idList.add(matcher.group(1));
            }
        }
        return idList;
    }
}
