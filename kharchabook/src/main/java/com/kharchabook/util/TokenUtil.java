package com.kharchabook.util;

import java.security.SecureRandom;
import java.util.Base64;

public final class TokenUtil {

    private static final SecureRandom RNG = new SecureRandom();
    private static final Base64.Encoder B64URL = Base64.getUrlEncoder().withoutPadding();

    private TokenUtil() {
    }

    public static String randomUrlToken(int bytes) {
        if (bytes <= 0) {
            throw new IllegalArgumentException("bytes must be > 0");
        }
        byte[] buf = new byte[bytes];
        RNG.nextBytes(buf);
        return B64URL.encodeToString(buf);
    }
}

