package com.mahashri.mahashrimart.controller;

import com.mahashri.mahashrimart.util.ServletUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/cart/remove")
public class CartRemoveServlet extends ServletUtil {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            services(request).cart().remove(currentUser(request).getId(), longParameter(request, "productId"));
            redirect(request, response, "/cart");
        } catch (Exception ex) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}