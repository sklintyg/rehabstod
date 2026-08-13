/*
 * Copyright (C) 2026 Inera AB (http://www.inera.se)
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
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.AEADBadTagException;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PatientIdEncryption {

  private static final Logger LOG = LoggerFactory.getLogger(PatientIdEncryption.class);
  private static final String TRANSFORMATION = "AES/GCM/NoPadding";
  private static final int GCM_IV_LENGTH_BYTES = 12;
  private static final int GCM_TAG_LENGTH_BITS = 128;
  private final SecretKeySpec secretKeySpec;
  private final SecureRandom random = new SecureRandom();

  public PatientIdEncryption(@Value("${aes.encryption.key}") String key) {
    try {
      final var keyBytes = key.getBytes(StandardCharsets.UTF_8);
      if (keyBytes.length != 16 && keyBytes.length != 24 && keyBytes.length != 32) {
        throw new IllegalArgumentException(
            "AES key must be 16, 24 or 32 bytes, but was " + keyBytes.length + " bytes");
      }
      secretKeySpec = new SecretKeySpec(keyBytes, "AES");
      // Fail fast at startup if the key/transformation combination is unusable.
      createCipher(Cipher.ENCRYPT_MODE, new GCMParameterSpec(GCM_TAG_LENGTH_BITS, randomIv()));
    } catch (Exception exception) {
      LOG.error("PatientIdEncryption error - Could not generate key from configured value");
      throw new RuntimeException(exception);
    }
  }

  private byte[] randomIv() {
    final var iv = new byte[GCM_IV_LENGTH_BYTES];
    random.nextBytes(iv);
    return iv;
  }

  public String encrypt(String value) {
    try {
      final var iv = randomIv();
      final var cipher =
          createCipher(Cipher.ENCRYPT_MODE, new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));
      final var cipherText = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));
      // The IV is not secret and must be stored/transmitted alongside the ciphertext so the
      // same IV can be used when decrypting. It is prepended to the ciphertext.
      final var combined = new byte[iv.length + cipherText.length];
      System.arraycopy(iv, 0, combined, 0, iv.length);
      System.arraycopy(cipherText, 0, combined, iv.length, cipherText.length);
      return encodeAndFormat(combined);
    } catch (Exception ex) {
      LOG.error("PatientIdEncryption error - Could not encrypt value", ex);
      throw new RuntimeException(ex);
    }
  }

  public String decrypt(String encrypted) {
    try {
      final var combined = getDecryptedValue(encrypted);
      if (combined.length <= GCM_IV_LENGTH_BYTES) {
        throw new IllegalArgumentException("Encrypted value is too short to contain an IV");
      }
      final var iv = Arrays.copyOfRange(combined, 0, GCM_IV_LENGTH_BYTES);
      final var cipherText = Arrays.copyOfRange(combined, GCM_IV_LENGTH_BYTES, combined.length);
      final var cipher =
          createCipher(Cipher.DECRYPT_MODE, new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));
      return decodeAndFormat(cipher.doFinal(cipherText));
    } catch (AEADBadTagException ex) {
      LOG.error(
          "PatientIdEncryption error - Authentication tag mismatch, value may be tampered "
              + "with or encrypted with a different key");
      throw new RuntimeException(ex);
    } catch (Exception ex) {
      LOG.error("PatientIdEncryption error - Could not decrypt value");
      throw new RuntimeException(ex);
    }
  }

  /**
   * {@link Cipher} instances are stateful and not thread-safe, so a new instance must be created
   * per invocation instead of sharing one across the singleton bean/threads.
   */
  private Cipher createCipher(int mode, GCMParameterSpec gcmParameterSpec) {
    try {
      final var cipher = Cipher.getInstance(TRANSFORMATION);
      cipher.init(mode, secretKeySpec, gcmParameterSpec);
      return cipher;
    } catch (NoSuchAlgorithmException | NoSuchPaddingException ex) {
      LOG.error(
          "PatientIdEncryption error - Could not initialize cipher with cipher mode: {}",
          TRANSFORMATION,
          ex);
      throw new RuntimeException(ex);
    } catch (InvalidKeyException | InvalidAlgorithmParameterException ex) {
      LOG.error("PatientIdEncryption error - Could not initialize cipher", ex);
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
