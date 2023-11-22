package org.example;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class Utility {
    public static List<String> generateAll4DigitPasswords() {
        List<String> passwords = new ArrayList<>();

        for (int i = 0; i < 10000; i++) {
            String password = String.format("%04d", i);
            passwords.add(password);
        }
        return passwords;
    }
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes());
            byte[] digest = md.digest();
            BigInteger number = new BigInteger(1, digest);
            StringBuilder hexString = new StringBuilder(number.toString(16));

            // Pad with leading zeros if necessary
            while (hexString.length() < 64) {
                hexString.insert(0, '0');
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 hashing algorithm not found", e);
        }
    }
}
