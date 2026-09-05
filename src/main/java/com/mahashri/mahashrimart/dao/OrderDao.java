package com.mahashri.mahashrimart.dao;

import com.mahashri.mahashrimart.model.Order;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface OrderDao {
    long create(Connection connection, Order order) throws SQLException;
    void addItem(Connection connection, long orderId, long productId, int quantity, java.math.BigDecimal unitPrice)
            throws SQLException;
    List<Order> findByBuyerId(long buyerId) throws SQLException;
}