package com.mahashri.mahashrimart.service;

import com.mahashri.mahashrimart.dao.CartDao;
import com.mahashri.mahashrimart.dao.ProductDao;
import com.mahashri.mahashrimart.exception.ValidationException;
import com.mahashri.mahashrimart.model.CartItem;
import com.mahashri.mahashrimart.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartServiceTest {
    private static final long USER_ID = 1L;
    private static final long PRODUCT_ID = 10L;

    private CartDao cartDao;
    private ProductDao productDao;
    private CartService cartService;

    @BeforeEach
    void setUp() {
        cartDao = mock(CartDao.class);
        productDao = mock(ProductDao.class);
        cartService = new CartService(cartDao, productDao);
    }

    private static Product productWithStock(int stock) {
        Product product = new Product();
        product.setId(PRODUCT_ID);
        product.setStockQty(stock);
        return product;
    }

    private static CartItem cartItem(int quantity) {
        CartItem item = new CartItem();
        item.setProductId(PRODUCT_ID);
        item.setQuantity(quantity);
        return item;
    }

    @Test
    void viewReturnsTheUsersCart() throws Exception {
        List<CartItem> items = List.of(cartItem(2));
        when(cartDao.findByUserId(USER_ID)).thenReturn(items);

        assertSame(items, cartService.view(USER_ID));
    }

    @Test
    void addPutsNewItemInTheCartWhenStockIsEnough() throws Exception {
        when(productDao.findById(PRODUCT_ID)).thenReturn(Optional.of(productWithStock(5)));
        when(cartDao.findByUserId(USER_ID)).thenReturn(List.of());

        cartService.add(USER_ID, PRODUCT_ID, 2);

        verify(cartDao).addItem(USER_ID, PRODUCT_ID, 2);
    }

    @Test
    void addAllowsExactlyTheAvailableStock() throws Exception {
        when(productDao.findById(PRODUCT_ID)).thenReturn(Optional.of(productWithStock(5)));
        when(cartDao.findByUserId(USER_ID)).thenReturn(List.of());

        cartService.add(USER_ID, PRODUCT_ID, 5);

        verify(cartDao).addItem(USER_ID, PRODUCT_ID, 5);
    }

    @Test
    void addRejectsQuantityAboveStock() throws Exception {
        when(productDao.findById(PRODUCT_ID)).thenReturn(Optional.of(productWithStock(5)));
        when(cartDao.findByUserId(USER_ID)).thenReturn(List.of());

        assertThrows(ValidationException.class, () -> cartService.add(USER_ID, PRODUCT_ID, 6));
        verify(cartDao, never()).addItem(anyLong(), anyLong(), anyInt());
    }

    @Test
    void addCountsWhatIsAlreadyInTheCart() throws Exception {
        when(productDao.findById(PRODUCT_ID)).thenReturn(Optional.of(productWithStock(5)));
        when(cartDao.findByUserId(USER_ID)).thenReturn(List.of(cartItem(3)));

        // 3 already in cart + 3 more = 6, which is more than the 5 in stock
        assertThrows(ValidationException.class, () -> cartService.add(USER_ID, PRODUCT_ID, 3));
        verify(cartDao, never()).addItem(anyLong(), anyLong(), anyInt());
    }

    @Test
    void addRejectsAProductThatNoLongerExists() throws Exception {
        when(productDao.findById(PRODUCT_ID)).thenReturn(Optional.empty());

        assertThrows(ValidationException.class, () -> cartService.add(USER_ID, PRODUCT_ID, 1));
        verify(cartDao, never()).addItem(anyLong(), anyLong(), anyInt());
    }

    @Test
    void updateChangesQuantityWhenStockIsEnough() throws Exception {
        when(productDao.findById(PRODUCT_ID)).thenReturn(Optional.of(productWithStock(5)));

        cartService.update(USER_ID, PRODUCT_ID, 4);

        verify(cartDao).updateQuantity(USER_ID, PRODUCT_ID, 4);
    }

    @Test
    void updateRejectsQuantityAboveStock() throws Exception {
        when(productDao.findById(PRODUCT_ID)).thenReturn(Optional.of(productWithStock(5)));

        assertThrows(ValidationException.class, () -> cartService.update(USER_ID, PRODUCT_ID, 6));
        verify(cartDao, never()).updateQuantity(anyLong(), anyLong(), anyInt());
    }

    @Test
    void updateRejectsAProductThatNoLongerExists() throws Exception {
        when(productDao.findById(PRODUCT_ID)).thenReturn(Optional.empty());

        assertThrows(ValidationException.class, () -> cartService.update(USER_ID, PRODUCT_ID, 1));
        verify(cartDao, never()).updateQuantity(anyLong(), anyLong(), anyInt());
    }

    @Test
    void removeDeletesTheItemFromTheCart() throws Exception {
        cartService.remove(USER_ID, PRODUCT_ID);

        verify(cartDao).removeItem(USER_ID, PRODUCT_ID);
    }
}
