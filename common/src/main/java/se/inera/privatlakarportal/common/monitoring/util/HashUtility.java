package se.inera.privatlakarportal.common.monitoring.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

public final class HashUtility {

    private HashUtility() {
    }

    private static final Logger LOGGER = LoggerFactory
            .getLogger(HashUtility.class);

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
