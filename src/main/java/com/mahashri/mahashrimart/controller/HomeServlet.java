package com.mahashri.mahashrimart.controller;

import com.mahashri.mahashrimart.model.Product;
import com.mahashri.mahashrimart.model.Role;
import com.mahashri.mahashrimart.model.User;
import com.mahashri.mahashrimart.util.ServletUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@WebServlet({"/", "/home"})
public class HomeServlet extends ServletUtil {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String keyword = request.getParameter("q");
            String category = request.getParameter("category");

            var products = ((keyword != null && !keyword.isBlank()) || (category != null && !category.isBlank()))
                    ? services(request).products().search(keyword, category)
                    : services(request).products().browse();
            request.setAttribute("products", products);

            // Ids of products this buyer saved in the wishlist (empty for everyone else)
            Set<Long> wishedIds = new HashSet<>();
            User user = currentUser(request);
            if (user != null && user.getRole() == Role.BUYER) {
                for (Product product : products) {
                    if (services(request).wishlist().contains(user.getId(), product.getId())) {
                        wishedIds.add(product.getId());
                    }
                }
            }
            request.setAttribute("wishedIds", wishedIds);

            request.setAttribute("categories", services(request).products().listCategories());
            request.setAttribute("q", keyword);
            request.setAttribute("selectedCategory", category);

            view(request, response, "home");
        } catch (Exception ex) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}