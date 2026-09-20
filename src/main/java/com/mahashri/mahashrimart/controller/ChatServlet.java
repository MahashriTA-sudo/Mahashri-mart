package com.mahashri.mahashrimart.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mahashri.mahashrimart.service.ChatService;
import com.mahashri.mahashrimart.service.chat.ChatProviderFactory;
import com.mahashri.mahashrimart.util.ServletUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/** Thin HTTP endpoint for the chatbot widget. All rules live in {@link ChatService}. */
@WebServlet("/api/v1/chat")
public class ChatServlet extends ServletUtil {
    private static final Gson GSON = new GsonBuilder().serializeNulls().create();

    private volatile ChatService chatService;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        ChatService.ChatResult result;
        try {
            String sessionId = request.getSession(true).getId();
            result = chatService(request).handle(sessionId, request.getParameter("message"));
        } catch (Exception ex) {
            result = ChatService.ChatResult.ok(ChatService.FALLBACK_REPLY);
        }
        writeJson(response, result);
    }

    private ChatService chatService(HttpServletRequest request) {
        ChatService local = chatService;
        if (local == null) {
            synchronized (this) {
                local = chatService;
                if (local == null) {
                    String providerName = System.getenv("AI_CHATBOT_PROVIDER");
                    local = new ChatService(services(request).products(),
                            ChatProviderFactory.create(providerName));
                    chatService = local;
                }
            }
        }
        return local;
    }

    private void writeJson(HttpServletResponse response, ChatService.ChatResult result) throws IOException {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", result.success());
        if (result.success()) {
            body.put("data", Map.of("reply", result.reply()));
            body.put("error", null);
        } else {
            Map<String, Object> error = new LinkedHashMap<>();
            error.put("code", result.errorCode());
            error.put("message", result.errorMessage());
            body.put("data", null);
            body.put("error", error);
        }
        response.setStatus(result.status());
        if (result.status() == 429) {
            response.setHeader("Retry-After", "60");
        }
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(GSON.toJson(body));
    }
}