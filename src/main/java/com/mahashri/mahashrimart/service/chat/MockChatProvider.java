package com.mahashri.mahashrimart.service.chat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Rule based chatbot provider. It answers FAQ style questions about the marketplace and
 * looks up products in the catalogue text. It needs no network and no API key.
 */
public class MockChatProvider implements ChatProvider {

    private static final int MAX_RESULTS = 5;

    private static final String HELP_REPLY =
            "I can help you with:\n"
            + "- Finding products (for example: show toys, or pencil price)\n"
            + "- Categories (ask: categories)\n"
            + "- How to checkout, how to use the cart, how to see orders\n"
            + "- How to register, log in, or sell products";

    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
            "show", "me", "find", "search", "for", "i", "want", "to", "buy", "a", "an", "the",
            "some", "do", "you", "have", "any", "price", "of", "is", "are", "there", "looking",
            "need", "get", "please", "what", "about", "can", "in", "on", "with", "my", "us",
            "tell", "list", "all", "available", "stock", "products", "product", "items", "item",
            "how", "many", "does", "and", "or", "it"));

    @Override
    public String getReply(String userMessage, String context) {
        String msg = userMessage == null ? "" : userMessage.trim().toLowerCase(Locale.ROOT);
        List<String> words = tokens(msg);
        Set<String> wordSet = new HashSet<>(words);
        CatalogContext catalog = CatalogContext.parse(context);

        String faq = answerFaq(msg, words, wordSet, catalog);
        if (faq != null) {
            return faq;
        }
        return answerProductQuestion(words, catalog);
    }

    private String answerFaq(String msg, List<String> words, Set<String> wordSet, CatalogContext catalog) {
        if (words.size() <= 3 && hasAny(wordSet, "hi", "hello", "hey", "vanakkam")) {
            return "Hello! Welcome to MahashriMart. Ask me about products, categories, checkout or orders.";
        }
        if (hasAny(wordSet, "thanks", "thank", "bye")) {
            return "You are welcome! Happy shopping.";
        }
        if (hasAny(wordSet, "help") || msg.contains("what can you do")) {
            return HELP_REPLY;
        }
        if (msg.contains("how many") && hasAny(wordSet, "product", "products", "item", "items", "categories")) {
            if (catalog.items().isEmpty()) {
                return "I could not read the product list right now. Please try again.";
            }
            return "MahashriMart has " + catalog.items().size() + " products in "
                    + catalog.categories().size() + " categories.";
        }
        if (hasAny(wordSet, "seller", "sellers", "selling") || msg.contains("how to sell")
                || msg.contains("want to sell")) {
            return "To sell, click Join and register a seller account, then log in. "
                    + "Use Sell a product to add items, My listings to edit or delete them, "
                    + "and Incoming orders to see orders for your products.";
        }
        if (hasAny(wordSet, "checkout", "payment", "pay") || msg.contains("how to buy")
                || msg.contains("how to order") || msg.contains("place order")
                || msg.contains("place an order")) {
            return "To buy: log in as a buyer, add products to your cart, open Cart and click Checkout. "
                    + "The payment is a mock payment, so no real money is used.";
        }
        if (hasAny(wordSet, "cart")) {
            return "Add products to your cart from the product page, then open Cart in the top menu. "
                    + "The cart is available for buyer accounts.";
        }
        if (hasAny(wordSet, "delivery", "shipping", "shipped", "deliver")) {
            return "MahashriMart is a demo marketplace, so nothing is shipped in real life. "
                    + "After checkout your order is marked CONFIRMED and you can see it under Orders.";
        }
        if (hasAny(wordSet, "return", "returns", "refund", "refunds")) {
            return "Returns and refunds are not part of this demo version. "
                    + "The payment is a mock payment, so no real money is charged.";
        }
        if (hasAny(wordSet, "orders", "order", "track", "tracking")) {
            return "Log in as a buyer and click Orders in the top menu to see your order history.";
        }
        if (hasAny(wordSet, "login", "register", "signup", "join", "account", "password")
                || msg.contains("log in") || msg.contains("sign up")) {
            return "Click Join to create an account, or Log in if you already have one.";
        }
        if (hasAny(wordSet, "review", "reviews", "rating", "ratings", "rate")) {
            return "Open any product page to read its reviews and ratings.";
        }
        if (hasAny(wordSet, "category", "categories")) {
            if (catalog.categories().isEmpty()) {
                return "I could not read the categories right now. Please try again.";
            }
            return "Our categories: " + String.join(", ", catalog.categories())
                    + ".\nType a category name, for example: show toys.";
        }
        return null;
    }

    private String answerProductQuestion(List<String> words, CatalogContext catalog) {
        String padded = " " + String.join(" ", words) + " ";
        for (String category : catalog.categories()) {
            String key = " " + String.join(" ", tokens(category.toLowerCase(Locale.ROOT))) + " ";
            if (padded.contains(key)) {
                return formatProducts("Here are products in " + category + ":", catalog.itemsIn(category),
                        "No products are listed in " + category + " right now.");
            }
        }

        List<String> keywords = new ArrayList<>();
        for (String w : words) {
            if (w.length() >= 2 && !STOP_WORDS.contains(w)) {
                keywords.add(w);
            }
        }
        if (keywords.isEmpty()) {
            return HELP_REPLY;
        }

        List<CatalogContext.Item> found = match(catalog.items(), keywords, true);
        String heading = "I found these products:";
        if (found.isEmpty()) {
            found = match(catalog.items(), keywords, false);
            heading = "I found similar products:";
        }
        if (found.isEmpty()) {
            return "Sorry, I could not find products for \"" + String.join(" ", keywords)
                    + "\". I can help only with MahashriMart products, orders and checkout. "
                    + "Try asking for categories.";
        }
        return formatProducts(heading, found, "");
    }

    private static List<CatalogContext.Item> match(List<CatalogContext.Item> items, List<String> keywords,
                                                   boolean wholeWord) {
        int[] scores = new int[items.size()];
        int best = 0;
        for (int i = 0; i < items.size(); i++) {
            CatalogContext.Item item = items.get(i);
            String name = item.name().toLowerCase(Locale.ROOT);
            String category = item.category().toLowerCase(Locale.ROOT);
            List<String> nameWords = tokens(name);
            int score = 0;
            for (String k : keywords) {
                boolean hit;
                if (wholeWord) {
                    hit = wordMatch(nameWords, k);
                } else {
                    hit = k.length() >= 3 && (name.contains(k) || category.contains(k));
                }
                if (hit) {
                    score++;
                }
            }
            scores[i] = score;
            best = Math.max(best, score);
        }
        List<CatalogContext.Item> result = new ArrayList<>();
        for (int level = best; level >= 1; level--) {
            for (int i = 0; i < items.size(); i++) {
                if (scores[i] == level) {
                    result.add(items.get(i));
                }
            }
        }
        return result;
    }

    private static boolean wordMatch(List<String> nameWords, String keyword) {
        for (String w : nameWords) {
            if (w.equals(keyword) || w.equals(keyword + "s") || w.equals(keyword + "es")
                    || (keyword.endsWith("s") && keyword.length() > 3
                        && w.equals(keyword.substring(0, keyword.length() - 1)))) {
                return true;
            }
        }
        return false;
    }

    private static List<String> tokens(String text) {
        List<String> result = new ArrayList<>();
        for (String part : text.split("[^a-z0-9]+")) {
            if (!part.isEmpty()) {
                result.add(part);
            }
        }
        return result;
    }

    private static boolean hasAny(Set<String> wordSet, String... options) {
        for (String option : options) {
            if (wordSet.contains(option)) {
                return true;
            }
        }
        return false;
    }

    private static String formatProducts(String heading, List<CatalogContext.Item> items, String emptyMessage) {
        if (items == null || items.isEmpty()) {
            return emptyMessage;
        }
        StringBuilder sb = new StringBuilder(heading);
        int shown = Math.min(items.size(), MAX_RESULTS);
        for (int i = 0; i < shown; i++) {
            CatalogContext.Item item = items.get(i);
            sb.append("\n- ").append(item.name());
            if (!item.price().isEmpty()) {
                sb.append(" - Rs. ").append(item.price());
            }
            sb.append(item.stock() > 0 ? " (in stock)" : " (out of stock)");
        }
        if (items.size() > shown) {
            sb.append("\nand ").append(items.size() - shown)
              .append(" more. Use the search box on the Marketplace page to see all.");
        }
        return sb.toString();
    }
}