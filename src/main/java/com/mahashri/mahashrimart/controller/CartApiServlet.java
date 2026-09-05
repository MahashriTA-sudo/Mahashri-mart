package com.mahashri.mahashrimart.controller;

import com.mahashri.mahashrimart.model.CartItem;
import com.mahashri.mahashrimart.util.JsonUtil;
import com.mahashri.mahashrimart.util.ServletUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/cart")
public class CartApiServlet extends ServletUtil {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            List<CartItem> items = services(request).cart().view(currentUser(request).getId());
            JsonUtil.write(response, items);
        } catch (Exception ex) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}