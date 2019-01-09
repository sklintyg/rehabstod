/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.web.model;

import java.time.LocalDate;
import java.time.MonthDay;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Magnus Ekstrand on 03/02/16.
 */
public class Patient {

    private static final Logger LOG = LoggerFactory.getLogger(Patient.class);

    private static final int HASH_SEED = 31;

    private static final int GENDER_START = 10;
    private static final int GENDER_END = 11;
    private static final int DATE_PART_OF_PERSON_ID = 8;
    private static final int DAY_PART_OF_DATE_PART = 6;
    private static final int MONTH_PART_OF_DATE_PART = 4;
    private static final int SAMORDNINGSNUMMER_DAY_CONSTANT = 60;

    private static final DateTimeFormatter MONTHDAY_FORMATTER = DateTimeFormatter.ofPattern("MMdd");

    private String id;
    private String namn;

    private Gender kon;

    private int alder;


    public Patient() {
        // When we try to deserialize a JSON String to Patient an Exception
        // “JsonMappingException: No suitable constructor found” will be thrown
        // in absence of a default constructor
    }

    public Patient(String id, String namn) {
        this.id = id.trim();
        this.namn = namn;

        // Default fallback gender
        Gender kon = Gender.UNKNOWN;

        // Default fallback age
        int alder = 0;

        String normalizedPnr;
        if (this.id.matches("^(19|20)[0-9]{6}[-+]?[0-9]{4}$")) {
            normalizedPnr = this.id.replace("-", "").replace("+", "");
            kon = Gender.getGenderFromString(normalizedPnr.substring(GENDER_START, GENDER_END));
            alder = getPatientAge(normalizedPnr);
        } else if (this.id.matches("^[0-9]{6}[+-]?[0-9]{4}$")) {
            normalizedPnr = getCenturyFromYearAndSeparator(this.id) + this.id.replace("-", "").replace("+", "");
            kon = Gender.getGenderFromString(normalizedPnr.substring(GENDER_START, GENDER_END));
            alder = getPatientAge(normalizedPnr);
        } else {
            alder = getPatientAge(this.id);
        }

        this.kon = kon;
        this.alder = alder;
    }


    // getters and setters

    public String getId() {
        return id;
    }

    public String getNamn() {
        return namn;
    }

    // Need namn setter due to PU-service updating the name for us.
    public void setNamn(String namn) {
        this.namn = namn;
    }

    public Gender getKon() {
        return kon;
    }

    public void setKon(Gender kon) {
        this.kon = kon;
    }

    public int getAlder() {
        return alder;
    }

    public void setAlder(int alder) {
        this.alder = alder;
    }


    // api

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Patient patient = (Patient) o;
        if (!id.equals(patient.id)) {
            return false;
        }

        return namn.equals(patient.namn);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = HASH_SEED * result + namn.hashCode();
        return result;
    }


    // private scope - don't touch

    private int getPatientAge(String patientId) {
        int age;

        try {
            String dateString = patientId.substring(0, DATE_PART_OF_PERSON_ID);
            int day = Integer.parseInt(dateString.substring(DAY_PART_OF_DATE_PART));
            int month = Integer.parseInt(dateString.substring(MONTH_PART_OF_DATE_PART, DAY_PART_OF_DATE_PART));

            if (day > SAMORDNINGSNUMMER_DAY_CONSTANT) {
                dateString = dateString.substring(0, MONTH_PART_OF_DATE_PART)
                        + MONTHDAY_FORMATTER.format(MonthDay.of(month, day - SAMORDNINGSNUMMER_DAY_CONSTANT));
            }
            LocalDate birthDate = LocalDate.from(DateTimeFormatter.BASIC_ISO_DATE.parse(dateString));
            Period period = Period.between(birthDate, LocalDate.now());
            age = period.getYears();
        } catch (Exception e) {
            LOG.error("patientId '" + patientId
                    + "' cannot be parsed as a date for age-calculation (adjusting for samordningsnummer did not help)", e);
            age = 0;
        }

        return age;
    }

    private String getCenturyFromYearAndSeparator(String personnummer) {
        final Calendar now = Calendar.getInstance();
        final int currentYear = now.getWeekYear();
        final boolean personnummerContainsCentury = personnummer.matches("[0-9]{8}[-+]?[0-9]{4}");
        final int yearStartIndex = personnummerContainsCentury ? 2 : 0;
        final int yearFromPersonnummer = Integer.parseInt(personnummer.substring(yearStartIndex, yearStartIndex + 2));
        final int dividerToRemoveNonCenturyYear = 100;
        final int century = (currentYear - yearFromPersonnummer) / dividerToRemoveNonCenturyYear;
        if (personnummer.contains("+")) {
            return String.valueOf(century - 1);
        }
        return String.valueOf(century);
    }

}
