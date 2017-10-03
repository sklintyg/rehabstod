/**
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
 * <p>
 * This file is part of sklintyg (https://github.com/sklintyg).
 * <p>
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.intyg.rehabstod.web.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static org.junit.Assert.assertEquals;

/**
 * @author Magnus Ekstrand on 2017-02-21.
 */
@RunWith(MockitoJUnitRunner.class)
public class PatientTest {

    private LocalDate tolvanBirthdate = LocalDate.parse("1912-12-12");
    private LocalDate samordningsBirthdate = LocalDate.parse("1970-10-03");


    @Test
    public void testPatient() {
        String fullstandigtNamn = "Anders Andersson";
        String id = " 19121212-1212 ";
        final int expectedYear = (int) ChronoUnit.YEARS.between(tolvanBirthdate, LocalDate.now());

        Patient patient = createPatient(id, fullstandigtNamn);

        assertPatient(id, fullstandigtNamn, expectedYear, Gender.M, patient);
    }

    @Test
    public void testPatientShortId() {
        String fullstandigtNamn = "Anders Andersson";
        String id = " 19121212 ";
        final int expectedYear =  (int) ChronoUnit.YEARS.between(tolvanBirthdate, LocalDate.now());

        Patient patient = createPatient(id, fullstandigtNamn);

        assertPatient(id, fullstandigtNamn, expectedYear, Gender.UNKNOWN, patient);
    }

    @Test
    public void testPatientEvenShorterId() {
        String fullstandigtNamn = "Anders Andersson";
        String id = " 121212 ";
        final int expectedYear = 0;

        Patient patient = createPatient(id, fullstandigtNamn);

        assertPatient(id, fullstandigtNamn, expectedYear, Gender.UNKNOWN, patient);
    }

    @Test
    public void testPatientBadId() {
        String fullstandigtNamn = "Anders Andersson";
        String id = " 191212AB-ABCD ";
        final int expectedYear = 0;

        Patient patient = createPatient(id, fullstandigtNamn);

        assertPatient(id, fullstandigtNamn, expectedYear, Gender.UNKNOWN, patient);
    }

    @Test
    public void testSamordningnummer() {
        String fullstandigtNamn = "Anders Andersson";
        String id = "19701063-2391";

        final int expectedYear = (int) ChronoUnit.YEARS.between(samordningsBirthdate, LocalDate.now());

        Patient patient = createPatient(id, fullstandigtNamn);

        assertPatient(id, fullstandigtNamn, expectedYear, Gender.M, patient);
    }

    @Test
    public void testShortSamordningnummer() {
        String fullstandigtNamn = "Anders Andersson";
        String id = "701063-2391";
        final int expectedYear = (int) ChronoUnit.YEARS.between(samordningsBirthdate, LocalDate.now());

        Patient patient = createPatient(id, fullstandigtNamn);


        assertPatient(id, fullstandigtNamn, expectedYear, Gender.M, patient);
    }

    @Test
    public void testWhitespaceTrimming() {
        String fullstandigtNamn = "Anders Andersson";
        String id = " 19121212-1212 ";

        Patient patient = createPatient(id, fullstandigtNamn);

        assertEquals(id.trim(), patient.getId());
    }


    // private stuff - don't touch

    private void assertPatient(String id, String namn, int alder, Gender kon, Patient p) {
        assertEquals(id.trim(), p.getId());
        assertEquals(namn, p.getNamn());
        assertEquals(alder, p.getAlder());
        assertEquals(kon, p.getKon());
    }

    private Patient createPatient(String patientId, String fullstandigtNamn) {
        Patient patient = new Patient(patientId, fullstandigtNamn);
        return patient;
    }

}
