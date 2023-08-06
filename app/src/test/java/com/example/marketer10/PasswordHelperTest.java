package com.example.marketer10;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PasswordHelperTest {

    @Test
    public void passwordTrue() {
        String password = "password";
        String hashedPassword = PasswordHelper.hashPassword(password);

        assertTrue(PasswordHelper.checkPassword(password, hashedPassword));
    }

    @Test
    public void passwordFalse() {
        String password = "password";
        String wrongPassword = "wrongPassword";
        String hashedPassword = PasswordHelper.hashPassword(password);

        assertFalse(PasswordHelper.checkPassword(wrongPassword, hashedPassword));
    }
}
