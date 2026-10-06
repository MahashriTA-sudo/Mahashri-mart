package com.mahashri.mahashrimart.dao;

import com.mahashri.mahashrimart.model.WishlistItem;

import java.sql.SQLException;
import java.util.List;

public interface WishlistDao {
    List<WishlistItem> findByUserId(long userId) throws SQLException;
    boolean contains(long userId, long productId) throws SQLException;
    void add(long userId, long productId) throws SQLException;
    void remove(long userId, long productId) throws SQLException;
}
