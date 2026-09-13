package com.mahashri.mahashrimart.controller;

import com.mahashri.mahashrimart.listener.AppContextListener;
import com.mahashri.mahashrimart.util.JsonUtil;
import com.mahashri.mahashrimart.util.ServletUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.Map;

@WebServlet("/api/v1/health")
public class HealthServlet extends ServletUtil {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, String> body = new LinkedHashMap<>();
        String dbStatus = "DOWN";
        try {
            DataSource dataSource = AppContextListener.dataSource(request.getServletContext());
            try (Connection connection = dataSource.getConnection()) {
                if (connection.isValid(2)) {
                    dbStatus = "UP";
                }
            }
        } catch (Exception ex) {
            dbStatus = "DOWN";
        }
        body.put("status", "UP");
        body.put("db", dbStatus);
        if (!"UP".equals(dbStatus)) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        }
        JsonUtil.write(response, body);
    }
}