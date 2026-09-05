package com.mahashri.mahashrimart.util;

import com.mahashri.mahashrimart.dto.ProductRequest;
import com.mahashri.mahashrimart.dto.RegistrationRequest;
import com.mahashri.mahashrimart.exception.ValidationException;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.regex.Pattern;

public final class ValidationUtil {
    private static final Pattern EMAIL = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private ValidationUtil() {}

    public static RegistrationRequest registration(String name, String email, String password, String role)
            throws ValidationException {
        String cleanName = required(name, "Name");
        String cleanEmail = required(email, "Email").toLowerCase(Locale.ROOT);
        String cleanPassword = required(password, "Password");
        if (!EMAIL.matcher(cleanEmail).matches()) throw new ValidationException("Enter a valid email address.");
        if (cleanPassword.length() < 8) throw new ValidationException("Password must be at least 8 characters.");
        if (!"BUYER".equals(role) && !"SELLER".equals(role)) throw new ValidationException("Choose a valid account type.");
        return new RegistrationRequest(cleanName, cleanEmail, cleanPassword,
                com.mahashri.mahashrimart.model.Role.valueOf(role));
    }

    public static ProductRequest product(String name, String description, String price, String stockQty,
                                         String category, String imageUrl) throws ValidationException {
        String cleanName = required(name, "Product name");
        String cleanDescription = required(description, "Description");
        String cleanCategory = required(category, "Category");
        BigDecimal parsedPrice;
        int parsedStock;
        try {
            parsedPrice = new BigDecimal(required(price, "Price"));
            parsedStock = Integer.parseInt(required(stockQty, "Stock quantity"));
        } catch (NumberFormatException ex) {
            throw new ValidationException("Price and stock quantity must be valid numbers.");
        }
        if (parsedPrice.signum() < 0) throw new ValidationException("Price cannot be negative.");
        if (parsedStock < 0) throw new ValidationException("Stock quantity cannot be negative.");
        return new ProductRequest(cleanName, cleanDescription, parsedPrice, parsedStock, cleanCategory, trim(imageUrl));
    }

    public static int positiveQuantity(String value) throws ValidationException {
        try {
            int quantity = Integer.parseInt(required(value, "Quantity"));
            if (quantity < 1) throw new NumberFormatException();
            return quantity;
        } catch (NumberFormatException ex) {
            throw new ValidationException("Quantity must be a positive whole number.");
        }
    }

    public static String required(String value, String label) throws ValidationException {
        String cleaned = trim(value);
        if (cleaned.isBlank()) throw new ValidationException(label + " is required.");
        return cleaned;
    }

    public static String trim(String value) {
        return value == null ? "" : value.trim();
    }
}