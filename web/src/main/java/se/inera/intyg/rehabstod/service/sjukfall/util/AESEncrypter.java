/*
 * Copyright (C) 2023 Inera AB (http://www.inera.se)
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
public class AESEncrypter {

    private static final Logger LOG = LoggerFactory.getLogger(AESEncrypter.class);
    private static final String INIT_VECTOR = "fedcba9876543210";
    @Value("${aes.encryption.key}")
    private String key;

    public String encryptPatientId(String value) {
        try {
            final var iv = new IvParameterSpec(INIT_VECTOR.getBytes(StandardCharsets.UTF_8));
            final var keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");

            final var cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);

            final var encryptedValue = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedValue).replace("/", "-");
        } catch (Exception ex) {
            LOG.error("AESEncryption error - Could not encrypt value: {}", value);
            throw new RuntimeException(ex);
        }
    }

    public String decryptPatientId(String encrypted) {
        try {
            final var decryptedCiphertext = Base64.getDecoder().decode(encrypted.replace("-", "/"));
            final var iv = new IvParameterSpec(INIT_VECTOR.getBytes(StandardCharsets.UTF_8));
            final var keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");

            final var cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, iv);

            final var originalValue = cipher.doFinal(decryptedCiphertext);
            return new String(originalValue, StandardCharsets.UTF_8).replace("-", "");
        } catch (Exception ex) {
            LOG.error("AESEncryption error - Could not decrypt value: {}", encrypted);
            throw new RuntimeException(ex);
        }
    }
}
