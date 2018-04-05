/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.common.monitoring.util;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class HashUtility {

    private static final Logger LOGGER = LoggerFactory.getLogger(HashUtility.class);

    private static final String DIGEST = "SHA-256";
    private static final MessageDigest MSG_DIGEST;

    public static final String EMPTY = "EMPTY";
    private static final String NO_HASH_VALUE = "NO-HASH-VALUE";

    static {
        MessageDigest tmp = null;
        try {
            tmp = MessageDigest.getInstance(DIGEST);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("MessageDigest instantiation failed", e);
        }
        MSG_DIGEST = tmp;
    }


    // constructors

    private HashUtility() {
    }


    // api

    public static String hash(String payload) {
        if (StringUtils.isEmpty(payload)) {
            return EMPTY;
        }

        if (MSG_DIGEST == null) {
            LOGGER.error("Hashing not working due to MessageDigest not being instantiated");
            return NO_HASH_VALUE;
        }

        try {
            MSG_DIGEST.update(payload.getBytes("UTF-8"));
            byte[] digest = MSG_DIGEST.digest();
            return new String(Hex.encodeHex(digest));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }
}
