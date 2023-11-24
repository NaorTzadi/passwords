package org.example;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;

public class Main {
    private static String hashAlgorithm="SHA-256";
    private final static ArrayList<User> users=new ArrayList<>();
    private static CustomPassword customPassword=new CustomPassword(-1,false,-1,-1,false,false,false);
    private static User currentUser=new User("","",null);
    private static int passwordLength=7;
    private static int numberOfThreads=4;

    public static void main(String[] args) {
        mainMenu();
        //promptRegistration();
        //promptVerification();

        //crackPasswordWithAtMost(passwordLength);
        //System.out.println(crackPasswordWithExactly(passwordLength));
        crackPasswordByMultiThread(passwordLength,numberOfThreads);

        System.exit(1);
    }
    private static void mainMenu(){
        Scanner scanner=new Scanner(System.in);
        final String option1="1";final String option2="2";final String option3="3";final String option4="4";
        String decision;
        do {
            System.out.println("press 1 to sign in");
            System.out.println("press 2 to log in");
            System.out.println("press 3 to crack a hashed password");
            System.out.println("press 4 to create a custom password to crack");
            decision=scanner.nextLine();
        }while (!decision.equals(option1)&& !decision.equals(option2)&& !decision.equals(option3)&&!decision.equals(option4));

        if(decision.equals(option1)){
            promptRegistration();
        } else if (decision.equals(option2)) {
            promptVerification();
        } else if (decision.equals(option3)) {
            passwordCrackerMenu();
        } else{

        }

    }
    public static void passwordCrackerMenu(){
        promptHashType();

        Scanner scanner=new Scanner(System.in);
        final String option1="1";final String option2="2";final String option3="3";final String option4="4";
        String decision;
        do {
            System.out.println("press 1 to crack with exactly X number of characters");
            System.out.println("press 2 to crack with at most X number of characters");
            System.out.println("press 3 to crack with multi thread");
            System.out.println("press 4 to go back to the main menu");
            decision=scanner.nextLine();
        }while (!decision.equals(option1)&& !decision.equals(option2)&& !decision.equals(option3) && !decision.equals(option4));
        if(decision.equals(option1)){
            Utility.promptLengthOfPassword("exact");
            passwordLength=Integer.parseInt(decision);
            crackPasswordWithExactly(passwordLength);
        } else if (decision.equals(option2)) {
            Utility.promptLengthOfPassword("max");
            passwordLength=Integer.parseInt(decision);
            crackPasswordWithAtMost(passwordLength);
        } else if (decision.equals(option3)){
            Utility.promptLengthOfPassword("exact");
            System.out.println("choose the amount of threads based on your pc");
            decision=scanner.nextLine();
            if(decision.matches("-?\\d+")){
                numberOfThreads=Integer.parseInt(decision);
                crackPasswordByMultiThread(passwordLength,numberOfThreads);
            }
        }else {
            mainMenu();
        }
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

    private static void customPasswordMenu(){
        Scanner scanner=new Scanner(System.in);
        String decision;
        decision=scanner.nextLine();
        System.out.println("max number of characters:");
        System.out.println("min number of characters:");
        System.out.println("exact number of characters:");
        System.out.println("is capital letter required:");
        System.out.println("is lower letter required:");
        System.out.println("is unique characters required:");
        System.out.println("is allowed to contain username:");





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

    private static void promptHashType(){
        final String backToMainMenuOption="0";
        Scanner scanner=new Scanner(System.in);
        System.out.println("you can press 0 at any time to go back to the main menu.");
        System.out.println("please choose the hash type used");
        String chosenHashType=scanner.nextLine();
        if(chosenHashType.equals(backToMainMenuOption)){
            mainMenu();
        }
        for(String hashType:Utility.getListOfHashAlgorithms()){
            if (chosenHashType.equals(hashType)){
                hashAlgorithm=chosenHashType;
                break;
            }
        }
        if(!hashAlgorithm.equals(chosenHashType)){
            System.out.println("invalid hash type, here's a list:");
            Utility.printListOfHashAlgorithms();
            promptHashType();
        }
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
