package com.mahashri.mahashrimart.service;

import com.mahashri.mahashrimart.dao.ProductDao;
import com.mahashri.mahashrimart.dto.ProductRequest;
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
}