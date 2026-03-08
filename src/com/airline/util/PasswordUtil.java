package com.airline.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordUtil {
    private static final String SALT = "AirlineReservation2025";
    private static final String ALGORITHM = "SHA-256";

    public static String hash(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
            String combined = SALT + password;
            byte[] hashBytes = digest.digest(combined.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hashing failed", e);
        }
    }

    public static boolean verify(String password, String storedHash) {
        String inputHash = hash(password);
        return inputHash.equals(storedHash);
    }
}
