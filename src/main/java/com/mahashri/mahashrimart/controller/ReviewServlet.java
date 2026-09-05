package com.mahashri.mahashrimart.controller;

import com.mahashri.mahashrimart.exception.ValidationException;
import com.mahashri.mahashrimart.util.ServletUtil;
import com.mahashri.mahashrimart.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/reviews/new")
public class ReviewServlet extends ServletUtil {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        long productId = longParameter(request, "productId");
        try {
            int rating = ValidationUtil.positiveQuantity(request.getParameter("rating"));
            if (rating > 5) {
                throw new ValidationException("Rating must be between 1 and 5.");
            }
            String comment = request.getParameter("comment");
            services(request).reviews().submit(productId, currentUser(request).getId(), rating, comment);
            redirect(request, response, "/product?id=" + productId + "&reviewed=1");
        } catch (ValidationException ex) {
            redirect(request, response, "/product?id=" + productId + "&reviewError=1");
        } catch (Exception ex) {
            redirect(request, response, "/product?id=" + productId + "&reviewError=1");
        }
    }
}