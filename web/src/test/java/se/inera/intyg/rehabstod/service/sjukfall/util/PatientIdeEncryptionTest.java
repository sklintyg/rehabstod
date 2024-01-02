/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
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

package se.inera.intyg.rehabstod.service.sjukfall.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class PatientIdeEncryptionTest {

    private PatientIdEncryption patientIdEncryption;

    private static final String PATIENT_ID = "19121212-1212";
    private static final String PATIENT_ID_2 = "19131313-1313";
    private static final String PATIENT_ID_3 = "19141414-1414";
    private static final String KEY = "McQfTjWmZq4t7w!z%C*F-JaNdRgUkXp2";
    private static final String INVALID_KEY = "";

    @Test
    void shallDecryptToOriginalValue() {
        patientIdEncryption = new PatientIdEncryption(KEY);
        final var expectedValue = "191212121212";
        final var encryptedValue = patientIdEncryption.encrypt(PATIENT_ID);
        assertEquals(expectedValue, patientIdEncryption.decrypt(encryptedValue));
    }

    @Test
    void shallDecryptMultiplePatientIds() {
        patientIdEncryption = new PatientIdEncryption(KEY);
        final var firstExpectedValue = "191212121212";
        final var firstEncryptedPatientId = patientIdEncryption.encrypt(PATIENT_ID);
        final var secondExpectedValue = "191313131313";
        final var secondEncryptedPatientId = patientIdEncryption.encrypt(PATIENT_ID_2);
        final var thirdExpectedValue = "191414141414";
        final var thirdEncryptedPatientId = patientIdEncryption.encrypt(PATIENT_ID_3);

        assertEquals(firstExpectedValue, patientIdEncryption.decrypt(firstEncryptedPatientId));
        assertEquals(secondExpectedValue, patientIdEncryption.decrypt(secondEncryptedPatientId));
        assertEquals(thirdExpectedValue, patientIdEncryption.decrypt(thirdEncryptedPatientId));
    }

    @Test
    void shallThrowRuntimeException() {
        assertThrows(RuntimeException.class, () -> new PatientIdEncryption(INVALID_KEY));
    }
}