package com.ragesh.rageshmart.util;

import java.math.BigDecimal;
import java.util.regex.Pattern;

/** Shared input validation helpers used at the top of service methods (Section 13, rule 5). */
public final class ValidationUtil {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private ValidationUtil() { }

    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 8;
    }

    public static boolean isPositive(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) > 0;
    }

    public static boolean isNonNegativeInt(int value) {
        return value >= 0;
    }

    public static boolean isValidRating(int rating) {
        return rating >= 1 && rating <= 5;
    }
}
