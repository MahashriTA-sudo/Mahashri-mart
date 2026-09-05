package com.mahashri.mahashrimart.service;

import com.mahashri.mahashrimart.dao.ProductDao;
import com.mahashri.mahashrimart.dto.ProductRequest;
import com.mahashri.mahashrimart.exception.ValidationException;
import com.mahashri.mahashrimart.model.Product;

import java.sql.SQLException;
import java.util.List;

public class ProductService {
    private final ProductDao productDao;

    public ProductService(ProductDao productDao) { this.productDao = productDao; }
    public List<Product> browse() throws SQLException { return productDao.findAll(); }
    public Product find(long id) throws SQLException { return productDao.findById(id).orElse(null); }
    public long create(long sellerId, ProductRequest request) throws SQLException {
        return productDao.create(sellerId, request);
    }

    public List<Product> listForSeller(long sellerId) throws SQLException {
        return productDao.findBySellerId(sellerId);
    }

    public void update(long sellerId, long productId, ProductRequest request) throws SQLException, ValidationException {
        Product existing = productDao.findById(productId).orElse(null);
        if (existing == null) {
            throw new ValidationException("That listing does not exist.");
        }
        if (existing.getSellerId() != sellerId) {
            throw new ValidationException("You can only edit your own listings.");
        }
        productDao.update(productId, request);
    }

    public void delete(long sellerId, long productId) throws SQLException, ValidationException {
        Product existing = productDao.findById(productId).orElse(null);
        if (existing == null) {
            throw new ValidationException("That listing does not exist.");
        }
        if (existing.getSellerId() != sellerId) {
            throw new ValidationException("You can only delete your own listings.");
        }
        productDao.delete(productId);
    }
}