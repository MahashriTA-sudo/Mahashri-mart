package com.mahashri.mahashrimart.service;

import com.mahashri.mahashrimart.dao.CartDao;
import com.mahashri.mahashrimart.dao.OrderDao;
import com.mahashri.mahashrimart.dao.ProductDao;
import com.mahashri.mahashrimart.exception.InsufficientStockException;
import com.mahashri.mahashrimart.exception.ValidationException;
import com.mahashri.mahashrimart.model.CartItem;
import com.mahashri.mahashrimart.model.Order;
import com.mahashri.mahashrimart.model.OrderStatus;
import com.mahashri.mahashrimart.model.Product;
import com.mahashri.mahashrimart.util.TransactionManager;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class OrderService {
    private final DataSource dataSource;
    private final CartDao cartDao;
    private final ProductDao productDao;
    private final OrderDao orderDao;

    public OrderService(DataSource dataSource, CartDao cartDao, ProductDao productDao, OrderDao orderDao) {
        this.dataSource = dataSource;
        this.cartDao = cartDao;
        this.productDao = productDao;
        this.orderDao = orderDao;
    }

    public long checkout(long buyerId) throws Exception {
        return TransactionManager.inTransaction(dataSource, connection -> {
            List<CartItem> cart = cartDao.findByUserId(connection, buyerId);
            if (cart.isEmpty()) throw new ValidationException("Your cart is empty.");
            BigDecimal total = BigDecimal.ZERO;
            for (CartItem item : cart) {
                Product product = productDao.findByIdForUpdate(connection, item.getProductId()).orElse(null);
                if (product == null || product.getStockQty() < item.getQuantity()) {
                    throw new InsufficientStockException("One or more items no longer have enough stock.");
                }
                total = total.add(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            }
            Order order = new Order();
            order.setBuyerId(buyerId);
            order.setStatus(OrderStatus.CONFIRMED);
            order.setTotalAmount(total);
            long orderId = orderDao.create(connection, order);
            for (CartItem item : cart) {
                Product product = productDao.findByIdForUpdate(connection, item.getProductId())
                        .orElseThrow(() -> new SQLException("Product disappeared during checkout."));
                if (!productDao.decrementStock(connection, product.getId(), item.getQuantity())) {
                    throw new InsufficientStockException("One or more items no longer have enough stock.");
                }
                orderDao.addItem(connection, orderId, product.getId(), item.getQuantity(), product.getPrice());
            }
            cartDao.clear(connection, buyerId);
            return orderId;
        });
    }

    public List<Order> history(long buyerId) throws SQLException { return orderDao.findByBuyerId(buyerId); }
}