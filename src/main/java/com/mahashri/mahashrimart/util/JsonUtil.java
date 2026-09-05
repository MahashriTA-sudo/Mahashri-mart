package com.mahashri.mahashrimart.util;

import com.google.gson.Gson;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public final class JsonUtil {
    private static final Gson GSON = new Gson();

    private JsonUtil() {}

    public static void write(HttpServletResponse response, Object body) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(GSON.toJson(body));
    }
}