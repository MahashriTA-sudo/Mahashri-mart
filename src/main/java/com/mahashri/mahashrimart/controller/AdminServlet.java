package com.mahashri.mahashrimart.controller;

import com.mahashri.mahashrimart.model.Order;
import com.mahashri.mahashrimart.model.Product;
import com.mahashri.mahashrimart.model.User;
import com.mahashri.mahashrimart.util.ServletUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet({"/admin", "/admin/users", "/admin/orders", "/admin/products", "/admin/products/remove"})
public class AdminServlet extends ServletUtil {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        try {
            if (path.equals("/admin") || path.equals("/admin/users")) {
                showUsers(request, response);
            } else if (path.equals("/admin/orders")) {
                showOrders(request, response);
            } else if (path.equals("/admin/products")) {
                showProducts(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception ex) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        if (path.equals("/admin/products/remove")) {
            removeProduct(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void showUsers(HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<User> allUsers = services(request).users().listAll();
        request.setAttribute("allUsers", allUsers);
        request.setAttribute("activeTab", "users");
        view(request, response, "admin-dashboard");
    }

    private void showOrders(HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<Order> allOrders = services(request).orders().listAll();
        request.setAttribute("allOrders", allOrders);
        request.setAttribute("activeTab", "orders");
        view(request, response, "admin-dashboard");
    }

    private void showProducts(HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<Product> allProducts = services(request).products().browse();
        request.setAttribute("allProducts", allProducts);
        request.setAttribute("activeTab", "products");
        view(request, response, "admin-dashboard");
    }

    private void removeProduct(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        long id = longParameter(request, "id");
        try {
            Product product = services(request).products().find(id);
            if (product != null) {
                services(request).products().adminDelete(id);
            }
        } catch (Exception ex) {
            // fall through - still redirect either way
        }
        redirect(request, response, "/admin/products");
    }
}