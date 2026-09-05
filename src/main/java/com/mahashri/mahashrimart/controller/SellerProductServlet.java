package com.mahashri.mahashrimart.controller;

import com.mahashri.mahashrimart.dto.ProductRequest;
import com.mahashri.mahashrimart.exception.ValidationException;
import com.mahashri.mahashrimart.model.Product;
import com.mahashri.mahashrimart.model.User;
import com.mahashri.mahashrimart.util.ServletUtil;
import com.mahashri.mahashrimart.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet({"/seller/products", "/seller/products/new", "/seller/products/edit", "/seller/products/delete"})
public class SellerProductServlet extends ServletUtil {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        try {
            if (path.equals("/seller/products")) {
                showMyListings(request, response);
            } else if (path.equals("/seller/products/new")) {
                view(request, response, "seller-product");
            } else if (path.equals("/seller/products/edit")) {
                showEditForm(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception ex) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        if (path.equals("/seller/products/new")) {
            createProduct(request, response);
        } else if (path.equals("/seller/products/edit")) {
            updateProduct(request, response);
        } else if (path.equals("/seller/products/delete")) {
            deleteProduct(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void showMyListings(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        User user = currentUser(request);
        List<Product> myProducts = services(request).products().listForSeller(user.getId());
        request.setAttribute("myProducts", myProducts);
        view(request, response, "seller-listings");
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        long id = longParameter(request, "id");
        Product product = services(request).products().find(id);
        if (product == null || product.getSellerId() != currentUser(request).getId()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        request.setAttribute("product", product);
        request.setAttribute("editMode", true);
        view(request, response, "seller-product");
    }

    private void createProduct(HttpServletRequest request, HttpServletResponse response)
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

    private void updateProduct(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        long id = longParameter(request, "id");
        try {
            ProductRequest product = ValidationUtil.product(
                    request.getParameter("name"), request.getParameter("description"),
                    request.getParameter("price"), request.getParameter("stockQty"),
                    request.getParameter("category"), request.getParameter("imageUrl"));
            services(request).products().update(currentUser(request).getId(), id, product);
            redirect(request, response, "/seller/products");
        } catch (ValidationException ex) {
            setError(request, ex.getMessage());
            request.setAttribute("editMode", true);
            view(request, response, "seller-product");
        } catch (Exception ex) {
            setError(request, "We could not update that listing. Please try again.");
            request.setAttribute("editMode", true);
            view(request, response, "seller-product");
        }
    }

    private void deleteProduct(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        long id = longParameter(request, "id");
        try {
            services(request).products().delete(currentUser(request).getId(), id);
        } catch (Exception ex) {
            // fall through - still redirect to listings either way
        }
        redirect(request, response, "/seller/products");
    }
}