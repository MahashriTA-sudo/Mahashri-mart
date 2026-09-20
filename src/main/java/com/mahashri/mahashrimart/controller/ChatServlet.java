package com.mahashri.mahashrimart.controller;

import com.google.gson.Gson;
import com.mahashri.mahashrimart.model.Product;
import com.mahashri.mahashrimart.util.ServletUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@WebServlet("/api/v1/chat")
public class ChatServlet extends ServletUtil {
    private static final Gson GSON = new Gson();
    private static final int MAX_MESSAGE_LENGTH = 200;
    private static final int MAX_RESULTS = 5;

    private static final String HELP_REPLY =
            "I can help you with:\n"
            + "- Finding products (for example: show toys, or pen price)\n"
            + "- Categories (ask: categories)\n"
            + "- How to checkout, how to use the cart, how to see orders\n"
            + "- How to register, log in, or sell products";

    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
            "show", "me", "find", "search", "for", "i", "want", "to", "buy", "a", "an", "the",
            "some", "do", "you", "have", "any", "price", "of", "is", "are", "there", "looking",
            "need", "get", "please", "what", "about", "can", "in", "on", "with", "my", "us",
            "tell", "list", "all", "available", "stock", "products", "product", "items", "item"));

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String reply;
        try {
            reply = buildReply(request, request.getParameter("message"));
        } catch (Exception ex) {
            reply = "Sorry, I could not check that right now. Please try again.";
        }
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(GSON.toJson(Map.of("reply", reply)));
    }

    private String buildReply(HttpServletRequest request, String rawMessage) throws Exception {
        if (rawMessage == null || rawMessage.isBlank()) {
            return "Please type your question. For example: show toys, or how to checkout.";
        }
        String text = rawMessage.trim();
        if (text.length() > MAX_MESSAGE_LENGTH) {
            text = text.substring(0, MAX_MESSAGE_LENGTH);
        }
        String msg = text.toLowerCase(Locale.ROOT);
        List<String> words = tokens(msg);
        Set<String> wordSet = new HashSet<>(words);

        if (words.size() <= 3 && hasAny(wordSet, "hi", "hello", "hey", "vanakkam")) {
            return "Hello! Welcome to MahashriMart. Ask me about products, categories, checkout or orders.";
        }
        if (hasAny(wordSet, "thanks", "thank", "bye")) {
            return "You are welcome! Happy shopping.";
        }
        if (hasAny(wordSet, "help") || msg.contains("what can you do")) {
            return HELP_REPLY;
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

        List<String> categories = services(request).products().listCategories();
        if (hasAny(wordSet, "category", "categories")) {
            return "Our categories: " + String.join(", ", categories)
                    + ".\nType a category name, for example: show toys.";
        }

        String padded = " " + String.join(" ", words) + " ";
        for (String category : categories) {
            String key = " " + String.join(" ", tokens(category.toLowerCase(Locale.ROOT))) + " ";
            if (padded.contains(key)) {
                List<Product> found = services(request).products().search(null, category);
                return formatProducts("Here are products in " + category + ":", found,
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

        Map<Long, Product> results = new LinkedHashMap<>();
        addAll(results, services(request).products().search(String.join(" ", keywords), null));
        if (results.isEmpty()) {
            for (String k : keywords) {
                if (k.length() < 3) {
                    continue;
                }
                addAll(results, services(request).products().search(k, null));
                if (k.endsWith("s") && k.length() > 3) {
                    addAll(results, services(request).products().search(k.substring(0, k.length() - 1), null));
                }
            }
        }
        if (results.isEmpty()) {
            return "Sorry, I could not find products for \"" + String.join(" ", keywords)
                    + "\". Try another word, or ask me for categories.";
        }
        return formatProducts("I found these products:", new ArrayList<>(results.values()), "");
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

    private static void addAll(Map<Long, Product> target, List<Product> products) {
        if (products == null) {
            return;
        }
        for (Product p : products) {
            target.putIfAbsent(p.getId(), p);
        }
    }

    private static String formatProducts(String heading, List<Product> products, String emptyMessage) {
        if (products == null || products.isEmpty()) {
            return emptyMessage;
        }
        StringBuilder sb = new StringBuilder(heading);
        int shown = Math.min(products.size(), MAX_RESULTS);
        for (int i = 0; i < shown; i++) {
            Product p = products.get(i);
            sb.append("\n- ").append(p.getName());
            if (p.getPrice() != null) {
                sb.append(" - Rs. ").append(p.getPrice().toPlainString());
            }
            sb.append(p.getStockQty() > 0 ? " (in stock)" : " (out of stock)");
        }
        if (products.size() > shown) {
            sb.append("\nand ").append(products.size() - shown)
              .append(" more. Use the search box on the Marketplace page to see all.");
        }
        return sb.toString();
    }
}