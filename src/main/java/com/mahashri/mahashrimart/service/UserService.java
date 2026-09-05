package com.mahashri.mahashrimart.service;

import com.mahashri.mahashrimart.dao.UserDao;
import com.mahashri.mahashrimart.dto.LoginRequest;
import com.mahashri.mahashrimart.dto.RegistrationRequest;
import com.mahashri.mahashrimart.exception.AuthenticationException;
import com.mahashri.mahashrimart.exception.ValidationException;
import com.mahashri.mahashrimart.model.User;
import com.mahashri.mahashrimart.util.PasswordUtil;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class UserService {
    private final UserDao userDao;

    public UserService(UserDao userDao) { this.userDao = userDao; }

    public User register(RegistrationRequest request) throws SQLException, ValidationException {
        if (userDao.findByEmail(request.email()).isPresent()) {
            throw new ValidationException("An account with that email already exists.");
        }
        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPasswordHash(PasswordUtil.hash(request.password()));
        user.setRole(request.role());
        user.setId(userDao.create(user));
        return user;
    }

    public User authenticate(LoginRequest request) throws SQLException, AuthenticationException {
        Optional<User> user = userDao.findByEmail(request.email());
        if (user.isEmpty() || !PasswordUtil.matches(request.password(), user.get().getPasswordHash())) {
            throw new AuthenticationException("Email or password is incorrect.");
        }
        return user.get();
    }

    public List<User> listAll() throws SQLException {
        return userDao.findAll();
    }
}