package org.example;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class Utility {

    public static List<String> generatePasswordsOfLength(int length) {
        List<String> passwords = new ArrayList<>();
        int maxNumber = (int) Math.pow(10, length);

        for (int i = 0; i < maxNumber; i++) {
            String formatString = "%0" + length + "d";
            String password = String.format(formatString, i);
            passwords.add(password);
        }

        return passwords;
    }
}
