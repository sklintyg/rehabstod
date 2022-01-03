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
package se.inera.intyg.rehabstod.common.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Formats {@link java.time.LocalDateTime} into HH:mm format.
 *
 * Created by eriklupander on 2016-09-02.
 */
public final class HourMinuteFormatter {

    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    private HourMinuteFormatter() {

    }

    public static String print(LocalDateTime localDateTime) {
        return localDateTime.format(dateTimeFormatter);
    }
}
