package com.mahashri.mahashrimart.model;

import java.math.BigDecimal;
import java.util.List;

/**
 * Sales numbers for one seller's dashboard.
 * Revenue counts only the seller's own items, and only in orders that are not cancelled.
 */
public class SellerSalesSummary {
    private final int totalOrders;
    private final int activeOrders;
    private final int deliveredOrders;
    private final int cancelledOrders;
    private final BigDecimal totalRevenue;

    private SellerSalesSummary(int totalOrders, int activeOrders, int deliveredOrders,
                               int cancelledOrders, BigDecimal totalRevenue) {
        this.totalOrders = totalOrders;
        this.activeOrders = activeOrders;
        this.deliveredOrders = deliveredOrders;
        this.cancelledOrders = cancelledOrders;
        this.totalRevenue = totalRevenue;
    }

    /**
     * Builds the summary from the orders of one seller
     * (the orders list must contain only that seller's items).
     */
    public static SellerSalesSummary from(List<Order> orders) {
        int total = 0;
        int active = 0;
        int delivered = 0;
        int cancelled = 0;
        BigDecimal revenue = BigDecimal.ZERO;

        for (Order order : orders) {
            total++;
            if (order.getStatus() == OrderStatus.CANCELLED) {
                cancelled++;
                continue;
            }
            if (order.getStatus() == OrderStatus.DELIVERED) {
                delivered++;
            } else {
                active++;
            }
            for (OrderItem item : order.getItems()) {
                revenue = revenue.add(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            }
        }
        return new SellerSalesSummary(total, active, delivered, cancelled, revenue);
    }

    public int getTotalOrders() { return totalOrders; }
    public int getActiveOrders() { return activeOrders; }
    public int getDeliveredOrders() { return deliveredOrders; }
    public int getCancelledOrders() { return cancelledOrders; }
    public BigDecimal getTotalRevenue() { return totalRevenue; }
}