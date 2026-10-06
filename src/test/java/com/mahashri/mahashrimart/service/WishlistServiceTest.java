package com.mahashri.mahashrimart.service;

import com.mahashri.mahashrimart.dao.ProductDao;
import com.mahashri.mahashrimart.dao.WishlistDao;
import com.mahashri.mahashrimart.exception.ValidationException;
import com.mahashri.mahashrimart.model.Product;
import com.mahashri.mahashrimart.model.WishlistItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WishlistServiceTest {
    private static final long USER_ID = 1L;
    private static final long PRODUCT_ID = 10L;

    private WishlistDao wishlistDao;
    private ProductDao productDao;
    private WishlistService service;

    @BeforeEach
    void setUp() {
        wishlistDao = mock(WishlistDao.class);
        productDao = mock(ProductDao.class);
        service = new WishlistService(wishlistDao, productDao);
    }

    @Test
    void viewReturnsTheUsersWishlist() throws Exception {
        List<WishlistItem> items = List.of(new WishlistItem());
        when(wishlistDao.findByUserId(USER_ID)).thenReturn(items);

        assertSame(items, service.view(USER_ID));
    }

    @Test
    void addSavesProductWhenItExistsAndIsNotSavedYet() throws Exception {
        when(productDao.findById(PRODUCT_ID)).thenReturn(Optional.of(new Product()));
        when(wishlistDao.contains(USER_ID, PRODUCT_ID)).thenReturn(false);

        service.add(USER_ID, PRODUCT_ID);

        verify(wishlistDao).add(USER_ID, PRODUCT_ID);
    }

    @Test
    void addTwiceDoesNotInsertADuplicate() throws Exception {
        when(productDao.findById(PRODUCT_ID)).thenReturn(Optional.of(new Product()));
        when(wishlistDao.contains(USER_ID, PRODUCT_ID)).thenReturn(true);

        service.add(USER_ID, PRODUCT_ID);

        verify(wishlistDao, never()).add(anyLong(), anyLong());
    }

    @Test
    void addRejectsUnknownProduct() throws Exception {
        when(productDao.findById(PRODUCT_ID)).thenReturn(Optional.empty());

        assertThrows(ValidationException.class, () -> service.add(USER_ID, PRODUCT_ID));
        verify(wishlistDao, never()).add(anyLong(), anyLong());
    }

    @Test
    void removeDeletesTheEntry() throws Exception {
        service.remove(USER_ID, PRODUCT_ID);

        verify(wishlistDao).remove(USER_ID, PRODUCT_ID);
    }

    @Test
    void containsAsksTheDao() throws Exception {
        when(wishlistDao.contains(USER_ID, PRODUCT_ID)).thenReturn(true);

        assertTrue(service.contains(USER_ID, PRODUCT_ID));
    }
}
