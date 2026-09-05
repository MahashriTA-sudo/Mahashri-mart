package com.mahashri.mahashrimart.controller;

import com.mahashri.mahashrimart.dto.RegistrationRequest;
import com.mahashri.mahashrimart.exception.ValidationException;
import com.mahashri.mahashrimart.util.ServletUtil;
import com.mahashri.mahashrimart.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends ServletUtil {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        view(request, response, "register");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            RegistrationRequest registration = ValidationUtil.registration(
                    request.getParameter("name"), request.getParameter("email"),
                    request.getParameter("password"), request.getParameter("role"));
            services(request).users().register(registration);
            redirect(request, response, "/login?registered=1");
        } catch (ValidationException ex) {
            setError(request, ex.getMessage());
            view(request, response, "register");
        } catch (Exception ex) {
            setError(request, "We could not create your account. Please try again.");
            view(request, response, "register");
        }
    }
}