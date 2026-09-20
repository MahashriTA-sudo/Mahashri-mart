package com.mahashri.mahashrimart.service.chat;

import com.mahashri.mahashrimart.model.Product;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CatalogContextTest {

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
    void buildThenParseKeepsCategoriesAndProducts() {
        String text = CatalogContext.build(
                List.of("Toys", "Stationery"),
                List.of(product(1, "Doll Toy", "Toys", "449.00", 5)));
        CatalogContext parsed = CatalogContext.parse(text);
        assertEquals(List.of("Toys", "Stationery"), parsed.categories());
        assertEquals(1, parsed.items().size());
        assertEquals("Doll Toy", parsed.items().get(0).name());
        assertEquals("449.00", parsed.items().get(0).price());
        assertEquals(5, parsed.items().get(0).stock());
    }

    @Test
    void pipeCharacterInProductNameIsCleaned() {
        String text = CatalogContext.build(
                List.of("Toys"),
                List.of(product(1, "Car|Truck", "Toys", "10.00", 1)));
        assertEquals("Car Truck", CatalogContext.parse(text).items().get(0).name());
    }

    @Test
    void nullTextGivesEmptyCatalogue() {
        CatalogContext parsed = CatalogContext.parse(null);
        assertTrue(parsed.items().isEmpty());
        assertTrue(parsed.categories().isEmpty());
    }
}