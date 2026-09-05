package com.mahashri.mahashrimart.controller;

import com.mahashri.mahashrimart.dto.LoginRequest;
import com.mahashri.mahashrimart.exception.AuthenticationException;
import com.mahashri.mahashrimart.model.Role;
import com.mahashri.mahashrimart.model.User;
import com.mahashri.mahashrimart.util.ServletUtil;
import com.mahashri.mahashrimart.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends ServletUtil {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (currentUser(request) != null) {
            redirect(request, response, "/");
            return;
        }
        if ("1".equals(request.getParameter("registered"))) {
            request.setAttribute("success", "Your account is ready. Sign in to continue.");
        }
        view(request, response, "login");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String email = ValidationUtil.required(request.getParameter("email"), "Email");
            String password = ValidationUtil.required(request.getParameter("password"), "Password");
            User user = services(request).users().authenticate(new LoginRequest(email.toLowerCase(), password));
            request.changeSessionId();
            HttpSession session = request.getSession(true);
            session.setMaxInactiveInterval(30 * 60);
            session.setAttribute("user", user);
            redirect(request, response, user.getRole() == Role.SELLER ? "/seller/products/new" : "/");
        } catch (AuthenticationException | com.mahashri.mahashrimart.exception.ValidationException ex) {
            setError(request, ex.getMessage());
            view(request, response, "login");
        } catch (Exception ex) {
            setError(request, "We could not sign you in. Please try again.");
            view(request, response, "login");
        }
    }
}