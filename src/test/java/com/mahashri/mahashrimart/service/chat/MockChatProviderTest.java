package com.mahashri.mahashrimart.service.chat;

import com.mahashri.mahashrimart.model.Product;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MockChatProviderTest {

    private final MockChatProvider provider = new MockChatProvider();

    private final String context = CatalogContext.build(
            List.of("Toys", "Stationery"),
            List.of(
                    product(1, "Doll Toy", "Toys", "449.00", 5),
                    product(2, "Gel Pen Pack", "Stationery", "99.00", 20),
                    product(3, "Color Pencils Set", "Stationery", "299.00", 0)));

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
    void greetingGetsWelcomeReply() {
        assertTrue(provider.getReply("hi", context).contains("Welcome"));
    }

    @Test
    void categoriesQuestionListsAllCategories() {
        String reply = provider.getReply("categories", context);
        assertTrue(reply.contains("Toys"));
        assertTrue(reply.contains("Stationery"));
    }

    @Test
    void categoryNameReturnsOnlyThatCategory() {
        String reply = provider.getReply("show toys", context);
        assertTrue(reply.contains("Doll Toy"));
        assertFalse(reply.contains("Gel Pen"));
    }

    @Test
    void penSearchFindsPenBeforePencil() {
        String reply = provider.getReply("pen price", context);
        assertTrue(reply.contains("Gel Pen Pack"));
        assertFalse(reply.contains("Color Pencils Set"));
    }

    @Test
    void outOfStockProductIsMarked() {
        assertTrue(provider.getReply("pencils", context).contains("out of stock"));
    }

    @Test
    void unknownProductGivesPoliteNotFoundReply() {
        assertTrue(provider.getReply("zzzzqq", context).contains("could not find"));
    }

    @Test
    void checkoutFaqMentionsMockPayment() {
        assertTrue(provider.getReply("how to checkout", context).contains("mock payment"));
    }

    @Test
    void deliveryFaqExplainsDemoMarketplace() {
        assertTrue(provider.getReply("delivery", context).contains("demo marketplace"));
    }

    @Test
    void productCountQuestionUsesCatalogue() {
        assertTrue(provider.getReply("how many products", context).contains("3 products in 2 categories"));
    }

    @Test
    void emptyContextStillAnswersFaqAndNeverFails() {
        assertTrue(provider.getReply("hi", "").contains("Welcome"));
        assertTrue(provider.getReply("show toys", "").contains("could not find"));
    }
}