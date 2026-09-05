package com.mahashri.mahashrimart.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordUtilTest {
    @Test
    void hashesAreVerifiableWithoutStoringPlaintext() {
        String hash = PasswordUtil.hash("correct horse battery staple");

        assertNotEquals("correct horse battery staple", hash);
        assertTrue(PasswordUtil.matches("correct horse battery staple", hash));
        assertFalse(PasswordUtil.matches("wrong password", hash));
    }
}