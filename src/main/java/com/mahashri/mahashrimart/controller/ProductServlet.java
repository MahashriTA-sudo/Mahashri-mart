package com.mahashri.mahashrimart.controller;

import com.mahashri.mahashrimart.model.Product;
import com.mahashri.mahashrimart.util.ServletUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/product")
public class ProductServlet extends ServletUtil {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        long id = longParameter(request, "id");
        try {
            Product product = services(request).products().find(id);
            if (product == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            request.setAttribute("product", product);
            if ("1".equals(request.getParameter("created"))) {
                request.setAttribute("success", "Your listing is live.");
            }
            if ("stock".equals(request.getParameter("error"))) {
                request.setAttribute("error", "There is not enough stock for that quantity.");
            }
            view(request, response, "product");
        } catch (Exception ex) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}