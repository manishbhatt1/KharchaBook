package com.kharchabook.util;

import java.security.SecureRandom;
import java.util.Base64;

public class TokenUtil {
    
    private static final SecureRandom random = new SecureRandom();
    
    public static String randomUrlToken(int length) {
        byte[] bytes = new byte[length];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
