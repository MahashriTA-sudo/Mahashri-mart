package com.mahashri.mahashrimart.service;

import com.mahashri.mahashrimart.dao.UserDao;
import com.mahashri.mahashrimart.dto.LoginRequest;
import com.mahashri.mahashrimart.dto.RegistrationRequest;
import com.mahashri.mahashrimart.exception.AuthenticationException;
import com.mahashri.mahashrimart.exception.ValidationException;
import com.mahashri.mahashrimart.model.Role;
import com.mahashri.mahashrimart.model.User;
import com.mahashri.mahashrimart.util.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {
    private UserDao userDao;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userDao = mock(UserDao.class);
        userService = new UserService(userDao);
    }

    @Test
    void registrationSucceedsForNewEmail() throws Exception {
        when(userDao.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(userDao.create(any())).thenReturn(42L);

        RegistrationRequest request = new RegistrationRequest("Test User", "new@example.com", "password123", Role.BUYER);
        User result = userService.register(request);

        assertEquals(42L, result.getId());
        assertEquals("new@example.com", result.getEmail());
        verify(userDao).create(any());
    }

    @Test
    void registrationRejectsDuplicateEmail() throws SQLException {
        User existing = new User();
        existing.setEmail("taken@example.com");
        when(userDao.findByEmail("taken@example.com")).thenReturn(Optional.of(existing));

        RegistrationRequest request = new RegistrationRequest("Test User", "taken@example.com", "password123", Role.BUYER);

        assertThrows(ValidationException.class, () -> userService.register(request));
    }

    @Test
    void authenticateSucceedsWithCorrectPassword() throws Exception {
        User existing = new User();
        existing.setEmail("user@example.com");
        existing.setPasswordHash(PasswordUtil.hash("correctPassword"));
        when(userDao.findByEmail("user@example.com")).thenReturn(Optional.of(existing));

        LoginRequest request = new LoginRequest("user@example.com", "correctPassword");
        User result = userService.authenticate(request);

        assertEquals("user@example.com", result.getEmail());
    }

    @Test
    void authenticateFailsWithWrongPassword() throws SQLException {
        User existing = new User();
        existing.setEmail("user@example.com");
        existing.setPasswordHash(PasswordUtil.hash("correctPassword"));
        when(userDao.findByEmail("user@example.com")).thenReturn(Optional.of(existing));

        LoginRequest request = new LoginRequest("user@example.com", "wrongPassword");

        assertThrows(AuthenticationException.class, () -> userService.authenticate(request));
    }

    @Test
    void authenticateFailsForUnknownEmail() throws SQLException {
        when(userDao.findByEmail("nobody@example.com")).thenReturn(Optional.empty());

        LoginRequest request = new LoginRequest("nobody@example.com", "anyPassword");

        assertThrows(AuthenticationException.class, () -> userService.authenticate(request));
    }
}