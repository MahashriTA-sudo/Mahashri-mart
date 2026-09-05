package com.mahashri.mahashrimart.controller;

import com.mahashri.mahashrimart.exception.InsufficientStockException;
import com.mahashri.mahashrimart.exception.ValidationException;
import com.mahashri.mahashrimart.model.CartItem;
import com.mahashri.mahashrimart.util.ServletUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/checkout")
public class CheckoutServlet extends ServletUtil {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<CartItem> items = services(request).cart().view(currentUser(request).getId());
            if (items.isEmpty()) {
                redirect(request, response, "/cart");
                return;
            }
            request.setAttribute("items", items);
            view(request, response, "checkout");
        } catch (Exception ex) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        try {
            long orderId = services(request).orders().checkout(currentUser(request).getId());
            redirect(request, response, "/orders?placed=" + orderId);
        } catch (ValidationException | InsufficientStockException ex) {
            setError(request, ex.getMessage());
            doGet(request, response);
        } catch (Exception ex) {
            setError(request, "We could not complete checkout. Please review your cart and try again.");
            doGet(request, response);
        }
    }
}