package com.mahashri.mahashrimart.service;

import com.mahashri.mahashrimart.dao.*;

import javax.sql.DataSource;

public class ApplicationServices {
    private final UserService userService;
    private final ProductService productService;
    private final CartService cartService;
    private final OrderService orderService;

    public ApplicationServices(DataSource dataSource) {
        UserDao userDao = new JdbcUserDao(dataSource);
        ProductDao productDao = new JdbcProductDao(dataSource);
        CartDao cartDao = new JdbcCartDao(dataSource);
        OrderDao orderDao = new JdbcOrderDao(dataSource);
        userService = new UserService(userDao);
        productService = new ProductService(productDao);
        cartService = new CartService(cartDao, productDao);
        orderService = new OrderService(dataSource, cartDao, productDao, orderDao);
    }

    public UserService users() { return userService; }
    public ProductService products() { return productService; }
    public CartService cart() { return cartService; }
    public OrderService orders() { return orderService; }
}