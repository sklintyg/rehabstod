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

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PatientIdEncryption {

    private static final Logger LOG = LoggerFactory.getLogger(PatientIdEncryption.class);
    private static final String INIT_VECTOR = "fedcba9876543210";
    private final Cipher cipher;
    private final IvParameterSpec ivParameterSpec;
    private final SecretKeySpec secretKeySpec;

    public PatientIdEncryption(@Value("${aes.encryption.key}") String key) {
        try {
            ivParameterSpec = new IvParameterSpec(INIT_VECTOR.getBytes(StandardCharsets.UTF_8));
            secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
            cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        } catch (Exception exception) {
            LOG.error("PatientIdEncryption error - Could not generate key from value: {}", key);
            throw new RuntimeException(exception);
        }
    }

    public String encrypt(String value) {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
            return encodeAndFormat(cipher.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            LOG.error("PatientIdEncryption error - Could not encrypt value: {}", value);
            throw new RuntimeException(ex);
        }
    }

    public String decrypt(String encrypted) {
        try {
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
            return decodeAndFormat(cipher.doFinal(getDecryptedValue(encrypted)));
        } catch (Exception ex) {
            LOG.error("PatientIdEncryption error - Could not decrypt value: {}", encrypted);
            throw new RuntimeException(ex);
        }
    }

    private static String encodeAndFormat(byte[] encryptedValue) {
        return Base64.getEncoder().encodeToString(encryptedValue).replace("/", "-");
    }

    private static String decodeAndFormat(byte[] originalValue) {
        return new String(originalValue, StandardCharsets.UTF_8).replace("-", "");
    }

    private static byte[] getDecryptedValue(String encrypted) {
        return Base64.getDecoder().decode(encrypted.replace("-", "/"));
    }
}
