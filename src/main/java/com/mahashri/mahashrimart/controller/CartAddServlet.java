package com.mahashri.mahashrimart.controller;

import com.mahashri.mahashrimart.exception.ValidationException;
import com.mahashri.mahashrimart.util.ServletUtil;
import com.mahashri.mahashrimart.util.ValidationUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/cart/add")
public class CartAddServlet extends ServletUtil {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            long productId = longParameter(request, "productId");
            int quantity = ValidationUtil.positiveQuantity(request.getParameter("quantity"));
            services(request).cart().add(currentUser(request).getId(), productId, quantity);

            String next = request.getParameter("next");
            if ("checkout".equals(next)) {
                redirect(request, response, "/checkout");
            } else if ("home".equals(next)) {
                redirect(request, response, "/?added=1#marketplace");
            } else {
                redirect(request, response, "/cart");
            }
        } catch (ValidationException ex) {
            redirect(request, response, "/product?id=" + longParameter(request, "productId") + "&error=stock");
        } catch (Exception ex) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}