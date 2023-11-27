package org.example;

import java.util.concurrent.atomic.AtomicBoolean;

public class PasswordCrackerThread implements Runnable {
    private static final AtomicBoolean found = new AtomicBoolean(false);

    private final int start;
    private final int end;
    private final int passwordLength;
    private final User user;

    public PasswordCrackerThread(int start, int end, int passwordLength,User user) {
        this.start = start;
        this.end = end;
        this.passwordLength = passwordLength;
        this.user=user;
    }

    @Override
    public void run() {
        for (int i = start; i <= end && !found.get() ; i++) {
            String formatString = "%0" + passwordLength + "d";
            String password = String.format(formatString, i);
            String hashedPassword = Main.hashPassword(password,Main.toPrimitiveByteArray(user.getSalt()));
            if (hashedPassword.equals(user.getHashedPassword())){
                System.out.println("the password is: "+password);
                found.set(true);
                break;
            }
        }
    }

}
