/**
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
 *
 * This file is part of rehabstod (https://github.com/sklintyg/rehabstod).
 *
 * rehabstod is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * rehabstod is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.intyg.rehabstod.common.integration.json;

import static org.junit.Assert.assertEquals;

import org.joda.time.LocalDate;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;

/**
 * Created by eriklupander on 2016-02-01.
 */
public class CustomObjectMapperTest {

    private static final int YEAR = 2016;
    private static final int MONTH = 2;
    private static final int DAY = 11;

    @Test
    public void testLocalDateSerializesIntoYYYYMMDD() throws JsonProcessingException {
        String serializedLocalDate = new CustomObjectMapper().writeValueAsString(LocalDate.parse("2016-02-11"));
        assertEquals("\"2016-02-11\"", serializedLocalDate);
    }

    @Test
    public void testLocalDateDeserializesFromYYYYMMDD() throws IOException {
        LocalDate localDate = new CustomObjectMapper().readValue("\"2016-02-11\"", LocalDate.class);
        assertEquals(YEAR, localDate.getYear());
        assertEquals(MONTH, localDate.getMonthOfYear());
        assertEquals(DAY, localDate.getDayOfMonth());
    }

}
