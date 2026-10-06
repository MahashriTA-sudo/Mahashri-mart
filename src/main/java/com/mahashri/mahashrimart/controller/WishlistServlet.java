package com.mahashri.mahashrimart.controller;

import com.mahashri.mahashrimart.exception.ValidationException;
import com.mahashri.mahashrimart.util.ServletUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet({"/wishlist", "/wishlist/add", "/wishlist/remove"})
public class WishlistServlet extends ServletUtil {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setAttribute("items", services(request).wishlist().view(currentUser(request).getId()));
            if ("1".equals(request.getParameter("removed"))) {
                request.setAttribute("success", "Removed from your wishlist.");
            }
            view(request, response, "wishlist");
        } catch (Exception ex) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        long productId = longParameter(request, "productId");
        long userId = currentUser(request).getId();
        try {
            if ("/wishlist/add".equals(request.getServletPath())) {
                services(request).wishlist().add(userId, productId);
                redirect(request, response, "/product?id=" + productId + "&wished=1");
            } else if ("/wishlist/remove".equals(request.getServletPath())) {
                services(request).wishlist().remove(userId, productId);
                if ("product".equals(request.getParameter("from"))) {
                    redirect(request, response, "/product?id=" + productId + "&unwished=1");
                } else {
                    redirect(request, response, "/wishlist?removed=1");
                }
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (ValidationException ex) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception ex) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
