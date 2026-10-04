package com.mahashri.mahashrimart.controller;

import com.mahashri.mahashrimart.exception.ValidationException;
import com.mahashri.mahashrimart.util.ServletUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet({"/orders", "/orders/cancel"})
public class OrderHistoryServlet extends ServletUtil {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setAttribute("orders", services(request).orders().history(currentUser(request).getId()));
            if (request.getParameter("placed") != null) {
                request.setAttribute("success", "Order #" + request.getParameter("placed") + " confirmed. Thank you!");
            }
            if (request.getParameter("cancelled") != null) {
                request.setAttribute("success", "Order #" + request.getParameter("cancelled") + " has been cancelled and stock restored.");
            }
            if (request.getParameter("error") != null) {
                request.setAttribute("error", request.getParameter("error"));
            }
            view(request, response, "orders");
        } catch (Exception ex) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        if (path.equals("/orders/cancel")) {
            cancelOrder(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void cancelOrder(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        long orderId = longParameter(request, "id");
        long buyerId = currentUser(request).getId();
        try {
            services(request).orders().cancelOrder(orderId, buyerId);
            redirect(request, response, "/orders?cancelled=" + orderId);
        } catch (ValidationException ex) {
            redirect(request, response, "/orders?error=" + java.net.URLEncoder.encode(ex.getMessage(), "UTF-8"));
        } catch (Exception ex) {
            redirect(request, response, "/orders?error=An+unexpected+error+occurred.");
        }
    }
}