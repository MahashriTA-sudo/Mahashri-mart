package com.mahashri.mahashrimart.service;

import com.mahashri.mahashrimart.model.Product;
import com.mahashri.mahashrimart.service.chat.ChatProvider;
import com.mahashri.mahashrimart.service.chat.MockChatProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ChatServiceTest {

    private ProductService products;
    private long now;

    @BeforeEach
    void setUp() throws Exception {
        products = mock(ProductService.class);
        when(products.listCategories()).thenReturn(List.of("Toys", "Stationery"));
        when(products.search(isNull(), eq("Toys"))).thenReturn(List.of(
                product(1, "Doll Toy", "Toys", "449.00", 5),
                product(2, "Robot Toy", "Toys", "699.00", 3)));
        when(products.search(isNull(), eq("Stationery"))).thenReturn(List.of(
                product(3, "Gel Pen Pack", "Stationery", "99.00", 20)));
        now = 0L;
    }

    private ChatService service(ChatProvider provider) {
        return new ChatService(products, provider, () -> now);
    }

    private static Product product(long id, String name, String category, String price, int stock) {
        Product p = new Product();
        p.setId(id);
        p.setName(name);
        p.setCategory(category);
        p.setPrice(new BigDecimal(price));
        p.setStockQty(stock);
        return p;
    }

    @Test
    void blankMessageIsRejectedWithValidationError() {
        ChatService.ChatResult result = service(new MockChatProvider()).handle("s1", "   ");
        assertFalse(result.success());
        assertEquals(400, result.status());
        assertEquals("VALIDATION_ERROR", result.errorCode());
    }

    @Test
    void tooLongMessageIsRejected() {
        String longMessage = "x".repeat(ChatService.MAX_MESSAGE_LENGTH + 1);
        ChatService.ChatResult result = service(new MockChatProvider()).handle("s1", longMessage);
        assertEquals(400, result.status());
        assertEquals("VALIDATION_ERROR", result.errorCode());
    }

    @Test
    void productQuestionReturnsProductsFromCatalogue() {
        ChatService.ChatResult result = service(new MockChatProvider()).handle("s1", "show toys");
        assertTrue(result.success());
        assertEquals(200, result.status());
        assertTrue(result.reply().contains("Doll Toy"));
        assertTrue(result.reply().contains("Robot Toy"));
    }

    @Test
    void eleventhMessageInOneMinuteIsRateLimited() {
        ChatService service = service(new MockChatProvider());
        for (int i = 1; i <= ChatService.MAX_MESSAGES_PER_MINUTE; i++) {
            assertEquals(200, service.handle("s1", "hello number " + i).status());
        }
        ChatService.ChatResult blocked = service.handle("s1", "one more message");
        assertEquals(429, blocked.status());
        assertEquals("RATE_LIMITED", blocked.errorCode());
    }

    @Test
    void rateLimitResetsAfterOneMinute() {
        ChatService service = service(new MockChatProvider());
        for (int i = 1; i <= ChatService.MAX_MESSAGES_PER_MINUTE; i++) {
            service.handle("s1", "hello number " + i);
        }
        assertEquals(429, service.handle("s1", "blocked now").status());
        now += 61_000L;
        assertEquals(200, service.handle("s1", "allowed again").status());
    }

    @Test
    void rateLimitIsSeparateForEachSession() {
        ChatService service = service(new MockChatProvider());
        for (int i = 1; i <= ChatService.MAX_MESSAGES_PER_MINUTE; i++) {
            service.handle("s1", "hello number " + i);
        }
        assertEquals(429, service.handle("s1", "blocked").status());
        assertEquals(200, service.handle("s2", "hi").status());
    }

    @Test
    void providerFailureReturnsStaticFallbackReply() {
        ChatService service = service((message, context) -> {
            throw new IllegalStateException("provider down");
        });
        ChatService.ChatResult result = service.handle("s1", "show toys");
        assertTrue(result.success());
        assertEquals(200, result.status());
        assertEquals(ChatService.FALLBACK_REPLY, result.reply());
    }

    @Test
    void databaseFailureReturnsStaticFallbackReply() throws Exception {
        when(products.listCategories()).thenThrow(new RuntimeException("db down"));
        ChatService.ChatResult result = service(new MockChatProvider()).handle("s1", "show toys");
        assertTrue(result.success());
        assertEquals(ChatService.FALLBACK_REPLY, result.reply());
    }

    @Test
    void repeatedQuestionInSameSessionIsServedFromCache() {
        AtomicInteger calls = new AtomicInteger();
        ChatService service = service((message, context) -> {
            calls.incrementAndGet();
            return "cached reply";
        });
        service.handle("s1", "Show Toys");
        ChatService.ChatResult second = service.handle("s1", "  show   toys ");
        assertEquals("cached reply", second.reply());
        assertEquals(1, calls.get());
    }
}