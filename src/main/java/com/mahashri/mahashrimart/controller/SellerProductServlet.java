package com.mahashri.mahashrimart.controller;

import com.mahashri.mahashrimart.dto.ProductRequest;
import com.mahashri.mahashrimart.exception.ValidationException;
import com.mahashri.mahashrimart.model.User;
import com.mahashri.mahashrimart.util.ServletUtil;
import com.mahashri.mahashrimart.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/seller/products/new")
public class SellerProductServlet extends ServletUtil {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        view(request, response, "seller-product");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            ProductRequest product = ValidationUtil.product(
                    request.getParameter("name"), request.getParameter("description"),
                    request.getParameter("price"), request.getParameter("stockQty"),
                    request.getParameter("category"), request.getParameter("imageUrl"));
            long id = services(request).products().create(currentUser(request).getId(), product);
            redirect(request, response, "/product?id=" + id + "&created=1");
        } catch (ValidationException ex) {
            setError(request, ex.getMessage());
            view(request, response, "seller-product");
        } catch (Exception ex) {
            setError(request, "We could not publish that listing. Please try again.");
            view(request, response, "seller-product");
        }
    }
}