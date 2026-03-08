package com.airline.util;

import java.util.regex.Pattern;

public class ValidationUtil {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern MOBILE_PATTERN = Pattern.compile("^[6-9]\\d{9}$");

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidMobile(String mobile) {
        if (mobile == null || mobile.trim().isEmpty()) return false;
        String digits = mobile.trim().replaceAll("[^0-9]", "");
        return MOBILE_PATTERN.matcher(digits).matches() || (digits.length() == 10 && digits.matches("^[6-9]\\d{9}$"));
    }

    public static String normalizeMobile(String mobile) {
        if (mobile == null) return "";
        return mobile.trim().replaceAll("[^0-9]", "");
    }
}
