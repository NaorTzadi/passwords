package org.example;

public class User {
    private String username;
    private String hashedPassword;
    private Byte[] salt;

    public User(String username, String hashedPassword, Byte[] salt) {
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.salt = salt;
    }
    public String getUsername() {return username;}
    public String getHashedPassword() {return hashedPassword;}
    public Byte[] getSalt() {return salt;}
    public void setUsername(String username) {this.username = username;}
    public void setHashedPassword(String hashedPassword) {this.hashedPassword = hashedPassword;}
    public void setSalt(Byte[] salt) {this.salt = salt;}
}
