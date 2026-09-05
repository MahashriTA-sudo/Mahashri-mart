package com.mahashri.mahashrimart.controller;

import com.mahashri.mahashrimart.util.ServletUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet({"/", "/home"})
public class HomeServlet extends ServletUtil {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String keyword = request.getParameter("q");
            String category = request.getParameter("category");

            if ((keyword != null && !keyword.isBlank()) || (category != null && !category.isBlank())) {
                request.setAttribute("products", services(request).products().search(keyword, category));
            } else {
                request.setAttribute("products", services(request).products().browse());
            }
            request.setAttribute("categories", services(request).products().listCategories());
            request.setAttribute("q", keyword);
            request.setAttribute("selectedCategory", category);

            view(request, response, "home");
        } catch (Exception ex) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}