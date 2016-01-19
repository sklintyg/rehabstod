/*
 * Copyright (C) 2015 Inera AB (http://www.inera.se)
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

package se.inera.intyg.rehabstod.common.util;

import java.util.Arrays;
import java.util.List;

public final class StringUtil {

    /** Hidden constructor. */
    private StringUtil() {
    }

    /** Returns true if string is null or ahs length 0, otherwise false. */
    public static boolean isNullOrEmpty(String string) {
        return string == null || string.length() == 0;
    }

    public static String emptyToNull(String string) {
        if (isNullOrEmpty(string)) {
            return null;
        } else {
            return string;
        }
    }

    public static String join(String separator, List<String> parts) {
        StringBuilder result = new StringBuilder();
        for (String part: parts) {
            if (!isNullOrEmpty(part)) {
                if (result.length() > 0) {
                    result.append(separator);
                }
                result.append(part);
            }
        }
        return result.toString();
    }

    public static String join(String separator, String...parts) {
        return join(separator, Arrays.asList(parts));
    }
}
