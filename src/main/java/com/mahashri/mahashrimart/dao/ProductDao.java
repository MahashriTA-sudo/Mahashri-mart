package com.mahashri.mahashrimart.dao;

import com.mahashri.mahashrimart.dto.ProductRequest;
import com.mahashri.mahashrimart.model.Product;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ProductDao {
    List<Product> findAll() throws SQLException;
    Optional<Product> findById(long id) throws SQLException;
    long create(long sellerId, ProductRequest request) throws SQLException;
    Optional<Product> findByIdForUpdate(Connection connection, long id) throws SQLException;
    boolean decrementStock(Connection connection, long id, int quantity) throws SQLException;
}