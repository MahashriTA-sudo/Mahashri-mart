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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class OrderServiceTest {
    private static final long BUYER_ID = 1L;
    private static final long SELLER_ID = 20L;
    private static final long ORDER_ID = 5L;
    private static final long PRODUCT_ID = 10L;

    private DataSource dataSource;
    private Connection connection;
    private CartDao cartDao;
    private ProductDao productDao;
    private OrderDao orderDao;
    private OrderService orderService;

    @BeforeEach
    void setUp() throws Exception {
        dataSource = mock(DataSource.class);
        connection = mock(Connection.class);
        cartDao = mock(CartDao.class);
        productDao = mock(ProductDao.class);
        orderDao = mock(OrderDao.class);
        when(dataSource.getConnection()).thenReturn(connection);
        orderService = new OrderService(dataSource, cartDao, productDao, orderDao);
    }

    // ---------- helpers ----------

    private static CartItem cartItem(long productId, int quantity) {
        CartItem item = new CartItem();
        item.setProductId(productId);
        item.setQuantity(quantity);
        return item;
    }

    private static Product product(long id, String price, int stock) {
        Product product = new Product();
        product.setId(id);
        product.setPrice(new BigDecimal(price));
        product.setStockQty(stock);
        return product;
    }

    private static Order order(long id, long buyerId, OrderStatus status, long productId, int quantity) {
        Order order = new Order();
        order.setId(id);
        order.setBuyerId(buyerId);
        order.setStatus(status);
        OrderItem item = new OrderItem();
        item.setProductId(productId);
        item.setQuantity(quantity);
        order.setItems(List.of(item));
        return order;
    }

    // ---------- checkout ----------

    @Test
    void checkoutFailsWhenCartIsEmpty() throws Exception {
        when(cartDao.findByUserId(connection, BUYER_ID)).thenReturn(List.of());

        assertThrows(ValidationException.class, () -> orderService.checkout(BUYER_ID));

        verify(orderDao, never()).create(any(), any());
        verify(connection).rollback();
    }

    @Test
    void checkoutCreatesOrderReducesStockAndClearsCart() throws Exception {
        when(cartDao.findByUserId(connection, BUYER_ID)).thenReturn(List.of(cartItem(PRODUCT_ID, 2)));
        Product product = product(PRODUCT_ID, "50.00", 5);
        when(productDao.findByIdForUpdate(connection, PRODUCT_ID)).thenReturn(Optional.of(product));
        when(orderDao.create(eq(connection), any(Order.class))).thenReturn(ORDER_ID);
        when(productDao.decrementStock(connection, PRODUCT_ID, 2)).thenReturn(true);

        long orderId = orderService.checkout(BUYER_ID);

        assertEquals(ORDER_ID, orderId);
        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderDao).create(eq(connection), captor.capture());
        assertEquals(OrderStatus.CONFIRMED, captor.getValue().getStatus());
        assertEquals(BUYER_ID, captor.getValue().getBuyerId());
        assertEquals(0, new BigDecimal("100.00").compareTo(captor.getValue().getTotalAmount()));
        verify(orderDao).addItem(connection, ORDER_ID, PRODUCT_ID, 2, product.getPrice());
        verify(cartDao).clear(connection, BUYER_ID);
        verify(connection).commit();
    }

    @Test
    void checkoutFailsWhenNotEnoughStock() throws Exception {
        when(cartDao.findByUserId(connection, BUYER_ID)).thenReturn(List.of(cartItem(PRODUCT_ID, 3)));
        when(productDao.findByIdForUpdate(connection, PRODUCT_ID))
                .thenReturn(Optional.of(product(PRODUCT_ID, "50.00", 1)));

        assertThrows(InsufficientStockException.class, () -> orderService.checkout(BUYER_ID));

        verify(orderDao, never()).create(any(), any());
        verify(cartDao, never()).clear(any(), anyLong());
    }

    @Test
    void checkoutFailsWhenProductNoLongerExists() throws Exception {
        when(cartDao.findByUserId(connection, BUYER_ID)).thenReturn(List.of(cartItem(PRODUCT_ID, 1)));
        when(productDao.findByIdForUpdate(connection, PRODUCT_ID)).thenReturn(Optional.empty());

        assertThrows(InsufficientStockException.class, () -> orderService.checkout(BUYER_ID));
        verify(orderDao, never()).create(any(), any());
    }

    // ---------- cancelOrder ----------

    @Test
    void buyerCanCancelConfirmedOrderAndStockIsRestored() throws Exception {
        when(orderDao.findById(ORDER_ID))
                .thenReturn(Optional.of(order(ORDER_ID, BUYER_ID, OrderStatus.CONFIRMED, PRODUCT_ID, 2)));

        orderService.cancelOrder(ORDER_ID, BUYER_ID);

        verify(productDao).incrementStock(connection, PRODUCT_ID, 2);
        verify(orderDao).updateStatus(connection, ORDER_ID, OrderStatus.CANCELLED);
    }

    @Test
    void buyerCanCancelPendingOrder() throws Exception {
        when(orderDao.findById(ORDER_ID))
                .thenReturn(Optional.of(order(ORDER_ID, BUYER_ID, OrderStatus.PENDING, PRODUCT_ID, 1)));

        orderService.cancelOrder(ORDER_ID, BUYER_ID);

        verify(orderDao).updateStatus(connection, ORDER_ID, OrderStatus.CANCELLED);
    }

    @Test
    void buyerCannotCancelAnotherBuyersOrder() throws Exception {
        when(orderDao.findById(ORDER_ID))
                .thenReturn(Optional.of(order(ORDER_ID, 999L, OrderStatus.CONFIRMED, PRODUCT_ID, 2)));

        assertThrows(ValidationException.class, () -> orderService.cancelOrder(ORDER_ID, BUYER_ID));

        verify(productDao, never()).incrementStock(any(), anyLong(), anyInt());
        verify(orderDao, never()).updateStatus(any(), anyLong(), any());
    }

    @Test
    void buyerCannotCancelShippedOrder() throws Exception {
        when(orderDao.findById(ORDER_ID))
                .thenReturn(Optional.of(order(ORDER_ID, BUYER_ID, OrderStatus.SHIPPED, PRODUCT_ID, 2)));

        assertThrows(ValidationException.class, () -> orderService.cancelOrder(ORDER_ID, BUYER_ID));

        verify(productDao, never()).incrementStock(any(), anyLong(), anyInt());
        verify(orderDao, never()).updateStatus(any(), anyLong(), any());
    }

    @Test
    void cancelFailsWhenOrderDoesNotExist() throws Exception {
        when(orderDao.findById(ORDER_ID)).thenReturn(Optional.empty());

        assertThrows(ValidationException.class, () -> orderService.cancelOrder(ORDER_ID, BUYER_ID));
        verify(orderDao, never()).updateStatus(any(), anyLong(), any());
    }

    // ---------- advanceOrderStatus (seller) ----------

    @Test
    void sellerCanMarkConfirmedOrderAsShipped() throws Exception {
        Order order = order(ORDER_ID, BUYER_ID, OrderStatus.CONFIRMED, PRODUCT_ID, 1);
        when(orderDao.findById(ORDER_ID)).thenReturn(Optional.of(order));
        when(orderDao.findBySellerId(SELLER_ID)).thenReturn(List.of(order));

        orderService.advanceOrderStatus(ORDER_ID, SELLER_ID, OrderStatus.SHIPPED);

        verify(orderDao).updateStatus(connection, ORDER_ID, OrderStatus.SHIPPED);
    }

    @Test
    void sellerCanMarkShippedOrderAsDelivered() throws Exception {
        Order order = order(ORDER_ID, BUYER_ID, OrderStatus.SHIPPED, PRODUCT_ID, 1);
        when(orderDao.findById(ORDER_ID)).thenReturn(Optional.of(order));
        when(orderDao.findBySellerId(SELLER_ID)).thenReturn(List.of(order));

        orderService.advanceOrderStatus(ORDER_ID, SELLER_ID, OrderStatus.DELIVERED);

        verify(orderDao).updateStatus(connection, ORDER_ID, OrderStatus.DELIVERED);
    }

    @Test
    void sellerCannotShipAnOrderThatIsNotConfirmed() throws Exception {
        Order order = order(ORDER_ID, BUYER_ID, OrderStatus.PENDING, PRODUCT_ID, 1);
        when(orderDao.findById(ORDER_ID)).thenReturn(Optional.of(order));
        when(orderDao.findBySellerId(SELLER_ID)).thenReturn(List.of(order));

        assertThrows(ValidationException.class,
                () -> orderService.advanceOrderStatus(ORDER_ID, SELLER_ID, OrderStatus.SHIPPED));
        verify(orderDao, never()).updateStatus(any(), anyLong(), any());
    }

    @Test
    void sellerCannotDeliverAnOrderThatIsNotShipped() throws Exception {
        Order order = order(ORDER_ID, BUYER_ID, OrderStatus.CONFIRMED, PRODUCT_ID, 1);
        when(orderDao.findById(ORDER_ID)).thenReturn(Optional.of(order));
        when(orderDao.findBySellerId(SELLER_ID)).thenReturn(List.of(order));

        assertThrows(ValidationException.class,
                () -> orderService.advanceOrderStatus(ORDER_ID, SELLER_ID, OrderStatus.DELIVERED));
        verify(orderDao, never()).updateStatus(any(), anyLong(), any());
    }

    @Test
    void sellerCannotCancelAnOrder() throws Exception {
        Order order = order(ORDER_ID, BUYER_ID, OrderStatus.CONFIRMED, PRODUCT_ID, 1);
        when(orderDao.findById(ORDER_ID)).thenReturn(Optional.of(order));
        when(orderDao.findBySellerId(SELLER_ID)).thenReturn(List.of(order));

        assertThrows(ValidationException.class,
                () -> orderService.advanceOrderStatus(ORDER_ID, SELLER_ID, OrderStatus.CANCELLED));
        verify(orderDao, never()).updateStatus(any(), anyLong(), any());
    }

    @Test
    void sellerCannotUpdateAnOrderWithNoneOfTheirProducts() throws Exception {
        Order order = order(ORDER_ID, BUYER_ID, OrderStatus.CONFIRMED, PRODUCT_ID, 1);
        when(orderDao.findById(ORDER_ID)).thenReturn(Optional.of(order));
        when(orderDao.findBySellerId(SELLER_ID)).thenReturn(List.of());

        assertThrows(ValidationException.class,
                () -> orderService.advanceOrderStatus(ORDER_ID, SELLER_ID, OrderStatus.SHIPPED));
        verify(orderDao, never()).updateStatus(any(), anyLong(), any());
    }

    @Test
    void advanceFailsWhenOrderDoesNotExist() throws Exception {
        when(orderDao.findById(ORDER_ID)).thenReturn(Optional.empty());

        assertThrows(ValidationException.class,
                () -> orderService.advanceOrderStatus(ORDER_ID, SELLER_ID, OrderStatus.SHIPPED));
    }

    // ---------- adminUpdateStatus ----------

    @Test
    void adminCancellationRestoresStock() throws Exception {
        when(orderDao.findById(ORDER_ID))
                .thenReturn(Optional.of(order(ORDER_ID, BUYER_ID, OrderStatus.SHIPPED, PRODUCT_ID, 3)));

        orderService.adminUpdateStatus(ORDER_ID, OrderStatus.CANCELLED);

        verify(productDao).incrementStock(connection, PRODUCT_ID, 3);
        verify(orderDao).updateStatus(connection, ORDER_ID, OrderStatus.CANCELLED);
    }

    @Test
    void adminCannotCancelADeliveredOrder() throws Exception {
        when(orderDao.findById(ORDER_ID))
                .thenReturn(Optional.of(order(ORDER_ID, BUYER_ID, OrderStatus.DELIVERED, PRODUCT_ID, 3)));

        assertThrows(ValidationException.class,
                () -> orderService.adminUpdateStatus(ORDER_ID, OrderStatus.CANCELLED));

        verify(productDao, never()).incrementStock(any(), anyLong(), anyInt());
        verify(orderDao, never()).updateStatus(any(), anyLong(), any());
    }

    @Test
    void adminCancellingAnAlreadyCancelledOrderDoesNotRestoreStockTwice() throws Exception {
        when(orderDao.findById(ORDER_ID))
                .thenReturn(Optional.of(order(ORDER_ID, BUYER_ID, OrderStatus.CANCELLED, PRODUCT_ID, 3)));

        orderService.adminUpdateStatus(ORDER_ID, OrderStatus.CANCELLED);

        verify(productDao, never()).incrementStock(any(), anyLong(), anyInt());
    }

    @Test
    void adminCanSetAnyOtherStatusWithoutTouchingStock() throws Exception {
        when(orderDao.findById(ORDER_ID))
                .thenReturn(Optional.of(order(ORDER_ID, BUYER_ID, OrderStatus.CONFIRMED, PRODUCT_ID, 3)));

        orderService.adminUpdateStatus(ORDER_ID, OrderStatus.DELIVERED);

        verify(productDao, never()).incrementStock(any(), anyLong(), anyInt());
        verify(orderDao).updateStatus(connection, ORDER_ID, OrderStatus.DELIVERED);
    }

    @Test
    void adminUpdateFailsWhenOrderDoesNotExist() throws Exception {
        when(orderDao.findById(ORDER_ID)).thenReturn(Optional.empty());

        assertThrows(ValidationException.class,
                () -> orderService.adminUpdateStatus(ORDER_ID, OrderStatus.SHIPPED));
    }

    // ---------- listing ----------

    @Test
    void listingMethodsDelegateToTheDao() throws Exception {
        List<Order> orders = List.of(new Order());
        when(orderDao.findByBuyerId(BUYER_ID)).thenReturn(orders);
        when(orderDao.findAll()).thenReturn(orders);
        when(orderDao.findAllByStatus(OrderStatus.SHIPPED)).thenReturn(orders);
        when(orderDao.findBySellerId(SELLER_ID)).thenReturn(orders);

        assertSame(orders, orderService.history(BUYER_ID));
        assertSame(orders, orderService.listAll());
        assertSame(orders, orderService.listAllByStatus(OrderStatus.SHIPPED));
        assertSame(orders, orderService.listForSeller(SELLER_ID));
    }
}