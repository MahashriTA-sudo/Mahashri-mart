package com.mahashri.mahashrimart.controller;

import com.mahashri.mahashrimart.exception.ValidationException;
import com.mahashri.mahashrimart.util.ServletUtil;
import com.mahashri.mahashrimart.util.ValidationUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/cart/update")
public class CartUpdateServlet extends ServletUtil {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            long productId = longParameter(request, "productId");
            int quantity = ValidationUtil.positiveQuantity(request.getParameter("quantity"));
            services(request).cart().update(currentUser(request).getId(), productId, quantity);
            redirect(request, response, "/cart");
        } catch (ValidationException ex) {
            redirect(request, response, "/cart?error=stock");
        } catch (Exception ex) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}