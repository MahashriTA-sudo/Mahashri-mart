package com.mahashri.mahashrimart.service;

import com.mahashri.mahashrimart.dao.*;

import javax.sql.DataSource;

public class ApplicationServices {
    private final UserService userService;
    private final ProductService productService;
    private final CartService cartService;
    private final OrderService orderService;
    private final ReviewService reviewService;

    public ApplicationServices(DataSource dataSource) {
        UserDao userDao = new JdbcUserDao(dataSource);
        ProductDao productDao = new JdbcProductDao(dataSource);
        CartDao cartDao = new JdbcCartDao(dataSource);
        OrderDao orderDao = new JdbcOrderDao(dataSource);
        ReviewDao reviewDao = new JdbcReviewDao(dataSource);
        userService = new UserService(userDao);
        productService = new ProductService(productDao);
        cartService = new CartService(cartDao, productDao);
        orderService = new OrderService(dataSource, cartDao, productDao, orderDao);
        reviewService = new ReviewService(reviewDao);
    }

    public UserService users() { return userService; }
    public ProductService products() { return productService; }
    public CartService cart() { return cartService; }
    public OrderService orders() { return orderService; }
    public ReviewService reviews() { return reviewService; }
}