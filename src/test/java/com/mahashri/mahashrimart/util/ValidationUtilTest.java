package com.mahashri.mahashrimart.util;

import com.mahashri.mahashrimart.exception.ValidationException;
import com.mahashri.mahashrimart.model.Role;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ValidationUtilTest {
    @Test
    void registrationNormalizesEmailAndBuildsRole() throws ValidationException {
        var request = ValidationUtil.registration(" Riya ", "RIYA@example.com ", "password123", "BUYER");

        assertEquals("Riya", request.name());
        assertEquals("riya@example.com", request.email());
        assertEquals(Role.BUYER, request.role());
    }

    @Test
    void productRejectsNegativeStock() {
        assertThrows(ValidationException.class, () ->
                ValidationUtil.product("Bowl", "A bowl", "50", "-1", "Home", ""));
    }

    @Test
    void productParsesMoneyAndOptionalImage() throws ValidationException {
        var request = ValidationUtil.product("Bowl", "A bowl", "50.50", "3", "Home", " https://example.com/bowl.jpg ");

        assertEquals(new BigDecimal("50.50"), request.price());
        assertEquals(3, request.stockQty());
        assertEquals("https://example.com/bowl.jpg", request.imageUrl());
    }
}