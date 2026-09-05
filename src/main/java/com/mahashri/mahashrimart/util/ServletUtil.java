package com.mahashri.mahashrimart.util;

import com.mahashri.mahashrimart.listener.AppContextListener;
import com.mahashri.mahashrimart.model.User;
import com.mahashri.mahashrimart.service.ApplicationServices;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public abstract class ServletUtil extends HttpServlet {
    protected ApplicationServices services(HttpServletRequest request) {
        return AppContextListener.services(request.getServletContext());
    }

    protected User currentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session == null ? null : (User) session.getAttribute("user");
    }

    protected void view(HttpServletRequest request, HttpServletResponse response, String view)
            throws IOException, javax.servlet.ServletException {
        request.getRequestDispatcher("/WEB-INF/views/" + view + ".jsp").forward(request, response);
    }

    protected void redirect(HttpServletRequest request, HttpServletResponse response, String path)
            throws IOException {
        response.sendRedirect(request.getContextPath() + path);
    }

    protected long longParameter(HttpServletRequest request, String name) {
        try {
            return Long.parseLong(request.getParameter(name));
        } catch (RuntimeException ex) {
            return -1;
        }
    }

    protected void setError(HttpServletRequest request, String message) {
        request.setAttribute("error", message);
    }
}