package org.example;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;

public class Main {
    private final static String hashAlgorithm="SHA-256";
    private final static ArrayList<User> users=new ArrayList<>();
    private final static User currentUser=new User("","",null);
    private final static int passwordLength=7;
    private final static int numberOfThreads=3;

    public static void main(String[] args) {

        promptRegistration();
        //promptVerification();

        //crackPasswordWithAtMost(passwordLength);
        //System.out.println(crackPasswordWithExactly(passwordLength));
        crackPasswordByMultiThread(passwordLength,numberOfThreads);

        System.exit(1);
    }
    private static String crackPasswordWithExactly(int length){
        long startTime = System.currentTimeMillis(); // מתחיל טיימר
        int maxNumber = (int) Math.pow(10, length);

        String password="";
        String hashedPassword="";
        for (int i = 0; i < maxNumber; i++) {
            String formatString = "%0" + length + "d";
            password = String.format(formatString, i);
            hashedPassword = hashPassword(password, toPrimitiveByteArray(currentUser.getSalt()));
            if (hashedPassword.equals(currentUser.getHashedPassword())) {
                break;
            }
        }
        long duration = System.currentTimeMillis() - startTime;       // מודד טיימר
        if (!hashedPassword.equals(currentUser.getHashedPassword())) {
           password="not found";
        }
        return password+"."+duration;
    }
    private static void crackPasswordWithAtMost(int maxLength){
        for(int i=1;i<=maxLength;i++){
            String result= crackPasswordWithExactly(i);
            String duration=result.substring(result.indexOf('.')+1);
            String password=result.substring(0,result.indexOf('.'));
            if(!password.equals("not found")){
                System.out.println("the password is: "+password);
                System.out.println("duration: "+duration+" milliseconds");
            }
        }
    }

    private static void crackPasswordByMultiThread(int passwordLength, int numberOfThreads) {
        long startTime = System.currentTimeMillis(); // מתחיל טיימר

        int totalRange = (int) Math.pow(10, passwordLength); // Total range based on password length
        int rangePerThread = totalRange / numberOfThreads;

        Thread[] threads = new Thread[numberOfThreads];

        for (int i = 0; i < numberOfThreads; i++) {
            int start = i * rangePerThread;
            int end = (i == numberOfThreads - 1) ? totalRange - 1 : (i + 1) * rangePerThread - 1;
            threads[i] = new Thread(new PasswordCrackerThread(start, end, passwordLength,currentUser));
            threads[i].start();
        }
        for (int i = 0; i < numberOfThreads; i++) {try {threads[i].join();} catch (InterruptedException e) {e.printStackTrace();}}// מחכה שכל התהליכונים יסיימו

        long duration = System.currentTimeMillis() - startTime;       // מודד טיימר
        System.out.println("Time taken: " + duration + " milliseconds");// מדפיס תוצאה
    }


    private static void promptRegistration(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("enter your username:");
        String newUsername=scanner.nextLine();
        System.out.println("Create a new password:");
        String newPassword = scanner.nextLine();
        registerUser(newPassword,newUsername);
    }

    private static void registerUser(String password,String username) {
        // Generate a random salt
        // the secure random is choosing 16 bytes meaning 16 numbers which each
        // one is in the range of 127 to -128 and it does so with less predictability then
        // the usual random which i guess takes a bit more time and that is why you dont always use secure random.
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        // Hash the password with salt
        String hashedPassword = hashPassword(password, salt);
        currentUser.setHashedPassword(hashedPassword);
        currentUser.setSalt(toByteObjectArray(salt));
        currentUser.setUsername(username);
        users.add(currentUser);
    }

    private static void promptVerification(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your username for verification:");
        String enteredUsername=scanner.nextLine();
        System.out.println("Enter your password for verification:");
        String enteredPassword = scanner.nextLine();
        if (verifyPassword(enteredPassword,enteredUsername)) {
            System.out.println("Password verified successfully!");
        } else {
            System.out.println("Password verification failed.");
        }
    }
    private static boolean verifyPassword(String password, String username) {
        for (User user:users) {
            if(username.equals(user.getUsername())){
                if(password.equals(user.getHashedPassword())){
                    System.out.println("we suspect you are not the owner of the account...");
                    return false;
                }
                String hashedPassword = hashPassword(password,toPrimitiveByteArray(user.getSalt()));
                if (hashedPassword.equals(user.getHashedPassword())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String hashPassword(String password, byte[] salt) {
        try {
            //the messageDigest is representing the hash algorithm we have chosen.
            // It provides applications the functionality of a message digest algorithm, such as SHA-256.
            // Message digest algorithms are used to produce a fixed-size hash value from arbitrary-length data (like a password).
            MessageDigest digest = MessageDigest.getInstance(hashAlgorithm);
            //we are giving the hash algorithm an additional bytes (the ones generated by secure random) to hash along with the plain text password.
            digest.update(salt);
            //breaks the password into bytes and digest it along with the salt bytes which are already in the MessageDigest  from the previous line and now they are hashed.
            byte[] encodedHash = digest.digest(password.getBytes());

            StringBuilder hexString = new StringBuilder();
            //we go through all the bytes that were hashed and covert them into a hexadecimal string.
            //we do this part for convenience reasons.
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            //System.out.println(hexString);// רק לצורך בדיקה
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static Byte[] toByteObjectArray(byte[] bytesPrimitive) {
        // in this method we turn byte from its primitive type into its wrapper class type by
        // creating a wrapper class array and passing to it the bytes from the primitive type array.
        Byte[] bytesObject = new Byte[bytesPrimitive.length];
        int i = 0;
        for (byte b : bytesPrimitive) {
            bytesObject[i++] = b; // Autoboxing primitive byte to Byte object
        }
        return bytesObject;
    }

    public static byte[] toPrimitiveByteArray(Byte[] bytesObject) {
        //in this method we turn byte from its wrapper class type into its primitive type by
        //creating a primitive type array and passing to it the bytes from the wrapper class array.
        byte[] bytesPrimitive = new byte[bytesObject.length];
        int i = 0;
        for (Byte b : bytesObject) {
            bytesPrimitive[i++] = b; // Unboxing Byte object to primitive byte
        }
        return bytesPrimitive;
    }
}
