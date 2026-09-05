package com.mahashri.mahashrimart.service;

import com.mahashri.mahashrimart.dao.CartDao;
import com.mahashri.mahashrimart.dao.ProductDao;
import com.mahashri.mahashrimart.exception.ValidationException;
import com.mahashri.mahashrimart.model.CartItem;
import com.mahashri.mahashrimart.model.Product;

import java.sql.SQLException;
import java.util.List;

public class CartService {
    private final CartDao cartDao;
    private final ProductDao productDao;

    public CartService(CartDao cartDao, ProductDao productDao) {
        this.cartDao = cartDao;
        this.productDao = productDao;
    }

    public List<CartItem> view(long userId) throws SQLException { return cartDao.findByUserId(userId); }

    public void add(long userId, long productId, int quantity) throws SQLException, ValidationException {
        Product product = productDao.findById(productId).orElse(null);
        if (product == null) throw new ValidationException("That product is no longer available.");
        CartItem existing = cartDao.findByUserId(userId).stream()
                .filter(item -> item.getProductId() == productId)
                .findFirst().orElse(null);
        int requested = quantity + (existing == null ? 0 : existing.getQuantity());
        if (requested > product.getStockQty()) throw new ValidationException("There is not enough stock for that quantity.");
        cartDao.addItem(userId, productId, quantity);
    }

    public void update(long userId, long productId, int quantity) throws SQLException, ValidationException {
        Product product = productDao.findById(productId).orElse(null);
        if (product == null) throw new ValidationException("That product is no longer available.");
        if (quantity > product.getStockQty()) throw new ValidationException("There is not enough stock for that quantity.");
        cartDao.updateQuantity(userId, productId, quantity);
    }

    public void remove(long userId, long productId) throws SQLException { cartDao.removeItem(userId, productId); }
}