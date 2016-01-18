package se.inera.intyg.rehabstod.hsa.adapter;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

/**
 * Adapter for converting XML Schema types to Java dates and vice versa.
 *
 * @author andreaskaltenbach
 */
public final class LocalDateAdapter {

    private static final String ISO_DATE_PATTERN = "YYYY-MM-dd";
    private static final String ISO_DATE_TIME_PATTERN = "YYYY-MM-dd'T'HH:mm:ss";

    private static final int DATE_END_INDEX = 10;
    private static final String TIMEZONE_PATTERN = "\\+.*";

    private LocalDateAdapter() {
    }

    /**
     * Converts an xs:date to a Joda Time LocalDate.
     */
    public static LocalDate parseDate(String dateString) {
        if (dateString.length() > DATE_END_INDEX) {
            return new LocalDate(dateString.substring(0, DATE_END_INDEX));
        } else {
            return new LocalDate(dateString);
        }
    }

    /**
     * Converts an xs:datetime to a Joda Time LocalDateTime.
     */
    public static LocalDateTime parseDateTime(String dateString) {

        // crop timezone information ('+...')
        return new LocalDateTime(dateString.replaceAll(TIMEZONE_PATTERN, ""));
    }

    /**
     * Converts an intyg:common-model:1:date to a Joda Time LocalDate.
     */
    public static LocalDate parseIsoDate(String dateString) {
        return LocalDate.parse(dateString);
    }

    /**
     * Converts an intyg:common-model:1:dateTime to a Joda Time LocalDateTime.
     */
    public static LocalDateTime parseIsoDateTime(String dateString) {
        return LocalDateTime.parse(dateString);
    }

    /**
     * Converts a Joda Time LocalDateTime to an xs:datetime.
     */
    public static String printDateTime(LocalDateTime dateTime) {
        return dateTime.toString();
    }

    /**
     * Converts a Joda Time LocalDate to an xs:date.
     */
    public static String printDate(LocalDate date) {
        return date.toString();
    }

    /**
     * Converts a Joda Time LocalDateTime to an intyg:common-model:1:date.
     */
    public static String printIsoDateTime(LocalDateTime dateTime) {
        return dateTime.toString(ISO_DATE_TIME_PATTERN);
    }

    /**
     * Converts a Joda Time LocalDate to an intyg:common-model:1:dateTime.
     */
    public static String printIsoDate(LocalDate date) {
        return date.toString(ISO_DATE_PATTERN);
    }
}
