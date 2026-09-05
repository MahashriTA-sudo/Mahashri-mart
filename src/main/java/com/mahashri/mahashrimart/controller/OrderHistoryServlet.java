package com.mahashri.mahashrimart.controller;

import com.mahashri.mahashrimart.util.ServletUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/orders")
public class OrderHistoryServlet extends ServletUtil {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setAttribute("orders", services(request).orders().history(currentUser(request).getId()));
            if (request.getParameter("placed") != null) {
                request.setAttribute("success", "Order #" + request.getParameter("placed") + " confirmed. Thank you.");
            }
            view(request, response, "orders");
        } catch (Exception ex) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}