package com.mahashri.mahashrimart.service;

import com.mahashri.mahashrimart.dao.CartDao;
import com.mahashri.mahashrimart.dao.OrderDao;
import com.mahashri.mahashrimart.dao.ProductDao;
import com.mahashri.mahashrimart.exception.InsufficientStockException;
import com.mahashri.mahashrimart.exception.ValidationException;
import com.mahashri.mahashrimart.model.CartItem;
import com.mahashri.mahashrimart.model.Order;
import com.mahashri.mahashrimart.model.OrderItem;
import com.mahashri.mahashrimart.model.OrderStatus;
import com.mahashri.mahashrimart.model.Product;
import com.mahashri.mahashrimart.util.TransactionManager;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

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

    /** Place a new order from the buyer's cart (transactional). */
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

    /**
     * Buyer cancels their own order.
     * Allowed only when status is PENDING or CONFIRMED.
     * Stock is restored for each item.
     */
    public void cancelOrder(long orderId, long buyerId) throws Exception {
        TransactionManager.inTransaction(dataSource, connection -> {
            Optional<Order> opt = orderDao.findById(orderId);
            if (opt.isEmpty()) throw new ValidationException("Order not found.");
            Order order = opt.get();
            if (order.getBuyerId() != buyerId) throw new ValidationException("You do not own this order.");
            if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.CONFIRMED) {
                throw new ValidationException("Only PENDING or CONFIRMED orders can be cancelled.");
            }
            // restore stock for every item
            for (OrderItem item : order.getItems()) {
                productDao.incrementStock(connection, item.getProductId(), item.getQuantity());
            }
            orderDao.updateStatus(connection, orderId, OrderStatus.CANCELLED);
            return null;
        });
    }

    /**
     * Seller advances an order they have a product in.
     * Allowed transitions: CONFIRMED → SHIPPED → DELIVERED.
     */
    public void advanceOrderStatus(long orderId, long sellerId, OrderStatus newStatus) throws Exception {
        TransactionManager.inTransaction(dataSource, connection -> {
            Optional<Order> opt = orderDao.findById(orderId);
            if (opt.isEmpty()) throw new ValidationException("Order not found.");
            Order order = opt.get();

            // Verify seller owns at least one item in this order
            List<Order> sellerOrders = orderDao.findBySellerId(sellerId);
            boolean hasClaim = sellerOrders.stream().anyMatch(o -> o.getId() == orderId);
            if (!hasClaim) throw new ValidationException("You do not have products in this order.");

            OrderStatus current = order.getStatus();
            if (newStatus == OrderStatus.SHIPPED && current != OrderStatus.CONFIRMED) {
                throw new ValidationException("Order must be CONFIRMED before marking SHIPPED.");
            }
            if (newStatus == OrderStatus.DELIVERED && current != OrderStatus.SHIPPED) {
                throw new ValidationException("Order must be SHIPPED before marking DELIVERED.");
            }
            if (newStatus != OrderStatus.SHIPPED && newStatus != OrderStatus.DELIVERED) {
                throw new ValidationException("Sellers can only mark orders as SHIPPED or DELIVERED.");
            }
            orderDao.updateStatus(connection, orderId, newStatus);
            return null;
        });
    }

    /**
     * Admin can set any order to any status.
     * If forcing a cancellation, stock is restored.
     */
    public void adminUpdateStatus(long orderId, OrderStatus newStatus) throws Exception {
        TransactionManager.inTransaction(dataSource, connection -> {
            Optional<Order> opt = orderDao.findById(orderId);
            if (opt.isEmpty()) throw new ValidationException("Order not found.");
            Order order = opt.get();
            if (newStatus == OrderStatus.CANCELLED && order.getStatus() != OrderStatus.CANCELLED) {
                // restore stock on admin cancellation
                for (OrderItem item : order.getItems()) {
                    productDao.incrementStock(connection, item.getProductId(), item.getQuantity());
                }
            }
            orderDao.updateStatus(connection, orderId, newStatus);
            return null;
        });
    }

    public List<Order> history(long buyerId) throws SQLException { return orderDao.findByBuyerId(buyerId); }

    public List<Order> listAll() throws SQLException { return orderDao.findAll(); }

    public List<Order> listAllByStatus(OrderStatus status) throws SQLException { return orderDao.findAllByStatus(status); }

    public List<Order> listForSeller(long sellerId) throws SQLException { return orderDao.findBySellerId(sellerId); }
}