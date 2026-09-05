package com.mahashri.mahashrimart.dao;

import com.mahashri.mahashrimart.model.User;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface UserDao {
    Optional<User> findByEmail(String email) throws SQLException;
    long create(User user) throws SQLException;
    List<User> findAll() throws SQLException;
}