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

    @Test
    public void testLocalDateSerializesIntoYYYYMMDD() throws JsonProcessingException {
        String serializedLocalDate = new CustomObjectMapper().writeValueAsString(LocalDate.parse("2016-02-11"));
        assertEquals("\"2016-02-11\"", serializedLocalDate);
    }

    @Test
    public void testLocalDateDeserializesFromYYYYMMDD() throws IOException {
        LocalDate localDate = new CustomObjectMapper().readValue("\"2016-02-11\"", LocalDate.class);
        assertEquals(2016, localDate.getYear());
        assertEquals(2, localDate.getMonthOfYear());
        assertEquals(11, localDate.getDayOfMonth());
    }

}
