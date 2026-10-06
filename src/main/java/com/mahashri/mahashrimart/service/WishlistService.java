package com.mahashri.mahashrimart.service;

import com.mahashri.mahashrimart.dao.ProductDao;
import com.mahashri.mahashrimart.dao.WishlistDao;
import com.mahashri.mahashrimart.exception.ValidationException;
import com.mahashri.mahashrimart.model.WishlistItem;

import java.sql.SQLException;
import java.util.List;

public class WishlistService {
    private final WishlistDao wishlistDao;
    private final ProductDao productDao;

    public WishlistService(WishlistDao wishlistDao, ProductDao productDao) {
        this.wishlistDao = wishlistDao;
        this.productDao = productDao;
    }

    public List<WishlistItem> view(long userId) throws SQLException { return wishlistDao.findByUserId(userId); }

    public boolean contains(long userId, long productId) throws SQLException {
        return wishlistDao.contains(userId, productId);
    }

    /** Adds a product to the wishlist. Adding the same product twice is harmless. */
    public void add(long userId, long productId) throws SQLException, ValidationException {
        if (productDao.findById(productId).isEmpty()) {
            throw new ValidationException("That product is no longer available.");
        }
        if (!wishlistDao.contains(userId, productId)) {
            wishlistDao.add(userId, productId);
        }
    }

    public void remove(long userId, long productId) throws SQLException {
        wishlistDao.remove(userId, productId);
    }
}
