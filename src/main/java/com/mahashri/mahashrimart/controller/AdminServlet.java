package com.mahashri.mahashrimart.controller;

import com.mahashri.mahashrimart.exception.ValidationException;
import com.mahashri.mahashrimart.model.Order;
import com.mahashri.mahashrimart.model.OrderStatus;
import com.mahashri.mahashrimart.model.Product;
import com.mahashri.mahashrimart.model.User;
import com.mahashri.mahashrimart.util.ServletUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet({"/admin", "/admin/users", "/admin/orders", "/admin/products", "/admin/products/remove", "/admin/orders/update"})
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
        } else if (path.equals("/admin/orders/update")) {
            updateOrderStatus(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    // ── GET handlers ──────────────────────────────────────────────────────────

    private void showUsers(HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<User> allUsers = services(request).users().listAll();
        request.setAttribute("allUsers", allUsers);
        request.setAttribute("activeTab", "users");
        view(request, response, "admin-dashboard");
    }

    private void showOrders(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String statusFilter = request.getParameter("status");
        List<Order> allOrders;
        if (statusFilter != null && !statusFilter.isBlank()) {
            try {
                OrderStatus filter = OrderStatus.valueOf(statusFilter.toUpperCase());
                allOrders = services(request).orders().listAllByStatus(filter);
                request.setAttribute("statusFilter", filter.name());
            } catch (IllegalArgumentException ex) {
                allOrders = services(request).orders().listAll();
            }
        } else {
            allOrders = services(request).orders().listAll();
        }
        request.setAttribute("allOrders", allOrders);
        request.setAttribute("activeTab", "orders");
        request.setAttribute("allStatuses", OrderStatus.values());
        if (request.getParameter("updated") != null) {
            request.setAttribute("success", "Order #" + request.getParameter("updated") + " status updated.");
        }
        if (request.getParameter("error") != null) {
            request.setAttribute("error", request.getParameter("error"));
        }
        view(request, response, "admin-dashboard");
    }

    private void showProducts(HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<Product> allProducts = services(request).products().browse();
        request.setAttribute("allProducts", allProducts);
        request.setAttribute("activeTab", "products");
        view(request, response, "admin-dashboard");
    }

    // ── POST handlers ─────────────────────────────────────────────────────────

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

    private void updateOrderStatus(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        long orderId = longParameter(request, "id");
        String statusParam = request.getParameter("status");
        String refFilter = request.getParameter("statusFilter");
        String redirectBase = "/admin/orders" + (refFilter != null && !refFilter.isBlank() ? "?status=" + refFilter + "&" : "?");
        try {
            OrderStatus newStatus = OrderStatus.valueOf(statusParam);
            services(request).orders().adminUpdateStatus(orderId, newStatus);
            redirect(request, response, redirectBase + "updated=" + orderId +
                    (refFilter != null && !refFilter.isBlank() ? "" : ""));
        } catch (ValidationException ex) {
            redirect(request, response, redirectBase + "error=" + java.net.URLEncoder.encode(ex.getMessage(), "UTF-8"));
        } catch (IllegalArgumentException ex) {
            redirect(request, response, redirectBase + "error=Invalid+status+value.");
        } catch (Exception ex) {
            redirect(request, response, redirectBase + "error=An+unexpected+error+occurred.");
        }
    }
}