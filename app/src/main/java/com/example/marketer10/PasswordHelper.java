package com.example.marketer10;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHelper {

    // Hash a password using BCrypt
    public static String hashPassword(String plainTextPassword) {
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
    }

    // Check a password against a stored hash
    public static boolean checkPassword(String plainTextPassword, String hashedPassword) {
        return BCrypt.checkpw(plainTextPassword, hashedPassword);
    }

    public static boolean isPasswordStrong(String password) {
        // Check for length
        if (password.length() < 4) {
            return false;
        }

        // Check for digit
        if (!password.matches(".*\\d.*")) {
            return false;
        }

        // Check for lower case letter
        if (!password.matches(".*[a-z].*")) {
            return false;
        }

        // Check for upper case letter
        if (!password.matches(".*[A-Z].*")) {
            return false;
        }


        return true;
    }

}
