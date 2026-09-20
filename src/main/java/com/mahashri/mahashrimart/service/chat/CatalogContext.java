package com.mahashri.mahashrimart.service.chat;

import com.mahashri.mahashrimart.model.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Plain text snapshot of the product catalogue that is handed to a {@link ChatProvider}.
 * Format: one CATEGORIES line, then one PRODUCT line per product (fields separated by a pipe).
 */
public final class CatalogContext {

    /** One product line in the catalogue text. */
    public record Item(String name, String category, String price, int stock) { }

    private static final String CATEGORIES_PREFIX = "CATEGORIES:";
    private static final String PRODUCT_PREFIX = "PRODUCT:";

    private final List<String> categories;
    private final List<Item> items;

    private CatalogContext(List<String> categories, List<Item> items) {
        this.categories = categories;
        this.items = items;
    }

    /**
     * Builds the catalogue text from categories and products.
     *
     * @param categories all category names
     * @param products   all products
     * @return catalogue text
     */
    public static String build(List<String> categories, List<Product> products) {
        StringBuilder sb = new StringBuilder(CATEGORIES_PREFIX);
        List<String> cleanCategories = new ArrayList<>();
        for (String category : categories) {
            cleanCategories.add(clean(category));
        }
        sb.append(String.join("|", cleanCategories)).append('\n');
        for (Product p : products) {
            String price = p.getPrice() == null ? "" : p.getPrice().toPlainString();
            sb.append(PRODUCT_PREFIX)
              .append(clean(p.getName())).append('|')
              .append(clean(p.getCategory())).append('|')
              .append(price).append('|')
              .append(p.getStockQty()).append('\n');
        }
        return sb.toString();
    }

    /**
     * Reads catalogue text created by {@link #build(List, List)}.
     *
     * @param text catalogue text, may be null or empty
     * @return the parsed catalogue
     */
    public static CatalogContext parse(String text) {
        List<String> categories = new ArrayList<>();
        List<Item> items = new ArrayList<>();
        if (text != null) {
            for (String line : text.split("\n")) {
                if (line.startsWith(CATEGORIES_PREFIX)) {
                    for (String c : line.substring(CATEGORIES_PREFIX.length()).split("\\|")) {
                        if (!c.isBlank()) {
                            categories.add(c.trim());
                        }
                    }
                } else if (line.startsWith(PRODUCT_PREFIX)) {
                    String[] parts = line.substring(PRODUCT_PREFIX.length()).split("\\|", -1);
                    if (parts.length >= 4) {
                        items.add(new Item(parts[0], parts[1], parts[2], toInt(parts[3])));
                    }
                }
            }
        }
        return new CatalogContext(categories, items);
    }

    /** @return category names */
    public List<String> categories() {
        return categories;
    }

    /** @return all products */
    public List<Item> items() {
        return items;
    }

    /**
     * Finds the products of one category.
     *
     * @param category category name
     * @return products in that category
     */
    public List<Item> itemsIn(String category) {
        List<Item> result = new ArrayList<>();
        for (Item item : items) {
            if (item.category().toLowerCase(Locale.ROOT).equals(category.toLowerCase(Locale.ROOT))) {
                result.add(item);
            }
        }
        return result;
    }

    private static String clean(String value) {
        return value == null ? "" : value.replaceAll("[|\\r\\n]+", " ").trim();
    }

    private static int toInt(String value) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ex) {
            return 0;
        }
    }
}