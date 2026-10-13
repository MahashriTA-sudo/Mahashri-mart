package com.mahashri.mahashrimart.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Order {
    private long id;
    private long buyerId;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<OrderItem> items = new ArrayList<>();

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getBuyerId() { return buyerId; }
    public void setBuyerId(long buyerId) { this.buyerId = buyerId; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }

    // Shows the date like "4 Oct 2026, 8:17 PM" in Indian time.
    // The stored time follows the computer's own timezone (IST on a laptop, UTC on Render),
    // so we read it in that timezone first and then convert to Indian time.
    public String getCreatedAtDisplay() {
        if (createdAt == null) {
            return "";
        }
        return createdAt
                .atZone(ZoneId.systemDefault())
                .withZoneSameInstant(ZoneId.of("Asia/Kolkata"))
                .format(DateTimeFormatter.ofPattern("d MMM yyyy, h:mm a", Locale.ENGLISH));
    }

    // 0 = not started, 1 = Confirmed, 2 = Shipped, 3 = Delivered
    public int getProgressStep() {
        if (status == null) {
            return 0;
        }
        switch (status) {
            case CONFIRMED: return 1;
            case SHIPPED:   return 2;
            case DELIVERED: return 3;
            default:        return 0;
        }
    }
}