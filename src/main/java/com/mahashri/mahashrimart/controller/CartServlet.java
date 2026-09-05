package com.mahashri.mahashrimart.controller;

import com.mahashri.mahashrimart.model.CartItem;
import com.mahashri.mahashrimart.model.User;
import com.mahashri.mahashrimart.util.ServletUtil;
import com.mahashri.mahashrimart.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet("/cart")
public class CartServlet extends ServletUtil {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<CartItem> items = services(request).cart().view(currentUser(request).getId());
            request.setAttribute("items", items);
            request.setAttribute("total", total(items));
            if ("stock".equals(request.getParameter("error"))) {
                request.setAttribute("error", "Please check the available stock and try again.");
            }
            view(request, response, "cart");
        } catch (Exception ex) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private BigDecimal total(List<CartItem> items) {
        return items.stream().map(CartItem::getLineTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}