package com.mahashri.mahashrimart.service;

import com.mahashri.mahashrimart.dao.ProductDao;
import com.mahashri.mahashrimart.dto.ProductRequest;
import com.mahashri.mahashrimart.exception.ValidationException;
import com.mahashri.mahashrimart.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {
    private ProductDao productDao;
    private ProductService productService;

    @BeforeEach
    void setUp() {
        productDao = mock(ProductDao.class);
        productService = new ProductService(productDao);
    }

    @Test
    void sellerCanUpdateTheirOwnProduct() throws Exception {
        Product existing = new Product();
        existing.setId(1L);
        existing.setSellerId(100L);
        when(productDao.findById(1L)).thenReturn(Optional.of(existing));

        ProductRequest request = new ProductRequest("New name", "desc", BigDecimal.TEN, 5, "Home", "");
        productService.update(100L, 1L, request);

        verify(productDao).update(1L, request);
    }

    @Test
    void sellerCannotUpdateAnotherSellersProduct() throws Exception {
        Product existing = new Product();
        existing.setId(1L);
        existing.setSellerId(100L);
        when(productDao.findById(1L)).thenReturn(Optional.of(existing));

        ProductRequest request = new ProductRequest("New name", "desc", BigDecimal.TEN, 5, "Home", "");

        assertThrows(ValidationException.class, () ->
                productService.update(999L, 1L, request));
        verify(productDao, never()).update(anyLong(), any());
    }

    @Test
    void sellerCannotDeleteAnotherSellersProduct() throws Exception {
        Product existing = new Product();
        existing.setId(1L);
        existing.setSellerId(100L);
        when(productDao.findById(1L)).thenReturn(Optional.of(existing));

        assertThrows(ValidationException.class, () ->
                productService.delete(999L, 1L));
        verify(productDao, never()).delete(anyLong());
    }

    @Test
    void updateThrowsWhenProductDoesNotExist() throws Exception {
        when(productDao.findById(1L)).thenReturn(Optional.empty());
        ProductRequest request = new ProductRequest("New name", "desc", BigDecimal.TEN, 5, "Home", "");

        assertThrows(ValidationException.class, () ->
                productService.update(100L, 1L, request));
    }
}