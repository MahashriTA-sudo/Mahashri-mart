package com.mahashri.mahashrimart.controller;

import com.mahashri.mahashrimart.exception.ValidationException;
import com.mahashri.mahashrimart.model.OrderStatus;
import com.mahashri.mahashrimart.util.ServletUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet({"/seller/orders", "/seller/orders/update"})
public class SellerOrdersServlet extends ServletUtil {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setAttribute("orders", services(request).orders().listForSeller(currentUser(request).getId()));
            if (request.getParameter("updated") != null) {
                request.setAttribute("success", "Order #" + request.getParameter("updated") + " status updated.");
            }
            if (request.getParameter("error") != null) {
                request.setAttribute("error", request.getParameter("error"));
            }
            view(request, response, "seller-orders");
        } catch (Exception ex) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        if (path.equals("/seller/orders/update")) {
            updateStatus(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void updateStatus(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        long orderId = longParameter(request, "id");
        String statusParam = request.getParameter("status");
        long sellerId = currentUser(request).getId();
        try {
            OrderStatus newStatus = OrderStatus.valueOf(statusParam);
            services(request).orders().advanceOrderStatus(orderId, sellerId, newStatus);
            redirect(request, response, "/seller/orders?updated=" + orderId);
        } catch (ValidationException ex) {
            redirect(request, response, "/seller/orders?error=" + java.net.URLEncoder.encode(ex.getMessage(), "UTF-8"));
        } catch (IllegalArgumentException ex) {
            redirect(request, response, "/seller/orders?error=Invalid+status+value.");
        } catch (Exception ex) {
            redirect(request, response, "/seller/orders?error=An+unexpected+error+occurred.");
        }
    }
}