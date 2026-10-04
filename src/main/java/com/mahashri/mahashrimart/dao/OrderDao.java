package com.mahashri.mahashrimart.dao;

import com.mahashri.mahashrimart.model.Order;
import com.mahashri.mahashrimart.model.OrderStatus;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface OrderDao {
    long create(Connection connection, Order order) throws SQLException;
    void addItem(Connection connection, long orderId, long productId, int quantity, java.math.BigDecimal unitPrice)
            throws SQLException;
    List<Order> findByBuyerId(long buyerId) throws SQLException;
    List<Order> findAll() throws SQLException;
    List<Order> findBySellerId(long sellerId) throws SQLException;
    Optional<Order> findById(long orderId) throws SQLException;
    boolean updateStatus(Connection connection, long orderId, OrderStatus newStatus) throws SQLException;
    List<Order> findAllByStatus(OrderStatus status) throws SQLException;
}