package com.mahashri.mahashrimart.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SellerSalesSummaryTest {

    private Order order(OrderStatus status, String unitPrice, int quantity) {
        OrderItem item = new OrderItem();
        item.setUnitPrice(new BigDecimal(unitPrice));
        item.setQuantity(quantity);
        Order order = new Order();
        order.setStatus(status);
        List<OrderItem> items = new ArrayList<>();
        items.add(item);
        order.setItems(items);
        return order;
    }

    @Test
    void emptyList_givesAllZeros() {
        SellerSalesSummary summary = SellerSalesSummary.from(new ArrayList<>());
        assertEquals(0, summary.getTotalOrders());
        assertEquals(0, summary.getActiveOrders());
        assertEquals(0, summary.getDeliveredOrders());
        assertEquals(0, summary.getCancelledOrders());
        assertEquals(0, BigDecimal.ZERO.compareTo(summary.getTotalRevenue()));
    }

    @Test
    void countsOrdersByStatus() {
        List<Order> orders = List.of(
                order(OrderStatus.CONFIRMED, "100.00", 1),
                order(OrderStatus.SHIPPED, "100.00", 1),
                order(OrderStatus.DELIVERED, "100.00", 1),
                order(OrderStatus.CANCELLED, "100.00", 1));
        SellerSalesSummary summary = SellerSalesSummary.from(orders);
        assertEquals(4, summary.getTotalOrders());
        assertEquals(2, summary.getActiveOrders());
        assertEquals(1, summary.getDeliveredOrders());
        assertEquals(1, summary.getCancelledOrders());
    }

    @Test
    void revenue_multipliesPriceByQuantity() {
        SellerSalesSummary summary = SellerSalesSummary.from(
                List.of(order(OrderStatus.DELIVERED, "250.50", 3)));
        assertEquals(0, new BigDecimal("751.50").compareTo(summary.getTotalRevenue()));
    }

    @Test
    void revenue_ignoresCancelledOrders() {
        SellerSalesSummary summary = SellerSalesSummary.from(List.of(
                order(OrderStatus.DELIVERED, "100.00", 2),
                order(OrderStatus.CANCELLED, "999.00", 5)));
        assertEquals(0, new BigDecimal("200.00").compareTo(summary.getTotalRevenue()));
    }

    @Test
    void revenue_addsUpManyOrders() {
        SellerSalesSummary summary = SellerSalesSummary.from(List.of(
                order(OrderStatus.CONFIRMED, "10.00", 1),
                order(OrderStatus.SHIPPED, "20.00", 2),
                order(OrderStatus.DELIVERED, "30.00", 3)));
        assertEquals(0, new BigDecimal("140.00").compareTo(summary.getTotalRevenue()));
    }

    @Test
    void cancelledOrder_isNotCountedAsActiveOrDelivered() {
        SellerSalesSummary summary = SellerSalesSummary.from(
                List.of(order(OrderStatus.CANCELLED, "50.00", 1)));
        assertEquals(1, summary.getTotalOrders());
        assertEquals(0, summary.getActiveOrders());
        assertEquals(0, summary.getDeliveredOrders());
        assertEquals(1, summary.getCancelledOrders());
    }
}