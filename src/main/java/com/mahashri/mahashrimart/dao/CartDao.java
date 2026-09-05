package com.mahashri.mahashrimart.dao;

import com.mahashri.mahashrimart.model.CartItem;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface CartDao {
    List<CartItem> findByUserId(long userId) throws SQLException;
    void addItem(long userId, long productId, int quantity) throws SQLException;
    void updateQuantity(long userId, long productId, int quantity) throws SQLException;
    void removeItem(long userId, long productId) throws SQLException;
    List<CartItem> findByUserId(Connection connection, long userId) throws SQLException;
    void clear(Connection connection, long userId) throws SQLException;
}