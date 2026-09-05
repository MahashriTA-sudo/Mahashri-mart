package com.mahashri.mahashrimart.controller;

import com.mahashri.mahashrimart.util.ServletUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends ServletUtil {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        logout(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        logout(request, response);
    }

    private void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (request.getSession(false) != null) request.getSession(false).invalidate();
        redirect(request, response, "/");
    }
}