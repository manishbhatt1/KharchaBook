package com.kharchabook.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Pattern;

public final class ValidationUtil {

    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\p{L}\\s'.-]+$");

    private ValidationUtil() {
    }

    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static boolean isValidFullName(String name) {
        if (isBlank(name)) {
            return false;
        }
        return NAME_PATTERN.matcher(name.trim()).matches();
    }

    public static boolean isPositiveAmount(BigDecimal amount) {
        return amount != null && amount.compareTo(BigDecimal.ZERO) > 0;
    }

    public static BigDecimal parseAmount(String raw, StringBuilder error) {
        if (isBlank(raw)) {
            error.append("This field is required. Please fill in all required fields before submitting.");
            return null;
        }
        try {
            BigDecimal a = new BigDecimal(raw.trim());
            if (a.compareTo(BigDecimal.ZERO) <= 0) {
                error.append("Amount must be greater than zero. Please enter a valid amount.");
                return null;
            }
            return a.setScale(2, RoundingMode.HALF_UP);
        } catch (NumberFormatException e) {
            error.append("Please enter a valid numeric amount (e.g. 1500 or 1500.50).");
            return null;
        }
    }

    public static int parseYear(String raw, int defaultYear) {
        if (isBlank(raw)) {
            return defaultYear;
        }
        try {
            return Integer.parseInt(raw.trim());
        } catch (NumberFormatException e) {
            return defaultYear;
        }
    }

    public static Integer parseMonth(String raw) {
        if (isBlank(raw)) {
            return null;
        }
        try {
            int m = Integer.parseInt(raw.trim());
            if (m >= 1 && m <= 12) {
                return m;
            }
        } catch (NumberFormatException ignored) {
        }
        return null;
    }
}
